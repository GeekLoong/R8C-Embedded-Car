package net.kuisec.r8c.CustomView;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * DialogItem点击事件
 */
public abstract class OnItemTouchListener implements RecyclerView.OnItemTouchListener {

    RecyclerView recyclerView;
    GestureDetectorCompat gestureDetectorCompat;

    public OnItemTouchListener(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.gestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(), new SimpleOnGestureListener());
    }

    /**
     * 拦截触摸事件
     * @param rv    控件
     * @param e 事件类型
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        gestureDetectorCompat.onTouchEvent(e);
        return false;
    }

    /**
     * 触摸事件
     *
     * @param rv 控件对象
     * @param e  事件类型
     */
    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        gestureDetectorCompat.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    /**
     * 配置手势检测器类
     */
    private class SimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        /**
         * 单击事件
         *
         * @param e 事件本身
         * @return 返回一个
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(child);
                onItemClick(viewHolder);
            }
            return true;
        }

        /**
         * 长按事件
         * @param e 事件本身
         */
        @Override
        public void onLongPress(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(child);
                onItemLongClick(viewHolder);
            }
        }
    }

    public abstract void onItemClick(RecyclerView.ViewHolder viewHolder);

    public abstract void onItemLongClick(RecyclerView.ViewHolder viewHolder);
}
