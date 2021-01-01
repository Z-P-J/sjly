package com.zpj.shouji.market.ui.fragment.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.utils.AnimationUtil;
import com.zpj.widget.setting.OnCheckableItemClickListener;
import com.zpj.widget.setting.OnCommonItemClickListener;

public abstract class BaseSettingFragment extends BaseSwipeBackFragment
        implements OnCommonItemClickListener, OnCheckableItemClickListener {

//    @Override
//    protected boolean supportSwipeBack() {
//        return true;
//    }
//
//    @Override
//    public void onSupportVisible() {
//        super.onSupportVisible();
//        ThemeUtils.initStatusBar(this);
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        afterInitView();
    }

    protected void afterInitView() {
        LinearLayout container = findViewById(R.id.ll_container);
        View[] views = new View[container.getChildCount()];
        for (int i = 0; i < container.getChildCount(); i++) {
            views[i] = container.getChildAt(i);
        }
//        container.getChildCount() * 50
        AnimationUtil.doDelayShowAnim(500, 50, views);
    }

}
