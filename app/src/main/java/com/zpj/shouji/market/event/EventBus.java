package com.zpj.shouji.market.event;

import android.arch.lifecycle.LifecycleOwner;
import android.view.View;

import com.yalantis.ucrop.CropEvent;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.rxbus.RxObserver;
import com.zpj.rxbus.RxSubscriber;
import com.zpj.shouji.market.model.AppDetailInfo;

import io.reactivex.functions.Consumer;

public class EventBus {

    public static final String KEY_FAB_EVENT = "event_fab";
    public static final String KEY_COLOR_CHANGE_EVENT = "event_color_change";
    public static final String KEY_HIDE_LOADING_EVENT = "event_hide_loading";
    public static final String KEY_MAIN_ACTION_EVENT = "event_main_action";
    public static final String KEY_USER_INFO_CHANGE_EVENT = "event_user_info_change";
    public static final String KEY_SKIN_CHANGE_EVENT = "event_skin_change_change";
    public static final String KEY_REFRESH_EVENT = "event_refresh_change";
    private static final String KEY_SCROLL_EVENT = "event_scroll";
    private static final String KEY_IMAGE_UPLOAD_EVENT = "event_image_upload";
    private static final String KEY_SEARCH_EVENT = "event_search";
    private static final String KEY_KEYWORD_CHANGE_EVENT = "event_keyword_change";
    private static final String KEY_GET_APP_INFO_EVENT = "event_get_app_info";

    public static final String KEY_SIGN_OUT_EVENT = "event_sign_out";

    public static void post(Object o) {
        RxSubscriber.post(o);
    }

    public static void post(Object o, long delay) {
        RxSubscriber.post(o, delay);
    }

    public static void sendFabEvent(boolean isShow) {
        RxSubscriber.post(KEY_FAB_EVENT, isShow);
    }

    public static void onFabEvent(LifecycleOwner lifecycleOwner, Consumer<Boolean> consumer) {
        registerObserver(lifecycleOwner, KEY_FAB_EVENT, Boolean.class, consumer);
    }

    public static void sendColorChangeEvent(boolean isDark) {
        RxSubscriber.post(KEY_COLOR_CHANGE_EVENT, isDark);
    }

    public static void onColorChangeEvent(Object o, Consumer<Boolean> consumer) {
        registerObserver(o, KEY_COLOR_CHANGE_EVENT, Boolean.class, consumer);
    }

    public static void sendMainActionEvent(boolean isShow) {
        RxSubscriber.post(KEY_MAIN_ACTION_EVENT, isShow);
    }

    public static void onMainActionEvent(LifecycleOwner lifecycleOwner, Consumer<Boolean> consumer) {
        registerObserver(lifecycleOwner, KEY_MAIN_ACTION_EVENT, Boolean.class, consumer);
    }

    public static void sendUserInfoChangeEvent() {
        RxSubscriber.post(KEY_USER_INFO_CHANGE_EVENT);
    }

    public static void onUserInfoChangeEvent(LifecycleOwner lifecycleOwner, Consumer<String> consumer) {
        registerObserver(lifecycleOwner, KEY_USER_INFO_CHANGE_EVENT, consumer);
    }

    public static void sendSkinChangeEvent() {
        RxSubscriber.post(KEY_SKIN_CHANGE_EVENT);
    }

    public static void onSkinChangeEvent(LifecycleOwner lifecycleOwner, Consumer<String> consumer) {
        registerObserver(lifecycleOwner, KEY_SKIN_CHANGE_EVENT, consumer);
    }

    public static void sendRefreshEvent() {
        RxSubscriber.post(KEY_REFRESH_EVENT);
    }

    public static void onRefreshEvent(LifecycleOwner lifecycleOwner, Consumer<String> consumer) {
        registerObserver(lifecycleOwner, KEY_REFRESH_EVENT, consumer);
    }

    public static void sendScrollEvent(float percent) {
        RxSubscriber.post(KEY_SCROLL_EVENT, percent);
    }

    public static void onScrollEvent(LifecycleOwner lifecycleOwner, Consumer<Float> consumer) {
        registerObserver(lifecycleOwner, KEY_SCROLL_EVENT, Float.class, consumer);
    }

    public static void sendImageUploadEvent(CropEvent event) {
        RxSubscriber.post(KEY_IMAGE_UPLOAD_EVENT, event);
    }

