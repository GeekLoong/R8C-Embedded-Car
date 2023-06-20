package net.kuisec.r8c.Utils;

import android.annotation.SuppressLint;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentActivity;

import net.kuisec.r8c.R;

/**
 * 悬浮窗工具类
 *
 * @author Jinsn
 * @date 2022/10/18 12:23
 */
public class FloatWindowUtil implements View.OnClickListener {
    FragmentActivity activity;
    WindowManager windowManager;
    WindowManager.LayoutParams params;
    View view;
    ImageButton closeButton, minimizeButton, tempButton;
    int[] coordinate = {0, 0};

    public FloatWindowUtil(FragmentActivity activity) {
        this.activity = activity;
    }

    /**
     * 创建悬浮窗
     *
     * @param resource 悬浮窗布局文件
     * @return 返回一个布局，提供给监听事件
     */
    @SuppressLint("ClickableViewAccessibility")
    public View create(int resource) {
        params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.RGBA_8888;
        windowManager = activity.getWindowManager();
        view = LayoutInflater.from(activity.getApplicationContext()).inflate(resource, null, false);
        windowManager.addView(view, params);
        FloatWindowsManagerUtil.addWindow(this);
        //页面加载完成后设置监听事件
        view.post(() -> {
            view.setOnTouchListener(new FloatItemTouchListener(params, windowManager));
            view.setOnClickListener(this);
            closeButton = view.findViewById(R.id.floating_close);
            closeButton.setOnClickListener(this);
            minimizeButton = view.findViewById(R.id.floating_minimize);
            minimizeButton.setOnClickListener(this);
            //判断是否是隐藏了关闭按钮的布局
            if (closeButton.getVisibility() == View.GONE) {
                tempButton = closeButton;
            }
        });
        return view;
    }

    /**
     * 关闭窗口
     */
    public void close() {
        if (view != null) {
            windowManager.removeView(view);
        }
    }

    /**
     * 最小化窗口并居左
     */
    public void minimize(boolean control) {
        ViewGroup vp = (ViewGroup) view;
        if (!control) {
            //最大化并恢复原样
            view.post(() -> {
                params.x += coordinate[0];
                windowManager.updateViewLayout(view, params);
                //最大化后将值设置为0，防止双击造成双倍距离移动
                coordinate[0] = 0;
            });
            for (int viewCount = 0; viewCount < vp.getChildCount(); viewCount++) {
                if (vp.getChildAt(viewCount) != minimizeButton) {
                    //如果当前控件是隐藏的关闭按钮，则不显示
                    if (!vp.getChildAt(viewCount).equals(tempButton)) {
                        vp.getChildAt(viewCount).setVisibility(View.VISIBLE);
                    }
                } else {
                    minimizeButton.setImageResource(R.drawable.minimize);
                    minimizeButton.setClickable(true);
                }
            }
        } else {
            for (int viewCount = 0; viewCount < vp.getChildCount(); viewCount++) {
                if (vp.getChildAt(viewCount) != minimizeButton) {
                    vp.getChildAt(viewCount).setVisibility(View.GONE);
                } else {
                    minimizeButton.setImageResource(R.drawable.maximize);
                    minimizeButton.setClickable(false);
                }
            }
            //最小化并移动到边缘
            view.post(() -> {
                view.getLocationOnScreen(coordinate);
                params.x -= coordinate[0];
                windowManager.updateViewLayout(view, params);
            });
        }
    }

    /**
     * 隐藏窗口
     */
    public void hideWindow() {
        view.setVisibility(View.GONE);
    }

    /**
     * 显示窗口
     */
    public void showWindow() {
        view.setVisibility(View.VISIBLE);
    }

    /**
     * 悬浮窗点击事件
     *
     * @param v 视图
     */
    @Override
    public void onClick(View v) {
        //点击根布局显示所有
        if (view.equals(v)) {
            minimize(false);
        } else if (closeButton.equals(v)) {
            close();
            FloatWindowsManagerUtil.remove(this);
        } else if (minimizeButton.equals(v)) {
            minimize(true);
        }
    }


    /**
     * 悬浮窗滑动类
     */
    public static class FloatItemTouchListener implements View.OnTouchListener {
        WindowManager.LayoutParams params;
        WindowManager manager;
        int x, y;
        boolean letThrough = false;
        long tempTime = 0;

        public FloatItemTouchListener(WindowManager.LayoutParams params, WindowManager manager) {
            this.params = params;
            this.manager = manager;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    tempTime = TimeUtil.getMsTime();
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    params.x += movedX;
                    params.y += movedY;
                    manager.updateViewLayout(v, params);
                    break;
                case MotionEvent.ACTION_UP:
                    //计算按下的时间
                    long differenceTime = TimeUtil.getMsTime() - tempTime;
                    //当按下的时间不足100ms，打开被缩小的面板
                    letThrough = differenceTime >= 100;
                    break;
                default:
                    break;
            }
            return letThrough;
        }
    }
}
