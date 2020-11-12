package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.constant.UpdateFlagAction;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.shouji.market.utils.ThemeUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

public class MyDiscoverFragment extends BaseFragment {

    private static final String[] TAB_TITLES = {"我的发现", "与我有关", "私有发现"};

    protected ViewPager viewPager;
    private MagicIndicator magicIndicator;

    public static void start() {
        StartFragmentEvent.start(new MyDiscoverFragment());
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
        setToolbarTitle("我的发现");
        viewPager = view.findViewById(R.id.view_pager);
        magicIndicator = view.findViewById(R.id.magic_indicator);
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        List<Fragment> fragments = new ArrayList<>();
        MyPublishDiscoverFragment myPublishDiscoverFragment = findChildFragment(MyPublishDiscoverFragment.class);
        if (myPublishDiscoverFragment == null) {
            myPublishDiscoverFragment = MyPublishDiscoverFragment.newInstance();
        }
        MyRelatedDiscoverFragment myRelatedDiscoverFragment = findChildFragment(MyRelatedDiscoverFragment.class);
        if (myRelatedDiscoverFragment == null) {
            myRelatedDiscoverFragment = MyRelatedDiscoverFragment.newInstance();
        }

        MyPrivateDiscoverFragment myPrivateDiscoverFragment = findChildFragment(MyPrivateDiscoverFragment.class);
        if (myPrivateDiscoverFragment == null) {
            myPrivateDiscoverFragment = MyPrivateDiscoverFragment.newInstance();
        }
        fragments.add(myPublishDiscoverFragment);
        fragments.add(myRelatedDiscoverFragment);
        fragments.add(myPrivateDiscoverFragment);
        viewPager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES));
        viewPager.setOffscreenPageLimit(fragments.size());

        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        ThemeUtils.initStatusBar(this);
    }

    @Override
    public void onDestroy() {
        HttpApi.updateFlagApi(UpdateFlagAction.DISCOVER);
        super.onDestroy();
    }

    public static class MyRelatedDiscoverFragment extends ThemeListFragment {

        public static MyRelatedDiscoverFragment newInstance() {
            String url = "http://tt.shouji.com.cn/app/user_content_list_xml_v2.jsp?t=discuss";
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            MyRelatedDiscoverFragment fragment = new MyRelatedDiscoverFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

    public static class MyPublishDiscoverFragment extends ThemeListFragment {

        public static MyPublishDiscoverFragment newInstance() {
            String url = "http://tt.shouji.com.cn/app/user_content_list_xml_v2.jsp?t=discuss&thread=thread";
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            MyPublishDiscoverFragment fragment = new MyPublishDiscoverFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

    public static class MyPrivateDiscoverFragment extends ThemeListFragment {

        public static MyPrivateDiscoverFragment newInstance() {
            String url = "http://tt.shouji.com.cn/app/user_content_list_xml_v2.jsp?t=discuss&thread=private";
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            MyPrivateDiscoverFragment fragment = new MyPrivateDiscoverFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

}
