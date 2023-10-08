import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * {@link java.util.concurrent.StructuredTaskScope}
 * 此类允许开发人员将任务构建为一系列并发子任务，并将它们作为一个单元进行协调。
 * 子任务在它们自己的线程中执行，方法是单独 `forking` 它们，然后将它们 `joining` 为一个单元，并且可能将它们作为一个单元 `cancelling`。
 * 子任务的成功结果或异常由父任务聚合和处理。
 * StructuredTaskScope将子任务的生命周期限制在一个明确的词法范围内，
 * 任务与其子任务的所有交互（forking, joining, cancelling, handling errors, and composing results）都在该范围内发生
 */
public class StructuredConcurrency {

    public static void main(String[] args) throws InterruptedException {
        StructuredConcurrency demo = new StructuredConcurrency();
        Resp handler = demo.handler();
        System.out.println(handler);

    }

    Resp handler() throws InterruptedException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            /*
            每次调用都会fork(...)启动一个新线程来执行子任务，默认情况下是虚拟线程。子任务可以创建自己的嵌套StructuredTaskScope来派生自己的子任务，
            从而创建层次结构。该层次结构反映在代码的块结构中，它限制了子任务的生命周期：一旦作用域关闭，所有子任务的线程都保证终止，并且当块退出时，不会留下任何线程。
             */
            Supplier<Integer> order = scope.fork(this::fetchOrder);
            Supplier<String> user = scope.fork(this::findUser);
            // 连接两个子任务 & 传播错误
            // 短路错误处理 — 如果一个findUser()或fetchOrder()一个子任务失败，则另一个尚未完成的任务将被取消。（这是由 实施的关闭策略管理的ShutdownOnFailure；其他策略也是可能的）
            scope.join().exception();
            return new Resp(user.get(), order.get());
        }
    }

    String findUser() {
        return "WangMing";
    }

    Integer fetchOrder() {
        int i = new Random().nextInt(1, 100);
        if (i > 50) {
            throw new RuntimeException("Something wrong with you!");
        }
        return i;
    }



    /**
     * StructuredTaskScope失败时关闭策略
     * 同时运行一组任务，如果其中任何一个失败，则失败
     */
    <T> List<T> runAll(List<Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            List<? extends Supplier<T>> suppliers = tasks.stream().map(scope::fork).toList();
            scope.join().throwIfFailed();  // Propagate exception if any subtask fails
            // 在这里，所有任务都成功了，处理它们的结果
            return suppliers.stream().map(Supplier::get).toList();
        }
    }

    /**
     * StructuredTaskScope成功关闭策略，返回第一个成功子任务的结果
     * 一旦一个子任务成功，该范围就会自动关闭，取消未完成的子任务。
     * 如果所有子任务都失败或者给定的截止日期已过，则任务将失败。例如，此模式在需要来自任何一个冗余服务集合的结果的服务器应用程序中非常有用。
     */
    <T> T race(List<Callable<T>> tasks, Instant deadline)
            throws InterruptedException, ExecutionException, TimeoutException {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<T>()) {
            for (var task : tasks) {
                scope.fork(task);
            }
            return scope.joinUntil(deadline)
                    .result();  // Throws if none of the subtasks completed successfully
        }
    }


    /**
     * 处理结果
     */
    <T> List<Future<T>> executeAll(List<Callable<T>> tasks)
            throws InterruptedException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            List<? extends Supplier<Future<T>>> futures = tasks.stream()
                    .map(StructuredConcurrency::asFuture)
                    .map(scope::fork)
                    .toList();
            scope.join();
            return futures.stream().map(Supplier::get).toList();
        }
    }

    /**
     * 并行运行任务列表并返回已完成的列表，Future其中包含每个任务各自的成功或异常结果
     */
    static <T> Callable<Future<T>> asFuture(Callable<T> task) {
        return () -> {
            try {
                return CompletableFuture.completedFuture(task.call());
            } catch (Exception ex) {
                return CompletableFuture.failedFuture(ex);
            }
        };
    }
}

record Resp(String user, Integer orderCount) {
    @Override
    public String toString() {
        return STR. "Customer: \{ user }, Order: \{ orderCount }" ;
    }
}



