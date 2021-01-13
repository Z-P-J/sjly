package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.glide.transformations.blur.CropBlurTransformation;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.widget.toolbar.BaseToolBar;
import com.zpj.widget.toolbar.ZToolBar;
import com.zxy.skin.sdk.SkinEngine;

import net.lucode.hackware.magicindicator.MagicIndicator;

import top.defaults.drawabletoolbox.DrawableBuilder;

public class AppDetailLayout extends FrameLayout {

    private CollapsingToolbarLayout toolbarLayout;
    private final LinearLayout headerLayout;
    private final MagicIndicator magicIndicator;
    private final ViewPager mViewPager;

    private final ImageView icon;
    private final TextView title;
    private final TextView tvVersion;
    private final TextView tvSize;
    private final TextView shortInfo;
    private final TextView shortIntroduce;

    private BaseToolBar toolBar;
    private View buttonBarLayout;
    private ImageView ivToolbarAvater;
    private TextView tvToolbarName;

    private int mTopViewHeight;


    public AppDetailLayout(Context context) {
        this(context, null);
    }

    public AppDetailLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppDetailLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        setBackgroundColor(Color.WHITE);
//        setBackgroundColor(ThemeUtils.getDefaultBackgroundColor(context));
        SkinEngine.setBackground(this, R.attr.backgroundColor);

        LayoutInflater.from(context).inflate(R.layout.layout_app_detail2, this, true);
        toolbarLayout = findViewById(R.id.collapsingToolbar);
        headerLayout = findViewById(R.id.layout_header);
        mViewPager = findViewById(R.id.view_pager);
        magicIndicator = findViewById(R.id.magic_indicator);

        icon = findViewById(R.id.iv_icon);
        title = findViewById(R.id.tv_title);
        tvVersion = findViewById(R.id.tv_version);
        tvSize = findViewById(R.id.tv_size);
        shortInfo = findViewById(R.id.tv_info);
        shortIntroduce = findViewById(R.id.tv_detail);

        View shadowView = findViewById(R.id.shadow_view);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, i) -> {
            float alpha = (float) Math.abs(i) / appBarLayout1.getTotalScrollRange();
            alpha = Math.min(1f, alpha);
            buttonBarLayout.setAlpha(alpha);
            if (alpha >= 1f) {
                headerLayout.setAlpha(0f);
                shadowView.setAlpha(1f);
            } else {
                shadowView.setAlpha(0f);
                headerLayout.setAlpha(1f);
            }
        });
    }

    public void loadInfo(AppDetailInfo info) {
        Glide.with(icon)
                .load(info.getIconUrl())
                .apply(GlideRequestOptions.getDefaultIconOption())
                .into(icon);
        Glide.with(getContext())
                .load(info.getIconUrl())
                .apply(GlideRequestOptions.getDefaultIconOption())
                .into(ivToolbarAvater);

        Glide.with(getContext())
                .asDrawable()
                .load(info.getIconUrl())
                .apply(
                        RequestOptions
                                .bitmapTransform(new CropBlurTransformation(25, 0.3f))
                                .error(R.drawable.bg_member_default)
                                .placeholder(R.drawable.bg_member_default)
                )
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        toolbarLayout.setBackground(resource);
                    }
                });

        title.setText(info.getName());
        tvVersion.setText(info.getVersion());
        tvSize.setText(info.getSize());
        tvToolbarName.setText(info.getName());
        shortInfo.setText(info.getLanguage() + " | " + info.getAds() + " | " + info.getFirmware());
        shortIntroduce.setText(info.getLineInfo());

        tvVersion.setBackground(new DrawableBuilder()
                .rectangle()
                .rounded()
                .strokeColor(getResources().getColor(R.color.colorPrimary))
                .solidColor(getResources().getColor(R.color.colorPrimary))
                .build());
        tvSize.setBackground(new DrawableBuilder()
                .rectangle()
                .rounded()
                .strokeColor(getResources().getColor(R.color.light_blue1))
                .solidColor(getResources().getColor(R.color.light_blue1))
                .build());
        int color = Color.WHITE;
        title.setTextColor(color);
        tvVersion.setTextColor(color);
        tvSize.setTextColor(color);
        tvToolbarName.setTextColor(color);
        shortInfo.setTextColor(color);
        shortIntroduce.setTextColor(color);
    }

    public void bindToolbar(ZToolBar toolBar) {
        this.toolBar = toolBar;
        buttonBarLayout = toolBar.getCenterCustomView();
        buttonBarLayout.setAlpha(0);
        ivToolbarAvater = toolBar.findViewById(R.id.toolbar_avatar);
        tvToolbarName = toolBar.findViewById(R.id.toolbar_name);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public MagicIndicator getMagicIndicator() {
        return magicIndicator;
    }

}
