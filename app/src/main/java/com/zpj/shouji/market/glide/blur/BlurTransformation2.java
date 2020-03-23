package com.zpj.shouji.market.glide.blur;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

import per.goweii.burred.Blurred;

/**
 * 描述：高斯模糊
 *
 * @author Cuizhen
 * @date 2018/9/13
 */
public class BlurTransformation2 extends BitmapTransformation {

    private static final String ID = BlurTransformation2.class.getName();

    private static float PERCENT = 0.1F;
    private static float SCALE = 0.5f;

    private float percent;
    private float scale;

    public BlurTransformation2() {
        this(PERCENT);
    }

    public BlurTransformation2(float percent) {
        this(percent, SCALE);
    }

    public BlurTransformation2(float percent, float scale) {
        this.percent = percent;
        this.scale = scale;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return Blurred.with(toTransform).percent(percent).scale(scale).blur();
    }

    @Override
    public String toString() {
        return "BlurTransformation(percent=" + percent + ")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BlurTransformation2 && ((BlurTransformation2) o).percent == percent;
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
