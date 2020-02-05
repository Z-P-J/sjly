package com.zpj.shouji.market.ui.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leon.lib.settingview.LSettingItem;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;

public abstract class BaseSettingFragment extends BaseFragment
        implements LSettingItem.OnLSettingItemClick {

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }
}
