package com.zpj.shouji.market.ui.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.widget.setting.CheckableSettingItem;
import com.zpj.widget.setting.CommonSettingItem;
import com.zpj.widget.setting.OnItemClickListener;
import com.zpj.widget.setting.SwitchSettingItem;

public class DownloadSettingFragment extends BaseSettingFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_download;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("下载设置");
        CommonSettingItem itemDownloadFolder = view.findViewById(R.id.item_download_folder);
        itemDownloadFolder.setListener(this);

        CommonSettingItem itemMaxDownloading = view.findViewById(R.id.item_max_downloading);
        itemMaxDownloading.setListener(this);

        CommonSettingItem itemMaxThread = view.findViewById(R.id.item_max_thread);
        itemMaxThread.setListener(this);

        SwitchSettingItem itemInstallDownloaded = view.findViewById(R.id.item_install_downloaded);
        itemInstallDownloaded.setListener(this);

        SwitchSettingItem itemShowDownloadedRing = view.findViewById(R.id.item_show_downloaded_ring);
        itemShowDownloadedRing.setListener(this);

        SwitchSettingItem itemShowDownloadNotification = view.findViewById(R.id.item_show_downloaded_notification);
        itemShowDownloadNotification.setListener(this);
    }

    @Override
    public void onItemClick(CommonSettingItem item) {
        switch (item.getId()) {
            case R.id.item_download_folder:

                break;
            case R.id.item_max_downloading:

                break;
            case R.id.item_max_thread:

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(CheckableSettingItem item) {
        switch (item.getId()) {
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
