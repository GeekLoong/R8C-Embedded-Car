package net.kuisec.r8c.Const;

/**
 * 重要协议帧常量
 */
public class HeaderConst {


    /**
     * 主车协议头
     */
    public static final byte CAR_FLAG = 0x77;
    /**
     * 安卓协议头
     */
    public static final byte ANDROID_FLAG = 0x66;


    /**
     * 主车状态标志
     */
    public static final byte CAR_STATE_FLAG = (byte) 0xAA;
    /**
     * 主车保存日志
     */
    public static final byte SAVE_CAR_LOG = (byte) 0xBB;
    /**
     * 传输任务标志（GET）
     */
    public static final byte TASK_FLAG = (byte) 0xCC;
    /**
     * 传输数据标志（POST）
     */
    public static final byte DATA_FLAG = (byte) 0xDD;
    /**
     * 主车请求任务数据的标志
     */
    public static final byte GET_TASK_DATA_FLAG = (byte) 0xEE;
    /**
     * 主车 or 安卓回复标志（握手）
     */
    public static final byte REPLY_FLAG = (byte) 0xFF;


    /**
     * 主车 or 安卓协议尾
     */
    public static final byte END_FLAG = (byte) 0x00;

}
