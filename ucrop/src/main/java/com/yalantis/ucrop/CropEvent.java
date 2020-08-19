package com.yalantis.ucrop;

import android.net.Uri;

import org.greenrobot.eventbus.EventBus;

public class CropEvent {

    private Uri uri;

    private CropEvent(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public static void post(Uri uri) {
        EventBus.getDefault().post(new CropEvent(uri));
    }

}
