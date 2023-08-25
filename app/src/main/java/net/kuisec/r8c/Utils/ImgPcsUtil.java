package net.kuisec.r8c.Utils;

import static net.kuisec.r8c.Const.SignConst.A_FLAG;
import static net.kuisec.r8c.Const.SignConst.B_FLAG;
import static net.kuisec.r8c.Const.SignConst.C_FLAG;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.hyperai.hyperlpr3.HyperLPR3;
import com.hyperai.hyperlpr3.bean.Plate;
import com.kuisec.rec.plugin.results.OCRResult;
import com.kuisec.rec.plugin.results.RecResult;

import net.kuisec.r8c.Bean.OCRRect;
import net.kuisec.r8c.Bean.RectResult;
import net.kuisec.r8c.Bean.ServiceBean;
import net.kuisec.r8c.Const.SleepTimesConst;
import net.kuisec.r8c.MainActivity;
import net.kuisec.r8c.R;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


/**
 * 图像处理工具类
 *
 * @author Jinsn
 * @date 2022/10/12 10:07
 */
public class ImgPcsUtil {
    private static final String IMG_REC = "图像识别";
    public static Scalar[][] dark_hsv_low = {};
    public static Scalar[] dark_hsv_high = {};
    public static Scalar[][] lp_hsv_low = {};
    public static Scalar[] lp_hsv_high = {};
    //实时图像
    private static Bitmap debugImg;

    private static boolean recOver = false;
    private static List<OCRResult> ocrRL;
    private static List<RectResult> rectRL;

    public static final String OCR = "ocr";
    public static final String TLR = "tlr";
    public static final String VTR = "vtr";
    public static final String TSR = "tsr";
    public static final String MR = "mr";
    public static final String HBR = "hbr";

    private final static List<String> trafficLightLabels = Arrays.asList("RedLight", "YellowLight", "GreenLight");
    private final static List<String> trafficSignLabels = Arrays.asList("GoStraight", "LeftTurn", "RightTurn", "U-Turn", "NoThroughRoad", "NoPassage", "NoU-Turn", "NoLeftTurn", "NoRightTurn");
    private final static List<String> maskLabels = Arrays.asList("face_mask", "mask");
    private final static List<String> speedLimitLabels = Arrays.asList("NoThroughRoad", "NoU-Turn", "NoLeftTurn", "NoRightTurn");
    private final static List<String> shapeLabels = Arrays.asList("Triangle", "Circle", "Rectangle", "Diamond", "FivePointedStar", "Trapezoid", "InvalidFigure");
    private final static Map<String, String> vehicleTypeLabels = new LinkedHashMap<>() {
        {
            put("Motorcycle", "摩托车");
            put("Car", "轿车");
            put("Truck", "货车");
        }
    };
    private final static Map<String, String> shapeChineseLabels = new LinkedHashMap<>() {
        {
            put("Triangle", "三角形");
            put("Circle", "圆形");
            put("Rectangle", "矩形");
            put("Diamond", "菱形");
            put("FivePointedStar", "五角星形");
            put("Trapezoid", "梯形");
            put("InvalidFigure", "无效图形");
        }
    };
    private final static List<String> colorNameList = new ArrayList<>() {{
        add("天蓝色");
        add("黄色");
        add("品红色");
        add("蓝色");
        add("绿色");
        add("红色");
        add("黑色");
        add("白色");
    }};


    //TFT 翻页次数
    public static final int maxRecCount = 12;


