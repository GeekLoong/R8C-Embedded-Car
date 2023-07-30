package net.kuisec.r8c.Utils;

import static net.kuisec.r8c.Const.SignConst.TFT_COLOR_BLACK;
import static net.kuisec.r8c.Const.SignConst.TFT_COLOR_BLUE;
import static net.kuisec.r8c.Const.SignConst.TFT_COLOR_GREEN;
import static net.kuisec.r8c.Const.SignConst.TFT_COLOR_MAGENTA;
import static net.kuisec.r8c.Const.SignConst.TFT_COLOR_RED;
import static net.kuisec.r8c.Const.SignConst.TFT_COLOR_SKY_BLUE;
import static net.kuisec.r8c.Const.SignConst.TFT_COLOR_WHITE;
import static net.kuisec.r8c.Const.SignConst.TFT_COLOR_YELLOW;
import static net.kuisec.r8c.Const.SignConst.TFT_SHAPE_CIR;
import static net.kuisec.r8c.Const.SignConst.TFT_SHAPE_DIA;
import static net.kuisec.r8c.Const.SignConst.TFT_SHAPE_FIV;
import static net.kuisec.r8c.Const.SignConst.TFT_SHAPE_REC;
import static net.kuisec.r8c.Const.SignConst.TFT_SHAPE_TRA;
import static net.kuisec.r8c.Const.SignConst.TFT_SHAPE_TRI;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     *
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
     * 获得出现次数最多的形状的数量
     *
     * @return 返回形状的数量
     */
    public static int getMaxCountShapeFromStorage() {
        int[] shapes = {
                getShapeFromStorage(TFT_SHAPE_REC),
                getShapeFromStorage(TFT_SHAPE_CIR),
                getShapeFromStorage(TFT_SHAPE_TRI),
                getShapeFromStorage(TFT_SHAPE_DIA),
                getShapeFromStorage(TFT_SHAPE_FIV),
                getShapeFromStorage(TFT_SHAPE_TRA)
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
     *
     * @return 返回形状的数量
     */
    public static int getMaxCountColorFromStorage() {
        int[] colors = {
                getColorFromStorage(TFT_COLOR_RED),
                getColorFromStorage(TFT_COLOR_GREEN),
                getColorFromStorage(TFT_COLOR_YELLOW),
                getColorFromStorage(TFT_COLOR_BLUE),
                getColorFromStorage(TFT_COLOR_SKY_BLUE),
                getColorFromStorage(TFT_COLOR_MAGENTA),
                getColorFromStorage(TFT_COLOR_BLACK),
                getColorFromStorage(TFT_COLOR_WHITE)
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
     * ID 对应颜色
     */
    private final static List<String> colorNameList = new ArrayList<>() {{
        add("红色");
        add("绿色");
        add("蓝色");
        add("黄色");
        add("品红色");
        add("天蓝色");
        add("黑色");
        add("白色");
    }};


    /**
     * ID 对应形状
     */
    private final static List<String> shapeNameList = new ArrayList<>() {{
        add("矩形");
        add("圆形");
        add("三角形");
        add("菱形");
        add("五角星形");
        add("梯形");
    }};


    /**
     * 从存储中获得指定颜色的数量并回传给竞赛平台
     *
     * @param colorID 颜色代号
     */
    public static int getColorFromStorage(byte colorID) {
        String colorName = colorNameList.get(colorID - 1);
        int colorSum = 0;
        if (!colorName.isEmpty()) {
            for (String shape : shapeNameList) {
                colorSum += Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorName + shape));
            }
        }
        return colorSum;
    }


    /**
     * 从存储中获得指定形状的数量并回传给竞赛平台
     *
     * @param shapeID 形状代号
     */
    public static int getShapeFromStorage(byte shapeID) {
        String shapeName = shapeNameList.get(shapeID - 1);
        int shapeSum = 0;
        if (!shapeName.isEmpty()) {
            for (String color : colorNameList) {
                shapeSum += Integer.parseInt(SharedPreferencesUtil.queryKey2Value(color + shapeName));
            }
        }
        return shapeSum;
    }


    /**
     * 获得数量最多的形状类型ID
     *
     * @return 返回形状类型ID
     */
    public static byte getMaxShapeClassFromStorage() {
        int[] shapes = {
                getShapeFromStorage(TFT_SHAPE_REC),
                getShapeFromStorage(TFT_SHAPE_CIR),
                getShapeFromStorage(TFT_SHAPE_TRI),
                getShapeFromStorage(TFT_SHAPE_DIA),
                getShapeFromStorage(TFT_SHAPE_FIV),
                getShapeFromStorage(TFT_SHAPE_TRA)
        };
        int temp = 0;
        byte maxShapeID = TFT_SHAPE_REC;
        for (int i = 0; i < shapes.length; i++) {
            if (shapes[i] >= temp) {
                temp = shapes[i];
                maxShapeID = (byte) (i + 1);
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
                getColorFromStorage(TFT_COLOR_GREEN),
                getColorFromStorage(TFT_COLOR_BLUE),
                getColorFromStorage(TFT_COLOR_YELLOW),
                getColorFromStorage(TFT_COLOR_MAGENTA),
                getColorFromStorage(TFT_COLOR_SKY_BLUE),
                getColorFromStorage(TFT_COLOR_BLACK),
                getColorFromStorage(TFT_COLOR_WHITE)
        };
        int temp = 0;
        byte maxColorID = TFT_COLOR_RED;
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] >= temp) {
                temp = colors[i];
                maxColorID = (byte) (i + 1);
            }
        }
        return maxColorID;
    }


    /**
     * 获得颜色类型最多的形状类型ID
     *
     * @return 返回形状类型ID
     */
    public static byte getMaxColorShapeClassFromStorage() {
        int[] shapes = {
                0,
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
                int index = shapeNameList.indexOf(colorShape.substring(colorIndex + 1));
                shapes[index]++;
            }
        }
        int temp = 0;
        byte maxShapeID = TFT_SHAPE_REC;
        for (int i = 0; i < shapeNameList.size(); i++) {
            if (shapes[i] >= temp) {
                temp = shapes[i];
                maxShapeID = (byte) (i + 1);
            }
        }
        return maxShapeID;
    }


    /**
     * 从存储中获得形状总数
     */
    public static int getAllShapeFromStorage() {
        int allShapeSum = 0;
        for (String shapeName : SharedPreferencesUtil.COLOR_SHAPE_FLAG) {
            allShapeSum += Integer.parseInt(SharedPreferencesUtil.queryKey2Value(shapeName));
        }
        return allShapeSum;
    }


    /**
     * 从存储中获得指定颜色和指定形状的数量并回传给竞赛平台
     *
     * @param colorID 颜色代号
     * @param shapeID 形状代号
     */
    public static int getColorShapeFromStorage(byte colorID, byte shapeID) {
        String colorName = colorNameList.get(colorID - 1);
        String shapeName = shapeNameList.get(shapeID - 1);
        String colorShape = colorName + shapeName;
        return Integer.parseInt(SharedPreferencesUtil.queryKey2Value(colorShape));
    }


    /**
     * 交通灯识别与保存
     *
     * @param lightName 交通灯识别结果
     * @param classID   交通灯类型
     */
    public static void saveTrafficLight(String lightName, byte classID) {
        SharedPreferencesUtil.insert(SharedPreferencesUtil.trafficLightTag + classID, lightName);
        CommunicationUtil.replyCar();
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
     * 匹配车牌（指定格式）
     * 示例格式：XXYYXY
     *
     * @param text 车牌内容
     * @return 返回指定车牌格式
     */
    public static boolean matchLP(String text) {
        String regex = "^" + SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.LPRegex) + "$";
        regex = regex.replaceAll("X", "[A-Z]");
        regex = regex.replaceAll("Y", "\\\\d");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }


    /**
     * 裁剪字符串中的花括号内容
     *
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
     * 查找字符串中最长公共子串
     * @param strs 字符串集
     * @return 返回相同字符串
     */
    public static String findLongestCommonSubstring(List<String> strs) {
        if (strs.size() == 0) {
            return "";
        }
        String minStr = strs.get(0);
        for (String str : strs) {
            if (str.length() < minStr.length()) {
                minStr = str;
            }
        }
        int length = minStr.length();
        for (int len = length; len > 0; len--) {
            for (int i = 0; i <= length - len; i++) {
                String subStr = minStr.substring(i, i + len);
                boolean allContains = true;
                for (String str : strs) {
                    if (!str.contains(subStr)) {
                        allContains = false;
                        break;
                    }
                }
                if (allContains) {
                    return subStr;
                }
            }
        }
        return "";
    }


    /**
     * 合并两个 byte 数组
     *
     * @param args1 合并时排序在前的数组
     * @param args2 合并时排序在后的数组
     * @return 返回合并完的数组
     */
    public static byte[] mergeTwoArrays(@NotNull byte[] args1, @NotNull byte[] args2) {
        byte[] result = new byte[args1.length + args2.length];
        System.arraycopy(args1, 0, result, 0, args1.length);
        System.arraycopy(args2, 0, result, args1.length, args2.length);
        return result;
    }

    public static int[] mergeTwoArrays(@NotNull int[] args1, @NotNull int[] args2) {
        int[] result = new int[args1.length + args2.length];
        System.arraycopy(args1, 0, result, 0, args1.length);
        System.arraycopy(args2, 0, result, args1.length, args2.length);
        return result;
    }


    /**
     * LZ78压缩算法
     *
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
     * 斐文那契 LFSR 线性反馈移位寄存器算法
     *
     * @param results 原二维码识别结果
     * @return 返回解密内容
     */
    public static String LFSR(String[] results) {
        //移位寄存器
        StringBuilder sReg = new StringBuilder();
        //反馈函数
        String feedFun = "";
        //判断寄存器与反馈函数，A 代表移位寄存器，B 代表反馈函数
        for (String var : results) {
            if (var.contains("A")) {
                sReg = new StringBuilder(var);
            } else if (var.contains("B")) {
                feedFun = var;
            }
        }
        //过滤特殊字符
        sReg = new StringBuilder(DataPcsUtil.numberFilter(sReg.toString()));
        feedFun = DataPcsUtil.numberFilter(feedFun);
        LogUtil.printLog("test", sReg + "\n" + feedFun);
        //记录长度，计算缺失然后补0
        int sRegInitLength = sReg.length();
        //记录输出位（最低位）
        List<String> outputList = new ArrayList<>();
        //默认增加原数据输出位
        outputList.add(String.valueOf(sReg.charAt(sReg.length() - 1)));
        //抽头序列查找
        List<Integer> tapList = new ArrayList<>();
        for (int i = 0; i < feedFun.length(); i++) {
            //顺序查找
            int i1 = feedFun.indexOf("1");
            //逆序查找
            int i2 = feedFun.lastIndexOf("1");
            //替换已经查找的字符串
            if (i1 != i2) {
                tapList.add(i1);
                tapList.add(i2);
                feedFun = feedFun.substring(0, i1) + "0" + feedFun.substring(i1 + 1);
                feedFun = feedFun.substring(0, i2) + "0" + feedFun.substring(i2 + 1);
            } else if (i1 != -1) {
                tapList.add(i1);
                break;
            } else {
                break;
            }
        }
        //异或运算，大于两个异或位后再开始
        if (tapList.size() > 1) {
            for (int i = 0; i < (Math.pow(2, sRegInitLength) - 1); i++) {
                int bool = Integer.parseInt(String.valueOf(sReg.charAt(tapList.get(0))));
                for (int j = 0; j < tapList.size(); j++) {
                    if (j > 0) {
                        bool ^= Integer.parseInt(String.valueOf(sReg.charAt(tapList.get(j))));
                    }
                }
                //位移运算
                int sRegBinary = Integer.valueOf(sReg.toString(), 2) >> 1;
                sReg = new StringBuilder(Integer.toBinaryString(sRegBinary).trim());
                //补充缺失bit
                if (sReg.length() != sRegInitLength) {
                    int dif = sRegInitLength - sReg.length();
                    for (int k = 0; k < dif; k++) {
                        sReg.insert(0, "0");
                    }
                }
                sReg = new StringBuilder(sReg.toString().replaceFirst("0", String.valueOf(bool)));
                outputList.add(String.valueOf(sReg.charAt(sReg.length() - 1)));
                LogUtil.printLog("test", sReg + "\n");
            }
        }
        String bit = DataPcsUtil.numberFilter(outputList.toString());
        LogUtil.printLog("test", bit);
        List<String> hexList = new ArrayList<>();
        for (int i = 0; i < 48; i += 8) {
            String substring = bit.substring(i, i + 8);
            hexList.add(Integer.toHexString(Integer.parseInt(substring, 2)));
        }
        StringBuilder builder = new StringBuilder();
        for (String hex : hexList) {
            builder.append(hex).append(" ");
        }
        LogUtil.printLog("LFSR：", builder.toString());
        return builder.toString();
    }

}
