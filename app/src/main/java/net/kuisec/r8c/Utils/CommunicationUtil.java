package net.kuisec.r8c.Utils;

import static net.kuisec.r8c.Const.CtrlCarConst.FIND_WAY;
import static net.kuisec.r8c.Const.CtrlCarConst.GO;
import static net.kuisec.r8c.Const.CtrlCarConst.LEFT_TURN;
import static net.kuisec.r8c.Const.CtrlCarConst.RIGHT_TURN;
import static net.kuisec.r8c.Const.CtrlCarConst.START_TASK;
import static net.kuisec.r8c.Const.CtrlCarConst.STOP;
import static net.kuisec.r8c.Const.CtrlCarConst.TFT_A_ANDROID;
import static net.kuisec.r8c.Const.CtrlCarConst.TFT_A_DASHBOARD;
import static net.kuisec.r8c.Const.CtrlCarConst.TFT_B_ANDROID;
import static net.kuisec.r8c.Const.CtrlCarConst.TFT_B_DASHBOARD;
import static net.kuisec.r8c.Const.CtrlCarConst.TFT_C_ANDROID;
import static net.kuisec.r8c.Const.CtrlCarConst.TFT_C_DASHBOARD;
import static net.kuisec.r8c.Const.CtrlCarConst.TFT_IMG_DOWN;
import static net.kuisec.r8c.Const.CtrlCarConst.TFT_IMG_UP;
import static net.kuisec.r8c.Const.HeaderConst.ANDROID_FLAG;
import static net.kuisec.r8c.Const.HeaderConst.CAR_FLAG;
import static net.kuisec.r8c.Const.HeaderConst.DATA_FLAG;
import static net.kuisec.r8c.Const.HeaderConst.END_FLAG;
import static net.kuisec.r8c.Const.HeaderConst.REPLY_FLAG;
import static net.kuisec.r8c.Const.HeaderConst.SAVE_CAR_LOG;
import static net.kuisec.r8c.Const.HeaderConst.TASK_FLAG;
import static net.kuisec.r8c.Const.SignConst.B_FLAG;
import static net.kuisec.r8c.Const.SignConst.C_FLAG;
import static net.kuisec.r8c.Const.SignConst.LOG_CAR_MODEL_RECEP;
import static net.kuisec.r8c.Const.SignConst.LOG_CAR_MODEL_SEND;
import static net.kuisec.r8c.Const.SignConst.QRCODE_STORAGE_FLAG;
import static net.kuisec.r8c.Const.SignConst.RFID_STORAGE_FLAG;
import static net.kuisec.r8c.Const.SignConst.TEXT_STORAGE_FLAG;
import static net.kuisec.r8c.Const.SignConst.TFT_HEX_STORAGE_FLAG;
import static net.kuisec.r8c.Const.SignConst.TFT_LP_STORAGE_FLAG;
import static net.kuisec.r8c.Const.SignConst.TFT_MAX_SHAPE_CLASS_STORAGE_FLAG;
import static net.kuisec.r8c.Const.SignConst.TFT_PERSON_COUNT_STORAGE_FLAG;
import static net.kuisec.r8c.Const.SignConst.TFT_SHAPE_ALL_STORAGE_FLAG;
import static net.kuisec.r8c.Const.SignConst.TFT_TRAFFIC_STORAGE_FLAG;
import static net.kuisec.r8c.Const.SignConst.TFT_VEHICLE_TYPE_BICYCLE;
import static net.kuisec.r8c.Const.SignConst.TFT_VEHICLE_TYPE_CAR;
import static net.kuisec.r8c.Const.SignConst.TFT_VEHICLE_TYPE_MOTORCYCLE;
import static net.kuisec.r8c.Const.SignConst.TFT_VEHICLE_TYPE_STORAGE_FLAG;
import static net.kuisec.r8c.Const.SignConst.TFT_VEHICLE_TYPE_TRUCK;
import static net.kuisec.r8c.Const.SignConst.TRAFFIC_LIGHT_GREEN;
import static net.kuisec.r8c.Const.SignConst.TRAFFIC_LIGHT_RED;
import static net.kuisec.r8c.Const.SignConst.TRAFFIC_LIGHT_STORAGE_FLAG;
import static net.kuisec.r8c.Const.SignConst.TRAFFIC_LIGHT_YELLOW;

