package com.zpj.sjly.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Synopsis     动画工具类
 * Author		Mosr
 * version		${VERSION}
 * Create 	    2016/12/22 14:20
 * Email  		intimatestranger@sina.cn
 */
public class AnimationUtil {
    private boolean ismHiddenActionstart = false;
    private static AnimationUtil mInstance;


    public static AnimationUtil with() {
        if (mInstance == null) {
            synchronized (AnimationUtil.class) {
                if (mInstance == null) {
                    mInstance = new AnimationUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 从控件所在位置移动到控件的底部
     *
     * @param v
     * @param Duration 动画时间
     */
    public void moveToViewBottom(final View v, long Duration) {
        if (v.getVisibility() != View.VISIBLE)
            return;
        if (ismHiddenActionstart)
            return;
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mHiddenAction.setDuration(Duration);
        v.clearAnimation();
        v.setAnimation(mHiddenAction);
        mHiddenAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ismHiddenActionstart = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                ismHiddenActionstart = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 从控件的底部移动到控件所在位置
     *
     * @param v
     * @param Duration 动画时间
     */
    public void bottomMoveToViewLocation(View v, long Duration) {
        if (v.getVisibility() == View.VISIBLE)
            return;
        v.setVisibility(View.VISIBLE);
        TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(Duration);
        v.clearAnimation();
        v.setAnimation(mShowAction);
    }

    /**
     * 从控件所在位置移动到控件的顶部
     *
     * @param v
     * @param Duration 动画时间
     */
    public void moveToViewTop(final View v, long Duration) {
        if (v.getVisibility() != View.VISIBLE)
            return;
        if (ismHiddenActionstart)
            return;
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        mHiddenAction.setDuration(Duration);
        v.clearAnimation();
        v.setAnimation(mHiddenAction);
        mHiddenAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ismHiddenActionstart = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                ismHiddenActionstart = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 从控件的顶部移动到控件所在位置
     *
     * @param v
     * @param Duration 动画时间
     */
    public void topMoveToViewLocation(View v, long Duration) {
        if (v.getVisibility() == View.VISIBLE)
            return;
        v.setVisibility(View.VISIBLE);
        TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(Duration);
        v.clearAnimation();
        v.setAnimation(mShowAction);
    }

    /**
     * 从控件的右边移动到控件所在位置
     *
     * @param v
     * @param Duration 动画时间
     */
    public void rightMoveToViewLocation(View v, long Duration) {
        if (v.getVisibility() == View.VISIBLE) return;
        v.setVisibility(View.VISIBLE);
        TranslateAnimation mShowAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(Duration);
        v.clearAnimation();
        v.setAnimation(mShowAction);
    }

    public void leftMoveToRightHide(View v, long Duration) {
        if (v.getVisibility() == View.GONE) return;
        v.setVisibility(View.GONE);
        TranslateAnimation mShowAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(Duration);
        v.clearAnimation();
        v.setAnimation(mShowAction);
    }
}

