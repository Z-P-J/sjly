package com.zpj.shouji.market.utils;

import android.text.TextUtils;

import com.zpj.downloader.util.content.SPHelper;

public final class UserManager {

    private static String cookie;

    public static void setCookie(String cookie) {
        UserManager.cookie = cookie;
        SPHelper.putString("cookie", cookie);
    }

    public static String getCookie() {
        if (TextUtils.isEmpty(cookie)) {
            return SPHelper.getString("cookie", "");
        }
        return cookie;
    }

    public static boolean isLogin() {
        return SPHelper.getBoolean("is_login", false);
    }
}
