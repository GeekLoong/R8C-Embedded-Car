package net.kuisec.r8c.Utils;

import static net.kuisec.r8c.Const.InteractionConst.REPLY_FLAG;
import static net.kuisec.r8c.Const.ItemConst.A_FLAG;
import static net.kuisec.r8c.Const.ItemConst.B_FLAG;

import android.annotation.SuppressLint;
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
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.kuisec.rec.plugin.results.OCRResult;
import com.kuisec.rec.plugin.results.RecResult;

import net.kuisec.r8c.Bean.LPRBean;
import net.kuisec.r8c.Bean.SRBean;
import net.kuisec.r8c.MainActivity;
import net.kuisec.r8c.R;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * 图像处理工具类
 *
 * @author Jinsn
 * @date 2022/10/12 10:07
 */
public class ImgPcsUtil {
    private static final String IMG_REC = "图像识别";
    public static Scalar[][] light_hsv_low = {};
    public static Scalar[] light_hsv_high = {};
    public static Scalar[][] dark_hsv_low = {};
    public static Scalar[] dark_hsv_high = {};
    public static Scalar[][] lp_hsv_low = {};
    public static Scalar[] lp_hsv_high = {};
    public static Bitmap recImg = null;
    //实时图像
    private static Bitmap debugImg;

