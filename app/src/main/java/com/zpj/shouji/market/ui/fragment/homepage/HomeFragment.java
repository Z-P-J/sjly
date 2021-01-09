package com.zpj.shouji.market.ui.fragment.homepage;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.zpj.blur.ZBlurry;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.shouji.market.ui.fragment.manager.ManagerFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchFragment;
import com.zpj.shouji.market.ui.widget.indicator.HomePagerTitleView;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;

public class HomeFragment extends SkinFragment {

    private static final String[] TAB_TITLES = {"推荐", "发现", "乐图"};
    private static final int[] TAB_IMAGES = {R.drawable.ic_tab_recommend, R.drawable.ic_tab_discover, R.drawable.ic_tab_wallpaper};

    private ViewPager viewPager;

    private View shadowView;

    private ImageButton btnSearch;
    private ImageButton btnManage;

    private float alpha = 0f;

    private ZBlurry blurred;

//    public static class FirstFragment extends BaseContainerFragment {
//
//        @Override
//        protected SupportFragment getRootFragment() {
//            RecommendFragment2 fragment = findChildFragment(RecommendFragment2.class);
//            if (fragment == null) {
//                fragment = new RecommendFragment2();
//            }
//            return fragment;
//        }
//
//    }
//
//    public static class SecondFragment extends BaseContainerFragment {
//
//        @Override
//        protected SupportFragment getRootFragment() {
//            DiscoverFragment fragment = findChildFragment(DiscoverFragment.class);
//            if (fragment == null) {
//                fragment = DiscoverFragment.newInstance();
//            }
//            return fragment;
//        }
//
//    }
//
//    public static class ThirdFragment extends BaseContainerFragment {
//
//        @Override
//        protected SupportFragment getRootFragment() {
//            WallpaperFragment fragment = findChildFragment(WallpaperFragment.class);
//            if (fragment == null) {
//                fragment = new WallpaperFragment();
//            }
//            return fragment;
//        }
//
//    }
//
//    public static class FourthFragment extends BaseContainerFragment {
//
//        @Override
//        protected SupportFragment getRootFragment() {
//            RecommendFragment3 fragment = findChildFragment(RecommendFragment3.class);
//            if (fragment == null) {
//                fragment = new RecommendFragment3();
//            }
//            return fragment;
//        }
//
//    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBus.onSkinChangeEvent(this, s -> initStatusBar());
        EventBus.onScrollEvent(this, new RxBus.SingleConsumer<Float>() {
            @Override
            public void onAccept(Float percent) throws Exception {
                alpha = percent;
                initStatusBar();
            }
        });
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


        ArrayList<SupportFragment> list = new ArrayList<>();
        RecommendFragment recommendFragment = findChildFragment(RecommendFragment.class);
        if (recommendFragment == null) {
            recommendFragment = new RecommendFragment();
        }
//        RecommendFragment2 recommendFragment = findChildFragment(RecommendFragment2.class);
//        if (recommendFragment == null) {
//            recommendFragment = new RecommendFragment2();
//        }
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

//        FourthFragment fourthFragment = findChildFragment(FourthFragment.class);
//        if (fourthFragment == null) {
//            fourthFragment = new FourthFragment();
//        }

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
//        list.add(fourthFragment);

        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(list.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        list.get(0).onSupportVisible();
                        break;
                    case 1:
                        EventBus.sendScrollEvent(1);
                        break;
                    case 2:
                        EventBus.sendScrollEvent(0);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        MagicIndicator magicIndicator = (MagicIndicator) toolbar.getCenterCustomView();

        MagicIndicatorHelper.builder(context)
                .setMagicIndicator(magicIndicator)
                .setTabTitles(TAB_TITLES)
                .setViewPager(viewPager)
                .setOnGetTitleViewListener((context, index) -> {
                    HomePagerTitleView titleView = new HomePagerTitleView(context);
                    titleView.setImageResource(TAB_IMAGES[index]);
                    titleView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewPager.setCurrentItem(index);
                        }
                    });
                    return titleView;

//                    CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);
//                    View customLayout = LayoutInflater.from(context).inflate(R.layout.layout_pager_title, null);
//                    ImageView ivTitle = customLayout.findViewById(R.id.iv_title);
//                    ivTitle.setImageResource(TAB_IMAGES[index]);
//                    commonPagerTitleView.setContentView(customLayout);
//                    commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {
//                        @Override
//                        public void onSelected(int index, int totalCount) {
//                            ivTitle.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
//                        }
//
//                        @Override
//                        public void onDeselected(int index, int totalCount) {
//                            ivTitle.setColorFilter(context.getResources().getColor(R.color.color_text_normal));
//                        }
//
//                        @Override
//                        public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
//                            float scale = 1.2f + (0.8f - 1.2f) * leavePercent;
//                            customLayout.setScaleX(scale);
//                            customLayout.setScaleY(scale);
//                        }
//
//                        @Override
//                        public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
//                            float scale = 0.8f + (1.2f - 0.8f) * enterPercent;
//                            customLayout.setScaleX(scale);
//                            customLayout.setScaleY(scale);
//                        }
//                    });
//
//                    commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            viewPager.setCurrentItem(index);
//                        }
//                    });
//                    return commonPagerTitleView;



//                    ColorChangePagerTitleView titleView = new ColorChangePagerTitleView(context);
//                    titleView.setNormalColor(context.getResources().getColor(R.color.color_text_normal));
//                    titleView.setSelectedColor(context.getResources().getColor(R.color.colorPrimary));
//                    titleView.setTextSize(14);
//                    titleView.setText(TAB_TITLES[index]);
//                    titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index, true));
//                    return titleView;
                })
                .build();

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
        boolean isDark = alpha < 0.5f && isLazyInit();
        boolean isNightMode = AppConfig.isNightMode();
        if (isNightMode) {
//            ColorChangeEvent.post(isDark);
            EventBus.sendColorChangeEvent(isDark);
        } else {
            if (viewPager.getCurrentItem() != 0) {
                isDark = false;
//                ColorChangeEvent.post(false);
                EventBus.sendColorChangeEvent(false);
            } else {
//                ColorChangeEvent.post(isDark);
                EventBus.sendColorChangeEvent(isDark);
            }
        }

        boolean isLightStyle = !isDark;
        toolbar.setLightStyle(isLightStyle);
        shadowView.setVisibility(alpha > 0.5f ? View.VISIBLE : View.GONE);
//        int color = getResources().getColor((AppConfig.isNightMode() || isDark) ? R.color.white : R.color.color_text_major);
        int color = (AppConfig.isNightMode() || isDark) ? Color.WHITE : getResources().getColor(R.color.color_text_major);
        Log.d("HomeFragment", "isNight=" + AppConfig.isNightMode() + " isDark=" + isDark + " color=" + color);
        btnManage.setColorFilter(color);
        btnSearch.setColorFilter(color);

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
    
}
