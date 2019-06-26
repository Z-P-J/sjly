package com.zpj.sjly;

import android.app.Application;

import com.zpj.qxdownloader.QXDownloader;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        QXDownloader.init(this);
    }
}
