package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class AutoSizeViewPager extends ViewPager {

    private static final String TAG = "AutoSizeViewPager";

    private final SparseIntArray heightArray = new SparseIntArray();

    private int lastHeight = 0;
    private float lastOffset = 0;

    private int fromItem;
    private int toItem;


    public AutoSizeViewPager(@NonNull Context context) {
        this(context, null);
    }

    public AutoSizeViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        fromItem = getCurrentItem();
        toItem = getCurrentItem();
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {
                offset = (float) position + offset;
                Log.d(TAG, "\n\nonPageScrolled position=" + position + " offset=" + offset + " lastOffset=" + lastOffset);

                int height;

                if (offset % 1 == 0) { // 判断offset是否是整数
                    fromItem = toItem = position;
                    height = getHeight(position);
                } else {
                    float min = fromItem;
                    float max = toItem;
                    if (min > max) {
                        max = fromItem;
                        min = toItem;
                    }
                    float percent;
//                    Log.d(TAG, "onPageScrolled percent=" + percent);
                    if (lastOffset < offset) { // 向左滚动
                        if (fromItem == toItem) {
                            toItem += 1;
                            percent = offset - fromItem;
                        } else {
                            if (fromItem > toItem) {
                                fromItem = (int) min;
                                toItem = (int)max;
                            }
                            percent = (offset - min) / (max - min);
                        }
                        height = (int) ((float)getHeight(fromItem)
                                + (float)(getHeight(toItem) - getHeight(fromItem)) * percent);
                    } else {
                        if (fromItem == toItem) {
                            toItem -= 1;
                            percent = offset - toItem;
                        } else {
                            if (fromItem < toItem) {
                                fromItem = (int) max;
                                toItem = (int)min;
                            }
                            percent = (offset - min) / (max - min);
                        }
                        height = (int) ((float)getHeight(toItem)
                                + (float)(getHeight(fromItem) - getHeight(toItem)) * percent);
                    }
                    lastOffset = offset;
                }
                Log.d(TAG, "onPageScrolled height=" + height + " fromItem=" + fromItem + " toItem=" + toItem);
                if (height <= 0 || lastHeight == height) {
                    return;
                }
                lastHeight = height;
                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = height;
                setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected position=" + position);
                if (toItem == position) {
                    return;
                }
                fromItem = toItem;
                toItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //下面遍历所有child的高度
        Log.d("onMeasureonMeasure", "getChildCount=" + getChildCount() + " getCurrentItem=" + getCurrentItem() + " heightMeasureSpec=" + heightMeasureSpec + " heightArray.size=" + heightArray.size());
        if (heightArray.size() != getChildCount()) {
            for (int i = 0; i < getChildCount(); i++) {
                if (heightArray.get(i, -1) == -1) {
                    View child = getChildAt(i);
                    child.measure(widthMeasureSpec,
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    int h = child.getMeasuredHeight();
                    if (h == 0) {
                        continue;
                    }
                    heightArray.put(i, h);
                }
            }
            lastHeight = heightArray.get(getCurrentItem(), lastHeight);
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(lastHeight,
                MeasureSpec.EXACTLY);
        Log.d("onMeasureonMeasure", "lastHeight=" + lastHeight + " heightMeasureSpec=" + heightMeasureSpec + "    ---------------------------------------------");

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int getHeight(int position) {
        return position >= 0 && position < heightArray.size() ? heightArray.get(position, 0) : 0;
    }

    public void setScrollerSpeed(int speed) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(), null, speed);
            mScroller.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class FixedSpeedScroller extends Scroller {

        private int mDuration = 1000;

        FixedSpeedScroller(Context context) {
            super(context);
        }

        FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        FixedSpeedScroller(Context context, Interpolator interpolator, int duration) {
            this(context, interpolator);
            mDuration = duration;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

    }

}
