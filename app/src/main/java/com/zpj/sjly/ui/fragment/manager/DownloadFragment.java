package com.zpj.sjly.ui.fragment.manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.sjly.R;
import com.zpj.sjly.ui.fragment.base.BaseFragment;

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
