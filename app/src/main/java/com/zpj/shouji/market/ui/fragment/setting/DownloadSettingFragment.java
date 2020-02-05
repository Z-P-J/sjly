package com.zpj.shouji.market.ui.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leon.lib.settingview.LSettingItem;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;

public class DownloadSettingFragment extends BaseSettingFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_download;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("下载设置");
        LSettingItem itemDownloadFolder = view.findViewById(R.id.item_download_folder);
        itemDownloadFolder.setOnLSettingItemClick(this);

        LSettingItem itemMaxDownloading = view.findViewById(R.id.item_max_downloading);
        itemMaxDownloading.setOnLSettingItemClick(this);

        LSettingItem itemMaxThread = view.findViewById(R.id.item_max_thread);
        itemMaxThread.setOnLSettingItemClick(this);

        LSettingItem itemInstallDownloaded = view.findViewById(R.id.item_install_downloaded);
        itemInstallDownloaded.setOnLSettingItemClick(this);

        LSettingItem itemShowDownloadedRing = view.findViewById(R.id.item_show_downloaded_ring);
        itemShowDownloadedRing.setOnLSettingItemClick(this);

        LSettingItem itemShowDownloadNotification = view.findViewById(R.id.item_show_downloaded_notification);
        itemShowDownloadNotification.setOnLSettingItemClick(this);
    }

    @Override
    public void click(View view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.item_download_folder:

                break;
            case R.id.item_max_downloading:

                break;
            case R.id.item_max_thread:

                break;
            case R.id.item_install_downloaded:

                break;
            case R.id.item_show_downloaded_ring:

                break;
            case R.id.item_show_downloaded_notification:

                break;
            default:
                break;
        }
    }

}
