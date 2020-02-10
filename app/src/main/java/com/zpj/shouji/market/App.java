package com.zpj.shouji.market;

import android.app.Application;

import com.bumptech.glide.request.target.ViewTarget;
import com.felix.atoast.library.AToast;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.zpj.downloader.ZDownloader;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
        AToast.onInit(this);
        ZDownloader.init(this);
        ViewTarget.setTagId(R.id.glide_tag_id);
    }

}
