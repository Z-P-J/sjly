package com.zpj.fragmentation.dialog.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.zpj.fragmentation.dialog.R;

public final class DialogThemeUtils {

    private DialogThemeUtils() {

    }

    public static int getColorPrimary(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    private static int getColor(Context context, int attr, int defaultColor) {
        int[] ints = { attr };
        TypedArray typedArray = context.obtainStyledAttributes(ints);
        int color = typedArray.getColor(0, defaultColor);
        typedArray.recycle();
        return color;
    }

    private static Drawable getDrawable(Context context, int attr, int defaultDrawable) {
        int[] ints = { attr };
        TypedArray typedArray = context.obtainStyledAttributes(ints);
        Drawable drawable = typedArray.getDrawable(0);
        typedArray.recycle();
        if (drawable == null) {
            return context.getResources().getDrawable(defaultDrawable);
        }
        return drawable;
    }

    public static int getAttachListDialogBackgroundColor(Context context) {
        return getColor(context, R.attr._dialog_background_color_attach_list, Color.WHITE);
//        int[] ints = { R.attr._dialog_card_background };
//        TypedArray typedArray = context.obtainStyledAttributes(ints);
//        int color = typedArray.getColor(0, Color.WHITE);
//        typedArray.recycle();
//        return color;
    }

    public static int getMajorTextColor(Context context) {
        return getColor(context, R.attr._dialog_text_color_major, context.getResources().getColor(R.color._dialog_text_major_color));
    }

    public static int getNormalTextColor(Context context) {
        return getColor(context, R.attr._dialog_text_color_normal, context.getResources().getColor(R.color._dialog_text_normal_color));
    }

    public static int getPositiveTextColor(Context context) {
        return getColor(context, R.attr._dialog_text_color_positive, getColorPrimary(context));
    }

    public static int getNegativeTextColor(Context context) {
        return getColor(context, R.attr._dialog_text_color_negative, context.getResources().getColor(R.color._dialog_text_normal_color));
    }

    public static int getLoadingTextColor(Context context) {
        return getColor(context, R.attr._dialog_text_color_loading, Color.parseColor("#DDDDDD"));
    }

    public static Drawable getCenterDialogBackground(Context context) {
        return getDrawable(context, R.attr._dialog_background_center, R.drawable._dialog_round_bg);
    }

    public static Drawable getBottomDialogBackground(Context context) {
        return getDrawable(context, R.attr._dialog_background_bottom, R.drawable._dialog_top_round_bg);
    }

    public static Drawable getLoadingDialogBackground(Context context) {
        return getDrawable(context, R.attr._dialog_background_loading, R.drawable._dialog_round_dark_bg);
    }


}
