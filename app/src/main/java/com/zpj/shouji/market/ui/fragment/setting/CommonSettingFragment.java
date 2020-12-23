//package com.zpj.shouji.market.ui.fragment.setting;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.View;
//
//import com.zpj.toast.ZToast;
//import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
//import com.zpj.http.core.IHttp;
//import com.zpj.http.core.ObservableTask;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.constant.AppConfig;
//import com.zpj.shouji.market.event.HideLoadingEvent;
//import com.zpj.shouji.market.event.ShowLoadingEvent;
//import com.zpj.shouji.market.event.StartFragmentEvent;
//import com.zpj.shouji.market.manager.AppUpdateManager;
//import com.zpj.shouji.market.ui.fragment.dialog.BrightnessDialogFragment;
//import com.zpj.shouji.market.utils.DataCleanManagerUtils;
//import com.zpj.widget.setting.CheckableSettingItem;
//import com.zpj.widget.setting.CommonSettingItem;
//
//public class CommonSettingFragment extends BaseSettingFragment {
//
//    public static void start() {
//        StartFragmentEvent.start(new CommonSettingFragment());
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_setting_common;
//    }
//
//    @Override
//    public CharSequence getToolbarTitle(Context context) {
//        return "通用设置";
//    }
//
//    @Override
//    protected void initView(View view, @Nullable Bundle savedInstanceState) {
//
//        CheckableSettingItem itemShowStartPage = view.findViewById(R.id.item_show_start_page);
//        itemShowStartPage.setChecked(AppConfig.isShowSplash());
//        itemShowStartPage.setOnItemClickListener(this);
//
//        CheckableSettingItem itemShowUpdateNotification = view.findViewById(R.id.item_show_update_notification);
//        itemShowUpdateNotification.setChecked(AppConfig.isShowUpdateNotification());
//        itemShowUpdateNotification.setOnItemClickListener(this);
//
//        CommonSettingItem itemBrightnessControl = view.findViewById(R.id.item_brightness_control);
//        itemBrightnessControl.setOnItemClickListener(this);
//
//
////        CheckableSettingItem itemAutoSaveTraffic = view.findViewById(R.id.item_auto_save_traffic);
////        itemAutoSaveTraffic.setChecked(AppConfig.isAutoSaveTraffic());
////        itemAutoSaveTraffic.setOnItemClickListener(this);
//
//        CheckableSettingItem itemShowOriginalImage = view.findViewById(R.id.item_show_original_image);
//        itemShowOriginalImage.setChecked(AppConfig.isShowOriginalImage());
//        itemShowOriginalImage.setOnItemClickListener(this);
//
//        CheckableSettingItem compressUploadImageItem = view.findViewById(R.id.item_compress_upload_image);
//        compressUploadImageItem.setChecked(AppConfig.isCompressUploadImage());
//        compressUploadImageItem.setOnItemClickListener(this);
//
//        CommonSettingItem itemClearCache = view.findViewById(R.id.item_clear_cache);
//        itemClearCache.setOnItemClickListener(this);
//        new ObservableTask<String>(
//                emitter -> {
//                    emitter.onNext(DataCleanManagerUtils.getTotalCacheSizeStr(context));
//                    emitter.onComplete();
//                })
//                .onSuccess(itemClearCache::setRightText)
//                .subscribe();
//    }
//
//    @Override
//    public void onItemClick(CheckableSettingItem item) {
//        switch (item.getId()) {
//            case R.id.item_show_start_page:
//                AppConfig.setShowSplash(item.isChecked());
//                break;
//            case R.id.item_show_update_notification:
//                AppConfig.setShowUpdateNotification(item.isChecked());
//                if (item.isChecked()) {
//                    AppUpdateManager.getInstance().notifyUpdate();
//                } else {
//                    AppUpdateManager.getInstance().cancelNotifyUpdate();
//                }
//                break;
////            case R.id.item_auto_save_traffic:
////                AppConfig.setAutoSaveTraffic(item.isChecked());
////                break;
//            case R.id.item_show_original_image:
//                AppConfig.setShowOriginalImage(item.isChecked());
//                break;
//            case R.id.item_compress_upload_image:
//                AppConfig.setCompressUploadImage(item.isChecked());
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void onItemClick(CommonSettingItem item) {
//        switch (item.getId()) {
//            case R.id.item_brightness_control:
//                new BrightnessDialogFragment().show(context);
////                BrightnessPopup.with(context).show();
//                break;
//            case R.id.item_clear_cache:
//                if (DataCleanManagerUtils.getTotalCacheSize(context) > 0) {
//                    new AlertDialogFragment()
//                            .setTitle("清除缓存")
//                            .setContent("您将清除本应用所有缓存数据，确认清除？")
////                            .setAutoDismiss(false)
//                            .setPositiveButton(popup -> {
//                                ShowLoadingEvent.post("清除中...");
//                                new ObservableTask<String>(
//                                        emitter -> {
//                                            DataCleanManagerUtils.clearAllCache(context);
//                                            emitter.onNext(DataCleanManagerUtils.getTotalCacheSizeStr(context));
//                                            emitter.onComplete();
//                                        })
//                                        .onSuccess(new IHttp.OnSuccessListener<String>() {
//                                            @Override
//                                            public void onSuccess(String data) throws Exception {
//                                                HideLoadingEvent.post(() -> {
//                                                    ZToast.success("清理成功");
//                                                    item.setRightText(data);
//                                                });
//                                            }
//                                        })
//                                        .subscribe();
//                            })
//                            .show(context);
////                    ZPopup.alert(context)
////                            .setTitle("清除缓存")
////                            .setContent("您将清除本应用所有缓存数据，确认清除？")
////                            .setAutoDismiss(false)
////                            .setConfirmButton(popup -> {
////                                popup.setOnDismissListener(() -> {
////                                    ShowLoadingEvent.post("清除中...");
////                                    new ObservableTask<String>(
////                                            emitter -> {
////                                                DataCleanManagerUtils.clearAllCache(context);
////                                                emitter.onNext(DataCleanManagerUtils.getTotalCacheSizeStr(context));
////                                                emitter.onComplete();
////                                            })
////                                            .onSuccess(new IHttp.OnSuccessListener<String>() {
////                                                @Override
////                                                public void onSuccess(String data) throws Exception {
////                                                    HideLoadingEvent.post(() -> {
////                                                        ZToast.success("清理成功");
////                                                        item.setRightText(data);
////                                                    });
////                                                }
////                                            })
////                                            .subscribe();
////                                });
////                                popup.dismiss();
////                            })
////                            .setCancelButton(BasePopup::dismiss)
////                            .show();
//                } else {
//                    ZToast.warning("暂无缓存");
//                }
//                break;
//            default:
//                break;
//        }
//    }
//}
