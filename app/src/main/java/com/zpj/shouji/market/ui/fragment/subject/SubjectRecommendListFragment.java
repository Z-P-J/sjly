package com.zpj.shouji.market.ui.fragment.subject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;

public class SubjectRecommendListFragment extends SubjectListFragment {

    public static void start(String defaultUrl) {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, defaultUrl);
        SubjectRecommendListFragment fragment = new SubjectRecommendListFragment();
        fragment.setArguments(args);
        start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_with_toolbar;
    }

//    @Override
//    public void onSupportVisible() {
//        super.onSupportVisible();
//        ThemeUtils.initStatusBar(this);
//    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        setToolbarTitle("专题");
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }
}
