package com.zpj.shouji.market.ui.fragment.setting;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.downloader.ZDownloader;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.http.core.IHttp;
import com.zpj.http.core.ObservableTask;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.ui.fragment.dialog.BrightnessDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.SeekBarDialogFragment;
import com.zpj.shouji.market.utils.DataCleanManagerUtils;
import com.zpj.utils.RootUtils;
import com.zpj.widget.setting.CheckableSettingItem;
import com.zpj.widget.setting.CommonSettingItem;
import com.zpj.widget.setting.SwitchSettingItem;

public class SettingFragment extends BaseSettingFragment {

    public static void start() {
        start(new SettingFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public CharSequence getToolbarTitle(Context context) {
        return "设置";
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        CheckableSettingItem itemShowStartPage = view.findViewById(R.id.item_show_start_page);
        itemShowStartPage.setChecked(AppConfig.isShowSplash());
        itemShowStartPage.setOnItemClickListener(this);

        CheckableSettingItem itemShowUpdateNotification = view.findViewById(R.id.item_show_update_notification);
        itemShowUpdateNotification.setChecked(AppConfig.isShowUpdateNotification());
        itemShowUpdateNotification.setOnItemClickListener(this);

        CommonSettingItem itemBrightnessControl = view.findViewById(R.id.item_brightness_control);
        itemBrightnessControl.setOnItemClickListener(this);


//        CheckableSettingItem itemAutoSaveTraffic = view.findViewById(R.id.item_auto_save_traffic);
//        itemAutoSaveTraffic.setChecked(AppConfig.isAutoSaveTraffic());
//        itemAutoSaveTraffic.setOnItemClickListener(this);

        CheckableSettingItem itemShowOriginalImage = view.findViewById(R.id.item_show_original_image);
        itemShowOriginalImage.setChecked(AppConfig.isShowOriginalImage());
        itemShowOriginalImage.setOnItemClickListener(this);

        CheckableSettingItem compressUploadImageItem = view.findViewById(R.id.item_compress_upload_image);
        compressUploadImageItem.setChecked(AppConfig.isCompressUploadImage());
        compressUploadImageItem.setOnItemClickListener(this);

        CommonSettingItem itemClearCache = view.findViewById(R.id.item_clear_cache);
        itemClearCache.setOnItemClickListener(this);
        new ObservableTask<String>(
                emitter -> {
                    emitter.onNext(DataCleanManagerUtils.getTotalCacheSizeStr(context));
                    emitter.onComplete();
                })
                .onSuccess(itemClearCache::setRightText)
                .subscribe();


        CommonSettingItem itemDownloadFolder = view.findViewById(R.id.item_download_folder);
        itemDownloadFolder.setInfoText(AppConfig.getDownloadPath());
        itemDownloadFolder.setOnItemClickListener(this);

        CommonSettingItem itemMaxDownloading = view.findViewById(R.id.item_max_downloading);
        itemMaxDownloading.setInfoText(String.valueOf(AppConfig.getMaxDownloadConcurrentCount()));
        itemMaxDownloading.setOnItemClickListener(this);

        CommonSettingItem itemMaxThread = view.findViewById(R.id.item_max_thread);
        itemMaxThread.setInfoText(String.valueOf(AppConfig.getMaxDownloadThreadCount()));
        itemMaxThread.setOnItemClickListener(this);

//        SwitchSettingItem itemShowDownloadedRing = view.findViewById(R.id.item_show_downloaded_ring);
//        itemShowDownloadedRing.setOnItemClickListener(this);

        SwitchSettingItem itemShowDownloadNotification = view.findViewById(R.id.item_show_downloaded_notification);
        itemShowDownloadNotification.setChecked(AppConfig.isShowDownloadNotification());
        itemShowDownloadNotification.setOnItemClickListener(this);


        CheckableSettingItem itemInstallDownloaded = view.findViewById(R.id.item_install_downloaded);
        itemInstallDownloaded.setChecked(AppConfig.isInstallAfterDownloaded());
        itemInstallDownloaded.setOnItemClickListener(this);

        CheckableSettingItem itemAutoDeleteApk = view.findViewById(R.id.item_auto_delete_apk);
        itemAutoDeleteApk.setChecked(AppConfig.isAutoDeleteAfterInstalled());
        itemAutoDeleteApk.setOnItemClickListener(this);

        SwitchSettingItem itemCheckSignature = view.findViewById(R.id.item_check_signature_before_install);
        itemCheckSignature.setChecked(AppConfig.isCheckSignature());
        itemCheckSignature.setOnItemClickListener(this);

        CheckableSettingItem itemAutoInstall = view.findViewById(R.id.item_accessibility_install);
        itemAutoInstall.setChecked(AppConfig.isAccessibilityInstall());
        itemAutoInstall.setOnItemClickListener(this);

        CheckableSettingItem itemRootInstall = view.findViewById(R.id.item_root_install);
        itemRootInstall.setVisibility(RootUtils.isRooted() ? View.VISIBLE : View.GONE);
        itemRootInstall.setChecked(AppConfig.isRootInstall());
        itemRootInstall.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(CheckableSettingItem item) {
        switch (item.getId()) {
            case R.id.item_show_start_page:
                AppConfig.setShowSplash(item.isChecked());
                break;
            case R.id.item_show_update_notification:
                AppConfig.setShowUpdateNotification(item.isChecked());
                if (item.isChecked()) {
                    AppUpdateManager.getInstance().notifyUpdate();
                } else {
                    AppUpdateManager.getInstance().cancelNotifyUpdate();
                }
                break;
//            case R.id.item_auto_save_traffic:
//                AppConfig.setAutoSaveTraffic(item.isChecked());
//                break;
            case R.id.item_show_original_image:
                AppConfig.setShowOriginalImage(item.isChecked());
                break;
            case R.id.item_compress_upload_image:
                AppConfig.setCompressUploadImage(item.isChecked());
                break;
//            case R.id.item_show_downloaded_ring:
//
//                break;
            case R.id.item_show_downloaded_notification:
                AppConfig.setShowDownloadNotification(item.isChecked());
                ZDownloader.setEnableNotification(item.isChecked());
                break;
            case R.id.item_install_downloaded:
                AppConfig.setInstallAfterDownloaded(item.isChecked());
                break;
            case R.id.item_auto_delete_apk:
                AppConfig.setAutoDeleteAfterInstalled(item.isChecked());
                break;
            case R.id.item_check_signature_before_install:
                AppConfig.setCheckSignature(item.isChecked());
                break;
            case R.id.item_accessibility_install:
                AppConfig.setAccessibilityInstall(item.isChecked());
                break;
            case R.id.item_root_install:
                AppConfig.setRootInstall(item.isChecked());
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(CommonSettingItem item) {
        switch (item.getId()) {
            case R.id.item_brightness_control:
                new BrightnessDialogFragment().show(context);
//                BrightnessPopup.with(context).show();
                break;
            case R.id.item_clear_cache:
                if (DataCleanManagerUtils.getTotalCacheSize(context) > 0) {
                    new AlertDialogFragment()
                            .setTitle("清除缓存")
                            .setContent("您将清除本应用所有缓存数据，确认清除？")
//                            .setAutoDismiss(false)
                            .setPositiveButton(popup -> {
                                ShowLoadingEvent.post("清除中...");
                                new ObservableTask<String>(
                                        emitter -> {
                                            DataCleanManagerUtils.clearAllCache(context);
                                            emitter.onNext(DataCleanManagerUtils.getTotalCacheSizeStr(context));
                                            emitter.onComplete();
                                        })
                                        .onSuccess(new IHttp.OnSuccessListener<String>() {
                                            @Override
                                            public void onSuccess(String data) throws Exception {
                                                HideLoadingEvent.post(() -> {
                                                    AToast.success("清理成功");
                                                    item.setRightText(data);
                                                });
                                            }
                                        })
                                        .subscribe();
                            })
                            .show(context);
                } else {
                    AToast.warning("暂无缓存");
                }
                break;
            case R.id.item_download_folder:

                break;
            case R.id.item_max_downloading:
                new SeekBarDialogFragment()
                        .setTitle("最大任务数")
                        .setMax(5)
                        .setMin(1)
                        .setProgress(AppConfig.getMaxDownloadConcurrentCount())
                        .setOnSeekProgressChangeListener(new SeekBarDialogFragment.OnSeekProgressChangeListener() {
                            @Override
                            public void onSeek(int progress) {
                                AppConfig.setMaxDownloadConcurrentCount(progress);
                                item.setInfoText(String.valueOf(progress));
                            }
                        })
                        .show(context);
                break;
            case R.id.item_max_thread:
                new SeekBarDialogFragment()
                        .setTitle("最大线程数")
                        .setMax(9)
                        .setMin(1)
                        .setProgress(AppConfig.getMaxDownloadThreadCount())
                        .setOnSeekProgressChangeListener(new SeekBarDialogFragment.OnSeekProgressChangeListener() {
                            @Override
                            public void onSeek(int progress) {
                                AppConfig.setMaxDownloadThreadCount(progress);
                                item.setInfoText(String.valueOf(progress));
                            }
                        })
                        .show(context);
                break;
            default:
                break;
        }
    }
}
