package com.zpj.utils;

import android.annotation.SuppressLint;
import android.content.Context;

public class ZUtils {
    @SuppressLint("StaticFieldLeak")
    private static ZUtils zUtils;
    private final Context context;

    private ZUtils(Context context) {
        this.context = context;
    }

    public static void init(Context context) {
        if (zUtils == null) {
            synchronized (ZUtils.class) {
                if (zUtils == null) {
                    zUtils = new ZUtils(context);
                }
            }
        }
    }

    public static ZUtils getInstance() {
        if (zUtils == null) {
            throw new RuntimeException("You must init ZUtils in your application!");
        }
        return zUtils;
    }

    public Context getContext() {
        return context;
    }
}
