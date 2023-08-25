package net.kuisec.r8c.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.opencv.core.Scalar;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 共享文件工具类
 *
 * @author Jinsn
 * @date 2022/10/31 18:14
 */
public class SharedPreferencesUtil {
    private static SharedPreferences reader;
    private static SharedPreferences.Editor writer;

    public final static String qrCodeTag = "二维码";
    public final static String chineseTag = "中文";
    public final static String trafficLightTag = "交通灯";
    public final static String trafficSignTag = "交通标志";
    public final static String LPTag = "车牌";
    public final static String LPColor = "车牌颜色";
    public final static String LPVehicleType = "车牌车型";
    public final static String LPRegex = "车牌格式";
    public final static String imgTh = "图像识别阈值";
    public final static String ocrTh = "文字识别阈值";
    public final static String LPATh = "车牌识别面积阈值";
    public final static String vehicleType = "车型";
    public final static String hex = "组装16进制";
    public final static String defaultTrafficSignTag = "默认交通标志";
    public final static String defaultVehicleType = "默认车型";
    public final static String tftAD = "tftA码盘距离";
    public final static String tftBD = "tftB码盘距离";
    public final static String tftCD = "tftC码盘距离";
    public final static String shapeClassHex = "多边形类型组合代码";
    public final static String shapeColorHex = "多边形颜色组合代码";
    public final static String personCount = "行人数量";
    public final static String personCountContent = "行人数量内容";
    public final static String ocrContent = "文字识别内容";
    public final static String lpContent = "车牌识别内容";
    public final static String hexContent = "组装16进制结果";
    public final static String personMask = "行人口罩";
    public final static String personOCC = "行人遮挡";
    public final static String mainCmd = "万能指令主指令";
    public final static String alarmCodeContent = "主车报警码";
    public final static String powerOpenCodeContent = "主车无线充电";
    public final static String rfidContent = "RFID数据内容";


    /**
     * 多边形枚举数组
     */
    public static final String[] COLOR_SHAPE_FLAG = {
            "红色三角形",
            "红色矩形",
            "红色菱形",
            "红色五角星形",
            "红色圆形",
            "红色梯形",

            "绿色三角形",
            "绿色矩形",
            "绿色菱形",
            "绿色五角星形",
            "绿色圆形",
            "绿色梯形",

            "蓝色三角形",
            "蓝色矩形",
            "蓝色菱形",
            "蓝色五角星形",
            "蓝色圆形",
            "蓝色梯形",

            "黄色三角形",
            "黄色矩形",
            "黄色菱形",
            "黄色五角星形",
            "黄色圆形",
            "黄色梯形",

            "天蓝色三角形",
            "天蓝色矩形",
            "天蓝色菱形",
            "天蓝色五角星形",
            "天蓝色圆形",
            "天蓝色梯形",

            "品红色三角形",
            "品红色矩形",
            "品红色菱形",
            "品红色五角星形",
            "品红色圆形",
            "品红色梯形",

            "黑色三角形",
            "黑色矩形",
            "黑色菱形",
            "黑色五角星形",
            "黑色圆形",
            "黑色梯形",

            "白色三角形",
            "白色矩形",
            "白色菱形",
            "白色五角星形",
            "白色圆形",
            "白色梯形"
    };

    /**
     * 多边形 HSV 颜色阈值键名
     */
    public static final String[] shapeHsvColorName = {
            //形状杂色背景最低阈值
            "darkSkyBlueLow",
            "darkYellowLow",
            "darkMagentaLow",
            "darkBlueLow",
            "darkGreenLow",
            "darkRedLow",
            "darkBlackLow",
            "darkWhiteLow",

            //形状杂色背景最高阈值
            "darkSkyBlueHigh",
            "darkYellowHigh",
            "darkMagentaHigh",
            "darkBlueHigh",
            "darkGreenHigh",
            "darkRedHigh",
            "darkBlackHigh",
            "darkWhiteHigh"
    };


    /**
     * 车牌 HSV 颜色阈值键名
     */
    public static final String[] lpHSVColorName = {
            //车牌颜色最低阈值
            "greenLow",
            "skyBlueLow",
            "yellowLow",
            "blueLow",
            //车牌颜色最高阈值
            "greenHigh",
            "skyBlueHigh",
            "yellowHigh",
            "blueHigh"
    };


