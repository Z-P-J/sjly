package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.bean.AppDetailInfo;
import com.zpj.shouji.market.ui.adapter.ImgAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.fragment.main.homepage.ExploreFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AppCommentFragment extends ExploreFragment {

//    private
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onDestroy() {
//        EventBus.getDefault().unregister(this);
//        super.onDestroy();
//    }
//
//    @Subscribe
//    public void onGetAppDetailInfo(AppDetailInfo info) {
//
//    }

    public static AppCommentFragment newInstance(String url) {
        return newInstance(url, true);
    }

    public static AppCommentFragment newInstance(String url, boolean shouldLazyLoad) {
        AppCommentFragment fragment = new AppCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("default_url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

}
