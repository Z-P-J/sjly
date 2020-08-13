package com.zpj.shouji.market.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.daimajia.swipe.SwipeLayout;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.anim.DefaultHorizontalAnimator;
import com.zpj.fragmentation.anim.DefaultNoAnimator;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.ui.widget.PercentImageView;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class AboutMeFragment extends BaseFragment {

    SwipeLayout sl;
    ImageView iv_blur;
    ImageView civ_icon;
    TextView tv_name;
    TextView tv_sign;


    RelativeLayout rl_info;
    RelativeLayout rl_reward;
    PercentImageView piv_qq_qrcode;
    PercentImageView piv_wx_qrcode;

    public static void start() {
        StartFragmentEvent.start(new AboutMeFragment());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setFragmentAnimator(new DefaultNoAnimator());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about_me;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("关于作者");
        iv_blur = view.findViewById(R.id.iv_blur);

        Glide.with(context)
                .load(getResources().getDrawable(R.drawable.logo_author))
                .apply(RequestOptions.bitmapTransform(new BlurTransformation()))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        iv_blur.setImageDrawable(resource);
                        iv_blur.setAlpha(0F);
                        post(() -> {
                            changeViewAlpha(iv_blur, 0, 1, 500);
                            changeViewSize(iv_blur, 4, 1, 2000);
                        });
                    }
                });
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        _mActivity.setFragmentAnimator(new DefaultHorizontalAnimator());
        setFragmentAnimator(new DefaultHorizontalAnimator());
    }

//    @Override
//    public FragmentAnimator onCreateFragmentAnimator() {
//        return new DefaultNoAnimator();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }

    private void changeVisible(int visible, View... views) {
        for (View view : views) {
            view.setVisibility(visible);
        }
    }

    private void changeViewSize(final View target, float from, float to, long dur) {
        final ValueAnimator animator = ValueAnimator.ofFloat(from, to);
        animator.setDuration(dur);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (target == null) {
                    animator.cancel();
                    return;
                }
                float f = (float) animator.getAnimatedValue();
                target.setScaleX(f);
                target.setScaleY(f);
            }
        });
        animator.start();
    }

    private void changeViewAlpha(final View target, float from, float to, long dur) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "alpha", from, to);
        animator.setDuration(dur);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    private void doDelayShowAnim(long dur, long delay, View... targets) {
        for (int i = 0; i < targets.length; i++) {
            final View target = targets[i];
            target.setAlpha(0);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(target, "translationY", 100, 0);
            ObjectAnimator animatorA = ObjectAnimator.ofFloat(target, "alpha", 0, 1);
            animatorY.setDuration(dur);
            animatorA.setDuration((long) (dur * 0.618F));
            AnimatorSet animator = new AnimatorSet();
            animator.playTogether(animatorA, animatorY);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setStartDelay(delay * i);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    changeVisible(View.VISIBLE, target);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
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
