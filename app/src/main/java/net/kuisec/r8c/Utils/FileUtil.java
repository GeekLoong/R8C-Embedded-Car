package net.kuisec.r8c.Utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import androidx.activity.result.ActivityResultLauncher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Jinsn
 * @date 2022/10/13 19:12
 */
public class FileUtil {

    private static boolean tftCameraFlag;
    private static ActivityResultLauncher<Intent> intentActivityResultLauncher;

    public static void init(ActivityResultLauncher<Intent> launcher) {
        intentActivityResultLauncher = launcher;
    }


    /**
     * 处理在文件管理器获得的图像文件
     *
     * @param resultData 返回数据
     * @param context    上下文
     */
    public static void acceptFileResult(Intent resultData, Context context) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(resultData.getData()));
            HandlerUtil.sendMsg(HandlerUtil.DEBUG_IMG_FLAG, bitmap);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 保存摄像头图像到手机 //Pictures/百科荣创竞赛平台/ 路径下
     *
     * @param bitmap 图像数据
     */
    public static void saveImg(Bitmap bitmap, String dirName, String fileName) {
        if (bitmap != null) {
            String state = Environment.getExternalStorageState();
            String path = Environment.getExternalStorageDirectory() + "/Pictures/百科荣创竞赛平台/" + dirName;
            if (state.equals(Environment.MEDIA_MOUNTED)) {
                File dirFile = new File(path);
                try {
                    if (!dirFile.exists()) {
                        boolean mkdirs = dirFile.mkdirs();
                    }
                    //使用时间作为文件名保存
                    String imgName = TimeUtil.getMsTime() + ".jpg";
                    //指定文件名保存，需要 fileName 不为空
                    if (!fileName.isEmpty())
                        imgName = fileName + ".jpg";
                    File img = new File(dirFile, imgName);
                    FileOutputStream fos = new FileOutputStream(img);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    HandlerUtil.sendMsg("保存图片成功，图片路径：" + path + imgName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                HandlerUtil.sendMsg("保存图片失败！");
            }
        }
    }


    /**
     * 选择图像文件
     */
    public static void selectImageFile() {
        if (intentActivityResultLauncher != null) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                intentActivityResultLauncher.launch(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                HandlerUtil.sendMsg("没有找到文件管理器");
            }
        }
    }

    /**
     * 拍照
     *
     * @param cmd 指令
     */
    public static void camera(byte classID, String cmd) {
        if (tftCameraFlag) {
            switch (cmd) {
                case "一秒一拍":
                    ThreadUtil.createThread(() -> {
                        while (true) {
                            if (!isCameraPlayFlag()) {
                                break;
                            } else {
                                FileUtil.saveImg(ImgPcsUtil.getImg(), "拍照", "");
                                ThreadUtil.sleep(1000);
                            }
                        }
                    });
                    break;
                case "拍立得":
                    saveImg(ImgPcsUtil.getImg(), "拍照", "");
                    setCameraPlayFlag(false);
                    break;
                case "TFT模式":
                    ThreadUtil.createThread(() -> {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("ctrl", "下一张");
                        int count = 0;
                        while (true) {
                            if (!isCameraPlayFlag() || count == 250) {
                                setCameraPlayFlag(false);
                                HandlerUtil.sendMsg(HandlerUtil.VOICE, "主人，我已拍照完成，请更换下一轮图片！");
                                break;
                            } else {
                                FileUtil.saveImg(ImgPcsUtil.getImg(), "拍照", "");
                                CommunicationUtil.tftCmd(classID, hashMap);
                                count++;
                                LogUtil.printLog("自动拍照", "完成第" + count + "轮拍照");
                                ThreadUtil.sleep(4000);
                            }
                        }
                    });
                    break;
            }
        }
    }

    public static boolean isCameraPlayFlag() {
        return tftCameraFlag;
    }

    /**
     * tft 拍照启动标志
     *
     * @param tftCameraFlag 拍照标志
     */
    public static void setCameraPlayFlag(boolean tftCameraFlag) {
        FileUtil.tftCameraFlag = tftCameraFlag;
    }
}