    /**
     * 初始化共享
     */
    public static void init(Context context) {
        //创建非共享配置文件
        reader = context.getSharedPreferences("r8c", Context.MODE_PRIVATE);
        writer = reader.edit();
        //检查初始配置是否存在
        initKeys();
        //删除历史识别记录
        deleteShapeColorRecHistoryStorage();
        deleteTrafficSignRecHistoryStorage();
        deleteVehicleTypeHistoryStorage();
        deleteRFIDHistoryStorage();
        deleteHexTextHistoryStorage();
    }


    /**
     * 初始化键
     */
    public static void initKeys() {
        //初始化日志
        if ("0".equals(queryKey2Value("log-name"))) {
            insert("log-name", "log-android");
        }
        if ("0".equals(queryKey2Value("log-android"))) {
            insert("log-android", TimeUtil.getLifeTime() + "：首次创建安卓日志系统");
        }
        if ("0".equals(queryKey2Value("log-car"))) {
            insert("log-car", TimeUtil.getLifeTime() + "：首次创建主车日志系统");
        }
        //初始化图像识别阈值
        if ("0".equals(queryKey2Value(imgTh))) {
            insert(imgTh, "0.50");
        }
        //初始化文字识别阈值
        if ("0".equals(queryKey2Value(ocrTh))) {
            insert(ocrTh, "0.75");
        }
        //初始化车牌识别面积阈值
        if ("0".equals(queryKey2Value(LPATh))) {
            insert(LPATh, "0.35");
        }
        //初始化车牌颜色
        if ("0".equals(queryKey2Value(LPColor))) {
            insert(LPColor, "无");
        }
        //初始化车牌
        if ("0".equals(queryKey2Value(LPTag))) {
            insert(LPTag, "");
        }
        //初始化车牌车型
        if ("0".equals(queryKey2Value(LPVehicleType))) {
            insert(LPVehicleType, "无");
        }
        //初始化行人口罩
        if ("0".equals(queryKey2Value(personMask))) {
            insert(personMask, "无");
        }
        //初始化行人遮挡
        if ("0".equals(queryKey2Value(personOCC))) {
            insert(personOCC, "无");
        }
        //初始化行人遮挡
        if ("0".equals(queryKey2Value(mainCmd))) {
            insert(mainCmd, "无");
        }
        //初始化车型
        if ("0".equals(queryKey2Value(defaultVehicleType))) {
            insert(defaultVehicleType, "AI识别");
        }
        //初始化交通标志
        if ("0".equals(queryKey2Value(defaultTrafficSignTag))) {
            insert(defaultTrafficSignTag, "AI识别");
        }
        //初始化 TFT A 码盘信息
        if ("0".equals(queryKey2Value(tftAD))) {
            insert(tftAD, "350");
        }
        //初始化 TFT B 码盘信息
        if ("0".equals(queryKey2Value(tftBD))) {
            insert(tftBD, "420");
        }
        //初始化 TFT C 码盘信息
        if ("0".equals(queryKey2Value(tftCD))) {
            insert(tftCD, "210");
        }
        //初始化车牌格式
        if ("0".equals(queryKey2Value(LPRegex))) {
            insert(LPRegex, "");
        }
        //初始化多边形类型组合代码
        if ("0".equals(queryKey2Value(shapeClassHex))) {
            insert(shapeClassHex, "");
        }
        //初始化多边形颜色组合代码
        if ("0".equals(queryKey2Value(shapeColorHex))) {
            insert(shapeColorHex, "");
        }
        //初始化行人识别数量
        if ("0".equals(queryKey2Value(personCount))) {
            insert(personCount, "");
        }
        //初始化行人识别结果数量
        if ("0".equals(queryKey2Value(personCountContent))) {
            insert(personCountContent, "");
        }
        //初始化文字识别结果
        if ("0".equals(queryKey2Value(ocrContent))) {
            insert(ocrContent, "");
        }
        //初始化车牌识别结果
        if ("0".equals(queryKey2Value(lpContent))) {
            insert(lpContent, "");
        }
        //初始化16进制结果
        if ("0".equals(queryKey2Value(hexContent))) {
            insert(hexContent, "");
        }
        //初始化主车报警码结果
        if ("0".equals(queryKey2Value(alarmCodeContent))) {
            insert(alarmCodeContent, "");
        }
        //初始化主车无线充电开启码结果
        if ("0".equals(queryKey2Value(powerOpenCodeContent))) {
            insert(powerOpenCodeContent, "");
        }
        //检查 Shape HSV 颜色阈值
        initShapeHSVColorTh();
        //检查 LP HSV 颜色阈值
        initLPHSVColorTh();
    }


