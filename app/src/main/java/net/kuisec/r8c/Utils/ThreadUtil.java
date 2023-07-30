package net.kuisec.r8c.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程统一管理工具类
 *
 * @author Jinsn
 */
public class ThreadUtil {
    
    private static ExecutorService mainExecutorService = null;
    private static boolean init = false;

    /**
     * 初始化
     */
    public static void init() {
        mainExecutorService = newExecutorService();
        init = true;
    }

    /**
     * 获得服务对象
     *
     * @return 返回线程池对象，用于特定场景
     */
    public static ExecutorService getExecutorService() {
        if (init) {
            return mainExecutorService;
        } else {
            return null;
        }
    }

    /**
     * 创建一个缓存线程池
     *
     * @return 返回一个可执行服务
     */
    private static ExecutorService newExecutorService() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactoryUtil("程序主要线程池"));
    }

    /**
     * 往线程池中添加线程
     *
     * @param runnable 线程内容
     */
    public static void createThread(Runnable runnable) {
        if (init && mainExecutorService != null) {
            mainExecutorService.execute(runnable);
        }
    }

    /**
     * 关闭线程池
     */
    public static void shutdownExecutorService() {
        if (init && mainExecutorService != null) {
            mainExecutorService.shutdownNow();
            LogUtil.printLog("线程池", "已经提交关闭线程池的命令！");
        }
    }

    /**
     * 线程休眠
     * @param time 休眠时长ms
     */
    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
            LogUtil.printLog("线程池", "线程休眠失败！");
        }
    }


}
