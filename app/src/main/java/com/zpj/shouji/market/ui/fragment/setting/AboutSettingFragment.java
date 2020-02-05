package com.zpj.shouji.market.ui.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leon.lib.settingview.LSettingItem;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;

public class AboutSettingFragment extends BaseSettingFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_about;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("关于应用");
        LSettingItem itemSearchEngine = view.findViewById(R.id.item_check_update);
        itemSearchEngine.setOnLSettingItemClick(this);
    }

    @Override
    public void click(View view, boolean isChecked) {
        if (view.getId() == R.id.item_check_update) {

        }
    }

}
