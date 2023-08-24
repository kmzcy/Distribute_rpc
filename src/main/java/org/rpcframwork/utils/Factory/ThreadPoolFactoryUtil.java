package org.rpcframwork.utils.Factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.rpcframwork.core.config.CustomThreadPoolConfig;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class ThreadPoolFactoryUtil {
    /**
     * 通过 threadNamePrefix 来区分不同线程池（我们可以把相同 threadNamePrefix 的线程池看作是为同一业务场景服务）。
     * key: threadNamePrefix
     * value: threadPool
     */

    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    private ThreadPoolFactoryUtil(){}

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix) {
        CustomThreadPoolConfig customThreadPoolConfig = new CustomThreadPoolConfig();
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix, CustomThreadPoolConfig customThreadPoolConfig) {
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon){
        ExecutorService threadPool = THREAD_POOLS.computeIfAbsent(threadNamePrefix, k -> createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon));
        // 如果 threadPool 被 shutdown 的话就重新创建一个
        if (threadPool.isShutdown() || threadPool.isTerminated()) {
            THREAD_POOLS.remove(threadNamePrefix);
            threadPool = createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon);
            THREAD_POOLS.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }

    private static ExecutorService createThreadPool(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon){
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(customThreadPoolConfig.getCorePoolSize(),
                                      customThreadPoolConfig.getMaximumPoolSize(),
                                      customThreadPoolConfig.getKeepAliveTime(),
                                      customThreadPoolConfig.getUnit(),
                                      customThreadPoolConfig.getWorkQueue(),
                                      threadFactory);
    }

    /**
     * 创建 ThreadFactory 。如果threadNamePrefix不为空则使用自建ThreadFactory，否则使用defaultThreadFactory
     *
     * @param threadNamePrefix 作为创建的线程名字的前缀
     * @param daemon           指定是否为 Daemon Thread(守护线程):所谓守护线程，是指在程序运行的时候在后台提供一种通用服务的线程.比如垃圾回收线程.
     *                         用户线程和守护线程两者几乎没有区别，唯一的不同之处就在于虚拟机的离开：如果用户线程已经全部退出运行了，只剩下守护线程存在
     *                         了，虚拟机也就退出了。将线程转换为守护线程可以通过调用Thread对象的setDaemon(true)方法来实现。在使用守护线程时需要注意一下几点：
     *                          (1) thread.setDaemon(true)必须在thread.start()之前设置，否则会跑出一个IllegalThreadStateException异常。
     *                               你不能把正在运行的常规线程设置为守护线程。
     *                          (2) 在Daemon线程中产生的新线程也是Daemon的。
     *                          (3) 守护线程应该永远不去访问固有资源，如文件、数据库，因为它会在任何时候甚至在一个操作的中间发生中断。
     * @return ThreadFactory
     */
    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon){
        if(threadNamePrefix != null){
            if(daemon != null){
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon)
                        .build();
            }else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }

        return Executors.defaultThreadFactory();
    }

    public static void shutDownAllThreadPool() {
        log.info("call shutDownAllThreadPool method");
        // 并行处理流，处理每个线程池
        THREAD_POOLS.entrySet().parallelStream().forEach(entry -> {
            ExecutorService threadPool = entry.getValue();
            threadPool.shutdown();
            log.info("shut down thread pool [{}] [{}]", entry.getKey(), threadPool.isTerminated());
            shutdownThreadPool(entry.getKey(), threadPool);
        });
    }

    /**
     * 关闭给定名字的单一线程池
     * @param threadNamePrefix
     */
    public static void shutdownThreadPool(String threadNamePrefix, ExecutorService threadPool){
        // isTerminated()方法是ExecutorService接口中的一个方法。当我们调用线程池的isTerminated()方法时，
        // 如果线程池中的所有任务都已经完成了，且线程池已经关闭了，那么这个方法就会返回true。否则，这个方法就会返回false。
        // 调用shutdown()方法将启动线程池的关闭序列。这个方法不会立即停止所有正在执行的任务，而是会拒绝接受新的任务，并且尽可能地完成所有已经提交的任务。
        if(threadPool == null){
            throw new RuntimeException("When shutdownThreadPool, there is no thread pool match name");
        }
        if(threadPool.isShutdown()){
            throw new RuntimeException("When shutdownThreadPool, the thread pool has already shut down");
        }

        log.info("Start to shutdown the thead pool: {}", threadNamePrefix);
        threadPool.shutdown();
        try {
            // 等待未完成任务结束
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow(); // 取消当前执行的任务
                log.warn("Interrupt the worker, which may cause some task inconsistent. Please check the biz logs.");

                // 等待任务取消的响应
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS))
                    log.error("Thread pool can't be shutdown even with interrupting worker threads, which may cause some task inconsistent. Please check the biz logs.");
            }
        } catch (InterruptedException ie) {
            // 重新取消当前线程进行中断
            threadPool.shutdownNow();
            log.error("The current server thread is interrupted when it is trying to stop the worker threads. This may leave an inconcistent state. Please check the biz logs.");

            // 保留中断状态
            Thread.currentThread().interrupt();
        }

        log.info("Finally shutdown the thead pool: {}", threadNamePrefix);
    }

    /**
     * 打印线程池的状态
     *
     * @param threadPool 线程池对象
     */
    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool) {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, createThreadFactory("print-thread-pool-status", false));
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("============ThreadPool Status=============");
            log.info("ThreadPool Size: [{}]", threadPool.getPoolSize());
            log.info("Active Threads: [{}]", threadPool.getActiveCount());
            log.info("Number of Tasks : [{}]", threadPool.getCompletedTaskCount());
            log.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());
            log.info("===========================================");
        }, 0, 1, TimeUnit.SECONDS);
    }
}
