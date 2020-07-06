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

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

public class MyLikeFragment extends BaseFragment {

    private static final String[] TAB_TITLES = {"收到的赞", "发出的赞"};

    protected ViewPager viewPager;
    private MagicIndicator magicIndicator;

    public static void start() {
        StartFragmentEvent.start(new MyLikeFragment());
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
        setToolbarTitle("我的赞");
        viewPager = view.findViewById(R.id.view_pager);
        magicIndicator = view.findViewById(R.id.magic_indicator);
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        List<Fragment> fragments = new ArrayList<>();
        ReceivedGoodFragment receivedGoodFragment = findChildFragment(ReceivedGoodFragment.class);
        if (receivedGoodFragment == null) {
            receivedGoodFragment = ReceivedGoodFragment.newInstance();
        }
        GiveLikeFragment giveLikeFragment = findChildFragment(GiveLikeFragment.class);
        if (giveLikeFragment == null) {
            giveLikeFragment = GiveLikeFragment.newInstance();
        }
        fragments.add(receivedGoodFragment);
        fragments.add(giveLikeFragment);
        viewPager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES));
        viewPager.setOffscreenPageLimit(fragments.size());
        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES);
    }

    @Override
    public void onDestroy() {
        HttpApi.updateFlagApi(UpdateFlagAction.GOOD);
        super.onDestroy();
    }

    public static class ReceivedGoodFragment extends ThemeListFragment {

        public static ReceivedGoodFragment newInstance() {
            String url = "http://tt.shouji.com.cn/app/user_content_flower_myself_xml_v2.jsp";
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            ReceivedGoodFragment fragment = new ReceivedGoodFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

    public static class GiveLikeFragment extends ThemeListFragment {

        public static GiveLikeFragment newInstance() {
            String url = "http://tt.shouji.com.cn/app/user_content_flower_send_xml_v2.jsp";
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            GiveLikeFragment fragment = new GiveLikeFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

}
