package com.zpj.shouji.market.ui.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leon.lib.settingview.LSettingItem;
import com.zpj.shouji.market.R;

public class InstallSettingFragment extends BaseSettingFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_install;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("安装设置");
        LSettingItem itemInstallDownloaded = view.findViewById(R.id.item_install_downloaded);
        itemInstallDownloaded.setOnLSettingItemClick(this);

        LSettingItem itemAutoDeleteApk = view.findViewById(R.id.item_auto_delete_apk);
        itemAutoDeleteApk.setOnLSettingItemClick(this);

        LSettingItem itemAutoInstall = view.findViewById(R.id.item_auto_install);
        itemAutoInstall.setOnLSettingItemClick(this);

        LSettingItem itemRootInstall = view.findViewById(R.id.item_root_install);
        itemRootInstall.setOnLSettingItemClick(this);
    }

    @Override
    public void click(View view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.item_install_downloaded:

                break;
            case R.id.item_auto_delete_apk:

                break;
            case R.id.item_auto_install:

                break;
            case R.id.item_root_install:

                break;
            default:
                break;
        }
    }

}
