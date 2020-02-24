package com.zpj.shouji.market.ui.fragment.homepage;

import android.os.Bundle;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;

public class DiscoverFragment extends ThemeListFragment {

    private static final String DEFAULT_URL = "http://tt.shouji.com.cn/app/faxian.jsp?index=faxian&versioncode=198";

    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEFAULT_URL, DEFAULT_URL);
        fragment.setArguments(bundle);
        return fragment;
    }

//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_discover;
//    }

}
