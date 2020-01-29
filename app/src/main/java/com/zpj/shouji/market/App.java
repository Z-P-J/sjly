package com.zpj.shouji.market;

import android.app.Application;
import android.graphics.BitmapFactory;

import com.bumptech.glide.request.target.ViewTarget;
import com.felix.atoast.library.AToast;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.zpj.downloader.ZDownloader;
import com.zpj.shouji.market.utils.ExecutorHelper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
        AToast.onInit(this);
        ZDownloader.init(this);
        ExecutorHelper.init();
        ViewTarget.setTagId(R.id.glide_tag_id);
    }

}
