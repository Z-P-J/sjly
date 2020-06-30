package com.zpj.shouji.market.ui.fragment.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.UserInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.UserListFragment;
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

public class MyFriendsFragment extends BaseFragment {

    private static final String[] TAB_TITLES = {"我关注的", "我的粉丝"};
    private static final String KEY_ID = "key_id";
    private static final String KEY_SHOW_TOOLBAR = "key_show_toolbar";

    protected ViewPager viewPager;
    private MagicIndicator magicIndicator;

    private String userId = "";
    private boolean showToolbar = true;

    public static MyFriendsFragment newInstance(String id, boolean showToolbar) {
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        args.putBoolean(KEY_SHOW_TOOLBAR, showToolbar);
        MyFriendsFragment fragment = new MyFriendsFragment();
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
            setToolbarTitle("我的好友");
            postOnEnterAnimationEnd(this::initViewPager);
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
        FollowersFragment followersFragment = findChildFragment(FollowersFragment.class);
        if (followersFragment == null) {
            followersFragment = FollowersFragment.newInstance(userId);
        }
        FansFragment fansFragment = findChildFragment(FansFragment.class);
        if (fansFragment == null) {
            fansFragment = FansFragment.newInstance(userId);
        }
        fragments.add(followersFragment);
        fragments.add(fansFragment);
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

    public static class FollowersFragment extends UserListFragment {
        public static FollowersFragment newInstance(String id) {
//            String url = "http://tt.shouji.com.cn/app/user_friend_list_xml.jsp";
            String url = "http://tt.tljpxm.com/app/view_member_friend_xml.jsp?mmid=" + id;
            Bundle args = new Bundle();
            args.putString(KEY_DEFAULT_URL, url);
            FollowersFragment fragment = new FollowersFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onClick(EasyViewHolder holder, View view, UserInfo data) {
            ProfileFragment.start(data.getMemberId(), true);
        }

    }

    public static class FansFragment extends UserListFragment {
        public static FansFragment newInstance(String id) {
//            String url = "http://tt.shouji.com.cn/app/user_fensi_list_xml.jsp";
            String url = "http://tt.tljpxm.com/app/view_member_fensi_xml.jsp?mmid=" + id;
            Bundle args = new Bundle();
            args.putString(KEY_DEFAULT_URL, url);
            FansFragment fragment = new FansFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onClick(EasyViewHolder holder, View view, UserInfo data) {
            ProfileFragment.start(data.getMemberId(), true);
        }

    }



}