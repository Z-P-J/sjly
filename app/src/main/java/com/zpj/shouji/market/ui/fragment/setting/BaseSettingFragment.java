package com.zpj.shouji.market.ui.fragment.setting;

import com.leon.lib.settingview.LSettingItem;
import com.zpj.fragmentation.BaseFragment;

public abstract class BaseSettingFragment extends BaseFragment
        implements LSettingItem.OnLSettingItemClick {

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }
}
