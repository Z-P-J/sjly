package com.zpj.shouji.market.ui.fragment.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.AppListFragment;
import com.zpj.shouji.market.ui.fragment.WallpaperListFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionListFragment;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
import com.zpj.utils.ScreenUtils;

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

public class MyCollectionFragment extends BaseFragment {

    private static final String[] TAB_TITLES = {"应用", "应用集", "发现", "乐图", "评论", "专题", "攻略", "教程"};
    private static final String KEY_ID = "key_id";
    private static final String KEY_SHOW_TOOLBAR = "key_show_toolbar";

    protected ViewPager viewPager;
    private MagicIndicator magicIndicator;

    private String userId = "";
    private boolean showToolbar = true;

    public static MyCollectionFragment newInstance(String id, boolean showToolbar) {
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        args.putBoolean(KEY_SHOW_TOOLBAR, showToolbar);
        MyCollectionFragment fragment = new MyCollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void start(String id) {
        StartFragmentEvent.start(newInstance(id, true));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_discover;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            userId = getArguments().getString(KEY_ID, "");
            showToolbar = getArguments().getBoolean(KEY_SHOW_TOOLBAR, true);
        }

        viewPager = view.findViewById(R.id.view_pager);
        magicIndicator = view.findViewById(R.id.magic_indicator);

        if (showToolbar) {
            postOnEnterAnimationEnd(this::initViewPager);
            setToolbarTitle("我的收藏");
        } else {
            toolbar.setVisibility(View.GONE);
            setSwipeBackEnable(false);
        }
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (!showToolbar) {
//            initViewPager();
            postOnEnterAnimationEnd(this::initViewPager);
        }
    }

    private void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        MyCollectionAppFragment myRelatedDiscoverFragment = findChildFragment(MyCollectionAppFragment.class);
        if (myRelatedDiscoverFragment == null) {
            myRelatedDiscoverFragment = MyCollectionAppFragment.newInstance(userId);
        }

        MyCollectionsFragment myCollectionsFragment = findChildFragment(MyCollectionsFragment.class);
        if (myCollectionsFragment == null) {
            myCollectionsFragment = MyCollectionsFragment.newInstance(userId);
        }


        MyCollectionDiscoverFragment myCollectionDiscoverFragment = findChildFragment(MyCollectionDiscoverFragment.class);
        if (myCollectionDiscoverFragment == null) {
            myCollectionDiscoverFragment = MyCollectionDiscoverFragment.newInstance(userId);
        }

        MyCollectionWallpaperFragment myCollectionWallpaperFragment = findChildFragment(MyCollectionWallpaperFragment.class);
        if (myCollectionWallpaperFragment == null) {
            myCollectionWallpaperFragment = MyCollectionWallpaperFragment.newInstance(userId);
        }

        MyCollectionCommentFragment myCollectionCommentFragment = findChildFragment(MyCollectionCommentFragment.class);
        if (myCollectionCommentFragment == null) {
            myCollectionCommentFragment = MyCollectionCommentFragment.newInstance(userId);
        }

        fragments.add(myRelatedDiscoverFragment);
        fragments.add(myCollectionsFragment);
        fragments.add(myCollectionDiscoverFragment);
        fragments.add(myCollectionWallpaperFragment);
        fragments.add(myCollectionCommentFragment);

        fragments.add(new SupportFragment());
        fragments.add(new SupportFragment());
        fragments.add(new SupportFragment());

        viewPager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES));
        viewPager.setOffscreenPageLimit(fragments.size());

        CommonNavigator navigator = new CommonNavigator(getContext());
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return TAB_TITLES.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleView.setNormalColor(getResources().getColor(R.color.color_text_major));
                titleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
                titleView.setTextSize(14);
                titleView.setText(TAB_TITLES[index]);
                titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index, true));
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(ScreenUtils.dp2px(context, 4f));
                indicator.setLineWidth(ScreenUtils.dp2px(context, 12f));
                indicator.setRoundRadius(ScreenUtils.dp2px(context, 4f));
                indicator.setColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimary));
                return indicator;
            }
        });
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    public static class MyCollectionAppFragment extends AppListFragment {

        public static MyCollectionAppFragment newInstance(String id) {
            Bundle args = new Bundle();
            args.putString(KEY_DEFAULT_URL, HttpApi.myCollectionAppsUrl(id));
            MyCollectionAppFragment fragment = new MyCollectionAppFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

    public static class MyCollectionsFragment extends CollectionListFragment {

        public static MyCollectionsFragment newInstance(String id) {
            Bundle args = new Bundle();
            args.putString(KEY_DEFAULT_URL, HttpApi.myCollectionsUrl(id));
            MyCollectionsFragment fragment = new MyCollectionsFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

    public static class MyCollectionDiscoverFragment extends ThemeListFragment {

        public static MyCollectionDiscoverFragment newInstance(String id) {
            Bundle args = new Bundle();
            args.putString(KEY_DEFAULT_URL, HttpApi.myCollectionDiscoverUrl(id));
            MyCollectionDiscoverFragment fragment = new MyCollectionDiscoverFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

    public static class MyCollectionWallpaperFragment extends WallpaperListFragment {

        public static MyCollectionWallpaperFragment newInstance(String id) {
            Bundle args = new Bundle();
            args.putString(KEY_DEFAULT_URL, HttpApi.myCollectionWallpaperUrl(id));
            MyCollectionWallpaperFragment fragment = new MyCollectionWallpaperFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        protected void handleArguments(Bundle arguments) {
            defaultUrl = arguments.getString(KEY_DEFAULT_URL, "");
            nextUrl = defaultUrl;
        }

        @Override
        public void onRefresh() {
            data.clear();
            nextUrl = defaultUrl;
            recyclerLayout.notifyDataSetChanged();
        }

        @Override
        protected int getHeaderLayout() {
            return 0;
        }
    }

    public static class MyCollectionCommentFragment extends ThemeListFragment {

        public static MyCollectionCommentFragment newInstance(String id) {
            Bundle args = new Bundle();
            args.putString(KEY_DEFAULT_URL, HttpApi.myCollectionCommentUrl(id));
            MyCollectionCommentFragment fragment = new MyCollectionCommentFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

}