import android.util.Log;

import net.kuisec.r8c.Const.SleepTimesConst;
import net.kuisec.r8c.Network.CameraCmdUtil;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通信工具类
 * 管理Socket的各种通信
 *
 * @author Jinsn
 * @date 2022/10/6 20:11
 */
public class CommunicationUtil {

    //socket对象
    private static Socket socket = null;
    //输入流
    private static InputStream input = null;
    //输出流
    private static OutputStream output = null;
    //缓存读取器
    private static BufferedReader reader = null;
    //连接标志
    private static boolean socketConnectedState = false;
    //历史数据
    public static byte[] historySendData;
    //已回复标志
    public static boolean reply = false;


    /**
     * 创建与竞赛平台的连接
     *
     * @param address 竞赛平台地址
     */
    public static void createConnect(String address) {
        //判断socket对象是否存在，若存在直接连接
        if (socket == null) {
            socketConnect(address);
        }
    }


    /**
     * 连接竞赛平台
     *
     * @param address 竞赛平台服务地址
     */
    private static void socketConnect(String address) {
        ThreadUtil.createThread(() -> {
            try {
                //创建一个socket对象
                socket = new Socket();
                //规定连接超时为3s
                socket.connect(new InetSocketAddress(address, 60000), 3000);
                if (socket.isConnected()) {
                    //初始化I流，并初始化给缓存读取器
                    input = socket.getInputStream();
                    //初始化O流
                    output = socket.getOutputStream();
                    //缓冲读取流
                    reader = new BufferedReader(new InputStreamReader(input));
                    //查找摄像头 IP
                    HandlerUtil.sendMsg(HandlerUtil.CAMERA_IP_FLAG, HandlerUtil.CAMERA_IP_OPEN);
                    HandlerUtil.sendMsg("成功连接竞赛平台");
                }
            } catch (ConnectException e) {  //连接异常，可能是传入了无效的地址
                close();
                e.printStackTrace();
            } catch (SocketTimeoutException e) {    //连接竞赛平台超时，可能是WIFI不对劲
                HandlerUtil.sendMsg("找不到竞赛平台，请确认您是否连接到竞赛平台的WiFi！");
                close();
                e.printStackTrace();
            } catch (SocketException e) {   //Socket已经关闭了连接却又重新使用
                HandlerUtil.sendMsg("死去的Socket突然开始攻击我，但还好我技高一筹。");
                close();
                e.printStackTrace();
            } catch (IOException e) {   //IO流出现问题
                HandlerUtil.sendMsg("IO异常：" + e);
                close();
                e.printStackTrace();
            }
        });
    }


