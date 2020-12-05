package com.zpj.shouji.market.ui.fragment.theme;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.felix.atoast.library.AToast;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.EventBus;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.glide.transformations.CircleWithBorderTransformation;
import com.zpj.shouji.market.glide.transformations.blur.BlurTransformation;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.adapter.DiscoverBinder;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.fragment.dialog.CommentDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.ShareDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.ThemeMoreDialogFragment;
import com.zpj.shouji.market.ui.fragment.login.LoginFragment;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.widget.tinted.TintedImageButton;
import com.zxy.skin.sdk.SkinEngine;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

public class ThemeDetailFragment extends BaseSwipeBackFragment {

    private final String[] TAB_TITLES = {"评论", "点赞"};

    private FloatingActionButton fabComment;
    private CommentDialogFragment commentDialogFragment;
    private ViewPager viewPager;
    private MagicIndicator magicIndicator;

    private View buttonBarLayout;
    private ImageView ivToolbarAvater;
    private TextView tvToolbarName;

    private TintedImageButton btnShare;
    private TintedImageButton btnCollect;
    private TintedImageButton btnMenu;

    private DiscoverInfo item;

    private String wallpaper;

    public static void start(DiscoverInfo item) {
        start(item, false);
    }

    public static void start(DiscoverInfo item, boolean showCommentPopup) {
        Bundle args = new Bundle();
        args.putBoolean(Keys.SHOW_TOOLBAR, showCommentPopup);
        ThemeDetailFragment fragment = new ThemeDetailFragment();
        fragment.setDiscoverInfo(item);
        fragment.setArguments(args);
        start(fragment);
    }

//    public static void start(DiscoverInfo item, String wallpaperUrl, FragmentLifeCycler lifeCycler) {
//        Bundle args = new Bundle();
//        args.putBoolean(Keys.SHOW_TOOLBAR, false);
//        args.putString(Keys.URL, wallpaperUrl);
//        ThemeDetailFragment fragment = new ThemeDetailFragment();
//        fragment.setDiscoverInfo(item);
//        fragment.setFragmentLifeCycler(lifeCycler);
//        fragment.setArguments(args);
//        start(fragment);
//    }

    public static void start(DiscoverInfo item, String wallpaperUrl) {
        Bundle args = new Bundle();
        args.putBoolean(Keys.SHOW_TOOLBAR, false);
        args.putString(Keys.URL, wallpaperUrl);
        ThemeDetailFragment fragment = new ThemeDetailFragment();
        fragment.setDiscoverInfo(item);
        fragment.setArguments(args);
        start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_theme_detail;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            wallpaper = getArguments().getString(Keys.URL, null);
        }

        View themeLayout = view.findViewById(R.id.layout_discover);

        toolbar.setLightStyle(AppConfig.isNightMode());

        ImageView ivBg = view.findViewById(R.id.iv_bg);
        if (!TextUtils.isEmpty(wallpaper)) {
            Glide.with(context)
                    .load(wallpaper)
//                    .apply(RequestOptions.bitmapTransform(new BlurTransformation()))
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 2)))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            ivBg.setImageDrawable(resource);
                            int color = AppConfig.isNightMode() ? Color.parseColor("#aa000000") : Color.parseColor("#ccffffff");
                            ivBg.setColorFilter(color);
                            ivBg.setVisibility(View.VISIBLE);
//                            final Drawable wrappedDrawable = DrawableCompat.wrap(ivBg.getDrawable().mutate());
//
//                            DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(color));
//                            ivBg.setImageDrawable(wrappedDrawable);
                        }
                    });
//            ShadowLayout shadowLayout = themeLayout.findViewById(R.id.layout_theme);
        }


        EasyViewHolder holder = new EasyViewHolder(themeLayout);
        DiscoverBinder binder = new DiscoverBinder(false, false);
        List<DiscoverInfo> discoverInfoList = new ArrayList<>();
        discoverInfoList.add(item);
        binder.onBindViewHolder(holder, discoverInfoList, 0, new ArrayList<>(0));
        holder.setOnItemLongClickListener(v -> {
//            ThemeMorePopupMenu.with(context)
//                    .setDiscoverInfo(item)
//                    .show();
            new ThemeMoreDialogFragment()
                    .setDiscoverInfo(item)
                    .show(context);
            return true;
        });

        AppBarLayout appBarLayout = view.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
//                float alpha = (float) Math.abs(i) / appBarLayout.getTotalScrollRange();
                float alpha = (float) Math.abs(i) / toolbar.getMeasuredHeight();
                alpha = Math.min(1f, alpha);
                buttonBarLayout.setAlpha(alpha);

                alpha = (float) Math.abs(i) / appBarLayout.getTotalScrollRange();
                if (alpha >= 1f) {
                    themeLayout.setAlpha(0f);
                } else {
                    themeLayout.setAlpha(1f);
                }
