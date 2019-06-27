package com.felix.atoast.library.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by chaichuanfa on 17/5/15.
 */

public class ScreenUtils {

    public static final int DEFAULT_STATUS_BAR_HEIGHT_DP = 25;

    public static int getActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data,
                    context.getResources().getDisplayMetrics());
        }
        return 0;
    }

    public static int getStatusBarHeight(Context context) {
        int result = dip2px(context, DEFAULT_STATUS_BAR_HEIGHT_DP);
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getMiddleAppY(Context context) {
        return (getScreenHeight(context) - getActionBarHeight(context) - getStatusBarHeight(
                context)) / 2;
    }

    public static int dip2px(Context mContext, int dipValue) {
        float reSize = mContext.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * reSize) + 0.5);
    }
}
