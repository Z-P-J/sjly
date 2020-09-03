package com.zpj.shouji.market.ui.fragment.setting;

import android.view.View;
import android.widget.LinearLayout;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.AnimationUtil;
import com.zpj.widget.setting.OnCheckableItemClickListener;
import com.zpj.widget.setting.OnCommonItemClickListener;

import com.zpj.fragmentation.anim.DefaultNoAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;

public abstract class BaseSettingFragment extends BaseFragment
        implements OnCommonItemClickListener, OnCheckableItemClickListener {

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        darkStatusBar();
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
