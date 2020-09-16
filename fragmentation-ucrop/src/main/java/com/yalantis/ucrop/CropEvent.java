package com.yalantis.ucrop;

import android.net.Uri;

import org.greenrobot.eventbus.EventBus;

public class CropEvent {

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
        EventBus.getDefault().post(new CropEvent(uri, isAvatar));
    }

}
