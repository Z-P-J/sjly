package com.zpj.fragmentation.dialog.base;

import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.animator.PopupAnimator;
import com.zpj.fragmentation.dialog.animator.ScrollScaleAnimator;
import com.zpj.fragmentation.dialog.enums.PopupAnimation;
import com.zpj.fragmentation.dialog.enums.PopupPosition;
import com.zpj.fragmentation.dialog.widget.PartShadowContainer;
import com.zpj.utils.ScreenUtils;


public abstract class AttachDialogFragment extends BaseDialogFragment {

    private static final String TAG = "AttachDialogFragment";

    protected int defaultOffsetY = 0;
    protected int defaultOffsetX = 0;
    protected PartShadowContainer attachPopupContainer;

    protected boolean isCenterHorizontal = false;
    protected boolean fixedStatusBarHeight;

    protected View attachView;
    protected PointF touchPoint = null;

    protected PopupPosition popupPosition = null;

    protected AtViewGravity atViewGravity = AtViewGravity.TOP;

    protected View contentView;

    public enum AtViewGravity {
        TOP, BOTTOM
    }

    @Override
    protected final int getImplLayoutId() {
        return R.layout._dialog_layout_attach_view;
    }

    protected abstract int getContentLayoutId();

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

        attachPopupContainer = findViewById(R.id.attachPopupContainer);

