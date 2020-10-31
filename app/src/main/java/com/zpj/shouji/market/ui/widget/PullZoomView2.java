//package com.zpj.shouji.market.ui.widget;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.support.v4.view.ViewCompat;
//import android.support.v4.widget.NestedScrollView;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewConfiguration;
//import android.view.ViewGroup;
//import android.view.ViewTreeObserver;
//import android.widget.Scroller;
//
//import com.zpj.shouji.market.R;
//import com.zpj.utils.ScreenUtils;
//
//public class PullZoomView2 extends PullZoomView {
//
//
//    public PullZoomView2(Context context) {
//        this(context, null);
//    }
//
//    public PullZoomView2(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public PullZoomView2(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//
////        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
////            @Override
////            public void onGlobalLayout() {
////                contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
////                ViewGroup.LayoutParams params = contentView.getLayoutParams();
////                params.height = ScreenUtils.getScreenHeight(getContext()) - ScreenUtils.getStatusBarHeight(getContext()) - ScreenUtils.dp2pxInt(getContext(), 56);
////            }
////        });
//
//
////        ViewGroup.LayoutParams params = contentView.getLayoutParams();
////        params.height = ScreenUtils.getScreenHeight(getContext()) - ScreenUtils.getStatusBarHeight(getContext()) - ScreenUtils.dp2pxInt(getContext(), 56);
//
//    }
//}