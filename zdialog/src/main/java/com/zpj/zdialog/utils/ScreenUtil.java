package com.zpj.zdialog.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtil {

//    private static Point point = new Point();


//    /**
//     * 获取屏幕宽度
//     *
//     * @param activity Activity
//     * @return ScreenWidth
//     */
//    public static int getScreenWidth(Activity activity) {
//        Display display = activity.getWindowManager().getDefaultDisplay();
//        if (display != null) {
//            display.getSize(point);
//            return point.x;
//        }
//        return 0;
//    }
//
//    /**
//     * 获取屏幕高度
//     *
//     * @param activity Activity
//     * @return ScreenHeight
//     */
//    public static int getScreenHeight(Activity activity) {
//        Display display = activity.getWindowManager().getDefaultDisplay();
//        if (display != null) {
//            display.getSize(point);
//            return point.y;
//        }
//        return 0;
//    }

    /**
     * 获取屏幕的宽度（单位：px）
     *
     * @return 屏幕宽px
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(dm);// 给白纸设置宽高
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的高度（单位：px）
     *
     * @return 屏幕高px
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(dm);// 给白纸设置宽高
        return dm.heightPixels;
    }

}
