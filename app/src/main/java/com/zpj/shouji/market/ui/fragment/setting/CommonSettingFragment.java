package com.zpj.shouji.market.ui.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leon.lib.settingview.LSettingItem;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;

public class CommonSettingFragment extends BaseSettingFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_common;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("通用设置");

        LSettingItem itemShowStartPage = view.findViewById(R.id.item_show_start_page);
        itemShowStartPage.setOnLSettingItemClick(this);

        LSettingItem itemShowUpdateNotification = view.findViewById(R.id.item_show_update_notification);
        itemShowUpdateNotification.setOnLSettingItemClick(this);

        LSettingItem itemBrightnessControl = view.findViewById(R.id.item_brightness_control);
        itemBrightnessControl.setOnLSettingItemClick(this);



        LSettingItem itemAutoSaveTraffic = view.findViewById(R.id.item_auto_save_traffic);
        itemAutoSaveTraffic.setOnLSettingItemClick(this);

        LSettingItem itemShowOriginalImage = view.findViewById(R.id.item_show_original_image);
        itemShowOriginalImage.setOnLSettingItemClick(this);

        LSettingItem itemClearCache = view.findViewById(R.id.item_clear_cache);
        itemClearCache.setOnLSettingItemClick(this);
    }

    @Override
    public void click(View view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.item_show_start_page:

                break;
            case R.id.item_show_update_notification:

                break;
            case R.id.item_brightness_control:

                break;
            case R.id.item_auto_save_traffic:

                break;
            case R.id.item_show_original_image:

                break;
            case R.id.item_clear_cache:

                break;
            default:
                break;
        }
    }

}
