package com.zpj.shouji.market.ui.fragment.search;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.AppListFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionListFragment;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
import com.zpj.shouji.market.ui.fragment.UserListFragment;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.utils.ScreenUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
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

        SearchAppListFragment appListFragment = findChildFragment(SearchAppListFragment.class);
        if (appListFragment == null) {
            appListFragment = new SearchAppListFragment();
        }

        SearchCollectionListFragment collectionListFragment = findChildFragment(SearchCollectionListFragment.class);
        if (collectionListFragment == null) {
            collectionListFragment = new SearchCollectionListFragment();
        }

        SearchThemeListFragment exploreFragment = findChildFragment(SearchThemeListFragment.class);
        if (exploreFragment == null) {
            exploreFragment = new SearchThemeListFragment();
        }

        SearchUserListFragment userListFragment = findChildFragment(SearchUserListFragment.class);
        if (userListFragment == null) {
            userListFragment = new SearchUserListFragment();
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
        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(TAB_TITLES.length);
        MagicIndicator magicIndicator = view.findViewById(R.id.magic_indicator);
        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES, true);
    }


    public static class SearchAppListFragment extends AppListFragment
            implements SearchResultFragment.KeywordObserver {

        private String keyword;

        @Override
        public void updateKeyword(String key) {
            if (TextUtils.equals(key, keyword)) {
                return;
            }
            this.keyword = key;

            defaultUrl = "http://tt.shouji.com.cn/androidv3/app_search_xml.jsp?sdk=26&type=default&s=" + key;
            nextUrl = defaultUrl;

            if (isLazyInit()) {
                recyclerLayout.showLoading();
                postOnSupportVisibleDelayed(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
                        getData();
                    }
                }, 250);
            }
        }

    }

    public static class SearchCollectionListFragment extends CollectionListFragment
            implements SearchResultFragment.KeywordObserver {

        private String keyword;

        @Override
        public void updateKeyword(String key) {
            if (TextUtils.equals(key, keyword)) {
                return;
            }
            this.keyword = key;

            defaultUrl = "http://tt.shouji.com.cn/androidv3/yyj_view_phb_xml.jsp?title=" + key;
            nextUrl = defaultUrl;

            if (isLazyInit()) {
                recyclerLayout.showLoading();
                postOnSupportVisibleDelayed(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
                        getData();
                    }
                }, 250);
            }
        }

    }

    public static class SearchThemeListFragment extends ThemeListFragment
            implements SearchResultFragment.KeywordObserver {

        private String keyword;

        @Override
        public void updateKeyword(String key) {
            if (TextUtils.equals(key, keyword)) {
                return;
            }
            this.keyword = key;

            defaultUrl = "http://tt.shouji.com.cn/app/faxian.jsp?s=" + key;
            nextUrl = defaultUrl;
//            if (isLazyInit()) {
//                onRefresh();
//            }

            if (isLazyInit()) {
                recyclerLayout.showLoading();
                postOnSupportVisibleDelayed(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
                        getData();
                    }
                }, 250);
            }
        }

    }

    public static class SearchUserListFragment extends UserListFragment
            implements SearchResultFragment.KeywordObserver {

        private String keyword;

        @Override
        public void updateKeyword(String key) {
            if (TextUtils.equals(key, keyword)) {
                return;
            }
            this.keyword = key;
            defaultUrl = "http://tt.shouji.com.cn/androidv3/app_search_user_xml.jsp?s=" + key;
            nextUrl = defaultUrl;
            if (isLazyInit()) {
                recyclerLayout.showLoading();
                postOnSupportVisibleDelayed(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
                        getData();
                    }
                }, 250);
            }
        }

    }

    @Subscribe
    public void onSearchEvent(SearchFragment.SearchEvent event) {
        Log.d("onSearchEvent", "keyword=" + event.keyword + " observers=" + observers.size());
        for (KeywordObserver observer : observers) {
            observer.updateKeyword(event.keyword);
        }
    }

}
