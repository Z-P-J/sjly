package com.zpj.shouji.market.ui.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.widget.setting.CommonSettingItem;

public class InstallSettingFragment extends BaseSettingFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_install;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("安装设置");
        CommonSettingItem itemInstallDownloaded = view.findViewById(R.id.item_install_downloaded);
        itemInstallDownloaded.setOnItemClickListener(this);

        CommonSettingItem itemAutoDeleteApk = view.findViewById(R.id.item_auto_delete_apk);
        itemAutoDeleteApk.setOnItemClickListener(this);

        CommonSettingItem itemAutoInstall = view.findViewById(R.id.item_auto_install);
        itemAutoInstall.setOnItemClickListener(this);

        CommonSettingItem itemRootInstall = view.findViewById(R.id.item_root_install);
        itemRootInstall.setOnItemClickListener(this);
    }

    @Override
    public void onClick(CommonSettingItem item) {
        switch (item.getId()) {
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
