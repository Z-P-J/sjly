package com.zpj.shouji.market.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

import com.zpj.fragmentation.SupportFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.AppConfig;

public final class ThemeUtils {

    private ThemeUtils() {

    }

    public static void initStatusBar(SupportFragment fragment) {
        if (AppConfig.isNightMode()) {
            fragment.lightStatusBar();
        } else {
            fragment.darkStatusBar();
        }
    }

    public static int getDefaultBackgroundColor(Context context) {
        int[] ints = { R.attr.backgroundColor };
        TypedArray typedArray = context.obtainStyledAttributes(ints);
        int color = typedArray.getColor(0, Color.WHITE);
        typedArray.recycle();
        return color;
    }

    public static int getTextColorMajor(Context context) {
        int[] ints = { R.attr.textColorMajor };
        TypedArray typedArray = context.obtainStyledAttributes(ints);
        int color = typedArray.getColor(0, context.getResources().getColor(R.color.color_text_major));
        typedArray.recycle();
        return color;
    }

    public static int getTextColorNormal(Context context) {
        int[] ints = { R.attr.textColorNormal };
        TypedArray typedArray = context.obtainStyledAttributes(ints);
        int color = typedArray.getColor(0, context.getResources().getColor(R.color.color_text_normal));
        typedArray.recycle();
        return color;
    }

    public static int getTextColorMinor(Context context) {
        int[] ints = { R.attr.textColorMinor };
        TypedArray typedArray = context.obtainStyledAttributes(ints);
        int color = typedArray.getColor(0, context.getResources().getColor(R.color.color_text_minor));
        typedArray.recycle();
        return color;
    }

}
