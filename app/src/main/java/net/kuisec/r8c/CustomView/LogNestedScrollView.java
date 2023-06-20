package net.kuisec.r8c.CustomView;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;


/**
 * @author Jinsn
 * @date 2022/11/3 13:37
 */
public class LogNestedScrollView extends NestedScrollView {

    int maxWidth;
    int maxHeight;

    public LogNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public LogNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LogNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        @SuppressLint("Recycle") TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LogNestedScrollView);
//        maxWidth = array.getLayoutDimension(R.styleable.LogNestedScrollView_maxWidth, maxWidth);
//        maxHeight = array.getLayoutDimension(R.styleable.LogNestedScrollView_maxHeight, maxHeight);
//        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (maxWidth > 0) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST);
        }
        if (maxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }
}
