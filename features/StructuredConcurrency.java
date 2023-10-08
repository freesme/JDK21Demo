import java.util.Random;
import java.util.concurrent.StructuredTaskScope;
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
}


record Resp(String user, Integer orderCount) {
    @Override
    public String toString() {
        return STR. "Customer: \{ user }, Order: \{ orderCount }" ;
    }
}
