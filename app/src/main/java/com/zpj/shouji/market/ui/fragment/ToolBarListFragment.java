package com.zpj.shouji.market.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.StartFragmentEvent;

public class ToolBarListFragment extends AppListFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_with_toolbar;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    public static void start(String url, String title) {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, url);
        args.putString(Keys.TITLE, title);
        ToolBarListFragment fragment = new ToolBarListFragment();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    public static void startRecentUpdate() {
        start("http://tt.shouji.com.cn/androidv3/app_list_xml.jsp?index=1", "最近更新");
    }

    public static void startSubjectDetail(String id) {
        start("http://tt.shouji.com.cn/androidv3/special_list_xml.jsp?id=" + id, "专题详情");
    }

    public static void startRecommendSoftList() {
        start("http://tt.shouji.com.cn/androidv3/special_list_xml.jsp?id=-9998", "应用推荐");
    }

    public static void startUpdateSoftList() {
        start("http://tt.shouji.com.cn/androidv3/soft_index_xml.jsp?sort=time", "最新应用");
    }

    public static void startRecommendGameList() {
        start("http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sdk=100&sort=day", "游戏推荐");
    }

    public static void startUpdateGameList() {
        start("http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sort=time", "最新游戏");
    }

    public static void startNetGameList() {
        start("http://tt.shouji.com.cn/androidv3/netgame.jsp", "热门网游");
    }

    public static void startRecentDownload() {
        start("http://tt.shouji.com.cn/androidv3/app_downing_xml.jsp", "看看-乐友们都在下载什么");
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        if (getArguments() != null) {
            setToolbarTitle(getArguments().getString(Keys.TITLE, "Title"));
        }

    }
}
