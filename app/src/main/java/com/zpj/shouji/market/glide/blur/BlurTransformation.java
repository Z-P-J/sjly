package com.zpj.shouji.market.glide.blur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

import per.goweii.burred.Blurred;

public class BlurTransformation extends BitmapTransformation {

    private static final int VERSION = 1;
    private static final String ID = "BlurTransformation." + VERSION;

    private Context context;
    private static int DEFAULT_RADIUS = 25;
    private static int DEFAULT_DOWN_SAMPLING = 1;

    private int radius;
    private int sampling;

    public BlurTransformation() {
        this(null, DEFAULT_RADIUS, DEFAULT_DOWN_SAMPLING);
    }
//
    public BlurTransformation(int radius, int sampling) {
        this(null, radius, sampling);
    }

    public BlurTransformation(Context context, int radius) {
        this(context, radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(Context context, int radius, int sampling) {
        this.context = context;
        this.radius = radius;
        this.sampling = sampling;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
//        int width = toTransform.getWidth();
//        int height = toTransform.getHeight();
////        toTransform = Bitmap.createBitmap(toTransform, width / 4, height / 4, width / 2, height / 2);
////        int scaledWidth = toTransform.getWidth() / sampling;
////        int scaledHeight = toTransform.getHeight() / sampling;
//
//        Bitmap bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(bitmap);
////        canvas.scale(1 / (float) sampling, 1 / (float) sampling);
//        Paint paint = new Paint();
//        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
//        canvas.drawBitmap(toTransform, 0, 0, paint);

        Blurred mBlurred = new Blurred();
        toTransform = mBlurred.bitmap(toTransform)
                .keepSize(false)
//                .backgroundColor(Color.GRAY)
                .recycleOriginal(false)
                .scale(1F / sampling)
                .radius(radius)
                .blur();

        return toTransform;
    }

    @Override
    public String toString() {
        return "BlurTransformation(radius=" + radius + ", sampling=" + sampling + ")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BlurTransformation &&
                ((BlurTransformation) o).radius == radius &&
                ((BlurTransformation) o).sampling == sampling;
    }

    @Override
    public int hashCode() {
        return ID.hashCode() + radius * 1000 + sampling * 10;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + radius + sampling).getBytes(CHARSET));
    }
}
