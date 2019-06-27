package com.felix.atoast.library.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;

public class ToastUtils {

    private ToastUtils() {
    }

    public static Drawable getDrawableFrame(Context mContext, @ColorInt int tintColor) {
        GradientDrawable toastDrawable = new GradientDrawable();
        toastDrawable.setColor(tintColor);
        toastDrawable.setCornerRadius(ScreenUtils.dip2px(mContext, 20));
        return toastDrawable;
    }

    public static void setBackground(@NonNull View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }
}
