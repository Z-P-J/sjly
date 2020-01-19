package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;

import com.zpj.shouji.market.ui.fragment.homepage.ExploreFragment;

public class AppExploreFragment extends ExploreFragment {

    public static AppExploreFragment newInstance(String url) {
        return newInstance(url, true);
    }

    public static AppExploreFragment newInstance(String url, boolean shouldLazyLoad) {
        AppExploreFragment fragment = new AppExploreFragment();
        Bundle bundle = new Bundle();
        bundle.putString("default_url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

}
