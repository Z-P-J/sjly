package com.zpj.shouji.market.glide.mission;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.shouji.market.model.InstalledAppInfo;

import java.io.InputStream;

public class MissionIconModelLoader implements ModelLoader<AppDownloadMission, InputStream> {

    private final Context context;

    public MissionIconModelLoader(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull AppDownloadMission downloadMission, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(downloadMission.getPackageName()), new MissionIconFetcher(context, downloadMission));
    }

    @Override
    public boolean handles(@NonNull AppDownloadMission downloadMission) {
        return true;
    }
}