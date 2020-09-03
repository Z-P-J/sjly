package com.zpj.shouji.market.ui.fragment.theme;

import android.graphics.Color;
import android.os.Bundle;
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
import com.felix.atoast.library.AToast;
import com.shehuan.niv.NiceImageView;
import com.zpj.popup.interfaces.OnDismissListener;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.RefreshEvent;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.glide.blur.BlurTransformation;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.adapter.DiscoverBinder;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.ListenerFragment;
import com.zpj.shouji.market.ui.fragment.login.LoginFragment;
import com.zpj.shouji.market.ui.widget.popup.CommentPopup;
import com.zpj.shouji.market.ui.widget.popup.ThemeMorePopupMenu;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.widget.tinted.TintedImageButton;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThemeDetailFragment extends ListenerFragment {

    private final String[] TAB_TITLES = {"评论", "点赞"};

    private FloatingActionButton fabComment;
    private CommentPopup commentPopup;
    private ViewPager viewPager;
    private MagicIndicator magicIndicator;

    private View buttonBarLayout;
    private NiceImageView ivToolbarAvater;
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
        StartFragmentEvent.start(fragment);
    }

    public static void start(DiscoverInfo item, String wallpaperUrl, FragmentLifeCycler lifeCycler) {
        Bundle args = new Bundle();
        args.putBoolean(Keys.SHOW_TOOLBAR, false);
        args.putString(Keys.URL, wallpaperUrl);
        ThemeDetailFragment fragment = new ThemeDetailFragment();
        fragment.setDiscoverInfo(item);
        fragment.setFragmentLifeCycler(lifeCycler);
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_theme_detail;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            wallpaper = getArguments().getString(Keys.URL, null);
        }

        View themeLayout = view.findViewById(R.id.layout_discover);

        ImageView ivBg = view.findViewById(R.id.iv_bg);
        if (!TextUtils.isEmpty(wallpaper)) {
            ivBg.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(wallpaper)
//                    .apply(RequestOptions.bitmapTransform(new BlurTransformation()))
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 2)))
                    .into(ivBg);
//            ShadowLayout shadowLayout = themeLayout.findViewById(R.id.layout_theme);
        }


        EasyViewHolder holder = new EasyViewHolder(themeLayout);
        DiscoverBinder binder = new DiscoverBinder(false, false);
        List<DiscoverInfo> discoverInfoList = new ArrayList<>();
        discoverInfoList.add(item);
        binder.onBindViewHolder(holder, discoverInfoList, 0, new ArrayList<>(0));
        holder.setOnItemLongClickListener(v -> {
            ThemeMorePopupMenu.with(context)
                    .setDiscoverInfo(item)
                    .show();
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
                if (TextUtils.isEmpty(wallpaper)) {
                    int color = alphaColor(Color.WHITE, alpha);
                    toolbar.setBackgroundColor(color);
                }
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
                .into(ivToolbarAvater);

        tvToolbarName.setText(item.getNickName());
        tvToolbarName.setTextColor(Color.BLACK);

        btnShare = toolbar.getRightCustomView().findViewById(R.id.btn_share);
        btnCollect = toolbar.getRightCustomView().findViewById(R.id.btn_collect);
        btnMenu = toolbar.getRightCustomView().findViewById(R.id.btn_menu);

        btnShare.setTint(Color.BLACK);
        btnCollect.setTint(Color.BLACK);
        btnMenu.setTint(Color.BLACK);

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

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        darkStatusBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (commentPopup != null) {
            commentPopup.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (commentPopup != null) {
            commentPopup.hide();
        }
    }

    private void showCommentPopup() {
        if (!UserManager.getInstance().isLogin()) {
            AToast.warning(R.string.text_msg_not_login);
            LoginFragment.start();
            return;
        }
        fabComment.hide();
        if (commentPopup == null) {
            commentPopup = CommentPopup.with(
                    context, item.getId(), item.getNickName(), item.getContentType(), () -> {
                        commentPopup = null;
                        RefreshEvent.postEvent();
                    })
                    .setDecorView(Objects.requireNonNull(getView()).findViewById(R.id.fl_container))
                    .setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            fabComment.show();
                        }
                    })
                    .show();
        }
        commentPopup.show();
    }

    private void setDiscoverInfo(DiscoverInfo discoverInfo) {
        this.item = discoverInfo;
    }

    public static int alphaColor(int color, float alpha) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & color;
        return a + rgb;
    }

}
