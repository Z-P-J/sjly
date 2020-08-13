package com.zpj.shouji.market.ui.fragment.theme;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shehuan.niv.NiceImageView;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.adapter.DiscoverBinder;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.widget.popup.AppCommentPopup;
import com.zpj.shouji.market.ui.widget.popup.CommentPopup;
import com.zpj.shouji.market.ui.widget.popup.ThemeMorePopupMenu;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.tinted.TintedImageButton;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.List;

public class ThemeDetailFragment extends BaseFragment {

    private final String[] TAB_TITLES = {"评论", "赞"};

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

    public static void start(DiscoverInfo item, boolean showCommentPopup) {
        Bundle args = new Bundle();
        args.putBoolean(Keys.SHOW_TOOLBAR, showCommentPopup);
        ThemeDetailFragment fragment = new ThemeDetailFragment();
        fragment.setDiscoverInfo(item);
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_discover_detail;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        View themeLayout = view.findViewById(R.id.layout_discover);
        EasyViewHolder holder = new EasyViewHolder(themeLayout);
        DiscoverBinder binder = new DiscoverBinder(false);
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
                float alpha = (float) Math.abs(i) / appBarLayout.getTotalScrollRange();
                alpha = Math.min(1f, alpha);
                buttonBarLayout.setAlpha(alpha);
                if (alpha >= 1f) {
                    themeLayout.setAlpha(0f);
                } else {
                    themeLayout.setAlpha(1f);
                }
                int color = alphaColor(Color.WHITE, alpha);
                toolbar.setBackgroundColor(color);
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

        View fabComment = view.findViewById(R.id.fab_comment);
        fabComment.setOnClickListener(v -> {
            commentPopup = AppCommentPopup.with(context, item.getId(), item.getContentType(), "").show();
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
            supportUserListFragment = new SupportUserListFragment();
        }
        list.add(discoverListFragment);
        list.add(supportUserListFragment);

        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(list.size());


        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES, true);
        supportUserListFragment.setData(item.getSupportUserInfoList());

        if (getArguments() != null) {
            if (getArguments().getBoolean(Keys.SHOW_TOOLBAR, false)) {
                commentPopup = CommentPopup.with(context, item.getId(), item.getContentType()).show();
            }
        }
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

    private void setDiscoverInfo(DiscoverInfo discoverInfo) {
        this.item = discoverInfo;
    }

    public static int alphaColor(int color, float alpha) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & color;
        return a + rgb;
    }

}
