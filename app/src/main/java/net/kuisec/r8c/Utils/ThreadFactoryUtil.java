package net.kuisec.r8c.Utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 线程工厂工具类
 * @author Jinsn
 */
public class ThreadFactoryUtil implements ThreadFactory {
    private final String THREAD_NAME;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup group;

    public ThreadFactoryUtil(String threadName) {
        AtomicInteger poolNumber = new AtomicInteger(1);
        THREAD_NAME = threadName + "-" + poolNumber.getAndIncrement() + "-thread-";
        SecurityManager manager = System.getSecurityManager();
        group = (manager != null) ? manager.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(group, runnable, THREAD_NAME + threadNumber.getAndIncrement());
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
