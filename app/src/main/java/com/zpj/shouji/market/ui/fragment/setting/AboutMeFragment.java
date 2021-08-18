package com.zpj.shouji.market.ui.fragment.setting;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zpj.fragmentation.dialog.animator.DialogAnimator;
import com.zpj.fragmentation.dialog.impl.FullScreenDialogFragment;
import com.zpj.fragmentation.swipeback.SwipeBackLayout;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.transformations.blur.BlurTransformation;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.utils.AnimatorUtils;
import com.zpj.widget.setting.CommonSettingItem;
import com.zpj.widget.setting.OnCommonItemClickListener;

public class AboutMeFragment extends FullScreenDialogFragment implements OnCommonItemClickListener {


    private ImageView ivBlur;
    private ImageView ivAlipay;
    private ImageView ivWxpay;

    private CommonSettingItem githubItem;
    private CommonSettingItem sjlyItem;
    private CommonSettingItem emailItem;


    public static void start(Context context) {
        new AboutMeFragment().show(context);
    }

    @Override
    protected boolean enableSwipeBack() {
        return true;
    }

    @Override
    protected DialogAnimator onCreateDialogAnimator(ViewGroup contentView) {
        return null;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.fragment_setting_about_me;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        SwipeBackLayout swipeBackLayout = findViewById(R.id.layout_swipe_back);
        swipeBackLayout.setEdgeLevel(SwipeBackLayout.EdgeLevel.MAX);
        swipeBackLayout.setFragment(this, swipeBackLayout.getChildAt(0));

        ImageView ivIcon = findViewById(R.id.iv_icon);
        ivBlur = findViewById(R.id.iv_blur);

        ivAlipay = findViewById(R.id.iv_alipay);
        ivWxpay = findViewById(R.id.iv_wxpay);

        ivAlipay.setOnLongClickListener(v -> {
            PictureUtil.saveResource(context, R.drawable.ic_alipay, "支付宝付款码");
            return true;
        });

        ivWxpay.setOnLongClickListener(v -> {
            PictureUtil.saveResource(context, R.drawable.ic_wechat_pay, "微信付款码");
            return true;
        });

        Glide.with(context)
                .load(getResources().getDrawable(R.drawable.logo_author))
                .apply(RequestOptions.bitmapTransform(new BlurTransformation()))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        ivBlur.setImageDrawable(resource);
                        ivBlur.setAlpha(0F);
//                        if (AppConfig.isNightMode()) {
//                            iv_blur.setColorFilter(Color.parseColor("aa000000"));
//                        }
//                        post(() -> {
//                            AnimatorUtils.changeViewAlpha(ivBlur, 0, 1, getShowAnimDuration());
//                            AnimatorUtils.changeViewSize(ivBlur, 4, 1, 800);
//                        });
                    }
                });

        Glide.with(context)
                .load(getResources().getDrawable(R.drawable.logo_author))
                .apply(RequestOptions.circleCropTransform())
                .into(ivIcon);

        githubItem = findViewById(R.id.item_github);
        githubItem.setOnItemClickListener(this);

        sjlyItem = findViewById(R.id.item_sjly);
        sjlyItem.setOnItemClickListener(this);

        emailItem = findViewById(R.id.item_email);
        emailItem.setOnItemClickListener(this);
    }

    @Override
    public void doShowAnimation() {
        AnimatorUtils.changeViewAlpha(ivBlur, 0, 1, getShowAnimDuration());
        AnimatorUtils.changeViewSize(ivBlur, 4, 1, 800);
        AnimatorUtils.doDelayShowAnim(600, 100, findViewById(R.id.iv_icon), findViewById(R.id.iv_name),
                findViewById(R.id.tv_sign), githubItem, sjlyItem, emailItem);
    }

    @Override
    public void doDismissAnimation() {
        findViewById(R.id.tool_bar).animate().alpha(0f).setDuration(getDismissAnimDuration()).start();
        ivBlur.animate().alpha(0f).setDuration(getDismissAnimDuration()).start();
        doDelayHideAnim(getDismissAnimDuration() - 20 * 6, 20, findViewById(R.id.iv_icon), findViewById(R.id.iv_name),
                findViewById(R.id.tv_sign), githubItem, sjlyItem, emailItem);
        doDelayHideAnim(getDismissAnimDuration() - 60 * 2, 60, ivAlipay, ivWxpay);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        lightStatusBar();
    }

    @Override
    public void onItemClick(CommonSettingItem item) {
        switch (item.getId()) {
            case R.id.item_github:
            case R.id.item_sjly:
                WebFragment.start(item.getInfoText());
                break;
            case R.id.item_email:
                Uri uri = Uri.parse("mailto:" + item.getInfoText());
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                context.startActivity(Intent.createChooser(intent, "请选择邮件应用"));
                break;
        }
    }

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
