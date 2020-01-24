package com.zpj.shouji.market.glide.blur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.hoko.blur.HokoBlur;
import com.zpj.shouji.market.utils.FastBlur;

import java.security.MessageDigest;

public class BlurTransformation extends BitmapTransformation {

    private static final int VERSION = 1;
    private static final String ID = "BlurTransformation." + VERSION;

    private Context context;
    private static int MAX_RADIUS = 25;
    private static int DEFAULT_DOWN_SAMPLING = 1;

    private int radius;
    private int sampling;

    public BlurTransformation() {
        this(null, MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(int radius) {
        this(null, radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(Context context, int radius, int sampling) {
        this.context = context;
        this.radius = radius;
        this.sampling = sampling;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        toTransform = Bitmap.createBitmap(toTransform, width / 4, height / 4, width / 2, height / 2);
        int scaledWidth = toTransform.getWidth() / sampling;
        int scaledHeight = toTransform.getHeight() / sampling;

        Bitmap bitmap = pool.get(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / (float) sampling, 1 / (float) sampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(toTransform, 0, 0, paint);
//        bitmap = FastBlur.doBlur(bitmap, radius, true);
        bitmap = HokoBlur.with(context)
                .scheme(HokoBlur.SCHEME_NATIVE) //different implementation, RenderScript、OpenGL、Native(default) and Java
                .mode(HokoBlur.MODE_STACK) //blur algorithms，Gaussian、Stack(default) and Box
                .radius(15) //blur radius，max=25，default=5
                .sampleFactor(5.0f) //scale factor，if factor=2，the width and height of a bitmap will be scale to 1/2 sizes，default=5
                .forceCopy(false) //If scale factor=1.0f，the origin bitmap will be modified. You could set forceCopy=true to avoid it. default=false
                .needUpscale(true) //After blurring，the bitmap will be upscaled to origin sizes，default=true
//                .translateX(10)//add x axis offset when blurring
//                .translateY(10)//add y axis offset when blurring
                .processor() //parse a blur processor
                .blur(bitmap);
        return bitmap;
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
