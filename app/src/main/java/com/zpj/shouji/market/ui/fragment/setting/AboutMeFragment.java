package com.zpj.shouji.market.ui.fragment.setting;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.zpj.shouji.market.glide.blur.BlurTransformation;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.widget.PercentImageView;
import com.zpj.shouji.market.utils.AnimationUtil;
import com.zpj.utils.AnimatorUtils;
import com.zpj.widget.setting.CommonSettingItem;
import com.zpj.widget.setting.OnCommonItemClickListener;

public class AboutMeFragment extends BaseFragment implements OnCommonItemClickListener {

    private SwipeLayout sl;
    private ImageView iv_blur;
    private ImageView civ_icon;
    private TextView tv_name;
    private TextView tv_sign;


    private RelativeLayout rl_info;
    private RelativeLayout rl_reward;
    private PercentImageView piv_qq_qrcode;
    private PercentImageView piv_wx_qrcode;

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
    public CharSequence getToolbarTitle(Context context) {
        return "关于作者";
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
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
                            AnimationUtil.changeViewAlpha(iv_blur, 0, 1, 500);
                            AnimationUtil.changeViewSize(iv_blur, 4, 1, 1000);
                        });
                    }
                });

        CommonSettingItem githubItem = findViewById(R.id.item_github);
        githubItem.setOnItemClickListener(this);

        CommonSettingItem sjlyItem = findViewById(R.id.item_sjly);
        sjlyItem.setOnItemClickListener(this);

        CommonSettingItem emailItem = findViewById(R.id.item_email);
        emailItem.setOnItemClickListener(this);

        AnimationUtil.doDelayShowAnim(1000, 100, findViewById(R.id.iv_icon), findViewById(R.id.tv_name),
                findViewById(R.id.tv_sign), githubItem, sjlyItem, emailItem);
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        _mActivity.setFragmentAnimator(new DefaultHorizontalAnimator());
        setFragmentAnimator(new DefaultHorizontalAnimator());
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
}
