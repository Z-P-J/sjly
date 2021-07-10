package com.zpj.shouji.market.ui.fragment.manager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.zpj.downloader.BaseMission;
import com.zpj.downloader.DownloadManager;
import com.zpj.downloader.ZDownloader;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.model.AppUpdateInfo;
import com.zpj.shouji.market.model.IgnoredUpdateInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.shouji.market.ui.widget.indicator.BadgePagerTitle;
import com.zpj.utils.ScreenUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import java.util.ArrayList;
import java.util.List;

public class ManagerFragment extends BaseSwipeBackFragment {

    private static final String[] TAB_TITLES = {"下载管理", "更新", "已安装", "安装包"};

    private ViewPager viewPager;

    public static void start() {
        start(new ManagerFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_manager;
    }

    @Override
    public CharSequence getToolbarTitle(Context context) {
        return "应用管理";
    }

    @Override
    protected void initStatusBar() {
        lightStatusBar();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        DownloadManagerFragment downloadManagerFragment = findChildFragment(DownloadManagerFragment.class);
        if (downloadManagerFragment == null) {
            downloadManagerFragment = DownloadManagerFragment.newInstance(false);
        }

        UpdateManagerFragment updateManagerFragment = findChildFragment(UpdateManagerFragment.class);
        if (updateManagerFragment == null) {
            updateManagerFragment = UpdateManagerFragment.newInstance(false);
        }

        InstalledManagerFragment installedManagerFragment = findChildFragment(InstalledManagerFragment.class);
        if (installedManagerFragment == null) {
            installedManagerFragment = InstalledManagerFragment.newInstance(false);
        }

        PackageManagerFragment packageFragment = findChildFragment(PackageManagerFragment.class);
        if (packageFragment == null) {
            packageFragment = PackageManagerFragment.newInstance(false);
        }

        List<SkinFragment> fragments = new ArrayList<>();
        fragments.add(downloadManagerFragment);
        fragments.add(updateManagerFragment);
        fragments.add(installedManagerFragment);
        fragments.add(packageFragment);

        viewPager = findViewById(R.id.view_pager);

        postOnEnterAnimationEnd(() -> {
            FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(fragments.size());
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    switch (i) {
                        case 0:
                            toolbar.setRightButtonImage(R.drawable.ic_setting);
                            break;
                        case 1:
                            toolbar.setRightButtonImage(R.drawable.ic_search);
                            break;
                        case 2:
                            toolbar.setRightButtonImage(R.drawable.ic_search);
                            break;
                        case 3:
                            toolbar.setRightButtonImage(R.drawable.ic_search);
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

            MagicIndicator magicIndicator = findViewById(R.id.magic_indicator);
            CommonNavigator navigator = new CommonNavigator(getContext());
            navigator.setAdjustMode(true);
            navigator.setAdapter(new CommonNavigatorAdapter() {
                @Override
                public int getCount() {
                    return TAB_TITLES.length;
                }

                @Override
                public IPagerTitleView getTitleView(Context context, int index) {

                    BadgePagerTitle badgePagerTitle = new BadgePagerTitle(context);
                    badgePagerTitle.setAdjustMode(true);
                    badgePagerTitle.setNormalColor(Color.WHITE);
                    badgePagerTitle.setSelectedColor(Color.WHITE);
                    badgePagerTitle.setAutoCancelBadge(false);
                    badgePagerTitle.setTitle(TAB_TITLES[index]);
                    badgePagerTitle.setOnClickListener(view1 -> viewPager.setCurrentItem(index));
                    if (index == 0) {
                        ZDownloader.getAllMissions(new DownloadManager.OnLoadMissionListener<BaseMission<?>>() {
                            @Override
                            public void onLoaded(List<BaseMission<?>> missions) {
                                badgePagerTitle.setBadgeCount(missions.size());
                            }
                        });
                    } else if (index == 1) {
                        AppUpdateManager.getInstance().addCheckUpdateListener(new AppUpdateManager.CheckUpdateListener() {
                            @Override
                            public void onCheckUpdateFinish(List<AppUpdateInfo> updateInfoList, List<IgnoredUpdateInfo> ignoredUpdateInfoList) {
                                badgePagerTitle.setBadgeCount(updateInfoList.size());
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
                    } else {
                        badgePagerTitle.hideBadge();
                    }
                    return badgePagerTitle;
                }

                @Override
                public IPagerIndicator getIndicator(Context context) {
                    LinePagerIndicator indicator = new LinePagerIndicator(context);
                    indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                    indicator.setLineHeight(ScreenUtils.dp2px(context, 4f));
                    indicator.setLineWidth(ScreenUtils.dp2px(context, 12f));
                    indicator.setRoundRadius(ScreenUtils.dp2px(context, 4f));
                    indicator.setColors(Color.WHITE, Color.WHITE);
                    return indicator;
                }
            });
            magicIndicator.setNavigator(navigator);
            ViewPagerHelper.bind(magicIndicator, viewPager);
        });


    }

    public void showUpdateFragment() {
        postOnSupportVisible(() -> {
            if (viewPager != null) {
                viewPager.setCurrentItem(1, true);
            }
        });
    }

    public void showDownloadFragment() {
        postOnSupportVisible(() -> {
            if (viewPager != null) {
                viewPager.setCurrentItem(0, true);
            }
        });
    }

}
