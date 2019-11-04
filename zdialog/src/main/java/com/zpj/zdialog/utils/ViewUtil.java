package com.zpj.zdialog.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class ViewUtil {

    public static float dp2px(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

}
