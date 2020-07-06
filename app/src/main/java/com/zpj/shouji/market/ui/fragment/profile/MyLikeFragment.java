package com.zpj.shouji.market.ui.fragment.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
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
        ReceivedLikeFragment receivedLikeFragment = findChildFragment(ReceivedLikeFragment.class);
        if (receivedLikeFragment == null) {
            receivedLikeFragment = ReceivedLikeFragment.newInstance();
        }
        GiveLikeFragment giveLikeFragment = findChildFragment(GiveLikeFragment.class);
        if (giveLikeFragment == null) {
            giveLikeFragment = GiveLikeFragment.newInstance();
        }
        fragments.add(receivedLikeFragment);
        fragments.add(giveLikeFragment);
        viewPager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES));
        viewPager.setOffscreenPageLimit(fragments.size());
        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES);

//        CommonNavigator navigator = new CommonNavigator(getContext());
//        navigator.setAdapter(new CommonNavigatorAdapter() {
//            @Override
//            public int getCount() {
//                return TAB_TITLES.length;
//            }
//
//            @Override
//            public IPagerTitleView getTitleView(Context context, int index) {
//                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
//                titleView.setNormalColor(getResources().getColor(R.color.color_text_major));
//                titleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
//                titleView.setTextSize(14);
//                titleView.setText(TAB_TITLES[index]);
//                titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index, true));
//                return titleView;
//            }
//
//            @Override
//            public IPagerIndicator getIndicator(Context context) {
//                LinePagerIndicator indicator = new LinePagerIndicator(context);
//                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
//                indicator.setLineHeight(ScreenUtils.dp2px(context, 4f));
//                indicator.setLineWidth(ScreenUtils.dp2px(context, 12f));
//                indicator.setRoundRadius(ScreenUtils.dp2px(context, 4f));
//                indicator.setColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimary));
//                return indicator;
//            }
//        });
//        magicIndicator.setNavigator(navigator);
//        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    public static class ReceivedLikeFragment extends ThemeListFragment {

        public static ReceivedLikeFragment newInstance() {
            String url = "http://tt.shouji.com.cn/app/user_content_flower_myself_xml_v2.jsp";
            Bundle args = new Bundle();
            args.putString(KEY_DEFAULT_URL, url);
            ReceivedLikeFragment fragment = new ReceivedLikeFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

    public static class GiveLikeFragment extends ThemeListFragment {

        public static GiveLikeFragment newInstance() {
            String url = "http://tt.shouji.com.cn/app/user_content_flower_send_xml_v2.jsp";
            Bundle args = new Bundle();
            args.putString(KEY_DEFAULT_URL, url);
            GiveLikeFragment fragment = new GiveLikeFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

}
