package com.zpj.shouji.market.ui.fragment.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.markdown.MarkdownEditorFragment;
import com.zpj.markdown.MarkdownViewFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
import com.zpj.shouji.market.ui.widget.ScaleTransitionPagerTitleView;
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

public class MyDiscoverFragment extends BaseFragment {

    private static final String[] TAB_TITLES = {"与我有关", "我的发现", "私有发现"};

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
        MyRelatedDiscoverFragment myRelatedDiscoverFragment = findChildFragment(MyRelatedDiscoverFragment.class);
        if (myRelatedDiscoverFragment == null) {
            myRelatedDiscoverFragment = MyRelatedDiscoverFragment.newInstance();
        }
        MyPublishDiscoverFragment myPublishDiscoverFragment = findChildFragment(MyPublishDiscoverFragment.class);
        if (myPublishDiscoverFragment == null) {
            myPublishDiscoverFragment = MyPublishDiscoverFragment.newInstance();
        }
        MyPrivateDiscoverFragment myPrivateDiscoverFragment = findChildFragment(MyPrivateDiscoverFragment.class);
        if (myPrivateDiscoverFragment == null) {
            myPrivateDiscoverFragment = MyPrivateDiscoverFragment.newInstance();
        }
        fragments.add(myRelatedDiscoverFragment);
        fragments.add(myPublishDiscoverFragment);
        fragments.add(myPrivateDiscoverFragment);
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

    public static class MyRelatedDiscoverFragment extends ThemeListFragment {

        public static MyRelatedDiscoverFragment newInstance() {
            String url = "http://tt.shouji.com.cn/app/user_content_list_xml_v2.jsp?t=discuss&thread=thread";
            Bundle args = new Bundle();
            args.putString(KEY_DEFAULT_URL, url);
            MyRelatedDiscoverFragment fragment = new MyRelatedDiscoverFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

    public static class MyPublishDiscoverFragment extends ThemeListFragment {

        public static MyPublishDiscoverFragment newInstance() {
            String url = "http://tt.shouji.com.cn/app/user_content_list_xml_v2.jsp?t=discuss";
            Bundle args = new Bundle();
            args.putString(KEY_DEFAULT_URL, url);
            MyPublishDiscoverFragment fragment = new MyPublishDiscoverFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

    public static class MyPrivateDiscoverFragment extends ThemeListFragment {

        public static MyPrivateDiscoverFragment newInstance() {
            String url = "http://tt.shouji.com.cn/app/user_content_list_xml_v2.jsp?t=discuss&thread=private";
            Bundle args = new Bundle();
            args.putString(KEY_DEFAULT_URL, url);
            MyPrivateDiscoverFragment fragment = new MyPrivateDiscoverFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

}
