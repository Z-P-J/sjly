//package com.zpj.popupmenuview;
//
//import android.content.Context;
//import android.graphics.Rect;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.animation.TranslateAnimation;
//import android.widget.HorizontalScrollView;
//
///**
// * @description 具有阻尼效果的HorizontalScrollView
// *
// */
//public class dampHorizontalScrollView extends HorizontalScrollView  implements PopLayout.OnBulgeChangeCallback {
//
//    private View inner;// 子View
//
//    private float x;// 点击时y坐标
//
//    private Rect normal = new Rect();// 矩形(这里只是个形式，只是用于判断是否需要动画.)
//
//    private boolean isCount = false;// 是否开始计算
//
//    private boolean isMoveing = false;// 是否开始移动.
//
//    // private ImageView imageView;
//
//    private int initLeft;// 初始高度
//    private int left;// 拖动时时高度。
//
//    public dampHorizontalScrollView(Context context) {
//        super(context);
//    }
//
//    public dampHorizontalScrollView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    /***
//     * 根据 XML 生成视图工作完成.该函数在生成视图的最后调用，在所有子视图添加完之后. 即使子类覆盖了 onFinishInflate
//     * 方法，也应该调用父类的方法，使该方法得以执行.
//     */
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        if (getChildCount() > 0) {
//            inner = getChildAt(0);
//        }
//    }
//
//    /** touch 事件处理 **/
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (inner != null) {
//            commOnTouchEvent(ev);
//        }
//        return super.onTouchEvent(ev);
//    }
//
//    /**
//     * 滑动事件(让滑动的速度变为原来的1/2)
//     */
//    @Override
//    public void fling(int velocityY) {
//        super.fling(velocityY / 2);
//    }
//
//    /***
//     * 触摸事件
//     *
//     * @param ev
//     */
//    public void commOnTouchEvent(MotionEvent ev) {
//        int action = ev.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                break;
//
//            case MotionEvent.ACTION_UP:
//                isMoveing = false;
//                // 手指松开.
//                if (isNeedAnimation()) {
//                    animation();
//                }
//
//                break;
//            /***
//             * 排除出第一次移动计算，因为第一次无法得知y坐标， 在MotionEvent.ACTION_DOWN中获取不到，
//             * 因为此时是MyScrollView的touch事件传递到到了LIstView的孩子item上面.所以从第二次计算开始.
//             * 然而我们也要进行初始化，就是第一次移动的时候让滑动距离归0. 之后记录准确了就正常执行.
//             */
//            case MotionEvent.ACTION_MOVE:
//                final float preX = x;// 按下时的y坐标
//                float nowX = ev.getX();// 时时y坐标
//                int deltaX = (int) (nowX - preX);// 滑动距离
//                if (!isCount) {
//                    deltaX = 0; // 在这里要归0.
//                }
//                // 当滚动到最上或者最下时就不会再滚动，这时移动布局
//                isNeedMove();
//
//                if (isMoveing) {
//                    // 初始化头部矩形
//                    if (normal.isEmpty()) {
//                        // 保存正常的布局位置
//                        normal.set(inner.getLeft(), inner.getTop(),
//                                inner.getRight(), inner.getBottom());
//                    }
//
//                    // 移动布局
//                    inner.layout(inner.getLeft() + deltaX / 3, inner.getTop(),
//                            inner.getRight() + deltaX / 3, inner.getBottom());
//
//                    left += (deltaX / 6);
//                }
//
//                isCount = true;
//                x = nowX;
//                break;
//
//            default:
//                break;
//
//        }
//    }
//
//    /***
//     * 回缩动画
//     */
//    public void animation() {
//        TranslateAnimation taa = new TranslateAnimation(0, 0, left + 200,
//                initLeft + 200);
//        taa.setDuration(200);
//        TranslateAnimation ta = null;
//        // 开启移动动画
//        ta = new TranslateAnimation(inner.getLeft(), normal.left, 0, 0);
//        ta.setDuration(200);
//        inner.startAnimation(ta);
//        // 设置回到正常的布局位置
//        inner.layout(normal.left, normal.top, normal.right, normal.bottom);
//        normal.setEmpty();
//
//        isCount = false;
//        x = 0;// 手指松开要归0.
//
//    }
//
//    // 是否需要开启动画
//    public boolean isNeedAnimation() {
//        return !normal.isEmpty();
//    }
//
//    /***
//     * 是否需要移动布局 inner.getMeasuredHeight():获取的是控件的总高度
//     *
//     * getHeight()：获取的是屏幕的高度
//     *
//     * @return
//     */
//    public void isNeedMove() {
//        int scrollY = getScrollY();
//        if (scrollY == 0) {
//            isMoveing = true;
//        }
//    }
//
//    @Override
//    public void onBulgeChanged(int site, int size) {
//        if (getChildCount() > 0) {
//            View view = getChildAt(0);
//            if (view instanceof PopLayout.OnBulgeChangeCallback) {
//                ((PopLayout.OnBulgeChangeCallback) view).onBulgeChanged(site, size);
//            }
//        }
//    }
//}
//
