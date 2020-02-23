package com.zpj.shouji.market.ui.fragment.setting;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.widget.setting.CommonSettingItem;
import com.zpj.widget.setting.OnItemClickListener;

public abstract class BaseSettingFragment extends BaseFragment
        implements OnItemClickListener<CommonSettingItem> {

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }
}
