package com.zpj.fragmentation.dialog.base;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.zpj.fragmentation.dialog.animator.PopupAnimator;
import com.zpj.fragmentation.dialog.animator.ScaleAlphaAnimator;
import com.zpj.fragmentation.dialog.enums.PopupPosition;
import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.model.OptionMenu;
import com.zpj.fragmentation.dialog.widget.PopLayout;
import com.zpj.utils.ScreenUtils;


public abstract class ArrowDialogFragment extends BaseDialogFragment {

    private static final String TAG = "ArrowDialogFragment";

    protected int defaultOffsetY = 0;
    protected int defaultOffsetX = 0;

    protected boolean isCenterHorizontal = false;

    protected View attachView;
    protected PointF touchPoint = null;

    protected PopupPosition popupPosition = null;

    protected PopLayout mPopLayout;



    private ViewGroup contentView;


    @Override
    protected final int getImplLayoutId() {
        return R.layout._dialog_layout_arrow_view;
    }

    protected abstract int getContentLayoutId();

    @Override
    protected PopupAnimator getDialogAnimator(ViewGroup contentView) {
        return null;
    }

    @Override
    protected int getGravity() {
        return Gravity.NO_GRAVITY;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        if (attachView == null && touchPoint == null) {
//            throw new IllegalArgumentException("atView() or touchPoint must not be null for AttachPopupView ！");
            dismiss();
            return;
        }

        getImplView().setAlpha(0f);

        mPopLayout = findViewById(R.id.arrowPopupContainer);

        if (getContentLayoutId() > 0) {
            contentView = (ViewGroup) LayoutInflater.from(context).inflate(getContentLayoutId(), mPopLayout, false);
            mPopLayout.addView(contentView);
        }

        if (bgDrawable != null) {
            mPopLayout.setBackground(bgDrawable);
        }

    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mPopLayout
//                .getViewTreeObserver()
//                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        mPopLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                        show(attachView, null, touchPoint);
//                    }
//                });
//    }

    @Override
    public void doShowAnimation() {
        super.doShowAnimation();
        show(attachView, null, touchPoint);
    }

    public void show(View anchor, RectF frame, PointF origin) {

        if (origin == null) {
            origin = new PointF(-1, -1);
        }
        if (frame == null) {
            frame = new RectF();
        }


        int[] location = reviseFrameAndOrigin(anchor, frame, origin);

        int x = location[0], y = location[1];
        int width = anchor.getWidth(), height = anchor.getHeight();
        int contentWidth = mPopLayout.getMeasuredWidth(), contentHeight = mPopLayout.getMeasuredHeight();
        Log.d("whwhwhwhwh", "width=" + width + "   height=" + height);

        PointF offset = getOffset(frame, new Rect(x, y + height, contentWidth + x,
                contentHeight + y + height), x + origin.x, y + origin.y);

        float top = y - contentHeight - frame.top;
        float left = x - contentWidth - frame.left;
        float right = frame.right - x - width - contentWidth;
        float bottom = frame.bottom - y - height - contentHeight;
        Log.d("whwhwhwhwh", "top=" + top + "   left=" + left + "  right=" + right + "  bottom=" + bottom);
        float max1 = Math.max(top, bottom);
        float max2 = Math.max(right, left);
        float max = Math.max(max1, max2);
        Log.d("whwhwhwhwh", "max1=" + max1 + "   max2=" + max2 + "  max=" + max);
        int offsetY = ScreenUtils.getScreenHeight(context) - getRootView().getMeasuredHeight();
        if (max == bottom) {
            Log.d("whwhwhwhwh", "showAtBottom");
            showAtBottom(anchor, origin, offset.x, -offsetY);
        } else if (max == top) {
            Log.d("whwhwhwhwh", "showAtTop");
            showAtTop(anchor, origin, offset.x, -height - contentHeight - offsetY);
        } else if (bottom > 0) {
            Log.d("whwhwhwhwh", "showAtBottom");
            showAtBottom(anchor, origin, offset.x, -offsetY);
        } else if (top > 0) {
            Log.d("whwhwhwhwh", "showAtTop");
            showAtTop(anchor, origin, offset.x, -height - contentHeight - offsetY);
        } else if (max == right) {
            Log.d("whwhwhwhwh", "showAtRight");
            showAtRight(anchor, origin, width, offset.y - offsetY);
        } else {
            Log.d("whwhwhwhwh", "showAtLeft");
            showAtLeft(anchor, origin, -contentWidth, offset.y - offsetY);
        }
    }

    public void showAtTop(View anchor, PointF origin, float xOff, float yOff) {
        mPopLayout.setSiteMode(PopLayout.SITE_BOTTOM);
        mPopLayout.setOffset(origin.x - xOff);
//        show(anchor, xOff, yOff, PopLayout.SITE_BOTTOM);
        show(anchor, xOff, yOff, origin.x - xOff, mPopLayout.getMeasuredHeight());
    }

    public void showAtLeft(View anchor, PointF origin, float xOff, float yOff) {
        mPopLayout.setSiteMode(PopLayout.SITE_RIGHT);
        mPopLayout.setOffset(-origin.y - yOff);
//        show(anchor, xOff, yOff, PopLayout.SITE_RIGHT);
        show(anchor, xOff, yOff, mPopLayout.getMeasuredWidth(), -origin.y - yOff);
    }

    public void showAtRight(View anchor, PointF origin, float xOff, float yOff) {
        mPopLayout.setSiteMode(PopLayout.SITE_LEFT);
        mPopLayout.setOffset(-origin.y - yOff);
//        show(anchor, xOff, yOff, PopLayout.SITE_LEFT);
        show(anchor, xOff, yOff, 0, -origin.y - yOff);
    }

    public void showAtBottom(View anchor, PointF origin, float xOff, float yOff) {
        mPopLayout.setSiteMode(PopLayout.SITE_TOP);
        mPopLayout.setOffset(origin.x - xOff);
//        show(anchor, xOff, yOff, PopLayout.SITE_TOP);
        show(anchor, xOff, yOff, origin.x - xOff, 0);
    }

    private void show(View anchor, float xOff, float yOff, float pivotX, float pivotY) {
        Log.d(TAG, "getMeasuredHeight=" + mPopLayout.getMeasuredHeight() + " getMeasuredWidth=" + mPopLayout.getMeasuredWidth());
        Log.d(TAG, "xOff=" + xOff + " yOff=" + yOff);
        final int[] screenLocation = new int[2];
        anchor.getLocationOnScreen(screenLocation);
        Log.d(TAG, "screenLocation[0]=" + screenLocation[0] + " screenLocation[1]=" + screenLocation[1]);
        float x = screenLocation[0] + xOff;
        float y = screenLocation[1] + anchor.getMeasuredHeight() + yOff;
        Log.d(TAG, "x=" + x + " y=" + y);
        getImplView().post(() -> {
            getImplView().setTranslationX(x);
            getImplView().setTranslationY(y);
            getImplView().setAlpha(1f);
            popupContentAnimator = new ScaleAlphaAnimator(getImplView(), pivotX, pivotY);
            popupContentAnimator.initAnimator();
            popupContentAnimator.animateShow();
        });
    }

//    private void show(View anchor, float xOff, float yOff, int mode) {
//        Log.d(TAG, "getMeasuredHeight=" + mPopLayout.getMeasuredHeight() + " getMeasuredWidth=" + mPopLayout.getMeasuredWidth());
//        Log.d(TAG, "xOff=" + xOff + " yOff=" + yOff);
//        final int[] screenLocation = new int[2];
//        anchor.getLocationOnScreen(screenLocation);
//        Log.d(TAG, "screenLocation[0]=" + screenLocation[0] + " screenLocation[1]=" + screenLocation[1]);
//        float x = screenLocation[0] + xOff;
//        float y = screenLocation[1] + anchor.getMeasuredHeight() + yOff;
//        Log.d(TAG, "x=" + x + " y=" + y);
//        getImplView().post(() -> {
//            getImplView().setTranslationX(x);
//            getImplView().setTranslationY(y);
//            getImplView().setAlpha(1f);
//            float pivotX;
//            float pivotY;
//            switch (mode) {
//                case PopLayout.SITE_LEFT:
//                    pivotX = screenLocation[0] + anchor.getMeasuredWidth();
//                    pivotY = screenLocation[1] + anchor.getMeasuredHeight() / 2f;
//                    break;
//                case PopLayout.SITE_TOP:
//                    pivotX = screenLocation[0] + anchor.getMeasuredWidth() / 2f;
//                    pivotY = screenLocation[1] + anchor.getMeasuredHeight();
//                    break;
//                case PopLayout.SITE_RIGHT:
//                    pivotX = screenLocation[0];
//                    pivotY = screenLocation[1] + anchor.getMeasuredHeight() / 2f;
//                    break;
//                case PopLayout.SITE_BOTTOM:
//                    pivotX = screenLocation[0] + anchor.getMeasuredWidth() / 2f;
//                    pivotY = screenLocation[1];
//                    break;
//                default:
//                    pivotX = screenLocation[0] + anchor.getMeasuredWidth() / 2f;
//                    pivotY = screenLocation[1] + anchor.getMeasuredHeight() / 2f;
//                    break;
//            }
//            popupContentAnimator = new ScaleAnimator(getImplView(), pivotX, pivotY);
//            popupContentAnimator.initAnimator();
//            popupContentAnimator.animateShow();
//        });
//    }

    public int[] reviseFrameAndOrigin(View anchor, RectF frame, PointF origin) {
        int[] location = new int[2];
        anchor.getLocationInWindow(location);

        int l1 = location[0];
        int l2 = location[1];
        if (l1 == 0 || l2 == 0) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            location[0] = rect.left;
            location[1] = rect.top;
        }

        if (origin.x < 0 || origin.y < 0) {
            origin.set(anchor.getWidth() >> 1, anchor.getHeight() >> 1);
        }

        if (frame.isEmpty() || !frame.contains(origin.x + location[0], origin.y + location[1])) {
            Rect rect = new Rect();
            anchor.getWindowVisibleDisplayFrame(rect);
            frame.set(rect);
        }

        return location;
    }

