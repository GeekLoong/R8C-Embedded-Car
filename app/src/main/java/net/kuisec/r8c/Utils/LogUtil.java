package net.kuisec.r8c.Utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 规范统一日志工具类
 *
 * @author Jinsn
 * @date 2022/10/6 19:16
 */
public class LogUtil {
    private static final String TAG = "日志系统";
    private static List<String> androidLogList = new ArrayList<>();
    private static List<String> carLogList = new ArrayList<>();
    /**
     * 触摸状态
     */
    private static boolean logTouch = false;

    /**
     * 更新状态
     */
    private static boolean logUpdate = false;

    /**
     * 得到当前触摸状态
     *
     * @return 返回一个真假状态
     */
    public static boolean isLogTouch() {
        return logTouch;
    }

    /**
     * 设置日志系统触摸状态
     *
     * @param touch 状态 true（正在触摸） false（触摸结束）
     */
    public static void setLogTouch(boolean touch) {
        logTouch = touch;
    }

    /**
     * 初始化日志系统
     */
    public static void init() {
        //初始化时更新一次数据
        logUpdate = true;
        //初始化数组内容 - 从Shared对象拿出本地数据
        androidLogList = SharedPreferencesUtil.file2List("log-android");
        carLogList = SharedPreferencesUtil.file2List("log-car");
        //监听日志变化
        logChangeListener();
    }


    /**
     * 查询日志模式并切换到那个模式的数据
     */
    public static void queryHandLogModel() {
        logUpdate = true;
    }


    /**
     * 打印到 app 日志
     *
     * @param logTitle   日志标题
     * @param logContent 日志内容
     */
    public static void printLog(String logTitle, String logContent) {
        String log = logTitle + " ---- " + logContent;
        Log.e(TAG, log);
        addLog2System(log, "log-android");
    }

    /**
     * 添加打印到 car 日志
     *
     * @param logContent 日志内容
     */
    public static void printCarLog(String logTitle, String logContent) {
        String log = logTitle + " ---- " + logContent;
        Log.e(TAG, log);
        addLog2System(log, "log-car");
    }

    /**
     * 添加打印到 app 日志
     *
     * @param logContent 日志内容
     */
    public static void printLog(String logContent) {
        Log.e(TAG, logContent);
        addLog2System(logContent, "log-android");
    }


    /**
     * 打印到调试系统日志
     *
     * @param logTitle   日志标题
     * @param logContent 日志内容
     */
    public static void printSystemLog(String logTitle, String logContent) {
        String log = logTitle + " ---- " + logContent;
        Log.e(TAG, log);
    }

    /**
     * 添加日志到日志系统
     * 踩坑踩了一天
     * 原因竟然是关于赋值的指针问题
     * 应该使用深度拷贝的方式去拿临时数据
     * Java 提供了一个快捷的拷贝方式，如下
     */
    public static void addLog2System(String log, String logModel) {
        //非常小概率会索引越界报错，多个线程同时添加数据会引起这样的错误，猜测可能是添加数据时，多个线程都在获取索引值，但有一个线程在别的线程刚获得值时就添加了新的值，就会导致别的线程找不到那个索引，一般不会遇到，遇到就是开软件秒闪退
        List<String> logList;
        if (logModel.equals("log-android")) {
            logList = androidLogList;
        } else {
            logList = carLogList;
        }
        if (log.contains("数据内容")) {
            logList.add("\n" + TimeUtil.getLifeTime() + "：" + log);
        } else {
            logList.add(TimeUtil.getLifeTime() + "：" + log);
        }
        //临时数组，备份当前日志，防止数据修改引起的冲突，目的是控制数组版本，使得数据更新时永远都能得到最新的
        List<String> tempLogList = new ArrayList<>(logList);
        //当数据达到100个时，移除第一个数据，确保数据最新
        if (getSize(logModel) > 99) {
            tempLogList.remove(0);
            logList.remove(0);
        }
        StringBuilder builder = new StringBuilder();
        for (String line : tempLogList) {
            builder.append(line).append("\n");
        }
        String logContent = builder.toString();
        //存到本地日志系统
        SharedPreferencesUtil.insert(logModel, logContent);
        //清除临时数组
        tempLogList.clear();
        //开始更新数据
        logUpdate = true;
    }

    /**
     * 监听数组的长度，当数组长度增加时，同步数据到UI
     */
    private static void logChangeListener() {
        ThreadUtil.createThread(() -> {
            while (true) {
                if (!logTouch && logUpdate) {
                    //根据日志模式更新日志类型
                    String logName = SharedPreferencesUtil.queryKey2Value("log-name");
                    if (logName.equals("log-android")) {
                        HandlerUtil.sendMsg(HandlerUtil.LOG_FLAG, HandlerUtil.LOG_UPDATE, SharedPreferencesUtil.queryKey2Value("log-android"));
                    } else {
                        HandlerUtil.sendMsg(HandlerUtil.LOG_FLAG, HandlerUtil.LOG_UPDATE, SharedPreferencesUtil.queryKey2Value("log-car"));
                    }
                    logUpdate = false;
                }
            }
        });
    }

    public static int getSize(String logModel) {
        if (logModel.equals("log-android")) {
            return androidLogList.size();
        } else {
            return carLogList.size();
        }
    }

    /**
     * 打印竞赛平台发送的数据
     * @param mByte 字节数组
     * @param dataType 数据类型
     * @param logModel 日志模式
     */
    public static void printData(byte[] mByte, String dataType, String logModel) {
        if (mByte != null && mByte.length > 0) {
            final StringBuilder builder = new StringBuilder(mByte.length);
            for (byte byteChar : mByte) {
                builder.append(String.format("%02X ", byteChar));
            }
            String logContent = builder.toString();
            LogUtil.printSystemLog("系统日志", logContent);
            if (logModel.equals("log-android"))
                printLog(dataType, logContent);
            else
                printCarLog(dataType, logContent);
        }
    }

}
