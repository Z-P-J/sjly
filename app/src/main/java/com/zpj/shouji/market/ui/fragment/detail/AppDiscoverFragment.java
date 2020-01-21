package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;

import com.zpj.shouji.market.ui.fragment.discover.DiscoverListFragment;

public class AppDiscoverFragment extends DiscoverListFragment {

    public static AppDiscoverFragment newInstance(String url) {
        AppDiscoverFragment fragment = new AppDiscoverFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEFAULT_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

}
