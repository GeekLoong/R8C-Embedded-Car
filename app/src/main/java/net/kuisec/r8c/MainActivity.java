package net.kuisec.r8c;

import static net.kuisec.r8c.Const.InteractionConst.CAR_FLAG;
import static net.kuisec.r8c.Const.InteractionConst.CAR_STATE_FLAG;
import static net.kuisec.r8c.Const.InteractionConst.DATA_FLAG;
import static net.kuisec.r8c.Const.InteractionConst.GET_TASK_DATA_FLAG;
import static net.kuisec.r8c.Const.InteractionConst.REPLY_FLAG;
import static net.kuisec.r8c.Const.InteractionConst.TASK_FLAG;
import static net.kuisec.r8c.Const.ItemConst.CAMERA_SET1;
import static net.kuisec.r8c.Const.ItemConst.CAMERA_SET2;
import static net.kuisec.r8c.Const.ItemConst.CAMERA_SET3;
import static net.kuisec.r8c.Const.ItemConst.CAMERA_SET4;
import static net.kuisec.r8c.Const.ItemConst.QRCODE_A_STORAGE_FLAG;
import static net.kuisec.r8c.Const.ItemConst.QRCODE_B_STORAGE_FLAG;
import static net.kuisec.r8c.Const.ItemConst.RFID_ALARM;
import static net.kuisec.r8c.Const.ItemConst.RFID_LOCATION;
import static net.kuisec.r8c.Const.ItemConst.RFID_LP;
import static net.kuisec.r8c.Const.ItemConst.RFID_STORAGE_FLAG;
import static net.kuisec.r8c.Const.ItemConst.TEXT_STORAGE_FLAG;
import static net.kuisec.r8c.Const.ItemConst.TFT_CAR_MODEL_STORAGE_FLAG;
import static net.kuisec.r8c.Const.ItemConst.TFT_COLOR_ALL_STORAGE_FLAG;
import static net.kuisec.r8c.Const.ItemConst.TFT_COLOR_STORAGE_FLAG;
import static net.kuisec.r8c.Const.ItemConst.TFT_HEX_STORAGE_FLAG;
import static net.kuisec.r8c.Const.ItemConst.TFT_LP_STORAGE_FLAG;
import static net.kuisec.r8c.Const.ItemConst.TFT_MAX_SHAPE_CLASS_STORAGE_FLAG;
import static net.kuisec.r8c.Const.ItemConst.TFT_SHAPE_ALL_STORAGE_FLAG;
import static net.kuisec.r8c.Const.ItemConst.TFT_SHAPE_COLOR_STORAGE_FLAG;
import static net.kuisec.r8c.Const.ItemConst.TFT_SHAPE_STORAGE_FLAG;
import static net.kuisec.r8c.Const.ItemConst.TFT_TRAFFIC_STORAGE_FLAG;
import static net.kuisec.r8c.Const.ItemConst.TRAFFIC_LIGHT_STORAGE_FLAG;
import static net.kuisec.r8c.Const.TaskConst.CAMERA_ADJUST_FLAG;
import static net.kuisec.r8c.Const.TaskConst.QRCODE_REC_FLAG;
import static net.kuisec.r8c.Const.TaskConst.TEXT_REC_FLAG;
import static net.kuisec.r8c.Const.TaskConst.TFT_REC_FLAG;
import static net.kuisec.r8c.Const.TaskConst.TRAFFIC_LIGHT_REC_FLAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.kuisec.rec.plugin.results.RecResult;

