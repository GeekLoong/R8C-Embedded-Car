package net.kuisec.r8c.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 * 悬浮窗管理工具类
 * 管理悬浮窗的生命周期
 * 管理悬浮窗的隐藏显示
 *
 * @author Jinsn
 * @date 2022/11/1 17:00
 */
public class FloatWindowsManagerUtil {
    private static final List<FloatWindowUtil> windows = new ArrayList<>();

    /**
     * 添加悬浮窗到管理组
     *
     * @param window 悬浮窗
     */
    public static void addWindow(FloatWindowUtil window) {
        windows.add(window);
    }

    /**
     * 从管理组中移除悬浮窗
     *
     * @param window 悬浮窗
     */
    public static void remove(FloatWindowUtil window) {
        windows.remove(window);
    }

    /**
     * 显示悬浮窗
     *
     * @param window 悬浮窗
     */
    public static void showWindow(FloatWindowUtil window) {
        window.showWindow();
    }

    /**
     * 显示全部悬浮窗
     */
    public static void showWindows() {
        for (FloatWindowUtil window : windows) {
            window.showWindow();
        }
    }

    /**
     * 隐藏指定悬浮窗
     *
     * @param window 悬浮窗
     */
    public static void hideWindow(FloatWindowUtil window) {
        window.hideWindow();
    }

    /**
     * 隐藏所有悬浮窗
     */
    public static void hideWindows() {
        for (FloatWindowUtil window : windows) {
            window.hideWindow();
        }
    }

}
