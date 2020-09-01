package com.zpj.shouji.market.ui.fragment.homepage;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.ColorChangeEvent;
import com.zpj.shouji.market.event.ToolbarColorChangeEvent;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.manager.AppManagerFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchFragment;
import com.zpj.shouji.market.ui.widget.ColorChangePagerTitleView;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.widget.tinted.TintedImageButton;

import net.lucode.hackware.magicindicator.MagicIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class HomeFragment extends BaseFragment {

    private static final String[] TAB_TITLES = {"推荐", "发现", "乐图"};

    private View shadowView;

    private TintedImageButton btnSearch;
    private TintedImageButton btnManage;

    private boolean isLightStyle = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        shadowView = view.findViewById(R.id.view_shadow);
        toolbar.setLightStyle(false);


        ArrayList<Fragment> list = new ArrayList<>();
        RecommendFragment2 recommendFragment = findChildFragment(RecommendFragment2.class);
        if (recommendFragment == null) {
            recommendFragment = new RecommendFragment2();
        }
        DiscoverFragment exploreFragment = findChildFragment(DiscoverFragment.class);
        if (exploreFragment == null) {
            exploreFragment = DiscoverFragment.newInstance();
        }
        WallpaperFragment wallpaperFragment = findChildFragment(WallpaperFragment.class);
        if (wallpaperFragment == null) {
            wallpaperFragment = new WallpaperFragment();
        }
        list.add(recommendFragment);
        list.add(exploreFragment);
        list.add(wallpaperFragment);

        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    Log.d("isLightStyle", "isLightStyle=" + toolbar.isLightStyle());
                    ColorChangeEvent.post(!toolbar.isLightStyle());
                    shadowView.setVisibility(toolbar.isLightStyle() ? View.VISIBLE : View.GONE);
                } else {
                    ColorChangeEvent.post(false);
                    shadowView.setVisibility(View.GONE);
                }

//                ColorChangeEvent.post(i == 0);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        MagicIndicator magicIndicator = (MagicIndicator) toolbar.getCenterCustomView();

//        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES);

        MagicIndicatorHelper.builder(context)
                .setMagicIndicator(magicIndicator)
                .setTabTitles(TAB_TITLES)
                .setViewPager(viewPager)
                .setOnGetTitleViewListener((context, index) -> {
                    ColorChangePagerTitleView titleView = new ColorChangePagerTitleView(context);
                    titleView.setNormalColor(context.getResources().getColor(R.color.color_text_normal));
                    titleView.setSelectedColor(context.getResources().getColor(R.color.colorPrimary));
                    titleView.setTextSize(16);
                    titleView.setText(TAB_TITLES[index]);
                    titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index, true));
                    return titleView;
                })
                .build();

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (isLightStyle) {
            lightStatusBar();
        } else {
            darkStatusBar();
        }
    }

    @Override
    public void toolbarRightCustomView(@NonNull View view) {
        btnSearch = view.findViewById(R.id.btn_search);
        btnManage = view.findViewById(R.id.btn_manage);
        btnSearch.setOnClickListener(v -> SearchFragment.start());
        btnManage.setOnClickListener(v -> AppManagerFragment.start());
    }

    @Subscribe
    public void onColorChangeEvent(ColorChangeEvent event) {
        isLightStyle = event.isDark();
        int color = getResources().getColor(event.isDark() ? R.color.white : R.color.color_text_major);
        btnManage.setTint(color);
        btnSearch.setTint(color);
        Log.d("isLightStyle", "isDark=" + event.isDark());
        if (event.isDark()) {
            lightStatusBar();
        } else {
            darkStatusBar();
        }
    }

    @Subscribe
    public void onToolbarColorChangeEvent(ToolbarColorChangeEvent event) {
        toolbar.setBackgroundColor(event.getColor());
        toolbar.setLightStyle(event.isLightStyle());
        shadowView.setVisibility(event.isLightStyle() ? View.VISIBLE : View.GONE);
    }

}
