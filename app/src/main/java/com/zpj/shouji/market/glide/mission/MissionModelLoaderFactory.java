package com.zpj.shouji.market.glide.mission;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.shouji.market.model.InstalledAppInfo;

import java.io.InputStream;

public class MissionModelLoaderFactory implements ModelLoaderFactory<AppDownloadMission, InputStream> {

    private final Context context;

    public MissionModelLoaderFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ModelLoader<AppDownloadMission, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new MissionIconModelLoader(context);
    }

    @Override
    public void teardown() {

    }

}