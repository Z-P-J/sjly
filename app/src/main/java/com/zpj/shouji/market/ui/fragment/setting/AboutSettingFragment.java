package com.zpj.shouji.market.ui.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.widget.setting.CommonSettingItem;

public class AboutSettingFragment extends BaseSettingFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_about;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("关于应用");
        CommonSettingItem itemSearchEngine = view.findViewById(R.id.item_check_update);
        itemSearchEngine.setOnItemClickListener(this);
    }

    @Override
    public void onClick(CommonSettingItem item) {
        if (item.getId() == R.id.item_check_update) {

        }
    }
}
