//package com.zpj.imagepicker.utils;
//
//import android.graphics.Bitmap;
//import android.graphics.Matrix;
//import android.support.annotation.NonNull;
//import android.util.Log;
//
//import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
//import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
//import com.zpj.utils.ContextUtils;
//import com.zpj.utils.ScreenUtils;
//
//import java.security.MessageDigest;
//
//public class ScaleTransformation extends BitmapTransformation {
//
//    private static final String ID = ScaleTransformation.class.getName();
//
//    private float scale = 0.5f;
//    private int count = 0;
//
//    public ScaleTransformation() {
//
//    }
//
//    public ScaleTransformation(float scale) {
//        this.scale = scale;
//    }
//
//    @Override
//    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
//        count++;
//        Log.d("ScaleTransformation", "count=" + count);
//        Log.d("ScaleTransformation", "getAllocationByteCount=" + toTransform.getAllocationByteCount() + " getByteCount=" + toTransform.getByteCount());
//        Log.d("ScaleTransformation", "getWidth=" + toTransform.getWidth() + " getScreenWidth=" + ScreenUtils.getScreenWidth(ContextUtils.getApplicationContext()));
//        Matrix matrix = new Matrix();
//        matrix.setScale(scale, scale);
//        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(),
//                toTransform.getHeight(), matrix, true);
//    }
//
//    @Override
//    public String toString() {
//        return "ScaleTransformation()";
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        return o instanceof ScaleTransformation && ((ScaleTransformation) o).hashCode() == hashCode() && ((ScaleTransformation) o).scale == scale;
//    }
//
//    @Override
//    public int hashCode() {
//        return (ID.hashCode() + (int) (scale * 10));
//    }
//
//    @Override
//    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
//        messageDigest.update((ID + scale).getBytes(CHARSET));
//    }
//}