    /**
     * 初始化 HSV 颜色阈值
     */
    public static void initShapeHSVColorTh() {
        //默认颜色阈值，“!”代表换行
        Map<String, String> hsvColorMap = new HashMap<>();
        //青色
        hsvColorMap.put("darkSkyBlueLow",
                "//天蓝色 low!" +
                        "21, 135, 150!" +
                        "21, 135, 210!");
        hsvColorMap.put("darkSkyBlueHigh",
                "//天蓝色 high!" +
                        "40, 255, 255!");
        //黄色
        hsvColorMap.put("darkYellowLow",
                "//黄色 low!" +
                        "80, 135, 135!");
        hsvColorMap.put("darkYellowHigh",
                "//黄色 high!" +
                        "110, 255, 255!");
        //品红色
        hsvColorMap.put("darkMagentaLow",
                "//品红色 low!" +
                        "140, 80, 115!");
        hsvColorMap.put("darkMagentaHigh",
                "//品红色 high!" +
                        "180, 255, 255!");
        //蓝色
        hsvColorMap.put("darkBlueLow",
                "//蓝色 low!" +
                        "0, 160, 200!" +
                        "0, 160, 190!");
        hsvColorMap.put("darkBlueHigh",
                "//蓝色 high!" +
                        "18, 255, 255!");
        //绿色
        hsvColorMap.put("darkGreenLow",
                "//绿色 low!" +
                        "50, 70, 110!");
        hsvColorMap.put("darkGreenHigh",
                "//绿色 high!" +
                        "75, 255, 255!");
        //红色
        hsvColorMap.put("darkRedLow",
                "//红色 low!" +
                        "105, 160, 70!");
        hsvColorMap.put("darkRedHigh",
                "//红色 high!" +
                        "145, 255, 220!");
        //黑色
        hsvColorMap.put("darkBlackLow",
                "//黑色 low!" +
                        "105, 160, 70!");
        hsvColorMap.put("darkBlackHigh",
                "//黑色 high!" +
                        "145, 255, 220!");
        //白色
        hsvColorMap.put("darkWhiteLow",
                "//白色 low!" +
                        "13, 0, 230");
        hsvColorMap.put("darkWhiteHigh",
                "//白色 high!" +
                        "140, 90, 255");
        //检查 HSV 颜色阈值键是否不存在或为空
        for (String colorName : shapeHsvColorName) {
            if ("0".equals(queryKey2Value(colorName)) || queryKey2Value(colorName).isEmpty()) {
                //增加默认值
                insert(colorName, hsvColorMap.get(colorName));
            }
        }

        //HSV LOW 阈值
        List<Scalar[]> lowTh = new ArrayList<>();
        //HSV HIGH 阈值
        List<Scalar> highTh = new ArrayList<>();
        //遍历所有 HSV 颜色阈值属性
        for (int i = 0; i < shapeHsvColorName.length; i++) {
            //单个阈值属性所有阈值转换存放列表
            List<Scalar> ths = new ArrayList<>();
            //得到阈值
            String hsvContent = queryKey2Value(shapeHsvColorName[i]);
            //遍历单个颜色阈值属性的所有阈值行
            String[] lines = hsvContent.split("!");
            for (String line : lines) {
                //判断是否含注释，包含注释跳出该行
                if (!line.trim().contains("//") && !line.trim().isEmpty()) {
                    String[] num = line.split(",");
                    int h = Integer.parseInt(num[0].trim());
                    int s = Integer.parseInt(num[1].trim());
                    int v = Integer.parseInt(num[2].trim());
                    ths.add(new Scalar(h, s, v));
                }
            }
            if (i < 8) {
                lowTh.add(ths.toArray(new Scalar[]{}));
            } else {
                highTh.add(ths.get(0));
            }
        }
        ImgPcsUtil.dark_hsv_low = lowTh.toArray(new Scalar[][]{});
        ImgPcsUtil.dark_hsv_high = highTh.toArray(new Scalar[]{});
    }


