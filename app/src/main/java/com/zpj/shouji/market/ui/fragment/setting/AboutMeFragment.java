package com.zpj.shouji.market.ui.fragment.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.anim.DefaultHorizontalAnimator;
import com.zpj.fragmentation.anim.DefaultNoAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.glide.transformations.blur.BlurTransformation;
import com.zpj.shouji.market.ui.animator.MyFragmentAnimator;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.utils.AnimationUtil;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.widget.setting.CommonSettingItem;
import com.zpj.widget.setting.OnCommonItemClickListener;

public class AboutMeFragment extends BaseFragment implements OnCommonItemClickListener {


    private ImageView iv_blur;
    private ImageView ivAlipay;
    private ImageView ivWxpay;

    public static void start() {
        StartFragmentEvent.start(new AboutMeFragment());
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        setFragmentAnimator(new DefaultNoAnimator());
//        super.onCreate(savedInstanceState);
//    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultNoAnimator();
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
        ImageView ivIcon = findViewById(R.id.iv_icon);
        iv_blur = findViewById(R.id.iv_blur);

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
                        iv_blur.setImageDrawable(resource);
                        iv_blur.setAlpha(0F);
//                        if (AppConfig.isNightMode()) {
//                            iv_blur.setColorFilter(Color.parseColor("aa000000"));
//                        }
                        post(() -> {
                            AnimationUtil.changeViewAlpha(iv_blur, 0, 1, 500);
                            AnimationUtil.changeViewSize(iv_blur, 4, 1, 1000);
                        });
                    }
                });

        Glide.with(context)
                .load(getResources().getDrawable(R.drawable.logo_author))
                .apply(RequestOptions.circleCropTransform())
                .into(ivIcon);

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
//        _mActivity.setFragmentAnimator(new MyFragmentAnimator());
//        setFragmentAnimator(new MyFragmentAnimator());
//        _mActivity.setFragmentAnimator(new DefaultHorizontalAnimator());
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
