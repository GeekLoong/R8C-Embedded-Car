package net.kuisec.r8c.Utils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 主题工具类
 */
public class ThemeUtil {
    /**
     * 亮色主题
     * @param activity 获取活动对象
     */
    public static void setLightTheme(AppCompatActivity activity) {
        activity.getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    /**
     * 暗色主题
     * @param activity 获得活动对象
     */
    public static void setDarkTheme(AppCompatActivity activity) {
        activity.getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
