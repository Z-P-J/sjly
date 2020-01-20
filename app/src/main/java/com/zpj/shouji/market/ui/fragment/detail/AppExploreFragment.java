package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;

import com.zpj.shouji.market.ui.fragment.ExploreListFragment;

public class AppExploreFragment extends ExploreListFragment {

    public static AppExploreFragment newInstance(String url) {
        return newInstance(url, true);
    }

    public static AppExploreFragment newInstance(String url, boolean shouldLazyLoad) {
        AppExploreFragment fragment = new AppExploreFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEFAULT_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

}
