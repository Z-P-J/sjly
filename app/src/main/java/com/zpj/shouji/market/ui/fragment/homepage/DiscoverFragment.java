package com.zpj.shouji.market.ui.fragment.homepage;

import android.os.Bundle;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;

public class DiscoverFragment extends ThemeListFragment {

    public static DiscoverFragment newInstance(String url) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEFAULT_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_discover;
//    }

}