    /**
     * 发送数据到竞赛平台
     *
     * @param method 方法，get or post or 为空
     * @param mCmd   主指令，如果主指令为结束位，则不参与数据组装
     * @param mData  数据 byte[]
     */
    public static void sendData(String method, byte mCmd, byte[] mData) {
        //提交数据
        if (socket != null && !socket.isClosed() && socket.isConnected()) {
            ThreadUtil.createThread(() -> {
                String methodName;
                //组装动态数据
                List<Byte> dataList = new ArrayList<>();
                //组装包头
                dataList.add(ANDROID_FLAG);
                //组装标识
                if ("POST".equalsIgnoreCase(method)) {
                    dataList.add(DATA_FLAG);
                    methodName = "存储数据提交至竞赛平台";
                } else if ("GET".equalsIgnoreCase(method)) {
                    dataList.add(TASK_FLAG);
                    methodName = "任务提交至竞赛平台";
                } else {
                    dataList.add(REPLY_FLAG);
                    methodName = "回复竞赛平台";
                }
                //组装数据
                if (!method.isEmpty()) {
                    if (mCmd != REPLY_FLAG)
                        dataList.add(mCmd);
                    if (mData != null) {
                        for (byte b : mData) {
                            dataList.add(b);
                        }
                    }
                }
                //增加结束位
                dataList.add(END_FLAG);
                //动态数据转静态数据
                byte[] data = new byte[dataList.size()];
                for (int i = 0; i < dataList.size(); i++) {
                    data[i] = dataList.get(i);
                }
                //存储当前数据
                if ("POST".equalsIgnoreCase(method)) {
                    historySendData = data.clone();
                }
                //发送数据
                try {
                    output.write(data, 0, data.length);
                    output.flush();
                    //验证回复
                    if (data[1] != REPLY_FLAG && "POST".equalsIgnoreCase(method))
                        ReplyChecked();
                    //打印竞赛平台发送的数据
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data) {
                        stringBuilder.append(String.format("%02X ", byteChar));
                    }
                    LogUtil.printLog(methodName, stringBuilder.toString());
                    HandlerUtil.sendMsg(methodName);
                } catch (IOException e) {
                    HandlerUtil.sendMsg("连接出错，重新连接中...");
                    close();
                }
            });
        } else {
            HandlerUtil.sendMsg("没有与竞赛平台建立连接");
        }
    }

    /**
     * 发送数据到竞赛平台
     *
     * @param mData 数据 byte[]
     */
    public static void sendData(@NotNull byte[] mData) {
        //提交数据
        if (socket != null && !socket.isClosed() && socket.isConnected()) {
            ThreadUtil.createThread(() -> {
                //发送数据
                try {
                    output.write(mData, 0, mData.length);
                    output.flush();
                    //打印竞赛平台发送的数据
                    final StringBuilder stringBuilder = new StringBuilder(mData.length);
                    for (byte byteChar : mData) {
                        stringBuilder.append(String.format("%02X ", byteChar));
                    }
                    LogUtil.printLog("解锁专用发送通道", stringBuilder.toString());
                } catch (IOException e) {
                    HandlerUtil.sendMsg("连接出错，重新连接中...");
                    close();
                }
            });
        } else {
            HandlerUtil.sendMsg("没有与竞赛平台建立连接");
        }
    }


    /**
     * 验证回复，传输数据时如果主车5秒内未回复则再传输一次
     */
    public static void ReplyChecked() {
        //归位回复标志
        reply = false;
        //回复计数
        int replyCount = 1;
        //判断3秒后是否收到回复，若没有收到，再发送一次数据回去，最多发送三次
        while (true) {
            ThreadUtil.sleep(1000);
            if (!reply && replyCount < 4) {
                HandlerUtil.sendMsg(HandlerUtil.VOICE, "未回复");
                CommunicationUtil.sendData(historySendData);
                replyCount++;
            } else {
                break;
            }
        }
    }


    /**
     * 状态监听
     * 原理：isCon只能监听历史是否连接成功，isClo监听当前socket是否被关闭，只要没有null或者close都算没关闭。
     */
    public static void socketStateListener() {
        //监听Socket是否断开
        ThreadUtil.createThread(() -> {
            String socketStatesContent;
            while (true) {
                if (socket != null && socketConnectedState) {
                    if (socket.isConnected() && !socket.isClosed()) {
                        socketStatesContent = "已连接竞赛平台(无线)";
                        HandlerUtil.sendMsg(HandlerUtil.SOCKET_STATE_FLAG, HandlerUtil.SOCKET_OPEN, socketStatesContent);
                    } else {
                        socketStatesContent = "未连接竞赛平台";
                        HandlerUtil.sendMsg(HandlerUtil.SOCKET_STATE_FLAG, HandlerUtil.SOCKET_CLOSE, socketStatesContent);
                        socketConnectedState = true;
                    }
                } else {
                    socketStatesContent = "未连接竞赛平台";
                    HandlerUtil.sendMsg(HandlerUtil.SOCKET_STATE_FLAG, HandlerUtil.SOCKET_CLOSE, socketStatesContent);
                    socketConnectedState = true;
                }
                ThreadUtil.sleep(1500);
            }
        });

        //监听竞赛平台传过来的数据
        ThreadUtil.createThread(() -> {
            while (true) {
                if (socket != null && input != null) {
                    //创建新数据
                    byte[] data = new byte[50];
                    try {
                        int flag = input.read(data);
                        if (flag == -1) {
                            HandlerUtil.sendMsg("与竞赛平台断开连接");
                            HandlerUtil.sendMsg(HandlerUtil.CAMERA_IP_FLAG, HandlerUtil.CAMERA_IP_CLOSE);
                            close();
                        } else {
                            //裁剪数据
                            byte[] tempData = data.clone();
                            //打印数据
                            LogUtil.printData(tempData, "", "");
                            //判断日志是否来自 Zigbee，是的话记录到主车日志，否提供给数据解析模块
                            if (tempData[0] == CAR_FLAG && tempData[1] == SAVE_CAR_LOG) {
                                String logCarModelName = "主车发送";
                                if (tempData[2] == LOG_CAR_MODEL_SEND) {
                                    logCarModelName = "主车发送";
                                } else if (tempData[2] == LOG_CAR_MODEL_RECEP) {
                                    logCarModelName = "主车接收";
                                }
                                boolean error = false;
                                for (int i = tempData.length - 1; i > 0; i--) {
                                    if (tempData[i] == SAVE_CAR_LOG) {
                                        if (i < 3) {
                                            error = true;
                                            break;
                                        }
                                        tempData = Arrays.copyOfRange(tempData, 3, i + 1);
                                        break;
                                    }
                                }
                                if (!error) {
                                    LogUtil.printData(tempData, logCarModelName, "log-car");
                                } else {
                                    Log.e("主车 Zigbee 数据错误：", Arrays.toString(tempData));
                                }
                            } else {
                                for (int i = 0; i < tempData.length; i++) {
                                    if (tempData[i] == 0x00) {
                                        tempData = Arrays.copyOfRange(tempData, 0, i + 1);
                                        break;
                                    }
                                }
                                //解析数据
                                HandlerUtil.sendMsg(HandlerUtil.DATA_PARSE_FLAG, tempData);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 判断socket是否处于断连状态
     *
     * @return 是否断连
     */
    public static boolean isSocketConnection() {
        return socket != null && !socket.isClosed();
    }


    /**
     * 关闭与竞赛平台的连接
     */
    private static void close() {
        try {
            socket.close();
            if (socket != null && socket.isConnected()) {
                input.close();
                output.close();
                reader.close();
                input = null;
                output = null;
                reader = null;
                socketConnectedState = false;
            }
            socket = null;
        } catch (IOException e) {
            HandlerUtil.sendMsg("关闭无线连接失败");
            e.printStackTrace();
        }
    }


    /**
     * 蜂鸣器控制
     *
     * @param instructions 指令
     */
    public static void buzzer(int instructions) {
    }


    /**
     * 从存储中获得二维码识别及解析数据
     *
     * @param classID 二维码类型
     */
    public static void postQRCodeContent(byte classID) {
        String qrCodeContent = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.qrCodeTag + classID);
        qrCodeContent = qrCodeContent.isEmpty() ? "0" : qrCodeContent;
        byte[] data = DataPcsUtil.stringToBytes(qrCodeContent);
        LogUtil.printLog("存储的二维码：" + qrCodeContent);
        CommunicationUtil.sendData("post", QRCODE_STORAGE_FLAG, data);
    }


    /**
     * 从存储中获得文本识别数据并回传给竞赛平台
     *
     * @param classID 获得中文识别内容
     */
    public static void postChineseTextFromStorage(byte classID) {
        String chineseText = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.chineseTag + classID);
        String ocrContent = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.ocrContent);
        if (!ocrContent.equals("0") && !ocrContent.isEmpty()) {
            chineseText = ocrContent;
        }
        int index = 0;
        Pattern pattern = Pattern.compile("\"number\":(\\d)");
        Matcher matcher = pattern.matcher(SharedPreferencesUtil.queryKey2Value("红色二维码"));
        if (matcher.find()) {
            String result = matcher.group(1);
            if (result != null) {
                index = Integer.parseInt(result) - 1;
            }
        }
        chineseText = chineseText.substring(index, index + 1);
        sendData("post", TEXT_STORAGE_FLAG, DataPcsUtil.stringToBytes(chineseText));
        LogUtil.printLog("存储的中文：" + chineseText);
    }


    /**
     * 从存储中获得交通灯识别结果并回传给竞赛平台
     * 交通灯类型
     */
    public static void postTrafficLightFromStorage(byte classID) {
        String trafficLightName = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.trafficLightTag + classID);
        byte lightID = TRAFFIC_LIGHT_GREEN;
        switch (trafficLightName) {
            case "RedLight":
                lightID = TRAFFIC_LIGHT_RED;
                break;
            case "YellowLight":
                lightID = TRAFFIC_LIGHT_YELLOW;
                break;
        }
        byte[] data = {lightID};
        sendData("post", TRAFFIC_LIGHT_STORAGE_FLAG, data);
    }


    /**
     * 上传车牌识别结果
     *
     * @param classID 标志物类型 A or B
     */
    public static void postLPFromStorage(byte classID) {
        String lpTag = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.LPTag + classID);
//        String lpTag = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.LPTag);
        String lpContent = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.lpContent);
        if (!lpContent.isEmpty()) {
            lpTag = lpContent;
        }
        LogUtil.printLog("读取到的车牌数据", lpTag);
        byte[] data = DataPcsUtil.stringToAsciiBytes(lpTag);
        sendData("post", TFT_LP_STORAGE_FLAG, data);
    }


    /**
     * 从存储中获得 RFID 处理数据并回传给竞赛平台
     */
    public static void postRFIDFromStorage() {
        //发送数据
        sendData("post", RFID_STORAGE_FLAG, DataPcsUtil.stringToBytes(SharedPreferencesUtil.queryKey2Value("RFID")));
    }


    static int hexCount = 1;

    /**
     * 上传组装 16 进制数据
     */
    public static void postHexFromStorage(byte classID) {
        String hex = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.hex);
        String hexContent = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.hexContent);
        if (!hexContent.equals("0") && !hexContent.isEmpty()) {
            hex = hexContent;
        }
        LogUtil.printLog("组装的16进制数据内容", hex);
        //字符眀转字节
        byte[] data = DataPcsUtil.stringHexToByteHex(hex);
        //英文与数字组合
//        byte[] data = DataPcsUtil.stringToAsciiBytes(hex);
        CommunicationUtil.sendData("post", TFT_HEX_STORAGE_FLAG, data);
        hexCount++;
    }


    /**
     * 上传交通标志数据内容
     */
    public static void postTrafficSignFromStorage() {
        String trafficSign = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.trafficSignTag);
        //默认返回左转
        String dataContent = "02";
        switch (trafficSign) {
            case "GoStraight":
                dataContent = "01";
                break;
            case "RightTurn":
                dataContent = "03";
                break;
            case "U-turn":
                dataContent = "04";
                break;
            case "NoThroughRoad":
                dataContent = "05";
                break;
            case "NoPassage":
                dataContent = "06";
                break;
            case "NoU-Turn":
                dataContent = "07";
                break;
            case "NoLeftTurn":
                dataContent = "08";
                break;
            case "NoRightTurn":
                dataContent = "09";
                break;
            case "SpeedLimit":
                dataContent = "0A";
                break;
        }
        //检测是否设置交通标志识别结果，设置了就直接返回该设置
        String defaultTrafficSign = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.defaultTrafficSignTag);
        switch (defaultTrafficSign) {
            case "直行":
                dataContent = "01";
                break;
            case "左转":
                dataContent = "02";
                break;
            case "右转":
                dataContent = "03";
                break;
            case "掉头":
                dataContent = "04";
                break;
            case "禁止直行":
                dataContent = "05";
                break;
            case "禁止通行":
                dataContent = "06";
                break;
            case "禁止掉头":
                dataContent = "07";
                break;
            case "禁止左转":
                dataContent = "08";
                break;
            case "禁止右转":
                dataContent = "09";
                break;
            case "限速":
                dataContent = "0A";
                break;
        }
        byte[] data = DataPcsUtil.stringHexToByteHex(dataContent);
        CommunicationUtil.sendData("post", TFT_TRAFFIC_STORAGE_FLAG, data);
    }


    /**
     * 上传车型识别结果
     */
    public static void postVehicleTypeFromStorage() {
        String vehicleType = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.vehicleType);
        byte carClassID = TFT_VEHICLE_TYPE_TRUCK;
        switch (vehicleType) {
            case "Bicycle":
                carClassID = TFT_VEHICLE_TYPE_BICYCLE;
                break;
            case "Motorcycle":
                carClassID = TFT_VEHICLE_TYPE_MOTORCYCLE;
                break;
            case "Car":
                carClassID = TFT_VEHICLE_TYPE_CAR;
                break;
        }
        //检测是否设置车型识别结果，设置了就直接返回该设置
        String defaultVehicleType = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.defaultVehicleType);
        switch (defaultVehicleType) {
            case "自行车":
                carClassID = TFT_VEHICLE_TYPE_BICYCLE;
                break;
            case "摩托车":
                carClassID = TFT_VEHICLE_TYPE_MOTORCYCLE;
                break;
            case "轿车":
                carClassID = TFT_VEHICLE_TYPE_CAR;
                break;
            case "货车":
                carClassID = TFT_VEHICLE_TYPE_TRUCK;
                break;
        }
        CommunicationUtil.sendData("post", TFT_VEHICLE_TYPE_STORAGE_FLAG, new byte[]{carClassID});
    }


    /**
     * 从存储中查找最多图形类别信息
     */
    public static void postMaxShapeClassFromStorage() {
        String maxShapeClass = SharedPreferencesUtil.queryKey2Value("maxShapeClass");
        byte[] data;
        if (!"0".equals(maxShapeClass) && !maxShapeClass.isEmpty()) {
            data = DataPcsUtil.stringHexToByteHex(maxShapeClass);
        } else {
            //从存储中获得识别
            byte maxShapeClassID = DataPcsUtil.getMaxShapeClassFromStorage();
            data = new byte[]{maxShapeClassID};
        }
        CommunicationUtil.sendData("post", TFT_MAX_SHAPE_CLASS_STORAGE_FLAG, data);
    }


    /**
     * 从存储中查找全部图形数量
     */
    public static void postAllShapeCount() {
        int allShapeCount = DataPcsUtil.getMaxCountShapeFromStorage();
        CommunicationUtil.sendData("post", TFT_SHAPE_ALL_STORAGE_FLAG, new byte[]{(byte) 13});
    }


    /**
     * 从存储中查找行人数量信息
     */
    public static void postPersonCountFromStorage() {
        String personCount = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.personCount);
        //判空处理
        personCount = personCount.isEmpty() ? "0" : personCount;
        String personCountContent = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.personCountContent);
        if (!personCountContent.isEmpty()) {
            personCount = personCountContent;
        }
        //取余运算，不用时注释
        personCount = String.valueOf(Integer.parseInt(personCount) % 3);
        byte[] data = DataPcsUtil.stringToAsciiBytes(personCount);
        CommunicationUtil.sendData("post", TFT_PERSON_COUNT_STORAGE_FLAG, data);
    }


    /**
     * 从存储中查找报警码或无线充电开启码信息
     */
    public static void postAlarmCodeOrPowerOpenCodeFromStorage(byte classID) {
        String code;
        if (classID == 0x01) {
            code = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.alarmCodeContent);
            if (code.isEmpty())
                code = "03051445DE92";
        } else {
            code = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.powerOpenCodeContent);
            if (code.isEmpty())
                code = "A123B4";
        }
        code = "0" + classID + code;
        byte[] data = DataPcsUtil.stringHexToByteHex(code);
        CommunicationUtil.sendData("post", (byte) 0x20, data);
    }


    /**
     * 移动小车
     *
     * @param ctrl    移动指令
     * @param hashMap 小车指令
     */
    public static void movingCar(String ctrl, HashMap<String, Integer> hashMap) {
        switch (ctrl) {
            //前进
            case "forward":
                Integer coder = hashMap.get("coder");
                Integer speed = hashMap.get("speed");
                sendData("get", GO, new byte[]{(byte) (speed & 0xFF), (byte) (coder >> 8), (byte) (coder & 0xFF)});
                break;
            //左转
            case "turnLeft":
                sendData("get", LEFT_TURN, new byte[]{(byte) (hashMap.get("angle") & 0xFF)});
                break;
            //右转
            case "turnRight":
                sendData("get", RIGHT_TURN, new byte[]{(byte) (hashMap.get("angle") & 0xFF)});
                break;
            //循迹
            case "findWay":
                sendData("get", FIND_WAY, new byte[]{(byte) (hashMap.get("speed") & 0xFF)});
                break;
            default:
                break;
        }
    }


    /**
     * 停止小车
     */
    public static void stopCar() {
        sendData("get", STOP, null);
    }


    /**
     * 转向灯
     *
     * @param turnID 转向灯 ID
     */
    public static void turnSignal(int turnID) {
        switch (turnID) {
            //熄灭
            case 0:
                break;
            //双闪
            case 1:
                break;
            //左转灯
            case 2:
                break;
            //右转灯
            case 3:
                break;
            default:
                break;
        }
    }


    /**
     * TFT 数据处理
     *
     * @param classID TFT 类型
     * @param hashMap 数据
     */
    public static void tftCmd(byte classID, HashMap<String, String> hashMap) {
        String content = hashMap.getOrDefault("content", "1");
        byte classFLAG = TFT_A_ANDROID;
        switch (classID) {
            case B_FLAG:
                classFLAG = TFT_B_ANDROID;
                break;
            case C_FLAG:
                classFLAG = TFT_C_ANDROID;
                break;
        }
        switch (Objects.requireNonNull(hashMap.get("ctrl"))) {
            case "跳转":
                break;
            case "上一张":
                sendData("get", classFLAG, new byte[]{TFT_IMG_UP});
                break;
            case "下一张":
                sendData("get", classFLAG, new byte[]{TFT_IMG_DOWN});
                break;
            case "自动":
                break;
            case "车牌显示":
                break;
            case "暂停计时":
                break;
            case "重置计时":
                break;
            case "开始计时":
                break;
            case "HEX显示":
                break;
            case "距离显示":
                break;
            case "直行":
                break;
            case "左转":
                break;
            case "右转":
                break;
            case "掉头":
                break;
            case "禁止直行":
                break;
            case "禁止通行":
                break;
            default:
                break;
        }
    }


    /**
     * 语音播报标志物播放指定文本
     *
     * @param text 需要播报的文本
     */
    public static void voiceBroadcast(String text) {
        byte[] textBytes = DataPcsUtil.stringToBytes(text);
        int len = textBytes.length + 2;
        byte[] bytes = DataPcsUtil.mergeTwoArrays(new byte[]{(byte) 0xFD, (byte) (len >> 8), (byte) (len & 0xFF), 0x01, 0x01}, textBytes);
        //发送数据
        sendData(bytes);
    }


    /**
     * 启动主车并下发码盘信息
     */
    public static void startCar() {
        ThreadUtil.createThread(() -> {
            sendData("get", START_TASK, null);

            ThreadUtil.sleep(SleepTimesConst.WAIT_MAIN);

            String tftAD = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.tftAD);
            String tftADHex = Integer.toHexString(Integer.parseInt(tftAD));
            if (tftADHex.length() % 2 != 0) {
                tftADHex = "0" + tftADHex;
            }
            if (tftADHex.length() / 2 < 2) {
                tftADHex = "00" + tftADHex;
            }
            byte[] aData = DataPcsUtil.stringHexToByteHex(tftADHex);
            sendData("get", TFT_A_DASHBOARD, aData);

            ThreadUtil.sleep(SleepTimesConst.WAIT_MAIN);

            String tftBD = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.tftBD);
            String tftBDHex = Integer.toHexString(Integer.parseInt(tftBD));
            if (tftBDHex.length() % 2 != 0) {
                tftBDHex = "0" + tftBDHex;
            }
            if (tftBDHex.length() / 2 < 2) {
                tftBDHex = "00" + tftBDHex;
            }
            byte[] bData = DataPcsUtil.stringHexToByteHex(tftBDHex);
            sendData("get", TFT_B_DASHBOARD, bData);


            ThreadUtil.sleep(SleepTimesConst.WAIT_MAIN);

            String tftCD = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.tftCD);
            String tftCDHex = Integer.toHexString(Integer.parseInt(tftCD));
            if (tftCDHex.length() % 2 != 0) {
                tftCDHex = "0" + tftCDHex;
            }
            if (tftCDHex.length() / 2 < 2) {
                tftCDHex = "00" + tftCDHex;
            }
            byte[] cData = DataPcsUtil.stringHexToByteHex(tftCDHex);
            sendData("get", TFT_C_DASHBOARD, cData);
        });
    }


    /**
     * 摄像头云台控制
     * 云台工提供16个预设位 这只写四个
     *
     * @param vaCmd 控制指令
     * @return 返回指令传输状态 成功码为 200
     */
    public static int moveCamera(String vaCmd) {
        switch (vaCmd) {
            case "left":
                HandlerUtil.sendMsg("摄像头向左微调");
                return CameraCmdUtil.cameraAdjust(4);
            case "right":
                HandlerUtil.sendMsg("摄像头向右微调");
                return CameraCmdUtil.cameraAdjust(6);
            case "up":
                HandlerUtil.sendMsg("摄像头向上微调");
                return CameraCmdUtil.cameraAdjust(0);
            case "down":
                HandlerUtil.sendMsg("摄像头向下微调");
                return CameraCmdUtil.cameraAdjust(2);
            //默认视角
            case "set1":
                HandlerUtil.sendMsg("摄像头设置默认视角");
                return CameraCmdUtil.cameraAdjust(32);
            case "toggle1":
                HandlerUtil.sendMsg("摄像头移动到默认视角");
                return CameraCmdUtil.cameraAdjust(33);
            //静态标志物视角
            case "set2":
                HandlerUtil.sendMsg("摄像头设置静态标志物视角");
                return CameraCmdUtil.cameraAdjust(34);
            case "toggle2":
                HandlerUtil.sendMsg("摄像头移动到静态标志物视角");
                return CameraCmdUtil.cameraAdjust(35);
            //交通灯视角
            case "set3":
                HandlerUtil.sendMsg("摄像头设置交通灯视角");
                return CameraCmdUtil.cameraAdjust(36);
            case "toggle3":
                HandlerUtil.sendMsg("摄像头移动到交通灯视角");
                return CameraCmdUtil.cameraAdjust(37);
            //TFT 视角
            case "set4":
                HandlerUtil.sendMsg("摄像头设置 TFT 视角");
                return CameraCmdUtil.cameraAdjust(38);
            case "toggle4":
                HandlerUtil.sendMsg("摄像头移动到 TFT 视角");
                return CameraCmdUtil.cameraAdjust(39);
            case "reset":
                HandlerUtil.sendMsg("重置摄像头位置");
                return CameraCmdUtil.cameraAdjust(25);
            default:
                return -1;
        }
    }


    /**
     * 回复主车任务完成
     */
    public static void replyCar() {
        CommunicationUtil.sendData("", REPLY_FLAG, null);
    }

}