    protected PointF getOffset(RectF frame, Rect rect, float x, float y) {
        RectF rt = new RectF(rect);
        rt.offset(x - rt.centerX(), y - rt.centerY());
        Log.d("getOffset", "frame=" + frame.toString());
        Log.d("getOffset", "rt=" + rt.toString());
        if (!frame.contains(rt)) {
            float offsetX = 0, offsetY = 0;
            if (rt.bottom > frame.bottom) {
                offsetY = frame.bottom - rt.bottom;
            } else if (rt.top < frame.top) {
                offsetY = frame.top - rt.top;
            }
//            offsetX = Math.max(frame.bottom - rt.bottom, frame.top - rt.top);
            Log.d("getOffset", "offsetY111111111=" + (frame.bottom - rt.bottom));
            Log.d("getOffset", "offsetY2222222222=" + (frame.top - rt.top));

            if (rt.right > frame.right) {
                offsetX = frame.right - rt.right;
            } else if (rt.left < frame.left) {
                offsetX = frame.left - rt.left;
            }

            Log.d("getOffset", "offsetX1111111111=" + (frame.right - rt.right));
            Log.d("getOffset", "offsetX2222222222=" + (frame.left - rt.left));

            Log.d("getOffset", "offsetX=" + offsetX);
            Log.d("getOffset", "offsetY=" + offsetY);
            rt.offset(offsetX, offsetY);
        }
        return new PointF(rt.left - rect.left, rt.top - rect.top);
    }

