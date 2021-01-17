package com.zpj.shouji.market.ui.fragment.manager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.zpj.downloader.BaseMission;
import com.zpj.downloader.DownloadManager;
import com.zpj.downloader.ZDownloader;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.rxlife.RxLife;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.database.IgnoredUpdateManager;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.model.AppUpdateInfo;
import com.zpj.shouji.market.model.IgnoredUpdateInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.widget.indicator.BadgePagerTitle;
import com.zpj.utils.ScreenUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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

        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(downloadManagerFragment);
        fragments.add(updateManagerFragment);
        fragments.add(installedManagerFragment);
        fragments.add(packageFragment);

        viewPager = findViewById(R.id.view_pager);

        postOnEnterAnimationEnd(() -> {
            FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(4);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    switch (i) {
                        case 0:
                            toolbar.setRightButtonImage(R.drawable.ic_settings_white_24dp);
                            break;
                        case 1:
                            toolbar.setRightButtonImage(R.drawable.ic_search_white_24dp);
                            break;
                        case 2:
                            toolbar.setRightButtonImage(R.drawable.ic_search_white_24dp);
                            break;
                        case 3:
                            toolbar.setRightButtonImage(R.drawable.ic_search_white_24dp);
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


//                    ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
//                    titleView.setNormalColor(Color.WHITE);
//                    titleView.setSelectedColor(Color.WHITE);
//                    titleView.setTextSize(14);
//                    titleView.setText(TAB_TITLES[index]);
//                    titleView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view1) {
//                            viewPager.setCurrentItem(index);
//                        }
//                    });
//                    BadgePagerTitleView badgePagerTitleView = new BadgePagerTitleView(context);
//                    badgePagerTitleView.setInnerPagerTitleView(titleView);
//                    if (index == 0) {
//                        int min = ScreenUtils.dp2pxInt(context, 20);
//                        int padding = ScreenUtils.dp2pxInt(context, 4);
//                        TextView mTvUnreadCount = new TextView(context);
//                        mTvUnreadCount.setBackgroundResource(R.drawable.bg_msg_bubble);
//                        mTvUnreadCount.setText("12");
//                        mTvUnreadCount.setMinWidth(min);
//                        mTvUnreadCount.setTextSize(10);
//                        mTvUnreadCount.setTextColor(Color.WHITE);
//                        mTvUnreadCount.setPadding(padding, 0, padding, 0);
//                        mTvUnreadCount.setGravity(Gravity.CENTER);
//                        badgePagerTitleView.setBadgeView(mTvUnreadCount);
//                        badgePagerTitleView.setXBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_RIGHT, 0));
//                        badgePagerTitleView.setYBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_TOP, 0));
//                        badgePagerTitleView.setAutoCancelBadge(false);
//                    }
//                    return badgePagerTitleView;
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

    @Override
    public int getLaunchMode() {
        return super.getLaunchMode();
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
