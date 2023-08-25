package net.kuisec.r8c.Const;

/**
 * 所有标志物常量
 */
public class SignConst {


    /**
     * 所有 A 类标志物序列
     */
    public static final byte A_FLAG = 0x01;
    /**
     * 所有 B 类标志物序列
     */
    public static final byte B_FLAG = 0x02;
    /**
     * 所有 C 类标志物序列
     */
    public static final byte C_FLAG = 0x03;
    /**
     * 所有 D 类标志物序列
     */
    public static final byte D_FLAG = 0x04;


    /**
     * 二维码 存储内容获取标志
     */
    public static final byte QRCODE_STORAGE_FLAG = 0x01;


    /**
     * 文本 存储内容获取标志
     */
    public static final byte TEXT_STORAGE_FLAG = 0x02;


    /**
     * 交通灯 存储内容获取标志
     */
    public static final byte TRAFFIC_LIGHT_STORAGE_FLAG = 0x03;
    /**
     * 红灯
     */
    public static final byte TRAFFIC_LIGHT_RED = 0x01;
    /**
     * 绿灯
     */
    public static final byte TRAFFIC_LIGHT_GREEN = 0x02;
    /**
     * 黄灯
     */
    public static final byte TRAFFIC_LIGHT_YELLOW = 0x03;


    /**
     * RFID 存储内容获取标志
     */
    public static final byte RFID_STORAGE_FLAG = 0x04;
    /**
     * RFID 破损车牌 存储内容获取标志
     */
    public static final byte RFID_LP = 0x01;
    /**
     * RFID 坐标 存储内容获取标志
     */
    public static final byte RFID_LOCATION = 0x02;
    /**
     * RFID 报警码 存储内容获取标志
     */
    public static final byte RFID_ALARM = 0x03;
    /**
     * RFID 内容存储
     */
    public static final byte RFID_CONTENT = 0x04;


    /**
     * TFT 车牌 存储内容获取标志
     */
    public static final byte TFT_LP_STORAGE_FLAG = 0x05;


    /**
     * TFT 指定形状总数 存储内容获取标志
     */
    public static final byte TFT_SHAPE_STORAGE_FLAG = 0x06;
    /**
     * 矩形
     */
    public static final byte TFT_SHAPE_REC = 0x01;
    /**
     * 圆形
     */
    public static final byte TFT_SHAPE_CIR = 0x02;
    /**
     * 三角形
     */
    public static final byte TFT_SHAPE_TRI = 0x03;
    /**
     * 菱形
     */
    public static final byte TFT_SHAPE_DIA = 0x04;
    /**
     * 五角星形
     */
    public static final byte TFT_SHAPE_FIV = 0x05;
    /**
     * 梯形
     */
    public static final byte TFT_SHAPE_TRA = 0x06;


    /**
     * TFT 指定颜色总数 存储内容获取标志
     */
    public static final byte TFT_COLOR_STORAGE_FLAG = 0x07;
    /**
     * 红色
     */
    public static final byte TFT_COLOR_RED = 0x01;
    /**
     * 绿色
     */
    public static final byte TFT_COLOR_GREEN = 0x02;
    /**
     * 蓝色
     */
    public static final byte TFT_COLOR_BLUE = 0x03;
    /**
     * 黄色
     */
    public static final byte TFT_COLOR_YELLOW = 0x04;
    /**
     * 品色
     */
    public static final byte TFT_COLOR_MAGENTA = 0x05;
    /**
     * 青色
     */
    public static final byte TFT_COLOR_SKY_BLUE = 0x06;
    /**
     * 黑色
     */
    public static final byte TFT_COLOR_BLACK = 0x07;
    /**
     * 白色
     */
    public static final byte TFT_COLOR_WHITE = 0x08;


    /**
     * TFT 指定颜色的形状总数 存储内容获取标志
     * TFT 指定形状的颜色总数 存储内容获取标志
     */
    public static final byte TFT_SHAPE_COLOR_STORAGE_FLAG = 0x08;


    /**
     * TFT 16进制 存储内容获取标志
     */
    public static final byte TFT_HEX_STORAGE_FLAG = 0x09;


    /**
     * TFT 所有颜色总数 存储内容获取标志
     */
    public static final byte TFT_COLOR_ALL_STORAGE_FLAG = 0x0A;


    /**
     * TFT 所有形状总数 存储内容获取标志
     */
    public static final byte TFT_SHAPE_ALL_STORAGE_FLAG = 0x0B;

    /**
     * TFT 交通标志类型 存储内容获取标志
     */
    public static final byte TFT_TRAFFIC_STORAGE_FLAG = 0x0C;


    /**
     * TFT 车辆类型 存储内容获取标志
     */
    public static final byte TFT_VEHICLE_TYPE_STORAGE_FLAG = 0x0D;
    /**
     * 自行车
     */
    public static final byte TFT_VEHICLE_TYPE_BICYCLE = 0x01;
    /**
     * 摩托车
     */
    public static final byte TFT_VEHICLE_TYPE_MOTORCYCLE = 0x02;
    /**
     * 轿车
     */
    public static final byte TFT_VEHICLE_TYPE_CAR = 0x03;
    /**
     * 卡车
     */
    public static final byte TFT_VEHICLE_TYPE_TRUCK = 0x04;


    /**
     * TFT 获取最多图形信息类别
     */
    public static final byte TFT_MAX_SHAPE_CLASS_STORAGE_FLAG = 0x0E;


    /**
     * TFT 获取行人数量
     */
    public static final byte TFT_PERSON_COUNT_STORAGE_FLAG = 0x0F;


    /**
     * 摄像头预设1 默认视角
     */
    public static final byte CAMERA_SET1 = 0x01;
    /**
     * 摄像头预设2 静态标志物
     */
    public static final byte CAMERA_SET2 = 0x02;
    /**
     * 摄像头预设3 交通灯视角
     */
    public static final byte CAMERA_SET3 = 0x03;
    /**
     * 摄像头预设4 TFT 视角
     */
    public static final byte CAMERA_SET4 = 0x04;


    /**
     * 主车 Zigbee 发送日志保存
     */
    public static final byte LOG_CAR_MODEL_SEND = 0x0A;
    /**
     * 主车 Zigbee 接收日志保存
     */
    public static final byte LOG_CAR_MODEL_RECEP = 0x0B;
}
