package com.zpj.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ZViewPager extends ViewPager {

    private boolean isCanScroll = true;
    private boolean autoSize;

    public ZViewPager(Context context) {
        super(context);
    }

    public ZViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (autoSize) {
            int height = 0;
            //下面遍历所有child的高度
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if (h > height) //采用最大的view的高度。
                    height = h;
            }

            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isCanScroll && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isCanScroll && super.onTouchEvent(ev);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return isCanScroll && super.canScrollVertically(direction);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return isCanScroll && super.canScrollHorizontally(direction);
    }

    public void setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
    }
}
