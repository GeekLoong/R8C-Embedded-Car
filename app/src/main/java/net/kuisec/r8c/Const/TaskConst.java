package net.kuisec.r8c.Const;

/**
 * 所有任务常量
 */
public class TaskConst {


    /**
     * 二维码识别
     */
    public static final byte QRCODE_REC_FLAG = 0x51;


    /**
     * 文字识别
     */
    public static final byte TEXT_REC_FLAG = 0x61;


    /**
     * 交通灯识别
     */
    public static final byte TRAFFIC_LIGHT_REC_FLAG = 0x71;


    /**
     * TFT 识别
     */
    public static final byte TFT_REC_FLAG = (byte) 0x81;


    /**
     * 摄像头预设位调整
     */
    public static final byte CAMERA_ADJUST_FLAG = (byte) 0x91;
}
