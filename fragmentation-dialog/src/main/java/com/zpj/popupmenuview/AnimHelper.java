package com.zpj.popupmenuview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cuizhen
 * @date 2018/5/20
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class AnimHelper {





    public static Animator createZoomInAnim(@NonNull final View target, int centerX, int centerY){
        target.setPivotX(centerX);
        target.setPivotY(centerY);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, "scaleX", 0, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, "scaleY", 0, 1);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setInterpolator(new DecelerateInterpolator());
        return set;
    }


    public static Animator createDelayedZoomInAnim(@NonNull final View target, int centerX, int centerY){
        if (!(target instanceof ViewGroup)) {
            return createZoomInAnim(target, centerX, centerY);
        }
        final ViewGroup targetGroup = (ViewGroup) target;
        for (int i = 0; i < targetGroup.getChildCount(); i++) {
            View targetChild = targetGroup.getChildAt(i);
            targetChild.setAlpha(0);
        }
        targetGroup.setPivotX(centerX);
        targetGroup.setPivotY(centerY);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(targetGroup, "scaleX", 0, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(targetGroup, "scaleY", 0, 1);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setInterpolator(new DecelerateInterpolator());
        scaleX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private boolean isChildAnimStart = false;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = animation.getAnimatedFraction();
                if (!isChildAnimStart && f > 0.618F) {
                    isChildAnimStart = true;
                    final List<Animator> childAnimators = new ArrayList<>(targetGroup.getChildCount());
                    for (int i = 0; i < targetGroup.getChildCount(); i++) {
                        View targetChild = targetGroup.getChildAt(i);
                        ObjectAnimator alphaChild = ObjectAnimator.ofFloat(targetChild, "alpha", 0, 1);
                        alphaChild.setStartDelay(18 * i);
                        alphaChild.setDuration(50);
                        childAnimators.add(alphaChild);
                    }
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(childAnimators);
                    set.setInterpolator(new DecelerateInterpolator());
                    set.start();
                }
            }
        });
        return set;
    }

    public static Animator createDelayedZoomOutAnim(@NonNull final View target, int centerX, int centerY){
        if (!(target instanceof ViewGroup)) {
            return createZoomInAnim(target, centerX, centerY);
        }
        final ViewGroup targetGroup = (ViewGroup) target;
        targetGroup.setPivotX(centerX);
        targetGroup.setPivotY(centerY);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(targetGroup, "scaleX", targetGroup.getScaleX(), 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(targetGroup, "scaleY", targetGroup.getScaleY(), 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setInterpolator(new DecelerateInterpolator());
        scaleX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private boolean isChildAnimStart = false;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!isChildAnimStart) {
                    isChildAnimStart = true;
                    final List<Animator> childAnimators = new ArrayList<>(targetGroup.getChildCount());
                    for (int i = targetGroup.getChildCount() - 1; i >= 0 ; i--) {
                        View targetChild = targetGroup.getChildAt(i);
                        ObjectAnimator alphaChild = ObjectAnimator.ofFloat(targetChild, "alpha", targetChild.getAlpha(), 0);
                        alphaChild.setStartDelay(18 * (targetGroup.getChildCount() - 1 - i));
                        alphaChild.setDuration(50);
                        childAnimators.add(alphaChild);
                    }
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(childAnimators);
                    set.setInterpolator(new DecelerateInterpolator());
                    set.start();
                }
            }
        });
        return set;
    }

    public static Animator createDelayedZoomInAnim(@NonNull final View target,
                                                   @FloatRange(from = 0, to = 1) float centerPercentX,
                                                   @FloatRange(from = 0, to = 1) float centerPercentY){
        int centerX = (int) (target.getMeasuredWidth() * centerPercentX);
        int centerY = (int) (target.getMeasuredHeight() * centerPercentY);
        return createDelayedZoomInAnim(target, centerX, centerY);
    }

    public static Animator createDelayedZoomOutAnim(@NonNull final View target,
                                                   @FloatRange(from = 0, to = 1) float centerPercentX,
                                                   @FloatRange(from = 0, to = 1) float centerPercentY){
        int centerX = (int) (target.getMeasuredWidth() * centerPercentX);
        int centerY = (int) (target.getMeasuredHeight() * centerPercentY);
        return createDelayedZoomOutAnim(target, centerX, centerY);
    }
}