        contentView = LayoutInflater.from(getContext()).inflate(getContentLayoutId(), attachPopupContainer, false);
        attachPopupContainer.addView(contentView);
        if (bgDrawable != null) {
            contentView.setBackground(bgDrawable);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentView
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        doAttach();
                    }
                });
    }

    public boolean isShowUp;
    boolean isShowLeft;
    protected int bgDrawableMargin = 6;


    /**
     * 执行倚靠逻辑
     */
    float translationX = 0, translationY = 0;
    // 弹窗显示的位置不能超越Window高度
    float maxY = 0;
    float maxX = 0; // 显示在右边时候的最大值

    @Override
    public void doShowAnimation() {

    }

    protected void doAttach() {
        int offset = ScreenUtils.getScreenHeight(context) - getRootView().getMeasuredHeight();

        maxY = ScreenUtils.getScreenHeight(context);
        int width = getImplView().getMeasuredWidth();
        int height = getImplView().getMeasuredHeight();
        Log.d(TAG, "width=" + width + " height=" + height);
        int windowWidth = ScreenUtils.getScreenWidth(context);
        int windowHeight = ScreenUtils.getScreenHeight(context);
        //0. 判断是依附于某个点还是某个View
        if (touchPoint != null) {

            if (touchPoint.x == 0 && touchPoint.y == 0) {
                touchPoint.x = (windowWidth - width) / 2f;
                touchPoint.y = (windowHeight - height) / 2f;
                if (touchPoint.x < 0) {
                    touchPoint.x = 0;
                }
                if (touchPoint.y < 0) {
                    touchPoint.y = 0;
                }
            }
            // 依附于指定点
            maxX = Math.max(touchPoint.x - width, 0);
            // 尽量优先放在下方，当不够的时候在显示在上方
            //假设下方放不下，超出window高度
            boolean isTallerThanWindowHeight = (touchPoint.y + height) > maxY;
            if (isTallerThanWindowHeight) {
                isShowUp = touchPoint.y > windowHeight / 2f;
            } else {
                isShowUp = false;
            }
            isShowLeft = touchPoint.x < windowWidth / 2f;

            //修正高度，弹窗的高有可能超出window区域
            if (isShowUpToTarget()) {
                if (getImplView().getMeasuredHeight() > touchPoint.y) {
                    ViewGroup.LayoutParams params = getImplView().getLayoutParams();
                    params.height = (int) (touchPoint.y - ScreenUtils.getStatusBarHeight(context));
                    getImplView().setLayoutParams(params);
                }
            } else {
                if (getImplView().getMeasuredHeight() + touchPoint.y > windowHeight) {
                    ViewGroup.LayoutParams params = getImplView().getLayoutParams();
                    params.height = (int) (windowHeight - touchPoint.y);
                    getImplView().setLayoutParams(params);
                }
            }

            getImplView().post(new Runnable() {
                @Override
                public void run() {
                    translationX = (isShowLeft ? touchPoint.x : maxX) + (isShowLeft ? defaultOffsetX : -defaultOffsetX);
                    if (isCenterHorizontal) {
                        //水平居中
                        if (isShowLeft)
                            translationX -= width / 2f;
                        else
                            translationX += height / 2f;
                    }
                    if (isShowUpToTarget()) {
                        // 应显示在point上方
                        // translationX: 在左边就和atView左边对齐，在右边就和其右边对齐
                        translationY = touchPoint.y - height - defaultOffsetY;
                    } else {
                        translationY = touchPoint.y + defaultOffsetY;
                    }

                    translationY -= offset;

                    Log.d(TAG, "translationX=" + translationX + " translationY=" + translationY);

                    getImplView().setTranslationX(translationX);
                    getImplView().setTranslationY(translationY);

                    popupContentAnimator = getDialogAnimator((ViewGroup) getImplView());
                    if (popupContentAnimator != null) {
                        popupContentAnimator.initAnimator();
                        popupContentAnimator.animateShow();
                    }
                    getImplView().setAlpha(1f);
                }
            });

        } else {
            // 依附于指定View
            //1. 获取atView在屏幕上的位置
            int[] locations = new int[2];
            attachView.getLocationOnScreen(locations);
//            attachView.getLocationInWindow(); // getLocationOnScreen
            final Rect rect = new Rect(locations[0], locations[1], locations[0] + attachView.getMeasuredWidth(),
                    locations[1] + attachView.getMeasuredHeight());
            Log.d(TAG, "locations[0]=" + locations[0] + " locations[1]=" + locations[1]);

            maxX = Math.max(rect.right - width, 0);
            int centerX = (rect.left + rect.right) / 2;

            // 尽量优先放在下方，当不够的时候在显示在上方
            //假设下方放不下，超出window高度
            boolean isTallerThanWindowHeight = (rect.bottom + height) > maxY;
            if (isTallerThanWindowHeight) {
                int centerY = (rect.top + rect.bottom) / 2;
                isShowUp = centerY > windowHeight / 2;
            } else {
                isShowUp = false;
            }
            isShowLeft = centerX < windowWidth / 2;

            //修正高度，弹窗的高有可能超出window区域
            if (isShowUpToTarget()) {
                if (height > rect.top) {
                    ViewGroup.LayoutParams params = getImplView().getLayoutParams();
                    params.height = rect.top - ScreenUtils.getStatusBarHeight(context);
                    getImplView().setLayoutParams(params);
                }
            } else {
                if (getImplView().getMeasuredHeight() + rect.bottom > windowHeight) {
                    ViewGroup.LayoutParams params = getImplView().getLayoutParams();
                    params.height = windowHeight - rect.bottom;
                    getImplView().setLayoutParams(params);
                }
            }

            getImplView().post(new Runnable() {
                @Override
                public void run() {
                    translationX = (isShowLeft ? rect.left : maxX) + (isShowLeft ? defaultOffsetX : -defaultOffsetX);
                    if (isCenterHorizontal) {
                        //水平居中
                        if (isShowLeft)
                            translationX += (rect.width() - width) / 2f;
                        else
                            translationX -= (rect.width() - width) / 2f;
                    }
                    if (isShowUpToTarget()) {
                        //说明上面的空间比较大，应显示在atView上方
                        // translationX: 在左边就和atView左边对齐，在右边就和其右边对齐
//                        translationY = rect.top + rect.height() - height - defaultOffsetY;
                        translationY = rect.top - height - defaultOffsetY;
                        if (atViewGravity == AtViewGravity.TOP) {
                            translationY += rect.height();
                        }
                    } else {
//                        translationY = rect.bottom - rect.height() + defaultOffsetY;
                        translationY = rect.bottom + defaultOffsetY;
                        if (atViewGravity == AtViewGravity.TOP) {
                            translationY -= rect.height();
                        }
                    }

                    translationY -= offset;

//                    translationY -= ScreenUtils.getStatusBarHeight(context);

//                    if (getActivity() != null && (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                            & getActivity().getWindow().getAttributes().flags)
//                            == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
//
//                    } else {
//                        translationY -= ScreenUtils.getStatusBarHeight(context);
//                    }

                    Log.d(TAG, "translationX=" + translationX + " translationY=" + translationY);


                    getImplView().setTranslationX(translationX);
                    getImplView().setTranslationY(translationY);


                    popupContentAnimator = getDialogAnimator((ViewGroup) getImplView());
                    if (popupContentAnimator != null) {
                        popupContentAnimator.initAnimator();
                        popupContentAnimator.animateShow();
                    }
                    getImplView().setAlpha(1f);
                }
            });

        }
    }

    protected boolean isShowUpToTarget() {
        return (isShowUp || popupPosition == PopupPosition.Top)
                && popupPosition != PopupPosition.Bottom;
    }

    @Override
    protected PopupAnimator getDialogAnimator(ViewGroup contentView) {
        PopupAnimator animator;
        if (isShowUpToTarget()) {
            // 在上方展示
            if (isShowLeft) {
                animator = new ScrollScaleAnimator(getImplView(), PopupAnimation.ScrollAlphaFromLeftBottom);
            } else {
                animator = new ScrollScaleAnimator(getImplView(), PopupAnimation.ScrollAlphaFromRightBottom);
            }
        } else {
            // 在下方展示
            if (isShowLeft) {
                animator = new ScrollScaleAnimator(getImplView(), PopupAnimation.ScrollAlphaFromLeftTop);
            } else {
                animator = new ScrollScaleAnimator(getImplView(), PopupAnimation.ScrollAlphaFromRightTop);
            }
        }
        return animator;
    }

    public View getContentView() {
        return contentView;
    }

    public AttachDialogFragment setAttachView(View attachView) {
        this.attachView = attachView;
        return this;
    }

    public AttachDialogFragment setTouchPoint(PointF touchPoint) {
        this.touchPoint = touchPoint;
        return this;
    }

    public AttachDialogFragment setTouchPoint(float x, float y) {
        this.touchPoint = new PointF(x, y);;
        return this;
    }

    public AttachDialogFragment setCenterHorizontal(boolean centerHorizontal) {
        isCenterHorizontal = centerHorizontal;
        return this;
    }

    public AttachDialogFragment setPopupPosition(PopupPosition popupPosition) {
        this.popupPosition = popupPosition;
        return this;
    }

    public AttachDialogFragment setDefaultOffsetX(int defaultOffsetX) {
        this.defaultOffsetX = defaultOffsetX;
        return this;
    }

    public AttachDialogFragment setDefaultOffsetY(int defaultOffsetY) {
        this.defaultOffsetY = defaultOffsetY;
        return this;
    }

    public AttachDialogFragment setAtViewGravity(AtViewGravity atViewGravity) {
        this.atViewGravity = atViewGravity;
        return this;
    }

}
