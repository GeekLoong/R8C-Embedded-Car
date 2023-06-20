package net.kuisec.r8c.Utils;

import static net.kuisec.r8c.Const.InteractionConst.REPLY_FLAG;
import static net.kuisec.r8c.Const.ItemConst.A_FLAG;
import static net.kuisec.r8c.Const.ItemConst.TFT_COLOR_BLACK;
import static net.kuisec.r8c.Const.ItemConst.TFT_COLOR_BLUE;
import static net.kuisec.r8c.Const.ItemConst.TFT_COLOR_GREEN;
import static net.kuisec.r8c.Const.ItemConst.TFT_COLOR_MAGENTA;
import static net.kuisec.r8c.Const.ItemConst.TFT_COLOR_RED;
import static net.kuisec.r8c.Const.ItemConst.TFT_COLOR_SKY_BLUE;
import static net.kuisec.r8c.Const.ItemConst.TFT_COLOR_YELLOW;
import static net.kuisec.r8c.Const.ItemConst.TFT_SHAPE_DIA;
import static net.kuisec.r8c.Const.ItemConst.TFT_SHAPE_PEN;
import static net.kuisec.r8c.Const.ItemConst.TFT_SHAPE_REC;
import static net.kuisec.r8c.Const.ItemConst.TFT_SHAPE_ROU;
import static net.kuisec.r8c.Const.ItemConst.TFT_SHAPE_TRI;
import static net.kuisec.r8c.Const.ItemConst.TRAFFIC_LIGHT_GREEN;
import static net.kuisec.r8c.Const.ItemConst.TRAFFIC_LIGHT_RED;
import static net.kuisec.r8c.Const.ItemConst.TRAFFIC_LIGHT_YELLOW;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * 数据处理工具类
 */
public class DataPcsUtil {


    /**
     * 字符串 16 进制分割为 16 进制 byte 数组
     *
     * @param stringHex 字符串 16 进制格式
     * @return 返回 16 进制 byte 数组
     */
    public static byte[] stringHexToByteHex(String stringHex) {
        if (stringHex.length() % 2 == 0) {
            byte[] data = new byte[stringHex.length() / 2];
            for (int i = 0; i < stringHex.length(); i += 2) {
                data[i / 2] = (byte) Integer.parseInt(stringHex.substring(i, i + 2), 16);
            }
            return data;
        } else {
            return new byte[]{};
        }
    }


    /**
     * 字符串转 ASCII 码 byte 数组
     *
     * @param string 字符串
     * @return 返回 ASCII 码字符串
     */
    public static byte[] stringToAsciiBytes(String string) {
        return string.getBytes();
    }


    /**
     * ASCII 码类型的 byte 数组转字符串
     * @param data ASCII 码类型的 byte 数组
     * @return 转换好的字符串
     */
    public static String asciiBytesToString(byte[] data) {
        StringBuilder asciiString = new StringBuilder();
        for (byte b : data) {
            asciiString.append(b);
        }
        return asciiString.toString();
    }


