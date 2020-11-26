package com.zpj.shouji.market.glide.transformations.blur;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.zpj.blur.ZBlurry;

import java.security.MessageDigest;

public class PercentBlurTransformation extends BitmapTransformation {

    private static final String ID = PercentBlurTransformation.class.getName();

    private static float PERCENT = 0.1F;
    private static float SCALE = 1f;

    private float percent;
    private float scale;

    public PercentBlurTransformation() {
        this(PERCENT);
    }

    public PercentBlurTransformation(float percent) {
        this(percent, SCALE);
    }

    public PercentBlurTransformation(float percent, float scale) {
        this.percent = percent;
        this.scale = scale;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
//        int width = toTransform.getWidth();
//        int height = toTransform.getHeight();
//        toTransform = Bitmap.createBitmap(toTransform, width / 4, height / 4, width / 2, height / 2);
        return ZBlurry.with(toTransform)
                .percent(percent)
                .scale(scale)
                .blur();
    }

    @Override
    public String toString() {
        return "BlurTransformation(percent=" + percent + ")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PercentBlurTransformation && ((PercentBlurTransformation) o).percent == percent;
    }

    @Override
    public int hashCode() {
        return (ID.hashCode() + Float.valueOf(percent).hashCode());
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + percent).getBytes(CHARSET));
    }
}
