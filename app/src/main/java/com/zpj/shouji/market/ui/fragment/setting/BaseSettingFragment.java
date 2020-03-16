package com.zpj.shouji.market.ui.fragment.setting;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.widget.setting.OnCheckableItemClickListener;
import com.zpj.widget.setting.OnCommonItemClickListener;

import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public abstract class BaseSettingFragment extends BaseFragment
        implements OnCommonItemClickListener, OnCheckableItemClickListener {

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

}