    public static void onImageUploadEvent(LifecycleOwner lifecycleOwner, Consumer<CropEvent> consumer) {
        registerObserver(lifecycleOwner, KEY_IMAGE_UPLOAD_EVENT, CropEvent.class, consumer);
    }

    public static void sendSearchEvent(String keyword) {
        RxSubscriber.post(KEY_SEARCH_EVENT, keyword);
    }

    public static void onSearchEvent(LifecycleOwner lifecycleOwner, Consumer<String> consumer) {
        registerObserver(lifecycleOwner, KEY_SEARCH_EVENT, String.class, consumer);
    }

    public static void sendKeywordChangeEvent(String keyword) {
        RxSubscriber.post(KEY_KEYWORD_CHANGE_EVENT, keyword);
    }

    public static void onKeywordChangeEvent(LifecycleOwner lifecycleOwner, Consumer<String> consumer) {
        registerObserver(lifecycleOwner, KEY_KEYWORD_CHANGE_EVENT, String.class, consumer);
    }

    public static void sendGetAppInfoEvent(AppDetailInfo info) {
        RxSubscriber.postSticky(KEY_GET_APP_INFO_EVENT, info);
    }

    public static void removeGetAppInfoEvent() {
        RxObserver.removeStickyEvent(KEY_GET_APP_INFO_EVENT);
    }

    public static void onGetAppInfoEvent(LifecycleOwner lifecycleOwner, Consumer<AppDetailInfo> consumer) {
//        registerObserver(lifecycleOwner, KEY_GET_APP_INFO_EVENT, AppDetailInfo.class, consumer);
        RxObserver.withSticky(lifecycleOwner, KEY_GET_APP_INFO_EVENT, AppDetailInfo.class)
                .bindToLife(lifecycleOwner)
                .subscribe(consumer);
    }

    public static void sendSignOutEvent() {
        RxSubscriber.post(KEY_SIGN_OUT_EVENT);
    }

    public static void onSignOutEvent(LifecycleOwner lifecycleOwner, Consumer<String> consumer) {
        registerObserver(lifecycleOwner, KEY_SIGN_OUT_EVENT, consumer);
    }

//    public static void sendHideLoadingEvent() {
//        RxSubscriber.post(KEY_HIDE_LOADING_EVENT, null);
//    }
//
//    public static void sendHideLoadingEvent(IDialog.OnDismissListener listener) {
//        RxSubscriber.post(KEY_HIDE_LOADING_EVENT, listener);
//    }
//
//    public static void sendHideLoadingEvent(long delay, IDialog.OnDismissListener listener) {
//        Observable.timer(delay, TimeUnit.MILLISECONDS)
//                .doOnComplete(() -> {
//                    RxSubscriber.post(KEY_HIDE_LOADING_EVENT, listener);
//                })
//                .subscribe();
//    }
//
//    public static void onHideLoadingEvent(LifecycleOwner lifecycleOwner, Consumer<IDialog.OnDismissListener> consumer) {
//        RxObserver.with(lifecycleOwner, KEY_HIDE_LOADING_EVENT, IDialog.OnDismissListener.class)
//                .bindToLife(lifecycleOwner)
//                .subscribe(consumer);
//    }


    public static <T> void registerObserver(View view, Class<T> type, Consumer<T> next) {
        RxObserver.with(view, type)
                .subscribe(next);
    }

    public static <T> void registerObserver(LifecycleOwner lifecycleOwner, Class<T> type, Consumer<T> next) {
        RxObserver.with(lifecycleOwner, type)
                .bindToLife(lifecycleOwner)
                .subscribe(next);
    }

    public static void registerObserver(LifecycleOwner lifecycleOwner, String key, Consumer<String> next) {
        RxObserver.with(lifecycleOwner, key)
                .bindToLife(lifecycleOwner)
                .subscribe(next);
    }

    public static <T> void registerObserver(LifecycleOwner lifecycleOwner, String key, Class<T> type, Consumer<T> next) {
        RxObserver.with(lifecycleOwner, key, type)
                .bindToLife(lifecycleOwner)
                .subscribe(next);
    }

    public static <T> void registerObserver(Object o, String key, Class<T> type, Consumer<T> next) {
        RxObserver.with(o, key, type)
                .subscribe(next);
    }

    public static void unSubscribe(Object o) {
        RxObserver.unSubscribe(o);
    }


}
