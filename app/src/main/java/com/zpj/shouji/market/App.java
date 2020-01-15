package com.zpj.shouji.market;

import android.app.Application;

import com.bumptech.glide.request.target.ViewTarget;
import com.felix.atoast.library.AToast;
import com.zpj.downloader.ZDownloader;
import com.zpj.shouji.market.glide.ImageLoader;

import cn.bingoogolapple.photopicker.imageloader.BGAImage;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AToast.onInit(this);
        ZDownloader.init(this);
        ViewTarget.setTagId(R.id.glide_tag_id);
        BGAImage.setImageLoader(new ImageLoader());
    }
}
