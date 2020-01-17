package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.fragment.main.homepage.ExploreFragment;

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
