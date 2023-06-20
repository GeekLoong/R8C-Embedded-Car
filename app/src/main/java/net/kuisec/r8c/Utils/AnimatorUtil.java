package net.kuisec.r8c.Utils;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;

import net.kuisec.r8c.CustomView.LogNestedScrollView;

/**
 * @author Jinsn
 * @date 2022/11/1 15:08
 */
public class AnimatorUtil {

    /**
     * 设置日志系统动画
     *
     * @param layout 日志系统主容器
     * @param width  宽度（像素），int[0]为初始宽度，int[2]为变化宽度
     * @param height 高度（像素），int[0]为初始高度，int[2]为变化高度
     */
    public static void setLogAnimator(LogNestedScrollView layout, int[] width, int[] height) {
        //将Activity可用尺寸作为放大后的日志页
        PropertyValuesHolder layoutHolder1 = PropertyValuesHolder.ofInt("minimumWidth", width[0], width[1]);
        PropertyValuesHolder layoutHolder2 = PropertyValuesHolder.ofInt("minimumHeight", height[0], height[1]);
        PropertyValuesHolder layoutHolder3 = PropertyValuesHolder.ofInt("maxWidth", width[0], width[1]);
        PropertyValuesHolder layoutHolder4 = PropertyValuesHolder.ofInt("maxHeight", height[0], height[1]);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(layout, layoutHolder1, layoutHolder2, layoutHolder3, layoutHolder4);
        animator.setDuration(500);
        animator.start();
    }
}
