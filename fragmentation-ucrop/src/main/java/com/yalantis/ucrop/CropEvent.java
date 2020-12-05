package com.yalantis.ucrop;

import android.net.Uri;

import com.yalantis.ucrop.callback.CropCallback;
import com.zpj.rxbus.RxObserver;
import com.zpj.rxbus.RxSubscriber;

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
        RxSubscriber.post(EVENT_CROP, new CropEvent(uri, isAvatar));
    }

    public static void register(Object o, CropCallback callback) {
        RxObserver.with(o, EVENT_CROP, CropEvent.class)
                .subscribe(event -> {
                    RxObserver.unSubscribe(o);
                    callback.onCrop(event);
                });
    }

}
