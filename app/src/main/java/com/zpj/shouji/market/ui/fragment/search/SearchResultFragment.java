package com.zpj.shouji.market.ui.fragment.search;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.adapter.ZFragmentPagerAdapter;
import com.zpj.shouji.market.ui.fragment.AppListFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionListFragment;
import com.zpj.shouji.market.ui.fragment.discover.DiscoverListFragment;
import com.zpj.shouji.market.ui.fragment.UserListFragment;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.widget.DotPagerIndicator;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends BaseFragment {

    public interface KeywordObserver {
        void updateKeyword(String keyword);
    }

    private static final String[] TAB_TITLES = {"应用", "应用集", "发现", "用户"}; // "专题"
    private ZFragmentPagerAdapter adapter;
    private final List<KeywordObserver> observers = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        List<Fragment> list = new ArrayList<>();

        AppListFragment appListFragment = findChildFragment(AppListFragment.class);
        if (appListFragment == null) {
            appListFragment = new AppListFragment();
        }

        CollectionListFragment collectionListFragment = findChildFragment(CollectionListFragment.class);
        if (collectionListFragment == null) {
            collectionListFragment = new CollectionListFragment();
        }

        DiscoverListFragment exploreFragment = findChildFragment(DiscoverListFragment.class);
        if (exploreFragment == null) {
            exploreFragment = new DiscoverListFragment();
        }

        UserListFragment userListFragment = findChildFragment(UserListFragment.class);
        if (userListFragment == null) {
            userListFragment = new UserListFragment();
        }

        list.add(appListFragment);
        list.add(collectionListFragment);
        list.add(exploreFragment);
        list.add(userListFragment);

        for (Fragment fragment : list) {
            if (fragment instanceof KeywordObserver) {
                observers.add((KeywordObserver) fragment);
            }
        }
        adapter = new ZFragmentPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(TAB_TITLES.length);
        MagicIndicator magicIndicator = view.findViewById(R.id.magic_indicator);
        CommonNavigator navigator = new CommonNavigator(getContext());
        navigator.setAdjustMode(true);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return TAB_TITLES.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleView.setNormalColor(Color.parseColor("#fafafa"));
                titleView.setSelectedColor(Color.WHITE);
                titleView.setTextSize(14);
                titleView.setText(TAB_TITLES[index]);
                titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index));
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return new DotPagerIndicator(context);
            }
        });
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    @Subscribe
    public void onSearchEvent(SearchFragment.SearchEvent event) {
        for (KeywordObserver observer : observers) {
            observer.updateKeyword(event.keyword);
        }
    }

}
