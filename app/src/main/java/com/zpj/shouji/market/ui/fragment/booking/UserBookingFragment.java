package com.zpj.shouji.market.ui.fragment.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.BookingApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.model.BookingAppInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.dialog.BottomListMenuDialogFragment;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

public class UserBookingFragment extends BaseSwipeBackFragment {

    private static final String[] TAB_TITLES = {"已预约", "已上线"};

    protected ViewPager viewPager;
    private MagicIndicator magicIndicator;

    public static void start() {
        start(new UserBookingFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_discover;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("我的预约");
        viewPager = view.findViewById(R.id.view_pager);
        magicIndicator = view.findViewById(R.id.magic_indicator);
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        List<Fragment> fragments = new ArrayList<>();
        BookedFragment bookedFragment = findChildFragment(BookedFragment.class);
        if (bookedFragment == null) {
            bookedFragment = BookedFragment.newInstance();
        }
        PublishedFragment publishedFragment = findChildFragment(PublishedFragment.class);
        if (publishedFragment == null) {
            publishedFragment = PublishedFragment.newInstance();
        }
        fragments.add(bookedFragment);
        fragments.add(publishedFragment);
        viewPager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES));
        viewPager.setOffscreenPageLimit(fragments.size());

        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES, true);
    }

    public static class BookedFragment extends BookingAppListFragment {

        public static BookedFragment newInstance() {
            String url = "http://tt.shouji.com.cn/appv3/user_game_yuyue_list.jsp";
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            BookedFragment fragment = new BookedFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onClick(EasyViewHolder holder, View view, BookingAppInfo data) {
            AppDetailFragment.start(data.getAppType(), data.getAppId());
        }

        @Override
        public boolean onLongClick(EasyViewHolder holder, View view, BookingAppInfo appInfo) {
            new BottomListMenuDialogFragment()
                    .setMenu(R.menu.menu_booking)
                    .addHideItem(appInfo.isAutoDownload() ?  R.id.auto_download : R.id.cancel_auto_download)
                    .onItemClick((menu, view1, data) -> {
                        Runnable successRunnable = () -> {
                            menu.dismiss();
                            onRefresh();
                        };
                        switch (data.getItemId()) {
                            case R.id.cancel_booking:
                                showCancelBookingPopup(appInfo, successRunnable);
//                                BookingApi.cancelBookingApi(appInfo, successRunnable);
                                break;
                            case R.id.auto_download:
                                BookingApi.autoDownloadApi(appInfo, successRunnable);
                                break;
                            case R.id.cancel_auto_download:
                                BookingApi.cancelAutoDownloadApi(appInfo, successRunnable);
                                break;
                        }
                    })
                    .show(context);
            return true;
        }
    }

    public static class PublishedFragment extends BookingAppListFragment {

        public static PublishedFragment newInstance() {
            String url = "http://tt.shouji.com.cn/appv3/app_yuyue_online.jsp";
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            PublishedFragment fragment = new PublishedFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

}