    /**
     * 存储车牌阈值
     */
    public static void initLPHSVColorTh() {
        //默认颜色阈值，“!”代表换行
        Map<String, String> hsvColorMap = new HashMap<>();
        //绿色
        hsvColorMap.put("greenLow",
                "//绿色 low!" +
                        "30, 50, 65!");
        hsvColorMap.put("greenHigh",
                "//绿色 high!" +
                        "80, 255, 255!");
        //青色
        hsvColorMap.put("skyBlueLow",
                "//浅蓝色 low!" +
                        "15, 75, 100!");
        hsvColorMap.put("skyBlueHigh",
                "//浅蓝色 high!" +
                        "40, 255, 255!");
        hsvColorMap.put("yellowLow",
                "//黄色 low!" +
                        "70, 115, 90!");
        hsvColorMap.put("yellowHigh",
                "//黄色 high!" +
                        "130, 255, 255!");
        hsvColorMap.put("blueLow",
                "//深蓝色 low!" +
                        "0, 170, 120");
        hsvColorMap.put("blueHigh",
                "//深蓝色 high!" +
                        "20, 255, 220");
        //检查 HSV 颜色阈值键是否不存在或为空
        for (String colorName : lpHSVColorName) {
            if ("0".equals(queryKey2Value(colorName)) || queryKey2Value(colorName).isEmpty()) {
                //增加默认值
                insert(colorName, hsvColorMap.get(colorName));
            }
        }

        //HSV LOW 阈值
        List<Scalar[]> lowTh = new ArrayList<>();
        //HSV HIGH 阈值
        List<Scalar> highTh = new ArrayList<>();
        //遍历所有 HSV 颜色阈值属性
        for (int i = 0; i < lpHSVColorName.length; i++) {
            //单个阈值属性所有阈值转换存放列表
            List<Scalar> ths = new ArrayList<>();
            //得到阈值
            String hsvContent = queryKey2Value(lpHSVColorName[i]);
            //遍历单个颜色阈值属性的所有阈值行
            String[] lines = hsvContent.split("!");
            for (String line : lines) {
                //判断是否含注释，包含注释跳出该行
                if (!line.trim().contains("//") && !line.trim().isEmpty()) {
                    String[] num = line.split(",");
                    int h = Integer.parseInt(num[0].trim());
                    int s = Integer.parseInt(num[1].trim());
                    int v = Integer.parseInt(num[2].trim());
                    ths.add(new Scalar(h, s, v));
                }
            }
            if (i < 4) {
                lowTh.add(ths.toArray(new Scalar[]{}));
            } else {
                highTh.add(ths.get(0));
            }
        }
        ImgPcsUtil.lp_hsv_low = lowTh.toArray(new Scalar[][]{});
        ImgPcsUtil.lp_hsv_high = highTh.toArray(new Scalar[]{});
    }


