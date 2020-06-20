package io.haydar.filescanner.util;

import android.text.TextUtils;
import android.util.Log;

import io.haydar.filescanner.BuildConfig;

/**
 * @author Haydar
 * @Package io.haydar.filescanner.util
 * @DATE 2017-04-27
 */

public class LogUtil {
    private static final boolean isDebug = BuildConfig.DEBUG;

    public static void i(String tag, String... args) {
        if (isDebug) {
            Log.i(tag, getLog(tag, args));
        }
    }

    public static void d(String tag, String... args) {
        if (isDebug) {
            Log.i(tag, getLog(tag, args));
        }
    }

    public static void v(String tag, String... args) {

        if (isDebug) {
            Log.i(tag, getLog(tag, args));
        }
    }

    public static void w(String tag, String... args) {
        if (isDebug) {
            Log.i(tag, getLog(tag, args));
        }
    }

    public static void e(String tag, String... args) {
        if (isDebug) {
            Log.i(tag, getLog(tag, args));
        }
    }

    private static String getLog(String tag, String... args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            if (TextUtils.isEmpty(arg)) continue;

            builder.append(arg);
        }

        return builder.toString();
    }


}
