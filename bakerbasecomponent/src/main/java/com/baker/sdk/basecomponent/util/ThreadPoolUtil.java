package com.baker.sdk.basecomponent.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hsj55
 * 2020/9/15
 */
public class ThreadPoolUtil {
    private ThreadPoolUtil() {
    }

    //可用处理器的Java虚拟机的数量
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;
    private static final int KEEP_ALIVE_TIME = 10;
    private static final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
            10);

    // 线程工厂,把传递进来的runnable对象生成一个Thread
    private static final ThreadFactory threadFactory = new ThreadFactory() {

        // 原子型的integer变量生成的integer值不会重复
        private final AtomicInteger integer = new AtomicInteger();

        @Override
        public Thread newThread(Runnable arg0) {
            return new Thread(arg0, "MyThreadPool thread:"
                    + integer.getAndIncrement());
        }
    };

    // 当线程池发生异常的时候回调进入
    private static final RejectedExecutionHandler handler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 进行重启操作
        }

    };
    private static final ThreadPoolExecutor threadPool;

    static {
//        corePoolSize： 线程池维护线程的最少数量
//        maximumPoolSize：线程池维护线程的最大数量
//        keepAliveTime： 线程池维护线程所允许的空闲时间
//        unit： 线程池维护线程所允许的空闲时间的单位
//        workQueue： 线程池所使用的缓冲队列
//        threadFactory：创建执行线程的工厂
//        handler： 线程池对拒绝任务的处理策略
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, threadFactory,
                handler);
    }

    public static void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }

    public static boolean removeTask(Runnable runnable){
        boolean remove = threadPool.remove(runnable);
        return remove;
    }
}
