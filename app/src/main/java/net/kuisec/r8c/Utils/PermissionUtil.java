package net.kuisec.r8c.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限工具类
 *
 * @author Jinsn
 */
public class PermissionUtil {

    /**
     * 获取权限
     *
     * @param context 上下文内容
     */
    public static void requestPermission(Context context) {
        //申请所有文件访问权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getOpPackageName()));
                context.startActivity(intent);
            }
        }
        //权限组
        List<String> permissions = new ArrayList<String>() {{
            add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            add(android.Manifest.permission.INTERNET);
            add(android.Manifest.permission.CHANGE_NETWORK_STATE);
            add(android.Manifest.permission.ACCESS_NETWORK_STATE);
            add(android.Manifest.permission.CHANGE_WIFI_STATE);
            add(android.Manifest.permission.ACCESS_WIFI_STATE);
            add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            add(android.Manifest.permission.READ_PHONE_STATE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                add(android.Manifest.permission.POST_NOTIFICATIONS);
        }};
        //未授权的权限个数
        List<String> unPermissions = new ArrayList<>();
        //遍历所有需要申请的权限
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(context, p) != PackageManager.PERMISSION_GRANTED) {
                unPermissions.add(p);
            }
        }
        //判断未授权的权限个数，并且全部进行申请
        if (unPermissions.size() > 0) {
            ActivityCompat.requestPermissions((Activity) context, unPermissions.toArray(new String[0]), 100);
        }
    }
}
