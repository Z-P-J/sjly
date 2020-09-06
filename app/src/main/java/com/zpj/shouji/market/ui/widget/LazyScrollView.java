//package com.zpj.shouji.market.ui.widget;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.ScrollView;
//
//public class LazyScrollView extends ScrollView {
//    private static final String tag = "LazyScrollView";
//    private View view;
//
//    public LazyScrollView(Context context) {
//        this(context, null);
//    }
//
//    public LazyScrollView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public LazyScrollView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        init();
//    }
//
//    //这个获得总的高度
//    public int computeVerticalScrollRange() {
//        return super.computeHorizontalScrollRange();
//    }
//
//    public int computeVerticalScrollOffset() {
//        return super.computeVerticalScrollOffset();
//    }
//
//    private void init() {
//        this.view = getChildAt(0);
//        this.setOnTouchListener((v, event) -> {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    break;
//                case MotionEvent.ACTION_UP:
//                    if (view != null && onScrollListener != null) {
//                        postDelayed(() -> {
//                            if (view.getMeasuredHeight() <= getScrollY() + getHeight()) {
//                                if (onScrollListener != null) {
//                                    onScrollListener.onBottom();
//                                }
//
//                            } else if (getScrollY() == 0) {
//                                if (onScrollListener != null) {
//                                    onScrollListener.onTop();
//                                }
//                            } else {
//                                if (onScrollListener != null) {
//                                    onScrollListener.onScroll();
//                                }
//                            }
//                        }, 200);
//                    }
//                    break;
//
//                default:
//                    break;
//            }
//            return false;
//        });
//    }
//
////    /**
////     * 获得参考的View，主要是为了获得它的MeasuredHeight，然后和滚动条的ScrollY+getHeight作比较。
////     */
////    public void getView() {
////        this.view = getChildAt(0);
////        if (view != null) {
////            init();
////        }
////    }
//
//    /**
//     * 定义接口
//     *
//     * @author admin
//     */
//    public interface OnScrollListener {
//        void onBottom();
//
//        void onTop();
//
//        void onScroll();
//    }
//
//    private OnScrollListener onScrollListener;
//
//    public void setOnScrollListener(OnScrollListener onScrollListener) {
//        this.onScrollListener = onScrollListener;
//    }
//}
