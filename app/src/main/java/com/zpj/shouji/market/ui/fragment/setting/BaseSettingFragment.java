package com.zpj.shouji.market.ui.fragment.setting;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.zpj.fragmentation.dialog.impl.FullScreenDialogFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.utils.AnimatorUtils;
import com.zpj.widget.setting.OnCheckableItemClickListener;
import com.zpj.widget.setting.OnCommonItemClickListener;
import com.zpj.widget.toolbar.ZToolBar;

public abstract class BaseSettingFragment extends FullScreenDialogFragment
        implements OnCommonItemClickListener, OnCheckableItemClickListener {

    private View[] views;

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (AppConfig.isNightMode()) {
            lightStatusBar();
        } else {
            darkStatusBar();
        }
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        view.setAlpha(0f);
        super.initView(view, savedInstanceState);
        ZToolBar toolBar = findViewById(R.id.tool_bar);
        toolBar.setCenterText(getToolbarTitle(context));
    }

    @Override
    public void doShowAnimation() {
        view.animate().alpha(1f).setDuration(getShowAnimDuration()).start();
        LinearLayout container = findViewById(R.id.ll_container);
        views = new View[container.getChildCount()];
        for (int i = 0; i < container.getChildCount(); i++) {
            views[i] = container.getChildAt(i);
        }
        AnimatorUtils.doDelayShowAnim(500, 50, views);
    }

    @Override
    public void doDismissAnimation() {
        view.animate().alpha(0f).setDuration(getDismissAnimDuration()).start();
        doDelayHideAnim(getDismissAnimDuration(), 20, views);
    }

    public abstract String getToolbarTitle(Context context);

    public static void doDelayHideAnim(long dur, long delay, final View... targets) {
        for (int i = 0; i < targets.length; i++) {
            final View target = targets[i];
            target.setAlpha(1f);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(target, "translationY", 0, 100);
            ObjectAnimator animatorA = ObjectAnimator.ofFloat(target, "alpha", 1, 0);
            animatorY.setDuration(dur);
            animatorA.setDuration((long) (dur * 0.618F));
            AnimatorSet animator = new AnimatorSet();
            animator.playTogether(animatorA, animatorY);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setStartDelay(delay * i);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    for (View view : targets) {
                        view.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animator.start();
        }
    }


}
