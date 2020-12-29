package com.zpj.shouji.market.glide;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class ImageViewDrawableTarget extends SimpleTarget<Drawable> {

    protected final ImageView imageView;

    public ImageViewDrawableTarget(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void onLoadStarted(@Nullable Drawable placeholder) {
        super.onLoadStarted(placeholder);
        imageView.setImageDrawable(placeholder);
    }

    @Override
    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
        imageView.setImageDrawable(resource);
//        imageView.setScaleType(imageView.getScaleType());
    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        super.onLoadFailed(errorDrawable);
        imageView.setImageDrawable(errorDrawable);
    }
}
