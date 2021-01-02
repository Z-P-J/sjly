package com.zpj.shouji.market.glide.transformations;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.ScreenUtils;

import java.security.MessageDigest;

public class MaxSizeTransformation extends BitmapTransformation {

    private static final String ID = MaxSizeTransformation.class.getName();

    private final int maxSize;
    private int count = 0;

    public MaxSizeTransformation() {
        maxSize = ScreenUtils.getScreenWidth() / 4;
    }

    public MaxSizeTransformation(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        count++;

        Log.d(ID, "outWidth=" + outWidth + " outHeight=" + outHeight + " width=" + toTransform.getWidth() + " height=" + toTransform.getHeight() + " maxSize=" + maxSize);
        Log.d(ID, "count=" + count + " toTransform=" + toTransform);
        Log.d(ID, "getAllocationByteCount=" + toTransform.getAllocationByteCount() + " getByteCount=" + toTransform.getByteCount());
        Log.d(ID, "getWidth=" + toTransform.getWidth() + " getScreenWidth=" + ScreenUtils.getScreenWidth(ContextUtils.getApplicationContext()));
        if (count > 1) {
            return null;
        }

        int width = toTransform.getWidth();
        if (width < maxSize) {
            return toTransform;
        }
        Matrix matrix = new Matrix();
        float scale = maxSize * 1f / width;
        matrix.setScale(scale, scale);
        return Bitmap.createBitmap(toTransform, 0, 0, width,
                toTransform.getHeight(), matrix, false);
    }

    @Override
    public String toString() {
        return "ScaleTransformation()";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MaxSizeTransformation && ((MaxSizeTransformation) o).hashCode() == hashCode() && ((MaxSizeTransformation) o).maxSize == maxSize;
    }

    @Override
    public int hashCode() {
        return (ID.hashCode() + (int) (maxSize * 10));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + maxSize).getBytes(CHARSET));
    }
}
