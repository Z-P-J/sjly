package com.zpj.shouji.market.glide.blur;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

import per.goweii.burred.Blurred;

public class CropBlurTransformation extends BitmapTransformation {

    private static final String ID = CropBlurTransformation.class.getName();

    private static float RADIUS = 25F;
    private static float SCALE = 1f;

    private float radius;
    private float scale;

    public CropBlurTransformation() {
        this(RADIUS);
    }

    public CropBlurTransformation(float radius) {
        this(radius, SCALE);
    }

    public CropBlurTransformation(float radius, float scale) {
        this.radius = radius;
        this.scale = scale;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        int x = (int) (width * 0.15);
        int y = (int) (height * 0.15);
        toTransform = Bitmap.createBitmap(toTransform, x, y, (int) (width * 0.7), (int) (height * 0.7));
        return Blurred.with(toTransform)
                .scale(scale)
                .radius(radius)
                .blur();
    }

    @Override
    public String toString() {
        return "CropBlurTransformation(radius=" + radius + ")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CropBlurTransformation
                && ((CropBlurTransformation) o).radius == radius
                && ((CropBlurTransformation) o).scale == scale;
    }

    @Override
    public int hashCode() {
        return (ID.hashCode() + Float.valueOf(radius).hashCode() + Float.valueOf(scale).hashCode());
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + radius + scale).getBytes(CHARSET));
    }
}
