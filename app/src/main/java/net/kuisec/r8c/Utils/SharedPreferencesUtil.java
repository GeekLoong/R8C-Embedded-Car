package net.kuisec.r8c.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import org.opencv.core.Scalar;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    @SuppressLint("SdCardPath")
    private static final String PATH = "/data/data/net.kuisec.r8c/shared_prefs/r8c.xml";
    private static SharedPreferences reader;
    private static SharedPreferences.Editor writer;

    /**
     * 多边形枚举数组
     */
    public static final String[] COLOR_SHAPE_FLAG = {
            "红色三角形",
            "红色矩形",
            "红色菱形",
            "红色五角星形",
            "红色圆形",

            "绿色三角形",
            "绿色矩形",
            "绿色菱形",
            "绿色五角星形",
            "绿色圆形",

            "蓝色三角形",
            "蓝色矩形",
            "蓝色菱形",
            "蓝色五角星形",
            "蓝色圆形",

            "黄色三角形",
            "黄色矩形",
            "黄色菱形",
            "黄色五角星形",
            "黄色圆形",

            "天蓝色三角形",
            "天蓝色矩形",
            "天蓝色菱形",
            "天蓝色五角星形",
            "天蓝色圆形",

            "品红色三角形",
            "品红色矩形",
            "品红色菱形",
            "品红色五角星形",
            "品红色圆形",

            "黑色三角形",
            "黑色矩形",
            "黑色菱形",
            "黑色五角星形",
            "黑色圆形"
    };

    /**
     * 多边形 HSV 颜色阈值键名
     */
    public static final String[] hsvColorName = {
            //浅色最低阈值
            "lightSkyBlueLow",
            "lightYellowLow",
            "lightMagentaLow",
            "lightBlueLow",
            "lightGreenLow",
            "lightRedLow",

            //深色最低阈值
            "darkSkyBlueLow",
            "darkYellowLow",
            "darkMagentaLow",
            "darkBlueLow",
            "darkGreenLow",
            "darkRedLow",

            //浅色最高阈值
            "lightSkyBlueHigh",
            "lightYellowHigh",
            "lightMagentaHigh",
            "lightBlueHigh",
            "lightGreenHigh",
            "lightRedHigh",

            //深色最低阈值
            "darkSkyBlueHigh",
            "darkYellowHigh",
            "darkMagentaHigh",
            "darkBlueHigh",
            "darkGreenHigh",
            "darkRedHigh"
    };


    /**
     * 车牌 HSV 颜色阈值键名
     */
    public static final String[] lpHSVColorName = {
            //最低阈值
            "greenLow",
            "skyBlueLow",
            "yellowLow",
            "blueLow",
            //最高阈值
            "greenHigh",
            "skyBlueHigh",
            "yellowHigh",
            "blueHigh"
    };


    /**
     * 初始化共享
     */
    public static void init(Context context) {
        //创建共享文件
        reader = context.getSharedPreferences("r8c", Context.MODE_PRIVATE);
        writer = reader.edit();
        //检查初始配置是否存在
        initKeys();
        //检查 Shape HSV 颜色阈值
        initShapeHSVColorTh();
        //检查 LP HSV 颜色阈值
        initLPHSVColorTh();
        //删除历史识别记录
        deleteQRCodeHistoryStorage();
        deleteChineseHistoryStorage();
        deleteLPHistoryStorage();
        deleteTrafficLightHistoryStorage();
        deleteShapeColorHistoryStorage();
        deleteCarModelHistoryStorage();
        deleteRFIDHistoryStorage();
        deleteHexTextHistoryStorage();
    }


    /**
     * 初始化键
     */
    public static void initKeys() {
        //初始化日志
        if ("0".equals(queryKey2Value("log"))) {
            insert("log", TimeUtil.getLifeTime() + "：首次创建日志系统");
        }
        //初始化图像识别阈值
        if ("0".equals(queryKey2Value("imgTh"))) {
            insert("imgTh", "0.50");
        }
        //初始化文字识别阈值
        if ("0".equals(queryKey2Value("ocrTh"))) {
            insert("ocrTh", "0.75");
        }
        //初始化车牌识别面积阈值
        if ("0".equals(queryKey2Value("lpATh"))) {
            insert("lpATh", "0.35");
        }
        //初始化车牌颜色
        if ("0".equals(queryKey2Value("lpColor"))) {
            insert("lpColor", "绿色");
        }
        //初始化车型
        if ("0".equals(queryKey2Value("carModel"))) {
            insert("carModel", "轿车");
        }
        //初始化交通标志
        if ("0".equals(queryKey2Value("trafficFlag"))) {
            insert("trafficFlag", "左转");
        }
        //初始化 TFT A 码盘信息
        if ("0".equals(queryKey2Value("tftAD"))) {
            insert("tftAD", "350");
        }
        //初始化 TFT B 码盘信息
        if ("0".equals(queryKey2Value("tftBD"))) {
            insert("tftBD", "420");
        }
    }


    /**
     * 初始化 HSV 颜色阈值
     */
    public static void initShapeHSVColorTh() {
        //默认颜色阈值，“!”代表换行
        Map<String, String> hsvColorMap = new HashMap<>();
        //青色
        hsvColorMap.put("lightSkyBlueLow",
                "//天蓝色 light_low!" +
                        "27, 100, 235!" +
                        "20, 100, 150!");
        hsvColorMap.put("lightSkyBlueHigh",
                "//天蓝色 light_high!" +
                        "48, 200, 255!");
        hsvColorMap.put("darkSkyBlueLow",
                "//天蓝色 dark_low!" +
                        "21, 135, 150!" +
                        "21, 135, 210!");
        hsvColorMap.put("darkSkyBlueHigh",
                "//天蓝色 dark_high!" +
                        "40, 255, 255!");
        //黄色
        hsvColorMap.put("lightYellowLow",
                "//黄色 light_low!" +
                        "75, 110, 245!" +
                        "75, 60, 115!" +
                        "75, 60, 145!");
        hsvColorMap.put("lightYellowHigh",
                "//黄色 light_high!" +
                        "110, 255, 255!");
        hsvColorMap.put("darkYellowLow",
                "//黄色 dark_low!" +
                        "80, 135, 135!");
        hsvColorMap.put("darkYellowHigh",
                "//黄色 dark_high!" +
                        "110, 255, 255!");
        //品红色
        hsvColorMap.put("lightMagentaLow",
                "//品红色 light_low!" +
                        "90, 80, 235!" +
                        "101, 135, 140!" +
                        "101, 135, 150!");
        hsvColorMap.put("lightMagentaHigh",
                "//品红色 light_high!" +
                        "180, 255, 255!");
        hsvColorMap.put("darkMagentaLow",
                "//品红色 dark_low!" +
                        "140, 80, 115!");
        hsvColorMap.put("darkMagentaHigh",
                "//品红色 dark_high!" +
                        "180, 255, 255!");
        //蓝色
        hsvColorMap.put("lightBlueLow",
                "//蓝色 light_low!" +
                        "10, 220, 190!" +
                        "0, 115, 120!");
        hsvColorMap.put("lightBlueHigh",
                "//蓝色 light_high!" +
                        "20, 255, 190!");
        hsvColorMap.put("darkBlueLow",
                "//蓝色 dark_low!" +
                        "0, 160, 200!" +
                        "0, 160, 190");
        hsvColorMap.put("darkBlueHigh",
                "//蓝色 dark_high!" +
                        "18, 255, 255!");
        //绿色
        hsvColorMap.put("lightGreenLow",
                "//绿色 light_low!" +
                        "47, 120, 153!" +
                        "35, 70, 60!");
        hsvColorMap.put("lightGreenHigh",
                "//绿色 light_high!" +
                        "76, 255, 255!");
        hsvColorMap.put("darkGreenLow",
                "//绿色 dark_low!" +
                        "50, 70, 110!");
        hsvColorMap.put("darkGreenHigh",
                "//绿色 dark_high!" +
                        "75, 255, 255!");
        //红色
        hsvColorMap.put("lightRedLow",
                "//红色 light_low!" +
                        "105, 135, 150!" +
                        "105, 95, 60");
        hsvColorMap.put("lightRedHigh",
                "//红色 light_high!" +
                        "153, 255, 150");
        hsvColorMap.put("darkRedLow",
                "//红色 dark_low!" +
                        "105, 160, 70");
        hsvColorMap.put("darkRedHigh",
                "//红色 dark_high!" +
                        "145, 255, 220");
        //检查 HSV 颜色阈值模式是否不存在
        if ("0".equals(queryKey2Value("HSVModel"))) {
            //添加默认值
            insert("HSVModel", "dark");
        }
        //检查 HSV 颜色阈值键是否不存在或为空
        for (String colorName : hsvColorName) {
            if ("0".equals(queryKey2Value(colorName)) || queryKey2Value(colorName).isEmpty()) {
                //增加默认值
                insert(colorName, hsvColorMap.get(colorName));
            }
        }
        //将 ImgPcsUtil 中的 HSV 阈值调至最新
        //HSV LOW 阈值
        List<Scalar[]> lowTh = new ArrayList<>();
        //遍历所有 HSV LOW 颜色阈值属性
        for (int i = 0; i < 12; i++) {
            //单个阈值属性所有阈值转换存放列表
            List<Scalar> ths = new ArrayList<>();
            //得到阈值
            String hsvContent = queryKey2Value(hsvColorName[i]);
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
            lowTh.add(ths.toArray(new Scalar[]{}));
            //根据长度判断是否赋值给计算内容
            switch (i) {
                case 5:
                    ImgPcsUtil.light_hsv_low = lowTh.toArray(new Scalar[][]{});
                    lowTh = new ArrayList<>();
                    break;
                case 11:
                    ImgPcsUtil.dark_hsv_low = lowTh.toArray(new Scalar[][]{});
                    break;
            }
        }
        //HSV HIGH 阈值
        List<Scalar> highTh = new ArrayList<>();
        for (int i = 12; i < hsvColorName.length; i++) {
            //得到阈值
            String hsvContent = queryKey2Value(hsvColorName[i]);
            //遍历单个颜色阈值属性的所有阈值行
            String[] lines = hsvContent.split("!");
            for (String line : lines) {
                //判断是否含注释，包含注释跳出该行
                if (!line.trim().contains("//") && !line.trim().isEmpty()) {
                    //解析单个颜色阈值属性的阈值
                    String[] num = line.split(",");
                    int h = Integer.parseInt(num[0].trim());
                    int s = Integer.parseInt(num[1].trim());
                    int v = Integer.parseInt(num[2].trim());
                    highTh.add(new Scalar(h, s, v));
                }
            }
            //根据长度判断是否赋值给计算内容
            switch (i) {
                case 17:
                    ImgPcsUtil.light_hsv_high = highTh.toArray(new Scalar[]{});
                    highTh = new ArrayList<>();
                    break;
                case 23:
                    ImgPcsUtil.dark_hsv_high = highTh.toArray(new Scalar[]{});
                    break;
            }
        }
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
                        "30, 90, 40!");
        hsvColorMap.put("greenHigh",
                "//绿色 high!" +
                        "70, 255, 255!");
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
            case "light":
                for (int i = 0; i < 18; i++) {
                    if (i <= 5) {
                        lowHSVBuilder.append(SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.hsvColorName[i]));
                    } else if (i >= 12) {
                        highHSVBuilder.append(SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.hsvColorName[i]));
                    }
                }
                break;
            case "dark":
                for (int i = 0; i < 24; i++) {
                    if (i > 5 && i < 12) {
                        lowHSVBuilder.append(SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.hsvColorName[i]));
                    } else if (i > 17) {
                        highHSVBuilder.append(SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.hsvColorName[i]));
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
        String model = SharedPreferencesUtil.queryKey2Value("HSVModel");
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
                    hsvContent = "//" + lines[i];
                    break;
            }
            SharedPreferencesUtil.insert(model + colorName + ThName, hsvContent);
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
    public static void deleteShapeColorHistoryStorage() {
        for (String colorShape : COLOR_SHAPE_FLAG) {
            delete(colorShape);
        }
    }


    /**
     * 删除历史二维码识别数据
     */
    public static void deleteQRCodeHistoryStorage() {
        delete("二维码A");
        delete("二维码B");
    }


    /**
     * 删除历史交通灯识别数据
     */
    public static void deleteTrafficLightHistoryStorage() {
        delete("交通灯A");
        delete("交通灯B");
    }


    /**
     * 删除历史车牌识别数据
     */
    public static void deleteLPHistoryStorage() {
        delete("车牌A");
        delete("车牌B");
    }


    /**
     * 删除历史中文识别数据
     */
    public static void deleteChineseHistoryStorage() {
        delete("中文A");
        delete("中文B");
    }


    /**
     * 删除历史车型识别数据
     */
    public static void deleteCarModelHistoryStorage() {
        delete("车型");
    }


    /**
     * 删除历史组装 16 进制数据
     */
    public static void deleteHexTextHistoryStorage() {
        delete("组装16进制");
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
            LogUtil.print("存储", key + "存储成功");
        } else {
            LogUtil.print("存储", key + "存储失败");
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
     * 根据Value查询Key
     *
     * @param value 值内容
     * @return 返回查询到的键
     */
    public static String queryValue2Key(String value) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                reader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(PATH))));
            }
            String line;
            while (reader != null && (line = reader.readLine()) != null) {
                if (line.contains(value)) {
                    int first = line.indexOf("\"") + 1;
                    int second = line.indexOf("\"", first);
                    String key = line.substring(first, second);
                    builder.append(key).append("\n");
                }
            }
        } catch (Exception e) {
            builder.append("查找出错");
            e.printStackTrace();
        }
        return builder.toString();
    }


    /**
     * 根据Key查询Value行数
     *
     * @param key 键名
     * @return 返回行数
     */
    public static int queryKey2Line(String key) {
        int lineNumber = 0;
        ByteArrayInputStream strStream = new ByteArrayInputStream(queryKey2Value("log").getBytes(StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(new InputStreamReader(strStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                lineNumber += 1;
            }
        } catch (IOException e) {
            lineNumber = 0;
            e.printStackTrace();
        }
        return lineNumber;
    }

    /**
     * 文件内容转数组
     *
     * @return 返回组装好的数组
     */
    public static List<String> file2List() {
        List<String> logList = new ArrayList<>();
        ByteArrayInputStream strStream = new ByteArrayInputStream(queryKey2Value("log").getBytes(StandardCharsets.UTF_8));
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

    /**
     * 删除指定闭区间的行
     *
     * @param line  左闭区间
     * @param line2 右闭区间
     */
    public static void deleteLine(int line, int line2) {

    }

    /**
     * 清除文件内容
     */
    public static void clearFileContent() {
        writer.clear();
        writer.commit();
    }

    /**
     * 打印文件内容
     *
     * @return 返回文件内容
     */
    public static String printFileContent() {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(PATH)));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
