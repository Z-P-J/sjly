package com.zpj.fragmentation.dialog.base;

import android.graphics.Rect;
import android.view.ViewGroup;

import com.zpj.fragmentation.dialog.animator.PopupAnimator;
import com.zpj.fragmentation.dialog.animator.ScrollScaleAnimator;
import com.zpj.fragmentation.dialog.enums.PopupAnimation;
import com.zpj.fragmentation.dialog.enums.PopupPosition;
import com.zpj.utils.ScreenUtils;

/**
 * Description: 水平方向的依附于某个View或者某个点的弹窗，可以轻松实现微信朋友圈点赞的弹窗效果。
 * 支持通过popupPosition()方法手动指定想要出现在目标的左边还是右边，但是对Top和Bottom则不生效。
 * Create by lxj, at 2019/3/13
 */
public abstract class HorizontalAttachPopup extends AttachDialogFragment {

    @Override
    protected PopupAnimator getDialogAnimator(ViewGroup contentView) {
        ScrollScaleAnimator animator;
        if (isShowLeftToTarget()) {
            animator = new ScrollScaleAnimator(getImplView(), PopupAnimation.ScrollAlphaFromRight);
        } else {
            animator = new ScrollScaleAnimator(getImplView(), PopupAnimation.ScrollAlphaFromLeft);
        }
        animator.isOnlyScaleX = true;
        return animator;
    }

    /**
     * 执行附着逻辑
     */
    @Override
    protected void doAttach() {
        float translationX = 0, translationY = 0;
        int w = getImplView().getMeasuredWidth();
        int h = getImplView().getMeasuredHeight();
        //0. 判断是依附于某个点还是某个View
        if (touchPoint != null) {
            // 依附于指定点
            isShowLeft = touchPoint.x > ScreenUtils.getScreenWidth(context) / 2f;

            // translationX: 在左边就和点左边对齐，在右边就和其右边对齐
            translationX = isShowLeftToTarget() ? (touchPoint.x - w - defaultOffsetX) : (touchPoint.x + defaultOffsetX);
            translationY = touchPoint.y - h * .5f + defaultOffsetY;
        } else {
            // 依附于指定View
            //1. 获取atView在屏幕上的位置
            int[] locations = new int[2];
            attachView.getLocationOnScreen(locations);
            Rect rect = new Rect(locations[0], locations[1], locations[0] + attachView.getMeasuredWidth(),
                    locations[1] + attachView.getMeasuredHeight());

            int centerX = (rect.left + rect.right) / 2;

            isShowLeft = centerX > ScreenUtils.getScreenWidth(context) / 2;

            translationX = isShowLeftToTarget() ? (rect.left - w + defaultOffsetX) : (rect.right + defaultOffsetX);
            translationY = rect.top + (rect.height() - h) / 2f + defaultOffsetY;
        }
        getImplView().setTranslationX(translationX);
        getImplView().setTranslationY(translationY);
    }

    private boolean isShowLeftToTarget() {
        return (isShowLeft || popupPosition == PopupPosition.Left)
                && popupPosition != PopupPosition.Right;
    }

}
