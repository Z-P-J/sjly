package com.zpj.fragmentation.dialog.animator;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;

import com.zpj.fragmentation.dialog.config.DialogConfig;

/**
 * Description: 背景Shadow动画器，负责执行半透明的渐入渐出动画
 * Create by dance, at 2018/12/9
 */
public class ShadowBgAnimator extends PopupAnimator {

    private static int shadowBgColor = Color.parseColor("#9F000000");

    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    public int startColor = Color.TRANSPARENT;
    public boolean isZeroDuration = false;

    public ShadowBgAnimator(View target) {
        super(target);
    }

    public ShadowBgAnimator() {
    }

    @Override
    public void initAnimator() {
        targetView.setBackgroundColor(startColor);
    }

    @Override
    public void animateShow() {
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, startColor, shadowBgColor);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                targetView.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(isZeroDuration ? 0 : DialogConfig.getAnimationDuration()).start();
    }

    @Override
    public void animateDismiss() {
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, shadowBgColor, startColor);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                targetView.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(isZeroDuration ? 0 : DialogConfig.getAnimationDuration()).start();
    }

    public void animateDismiss(Runnable runnable) {
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, shadowBgColor, startColor);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                targetView.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                runnable.run();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(isZeroDuration ? 0 : DialogConfig.getAnimationDuration()).start();
    }

    public int calculateBgColor(float fraction) {
        return (int) argbEvaluator.evaluate(fraction, startColor, shadowBgColor);
    }

}
