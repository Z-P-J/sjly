package com.zpj.shouji.market.glide.transformations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.ScreenUtils;

import java.security.MessageDigest;

public class CircleWithBorderTransformation extends BitmapTransformation {


    private static final int VERSION = 1;
    private static final String ID = "com.zpj.shouji.market.glide.transformation.CropCircleWithBorderTransformation." + VERSION;

    private final int borderSize;
    private final int borderColor;


    public CircleWithBorderTransformation() {
        this.borderSize = ScreenUtils.dp2pxInt(ContextUtils.getApplicationContext(), 1);
        this.borderColor = Color.BLACK;
    }

    public CircleWithBorderTransformation(float borderSize, @ColorInt int borderColor) {
        this.borderSize = ScreenUtils.dp2pxInt(ContextUtils.getApplicationContext(), borderSize);
        this.borderColor = borderColor;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap = TransformationUtils.circleCrop(pool, toTransform, outWidth, outHeight);

        bitmap.setDensity(toTransform.getDensity());

        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderSize);
        paint.setAntiAlias(true);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(
                outWidth / 2f,
                outHeight / 2f,
                Math.max(outWidth, outHeight) / 2f - borderSize / 2f,
                paint
        );

        return bitmap;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + borderSize + borderColor).getBytes(CHARSET));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CircleWithBorderTransformation &&
                ((CircleWithBorderTransformation) o).borderSize == borderSize &&
                ((CircleWithBorderTransformation) o).borderColor == borderColor;
    }

    @Override
    public int hashCode() {
        return ID.hashCode() + borderSize * 100 + borderColor + 10;
    }
}
