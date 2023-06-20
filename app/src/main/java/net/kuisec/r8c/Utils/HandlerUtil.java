package net.kuisec.r8c.Utils;

import android.os.Handler;
import android.os.Message;

/**
 * 消息分发工具类
 * @author Jinsn
 * @date 2022/10/11 18:02
 */
public class HandlerUtil {
    private static Handler mHandler;
    private static boolean init = false;

    public static final int DATA_PARSE_FLAG = 1;

    public static final int WIFI_STATE_FLAG = 2;
    public static final int WIFI_CLOSE = 0;
    public static final int WIFI_OPEN = 1;

    public static final int SOCKET_STATE_FLAG = 3;
    public static final int SOCKET_CLOSE = 0;
    public static final int SOCKET_OPEN = 1;

    public static final int CAMERA_IP_FLAG = 4;
    public static final int CAMERA_IP_CLOSE = 0;
    public static final int CAMERA_IP_OPEN = 1;

    public static final int LOG_FLAG = 5;
    public static final int LOG_CLOSE = 0;
    public static final int LOG_OPEN = 1;
    public static final int LOG_UPDATE = 2;

    public static final int TEXT_FLAG = 6;
    public static final int CAMERA_IMG_FLAG = 7;
    public static final int DEBUG_IMG_FLAG = 8;
    public static final int VOICE = 9;

    /**
     * 初始化工具类
     * @param handler 消息分发器
     */
    public static void init(Handler handler) {
        mHandler = handler;
        init = true;
    }

    /**
     * 获得消息分发器
     * @return 返回消息分发器对象
     */
    public static Handler getHandler() {
        if (init) {
            return mHandler;
        } else {
            return null;
        }
    }

    /**
     * 发送文字消息到UI
     * @param msg 文字消息
     */
    public static void sendMsg(String msg) {
        if (init) {
            Message message = mHandler.obtainMessage();
            message.what = TEXT_FLAG;
            message.obj = msg;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 发送消息类型（通知型）
     * @param what 消息类型
     */
    public static void sendMsg(int what) {
        if (init) {
            Message message = mHandler.obtainMessage();
            message.what = what;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 数字控制消息
     * @param what 消息类型
     * @param arg1 数字控制指令
     */
    public static void sendMsg(int what, int arg1) {
        if (init) {
            Message message = mHandler.obtainMessage();
            message.what = what;
            message.arg1 = arg1;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 任意对象传输消息
     * @param what 消息类型
     * @param obj 传输的对象
     */
    public static void sendMsg(int what, Object obj) {
        if (init) {
            Message message = mHandler.obtainMessage();
            message.what = what;
            message.obj = obj;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 含数字控制指令的对象传输消息
     * @param what 消息类型
     * @param arg1 数字控制指令
     * @param obj 传输的对象
     */
    public static void sendMsg(int what, int arg1, Object obj) {
        if (init) {
            Message message = mHandler.obtainMessage();
            message.what = what;
            message.arg1 = arg1;
            message.obj = obj;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 提交任务 - 在主线程
     * 提交后会先判断页面是否加载成功，若页面还没有加载，那么放到缓存中，等待页面加载后执行
     * @param runnable 消息处理
     */
    public static void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    /**
     * 提交延时任务 - 在主线程
     * 该方法的作用仅仅是延时操作，将消息加入消息队阻塞指定事件，但不会影响其他消息的传递
     * 不要进行耗时操作，因为最终是在主线程运行
     * 耗时操作请使用ThreadUtil类
     * @param runnable 消息处理
     * @param ms 延时ms
     */
    public static void postDelayed(Runnable runnable, long ms) {
        if (init) {
            mHandler.postDelayed(runnable, ms);
        }
    }
}
