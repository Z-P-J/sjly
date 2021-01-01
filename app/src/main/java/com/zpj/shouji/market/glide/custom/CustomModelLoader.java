package com.zpj.shouji.market.glide.custom;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

public class CustomModelLoader<T, S extends GlideCustomData<T>> implements ModelLoader<S, T> {

    private Context context;

    public CustomModelLoader(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public LoadData<T> buildLoadData(@NonNull S glideCustomData, int width, int height, @NonNull Options options) {
        glideCustomData.setContext(context);
        return new LoadData<>(new ObjectKey(glideCustomData.getKey()), glideCustomData);
    }

    @Override
    public boolean handles(@NonNull S glideCustomData) {
        return true;
    }
}