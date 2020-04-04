package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;

public class MyDiscoverFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_viewpager;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("我的发现");
    }

}
