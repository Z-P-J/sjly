package com.zpj.shouji.market.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.shouji.market.R;

public abstract class BaseContainerFragment extends BaseFragment {

    private SupportFragment fragment;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_child;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        fragment = getRootFragment();
        if (fragment != null) {
            loadRootFragment(R.id.fl_container, fragment);
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (fragment != null) {
            fragment.onSupportVisible();
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (fragment != null) {
            fragment.onSupportInvisible();
        }
    }

    protected abstract SupportFragment getRootFragment();


}
