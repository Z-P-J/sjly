//package com.zpj.popupmenuview;
//
//import android.annotation.SuppressLint;
//import android.os.Build;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnTouchListener;
//import android.view.ViewTreeObserver.OnGlobalLayoutListener;
//import android.view.ViewTreeObserver.OnScrollChangedListener;
//import android.widget.HorizontalScrollView;
//import android.widget.ScrollView;
//
//public class UITools {
//
//    /**HorizontalScrollView添加阻尼效果
//     * ScrollView效果不太好
//     * 利用父元素的Padding给ScrollView添加弹性
//     * @param scrollView
//     * @param padding
//     */
//    public static void elasticPadding(final ScrollView scrollView, final int padding){
//        View child = scrollView.getChildAt(0);
//        //记录以前的padding
//        final int oldpt = child.getPaddingTop();
//        final int oldpb = child.getPaddingBottom();
//        //设置新的padding
//        child.setPadding(child.getPaddingLeft(), padding+oldpt, child.getPaddingRight(), padding+oldpb);
//
//        //添加视图布局完成事件监听
//        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//            private boolean inTouch = false; //手指是否按下状态
//
//            @SuppressLint("NewApi")
//            private void disableOverScroll(){
//                scrollView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
//            }
//
//            /**  滚动到顶部 */
//            private void scrollToTop(){
//                scrollView.smoothScrollTo(scrollView.getScrollX(), padding-oldpt);
//            }
//
//            /** 滚动到底部 */
//            private void scrollToBottom(){
//                scrollView.smoothScrollTo(scrollView.getScrollX(), scrollView.getChildAt(0).getBottom()-scrollView.getMeasuredHeight()-padding+oldpb);
//            }
//
//            /** 检测scrollView结束以后,复原位置 */
//            private final Runnable checkStopped = new Runnable() {
//                @Override
//                public void run() {
//                    int y = scrollView.getScrollY();
//                    int bottom = scrollView.getChildAt(0).getBottom()-y-scrollView.getMeasuredHeight();
//                    if(y <= padding && !inTouch){
//                        scrollToTop();
//                    }else if(bottom<=padding && !inTouch){
//                        scrollToBottom();
//                    }
//                }
//            };
//
//            @SuppressWarnings("deprecation")
//            @Override
//            public void onGlobalLayout() {
//                //移除监听器
//                scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                //设置最小高度
//                //scrollView.getChildAt(0).setMinimumHeight(scrollView.getMeasuredHeight());
//                //取消overScroll效果
//                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD){
//                    disableOverScroll();
//                }
//
//                scrollView.setOnTouchListener(new OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_POINTER_DOWN){
//                            inTouch = true;
//                        }else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
//                            inTouch = false;
//                            //手指弹起以后检测一次是否需要复原位置
//                            scrollView.post(checkStopped);
//                        }
//                        return false;
//                    }
//                });
//
//                scrollView.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener() {
//                    @Override
//                    public void onScrollChanged() {
//                        if(!inTouch && scrollView!=null && scrollView.getHandler()!=null){//如果持续滚动,移除checkStopped,停止滚动以后只执行一次检测任务
//                            scrollView.getHandler().removeCallbacks(checkStopped);
//                            scrollView.postDelayed(checkStopped, 100);
//                        }
//                    }
//                });
//
//                //第一次加载视图,复原位置
//                scrollView.postDelayed(checkStopped, 300);
//            }
//        });
//    }
//
//    /**
//     * 利用父元素的Padding给HorizontalScrollView添加弹性
//     * @param scrollView
//     * @param padding
//     */
//    public static void elasticPadding(final HorizontalScrollView scrollView, final int padding){
//        Log.i("", "elasticPadding>>>>!!");
//        View child = scrollView.getChildAt(0);
//
//        //记录以前的padding
//        final int oldpt = child.getPaddingTop();
//        final int oldpb = child.getPaddingBottom();
//        //设置新的padding
//        child.setPadding(padding+oldpt, child.getPaddingTop(), padding+oldpb, child.getPaddingBottom());
//
//        //添加视图布局完成事件监听
//        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//            private boolean inTouch = false; //手指是否按下状态
//
//            @SuppressLint("NewApi")
//            private void disableOverScroll(){
//                scrollView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
//            }
//
//            /**  滚动到左边 */
//            private void scrollToLeft(){
//                scrollView.smoothScrollTo(padding-oldpt, scrollView.getScrollY());
//            }
//
//            /** 滚动到底部 */
//            private void scrollToRight(){
//                scrollView.smoothScrollTo(scrollView.getChildAt(0).getRight()-scrollView.getMeasuredWidth()-padding+oldpb, scrollView.getScrollY());
//            }
//
//            /** 检测scrollView结束以后,复原位置 */
//            private final Runnable checkStopped = new Runnable() {
//                @Override
//                public void run() {
//                    int x = scrollView.getScrollX();
//                    int bottom = scrollView.getChildAt(0).getRight()-x-scrollView.getMeasuredWidth();
//                    if(x <= padding && !inTouch){
//                        scrollToLeft();
//                    }else if(bottom<=padding && !inTouch){
//                        scrollToRight();
//                    }
//                }
//            };
//
//            @SuppressWarnings("deprecation")
//            @Override
//            public void onGlobalLayout() {
//                //移除监听器
//                scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//
//                //取消overScroll效果
//                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD){
//                    disableOverScroll();
//                }
//
//                scrollView.setOnTouchListener(new OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_POINTER_DOWN){
//                            inTouch = true;
//                        }else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
//                            inTouch = false;
//                            //手指弹起以后检测一次是否需要复原位置
//                            scrollView.post(checkStopped);
//                        }
//                        return false;
//                    }
//                });
//
//                scrollView.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener() {
//                    @Override
//                    public void onScrollChanged() {
//                        //如果持续滚动,移除checkStopped,停止滚动以后只执行一次检测任务
//                        if(!inTouch && scrollView!=null && scrollView.getHandler()!=null){
//                            scrollView.getHandler().removeCallbacks(checkStopped);
//                            scrollView.postDelayed(checkStopped, 100);
//                        }
//                    }
//                });
//
//                //第一次加载视图,复原位置
//                scrollView.postDelayed(checkStopped, 300);
//            }
//        });
//    }
//}
//
