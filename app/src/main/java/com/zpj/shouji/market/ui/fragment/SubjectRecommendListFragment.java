package com.zpj.shouji.market.ui.fragment;

import android.os.Bundle;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.StartFragmentEvent;

public class SubjectRecommendListFragment extends SubjectListFragment {

    public static void start(String defaultUrl) {
        Bundle args = new Bundle();
        args.putString(KEY_DEFAULT_URL, defaultUrl);
        SubjectRecommendListFragment fragment = new SubjectRecommendListFragment();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_with_toolbar;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }
}
