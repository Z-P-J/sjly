package com.zpj.shouji.market.ui.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;

public class SearchFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

    }

}
