import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Stream;

/**
 * 自定义StructuredTaskScope子类的一个示例，
 * 它收集 【成功完成】的子任务的结果。它定义了方法results()，主任务将使用它来检索结果
 * @param <T>
 */
class MyScope<T> extends StructuredTaskScope<T> {

    private final Queue<T> results = new ConcurrentLinkedQueue<>();

    MyScope() { super(null, Thread.ofVirtual().factory()); }

    /**
     * 子任务完成（SUCCESS，FAILED）之后的处理逻辑
     */
    @Override
    protected void handleComplete(Subtask<? extends T> subtask) {
        // 任务执行成功，保存进队列
        if (subtask.state() == Subtask.State.SUCCESS)
            results.add(subtask.get());
    }

    @Override
    public MyScope<T> join() throws InterruptedException {
        super.join();
        return this;
    }

    // Returns a stream of results from the subtasks that completed successfully
    public Stream<T> results() {
        // 确保当前线程是此任务范围的所有者，并且它在分叉子任务后加入(使用join()或joinUntil(Instant))。
        super.ensureOwnerAndJoined();
        return results.stream();
    }

}