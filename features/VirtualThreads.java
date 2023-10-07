import com.sun.jdi.ThreadReference;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * <a href="https://openjdk.org/jeps/444"> 虚拟线程 </a><br>
 * <br>
 * 向Java平台引入虚拟线程。虚拟线程是轻量级线程，可以显著减少编写、维护和观察高吞吐量并发应用程序的工作量
 * <br>
 * 虚拟线程现在总是支持线程局部变量。像在预览版中那样，创建不能有线程局部变量的虚拟线程是不可能的。对线程局部变量的保证支持确保了更多现有库可以不加修改地与虚拟线程一起使用，并有助于将面向任务的代码迁移到使用虚拟线程。
 * <br>
 * 直接使用Thread创建的虚拟线程。Builder API(与通过Executors.newVirtualThreadPerTaskExecutor()创建的API相反)现在默认情况下也会在其整个生命周期中受到监控，并通过观察虚拟线程部分中描述的新线程转储进行观察。
 */
public class VirtualThreads {
    /**
     * <h2>目标</h2>
     * 使以简单的每个请求一个线程风格编写的服务器应用程序能够扩展到接近最佳的硬件利用率。
     * <br>允许使用java.lang.Thread API的现有代码以最小的更改采用虚拟线程。
     * <br>使用现有JDK工具对虚拟线程进行简单的故障排除、调试和分析。
     */
    void goals() {
    }

    public static void main(String[] args) throws InterruptedException {
//        withExecutors();
        Runnable runnable = () -> {
            System.out.println("===== v1 start =====");
            System.out.println("===  Hello world ===");
            System.out.println("====  v1 end   ====");
        };

        Thread thread = Thread.startVirtualThread(runnable);
        Thread virtual = Thread.ofVirtual().name("v1").unstarted(runnable);
        System.out.println(virtual.isVirtual());
        System.out.println(virtual.isAlive());

    }

    private static void withExecutors() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 10_000).forEach(i -> {
                executor.submit(() -> {
                    System.out.println(System.currentTimeMillis() + "---" + i);
//                    Thread.sleep(Duration.ofSeconds(1));
                    return i;
                });
            });
        }  // executor.close() is called implicitly, and waits
    }
}
