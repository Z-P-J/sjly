package com.zpj.shouji.market.glide.transformations.blur;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.ScreenUtils;

import java.security.MessageDigest;

public class CompressTransformation extends BitmapTransformation {

    private static final String ID = CompressTransformation.class.getName();

    public CompressTransformation() {

    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {

        Log.d("CompressTransformation", "getAllocationByteCount=" + toTransform.getAllocationByteCount() + " getByteCount=" + toTransform.getByteCount());
        Log.d("CompressTransformation", "getWidth=" + toTransform.getWidth() + " getScreenWidth=" + ScreenUtils.getScreenWidth(ContextUtils.getApplicationContext()));
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(),
                toTransform.getHeight(), matrix, true);
    }

    @Override
    public String toString() {
        return "CompressTransformation()";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CompressTransformation && ((CompressTransformation) o).hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return (ID.hashCode());
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID).getBytes(CHARSET));
    }
}
