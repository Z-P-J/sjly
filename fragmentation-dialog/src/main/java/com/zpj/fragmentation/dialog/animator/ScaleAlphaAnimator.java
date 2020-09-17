package com.zpj.fragmentation.dialog.animator;

import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.zpj.fragmentation.dialog.config.DialogConfig;
import com.zpj.fragmentation.dialog.enums.PopupAnimation;

/**
 * Description: 缩放透明
 * Create by dance, at 2018/12/9
 */
public class ScaleAlphaAnimator extends PopupAnimator {

    private float pivotX = 0;
    private float pivotY = 0;
    private float tension = 1f;

    public ScaleAlphaAnimator(View target, float pivotX, float pivotY, float tension) {
        this(target, pivotX, pivotY);
        this.tension = tension;
    }

    public ScaleAlphaAnimator(View target, float pivotX, float pivotY) {
        super(target, null);
        this.pivotX = pivotX;
        this.pivotY = pivotY;
    }

    public ScaleAlphaAnimator(View target, PopupAnimation popupAnimation) {
        super(target, popupAnimation);
    }

    @Override
    public void initAnimator() {
        targetView.setScaleX(0f);
        targetView.setScaleY(0f);
        targetView.setAlpha(0);

        // 设置动画参考点
        targetView.post(new Runnable() {
            @Override
            public void run() {
                if (popupAnimation == null) {
                    targetView.setPivotX(pivotX);
                    targetView.setPivotY(pivotY);
                } else {
                    applyPivot();
                }
            }
        });
    }

    /**
     * 根据不同的PopupAnimation来设定对应的pivot
     */
    private void applyPivot() {
        switch (popupAnimation) {
            case ScaleAlphaFromCenter:
                targetView.setPivotX(targetView.getMeasuredWidth() / 2f);
                targetView.setPivotY(targetView.getMeasuredHeight() / 2f);
                break;
            case ScaleAlphaFromLeftTop:
                targetView.setPivotX(0);
                targetView.setPivotY(0);
                break;
            case ScaleAlphaFromRightTop:
                targetView.setPivotX(targetView.getMeasuredWidth());
                targetView.setPivotY(0f);
                break;
            case ScaleAlphaFromLeftBottom:
                targetView.setPivotX(0f);
                targetView.setPivotY(targetView.getMeasuredHeight());
                break;
            case ScaleAlphaFromRightBottom:
                targetView.setPivotX(targetView.getMeasuredWidth());
                targetView.setPivotY(targetView.getMeasuredHeight());
                break;
        }

    }

    @Override
    public void animateShow() {
        targetView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(DialogConfig.getAnimationDuration())
                .setInterpolator(new OvershootInterpolator(tension))
                .start();
    }

    @Override
    public void animateDismiss() {
        targetView.animate()
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setDuration(DialogConfig.getAnimationDuration())
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
    }

}