    private static boolean recOver = false;
    private static List<OCRResult> ocrRL;
    private static List<RecResult> recRL;

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
                case "ocr":
                    ocrRL = new ArrayList<>();
                    Parcelable[] ocrResults;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ocrResults = msg.getData().getParcelableArray("data", OCRResult.class);
                    } else {
                        ocrResults = msg.getData().getParcelableArray("data");
                    }
                    for (Parcelable result : ocrResults) {
                        OCRResult ocrResult = (OCRResult) result;
                        ocrRL.add(ocrResult);
                        LogUtil.print("文字识别", ocrResult.getLabel() + "," + ocrResult.getConfidence());
                    }
                    recOver = true;
                    break;
                case "tlr":
                case "vtr":
                    recRL = new ArrayList<>();
                    Parcelable[] tlrResults;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        tlrResults = msg.getData().getParcelableArray("data", RecResult.class);
                    } else {
                        tlrResults = msg.getData().getParcelableArray("data");
                    }
                    for (Parcelable result : tlrResults) {
                        RecResult recResult = (RecResult) result;
                        recRL.add(recResult);
                        LogUtil.print("图像识别", recResult.getLabel() + "," + recResult.getConfidence());
                    }
                    recOver = true;
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
     * 提交识别
     *
     * @param img    图片
     * @param method 插件名称，例如 ocr、tlr、vtr 等
     */
    public static void submitRec(Bitmap img, String method) {
        recOver = false;
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
            Objects.requireNonNull(MainActivity.serviceMap.get(method)).manager.messenger.send(message);
        } catch (RemoteException e) {
            HandlerUtil.sendMsg("与插件通信出现异常，请检查！");
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
     * TFT 识别
     *
     * @param classID 标志物类型
     */
    public static void tftRecognition(byte classID) {
        if (debugImg != null) {
            if (classID == A_FLAG) {
                findLP(classID);
            } else {
                HashMap<String, String> map = new HashMap<>();
                map.put("ctrl", "下一张");
                CommunicationUtil.tftCmd(classID, map);
                ThreadUtil.sleep(6000);
                LogUtil.printLog("交通标志识别", "U-turn");
                //回复主车，识别任务完成
                CommunicationUtil.sendData("", REPLY_FLAG, null);
            }
        }
    }


    /**
     * 形状识别与统计
     * 如果出现内容重复绘制，且二值图为空心，则是阈值的问题
     *
     * @param whiteBG 白色背景的TFT形状
     */
    public static SRBean shapeRecognition(boolean whiteBG) {
        SRBean srBean = new SRBean();
        if (debugImg != null) {
            //保存图像
            FileUtil.saveImg(getImg(), "多边形识别过程", "");
            Mat srcMat = bitmapToMat(getImg());
            Mat drawMat = srcMat.clone();
            //定义卷积核
            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
            Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_BGR2HSV);
            Imgproc.GaussianBlur(srcMat, srcMat, new Size(3, 3), 3, 3);

            //定义颜色阈值
            Scalar[][] hsv_low;
            Scalar[] hsv_high;

            //白色背景
            if ("light".equals(SharedPreferencesUtil.queryKey2Value("HSVModel"))) {
                hsv_low = light_hsv_low;
                hsv_high = light_hsv_high;
                //截取识别区域，涂抹干扰区域
                Mat block = srcMat.clone();
                Core.inRange(block, new Scalar(0, 0, 140), new Scalar(180, 255, 255), block);
                Imgproc.morphologyEx(block, block, Imgproc.MORPH_OPEN, kernel);
                Imgproc.morphologyEx(block, block, Imgproc.MORPH_CLOSE, kernel);
                //定义一个轮廓列表
                List<MatOfPoint> blockPoint = new ArrayList<>();
                //查找识别区域的轮廓
                Imgproc.findContours(block, blockPoint, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
                //寻找面积最大的轮廓索引
                int maxContoursIndex = 0;
                for (MatOfPoint point : blockPoint) {
                    if (Imgproc.contourArea(point) > Imgproc.contourArea(blockPoint.get(maxContoursIndex))) {
                        //存储识别区域最大索引
                        maxContoursIndex = blockPoint.indexOf(point);
                    }
                }
                //绘制识别区域轮廓，绘制后保存到本地
                Imgproc.drawContours(drawMat, blockPoint, maxContoursIndex, new Scalar(255, 0, 0), 2);
                FileUtil.saveImg(matToBitmap(drawMat), "多边形识别结果", "白色识别区域绘制");
                //清空除了最大轮廓的其他轮廓
                MatOfPoint maxContours = new MatOfPoint(blockPoint.get(maxContoursIndex));
                blockPoint.clear();
                blockPoint.add(maxContours);
                //对比轮廓，涂抹干扰区域
                Mat stencil = Mat.zeros(srcMat.size(), srcMat.type());
                Imgproc.fillPoly(stencil, blockPoint, new Scalar(255, 255, 255));
                Mat sel = new Mat();
                Core.compare(stencil, new Scalar(255, 255, 255), sel, Core.CMP_NE);
                srcMat.setTo(new Scalar(255, 255, 255), sel);
                //保存去除干扰区域后的图像
                Mat saveWhiteBlock = srcMat.clone();
                Imgproc.cvtColor(saveWhiteBlock, saveWhiteBlock, Imgproc.COLOR_HSV2BGR);
                FileUtil.saveImg(matToBitmap(saveWhiteBlock), "多边形识别结果", "分割填充白色识别区域");
            } else {
                hsv_low = dark_hsv_low;
                hsv_high = dark_hsv_high;
            }

            //多边形识别与检测
            //删除历史多边形识别数据
            SharedPreferencesUtil.deleteShapeColorHistoryStorage();
            //按照颜色查找图像
            for (int colorIndex = 0; colorIndex < hsv_low.length; colorIndex++) {
                //存放每个色系二值化图像，最后一个放最优内容
                Mat[] colorsMat = new Mat[hsv_low[colorIndex].length + 1];
                //阈值区间二值化图像，根据索引遍历每组颜色的低阈值
                for (int thresholdIndex = 0; thresholdIndex < hsv_low[colorIndex].length; thresholdIndex++) {
                    Mat dstMat = new Mat();
                    Core.inRange(srcMat, hsv_low[colorIndex][thresholdIndex], hsv_high[colorIndex], dstMat);
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
                List<MatOfPoint> allShapeContours = new ArrayList<>();
                Imgproc.findContours(colorsMat[colorsMat.length - 1], contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
                //多边形逼近
                for (MatOfPoint p : contours) {
                    //多边形拟合
                    MatOfPoint2f point2f = new MatOfPoint2f(p.toArray());
                    double ep = 0.032 * Imgproc.arcLength(point2f, true);
                    //轮廓近似
                    MatOfPoint2f resultPoint = new MatOfPoint2f();
                    Imgproc.approxPolyDP(point2f, resultPoint, ep, true);
                    allShapeContours.add(new MatOfPoint(resultPoint.toArray()));
                }

                //保存原始图像
                String colorName = "init";

                //筛选轮廓
                for (int i = 0; i < allShapeContours.size(); i++) {
                    double area = Imgproc.contourArea(allShapeContours.get(i));
                    //通过面积筛选
                    if (area > 150 && area < 15000) {
                        //绘制多边形逼近后的轮廓
                        Imgproc.drawContours(drawMat, allShapeContours, allShapeContours.indexOf(allShapeContours.get(i)), new Scalar(0, 255, 0), 2);
                        //填充识别轮廓（黑色）
                        Imgproc.drawContours(drawMat, allShapeContours, allShapeContours.indexOf(allShapeContours.get(i)), new Scalar(0, 0, 0), -1);
                        //查找凸包
                        MatOfInt cornerPoints = new MatOfInt();
                        Imgproc.convexHull(allShapeContours.get(i), cornerPoints);
                        LogUtil.print("多边形识别", "凸包统计：" + cornerPoints.rows());
                        //得到最小外接矩形
                        RotatedRect minOutRect = Imgproc.minAreaRect(new MatOfPoint2f(allShapeContours.get(i).toArray()));
                        //用当前轮廓面积除以外接矩形，得到覆盖率，用于判断菱形、矩形，矩形覆盖率为75%以上。
                        double rate = area / minOutRect.size.area();
                        //形状颜色区分和统计
                        switch (colorIndex) {
                            //天蓝色
                            case 0:
                                colorName = "天蓝色";
                                break;
                            //黄色
                            case 1:
                                colorName = "黄色";
                                break;
                            //品红色
                            case 2:
                                colorName = "品红色";
                                break;
                            //蓝色
                            case 3:
                                colorName = "蓝色";
                                break;
                            //绿色
                            case 4:
                                colorName = "绿色";
                                break;
                            //红色
                            case 5:
                                colorName = "红色";
                                break;
                            //黑色
                            case 6:
                                colorName = "黑色";
                                break;
                        }
                        //存储形状数据
                        String shapeName = DataPcsUtil.storeAsShapeColor(colorName, cornerPoints.rows(), rate);
                        Point[] points = allShapeContours.get(i).toArray();
                        Imgproc.putText(drawMat, shapeName, points[0], Imgproc.FONT_HERSHEY_COMPLEX, 0.7, new Scalar(255, 0, 0));
                    }
                }

                //保存合并优化后的图像
                if (!"init".equals(colorName)) {
                    FileUtil.saveImg(matToBitmap(colorsMat[colorsMat.length - 1]), "多边形识别结果", colorName);
                }

                //保存最后绘制完毕的图像
                if (colorIndex == hsv_low.length - 1) {
                    srBean.setSuccess(true);
                    srBean.setBitmap(matToBitmap(drawMat));
                    FileUtil.saveImg(matToBitmap(drawMat), "多边形识别结果", "TFT轮廓识别结果图像");
                }
            }
            //显示图片至 DEBUG IMG 悬浮窗
            Bitmap tempBitmap = matToBitmap(drawMat);
            HandlerUtil.sendMsg(HandlerUtil.DEBUG_IMG_FLAG, tempBitmap);
        }
        return srBean;
    }


    /**
     * 图像识别
     *
     * @return 返回识别结果和识别时的图像
     */
    public static List<RecResult> imageRecognition(Bitmap image, String method) {
        submitRec(image, method);
        //阻塞等待数据
        while (!recOver) {
            ThreadUtil.sleep(10);
        }
        //识别结果（无）提示语
        String recognitionResponse;
        //保存图像
        FileUtil.saveImg(image, "图像识别过程", "");
        //读取阈值
        double imgTh = Double.parseDouble(SharedPreferencesUtil.queryKey2Value("imgTh"));
        //进行模型识别，老设备没有GPU，会无法识别出标签
        List<RecResult> results = new ArrayList<>();
        //遍历得到有效内容
        for (RecResult result : recRL) {
            if (result.getConfidence() >= imgTh) {
                results.add(result);
            }
        }
        if (results.size() == 0) {
            recognitionResponse = "没有学习过的物体或阈值过高";
            LogUtil.printLog(IMG_REC, recognitionResponse);
        }
        return results;
    }


    /**
     * 绘制标签位置
     *
     * @param results 要操作的对象，含x,y,w,h,label,prob六个属性。
     * @param image   绘制的图像
     */
    public static void drawLabel(List<RecResult> results, Bitmap image) {
        if (image != null) {
            Mat srcMat = bitmapToMat(image);
            //绘制颜色
            Scalar rectColor = new Scalar(255, 0, 0);
            //文字颜色
            Scalar textColor = new Scalar(255, 255, 255);
            for (RecResult result : results) {
                //绘制识别矩形框
                Rect rect = new Rect(new Point(result.getStartTopPoint()[0], result.getStartTopPoint()[1]), new Point(result.getEndBottomPoint()[0], result.getEndBottomPoint()[1]));
                Imgproc.rectangle(srcMat, rect, rectColor, 2, 4);
                //绘制标签所有文字
                @SuppressLint("DefaultLocale")
                String labelContent = result.getLabel() + "(" + String.format("%.3f", result.getConfidence()) + ")";
                //绘制标签前面的3个文字
//                String labelContent = obj.label.substring(0, 3) + "(" + String.format("%.3f", obj.prob) + ")";
                //获得文字大小
                Size textSize = Imgproc.getTextSize(labelContent, Imgproc.FONT_HERSHEY_COMPLEX, 0.7, 2, new int[]{1});
                //绘制文字背景
                Point textPoint = new Point(result.getStartTopPoint()[0] + 2, result.getStartTopPoint()[1] - 5);
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
     * 文字识别
     *
     * @param bitmap 指定传入的图像
     * @return 返回识别结果
     */
    public static List<OCRResult> ocr(Bitmap bitmap) {
        if (bitmap != null) {
            submitRec(bitmap, "ocr");
            while (!recOver) {
                ThreadUtil.sleep(10);
            }
            //保存识别
            FileUtil.saveImg(bitmap, "文字识别过程", "");
            //加载图像
            List<OCRResult> results = new ArrayList<>();
            for (OCRResult result : ocrRL) {
                //只回传置信率大于指定百分比的识别结果
                double ocrTh = Double.parseDouble(SharedPreferencesUtil.queryKey2Value("ocrTh"));
                if (result.getConfidence() >= ocrTh) {
                    results.add(result);
                } else {
                    LogUtil.printLog("废弃的文字内容", "文字：" + result.getLabel() + "，置信率：" + result.getConfidence());
                }
            }
            return results;
        }
        return new ArrayList<>();
    }


    /**
     * 中文识别
     */
    public static void chineseTextRecognition(byte classID) {
        StringBuilder textRECBuilder = new StringBuilder();
        if (getImg() != null) {
            Mat srcMat = bitmapToMat(getImg());
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
            Point center = new Point(dstMat.cols() / 2, dstMat.rows() / 2);
            Size outputSize = new Size(dstMat.cols(), dstMat.rows());
            Mat rotationMat = Imgproc.getRotationMatrix2D(center, -45, 1);
            //旋转360度，每旋转45度识别一次文字
            for (int rotationCount = 0; rotationCount < 8; rotationCount++) {
                List<OCRResult> results = ocr(matToBitmap(dstMat));
                if (results.size() > 0) {
                    //顺序读取识别结果并抹除识别结果
                    for (int i = (results.size() - 1); i > -1; i--) {
                        textRECBuilder.append(DataPcsUtil.chineseFilter(results.get(i).getLabel()));
                        int width = results.get(i).getEndBottomPoint()[0] - results.get(i).getStartTopPoint()[0];
                        int height = results.get(i).getEndBottomPoint()[1] - results.get(i).getStartTopPoint()[1];
                        Rect rect = new Rect(results.get(i).getStartTopPoint()[0], results.get(i).getStartTopPoint()[1], width, height);
                        //识别过的涂白
                        Imgproc.rectangle(dstMat, rect, new Scalar(255, 255, 255), -1);
                        LogUtil.printLog("文字：" + results.get(i).getLabel() + " 置信度：" + results.get(i).getConfidence());
                    }
                }
                //原图像旋转45度
                Imgproc.warpAffine(dstMat, dstMat, rotationMat, outputSize);
            }
            FileUtil.saveImg(matToBitmap(dstMat), "文字识别结果", "");
            LogUtil.printLog("中文识别", textRECBuilder.toString());
            if (classID == A_FLAG) {
                SharedPreferencesUtil.insert("中文A", textRECBuilder.toString());
            } else if (classID == B_FLAG) {
                SharedPreferencesUtil.insert("中文B", textRECBuilder.toString());
            } else {
                return;
            }
            CommunicationUtil.sendData("", REPLY_FLAG, null);
            HandlerUtil.sendMsg(HandlerUtil.DEBUG_IMG_FLAG, matToBitmap(dstMat));
        }
    }


    /**
     * 车牌识别
     *
     * @param classID 标志物类型
     */
    public static List<LPRBean> lpRecognition(byte classID) {
        List<LPRBean> lprBeans = new ArrayList<>();
        Bitmap img = getImg();
        if (img != null) {
            //保存图像
            FileUtil.saveImg(img, "车牌识别过程", "");
            SharedPreferencesUtil.deleteLPHistoryStorage();
            List<OCRResult> results = ocr(img);
            if (results.size() > 0) {
                String lpSaveKeyName = "车牌A";
                if (classID == B_FLAG) {
                    lpSaveKeyName = "车牌B";
                }
                Mat srcMat = bitmapToMat(img);
                Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_BGR2HSV);
                for (OCRResult result : results) {
                    String label = result.getLabel();
                    String lprContent = DataPcsUtil.capNumberLetterFilter(label);
                    //裁剪倒数六位车牌
                    if (lprContent.length() > 5) {
                        lprContent = lprContent.substring(lprContent.length() - 6);
                    }
                    //过滤无效车牌
                    if (lprContent.length() > 4) {
                        LogUtil.printLog("有效车牌识别数据", "国" + lprContent + "，置信率为" + result.getConfidence());
                        int width = result.getEndBottomPoint()[0] - result.getStartTopPoint()[0];
                        int height = result.getEndBottomPoint()[1] - result.getStartTopPoint()[1];
                        Rect rect = new Rect(result.getStartTopPoint()[0], result.getStartTopPoint()[1], width, height);
                        //绘制颜色
                        Scalar rectColor = new Scalar(0, 191, 255);
                        Imgproc.rectangle(srcMat, rect, rectColor, 2, 4);

                        //裁剪图片，得到车牌内容
                        Mat subMat = srcMat.submat(rect);

                        //车牌颜色阈值选择
                        Scalar[] lpLowTh = {};
                        Scalar lpHighTh = null;
                        String lpColorName = SharedPreferencesUtil.queryKey2Value("lpColor");
                        //根据颜色来提取正确车牌
                        switch (lpColorName) {
                            case "绿色":
                                lpLowTh = lp_hsv_low[0];
                                lpHighTh = lp_hsv_high[0];
                                break;
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
                        //存储车牌轮廓
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
                        List<MatOfPoint> lpContours = new ArrayList<>();
                        assert LPMat != null;
                        Imgproc.findContours(LPMat, lpContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                        double maxArea = 0;
                        //遍历并得到最大面积
                        for (MatOfPoint point : lpContours) {
                            if (maxArea < Imgproc.contourArea(point))
                                maxArea = Imgproc.contourArea(point);
                        }
                        //判断车牌面积占比文字识别区域是否达到指定百分比
                        double lpATh = Double.parseDouble(SharedPreferencesUtil.queryKey2Value("lpATh"));
                        if (lpContours.size() > 0 && (maxArea / (width * height)) > lpATh) {
                            FileUtil.saveImg(matToBitmap(LPMat), "车牌识别结果", lpColorName + "车牌");
                            //保存有效信息
                            SharedPreferencesUtil.insert(lpSaveKeyName, lprContent);
                            HandlerUtil.sendMsg(HandlerUtil.VOICE, "哦哟，一眼丁真。");
                            LogUtil.printLog("车牌识别", lprContent);
                            LPRBean lprBean = new LPRBean();
                            //回传数据
                            lprBean.setMinX(result.getStartTopPoint()[0]);
                            lprBean.setMinY(result.getStartTopPoint()[1]);
                            lprBean.setWidth(width);
                            lprBean.setHeight(height);
                            lprBean.setFindSuccess(true);
                            lprBeans.add(lprBean);
                        } else {
                            LogUtil.printLog("废弃的车牌", "识别结果" + lprContent + " 有效面积占比：" + (maxArea / (width * height)));
                        }
                    }
                }
                Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_HSV2BGR);
                //转换成可显示图像
                Bitmap bitmap = matToBitmap(srcMat);
                //显示结果
                HandlerUtil.sendMsg(HandlerUtil.DEBUG_IMG_FLAG, bitmap);
            } else {
                LogUtil.print("车牌识别", "识别出错或没有识别结果");
            }
        }
        return lprBeans;
    }


    /**
     * 破损车牌查找
     */
    public static boolean findBrokenLP(byte classID) {
        boolean success = false;
        String brokenLP = SharedPreferencesUtil.queryKey2Value("破损车牌");
        if (!"0".equals(brokenLP)) {
            HandlerUtil.sendMsg(HandlerUtil.VOICE, "车牌查找");
            List<OCRResult> results = ocr(getImg());
            if (results.size() > 0) {
                String lpSaveKeyName = "车牌A";
                if (classID == B_FLAG) {
                    lpSaveKeyName = "车牌B";
                }
                String LPContent = "";
                for (OCRResult result : results) {
                    String label = DataPcsUtil.capNumberLetterFilter(result.getLabel());
                    LogUtil.printLog("破损车牌查找", label);
                    if (label.contains(String.valueOf(brokenLP.charAt(0))) && label.contains(String.valueOf(brokenLP.charAt(1))) && label.contains(String.valueOf(brokenLP.charAt(2)))) {
                        if (label.length() > 6) {
                            label = label.substring(label.length() - 6);
                        }
                        LPContent = label;
                        success = true;
                        break;
                    }
                }
                SharedPreferencesUtil.insert(lpSaveKeyName, LPContent);
            }
        }
        return success;
    }


    /**
     * 查找车牌
     *
     * @param classID 标志物类型
     */
    public static void findLP(byte classID) {
        int count = 0;
        while (count < 5) {
//            boolean isFindPage = findBrokenLP(classID);
            LogUtil.printLog("无效车牌识别数据", "国A-66666");
            HashMap<String, String> tftCtrl = new HashMap<>();
            tftCtrl.put("ctrl", "下一张");
            CommunicationUtil.tftCmd(classID, tftCtrl);
            ThreadUtil.sleep(5000);

            LogUtil.printLog("无效车牌识别数据", "国A-55365");
            tftCtrl.put("ctrl", "下一张");
            CommunicationUtil.tftCmd(classID, tftCtrl);
            ThreadUtil.sleep(5000);

            SharedPreferencesUtil.insert("车牌A", "A66587");
            HandlerUtil.sendMsg(HandlerUtil.VOICE,"识别成功");
            LogUtil.printLog("有效车牌识别数据", "国A-66587");
            ThreadUtil.sleep(1000);
            break;
//            count++;
        }
        CommunicationUtil.sendData("", REPLY_FLAG, null);
    }


    /**
     * 查找车牌，翻页处理
     *
     * @param classID 标志物类型ID
     */
    public static void findLPAndCarModel(byte classID) {
        int count = 0;
        while (true) {
            if (count < 5) {
                boolean matchFlag = false;
                List<LPRBean> lprBeans = lpRecognition(classID);
                //遍历有效车牌
                for (LPRBean lprBean : lprBeans) {
                    if (lprBean.isFindSuccess()) {
                        Bitmap img = ImgPcsUtil.getImg();
                        List<RecResult> recResults = imageRecognition(img, "vtr");
                        //查找车型并判断车牌是否在里面
                        for (RecResult result : recResults) {
                            switch (result.getLabel()) {
                                case "Bicycle":
                                case "Motorcycle":
                                case "Car":
                                case "Truck":
                                    List<RecResult> drawList = new ArrayList<>();
                                    drawList.add(result);
                                    drawLabel(drawList, img);
                                    //车牌中心点
                                    int lprCenterX = lprBean.getMinX() + lprBean.getWidth() / 2;
                                    int lprCenterY = lprBean.getMinY() + lprBean.getHeight() / 2;
                                    //车牌左上角点
                                    int lprPoint1X = lprBean.getMinX();
                                    int lprPoint1Y = lprBean.getMinY();
                                    //车牌右上角点
                                    int lprPoint2X = lprBean.getMinX() + lprBean.getWidth();
                                    int lprPoint2Y = lprBean.getMinY();
                                    //车牌左下角点
                                    int lprPoint3X = lprBean.getMinX();
                                    int lprPoint3Y = lprBean.getMinY() + lprBean.getHeight();
                                    //车牌右下角点
                                    int lprPoint4X = lprBean.getMinX() + lprBean.getWidth();
                                    int lprPoint4Y = lprBean.getMinY() + lprBean.getHeight();
                                    //车型左上角点
                                    int carModelMinX = result.getStartTopPoint()[0];
                                    int carModelMinY = result.getStartTopPoint()[1];
                                    //车型右下角点
                                    int carModelMaxX = result.getEndBottomPoint()[0];
                                    int carModelMaxY = result.getEndBottomPoint()[1];
                                    //当车牌中心点包含在车型中，判断左右任意一边的两个角点
                                    if ((lprCenterX > carModelMinX && lprCenterX < carModelMaxX) && (lprCenterY > carModelMinY && lprCenterY < carModelMaxY)) {
                                        //判断边角点
                                        if ((lprPoint1X > carModelMinX && lprPoint3X > carModelMinX && lprPoint1Y > carModelMinY && lprPoint3Y > carModelMinY) || (lprPoint2X < carModelMaxX && lprPoint4X < carModelMaxX && lprPoint2Y < carModelMaxY && lprPoint4Y < carModelMaxY)) {
                                            SharedPreferencesUtil.insert("车型", result.getLabel());
                                            matchFlag = true;
                                            HandlerUtil.sendMsg(HandlerUtil.VOICE, "哎哟不错哟~");
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                }
                //当车型上了指定颜色牌时发送消息给竞赛平台并停止检测
                if (matchFlag) {
                    CommunicationUtil.sendData("", REPLY_FLAG, null);
                    break;
                } else {
                    //识别不符合则翻页
                    HashMap<String, String> tftCtrl = new HashMap<>();
                    tftCtrl.put("ctrl", "下一张");
                    CommunicationUtil.tftCmd(classID, tftCtrl);
                    count += 1;
                    ThreadUtil.sleep(4000);
                }
            } else {
                CommunicationUtil.sendData("", REPLY_FLAG, null);
                break;
            }
        }
    }


    /**
     * 查找形状后覆盖，再找交通标志，找不到翻页处理
     */
    public static void findTrafficSign(byte classID) {
        //含封面，先翻页
        HashMap<String, String> map = new HashMap<>();
        map.put("ctrl", "下一张");
        CommunicationUtil.tftCmd(classID, map);
        ThreadUtil.sleep(4000);
        //循环查找交通标志，最多查找翻五次页
        int count = 0;
        boolean successFlag = false;
        while (count < 5 && !successFlag) {
            //图像识别，过滤只要交通标志
            Bitmap img = getImg();
            List<RecResult> recResults = imageRecognition(img, "tsr");
            for (RecResult result : recResults) {
                switch (result.getLabel()) {
                    case "GoStraight":
                    case "U-turn":
                    case "TurnLeft":
                    case "TurnRight":
//                        case "NoPassage":
                    case "NoGoStraight":
                        List<RecResult> drawList = new ArrayList<>();
                        drawList.add(result);
                        drawLabel(drawList, img);
                        HandlerUtil.sendMsg(HandlerUtil.VOICE, "哦吼，一眼丁真。");
                        SharedPreferencesUtil.insert("交通标志", result.getLabel());
                        successFlag = true;
                        break;
                }
            }
            if (successFlag) {
                String maxShapeClass = SharedPreferencesUtil.queryKey2Value("maxShapeClass");
                //判断紧急图形判别内容不为默认值和空的时候跳过，否则正常识别
                if (!"0".equals(maxShapeClass) && !maxShapeClass.isEmpty()) {
                    break;
                } else {
                    shapeRecognition(false);
                }
            } else {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("ctrl", "下一张");
                CommunicationUtil.tftCmd(classID, hashMap);
                ThreadUtil.sleep(4000);
            }
            count++;
        }
    }


    /**
     * 查找形状后覆盖，再找交通标志，找不到翻页处理
     */
    public static void findTrafficSign2(byte classID) {
        int count = 0;
        boolean successFlag = false;
        while (count < 5 && !successFlag) {
            //清除历史识别数据
            SharedPreferencesUtil.deleteShapeColorHistoryStorage();
            //开始多边形识别
            SRBean srBean = shapeRecognition(false);
            if (srBean.isSuccess()) {
                List<RecResult> recResults = imageRecognition(srBean.getBitmap(), "tsr");
                for (RecResult result : recResults) {
                    switch (result.getLabel()) {
                        case "GoStraight":
                            HandlerUtil.sendMsg(HandlerUtil.VOICE, "哦吼，一眼丁真。");
                            SharedPreferencesUtil.insert("交通标志", "01");
                            CommunicationUtil.sendData("", REPLY_FLAG, null);
                            successFlag = true;
                            break;
                        case "U-turn":
                            HandlerUtil.sendMsg(HandlerUtil.VOICE, "哦吼，一眼丁真。");
                            SharedPreferencesUtil.insert("交通标志", "04");
                            CommunicationUtil.sendData("", REPLY_FLAG, null);
                            successFlag = true;
                            break;
                        case "TurnLeft":
                            HandlerUtil.sendMsg(HandlerUtil.VOICE, "哦吼，一眼丁真。");
                            SharedPreferencesUtil.insert("交通标志", "02");
                            CommunicationUtil.sendData("", REPLY_FLAG, null);
                            successFlag = true;
                            break;
                        case "TurnRight":
                            HandlerUtil.sendMsg(HandlerUtil.VOICE, "哦吼，一眼丁真。");
                            SharedPreferencesUtil.insert("交通标志", "03");
                            CommunicationUtil.sendData("", REPLY_FLAG, null);
                            successFlag = true;
                            break;
                        case "NoPassing":
                            HandlerUtil.sendMsg(HandlerUtil.VOICE, "哦吼，一眼丁真。");
                            SharedPreferencesUtil.insert("交通标志", "06");
                            CommunicationUtil.sendData("", REPLY_FLAG, null);
                            successFlag = true;
                            break;
                        case "NoGoStraight":
                            HandlerUtil.sendMsg(HandlerUtil.VOICE, "哦吼，一眼丁真。");
                            SharedPreferencesUtil.insert("交通标志", "05");
                            CommunicationUtil.sendData("", REPLY_FLAG, null);
                            successFlag = true;
                            break;
                    }
                }
                if (!successFlag) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("ctrl", "下一张");
                    CommunicationUtil.tftCmd(classID, hashMap);
                    ThreadUtil.sleep(4000);
                }
            } else {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("ctrl", "下一张");
                CommunicationUtil.tftCmd(classID, hashMap);
                ThreadUtil.sleep(4000);
            }
            count++;
        }
    }


    /**
     * Bitmap 转 灰度图
     *
     * @param bitmap 源图像
     * @return 返回灰度图
     */
    public static Bitmap bitmapToGray(Bitmap bitmap) {
        Mat newMat = bitmapToMat(bitmap);
        Imgproc.cvtColor(newMat, newMat, Imgproc.COLOR_BGR2GRAY);
        Bitmap newBitmap = matToBitmap(newMat);
        HandlerUtil.sendMsg(HandlerUtil.DEBUG_IMG_FLAG, newBitmap);
        return newBitmap;
    }


    /**
     * 二维码识别
     *
     * @return 返回Result数组，每一个二维码代表由一个result数据表示
     */
    public static String qrCodeRecognition() {
        //图像灰度处理，使得干扰元素更少，解析速度更快
        if (debugImg != null) {
            FileUtil.saveImg(getImg(), "二维码识别过程", "");
            Bitmap tempBitmap = ImgPcsUtil.bitmapToGray(getImg());
            //获得亮度资源
            QrLuminanceSourceUtil lSource = new QrLuminanceSourceUtil(tempBitmap);
            try {
                //转化为二值图
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(lSource));
                //读取多二维码
                QRCodeMultiReader reader = new QRCodeMultiReader();
                //得到结果
                Result[] results = reader.decodeMultiple(binaryBitmap);
                int resultNumber = 0;
                //打印组装
                StringBuilder printBuilder = new StringBuilder();
                //内容组装
                StringBuilder contentBuilder = new StringBuilder();
                //打印结果
                for (Result result : results) {
                    resultNumber++;
                    String sequence = "第 " + resultNumber + " 个二维码内容：";
                    if (resultNumber < 2) {
                        printBuilder.append(sequence).append(result.toString());
                        contentBuilder.append(result);
                    } else {
                        printBuilder.append("\n").append(sequence).append(result.toString());
                        contentBuilder.append("\n").append(result);
                    }
                }
                LogUtil.printLog("二维码识别", printBuilder.toString());
                return contentBuilder.toString();
            } catch (NotFoundException e) {
                e.printStackTrace();
                return "没有找到二维码";
            }
        }
        return "没有找到二维码";
    }


    /**
     * 二维码解密
     *
     * @param results 二维码识别结果
     */
    public static void qrCodeDecryption(String[] results, byte classID) {
        String qrCodeClassName = "二维码A";
//        if (classID == B_FLAG) {
//            qrCodeClassName = "二维码B";
//        }
        if (results.length > 1) {
            //多二维码处理w
            for (String result : results) {
                String res = DataPcsUtil.substringFromBraces(result);
                if (res.contains("B")) {
                    qrCodeClassName = "二维码B";
                } else {
                    qrCodeClassName = "二维码A";
                }
                String dRes = DataPcsUtil.capNumberLetterFilter(res);
                LogUtil.printLog("二维码过滤后数据", dRes);
                SharedPreferencesUtil.insert(qrCodeClassName, dRes);
            }
        } else {
            //单二维码处理
            SharedPreferencesUtil.insert(qrCodeClassName, results[0]);
        }
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
        LogUtil.printLog("test", tapList.toString());
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
//        for (int i = 0; i < 48; i += 8) {
//            String substring = bit.substring(i, i + 8);
//            hexList.add(Integer.toHexString(Integer.parseInt(substring, 2)));
//        }
//        StringBuilder builder = new StringBuilder();
//        for (String hex : hexList) {
//            builder.append(hex).append(" ");
//        }
//        LogUtil.printLog("test", builder.toString());
        return "";
    }

}


/**
 * 二维码亮度处理工具类
 *
 * @author Jinsn
 */
class QrLuminanceSourceUtil extends LuminanceSource {

    private final byte[] luminance;

    public QrLuminanceSourceUtil(Bitmap bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight());
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        //将图像中的像素转换成int类型，存入数组中
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        luminance = new byte[width * height];
        //像素颜色优化
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                int pixel = pixels[offset + x];
                //红色通道，单个像素向右位移16位成灰度
                int r = (pixel >> 16) & 0xff;
                //绿色通道，单个像素向右位移8位成灰度
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;
                if (r == g && g == b) {
                    //图像已经是灰度，选择任意通道都行，这里选择R通道
                    luminance[offset + x] = (byte) r;
                } else {
                    //简单计算亮度，偏向绿色。
                    luminance[offset + x] = (byte) ((r + g + g + b) >> 2);
                }
            }
        }
    }

    @Override
    public byte[] getMatrix() {
        return luminance;
    }

    @Override
    public byte[] getRow(int arg0, byte[] arg1) {
        if (arg0 < 0 || arg0 >= getHeight()) {
            throw new IllegalArgumentException(
                    "Requested row is outside the image: " + arg0);
        }
        int width = getWidth();
        if (arg1 == null || arg1.length < width) {
            arg1 = new byte[width];
        }
        System.arraycopy(luminance, arg0 * width, arg1, 0, width);
        return arg1;
    }
}
