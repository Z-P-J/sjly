package com.zpj.shouji.market.ui.fragment.manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;

public class DownloadFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_update;
    }

    @Override
    protected boolean supportSwipeBack() {
        return false;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

    }
}
