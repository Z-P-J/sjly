package com.zpj.shouji.market.event;

import android.net.Uri;

import com.yalantis.ucrop.CropEvent;

import org.greenrobot.eventbus.EventBus;

public class IconUploadSuccessEvent {

    private Uri uri;
    private boolean isAvatar;

    private IconUploadSuccessEvent(Uri uri, boolean isAvatar) {
        this.uri = uri;
        this.isAvatar = isAvatar;
    }

    public Uri getUri() {
        return uri;
    }

    public boolean isAvatar() {
        return isAvatar;
    }

    public static void post(CropEvent event) {
        EventBus.getDefault().post(new IconUploadSuccessEvent(event.getUri(), event.isAvatar()));
    }

}
