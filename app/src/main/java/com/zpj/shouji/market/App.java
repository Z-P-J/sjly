package com.zpj.shouji.market;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ViewTarget;
import com.felix.atoast.library.AToast;
import com.lqr.emoji.IImageLoader;
import com.lqr.emoji.LQREmotionKit;
import com.maning.librarycrashmonitor.MCrashMonitor;
import com.maning.librarycrashmonitor.utils.MFileUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.zpj.downloader.ZDownloader;
import com.zpj.utils.ZUtils;

import per.goweii.burred.Blurred;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        MFileUtils.setCrashLogPath(Environment.getExternalStorageDirectory().getPath() + "/sjly/Crash/Log");
//        MFileUtils.setCrashPicPath(Environment.getExternalStorageDirectory().getPath() + "/sjly/Crash/ScreenShoot");
        MCrashMonitor.init(this, true, file -> {
//                MCrashMonitor.startCrashShowPage(getContext());
        });
        ZUtils.init(this);
        FlowManager.init(this);
        AToast.onInit(this);
        ZDownloader.init(this);
        ViewTarget.setTagId(R.id.glide_tag_id);
        LQREmotionKit.init(this, new IImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).centerCrop().into(imageView);
            }
        });
        Blurred.init(this);
    }

}