import net.kuisec.r8c.Adapter.PagerItemAdapter;
import net.kuisec.r8c.Bean.ServiceBean;
import net.kuisec.r8c.CustomView.MainDialog;
import net.kuisec.r8c.Fragment.CarStateFragment;
import net.kuisec.r8c.Fragment.MapFragment;
import net.kuisec.r8c.Network.CameraCmdUtil;
import net.kuisec.r8c.Utils.AnimatorUtil;
import net.kuisec.r8c.Utils.CommunicationUtil;
import net.kuisec.r8c.Utils.DataPcsUtil;
import net.kuisec.r8c.Utils.FileUtil;
import net.kuisec.r8c.Utils.FloatWindowUtil;
import net.kuisec.r8c.Utils.FloatWindowsManagerUtil;
import net.kuisec.r8c.Utils.HandlerUtil;
import net.kuisec.r8c.Utils.ImgPcsUtil;
import net.kuisec.r8c.Utils.LogUtil;
import net.kuisec.r8c.Utils.PermissionUtil;
import net.kuisec.r8c.Utils.SharedPreferencesUtil;
import net.kuisec.r8c.Utils.TalkUtil;
import net.kuisec.r8c.Utils.ThemeUtil;
import net.kuisec.r8c.Utils.ThreadUtil;
import net.kuisec.r8c.Utils.TimeUtil;
import net.kuisec.r8c.Utils.WifiUtil;
import net.kuisec.r8c.databinding.ActivityMainBinding;
import net.kuisec.r8c.databinding.CarStateFragmentBinding;
import net.kuisec.r8c.ipc.PluginManager;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Jinsn
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 临时数组
     */
    public static byte[] tempByte;

    /**
     * 插件服务信息
     */
    public static Map<String, ServiceBean> serviceMap;

    ActivityMainBinding binding;
    /**
     * 碎片区功能
     */
    CarStateFragment carStateFragment;
    CarStateFragmentBinding carStateFragmentBinding;
    Handler handler;
    Bitmap bitmap;
    DisplayMetrics dm;
    boolean isExit = false;
    /**
     * 悬浮窗
     */
    FloatWindowUtil imgFloatWindow;
    View displayFloat;
    ImageView floatDisplay;
    /**
     * log系统
     */
    boolean logOpen = false;
    int statusBarHeight, navigationBarHeight;
    int activityHeight;
    int activityWidth;
    int logWidth, logHeight;
    long tempTime;
    /**
     * 日志系统滑动查看监听
     */
    @SuppressLint("ClickableViewAccessibility")
    private final View.OnTouchListener logOnTouchListener = (View v, MotionEvent event) -> {
        LogUtil.setLogTouch((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                tempTime = TimeUtil.getMsTime();
                break;
            case MotionEvent.ACTION_UP:
                //计算按下的时间
                long differenceTime = TimeUtil.getMsTime() - tempTime;
                //当按下的时间不足70ms，关闭日志系统
                if (differenceTime < 70) {
                    logAnimate();
                }
                break;
            default:
                break;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        setContentView(binding.getRoot());
    }

    /**
     * 初始化布局内容
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        imgFloatWindow = new FloatWindowUtil(this);
        displayFloat = imgFloatWindow.create(R.layout.float_img);
        floatDisplay = displayFloat.findViewById(R.id.floating_img);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.barMenu.setOnClickListener(this);
        binding.barSetting.setOnClickListener(this);
        binding.camera.setOnTouchListener(new CameraOnTouchListener());

        binding.logParent.setOnClickListener(this);
        binding.logTitle.setOnClickListener(this);
        binding.logContent.setOnClickListener(this);
        binding.logLayout.setOnTouchListener(logOnTouchListener);

        carStateFragment = CarStateFragment.newInstance();

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(carStateFragment);
        fragments.add(MapFragment.newInstance());
        this.binding.pages.setAdapter(new PagerItemAdapter(getSupportFragmentManager(), getLifecycle(), fragments));
    }

    /**
     * 初始化数据
     */
    @SuppressLint({"InternalInsetResource", "DiscouragedApi"})
    private void initData() {
        //初始化消息传递
        handler = new Handler(Looper.getMainLooper(), msg -> {
            switch (msg.what) {
                //数据传输
                case HandlerUtil.DATA_PARSE_FLAG:
                    parseData(msg);
                    break;
                case HandlerUtil.WIFI_STATE_FLAG:
                    switchImageState(HandlerUtil.WIFI_STATE_FLAG, msg);
                    break;
                case HandlerUtil.SOCKET_STATE_FLAG:
                    switchImageState(HandlerUtil.SOCKET_STATE_FLAG, msg);
                    break;
                case HandlerUtil.CAMERA_IP_FLAG:
                    cameraListener(msg.arg1);
                    break;
                case HandlerUtil.LOG_FLAG:
                    logState(msg);
                    break;
                case HandlerUtil.TEXT_FLAG:
                    Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case HandlerUtil.CAMERA_IMG_FLAG:
                    binding.camera.setImageBitmap(bitmap);
                    ImgPcsUtil.setBitmap(bitmap);
                    break;
                case HandlerUtil.DEBUG_IMG_FLAG:
                    floatDisplay.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    floatDisplay.setImageBitmap((Bitmap) msg.obj);
                    ImgPcsUtil.setBitmap((Bitmap) msg.obj);
                    break;
                case HandlerUtil.VOICE:
                    Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    LogUtil.printLog(String.valueOf(msg.obj));
                    TalkUtil.speak(String.valueOf(msg.obj));
                    break;
                default:
                    break;
            }
            return false;
        });
        //消息分发工具类
        HandlerUtil.init(handler);
        //获得碎片的视图对象
        HandlerUtil.post(() -> carStateFragmentBinding = carStateFragment.getBinding());
        //线程工具类初始化
        ThreadUtil.init();
        //文件工具类初始化
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {
                FileUtil.acceptFileResult(result.getData(), getApplicationContext());
            }
        });
        FileUtil.init(launcher);
        //共享文件工具类初始化
        SharedPreferencesUtil.init(getApplicationContext());
        //日志工具类初始化
        LogUtil.init();
        //检查并申请权限
        PermissionUtil.requestPermission(this);
        //设置状态栏主题
        ThemeUtil.setDarkTheme(this);
        //wifi工具类初始化
        WifiUtil.init(getApplicationContext());
        //wifi状态监听
        WifiUtil.wifiStateListener();
        //socket状态监听
        CommunicationUtil.socketStateListener();
        //加载C++库
        ThreadUtil.createThread(() -> {
            initLoadOpenCv();
            initLoadPlugin();
        });

        //提交任务到缓冲
        HandlerUtil.post(() -> {
            //记录日志窗口初始大小
            logWidth = binding.logParent.getMeasuredWidth();
            logHeight = binding.logParent.getMeasuredHeight();
            //得到系统窗口资源
            Resources resources = this.getResources();
            dm = resources.getDisplayMetrics();
            //得到自适应缩放dpi
            float scaledDensity = dm.scaledDensity;
            //记录顶部状态栏尺寸
            statusBarHeight = resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"));
            //记录底部导航栏尺寸
            navigationBarHeight = resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android"));
            //计算Activity可用尺寸，48、14是日志系统根布局左右上下的Margin dp值
            activityWidth = dm.widthPixels - (int) (48 * scaledDensity);
            activityHeight = dm.heightPixels - statusBarHeight - navigationBarHeight - (int) (14 * scaledDensity);
        });
        HandlerUtil.postDelayed(() -> {
            //最小化图片调试界面
            imgFloatWindow.minimize(true);
        }, 1000);
        //语音模块工具类初始化
        TalkUtil.initTalk(this);
    }

    /**
     * 加载OpenCV本地库
     */
    private void initLoadOpenCv() {
        boolean success = OpenCVLoader.initDebug();
        if (success) {
            HandlerUtil.sendMsg("OpenCV 加载成功...");
        } else {
            HandlerUtil.sendMsg("OpenCV 加载失败...");
        }
    }

    /**
     * 启动全部人工智能模型插件
     */
    private void initLoadPlugin() {
        serviceMap = new TreeMap<>();
        serviceMap.put("ocr", new ServiceBean("com.kuisec.rec.plugin.ocr",
                "OCRService",
                new PluginManager("文字识别", this)));
        serviceMap.put("tlr", new ServiceBean("com.kuisec.rec.plugin.tlr",
                "TLRService",
                new PluginManager("交通灯识别", this)));
        serviceMap.put("vtr", new ServiceBean("com.kuisec.rec.plugin.vtr",
                "VTRService",
                new PluginManager("车型识别", this)));
        Collection<ServiceBean> values = serviceMap.values();
        //还要保存连接，在destroy中断开绑定。
        for (ServiceBean service : values) {
            //识别服务集体初始化
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(service.Package, service.ServiceClassName));
            bindService(intent, service.manager, BIND_AUTO_CREATE);
        }
    }

    /**
     * 数据打印显示开关
     *
     * @param msg 消息
     */
    @SuppressLint("SetTextI18n")
    private void logState(Message msg) {
        switch (msg.arg1) {
            case HandlerUtil.LOG_CLOSE:
                binding.logTitle.setText("点击展开 R8C 日志系统");
                binding.logContent.setVisibility(View.GONE);
                break;
            case HandlerUtil.LOG_OPEN:
                binding.logTitle.setText("点击收缩 R8C 日志系统");
                //延时加载内容，防止卡顿
                HandlerUtil.postDelayed(() -> binding.logContent.setVisibility(View.VISIBLE), 550);
                HandlerUtil.postDelayed(() -> binding.logLayout.fullScroll(NestedScrollView.FOCUS_DOWN), 560);
                break;
            case HandlerUtil.LOG_UPDATE:
                binding.logContent.setText(msg.obj.toString());
                if (!LogUtil.isLogTouch()) {
                    binding.logLayout.fullScroll(NestedScrollView.FOCUS_DOWN);
                }
            default:
                break;
        }
    }

    /**
     * 状态面板更换函数
     *
     * @param swiId 视图id
     * @param msg   消息
     */
    @SuppressLint("SetTextI18n")
    private void switchImageState(int swiId, Message msg) {
        switch (swiId) {
            case HandlerUtil.WIFI_STATE_FLAG:    //WIFI
                String wifiStatesContent = (String) msg.obj;
                if (msg.arg1 == HandlerUtil.WIFI_OPEN) {
                    binding.wifiState.setImageResource(R.drawable.wifi_open);
                } else {
                    binding.wifiState.setImageResource(R.drawable.wifi_shut);
                }
                binding.wifiName.setText(wifiStatesContent);
                break;
            case HandlerUtil.SOCKET_STATE_FLAG:    //socket监听UI控制
                String socketStatesContent = (String) msg.obj;
                if (msg.arg1 == HandlerUtil.SOCKET_OPEN) {
                    binding.socketState.setImageResource(R.drawable.socket_open);
                } else {
                    binding.socketState.setImageResource(R.drawable.socket_shut);
                    CommunicationUtil.createConnect(WifiUtil.getWifiGateWay());
                }
                binding.connectText.setText(socketStatesContent);
                break;
            default:
                break;
        }
    }

    /**
     * 获取摄像头信息以及图像
     *
     * @param method 摄像头消息
     */
    @SuppressLint("SetTextI18n")
    private void cameraListener(int method) {
        ThreadUtil.createThread(() -> {
            //更新 IP
            CameraCmdUtil.updateIP();
            //阻塞得到摄像头 IP
            if (method == HandlerUtil.CAMERA_IP_OPEN) {
                while (!"0.0.0.0".equals(CameraCmdUtil.getIP())) {
                    bitmap = CameraCmdUtil.getImage().getImg();
                    HandlerUtil.sendMsg(HandlerUtil.CAMERA_IMG_FLAG);
                    runOnUiThread(() -> binding.cameraIP.setText("IP:" + CameraCmdUtil.getIP()));
                }
                //当摄像头 IP 失效，重置界面显示数据，停止接收图片
                bitmap = null;
                HandlerUtil.sendMsg(HandlerUtil.CAMERA_IMG_FLAG);
                runOnUiThread(() -> binding.cameraIP.setText("IP:未连接摄像头"));
            }
        });
    }

    /**
     * Socket 数据解析
     *
     * @param msg 数据
     */
    @SuppressLint("SetTextI18n")
    private void parseData(Message msg) {
        byte[] mByte = (byte[]) msg.obj;
        //判断是否是重复数据
        if (!Arrays.equals(mByte, tempByte)) {
            //当数据不是重复的，将该数据克隆到临时变量
            tempByte = mByte.clone();
            //对比帧头，将不符合协议规则的数据丢掉
            if (mByte[0] == CAR_FLAG) {
                //确保内容安全，将临时数据复制到处理数据上
                byte[] cmdData = mByte.clone();
                //打印数据
                LogUtil.printData(cmdData, "收到主车数据，数据内容");
                //协议解析，找到对应的协议后执行指定动作
                switch (cmdData[1]) {
                    //主车上传状态
                    case (byte) CAR_STATE_FLAG:
                        //标志位
                        int id = cmdData[10] & 0xff;
                        if (id > 0xA0) {
                            HandlerUtil.sendMsg("随机救援坐标为：" + Integer.toHexString(id).toUpperCase());
                        }
                        break;


                    //主车请求任务
                    case (byte) TASK_FLAG:
                        switch (cmdData[2]) {
                            //二维码识别
                            case (byte) QRCODE_REC_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "二维码识别");
                                ThreadUtil.createThread(() -> {
                                    ThreadUtil.sleep(1500);
                                    //逻辑处理
                                    String qrCodeContent = ImgPcsUtil.qrCodeRecognition();
                                    if (!"没有找到二维码".equals(qrCodeContent)) {
                                        //二维码算法处理
                                        String[] results = qrCodeContent.split(System.lineSeparator());
                                        ImgPcsUtil.qrCodeDecryption(results, cmdData[3]);
                                    }
                                    CommunicationUtil.sendData("", REPLY_FLAG, null);
                                });
                                break;

                            //文字识别
                            case (byte) TEXT_REC_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "文字识别");
                                ThreadUtil.createThread(() -> {
                                    ThreadUtil.sleep(1500);
                                    ImgPcsUtil.chineseTextRecognition(cmdData[3]);
                                });
                                break;

                            //交通灯识别
                            case (byte) TRAFFIC_LIGHT_REC_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "交通灯识别");
                                ThreadUtil.createThread(() -> {
                                    ThreadUtil.sleep(1000);
                                    while (true) {
                                        Bitmap img = ImgPcsUtil.getImg();
                                        List<RecResult> recResults = ImgPcsUtil.imageRecognition(img, "tlr");
                                        if (recResults.size() > 0) {
                                            //多个交通灯识别结果选第一个
                                            switch (recResults.get(0).getLabel()) {
                                                case "RedLight":
                                                case "GreenLight":
                                                case "YellowLight":
                                                    ImgPcsUtil.drawLabel(recResults, img);
                                                    DataPcsUtil.storeAsTrafficLight(recResults.get(0).getLabel(), cmdData[3]);
                                                    LogUtil.printLog("交通灯识别结果", recResults.get(0).getLabel());
                                                    HandlerUtil.sendMsg("交通灯识别结果为：" + recResults.get(0).getLabel());
                                                    break;
                                                default:
                                                    break;
                                            }
                                            break;
                                        }
                                        //找不到的情况下休息100ms
                                        ThreadUtil.sleep(100);
                                    }
                                });
                                break;

                            //TFT 识别
                            case (byte) TFT_REC_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "TFT识别");
                                ThreadUtil.createThread(() -> {
                                    ThreadUtil.sleep(1500);
                                    ImgPcsUtil.tftRecognition(cmdData[3]);
                                });
                                break;

                            //摄像头角度调整
                            case (byte) CAMERA_ADJUST_FLAG:
                                switch (cmdData[3]) {
                                    case CAMERA_SET1:
                                        ThreadUtil.createThread(() -> {
                                            int code = CommunicationUtil.moveCamera("toggle1");
                                            if (code == 200)
                                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "默认视角");
                                        });
                                        break;
                                    case CAMERA_SET2:
                                        ThreadUtil.createThread(() -> {
                                            int code = CommunicationUtil.moveCamera("toggle2");
                                            if (code == 200)
                                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "静态标志物视角");
                                        });
                                        break;
                                    case CAMERA_SET3:
                                        ThreadUtil.createThread(() -> {
                                            int code = CommunicationUtil.moveCamera("toggle3");
                                            if (code == 200)
                                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "交通灯视角");
                                        });
                                        break;
                                    case CAMERA_SET4:
                                        ThreadUtil.createThread(() -> {
                                            int code = CommunicationUtil.moveCamera("toggle4");
                                            if (code == 200)
                                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "TFT视角");
                                        });
                                        break;
                                }
                                break;

                            default:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "收到意外的任务");
                                break;
                        }
                        break;


                    //主车上传数据
                    case (byte) DATA_FLAG:
                        switch (cmdData[2]) {
                            //从车二维码数据处理
                            case (byte) QRCODE_A_STORAGE_FLAG:
                            case (byte) QRCODE_B_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "从车二维码数据处理");
                                break;

                            //RFID 数据处理
                            case (byte) RFID_STORAGE_FLAG:
                                switch (cmdData[3]) {
                                    //破损车牌数据处理
                                    case (byte) RFID_LP:
                                        HandlerUtil.sendMsg(HandlerUtil.VOICE, "破损车牌数据处理");
                                        if (cmdData[4] != 0x00) {
                                            StringBuilder builder = new StringBuilder();
                                            if (cmdData.length > 7) {
                                                builder.append((char) cmdData[4]).append((char) cmdData[5]).append((char) cmdData[6]);
                                                SharedPreferencesUtil.insert("破损车牌", builder.toString());
                                            } else {
                                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "破损车牌不全，无法处理");
                                            }
                                        }
                                        break;

                                    //坐标数据处理
                                    case (byte) RFID_LOCATION:
                                        HandlerUtil.sendMsg(HandlerUtil.VOICE, "坐标数据处理");

                                        break;

                                    //报警码数据处理
                                    case (byte) RFID_ALARM:
                                        HandlerUtil.sendMsg(HandlerUtil.VOICE, "报警码数据处理");
                                        byte[] data = Arrays.copyOfRange(cmdData, 4, cmdData.length - 1);
                                        String result = DataPcsUtil.LZ78(data);
                                        SharedPreferencesUtil.insert("报警码", result);
                                        break;
                                }
                                break;
                        }
                        break;


                    //主车请求数据
                    case (byte) GET_TASK_DATA_FLAG:
                        switch (cmdData[2]) {
                            //请求二维码识别数据
                            case (byte) QRCODE_A_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "二维码A数据请求");
                                CommunicationUtil.postQRCodeContent(cmdData[2]);
                                break;

                            case (byte) QRCODE_B_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "二维码B数据请求");
                                CommunicationUtil.postQRCodeContent(cmdData[2]);
                                break;

                            //请求文字识别数据
                            case (byte) TEXT_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "文字识别数据请求");
                                CommunicationUtil.postChineseTextFromStorage(cmdData[3]);
                                break;

                            //请求交通灯识别数据
                            case (byte) TRAFFIC_LIGHT_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "交通灯识别数据请求");
                                CommunicationUtil.postTrafficLightFromStorage(cmdData[3]);
                                break;

                            //请求RFID处理数据
                            case (byte) RFID_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "RFID处理数据请求");
                                switch (cmdData[3]) {
                                    case (byte) RFID_LP:
                                        HandlerUtil.sendMsg(HandlerUtil.VOICE, "RFID破损车牌修复数据请求");
                                        break;
                                    case (byte) RFID_LOCATION:
                                        HandlerUtil.sendMsg(HandlerUtil.VOICE, "RFID坐标数据请求");
                                        break;
                                    case (byte) RFID_ALARM:
                                        HandlerUtil.sendMsg(HandlerUtil.VOICE, "RFID报警码数据请求");
                                        String alarmCode = SharedPreferencesUtil.queryKey2Value("报警码");
                                        byte[] data = DataPcsUtil.mergeTwoArrays(new byte[]{RFID_ALARM}, DataPcsUtil.stringHexToByteHex(alarmCode));
                                        CommunicationUtil.sendData("post", RFID_STORAGE_FLAG, data);
                                        break;
                                }
                                break;

                            //请求车牌识别数据
                            case (byte) TFT_LP_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "车牌识别数据请求");
                                CommunicationUtil.postLPFromStorage(cmdData[3]);
                                break;

                            //请求 TFT 指定形状数据
                            case (byte) TFT_SHAPE_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "TFT指定形状识别数据请求");
                                break;

                            //请求 TFT 指定颜色数据
                            case (byte) TFT_COLOR_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "TFT指定颜色识别数据请求");
                                break;

                            //请求 TFT 指定形状和颜色数据
                            case (byte) TFT_SHAPE_COLOR_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "TFT指定形状和颜色数据请求");
                                break;

                            //请求 TFT HEX 数据
                            case (byte) TFT_HEX_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "TFT十六进制数据请求");
                                CommunicationUtil.postHexFromStorage(cmdData[3]);
                                break;

                            //请求 TFT 所有颜色数据
                            case (byte) TFT_COLOR_ALL_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "TFT所有颜色数据请求");
                                break;

                            //请求 TFT 所有形状数据
                            case (byte) TFT_SHAPE_ALL_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "TFT所有形状识别数据请求");
                                break;

                            //请求 TFT 交通标志物数据
                            case (byte) TFT_TRAFFIC_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "TFT交通标志识别数据请求");
                                CommunicationUtil.postTrafficFlagFromStorage();
                                break;

                            //请求 TFT 车型数据
                            case (byte) TFT_CAR_MODEL_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "TFT车型识别数据请求");
                                CommunicationUtil.postCarModelFromStorage();
                                break;

                            //请求 TFT 最多图形信息类别
                            case (byte) TFT_MAX_SHAPE_CLASS_STORAGE_FLAG:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "TFT最多图形类别数据请求");
                                CommunicationUtil.postMaxShapeClassFromStorage();
                                break;

                            default:
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "收到意外的请求");
                                break;

                        }
                        break;
                    case REPLY_FLAG:
                        LogUtil.printLog("数据交互", "收到主车回复");
                        CommunicationUtil.reply = true;
                        HandlerUtil.sendMsg("收到主车回复");
                        break;
                    default:
                        HandlerUtil.sendMsg(HandlerUtil.VOICE, "收到意外的数据");
                        break;
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void logAnimate() {
        //展开
        if (!logOpen) {
            logOpen = true;
            FloatWindowsManagerUtil.hideWindows();
            HandlerUtil.sendMsg(HandlerUtil.LOG_FLAG, HandlerUtil.LOG_OPEN);
            AnimatorUtil.setLogAnimator(binding.logLayout, new int[]{0, activityWidth}, new int[]{0, activityHeight});
        } else {
            logOpen = false;
            FloatWindowsManagerUtil.showWindows();
            HandlerUtil.sendMsg(HandlerUtil.LOG_FLAG, HandlerUtil.LOG_CLOSE);
            AnimatorUtil.setLogAnimator(binding.logLayout, new int[]{activityWidth, 0}, new int[]{activityHeight, 0});
        }
    }

    /**
     * 双击返回键退出程序
     *
     * @param keyCode 按键指令
     * @param event   按键动作
     * @return 返回拦截状态，true（不拦截），false（拦截）
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                HandlerUtil.sendMsg("1秒内操作两次返回键才能退出程序哈");
                HandlerUtil.postDelayed(() -> isExit = false, 1000);
            } else {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }
        return false;
    }

    /**
     * MainActivity按钮点击事件
     *
     * @param view 页面视图对象
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bar_menu:
                MainDialog dialog = new MainDialog();
                dialog.show(getSupportFragmentManager(), "Menu Dialog");
                break;
            case R.id.bar_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.log_parent:
            case R.id.log_title:
            case R.id.log_content:
                logAnimate();
                break;
            default:
                break;
        }
    }

    /**
     * 摄像头滑动调整监听类
     */
    private class CameraOnTouchListener implements View.OnTouchListener {
        private float x1 = 0;
        private float y1 = 0;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (bitmap != null) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // 点击位置坐标
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    // 弹起坐标
                    case MotionEvent.ACTION_UP:
                        float x2 = event.getX();
                        float y2 = event.getY();
                        float xx = x1 > x2 ? x1 - x2 : x2 - x1;
                        float yy = y1 > y2 ? y1 - y2 : y2 - y1;
                        // 判断滑屏趋势
                        int minLen = 30;
                        if (xx > yy) {
                            if ((x1 > x2) && (xx > minLen)) {
                                ThreadUtil.createThread(() -> CommunicationUtil.moveCamera("left"));
                            } else if ((x1 < x2) && (xx > minLen)) {
                                ThreadUtil.createThread(() -> CommunicationUtil.moveCamera("right"));
                            }
                        } else {
                            if ((y1 > y2) && (yy > minLen)) {
                                ThreadUtil.createThread(() -> CommunicationUtil.moveCamera("up"));
                            } else if ((y1 < y2) && (yy > minLen)) {
                                ThreadUtil.createThread(() -> CommunicationUtil.moveCamera("down"));
                            }
                        }
                        x1 = 0;
                        y1 = 0;
                        break;
                    default:
                        break;
                }
            }
            return true;
        }
    }
}