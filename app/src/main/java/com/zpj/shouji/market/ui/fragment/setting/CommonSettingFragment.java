package com.zpj.shouji.market.ui.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.widget.setting.CheckableSettingItem;
import com.zpj.widget.setting.CommonSettingItem;

public class CommonSettingFragment extends BaseSettingFragment {

    public static void start() {
        StartFragmentEvent.start(new CommonSettingFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_common;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("通用设置");

        CheckableSettingItem itemShowStartPage = view.findViewById(R.id.item_show_start_page);
        itemShowStartPage.setOnItemClickListener(this);

        CheckableSettingItem itemShowUpdateNotification = view.findViewById(R.id.item_show_update_notification);
        itemShowUpdateNotification.setOnItemClickListener(this);

        CommonSettingItem itemBrightnessControl = view.findViewById(R.id.item_brightness_control);
        itemBrightnessControl.setOnItemClickListener(this);



        CheckableSettingItem itemAutoSaveTraffic = view.findViewById(R.id.item_auto_save_traffic);
        itemAutoSaveTraffic.setOnItemClickListener(this);

        CheckableSettingItem itemShowOriginalImage = view.findViewById(R.id.item_show_original_image);
        itemShowOriginalImage.setOnItemClickListener(this);

        CommonSettingItem itemClearCache = view.findViewById(R.id.item_clear_cache);
        itemClearCache.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(CheckableSettingItem item) {
        switch (item.getId()) {
            case R.id.item_show_start_page:

                break;
            case R.id.item_show_update_notification:

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

    @Override
    public void onItemClick(CommonSettingItem item) {
        if (item.getId() == R.id.item_brightness_control) {

        }
    }
}
