//package com.zpj.shouji.market.ui.widget;
//
//import android.content.Context;
//import android.support.v4.view.ViewPager;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.animation.Interpolator;
//import android.widget.Scroller;
//
//import java.lang.reflect.Field;
//
//public class ZViewPager extends ViewPager {
//
//    private boolean isCanScroll = true;
//
//    public ZViewPager(Context context) {
//        super(context);
//    }
//
//    public ZViewPager(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public void setCanScroll(boolean isCanScroll) {
//        this.isCanScroll = isCanScroll;
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return isCanScroll && super.onInterceptTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        return isCanScroll && super.onTouchEvent(ev);
//    }
//
//    @Override
//    public boolean canScrollVertically(int direction) {
//        return isCanScroll && super.canScrollVertically(direction);
//    }
//
//    @Override
//    public boolean canScrollHorizontally(int direction) {
//        return isCanScroll && super.canScrollHorizontally(direction);
//    }
//
//    public void setScrollerSpeed(int speed) {
//        try {
//            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
//            mScroller.setAccessible(true);
//            FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(), null, speed);
//            mScroller.set(this, scroller);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static class FixedSpeedScroller extends Scroller {
//
//        private int mDuration = 1000;
//
//        FixedSpeedScroller(Context context) {
//            super(context);
//        }
//
//        FixedSpeedScroller(Context context, Interpolator interpolator) {
//            super(context, interpolator);
//        }
//
//        FixedSpeedScroller(Context context, Interpolator interpolator, int duration) {
//            this(context, interpolator);
//            mDuration = duration;
//        }
//
//        @Override
//        public void startScroll(int startX, int startY, int dx, int dy) {
//            super.startScroll(startX, startY, dx, dy, mDuration);
//        }
//
//        @Override
//        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
//            super.startScroll(startX, startY, dx, dy, mDuration);
//        }
//
//    }
//
//}
