package com.zpj.widget.switcher;

import android.os.Build;

class Utils {

    static final String STATE = "switch_state";
    static final String KEY_CHECKED = "checked";

    static final long SWITCHER_ANIMATION_DURATION = 800L;
    static final long COLOR_ANIMATION_DURATION = 300L;
    static final long TRANSLATE_ANIMATION_DURATION = 200L;
    static final float ON_CLICK_RADIUS_OFFSET = 2f;
    static final double BOUNCE_ANIM_AMPLITUDE_IN = 0.2;
    static final double BOUNCE_ANIM_AMPLITUDE_OUT = 0.15;
    static final double BOUNCE_ANIM_FREQUENCY_IN = 14.5;
    static final double BOUNCE_ANIM_FREQUENCY_OUT = 12.0;

    private Utils() {
        throw new IllegalAccessError("Access Forbidden!");
    }

    static boolean isLollipopAndAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

}
