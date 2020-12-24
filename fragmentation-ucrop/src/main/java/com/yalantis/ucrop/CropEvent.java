package com.yalantis.ucrop;

import android.net.Uri;

import com.yalantis.ucrop.callback.CropCallback;
import com.zpj.rxbus.RxBus;

public class CropEvent {

    private static final String EVENT_CROP = "event_crop_" + CropEvent.class.getSimpleName();

    private Uri uri;
    private boolean isAvatar;

    private CropEvent(Uri uri, boolean isAvatar) {
        this.uri = uri;
        this.isAvatar = isAvatar;
    }

    public Uri getUri() {
        return uri;
    }

    public boolean isAvatar() {
        return isAvatar;
    }

    public static void post(Uri uri, boolean isAvatar) {
        RxBus.post(EVENT_CROP, new CropEvent(uri, isAvatar));
    }

    public static void register(Object o, CropCallback callback) {
        RxBus.observe(o, EVENT_CROP, CropEvent.class)
                .doOnNext(new RxBus.SingleConsumer<CropEvent>() {
                    @Override
                    public void onAccept(CropEvent event) throws Exception {
                        RxBus.removeObservers(o);
                        callback.onCrop(event);
                    }
                })
                .subscribe();
    }

}
