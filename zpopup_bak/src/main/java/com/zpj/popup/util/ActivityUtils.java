package com.zpj.popup.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

public class ActivityUtils {

    public static Activity getActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else {
            return ((Activity) ((ContextWrapper) context).getBaseContext());
        }
    }

}
