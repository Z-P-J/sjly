package com.zpj.shouji.market.ui.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.zpj.downloader.ZDownloader;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.widget.setting.CheckableSettingItem;
import com.zpj.widget.setting.CommonSettingItem;
import com.zpj.widget.setting.SwitchSettingItem;

public class DownloadSettingFragment extends BaseSettingFragment {

    public static void start() {
        StartFragmentEvent.start(new DownloadSettingFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_download;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("下载设置");
        CommonSettingItem itemDownloadFolder = view.findViewById(R.id.item_download_folder);
        itemDownloadFolder.setInfoText(AppConfig.getDownloadPath());
        itemDownloadFolder.setOnItemClickListener(this);

        CommonSettingItem itemMaxDownloading = view.findViewById(R.id.item_max_downloading);
        itemMaxDownloading.setInfoText(String.valueOf(AppConfig.getMaxDownloadConcurrentCount()));
        itemMaxDownloading.setOnItemClickListener(this);

        CommonSettingItem itemMaxThread = view.findViewById(R.id.item_max_thread);
        itemMaxThread.setInfoText(String.valueOf(AppConfig.getMaxDownloadThreadCount()));
        itemMaxThread.setOnItemClickListener(this);

        SwitchSettingItem itemInstallDownloaded = view.findViewById(R.id.item_install_downloaded);
        itemInstallDownloaded.setChecked(AppConfig.isInstallAfterDownloaded());
        itemInstallDownloaded.setOnItemClickListener(this);

//        SwitchSettingItem itemShowDownloadedRing = view.findViewById(R.id.item_show_downloaded_ring);
//        itemShowDownloadedRing.setOnItemClickListener(this);

        SwitchSettingItem itemShowDownloadNotification = view.findViewById(R.id.item_show_downloaded_notification);
        itemShowDownloadNotification.setChecked(AppConfig.isShowDownloadNotification());
        itemShowDownloadNotification.setOnItemClickListener(this);
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
                AppConfig.setInstallAfterDownloaded(item.isChecked());
                break;
//            case R.id.item_show_downloaded_ring:
//
//                break;
            case R.id.item_show_downloaded_notification:
                AppConfig.setShowDownloadNotification(item.isChecked());
                ZDownloader.setEnableNotification(item.isChecked());
                break;
            default:
                break;
        }
    }
}