    /**
     * 解析识别数据
     * 如何做到数据接收后通知发送者？
     * 用全局变量+循环阻塞？
     */
    public static final Messenger messenger = new Messenger(new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            msg.getData().setClassLoader(OCRResult.class.getClassLoader());
            switch (msg.getData().getString("msg", "error")) {
                case OCR:
                    Parcelable[] ocrResults;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ocrResults = msg.getData().getParcelableArray("data", OCRResult.class);
                    } else {
                        ocrResults = msg.getData().getParcelableArray("data");
                    }
                    if (ocrResults != null) {
                        for (Parcelable result : ocrResults) {
                            OCRResult ocrResult = (OCRResult) result;
                            ocrRL.add(ocrResult);
                            LogUtil.printSystemLog("文字识别", ocrResult.getLabel() + "," + ocrResult.getConfidence());
                        }
                        recOver = true;
                    }
                    break;
                case TLR:
                case VTR:
                case TSR:
                case MR:
                case HBR:
                    Parcelable[] recResults;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        recResults = msg.getData().getParcelableArray("data", RecResult.class);
                    } else {
                        recResults = msg.getData().getParcelableArray("data");
                    }
                    if (recResults != null) {
                        for (Parcelable parcelable : recResults) {
                            RecResult result = (RecResult) parcelable;
                            Rect rect = new Rect(new Point(result.getStartTopPoint()[0], result.getStartTopPoint()[1]), new Point(result.getEndBottomPoint()[0], result.getEndBottomPoint()[1]));
                            RectResult rectResult = new RectResult(result.getLabel(), result.getConfidence(), rect);
                            LogUtil.printSystemLog("图像识别", result.getLabel() + "," + result.getConfidence());
                            rectRL.add(rectResult);
                        }
                        recOver = true;
                    }
                    break;
                case "error":
                    HandlerUtil.sendMsg("数据类型不匹配");
                    break;
            }
        }
    });

    /**
     * Mat 自动转四通道 RGB
     *
     * @param srcMat 转换 srcMat 资源
     * @return 返回转换好的四通道 RGB Bitmap 图像
     */
    public static Bitmap matToBitmap(Mat srcMat) {
        Bitmap bitmap = Bitmap.createBitmap(srcMat.width(), srcMat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(srcMat, bitmap);
        return bitmap;
    }


    /**
     * Bitmap 自动转 Mat
     *
     * @param srcBitmap 转换 srcBitmap 资源
     * @return 返回转换好的 Mat
     */
    public static Mat bitmapToMat(Bitmap srcBitmap) {
        Mat tempMat = new Mat(srcBitmap.getHeight(), srcBitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(srcBitmap, tempMat);
        return tempMat;
    }


    /**
     * 设置实时图像
     *
     * @param bitmap Bitmap图像
     */
    public static void setBitmap(Bitmap bitmap) {
        debugImg = bitmap;
    }


    /**
     * 获得实时图像
     *
     * @return 返回当前摄像头图像
     */
    public static Bitmap getImg() {
        if (debugImg != null) {
            return debugImg.copy(Bitmap.Config.ARGB_8888, true);
        } else {
            return null;
        }
    }


    /**
     * 提交识别，用全局标志做有点危险，出问题了就是多线程同时调用了。
     *
     * @param img    图片
     * @param method 插件名称，例如 ocr、tlr、vtr 等
     */
    public static void submitRec(Bitmap img, String method, String timeoutMsg) {
        recOver = false;
        if (method.equals(OCR)) ocrRL = new ArrayList<>();
        else rectRL = new ArrayList<>();
        ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, imgBytes);
        //组装消息
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString("msg", "rec");
        bundle.putByteArray("data", imgBytes.toByteArray());
        message.setData(bundle);
        message.replyTo = ImgPcsUtil.messenger;
        try {
            //发送消息
            ServiceBean serviceBean = MainActivity.serviceMap.get(method);
            if (serviceBean != null) {
                serviceBean.manager.messenger.send(message);
            }
        } catch (RemoteException e) {
            HandlerUtil.sendMsg("与插件通信出现异常，请检查“" + method.toUpperCase() + "”插件是否运行或传输的图片是否太大！");
        }
        waitRec(timeoutMsg);
    }


    /**
     * 设置超时阻塞
     *
     * @param timeoutMsg 超时消息
     */
    private static void waitRec(String timeoutMsg) {
        //阻塞等待识别完成，设置阻塞五秒超时
        int waitTime = 0;
        while (!recOver) {
            ThreadUtil.sleep(10);
            if (waitTime >= 5000) {
                HandlerUtil.sendMsg(HandlerUtil.VOICE, timeoutMsg);
                break;
            } else waitTime += 10;
        }
    }


    /**
     * HSV 图像区间实时查找
     *
     * @param lower HSV 左闭区间
     * @param upper HSV 右闭区间
     * @param img   img显示区对象
     */
    public static void realTimeHsv(Scalar lower, Scalar upper, ImageView img) {
        if (debugImg != null) {
            Mat src = bitmapToMat(getImg());
            Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2HSV);
            Imgproc.GaussianBlur(src, src, new Size(3, 3), 0, 0);
            //颜色区间
            Mat dst = new Mat();
            Core.inRange(src, lower, upper, dst);
            //开闭运算
            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
            Imgproc.morphologyEx(dst, dst, Imgproc.MORPH_OPEN, kernel);
            Imgproc.morphologyEx(dst, dst, Imgproc.MORPH_CLOSE, kernel);
            //显示图片
            Bitmap bitmap = matToBitmap(dst);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            img.setImageBitmap(bitmap);
        } else {
            img.setImageResource(R.drawable.no_img);
        }
    }


    /**
     * 图像识别
     *
     * @return 返回识别结果和识别时的图像
     */
    public static List<RectResult> imageRecognition(Bitmap img, String method) {
        //进行模型识别，老设备没有GPU，会无法识别出标签
        List<RectResult> results = new ArrayList<>();
        if (img != null) {
            submitRec(img, method, method.toUpperCase() + "识别超时");
            //识别结果（无）提示语
            String recognitionResponse;
            //保存图像
            FileUtil.saveImg(img, "图像识别过程", "");
            //读取阈值
            double imgTh = Double.parseDouble(SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.imgTh));
            //遍历得到有效内容
            for (RectResult result : rectRL) {
                if (result.getConfidence() >= imgTh) {
                    results.add(result);
                }
            }
            if (results.isEmpty()) {
                recognitionResponse = "没有学习过的物体或阈值过高";
                LogUtil.printLog(IMG_REC, recognitionResponse);
            }
        } else {
            HandlerUtil.sendMsg("未找到图像");
        }
        return results;
    }


    /**
     * 绘制标签位置
     *
     * @param results     标签属性
     * @param image       绘制的图像
     * @param shortString 是否开启短字符
     */
    public static void drawLabels(List<RectResult> results, Bitmap image, boolean shortString) {
        if (image != null) {
            Mat srcMat = bitmapToMat(image);
            //绘制颜色
            Scalar rectColor = new Scalar(255, 0, 0);
            //文字颜色
            Scalar textColor = new Scalar(255, 255, 255);
            for (RectResult result : results) {
                //绘制识别矩形框
                Imgproc.rectangle(srcMat, result.getRect(), rectColor, 2, 4);
                String labelContent;
                if (shortString) {
                    //绘制标签前面的3个文字
                    labelContent = result.getLabel().substring(0, 3) + "(" + String.format(Locale.CHINA, "%.3f", result.getConfidence()) + ")";
                } else {
                    //绘制标签所有文字
                    labelContent = result.getLabel() + "(" + String.format(Locale.CHINA, "%.3f", result.getConfidence()) + ")";
                }
                //获得文字大小
                Size textSize = Imgproc.getTextSize(labelContent, Imgproc.FONT_HERSHEY_COMPLEX, 0.7, 2, new int[]{1});
                //绘制文字背景
                Point textPoint = new Point(result.getRect().x + 2, result.getRect().y - 5);
                Rect backgroundRect = new Rect((int) textPoint.x - 4, (int) textPoint.y - 18, (int) textSize.width + 6, (int) textSize.height + 7);
                Imgproc.rectangle(srcMat, backgroundRect, rectColor, -1, 4);
                //绘制文字
                Imgproc.putText(srcMat, labelContent, textPoint, Imgproc.FONT_HERSHEY_COMPLEX, 0.7, textColor);
            }
            //转换成可显示图像
            Bitmap tempBitmap = matToBitmap(srcMat);
            //显示结果
            HandlerUtil.sendMsg(HandlerUtil.DEBUG_IMG_FLAG, tempBitmap);
            //保存识别结果
            FileUtil.saveImg(tempBitmap, "图像识别结果", "");
        }
    }


    /**
     * 将图像画幅扩大处理为等比例图像，用于文字识别前预处理
     *
     * @param bitmap 图像
     * @return 返回扩大的画幅
     */
    public static Mat equalScaleImage(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Mat srcMat = bitmapToMat(bitmap);
        //得到图像最长的边
        int maxLength;
        if (srcMat.width() > srcMat.height()) {
            maxLength = srcMat.cols();
        } else {
            maxLength = srcMat.rows();
        }
        //新建一个Mat存储原图像并居中
        Mat dstMat = new Mat(maxLength, maxLength, CvType.CV_8UC4);
        int x = (dstMat.cols() - srcMat.cols()) / 2;
        int y = (dstMat.rows() - srcMat.rows()) / 2;
        //将原图像复制到新Mat中并居中
        srcMat.copyTo(dstMat.rowRange(y, y + srcMat.rows()).colRange(x, x + srcMat.cols()));
        return dstMat;
    }


    /**
     * 文字识别，识别完成后旋转45°继续识别，直到没有结果为止
     *
     * @param srcMat 识别的矩阵，会继续旋转等操作
     * @param rotate 是否旋转
     * @return 返回识别结果
     */
    public static List<OCRRect> ocr(Mat srcMat, boolean rotate) {
        if (srcMat != null) {
            //初始化旋转角度
            int rotationAngle = 0;
            //提交识别任务
            submitRec(matToBitmap(srcMat), OCR, "文字识别超时");
            //保存识别时图片
            FileUtil.saveImg(matToBitmap(srcMat), "文字识别过程", "");
            //遍历识别结果
            List<OCRRect> results = new ArrayList<>();
            //解析结果并将结果所在位置涂白，接着进行下一次识别，直到无数据为止
            Mat dstMat = srcMat.clone();
            excludeText(results, dstMat, rotate, rotationAngle);
            //无数据检索后返回所有识别结果
            return results;
        } else {
            HandlerUtil.sendMsg("未找到图像");
        }
        return new ArrayList<>();
    }


    /**
     * 排除文字
     * 将识别过的文字涂白、旋转，在存在返回值时一直识别，直到无返回值为止
     *
     * @param results       全部符合规范的返回值存放列表
     * @param srcMat        即将旋转的图像
     * @param rotate        是否旋转
     * @param rotationAngle 旋转角度信息
     */
    private static void excludeText(List<OCRRect> results, Mat srcMat, boolean rotate, int rotationAngle) {
        if (!ocrRL.isEmpty()) {
            for (OCRResult result : ocrRL) {
                //只回传置信率大于指定百分比的识别结果
                double ocrTh = Double.parseDouble(SharedPreferencesUtil.queryKey2Value("ocrTh"));
                if (result.getConfidence() >= ocrTh) {
                    //将符合阈值的文字涂白
                    int width = result.getEndBottomPoint()[0] - result.getStartTopPoint()[0];
                    int height = result.getEndBottomPoint()[1] - result.getStartTopPoint()[1];
                    Rect rect = new Rect(result.getStartTopPoint()[0], result.getStartTopPoint()[1], width, height);
//                    Imgproc.rectangle(srcMat, rect, new Scalar(255, 255, 255), -1);
                    //存储识别信息
                    OCRRect ocrRectResult = new OCRRect(result.getLabel(), rotationAngle, result.getConfidence(), rect);
                    results.add(ocrRectResult);
                    //将涂白的图像显示到Debug窗口
                    HandlerUtil.sendMsg(HandlerUtil.DEBUG_IMG_FLAG, matToBitmap(srcMat));
                    LogUtil.printLog("原始有效文字：" + result.getLabel() + "，置信率：" + result.getConfidence());
                } else {
                    LogUtil.printLog("无效文字：" + result.getLabel() + "，置信率：" + result.getConfidence());
                }
            }
            //保存涂白后的图片
            FileUtil.saveImg(matToBitmap(srcMat), "文字涂白识别过程", "");
            //将涂白的图像顺时针旋转45度
            if (rotate) {
                rotationAngle -= 45;
                Point center = new Point((double) srcMat.cols() / 2, (double) srcMat.rows() / 2);
                Size outputSize = new Size(srcMat.cols(), srcMat.rows());
                //设置旋转任务
                Mat rotationMat = Imgproc.getRotationMatrix2D(center, rotationAngle, 1);
                Imgproc.warpAffine(srcMat, srcMat, rotationMat, outputSize);
                //再度识别
                submitRec(matToBitmap(srcMat), OCR, "文字识别超时");
                //递归检索结果
                excludeText(results, srcMat, true, rotationAngle);
            }
        }
    }


    /**
     * 单次中文识别
     */
    public static void recOnceChineseText(byte classID) {
        HandlerUtil.sendMsg(HandlerUtil.VOICE, "中文识别");
        ThreadUtil.createThread(() -> {
            ThreadUtil.sleep(SleepTimesConst.WAIT_CAMERA);
            Bitmap img = getImg();
            StringBuilder textRECBuilder = new StringBuilder();
            Mat srcMat = equalScaleImage(img);
            List<OCRRect> results = ocr(srcMat, false);
            if (results.size() > 0) {
                //顺序读取识别结果并抹除识别结果
                for (int i = (results.size() - 1); i > -1; i--) {
                    textRECBuilder.append(DataPcsUtil.chineseFilter(results.get(i).getLabel()));
                }
            }
            if (textRECBuilder.length() > 0) {
                HandlerUtil.sendMsg(HandlerUtil.VOICE, "找到咯");
            }
            LogUtil.printLog("有效中文识别：" + textRECBuilder);
            SharedPreferencesUtil.insert(SharedPreferencesUtil.chineseTag + classID, textRECBuilder.toString());
            CommunicationUtil.replyCar();
        });
    }


    /**
     * TFT 中文识别
     */
    public static void recTftChineseTextRecognition(byte classID) {
        HandlerUtil.sendMsg(HandlerUtil.VOICE, "TFT 中文识别");
        ThreadUtil.createThread(() -> {
            ThreadUtil.sleep(SleepTimesConst.WAIT_CAMERA);
            boolean recSuccess = false;
            for (int p = 0; p < maxRecCount; p++) {
                //默认翻一页，识别不到掉头再翻一页
                if (!recSuccess) {
                    tftDown(classID);
                }
                if (recSuccess) {
                    break;
                } else {
                    Bitmap img = getImg();
                    StringBuilder textRECBuilder = new StringBuilder();
                    Mat srcMat = equalScaleImage(img);
                    List<OCRRect> results = ocr(srcMat, false);
                    if (!results.isEmpty()) {
                        //顺序读取识别结果并抹除识别结果
                        for (int i = (results.size() - 1); i > -1; i--) {
                            textRECBuilder.append(DataPcsUtil.chineseFilter(results.get(i).getLabel()));
                        }
                    }
                    if (textRECBuilder.length() > 0) {
                        HandlerUtil.sendMsg(HandlerUtil.VOICE, "找到咯");
                        LogUtil.printLog("有效中文识别：" + textRECBuilder);
                        SharedPreferencesUtil.insert(SharedPreferencesUtil.chineseTag + classID, textRECBuilder.toString());
                        recSuccess = true;
                        CommunicationUtil.voiceBroadcast(textRECBuilder.toString());
                    }
                }
            }
            CommunicationUtil.replyCar();
        });
    }


    /**
     * 国赛找“真字”多边形计数识别
     *
     * @param classID
     */
    public static void recTftChineseTextRecognition2(byte classID) {
        HandlerUtil.sendMsg(HandlerUtil.VOICE, "TFT 中文识别");
        int maxShapePaperCount = 3;
        ThreadUtil.createThread(() -> {
            ThreadUtil.sleep(SleepTimesConst.WAIT_CAMERA);
            SharedPreferencesUtil.deleteShapeColorRecHistoryStorage();
            boolean recSuccess = false;
            int containZhenPaper = 1;
            for (int p = 0; p < maxRecCount; p++) {
                //默认翻一页，识别不到掉头再翻一页
                if (!recSuccess) {
                    tftDown(classID);
                }
                if (recSuccess) {
                    break;
                } else {
                    Bitmap img = getImg();
                    StringBuilder textRECBuilder = new StringBuilder();
                    Mat srcMat = equalScaleImage(img);
                    //判断最多三次含真
                    List<OCRRect> results = ocr(srcMat, false);
                    if (!results.isEmpty()) {
                        //顺序读取识别结果并抹除识别结果
                        for (int i = (results.size() - 1); i > -1; i--) {
                            textRECBuilder.append(DataPcsUtil.chineseFilter(results.get(i).getLabel()));
                        }
                    }
                    if (textRECBuilder.length() > 0 && textRECBuilder.toString().contains("真")) {
                        //含真字，containZhenPaper++，进行图形识别
                        containZhenPaper++;
                        List<RectResult> rectResults = imageRecognition(img, TSR);
                        List<RectResult> drawLabels = new ArrayList<>();
                        boolean shapeRecSuccess = parseShapes(rectResults, img, true, drawLabels);
                        if (shapeRecSuccess) {
                            HandlerUtil.sendMsg(HandlerUtil.VOICE, "找到咯");
                            LogUtil.printLog("有效中文识别：" + textRECBuilder);
                            assembleHex(classID);
                        }
                        drawLabels(drawLabels, img, true);
                        CommunicationUtil.voiceBroadcast("真");
                    }
                }
                if (containZhenPaper > maxShapePaperCount) {
                    recSuccess = true;
                }
            }
            CommunicationUtil.replyCar();
        });
    }

    static int qrCodeCount = 0;

    /**
     * 二维码解密
     */
    public static void qrCodeDecryption(byte classID) {
        String greenQRCode = SharedPreferencesUtil.queryKey2Value("绿色二维码");
        SharedPreferencesUtil.insert(SharedPreferencesUtil.qrCodeTag + classID, greenQRCode);
        qrCodeCount++;
    }


    /**
     * 二维码识别
     *
     * @param classID 二维码类型
     */
    public static void recQRCode(byte classID, boolean noColor) {
        HandlerUtil.sendMsg(HandlerUtil.VOICE, "二维码识别");
        ThreadUtil.createThread(() -> {
            ThreadUtil.sleep(SleepTimesConst.WAIT_CAMERA);
            boolean recSuccess = false;
            for (int p = 0; p < maxRecCount; p++) {
                if (recSuccess) {
                    break;
                } else {
                    Bitmap img = getImg();
                    if (img != null) {
                        if (noColor) {  //黑白二维码
                            recSuccess = recBWQRCode(classID, img);
                        } else {    //彩色二维码
                            recSuccess = recColorQRCode(classID, img);
                        }
                    } else {
                        HandlerUtil.sendMsg("未找到图像");
                    }
                }
                if (!recSuccess) {
                    //找不到的情况下休息500ms
                    ThreadUtil.sleep(SleepTimesConst.WAIT_CAMERA);
                }
            }
            if (recSuccess)
                HandlerUtil.sendMsg(HandlerUtil.VOICE, "找到咯");
            CommunicationUtil.replyCar();
        });
    }


    /**
     * 二维码解码
     *
     * @param img           图像
     * @param qrCodeResults 接收返回结果和定位的 ConcurrentHashMap，ConcurrentHashMap 可以保证唯一性
     */
    public static void qrDecode(Bitmap img, ConcurrentHashMap<String, Rect> qrCodeResults) {
        int[] pixels = new int[img.getWidth() * img.getHeight()];
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        RGBLuminanceSource source = new RGBLuminanceSource(img.getWidth(), img.getHeight(), pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeMultiReader multiReader = new QRCodeMultiReader();
        Result[] results = new Result[0];
        try {
            results = multiReader.decodeMultiple(binaryBitmap);
        } catch (NotFoundException ignored) {
        }
        for (Result result : results) {
            int minX = 999999;
            int minY = 999999;
            int maxX = 0;
            int maxY = 0;
            ResultPoint[] resultPoints = result.getResultPoints();
            for (ResultPoint point : resultPoints) {
                if (point.getX() < minX)
                    minX = (int) point.getX();
                if (point.getY() < minY)
                    minY = (int) point.getY();
                if (point.getX() > maxX)
                    maxX = (int) point.getX();
                if (point.getY() > maxY)
                    maxY = (int) point.getY();
            }
            int width = maxX - minX;
            int height = maxY - minY;
            //根据三个点定位最小内接矩形
            int max = Math.max(width, height);
            Rect rect = new Rect(minX, minY, max, max);
            qrCodeResults.put(result.getText(), rect);
        }
    }


    /**
     * 识别灰度二维码
     *
     * @param classID 静态标志物类型
     * @param img     带二维码的图像
     * @return 返回识别成功结果
     */
    public static boolean recBWQRCode(byte classID, Bitmap img) {
        boolean recSuccess = false;
        ConcurrentHashMap<String, Rect> results = new ConcurrentHashMap<>();
        //二维码解码
        qrDecode(img, results);
        if (!results.isEmpty()) {
            recSuccess = true;
            //二维码位置标记
            Mat drawMat = bitmapToMat(img);
            for (Rect rect : results.values()) {
                Imgproc.rectangle(drawMat, rect, new Scalar(3, 101, 100), 3, Imgproc.LINE_AA);
            }
            HandlerUtil.sendMsg(HandlerUtil.DEBUG_IMG_FLAG, matToBitmap(drawMat));
            //存储二维码
            StringBuilder builder = new StringBuilder();
            results.keySet().forEach(result -> {
                LogUtil.printLog("无色二维码识别：" + result);
                builder.append(result).append("\n");
            });
            SharedPreferencesUtil.insert(SharedPreferencesUtil.qrCodeTag + classID, builder.toString());
            qrCodeDecryption(classID);
        } else {
            LogUtil.printLog("未找到有效二维码");
            HandlerUtil.sendMsg("未找到有效二维码");
        }
        return recSuccess;
    }


    /**
     * 识别彩色二维码
     * 实测对比度 +-0.10 各5次可以提高识别率
     * 降低亮度，提高对比度
     *
     * @param img 带二维码的图像
     */
    public static boolean recColorQRCode(byte classID, Bitmap img) {
        boolean recSuccess = false;
        //彩色二维码颜色阈值
        Mat colorMat = bitmapToMat(img);
        List<Scalar[]> qrCodeColorTh = new ArrayList<>() {{
            //红色
            add(new Scalar[]{
                    new Scalar(105, 40, 30),
                    new Scalar(150, 255, 255)
            });
            //绿色
            add(new Scalar[]{
                    new Scalar(15, 40, 55),
                    new Scalar(80, 255, 255)
            });
            //蓝色
            add(new Scalar[]{
                    new Scalar(150, 0, 0),
                    new Scalar(180, 255, 255)
            });
            //黄色
            add(new Scalar[]{
                    new Scalar(87, 60, 110),
                    new Scalar(100, 255, 255)
            });
        }};
        Mat drawMat = bitmapToMat(img);
        //多线程读写 Map
        ConcurrentHashMap<String, Rect> qrCodeResults = new ConcurrentHashMap<>();
        Consumer<Double> consumer = scale -> {
            double cs = 1.00;
            int bs = 0;
            //每一次都进行识别，然后放入 ConcurrentHashMap 中。
            for (int i = 0; i < 5; i++) {
                //图像对比度 自增/自减 五次
                cs += scale;
                //图像亮度自减五次
                bs -= 5;
                LogUtil.printSystemLog("二维码识别图像信息：", "cs：" + cs + "，bs：" + bs);
                Mat srcMat = bitmapToMat(img);
                //线性变换，简易修改对比度
                srcMat.convertTo(srcMat, srcMat.type(), cs, bs);
                Bitmap lastImg = matToBitmap(srcMat);
                //二维码解码，最终结果放入 ConcurrentMap ，保证唯一性
                qrDecode(lastImg, qrCodeResults);
            }
        };
        //对比度自增处理
        CompletableFuture<Void> contrastIncreaseFuture = CompletableFuture.runAsync(() -> {
            consumer.accept(0.10);
        });
        //对比度自减处理
        CompletableFuture<Void> contrastDecreaseFuture = CompletableFuture.runAsync(() -> {
            consumer.accept(-0.10);
        });
        //等待异步处理完成
        CompletableFuture.allOf(contrastIncreaseFuture, contrastDecreaseFuture).join();
        //解析数据
        if (!qrCodeResults.keySet().isEmpty()) {
            //绘制和查找颜色
            qrCodeResults.keySet().forEach(label -> {
                Rect rect = qrCodeResults.get(label);
                assert rect != null;
                Imgproc.rectangle(drawMat, rect, new Scalar(3, 101, 100), 3, Imgproc.LINE_AA);
                Mat subMat = colorMat.submat(rect);
                Imgproc.cvtColor(subMat, subMat, Imgproc.COLOR_BGR2HSV);
                //迭代回归最优解
                String colorName = "";
                AtomicInteger area = new AtomicInteger(0);
                AtomicInteger index = new AtomicInteger(0);
                qrCodeColorTh.forEach(scalars -> {
                    Mat rangeMat = new Mat();
                    Core.inRange(subMat, scalars[0], scalars[1], rangeMat);
                    List<MatOfPoint> contours = new ArrayList<>();
                    Imgproc.findContours(rangeMat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                    //迭代得到最大面积
                    contours.forEach(matOfPoint -> {
                        if (area.get() < Imgproc.contourArea(matOfPoint)) {
                            //更新面积
                            area.set((int) Imgproc.contourArea(matOfPoint));
                            //更新索引
                            index.set(qrCodeColorTh.indexOf(scalars));
                        }
                    });
                });
                //二维码赋予颜色并存储
                switch (index.get()) {
                    case 0:
                        colorName = "红色";
                        break;
                    case 1:
                        colorName = "绿色";
                        break;
                    case 2:
                        colorName = "蓝色";
                        break;
                    case 3:
                        colorName = "黄色";
                        break;
                }
                LogUtil.printLog(colorName + "二维码：" + label);
                SharedPreferencesUtil.insert(colorName + SharedPreferencesUtil.qrCodeTag + classID, label);
            });
            //二维码解密
            recSuccess = true;
            qrCodeDecryption(classID);
            HandlerUtil.sendMsg(HandlerUtil.DEBUG_IMG_FLAG, matToBitmap(drawMat));
        } else {
            LogUtil.printLog("未找到有效二维码");
            HandlerUtil.sendMsg("未找到有效二维码");
        }
        return recSuccess;
    }


    /**
     * 交通灯识别
     *
     * @param classID 交通灯类型ID
     */
    public static void recTrafficLight(byte classID) {
        HandlerUtil.sendMsg(HandlerUtil.VOICE, "交通灯识别");
        ThreadUtil.createThread(() -> {
            ThreadUtil.sleep(SleepTimesConst.WAIT_CAMERA);
            boolean recSuccess = false;
            for (int p = 1; p < maxRecCount; p++) {
                if (recSuccess) {
                    break;
                } else {
                    Bitmap img = getImg();
                    List<RectResult> rectResults = imageRecognition(img, TLR);
                    if (!rectResults.isEmpty()) {
                        //同一时间识别到多个交通灯只取第一个
                        if (trafficLightLabels.contains(rectResults.get(0).getLabel())) {
                            //保存和打印交通灯识别信息
                            DataPcsUtil.saveTrafficLight(rectResults.get(0).getLabel(), classID);
                            HandlerUtil.sendMsg(HandlerUtil.VOICE, "找到咯");
                            drawLabels(rectResults, img, false);
                            recSuccess = true;
                        }
                    }
                    if (!recSuccess) {
                        //找不到的情况下休息500ms
                        ThreadUtil.sleep(SleepTimesConst.WAIT_CAMERA);
                    }
                }
            }
            CommunicationUtil.replyCar();
        });
    }


    /**
     * 解析交通标志查找和识别结果（需要先识别）
     *
     * @param rectResults 识别结果
     * @param img         原图
     * @return 返回解析成功的结果
     */
    public static boolean parseTrafficSigns(List<RectResult> rectResults, Bitmap img, List<RectResult> drawLabels) {
        HandlerUtil.sendMsg(HandlerUtil.VOICE, "交通标志查找和识别");
        boolean recSuccess = false;
        if (!rectResults.isEmpty()) {
            for (RectResult result : rectResults) {
                //匹配交通标志
                if (trafficSignLabels.contains(result.getLabel())) {
                    //没训练最低、最高限速标志，所以将删除的和误判的类别归类到限速中（裁剪后文字识别不好用，百度的都识别不出来，只能训练了）
                    if (speedLimitLabels.contains(result.getLabel())) {
                        result.setLabel("SpeedLimit");
                    }
                    SharedPreferencesUtil.insert(SharedPreferencesUtil.trafficSignTag, result.getLabel());
                    drawLabels.add(result);
                    recSuccess = true;
                }
            }
        }
        if (recSuccess)
            HandlerUtil.sendMsg(HandlerUtil.VOICE, "找到咯");
        return recSuccess;
    }


    /**
     * 解析多边形查找和识别结果（需要先识别）
     *
     * @param rectResults 识别结果
     * @param img         识别的原图
     * @param traIsRec    梯形是否纳入矩形
     * @return 返回解析成功的结果
     */
    public static boolean parseShapes(List<RectResult> rectResults, Bitmap img, boolean traIsRec, List<RectResult> drawLabels) {
        HandlerUtil.sendMsg(HandlerUtil.VOICE, "多边形查找和识别");
        boolean recSuccess = false;
        //五个以上图形才算正常图形页面，少于5个算无效
        if (rectResults.size() > 5) {
            //删除历史识别数据
//            SharedPreferencesUtil.deleteShapeColorRecHistoryStorage();
            //开始解析
            for (RectResult result : rectResults) {
                //匹配形状
                if (shapeLabels.contains(result.getLabel())) {
                    //裁剪图形，分类
                    Mat shapeMat = bitmapToMat(img).submat(result.getRect());
                    //定义卷积核，处理图形光滑度
                    Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
                    Imgproc.cvtColor(shapeMat, shapeMat, Imgproc.COLOR_BGR2HSV);
                    Imgproc.GaussianBlur(shapeMat, shapeMat, new Size(3, 3), 3, 3);
                    //按照颜色查找图像
                    double maxArea = 0;
                    int colorClassID = 0;
                    for (int colorIndex = 0; colorIndex < dark_hsv_high.length; colorIndex++) {
                        //存放每个色系二值化图像，最后一个放最优内容
                        Mat[] colorsMat = new Mat[dark_hsv_low[colorIndex].length + 1];
                        //阈值区间二值化图像，根据索引遍历每组颜色的低阈值
                        for (int thresholdIndex = 0; thresholdIndex < dark_hsv_low[colorIndex].length; thresholdIndex++) {
                            Mat dstMat = new Mat();
                            Core.inRange(shapeMat, dark_hsv_low[colorIndex][thresholdIndex], dark_hsv_high[colorIndex], dstMat);
                            colorsMat[thresholdIndex] = dstMat.clone();
                        }

                        //合并多个二值化图像，最后一个位置为合并位，初始内容为第一个二值化图，所以第一次第一位与最后一位不参与运算
                        colorsMat[colorsMat.length - 1] = colorsMat[0].clone();
                        for (int binaryIndex = 0; binaryIndex < (colorsMat.length - 1); binaryIndex++) {
                            //排除倒数第二个和最后一个
                            if (binaryIndex > 0 && binaryIndex < (colorsMat.length - 1)) {
                                //将索引位 Mat 与最后输出位 Mat 进行 or 运算
                                Core.bitwise_or(colorsMat[binaryIndex], colorsMat[colorsMat.length - 1], colorsMat[colorsMat.length - 1]);
                            }
                        }
                        //开闭运算，优化合并图像的噪声
                        Imgproc.morphologyEx(colorsMat[colorsMat.length - 1], colorsMat[colorsMat.length - 1], Imgproc.MORPH_OPEN, kernel);
                        Imgproc.morphologyEx(colorsMat[colorsMat.length - 1], colorsMat[colorsMat.length - 1], Imgproc.MORPH_CLOSE, kernel);

                        //查找轮廓
                        List<MatOfPoint> contours = new ArrayList<>();
                        Imgproc.findContours(colorsMat[colorsMat.length - 1], contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

                        //迭代轮阔，根据迭代获得最大面积，根据最大面积索引获得颜色
                        for (MatOfPoint matOfPoint : contours) {
                            double area = Imgproc.contourArea(matOfPoint);
                            if (area > maxArea) {
                                maxArea = area;
                                colorClassID = colorIndex;
                            }
                        }
                    }
                    //判断梯形是不是属于矩形
                    String shapeLabel;
                    String shapeClass = result.getLabel();
                    if (shapeClass.equals("Trapezoid") && traIsRec) {
                        shapeClass = "Rectangle";
                    }
                    //组装颜色和形状类型数据
                    shapeLabel = colorNameList.get(colorClassID) + shapeChineseLabels.get(shapeClass);
                    //读取图形数据内容，然后自增，最后覆盖数据
                    String shapeCount = String.valueOf(Integer.parseInt(SharedPreferencesUtil.queryKey2Value(shapeLabel)) + 1);
                    SharedPreferencesUtil.insert(shapeLabel, shapeCount);
                    drawLabels.add(result);
                    recSuccess = true;
                }
            }
        }
        if (recSuccess)
            HandlerUtil.sendMsg(HandlerUtil.VOICE, "找到咯");
        return recSuccess;
    }


    /**
     * 交通标志和多边形识别
     *
     * @param classID      TFT 类型
     * @param trafficSigns 是否查找交通标志
     * @param shapes       是否查找多边形
     * @param traIsRec     梯形是否归类到矩形
     */
    public static void recTrafficSignsAndShapes(byte classID, boolean trafficSigns, boolean shapes, boolean traIsRec) {
        ThreadUtil.createThread(() -> {
            ThreadUtil.sleep(SleepTimesConst.WAIT_CAMERA);
            boolean recSuccess = false;
            //迭代绘制
            List<RectResult> drawLabels = new ArrayList<>();
            Bitmap img = getImg();
            for (int p = 0; p < maxRecCount; p++) {
                if (recSuccess) {
                    break;
                } else {
                    img = getImg();
                    List<RectResult> rectResults = imageRecognition(img, TSR);
                    if (trafficSigns && shapes) {   //先查找交通标志再识别图形
                        boolean trafficSignIsTrue = parseTrafficSigns(rectResults, img, drawLabels);
                        if (trafficSignIsTrue) {
                            recSuccess = parseShapes(rectResults, img, traIsRec, drawLabels);
                        }
                    } else if (trafficSigns) {   //只查找和识别交通标志
                        recSuccess = parseTrafficSigns(rectResults, img, drawLabels);
                    } else {    //只查找和识别多边形
                        recSuccess = parseShapes(rectResults, img, traIsRec, drawLabels);
                    }
                }
                //默认翻一页，识别不到掉头再翻一页
                if (!recSuccess) {
                    tftDown(classID);
                }
            }
            drawLabels(drawLabels, img, true);
            assembleHex(classID);
            CommunicationUtil.replyCar();
        });
    }


    /**
     * 组装16进制数据
     */
    public static void assembleHex(byte classID) {
        /**
         * 黄：15
         * 待会调整二维码识别延时，现在太长了
         * TFT A往下翻页
         * TFT B码盘调小一点，撞了
         * 加个报警台
         */
        String redRec = SharedPreferencesUtil.queryKey2Value("红色矩形");
        String blueCir = SharedPreferencesUtil.queryKey2Value("蓝色圆形");
        String greenDia = SharedPreferencesUtil.queryKey2Value("绿色菱形");
        String yellowTri = SharedPreferencesUtil.queryKey2Value("黄色三角形");
        String maxCountColorFromStorage = String.valueOf(DataPcsUtil.getMaxCountColorFromStorage());
        String lastData = maxCountColorFromStorage.length() > 1 ? maxCountColorFromStorage : "0" + maxCountColorFromStorage;
        String hex = redRec + blueCir + greenDia + yellowTri + lastData;
        LogUtil.printLog("即将存储Hex" + classID + "为" + hex);
        SharedPreferencesUtil.insert(SharedPreferencesUtil.hex + classID, hex);
    }


    /**
     * 查找车牌和识别车牌内容
     * 根据车牌格式查找车牌（可选）
     *
     * @param img 图像
     * @return 车牌过滤结果
     */
    public static List<RectResult> findLP(Bitmap img) {
        List<RectResult> lpResults = new ArrayList<>();
        FileUtil.saveImg(img, "图像识别过程", "");
        Plate[] results = HyperLPR3.getInstance().plateRecognition(img, HyperLPR3.CAMERA_ROTATION_0, HyperLPR3.STREAM_BGRA);
        if (results.length < 1) {
            LogUtil.printLog("车牌识别：无结果");
            return lpResults;
        }
        String lpRegex = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.LPRegex);
        for (Plate result : results) {
            String lprContent = DataPcsUtil.capNumberLetterFilter(result.getCode());
            //过滤得到数字和大写字母，要求必须要满足 5 位
            if (lprContent.length() > 5) {
                //裁剪倒数六位车牌
                lprContent = lprContent.substring(lprContent.length() - 6);
                //更新车牌内容
                Rect rect = new Rect(new Point(result.getX1(), result.getY1()), new Point(result.getX2(), result.getY2()));
                RectResult rectResult = new RectResult(lprContent, result.getConfidence(), rect);
                LogUtil.printLog("车牌识别：" + rectResult.getLabel() + "," + rectResult.getConfidence());
                //判断是否设置了格式，设置了格式就走格式
                if (!lpRegex.isEmpty()) {
                    boolean yes = DataPcsUtil.matchLP(lprContent);
                    if (yes)
                        lpResults.add(rectResult);
                } else {
                    lpResults.add(rectResult);
                }
            }
        }
        return lpResults;
    }


    /**
     * 根据颜色查找车牌
     * 基于 findLP() 方法扩展
     * 根据车牌格式查找车牌（可选）
     * 根据颜色查找车牌（可选）
     *
     * @param img 图像
     * @return 车牌过滤结果
     */
    public static List<RectResult> findLPFromColor(Bitmap img) {
        String LPColor = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.LPColor);
        List<RectResult> results = findLP(img);
        if (LPColor.equals("无")) {
            return results;
        } else {
            List<RectResult> lpResults = new ArrayList<>();
            results.forEach(result -> {
                Mat subMat = bitmapToMat(img).submat(result.getRect());
                //转换颜色，否则无法正确识别
                Imgproc.cvtColor(subMat, subMat, Imgproc.COLOR_BGR2HSV);

                //车牌颜色阈值选择，默认绿色
                Scalar[] lpLowTh = lp_hsv_low[0];
                Scalar lpHighTh = lp_hsv_high[0];
                //根据预设颜色来判断车牌是否符合要求
                switch (LPColor) {
                    case "浅蓝色":
                        lpLowTh = lp_hsv_low[1];
                        lpHighTh = lp_hsv_high[1];
                        break;
                    case "黄色":
                        lpLowTh = lp_hsv_low[2];
                        lpHighTh = lp_hsv_high[2];
                        break;
                    case "深蓝色":
                        lpLowTh = lp_hsv_low[3];
                        lpHighTh = lp_hsv_high[3];
                        break;
                }
                for (Scalar scalar : lpLowTh) {
                    LogUtil.printSystemLog("车牌阈值", "" + Arrays.toString(scalar.val));
                }
                LogUtil.printSystemLog("车牌阈值", "" + Arrays.toString(lpHighTh.val));
                //车牌二值图
                Mat LPMat = null;
                //遍历每一个阈值并合并所有可能像素
                for (Scalar scalar : lpLowTh) {
                    Mat dstMat = new Mat();
                    Core.inRange(subMat, scalar, lpHighTh, dstMat);
                    if (LPMat == null) {
                        LPMat = dstMat.clone();
                    } else {
                        //将索引位 Mat 与最后输出位 Mat 进行 or 运算
                        Core.bitwise_or(dstMat, LPMat, LPMat);
                    }
                }
                //查找轮廓
                List<MatOfPoint> lpContours = new ArrayList<>();
                assert LPMat != null;
                Imgproc.findContours(LPMat, lpContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                double maxArea = 0;
                //迭代得到最大面积
                for (MatOfPoint point : lpContours) {
                    if (maxArea < Imgproc.contourArea(point)) {
                        maxArea = Imgproc.contourArea(point);
                    }
                }
                //判断车牌面积占比是否达到指定颜色面积的阈值
                int width = result.getRect().width;
                int height = result.getRect().height;
                LogUtil.printSystemLog("矩阵面积与车牌面积", maxArea + "," + (width * height));
                double lpATh = Double.parseDouble(SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.LPATh));
                if (lpContours.size() > 0 && (maxArea / (width * height)) > lpATh) {
                    lpResults.add(result);
                } else {
                    LogUtil.printLog("指定颜色出现的无效车牌：" + result.getLabel() + "，面积占比：" + (maxArea / (width * height)));
                }
            });
            return lpResults;
        }
    }


    /**
     * 根据车型查找车牌
     * 基于 findLPFromColor() 方法扩展
     * 根据车牌格式查找车牌（可选）
     * 根据颜色查找车牌（可选）
     * 根据车型查找车牌（可选）
     *
     * @param img 图像
     * @return 车牌过滤结果
     */
    public static List<RectResult> findLPFromVehicleType(Bitmap img) {
        //车型hex1
        String lpColor = "无";
        String vehicleType = "Car";
        List<RectResult> lastResults = new ArrayList<>();
        String hex1 = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.hex + 1);
        switch (hex1) {
            case "202301":
                lpColor = "黄色";
                vehicleType = "Motorcycle";
                break;
            case "202302":
                lpColor = "绿色";
                vehicleType = "Car";
                break;
            case "202303":
                lpColor = "蓝色";
                vehicleType = "Truck";
                break;
        }
        //查找默认车型（金手指）
        String defaultVe = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.defaultVehicleType);
        if (!defaultVe.equals("AI识别")) {
            switch (defaultVe) {
                case "摩托车":
                    lpColor = "黄色";
                    vehicleType = "Motorcycle";
                    break;
                case "轿车":
                    lpColor = "绿色";
                    vehicleType = "Car";
                    break;
                case "货车":
                    lpColor = "蓝色";
                    vehicleType = "Truck";
                    break;
            }
        }
        SharedPreferencesUtil.insert(SharedPreferencesUtil.LPColor, lpColor);
        List<RectResult> rectResults = imageRecognition(getImg(), VTR);
        for (RectResult rectResult : rectResults) {
            if (rectResult.getLabel().equals(vehicleType)) {
                List<RectResult> results = findLPFromColor(img);
                if (results.size() > 0) {
                    lastResults.add(results.get(0));
                    break;
                }
            }
        }
        return lastResults;
    }


    /**
     * 车牌查找和识别
     *
     * @param classID TFT 类型
     */
    public static void recLP(byte classID) {
        HandlerUtil.sendMsg(HandlerUtil.VOICE, "车牌查找和识别");
        ThreadUtil.createThread(() -> {
            ThreadUtil.sleep(SleepTimesConst.WAIT_CAMERA);
            boolean recSuccess = false;
            Bitmap img = getImg();
            List<RectResult> drawLabels = new ArrayList<>();
            for (int p = 0; p < maxRecCount; p++) {
                //默认翻一页，识别不到掉头再翻一页
                if (!recSuccess) {
                    tftDown(classID);
                }
                if (recSuccess) {
                    break;
                } else {
                    img = getImg();
                    if (img != null) {
                        drawLabels = findLPFromVehicleType(img);
                        if (!drawLabels.isEmpty()) {
                            drawLabels.forEach(result -> {
                                LogUtil.printLog("有效车牌" + result.getLabel());
                                SharedPreferencesUtil.insert(SharedPreferencesUtil.LPTag + classID, result.getLabel());
                            });
                            HandlerUtil.sendMsg(HandlerUtil.VOICE, "找到咯");
                            recSuccess = true;
                        }
                    } else {
                        HandlerUtil.sendMsg("未找到图像");
                    }
                }
            }
            drawLabels(drawLabels, img, false);
            CommunicationUtil.replyCar();
        });
    }


    /**
     * 破损车牌查找
     *
     * @param classID 标志物类型
     */
    public static boolean findLPFromBroken(byte classID) {
        return false;
    }

    /**
     * 国赛行人识别（先识别页面只含一个交通标志，再识别页面戴口罩行人数量，最多两张）
     *
     * @param classID
     */
    public static void recPerson2(byte classID) {
        HandlerUtil.sendMsg(HandlerUtil.VOICE, "行人识别");
        ThreadUtil.createThread(() -> {
            ThreadUtil.sleep(SleepTimesConst.WAIT_CAMERA);
            SharedPreferencesUtil.deletePersonCount();
            int maxPersonPaperCount = 2;
            int paperCount = 1;
            boolean recSuccess = false;
            List<RectResult> drawLabels = new ArrayList<>();
            Bitmap img = getImg();
            int a = 10;
            for (int p = 0; p < maxRecCount; p++) {
                //默认翻一页，识别不到掉头再翻一页
                if (!recSuccess) {
                    tftDown(classID);
                }
                if (recSuccess) {
                    break;
                } else {
                    img = getImg();
                    if (img != null) {
                        //交通标志识别
                        List<RectResult> rectResults = imageRecognition(img, TSR);
                        List<RectResult> lastResults = new ArrayList<>();
                        for (RectResult rectResult : rectResults) {
                            if (trafficSignLabels.contains(rectResult.getLabel())) {
                                lastResults.add(rectResult);
                            }
                        }
                        if (lastResults.size() == 1) {
                            drawLabels.add(lastResults.get(0));
                            SharedPreferencesUtil.insert(SharedPreferencesUtil.trafficSignTag, lastResults.get(0).getLabel());
                            paperCount = 4;
//                            Bitmap img1 = img.copy(Bitmap.Config.ARGB_8888, true);
//                            drawLabels(Collections.singletonList(lastResults.get(0)), img1, false);
//                            SharedPreferencesUtil.insert(SharedPreferencesUtil.trafficSignTag, lastResults.get(0).getLabel());
//                            List<RectResult> hbrResults = imageRecognition(img, HBR);
//                            if (!hbrResults.isEmpty()) {
//                                for (RectResult result : hbrResults) {
//                                    if (result.getLabel().equals("person")) {
//                                        //查询是否开启口罩检测
//                                        Mat subMat = bitmapToMat(img).submat(result.getRect());
//                                        List<RectResult> mrResults = imageRecognition(matToBitmap(subMat), MR);
//                                        boolean hasMask = false;
//                                        for (RectResult mrResult : mrResults) {
//                                            if (maskLabels.contains(mrResult.getLabel())) {
//                                                hasMask = true;
//                                                break;
//                                            }
//                                        }
//                                        if (hasMask) {
//                                            paperCount++;
//                                            int count = Integer.parseInt(SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.personCount));
//                                            SharedPreferencesUtil.insert(SharedPreferencesUtil.personCount, String.valueOf(count++));
//                                            drawLabels.add(result);
//                                        }
//                                    }
//                                }
//                            }
                        }
                    }
                }
                if (paperCount > maxPersonPaperCount) {
                    recSuccess = true;
                }
            }
            if (recSuccess)
                HandlerUtil.sendMsg(HandlerUtil.VOICE, "找到咯");
            drawLabels(drawLabels, img, false);
            CommunicationUtil.replyCar();
        });
    }

    /**
     * 行人识别
     *
     * @param classID TFT 类型
     */
    public static void recPerson(byte classID) {
        HandlerUtil.sendMsg(HandlerUtil.VOICE, "行人识别");
        ThreadUtil.createThread(() -> {
            ThreadUtil.sleep(SleepTimesConst.WAIT_CAMERA);
            boolean recSuccess = false;
            List<RectResult> drawLabels = new ArrayList<>();
            Bitmap img = getImg();
            for (int p = 0; p < maxRecCount; p++) {
                //默认翻一页，识别不到掉头再翻一页
                if (!recSuccess) {
                    tftDown(classID);
                }
                if (recSuccess) {
                    break;
                } else {
                    img = getImg();
                    List<RectResult> hbrResults = imageRecognition(img, HBR);
                    if (!hbrResults.isEmpty()) {
                        int personCount = 0;
                        for (RectResult result : hbrResults) {
                            if (result.getLabel().equals("person")) {
                                //查询是否开启口罩检测
                                if (!SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.personMask).equals("无")) {
                                    Mat subMat = bitmapToMat(img).submat(result.getRect());
                                    List<RectResult> mrResults = imageRecognition(matToBitmap(subMat), MR);
                                    boolean hasMask = false;
                                    for (RectResult mrResult : mrResults) {
                                        if (maskLabels.contains(mrResult.getLabel())) {
                                            hasMask = true;
                                            break;
                                        }
                                    }
                                    if (hasMask) {
                                        personCount++;
                                        drawLabels.add(result);
                                    }
                                } else {
                                    personCount++;
                                    drawLabels.add(result);
                                }
                                recSuccess = true;
                            }
                        }
                        if (personCount > 0) {
                            SharedPreferencesUtil.insert(SharedPreferencesUtil.personCount, String.valueOf(personCount));
                        }
                    }
                }
            }
            if (recSuccess)
                HandlerUtil.sendMsg(HandlerUtil.VOICE, "找到咯");
            drawLabels(drawLabels, img, false);
            CommunicationUtil.replyCar();
        });
    }


    public static void recVehicleType(byte classID) {
        HandlerUtil.sendMsg(HandlerUtil.VOICE, "车型识别");
        ThreadUtil.createThread(() -> {
            ThreadUtil.sleep(SleepTimesConst.WAIT_CAMERA);
            boolean recSuccess = false;
            List<RectResult> drawLabels = new ArrayList<>();
            Bitmap img = getImg();
            for (int p = 0; p < maxRecCount; p++) {
                //默认翻一页，识别不到掉头再翻一页
                if (!recSuccess) {
                    tftDown(classID);
                }
                if (recSuccess) {
                    break;
                } else {
                    img = getImg();
                    List<RectResult> rectResults = imageRecognition(img, VTR);
                    if (rectResults.size() == 1) {
                        String label = "";
                        RectResult rectResult = rectResults.get(0);
                        switch (rectResult.getLabel()) {
                            case "Motorcycle":
                                label = "01";
                                drawLabels.add(0, rectResult);
                                recSuccess = true;
                                break;
                            case "Car":
                                label = "02";
                                drawLabels.add(0, rectResult);
                                recSuccess = true;
                                break;
                            case "Truck":
                                label = "03";
                                drawLabels.add(0, rectResult);
                                recSuccess = true;
                                break;
                        }
                        if (recSuccess) {
                            SharedPreferencesUtil.insert(SharedPreferencesUtil.hex + 1, "2023" + label);
                        }
                    }
                }
            }
            if (recSuccess)
                HandlerUtil.sendMsg(HandlerUtil.VOICE, "找到咯");
            drawLabels(drawLabels, img, false);
            CommunicationUtil.replyCar();
        });
    }


    //单 TFT 进行多任务，跑完记得重启清标志位
    private static int tftACount = 0;
    private static int tftBCount = 0;
    private static int tftCCount = 0;

    /**
     * TFT 识别
     *
     * @param classID 标志物类型
     */
    public static void recTFT(byte classID) {
        HandlerUtil.sendMsg(HandlerUtil.VOICE, "TFT识别");
        switch (classID) {
            case A_FLAG:
                switch (tftACount) {
                    //车型识别
                    case 0:
                        recVehicleType(classID);
                        break;
                    //车牌识别
                    case 1:
                        recLP(classID);
                        break;
                }
                tftACount++;
                break;
            case B_FLAG:
                recTftChineseTextRecognition2(classID);
                tftBCount++;
                break;
            case C_FLAG:
                recPerson2(classID);
                tftCCount++;
                break;
        }
    }


    /**
     * TFT 下翻一页
     *
     * @param classID TFT 类型
     */
    public static void tftDown(byte classID) {
        HashMap<String, String> tftCtrl = new HashMap<>();
        tftCtrl.put("ctrl", "下一张");
        CommunicationUtil.tftCmd(classID, tftCtrl);
        ThreadUtil.sleep(SleepTimesConst.WAIT_TFT);
    }

}

