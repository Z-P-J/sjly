package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;

import com.zpj.shouji.market.ui.fragment.ExploreListFragment;

public class AppCommentFragment extends ExploreListFragment {

    public static AppCommentFragment newInstance(String url) {
        return newInstance(url, true);
    }

    public static AppCommentFragment newInstance(String url, boolean shouldLazyLoad) {
        AppCommentFragment fragment = new AppCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEFAULT_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

}
