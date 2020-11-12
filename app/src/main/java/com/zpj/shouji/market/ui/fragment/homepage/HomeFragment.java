package com.zpj.shouji.market.ui.fragment.homepage;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.zpj.blur.ZBlurry;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.event.ColorChangeEvent;
import com.zpj.shouji.market.event.ScrollChangeEvent;
import com.zpj.shouji.market.event.SkinChangeEvent;
import com.zpj.shouji.market.event.ToolbarColorChangeEvent;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseContainerFragment;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.shouji.market.ui.fragment.manager.ManagerFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchFragment;
import com.zpj.shouji.market.ui.widget.ColorChangePagerTitleView;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.widget.tinted.TintedImageButton;

import net.lucode.hackware.magicindicator.MagicIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class HomeFragment extends SkinFragment {

    private static final String[] TAB_TITLES = {"推荐", "发现", "乐图", "测试"};

    private ViewPager viewPager;

    private View shadowView;

    private TintedImageButton btnSearch;
    private TintedImageButton btnManage;

    private boolean isLightStyle = false;

    private float alpha = 0f;

    private ZBlurry blurred;

    public static class FirstFragment extends BaseContainerFragment {

        @Override
        protected SupportFragment getRootFragment() {
            RecommendFragment2 fragment = findChildFragment(RecommendFragment2.class);
            if (fragment == null) {
                fragment = new RecommendFragment2();
            }
            return fragment;
        }

    }

    public static class SecondFragment extends BaseContainerFragment {

        @Override
        protected SupportFragment getRootFragment() {
            DiscoverFragment fragment = findChildFragment(DiscoverFragment.class);
            if (fragment == null) {
                fragment = DiscoverFragment.newInstance();
            }
            return fragment;
        }

    }

    public static class ThirdFragment extends BaseContainerFragment {

        @Override
        protected SupportFragment getRootFragment() {
            WallpaperFragment fragment = findChildFragment(WallpaperFragment.class);
            if (fragment == null) {
                fragment = new WallpaperFragment();
            }
            return fragment;
        }

    }

    public static class FourthFragment extends BaseContainerFragment {

        @Override
        protected SupportFragment getRootFragment() {
            RecommendFragment3 fragment = findChildFragment(RecommendFragment3.class);
            if (fragment == null) {
                fragment = new RecommendFragment3();
            }
            return fragment;
        }

    }

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

        viewPager = findViewById(R.id.view_pager);
        shadowView = view.findViewById(R.id.view_shadow);
        toolbar.setLightStyle(false);

        blurred = ZBlurry.with(findViewById(R.id.fl_blur))
//                .fitIntoViewXY(false)
//                .antiAlias(true)
                .scale(0.1f)
                .radius(20)
//                .maxFps(40)
                .blur(toolbar, new ZBlurry.Callback() {
                    @Override
                    public void down(Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(bitmap);
                        drawable.setAlpha((int) (alpha * 255));
                        toolbar.setBackground(drawable, true);
                    }
                });
        blurred.pauseBlur();


        ArrayList<SupportFragment> list = new ArrayList<>();
//        RecommendFragment3 recommendFragment = findChildFragment(RecommendFragment3.class);
//        if (recommendFragment == null) {
//            recommendFragment = new RecommendFragment3();
//        }
        RecommendFragment2 recommendFragment = findChildFragment(RecommendFragment2.class);
        if (recommendFragment == null) {
            recommendFragment = new RecommendFragment2();
        }
//        SupportFragment recommendFragment = new SupportFragment();
        DiscoverFragment exploreFragment = findChildFragment(DiscoverFragment.class);
        if (exploreFragment == null) {
            exploreFragment = DiscoverFragment.newInstance();
        }
        WallpaperFragment wallpaperFragment = findChildFragment(WallpaperFragment.class);
        if (wallpaperFragment == null) {
            wallpaperFragment = new WallpaperFragment();
        }

//        RecommendFragment3 recommendFragment3 = findChildFragment(RecommendFragment3.class);
//        if (recommendFragment3 == null) {
//            recommendFragment3 = new RecommendFragment3();
//        }

        FourthFragment fourthFragment = findChildFragment(FourthFragment.class);
        if (fourthFragment == null) {
            fourthFragment = new FourthFragment();
        }

//        ArrayList<Fragment> list = new ArrayList<>();
//        FirstFragment recommendFragment = findChildFragment(FirstFragment.class);
//        if (recommendFragment == null) {
//            recommendFragment = new FirstFragment();
//        }
//        SecondFragment exploreFragment = findChildFragment(SecondFragment.class);
//        if (exploreFragment == null) {
//            exploreFragment = new SecondFragment();
//        }
//        ThirdFragment wallpaperFragment = findChildFragment(ThirdFragment.class);
//        if (wallpaperFragment == null) {
//            wallpaperFragment = new ThirdFragment();
//        }

//        list.add(DiscoverFragment.newInstance());
        list.add(recommendFragment);
        list.add(exploreFragment);
        list.add(wallpaperFragment);
        list.add(fourthFragment);

        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(list.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
//                if (!AppConfig.isNightMode()) {
//                    if (i == 0) {
//                        Log.d("isLightStyle", "isLightStyle=" + toolbar.isLightStyle());
//                        ColorChangeEvent.post(!toolbar.isLightStyle());
//                        shadowView.setVisibility(toolbar.isLightStyle() ? View.VISIBLE : View.GONE);
//                    } else {
//                        ColorChangeEvent.post(false);
//                        shadowView.setVisibility(View.GONE);
//                    }
//                    initStatusBar();
//                }

//                if (blurred != null) {
//                    if (i == 2) {
//                        blurred.pauseBlur();
//                    } else {
//                        blurred.startBlur();
//                    }
//                }

                switch (i) {
                    case 0:
                        list.get(0).onSupportVisible();
                        break;
                    case 1:
                        ScrollChangeEvent.post(1);
                        break;
                    case 2:
                        ScrollChangeEvent.post(0);
                        break;
                }


//                ColorChangeEvent.post(i == 0);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
//                if (blurred != null) {
//                    if (i == 2 && viewPager.getCurrentItem() == 2) {
//                        blurred.pauseBlur();
//                    } else {
//                        blurred.startBlur();
//                    }
//                }
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
                    titleView.setTextSize(14);
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
        if (blurred != null) {
            blurred.startBlur();
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (blurred != null) {
            blurred.pauseBlur();
        }
    }

    @Override
    protected void initStatusBar() {
//        if (isLightStyle) {
//            lightStatusBar();
//        } else {
//            darkStatusBar();
//        }

        boolean isDark = alpha < 0.5f && isLazyInit();
        boolean isNightMode = AppConfig.isNightMode();
        if (isNightMode) {
            ColorChangeEvent.post(isDark);
        } else {
            if (viewPager.getCurrentItem() != 0) {
                isDark = false;
                ColorChangeEvent.post(false);
            } else {
                ColorChangeEvent.post(isDark);
            }
        }

        isLightStyle = !isDark;
        toolbar.setLightStyle(isLightStyle);
        shadowView.setVisibility(alpha > 0.5f ? View.VISIBLE : View.GONE);
        int color = getResources().getColor((AppConfig.isNightMode() || isDark) ? R.color.white : R.color.color_text_major);
        btnManage.setTint(color);
        btnSearch.setTint(color);

        if (isSupportVisible()) {
            if (AppConfig.isNightMode() || isDark) {
                lightStatusBar();
            } else {
                darkStatusBar();
            }
        }

    }

    @Override
    public void toolbarRightCustomView(@NonNull View view) {
        btnSearch = view.findViewById(R.id.btn_search);
        btnManage = view.findViewById(R.id.btn_manage);
        btnSearch.setOnClickListener(v -> SearchFragment.start());
        btnManage.setOnClickListener(v -> ManagerFragment.start());
    }

    @Subscribe
    public void onSkinChangeEvent(SkinChangeEvent event) {
        initStatusBar();
    }

    @Subscribe
    public void onScrollChangeEvent(ScrollChangeEvent event) {
        alpha = event.getPercent();
        initStatusBar();

//        boolean isDark = alpha < 0.5f;
//
//        isLightStyle = isDark;
//        toolbar.setLightStyle(isLightStyle);
//        shadowView.setVisibility(isLightStyle ? View.VISIBLE : View.GONE);
//        int color = getResources().getColor((AppConfig.isNightMode() || isDark) ? R.color.white : R.color.color_text_major);
//        btnManage.setTint(color);
//        btnSearch.setTint(color);
//
//        if (AppConfig.isNightMode() || isDark) {
//            lightStatusBar();
//        } else {
//            darkStatusBar();
//        }
    }

    @Subscribe
    public void onColorChangeEvent(ColorChangeEvent event) {
//        isLightStyle = event.isDark();
//        int color = getResources().getColor(event.isDark() ? R.color.white : R.color.color_text_major);
//        btnManage.setTint(color);
//        btnSearch.setTint(color);
//        Log.d("isLightStyle", "isDark=" + event.isDark());
//        if (event.isDark()) {
//            lightStatusBar();
//        } else {
//            darkStatusBar();
//        }
    }

    @Subscribe
    public void onToolbarColorChangeEvent(ToolbarColorChangeEvent event) {
//        toolbar.setBackgroundColor(event.getColor());
//        toolbar.setLightStyle(event.isLightStyle());
//        shadowView.setVisibility(event.isLightStyle() ? View.VISIBLE : View.GONE);
    }

}
