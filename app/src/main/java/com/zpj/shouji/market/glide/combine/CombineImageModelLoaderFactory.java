package com.zpj.shouji.market.glide.combine;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

public class CombineImageModelLoaderFactory implements ModelLoaderFactory<CombineImage, InputStream> {
    private Context context;

    public CombineImageModelLoaderFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ModelLoader<CombineImage, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new CombineImageModelLoader(context);
    }

    @Override
    public void teardown() {

    }
}