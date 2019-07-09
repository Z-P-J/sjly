package com.zpj.sjly;

import android.app.Application;

import com.bumptech.glide.request.target.ViewTarget;
import com.felix.atoast.library.AToast;
import com.zpj.qxdownloader.QXDownloader;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AToast.onInit(this);
        QXDownloader.init(this);
        ViewTarget.setTagId(R.id.glide_tag_id);
    }
}