    /**
     * 字符串转 GBK 格式 byte 数组
     *
     * @param string 字符串
     * @return 数组
     */
    public static byte[] stringToBytes(String string) {
        //将文字转成 GBK 格式的数据
        try {
            return string.getBytes("GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new byte[]{};
        }
    }


    /**
     * 从存储在获得出现次数最多的颜色的数量
     *
     * @return 返回数量值
     */
//    public static int getMaxCountColorFromStorage() {
//        int redSum = 0, yellowSum = 0, greenSum = 0, magentaSum = 0, skyBlueSum = 0, blueSum = 0, blackSum = 0;
//        String[] colorNames = {
//                "红色",
//                "黄色",
//                "品红色",
//                "绿色",
//                "蓝色",
//                "天蓝色",
//                "黑色"
//        };
//        for (String colorShape : SharedPreferencesUtil.COLOR_SHAPE_FLAG) {
//            for (String colorName : colorNames) {
//                int index = colorShape.indexOf(colorName);
//                if (index != -1) {
//                    int result = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
//                    switch (colorName) {
//                        case "红色":
//                            if (!colorShape.contains(colorNames[2])) {
//                                redSum += result;
//                            }
//                            break;
//                        case "黄色":
//                            yellowSum += result;
//                            break;
//                        case "品红色":
//                            magentaSum += result;
//                            break;
//                        case "绿色":
//                            greenSum += result;
//                            break;
//                        case "蓝色":
//                            if (!colorShape.contains(colorNames[4])) {
//                                blueSum += result;
//                            }
//                            break;
//                        case "天蓝色":
//                            skyBlueSum += result;
//                            break;
//                        case "黑色":
//                            blackSum += result;
//                            break;
//                    }
//                }
//            }
//        }
//        int[] colorSums = {
//                redSum,
//                greenSum,
//                blueSum,
//                yellowSum,
//                magentaSum,
//                skyBlueSum,
//                blackSum
//        };
//        int maxColorSumIndex = 0;
//        int temp = 0;
//        for (int i = 0; i < colorSums.length; i++) {
//            if (colorSums[i] > temp) {
//                temp = colorSums[i];
//                maxColorSumIndex = i;
//            }
//        }
//        return colorSums[maxColorSumIndex];
//    }


    /**
     * 获得出现次数最多的形状的数量
     * @return 返回形状的数量
     */
    public static int getMaxCountShapeFromStorage() {
        int[] shapes = {
                getShapeFromStorage(TFT_SHAPE_REC),
                getShapeFromStorage(TFT_SHAPE_ROU),
                getShapeFromStorage(TFT_SHAPE_TRI),
                getShapeFromStorage(TFT_SHAPE_DIA),
                getShapeFromStorage(TFT_SHAPE_PEN)
        };
        int maxCountShape = 0;
        for (int count : shapes) {
            if (maxCountShape <= count) {
                maxCountShape = count;
            }
        }
        return maxCountShape;
    }


    /**
     * 获得出现次数最多的颜色的数量
     * @return 返回形状的数量
     */
    public static int getMaxCountColorFromStorage() {
        int[] colors = {
                getColorFromStorage(TFT_COLOR_RED),
                getColorFromStorage(TFT_COLOR_GREEN),
                getColorFromStorage(TFT_COLOR_YELLOW),
                getColorFromStorage(TFT_COLOR_BLUE),
                getColorFromStorage(TFT_COLOR_SKY_BLUE),
                getColorFromStorage(TFT_COLOR_MAGENTA)
        };
        int maxCountColor = 0;
        for (int count : colors) {
            if (maxCountColor <= count) {
                maxCountColor = count;
            }
        }
        return maxCountColor;
    }


    /**
     * 获得出现次数最多的带颜色的形状的数量
     * @return 返回带颜色的形状的数量
     */
    public static int getMaxCountColorShapeFromStorage() {
        int maxCountColorShape = 0;
        for (String colorShapeName : SharedPreferencesUtil.COLOR_SHAPE_FLAG) {
            int tempCount = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShapeName));
            if (maxCountColorShape <= tempCount) {
                maxCountColorShape = tempCount;
            }
        }
        return maxCountColorShape;
    }


    /**
     * 从存储中获得指定颜色的数量并回传给竞赛平台
     *
     * @param colorID 颜色代号
     */
    public static int getColorFromStorage(byte colorID) {
        String colorName = "";
        switch (colorID) {
            case TFT_COLOR_RED:
                colorName = "红色";
                break;
            case TFT_COLOR_GREEN:
                colorName = "绿色";
                break;
            case TFT_COLOR_YELLOW:
                colorName = "黄色";
                break;
            case TFT_COLOR_BLUE:
                colorName = "蓝色";
                break;
            case TFT_COLOR_SKY_BLUE:
                colorName = "天蓝色";
                break;
            case TFT_COLOR_MAGENTA:
                colorName = "品红色";
                break;
            case TFT_COLOR_BLACK:
                colorName = "黑色";
                break;
        }
        if (!colorName.isEmpty()) {
            String[] shapes = {
                    colorName + "三角形",
                    colorName + "矩形",
                    colorName + "菱形",
                    colorName + "五角星形",
                    colorName + "圆形"
            };
            int colorSum = 0;
            for (String shape : shapes) {
                colorSum += Integer.parseInt(SharedPreferencesUtil.queryKey2Value(shape));
            }
            return colorSum;
        }
        return 0;
    }


    /**
     * 从存储中获得指定形状的数量并回传给竞赛平台
     *
     * @param shapeID 形状代号
     */
    public static int getShapeFromStorage(byte shapeID) {
        String shapeName = "";
        switch (shapeID) {
            case TFT_SHAPE_REC:
                shapeName = "矩形";
                break;
            case TFT_SHAPE_ROU:
                shapeName = "圆形";
                break;
            case TFT_SHAPE_TRI:
                shapeName = "三角形";
                break;
            case TFT_SHAPE_DIA:
                shapeName = "菱形";
                break;
            case TFT_SHAPE_PEN:
                shapeName = "五角星形";
                break;
        }
        if (!shapeName.isEmpty()) {
            String[] colors = {
                    "天蓝色" + shapeName,
                    "黄色" + shapeName,
                    "品红色" + shapeName,
                    "蓝色" + shapeName,
                    "绿色" + shapeName,
                    "红色" + shapeName,
                    "黑色" + shapeName
            };
            int shapeSum = 0;
            for (String color : colors) {
                shapeSum += Integer.parseInt(SharedPreferencesUtil.queryKey2Value(color));
            }
            return shapeSum;
        }
        return 0;
    }


    /**
     * 获得数量最多的形状类型ID
     *
     * @return 返回形状类型ID
     */
    public static byte getMaxShapeClassFromStorage() {
        int[] shapes = {
                getShapeFromStorage(TFT_SHAPE_REC),
                getShapeFromStorage(TFT_SHAPE_ROU),
                getShapeFromStorage(TFT_SHAPE_TRI),
                getShapeFromStorage(TFT_SHAPE_DIA),
                getShapeFromStorage(TFT_SHAPE_PEN)
        };
        int temp = 0;
        byte maxShapeID = 0x01;
        for (int i = 0; i < shapes.length; i++) {
            if (shapes[i] >= temp) {
                temp = shapes[i];
                switch (i) {
                    case 0:
                        maxShapeID = TFT_SHAPE_REC;
                        break;
                    case 1:
                        maxShapeID = TFT_SHAPE_ROU;
                        break;
                    case 2:
                        maxShapeID = TFT_SHAPE_TRI;
                        break;
                    case 3:
                        maxShapeID = TFT_SHAPE_DIA;
                        break;
                    case 4:
                        maxShapeID = TFT_SHAPE_PEN;
                        break;
                }
            }
        }
        return maxShapeID;
    }


    /**
     * 获得颜色最多的颜色类型ID
     *
     * @return 返回颜色类型ID
     */
    public static byte getMaxColorClassFromStorage() {
        int[] colors = {
                getColorFromStorage(TFT_COLOR_RED),
                getColorFromStorage(TFT_COLOR_YELLOW),
                getColorFromStorage(TFT_COLOR_GREEN),
                getColorFromStorage(TFT_COLOR_BLUE),
                getColorFromStorage(TFT_COLOR_SKY_BLUE),
                getColorFromStorage(TFT_COLOR_MAGENTA)
        };
        int temp = 0;
        byte maxColorID = 0x01;
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] >= temp) {
                temp = colors[i];
                switch (i) {
                    case 0:
                        maxColorID = TFT_COLOR_RED;
                        break;
                    case 1:
                        maxColorID = TFT_COLOR_YELLOW;
                        break;
                    case 2:
                        maxColorID = TFT_COLOR_GREEN;
                        break;
                    case 3:
                        maxColorID = TFT_COLOR_BLUE;
                        break;
                    case 4:
                        maxColorID = TFT_COLOR_SKY_BLUE;
                        break;
                    case 5:
                        maxColorID = TFT_COLOR_MAGENTA;
                        break;
                }
            }
        }
        return maxColorID;
    }


    /**
     * 获得颜色类型最多的形状类型ID
     * @return 返回形状类型ID
     */
    public static byte getMaxColorShapeClassFromStorage() {
        int[] shapes = {
                0,
                0,
                0,
                0,
                0
        };
        for (String colorShape : SharedPreferencesUtil.COLOR_SHAPE_FLAG) {
            int count = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
            if (count > 0) {
                int colorIndex = colorShape.indexOf("色");
                switch (colorShape.substring(colorIndex + 1)) {
                    case "矩形":
                        shapes[0]++;
                        break;
                    case "圆形":
                        shapes[1]++;
                        break;
                    case "三角形":
                        shapes[2]++;
                        break;
                    case "菱形":
                        shapes[3]++;
                        break;
                    case "五角星形":
                        shapes[4]++;
                        break;
                }
            }
        }
        int temp = 0;
        byte maxShapeID = 0x01;
        for (int i = 0; i < 5; i++) {
            if (shapes[i] >= temp) {
                temp = shapes[i];
                switch (i) {
                    case 0:
                        maxShapeID = TFT_SHAPE_REC;
                        break;
                    case 1:
                        maxShapeID = TFT_SHAPE_ROU;
                        break;
                    case 2:
                        maxShapeID = TFT_SHAPE_TRI;
                        break;
                    case 3:
                        maxShapeID = TFT_SHAPE_DIA;
                        break;
                    case 4:
                        maxShapeID = TFT_SHAPE_PEN;
                        break;
                }
            }
        }
        return maxShapeID;
    }


    /**
     * 从存储中获得所有颜色类型的数量并回传给竞赛平台
     */
    public static int getColorAllFromStorage() {
        int allColorSum;
        int redSum = 0, yellowSum = 0, greenSum = 0, magentaSum = 0, skyBlueSum = 0, blueSum = 0, blackSum = 0;
        for (String colorShape : SharedPreferencesUtil.COLOR_SHAPE_FLAG) {
            if (redSum == 0) {
                int index = colorShape.indexOf("红色");
                if (index != -1 && !colorShape.contains("品红色")) {
                    int result = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
                    if (result > 0)
                        redSum += 1;
                    continue;
                }
            }
            if (yellowSum == 0) {
                int index = colorShape.indexOf("黄色");
                if (index != -1) {
                    int result = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
                    if (result > 0)
                        yellowSum += 1;
                    continue;
                }
            }
            if (greenSum == 0) {
                int index = colorShape.indexOf("绿色");
                if (index != -1) {
                    int result = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
                    if (result > 0)
                        greenSum += 1;
                    continue;
                }
            }
            if (magentaSum == 0) {
                int index = colorShape.indexOf("品红色");
                if (index != -1) {
                    int result = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
                    if (result > 0)
                        magentaSum += 1;
                    continue;
                }
            }
            if (skyBlueSum == 0) {
                int index = colorShape.indexOf("天蓝色");
                if (index != -1) {
                    int result = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
                    if (result > 0)
                        skyBlueSum += 1;
                    continue;
                }
            }
            if (blueSum == 0) {
                int index = colorShape.indexOf("蓝色");
                if (index != -1 && !colorShape.contains("蓝色")) {
                    int result = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
                    if (result > 0)
                        blueSum += 1;
                    continue;
                }
            }
            if (blackSum == 0) {
                int index = colorShape.indexOf("黑色");
                if (index != -1) {
                    int result = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
                    if (result > 0)
                        blackSum += 1;
                }
            }
        }
        allColorSum = redSum + yellowSum + greenSum + magentaSum + skyBlueSum + blueSum + blackSum;
        return allColorSum;
    }


    /**
     * 从存储中获得所有形状类型的数量并回传给竞赛平台
     */
    public static int getShapeAllFromStorage() {
        int allShapeSum;
        int rectSum = 0, rouSum = 0, triSum = 0, penSum = 0, diaSum = 0;
        for (String colorShape : SharedPreferencesUtil.COLOR_SHAPE_FLAG) {
            if (rectSum == 0) {
                int index = colorShape.indexOf("矩形");
                if (index != -1) {
                    int result = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
                    if (result > 0)
                        rectSum += 1;
                    continue;
                }
            }
            if (triSum == 0) {
                int index = colorShape.indexOf("三角形");
                if (index != -1) {
                    int result = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
                    if (result > 0)
                        triSum += 1;
                    continue;
                }
            }
            if (diaSum == 0) {
                int index = colorShape.indexOf("菱形");
                if (index != -1) {
                    int result = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
                    if (result > 0)
                        diaSum += 1;
                    continue;
                }
            }
            if (penSum == 0) {
                int index = colorShape.indexOf("五角星形");
                if (index != -1) {
                    int result = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
                    if (result > 0)
                        penSum += 1;
                    continue;
                }
            }
            if (rouSum == 0) {
                int index = colorShape.indexOf("圆形");
                if (index != -1) {
                    int result = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
                    if (result > 0)
                        rouSum += 1;
                }
            }
        }
        allShapeSum = rectSum + rouSum + triSum + diaSum + penSum;
        return allShapeSum;
    }


    /**
     * 从存储中获得指定颜色和指定形状的数量并回传给竞赛平台
     *
     * @param colorID 颜色代号
     * @param shapeID 形状代号
     */
    public static int getColorShapeFromStorage(byte colorID, byte shapeID) {
        String colorName = "";
        switch (colorID) {
            case TFT_COLOR_RED:
                colorName = "红色";
                break;
            case TFT_COLOR_GREEN:
                colorName = "绿色";
                break;
            case TFT_COLOR_YELLOW:
                colorName = "黄色";
                break;
            case TFT_COLOR_BLUE:
                colorName = "蓝色";
                break;
            case TFT_COLOR_SKY_BLUE:
                colorName = "天蓝色";
                break;
            case TFT_COLOR_MAGENTA:
                colorName = "品红色";
                break;
            case TFT_COLOR_BLACK:
                colorName = "黑色";
                break;
        }
        String shapeName = "";
        switch (shapeID) {
            case TFT_SHAPE_REC:
                shapeName = "矩形";
                break;
            case TFT_SHAPE_ROU:
                shapeName = "圆形";
                break;
            case TFT_SHAPE_TRI:
                shapeName = "三角形";
                break;
            case TFT_SHAPE_DIA:
                shapeName = "菱形";
                break;
            case TFT_SHAPE_PEN:
                shapeName = "五角星形";
                break;
        }
        if (!colorName.isEmpty() && !shapeName.isEmpty()) {
            int colorShapeSum = 0;
            String colorShape = colorName + shapeName;
            colorShapeSum += Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
            return colorShapeSum;
        }
        return 0;
    }


    /**
     * 存储形状和颜色
     * 以角点统计区分图形 五角星 5；三角形 3；矩形和菱形 4；圆形 6~9；
     *
     * @param color   形状的颜色
     * @param corners 角点数量
     * @param rate    多边形与外接旋转矩形的百分比
     */
    public static String storeAsShapeColor(String color, int corners, double rate) {
        String shape = "";
        String resN = "null";
        switch (corners) {
            case 3:
                shape = "三角形";
                resN = "Tri";
                break;
            case 4:
                LogUtil.print("四边形面积", "外接最小四边形，覆盖率：" + rate);
                if (rate >= 0.80) {
                    shape = "矩形";
                    resN = "Rec";
                } else {
                    shape = "菱形";
                    resN = "Dia";
                }
                break;
            case 5:
                shape = "五角星形";
                resN = "Pen";
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                shape = "圆形";
                resN = "Cir";
                break;
        }
        //存储内容
        if (!shape.isEmpty()) {
            String valueName = color + shape;
            LogUtil.print("多边形颜色分类", valueName);
            int shapeSum = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(valueName)) + 1;
            SharedPreferencesUtil.insert(valueName, String.valueOf(shapeSum));
        }
        return resN;
    }


    /**
     * 交通灯识别与保存
     *
     * @param lightName 交通灯识别结果
     * @param classID   交通灯类型
     */
    public static void storeAsTrafficLight(String lightName, byte classID) {
        byte lightID;
        switch (lightName) {
            case "RedLight":
                lightID = TRAFFIC_LIGHT_RED;
                break;
            case "GreenLight":
                lightID = TRAFFIC_LIGHT_GREEN;
                break;
            case "YellowLight":
                lightID = TRAFFIC_LIGHT_YELLOW;
                break;
            default:
                lightID = TRAFFIC_LIGHT_GREEN;
                break;
        }
        if (classID == A_FLAG) {
            SharedPreferencesUtil.insert("交通灯A", String.valueOf(lightID));
        } else {
            SharedPreferencesUtil.insert("交通灯B", String.valueOf(lightID));
        }
        CommunicationUtil.sendData("", REPLY_FLAG, null);
    }


    /**
     * 二维码识别结果存储
     *
     * @param results 识别结果
     * @param classID 标志物类型
     */
    public static void storeAsQRCode(String[] results, byte classID) {
        if (classID == A_FLAG) {
            for (String result : results) {
                SharedPreferencesUtil.insert("二维码A", chineseFilter(result));
                LogUtil.printLog("二维码A最终结果", chineseFilter(result));
            }
        } else {
            for (String result : results) {
                SharedPreferencesUtil.insert("二维码B", chineseFilter(result));
                LogUtil.printLog("二维码B最终结果", chineseFilter(result));
            }
        }
        CommunicationUtil.sendData("", REPLY_FLAG, null);
    }



    /**
     * 中文过滤器
     * 过滤得到中文
     *
     * @param text 需要过滤的文字
     * @return 返回中文
     */
    public static String chineseFilter(String text) {
        return text.replaceAll("[^\\u4e00-\\u9fa5]", "");
    }


    /**
     * 字母过滤器
     * 过滤得到字母A~Z，a~z，ASCII 码对应 65~90
     *
     * @param text 需要过滤的文字
     * @return 返回过滤完的文字
     */
    public static String letterFilter(String text) {
        return text.replaceAll("[^A-Za-z]", "");
    }


    /**
     * 数字过滤器
     * 过滤得到数字 0~9，ASCII 码对应 48~57
     *
     * @param text 传入的文字
     * @return 返回数字字符串
     */
    public static String numberFilter(String text) {
        return text.replaceAll("[^0-9]", "");
    }


    /**
     * 文字过滤器
     * 得到除特殊符号外的所有文字
     *
     * @param text 传入的文字
     * @return 返回文字字符串
     */
    public static String textFilter(String text) {
        return text.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]", "");
    }

    /**
     * 大写字母及数字过滤器
     * 得到大写字母及数字
     *
     * @param text 传入的文字
     * @return 返回大写字母及数字字符串
     */
    public static String capNumberLetterFilter(String text) {
        return text.replaceAll("[^A-Z0-9]", "");
    }


    /**
     * 大写字母及数字过滤器
     * 得到大写字母及数字
     *
     * @param text 传入的文字
     * @return 返回大写字母及数字字符串
     */
    public static String lowNumberLetterFilter(String text) {
        return text.replaceAll("[^a-z0-9]", "");
    }


    /**
     * 裁剪字符串中的花括号内容
     * @param text 传入的文字
     * @return 返回花括号中的内容
     */
    public static String substringFromBraces(String text) {
        int firstIndex, lastIndex;
        firstIndex = text.indexOf("{");
        lastIndex = text.lastIndexOf("}");
        return text.substring(firstIndex + 1, lastIndex);
    }


    /**
     * 合并两个 byte 数组
     * @param args1 合并时排序在前的数组
     * @param args2 合并时排序在后的数组
     * @return 返回合并完的数组
     */
    public static byte[] mergeTwoArrays(@NotNull byte[] args1,@NotNull byte[] args2) {
        byte[] result = new byte[args1.length + args2.length];
        System.arraycopy(args1, 0, result, 0, args1.length);
        System.arraycopy(args2, 0, result, args1.length, args2.length);
        return result;
    }

    public static int[] mergeTwoArrays(@NotNull int[] args1,@NotNull int[] args2) {
        int[] result = new int[args1.length + args2.length];
        System.arraycopy(args1, 0, result, 0, args1.length);
        System.arraycopy(args2, 0, result, args1.length, args2.length);
        return result;
    }


    /**
     * LZ78压缩算法
     * @param data ASCII 码类型的 byte 数组
     * @return 返回处理好的字符串
     */
    public static String LZ78(byte[] data) {
        //遍历和转换字符
        char[] results = new char[data.length];
        for (int i = 0; i < data.length; i++) {
            results[i] = (char) data[i];
        }
        //存放已分割字符串和索引
        HashMap<String, String> strIndexMap = new HashMap<>();
        //返回值
        StringBuilder resultStr = new StringBuilder();
        //存放临时数据
        StringBuilder tempStr = new StringBuilder();
        //存放最后清空时的索引，主要用于区分残缺数据
        int lastIndex = 0;
        //分割的字符串排序索引
        int indexCount = 1;
        for (int i = 0; i <= results.length; ) {
            //当字符串不重复时存储字符串和对应索引
            if (!strIndexMap.containsKey(tempStr.toString()) && tempStr.length() > 0) {
                //判断是否满足六位字节，满足停止
                if (resultStr.length() == 12) {
                    break;
                }
                //存储数据
                strIndexMap.put(tempStr.toString(), String.valueOf(indexCount));
                //增加索引
                indexCount++;
                //获得索引，找不到索引默认为0
                String index = strIndexMap.get(tempStr.substring(0, tempStr.length() - 1));
                if (index == null) {
                    index = "0";
                }
                //添加本轮字符串最后一个字符
                char lastChar = tempStr.charAt(tempStr.length() - 1);
                resultStr.append(index).append(lastChar);
                //清除内容
                tempStr = new StringBuilder();
                lastIndex = i;
            } else if (i != results.length) {//没遍历完前都只进行遍历
                tempStr.append(results[i]);
                i++;
            } else if (i != lastIndex) {//遍历完后清点数量，少了补0
                String index = strIndexMap.get(tempStr.toString());
                if (index == null) {
                    index = "0";
                }
                resultStr.append(index);
                resultStr.append("0".repeat(Math.max(0, 12 - resultStr.length())));
                break;
            } else {//齐全的话直接退出循环
                break;
            }
        }
        return resultStr.toString();
    }


    /**
     * RC4 算法
     */
    public static void RC4() {
        int[] S = new int[256];
        for (int i = 0; i < S.length; i++) {
            S[i] = i;
        }

    }

}
