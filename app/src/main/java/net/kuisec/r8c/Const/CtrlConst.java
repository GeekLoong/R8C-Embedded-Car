package net.kuisec.r8c.Const;

/**
 * 安卓控制指令常量，由0xCC作为前一位指令
 */
public class CtrlConst {


    /**
     * A 类 TFT 安卓控制
     */
    public static final byte TFT_A_ANDROID = 0x0B;
    /**
     * B 类 TFT 安卓控制
     */
    public static final byte TFT_B_ANDROID = 0x08;
    /**
     * 上一张
     */
    public static final byte TFT_IMG_UP = 0x01;
    /**
     * 下一张
     */
    public static final byte TFT_IMG_DOWN = 0x02;


    /**
     * 启动主车
     */
    public static final byte START_TASK = 0x01;

    /**
     * 上传 TFT A 码盘
     */
    public static final byte TFT_A_DASHBOARD = 0x02;

    /**
     * 上传 TFT B 码盘
     */
    public static final byte TFT_B_DASHBOARD = 0x03;

    /**
     * 循迹
     */
    public static final byte FIND_WAY = (byte) 0xB1;

    /**
     * 前进固定码盘
     */
    public static final byte GO = (byte) 0xB2;

    /**
     * 左转
     */
    public static final byte LEFT_TURN = (byte) 0xB3;

    /**
     * 右转
     */
    public static final byte RIGHT_TURN = (byte) 0xB4;

    /**
     * 主车停止
     */
    public static final byte STOP = (byte) 0xB5;

    /**
     * 取消主车等待从车的过程
     */
    public static final byte NOT_WAIT_SLAVE = (byte)0xFF;

}
