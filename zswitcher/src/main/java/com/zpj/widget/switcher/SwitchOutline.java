package com.zpj.widget.switcher;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.ViewOutlineProvider;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
abstract class SwitchOutline extends ViewOutlineProvider {

    final int width;
    final int height;

    SwitchOutline(int width, int height) {
        this.width = width;
        this.height = height;
    }

}