    /**
     * 解析 HSV 阈值存储
     *
     * @param hsvModel hsv 阈值模式
     */
    public static String[] parseHSV(String hsvModel) {
        StringBuilder lowHSVBuilder = new StringBuilder();
        StringBuilder highHSVBuilder = new StringBuilder();
        switch (hsvModel) {
            case "dark":
                for (int i = 0; i < shapeHsvColorName.length; i++) {
                    if (i < 8) {
                        lowHSVBuilder.append(SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.shapeHsvColorName[i]));
                    } else {
                        highHSVBuilder.append(SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.shapeHsvColorName[i]));
                    }
                }
                break;
            case "lp":
                for (int i = 0; i < lpHSVColorName.length; i++) {
                    if (i < 4) {
                        lowHSVBuilder.append(SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.lpHSVColorName[i]));
                    } else {
                        highHSVBuilder.append(SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.lpHSVColorName[i]));
                    }
                }
                break;
        }
        return new String[]{lowHSVBuilder.toString().replace("!", "\n"), highHSVBuilder.toString().replace("!", "\n")};
    }


    /**
     * 存储 Shape HSV 阈值
     *
     * @param textContent HSV 格式化文本内容
     */
    public static void saveShapeHSV(String ThName, String textContent) {
        String[] lines = textContent.split("!//");
        for (int i = 0; i < lines.length; i++) {
            String colorName = "";
            String hsvContent = "";
            switch (i) {
                case 0:
                    colorName = "SkyBlue";
                    hsvContent = lines[i] + "!";
                    break;
                case 1:
                    colorName = "Yellow";
                    hsvContent = "//" + lines[i] + "!";
                    break;
                case 2:
                    colorName = "Magenta";
                    hsvContent = "//" + lines[i] + "!";
                    break;
                case 3:
                    colorName = "Blue";
                    hsvContent = "//" + lines[i] + "!";
                    break;
                case 4:
                    colorName = "Green";
                    hsvContent = "//" + lines[i] + "!";
                    break;
                case 5:
                    colorName = "Red";
                    hsvContent = "//" + lines[i] + "!";
                    break;
                case 6:
                    colorName = "Black";
                    hsvContent = "//" + lines[i] + "!";
                    break;
                case 7:
                    colorName = "White";
                    hsvContent = "//" + lines[i];
                    break;
            }
            SharedPreferencesUtil.insert("dark" + colorName + ThName, hsvContent);
        }
    }


    /**
     * 保存车牌阈值
     *
     * @param ThName      阈值类型
     * @param textContent 文本内容
     */
    public static void saveLPHSV(String ThName, String textContent) {
        String[] lines = textContent.split("!//");
        for (int i = 0; i < lines.length; i++) {
            String colorName = "";
            String hsvContent = "";
            switch (i) {
                case 0:
                    colorName = "green";
                    hsvContent = lines[i] + "!";
                    break;
                case 1:
                    colorName = "skyBlue";
                    hsvContent = "//" + lines[i] + "!";
                    break;
                case 2:
                    colorName = "yellow";
                    hsvContent = "//" + lines[i] + "!";
                    break;
                case 3:
                    colorName = "blue";
                    hsvContent = "//" + lines[i];
                    break;
            }
            SharedPreferencesUtil.insert(colorName + ThName, hsvContent);
        }
    }


    /**
     * 删除历史多边形识别数据
     */
    public static void deleteShapeColorRecHistoryStorage() {
        for (String colorShape : COLOR_SHAPE_FLAG) {
            delete(colorShape);
        }
    }


    /**
     * 删除历史交通标志识别数据
     */
    public static void deleteTrafficSignRecHistoryStorage() {
        delete(trafficSignTag);
    }


    /**
     * 删除历史二维码识别数据
     */
    public static void deleteQRCodeHistoryStorage(byte classID) {
        delete(qrCodeTag + classID);
    }


    /**
     * 删除历史交通灯识别数据
     */
    public static void deleteTrafficLightHistoryStorage(byte classID) {
        delete(trafficLightTag + classID);
    }


    /**
     * 删除历史车牌识别数据
     */
    public static void deleteLPHistoryStorage(byte classID) {
        delete("车牌" + classID);
    }


    /**
     * 删除历史中文识别数据
     */
    public static void deleteChineseHistoryStorage(byte classID) {
        delete(chineseTag + classID);
    }


    /**
     * 删除历史车型识别数据
     */
    public static void deleteVehicleTypeHistoryStorage() {
        delete("车型");
    }

    /**
     * 删除历史行人数量
     */
    public static void deletePersonCount() {delete(personCount);}


    /**
     * 删除历史组装 16 进制数据
     */
    public static void deleteHexTextHistoryStorage() {
        delete(hex);
    }


    /**
     * 删除历史 RFID 修复数据
     */
    public static void deleteRFIDHistoryStorage() {
        delete("破损车牌");
        delete("坐标");
        delete("报警码");
    }


    /**
     * 添加数据
     *
     * @param key   值名称
     * @param value 值
     */
    public static void insert(String key, String value) {
        writer.putString(key, value);
        boolean success = writer.commit();
        if (success) {
            LogUtil.printSystemLog("存储", key + "存储成功");
        } else {
            LogUtil.printSystemLog("存储", key + "存储失败");
        }
    }


    /**
     * 删除数据
     *
     * @param valueName 值名称
     */
    private static void delete(String valueName) {
        writer.remove(valueName);
        writer.apply();
    }


    /**
     * 追加数据
     *
     * @param valueName 值名称
     * @param value     更新的值
     */
    public static void append(String valueName, String value) {
        String data = reader.getString(valueName, "") + value;
        writer.putString(valueName, data);
        writer.apply();
    }


    /**
     * 根据Key查询Value
     *
     * @param key 键名称
     * @return 返回查询到的值
     */
    public static String queryKey2Value(String key) {
        return reader.getString(key, "0");
    }


    /**
     * 文件内容转数组
     *
     * @return 返回组装好的数组
     */
    public static List<String> file2List(String key) {
        List<String> logList = new ArrayList<>();
        ByteArrayInputStream strStream = new ByteArrayInputStream(queryKey2Value(key).getBytes(StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(new InputStreamReader(strStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                logList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logList;
    }
}