//                if (TextUtils.isEmpty(wallpaper)) {
//                    int color = ColorUtils.alphaColor(Color.WHITE, alpha);
//                    toolbar.setBackgroundColor(color);
//                }
            }
        });

        viewPager = view.findViewById(R.id.view_pager);
        magicIndicator = view.findViewById(R.id.magic_indicator);

        buttonBarLayout = toolbar.getCenterCustomView();
        buttonBarLayout.setAlpha(0);
        ivToolbarAvater = toolbar.findViewById(R.id.toolbar_avatar);
        tvToolbarName = toolbar.findViewById(R.id.toolbar_name);

        Glide.with(context)
                .load(item.getIcon())
                .apply(
                        GlideRequestOptions.with()
                                .addTransformation(new CircleWithBorderTransformation(0.5f, Color.LTGRAY))
                                .get()
                                .error(R.mipmap.ic_launcher)
                                .placeholder(R.mipmap.ic_launcher)
                )
                .into(ivToolbarAvater);

        tvToolbarName.setText(item.getNickName());
        SkinEngine.setTextColor(tvToolbarName, R.attr.textColorMajor);
//        tvToolbarName.setTextColor(Color.BLACK);

        btnShare = toolbar.getRightCustomView().findViewById(R.id.btn_share);
        btnCollect = toolbar.getRightCustomView().findViewById(R.id.btn_collect);
        btnMenu = toolbar.getRightCustomView().findViewById(R.id.btn_menu);

//        btnShare.setTint(Color.BLACK);
        SkinEngine.applyViewAttr(btnShare, "tint", R.attr.textColorMajor);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareDialogFragment()
                        .setShareContent(getString(R.string.text_theme_share_content, item.getContent(), item.getId()))
                        .show(context);
            }
        });

        SkinEngine.applyViewAttr(btnCollect, "tint", R.attr.textColorMajor);

        btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpApi.addCollectionApi(item.getId(), new Runnable() {
                    @Override
                    public void run() {
                        btnCollect.setImageResource(R.drawable.ic_star_black_24dp);
                        btnCollect.setTint(Color.RED);
                    }
                });
//                HttpApi.deleteCollectionApi(info.getId());
            }
        });

//        btnMenu.setTint(Color.BLACK);
        SkinEngine.applyViewAttr(btnMenu, "tint", R.attr.textColorMajor);

        fabComment = view.findViewById(R.id.fab_comment);
        fabComment.setOnClickListener(v -> {
            showCommentPopup();
        });
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        ArrayList<Fragment> list = new ArrayList<>();

        ThemeCommentListFragment discoverListFragment = findChildFragment(ThemeCommentListFragment.class);
        if (discoverListFragment == null) {
            discoverListFragment = ThemeCommentListFragment.newInstance(item.getId(), item.getContentType());
        }
        //    private List<Fragment> fragments = new ArrayList<>();
        SupportUserListFragment supportUserListFragment = findChildFragment(SupportUserListFragment.class);
        if (supportUserListFragment == null) {
            supportUserListFragment = SupportUserListFragment.newInstance(item.getId());
        }
        list.add(discoverListFragment);
        list.add(supportUserListFragment);

        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(list.size());


        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES, true);

        if (getArguments() != null) {
            if (getArguments().getBoolean(Keys.SHOW_TOOLBAR, false)) {
                showCommentPopup();
            }
        }
    }

//    @Override
//    public void onSupportVisible() {
//        super.onSupportVisible();
//        ThemeUtils.initStatusBar(this);
//    }

    @Override
    public void onResume() {
        super.onResume();
//        if (commentPopup != null) {
//            commentPopup.show();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (commentPopup != null) {
//            commentPopup.hide();
//        }
    }

    @Override
    public void onDestroy() {
        commentDialogFragment = null;
        super.onDestroy();
    }

    private void showCommentPopup() {
        if (!UserManager.getInstance().isLogin()) {
            AToast.warning(R.string.text_msg_not_login);
            LoginFragment.start();
            return;
        }
        fabComment.hide();
        if (commentDialogFragment == null) {
            commentDialogFragment = CommentDialogFragment.with(
                    context, item.getId(), item.getNickName(), item.getContentType(), () -> {
                        commentDialogFragment = null;
                        EventBus.sendRefreshEvent();
                    });
            commentDialogFragment.setOnDismissListener(() -> fabComment.show());
        }
        commentDialogFragment.show(context);
    }

    private void setDiscoverInfo(DiscoverInfo discoverInfo) {
        this.item = discoverInfo;
    }

}
