package com.zpj.shouji.market.glide.custom;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

public class CustomModelLoaderFactory<T, S extends GlideCustomData<T>> implements ModelLoaderFactory<S, T> {
    private Context context;

    public CustomModelLoaderFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ModelLoader<S, T> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new CustomModelLoader<>(context);
    }

    @Override
    public void teardown() {

    }
}