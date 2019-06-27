package com.zpj.sjly.utils;

import android.text.TextUtils;

import com.zpj.qxdownloader.util.content.SPHelper;

public final class UserHelper {

    private static String cookie;

    public static void setCookie(String cookie) {
        UserHelper.cookie = cookie;
        SPHelper.putString("cookie", cookie);
    }

    public static String getCookie() {
        if (TextUtils.isEmpty(cookie)) {
            return SPHelper.getString("cookie", "");
        }
        return cookie;
    }
}
