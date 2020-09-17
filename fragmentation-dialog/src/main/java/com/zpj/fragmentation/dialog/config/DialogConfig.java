package com.zpj.fragmentation.dialog.config;

import android.graphics.Color;

public class DialogConfig {

    private DialogConfig() {

    }

    private static int primaryColor = Color.parseColor("#121212");
    private static int animationDuration = 360;
    public static int statusBarShadowColor = Color.parseColor("#55000000");
    private static int shadowBgColor = Color.parseColor("#9F000000");

    public static int getAnimationDuration() {
        return animationDuration;
    }
}
