package com.zpj.shouji.market.utils;

import android.text.TextUtils;

import com.zpj.utils.PrefsHelper;

public final class UserManager {

    private static String cookie;

    public static void setCookie(String cookie) {
        UserManager.cookie = cookie;
        PrefsHelper.with().putString("cookie", cookie);
    }

    public static String getCookie() {
        if (TextUtils.isEmpty(cookie)) {
            return PrefsHelper.with().getString("cookie", "");
        }
        return cookie;
    }

    public static boolean isLogin() {
        return PrefsHelper.with().getBoolean("is_login", false);
    }
}
