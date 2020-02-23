package com.zpj.shouji.market.ui.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.widget.setting.CommonSettingItem;

public class DownloadSettingFragment extends BaseSettingFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_download;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("下载设置");
        CommonSettingItem itemDownloadFolder = view.findViewById(R.id.item_download_folder);
        itemDownloadFolder.setOnItemClickListener(this);

        CommonSettingItem itemMaxDownloading = view.findViewById(R.id.item_max_downloading);
        itemMaxDownloading.setOnItemClickListener(this);

        CommonSettingItem itemMaxThread = view.findViewById(R.id.item_max_thread);
        itemMaxThread.setOnItemClickListener(this);

        CommonSettingItem itemInstallDownloaded = view.findViewById(R.id.item_install_downloaded);
        itemInstallDownloaded.setOnItemClickListener(this);

        CommonSettingItem itemShowDownloadedRing = view.findViewById(R.id.item_show_downloaded_ring);
        itemShowDownloadedRing.setOnItemClickListener(this);

        CommonSettingItem itemShowDownloadNotification = view.findViewById(R.id.item_show_downloaded_notification);
        itemShowDownloadNotification.setOnItemClickListener(this);
    }

    @Override
    public void onClick(CommonSettingItem item) {
        switch (item.getId()) {
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
