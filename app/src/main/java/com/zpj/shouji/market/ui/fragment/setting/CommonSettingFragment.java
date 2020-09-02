package com.zpj.shouji.market.ui.fragment.setting;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.http.core.IHttp;
import com.zpj.http.core.ObservableTask;
import com.zpj.popup.ZPopup;
import com.zpj.popup.impl.AlertPopup;
import com.zpj.popup.interfaces.OnConfirmListener;
import com.zpj.popup.interfaces.OnDismissListener;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.ui.widget.popup.BrightnessPopup;
import com.zpj.shouji.market.utils.DataCleanManagerUtils;
import com.zpj.widget.setting.CheckableSettingItem;
import com.zpj.widget.setting.CommonSettingItem;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.internal.operators.observable.ObservableObserveOn;

public class CommonSettingFragment extends BaseSettingFragment {

    public static void start() {
        StartFragmentEvent.start(new CommonSettingFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_common;
    }

    @Override
    public CharSequence getToolbarTitle(Context context) {
        return "通用设置";
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

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
        new ObservableTask<String>(
                emitter -> {
                    emitter.onNext(DataCleanManagerUtils.getTotalCacheSizeStr(context));
                    emitter.onComplete();
                })
                .onSuccess(itemClearCache::setRightText)
                .subscribe();
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
            default:
                break;
        }
    }

    @Override
    public void onItemClick(CommonSettingItem item) {
        switch (item.getId()) {
            case R.id.item_brightness_control:
                BrightnessPopup.with(context).show();
                break;
            case R.id.item_clear_cache:
                if (DataCleanManagerUtils.getTotalCacheSize(context) > 0) {
                    ZPopup.alert(context)
                            .setTitle("清除缓存")
                            .setContent("您将清除本应用所有缓存数据，确认清除？")
                            .setAutoDismiss(false)
                            .setConfirmButton(popup -> {
                                popup.setOnDismissListener(() -> {
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
                                });
                                popup.dismiss();
                            })
                            .show();
                } else {
                    AToast.warning("暂无缓存");
                }
                break;
            default:
                break;
        }
    }
}