    public ViewGroup getContentView() {
        return contentView;
    }

    public ArrowDialogFragment setAttachView(View attachView) {
        this.attachView = attachView;
        return this;
    }

    public ArrowDialogFragment setTouchPoint(PointF touchPoint) {
        this.touchPoint = touchPoint;
        return this;
    }

    public ArrowDialogFragment setTouchPoint(float x, float y) {
        this.touchPoint = new PointF(x, y);
        ;
        return this;
    }

    public ArrowDialogFragment setCenterHorizontal(boolean centerHorizontal) {
        isCenterHorizontal = centerHorizontal;
        return this;
    }

    public ArrowDialogFragment setPopupPosition(PopupPosition popupPosition) {
        this.popupPosition = popupPosition;
        return this;
    }

    public ArrowDialogFragment setDefaultOffsetX(int defaultOffsetX) {
        this.defaultOffsetX = defaultOffsetX;
        return this;
    }

    public ArrowDialogFragment setDefaultOffsetY(int defaultOffsetY) {
        this.defaultOffsetY = defaultOffsetY;
        return this;
    }

//    public static class TranslateSelfAnimator extends PopupAnimator {
//        //动画起始坐标
//        private float startTranslationX, startTranslationY;
//        private int oldWidth, oldHeight;
//        private float initTranslationX, initTranslationY;
//        private boolean hasInitDefTranslation = false;
//
//        public TranslateSelfAnimator(View target, PopupAnimation popupAnimation) {
//            super(target, popupAnimation);
//        }
//
//        @Override
//        public void initAnimator() {
//            if(!hasInitDefTranslation){
//                initTranslationX = targetView.getTranslationX();
//                initTranslationY = targetView.getTranslationY();
//                hasInitDefTranslation = true;
//            }
//            targetView.setAlpha(0);
//            // 设置起始坐标
//            applyTranslation();
//            startTranslationX = targetView.getTranslationX();
//            startTranslationY = targetView.getTranslationY();
//
//            oldWidth = targetView.getMeasuredWidth();
//            oldHeight = targetView.getMeasuredHeight();
//        }
//
//        private void applyTranslation() {
//            switch (popupAnimation) {
//                case TranslateFromLeft:
//                    targetView.setTranslationX(targetView.getTranslationX() - targetView.getMeasuredWidth());
//                    break;
//                case TranslateFromTop:
//                    targetView.setTranslationY(targetView.getTranslationY() - targetView.getMeasuredHeight());
//                    break;
//                case TranslateFromRight:
//                    targetView.setTranslationX(targetView.getTranslationX() + targetView.getMeasuredWidth());
//                    break;
//                case TranslateFromBottom:
//                    targetView.setTranslationY(targetView.getTranslationY() + targetView.getMeasuredHeight());
//                    break;
//            }
//        }
//
//        @Override
//        public void animateShow() {
//            targetView.animate()
//                    .translationX(initTranslationX)
//                    .translationY(initTranslationY)
//                    .alpha(1f)
//                    .setInterpolator(new FastOutSlowInInterpolator())
//                    .setDuration(XPopup.getAnimationDuration())
//                    .start();
//        }
//
//        @Override
//        public void animateDismiss() {
//            //执行消失动画的时候，宽高可能改变了，所以需要修正动画的起始值
//            switch (popupAnimation) {
//                case TranslateFromLeft:
//                    startTranslationX -= targetView.getMeasuredWidth() - oldWidth;
//                    break;
//                case TranslateFromTop:
//                    startTranslationY -= targetView.getMeasuredHeight() - oldHeight;
//                    break;
//                case TranslateFromRight:
//                    startTranslationX += targetView.getMeasuredWidth() - oldWidth;
//                    break;
//                case TranslateFromBottom:
//                    startTranslationY += targetView.getMeasuredHeight() - oldHeight;
//                    break;
//            }
//
//            targetView.animate()
//                    .translationX(startTranslationX)
//                    .translationY(startTranslationY)
//                    .alpha(0f)
//                    .setInterpolator(new FastOutSlowInInterpolator())
//                    .setDuration(XPopup.getAnimationDuration())
//                    .start();
//        }
//    }

//    public static class ScaleAnimator extends PopupAnimator {
//
//        private float pivotX;
//        private float pivotY;
//
//        public ScaleAnimator(View target, float pivotX, float pivotY) {
//            super(target, null);
//            this.pivotX = pivotX;
//            this.pivotY = pivotY;
//        }
//
//        @Override
//        public void initAnimator() {
//            targetView.setScaleX(0f);
//            targetView.setScaleY(0f);
//            targetView.setAlpha(0);
//
//            // 设置动画参考点
//            targetView.post(new Runnable() {
//                @Override
//                public void run() {
//                    targetView.setPivotX(pivotX);
//                    targetView.setPivotY(pivotY);
//                }
//            });
//        }
//
//        @Override
//        public void animateShow() {
//            targetView.animate().scaleX(1f).scaleY(1f).alpha(1f)
//                    .setDuration(XPopup.getAnimationDuration())
//                    .setInterpolator(new OvershootInterpolator(1f))
//                    .start();
//        }
//
//        @Override
//        public void animateDismiss() {
//            targetView.animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(XPopup.getAnimationDuration())
//                    .setInterpolator(new FastOutSlowInInterpolator()).start();
//        }
//
//    }

    public interface OnItemClickListener {

        void onItemClick(int position, OptionMenu menu);
    }

}
