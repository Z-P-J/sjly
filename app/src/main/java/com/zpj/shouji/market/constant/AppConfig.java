package com.zpj.shouji.market.constant;

import com.zpj.utils.PrefsHelper;

public final class AppConfig {

    private static final String KEY_AUTO_SAVE_TRAFFIC = "auto_save_traffic";
    private static final String KEY_SHOW_ORIGINAL_IMAGE = "show_original_image";
    private static final String KEY_COMPRESS_UPLOAD_IMAGE = "compress_upload_image";
    private static final String KEY_SHOW_SPLASH = "show_splash";
    private static final String KEY_SHOW_UPDATE_NOTIFICATION = "show_update_notification";

    private AppConfig() {

    }

    public static void setAutoSaveTraffic(boolean value) {
        PrefsHelper.with().putBoolean(KEY_AUTO_SAVE_TRAFFIC, value);
    }

    public static boolean isAutoSaveTraffic() {
        return PrefsHelper.with().getBoolean(KEY_AUTO_SAVE_TRAFFIC, false);
    }

    public static void setShowOriginalImage(boolean value) {
        PrefsHelper.with().putBoolean(KEY_SHOW_ORIGINAL_IMAGE, value);
    }

    public static boolean isShowOriginalImage() {
        return PrefsHelper.with().getBoolean(KEY_SHOW_ORIGINAL_IMAGE, true);
    }

    public static void setCompressUploadImage(boolean value) {
        PrefsHelper.with().putBoolean(KEY_COMPRESS_UPLOAD_IMAGE, value);
    }

    public static boolean isCompressUploadImage() {
        return PrefsHelper.with().getBoolean(KEY_COMPRESS_UPLOAD_IMAGE, false);
    }

    public static void setShowSplash(boolean value) {
        PrefsHelper.with().putBoolean(KEY_SHOW_SPLASH, value);
    }

    public static boolean isShowSplash() {
        return PrefsHelper.with().getBoolean(KEY_SHOW_SPLASH, true);
    }

    public static void setShowUpdateNotification(boolean value) {
        PrefsHelper.with().putBoolean(KEY_SHOW_UPDATE_NOTIFICATION, value);
    }

    public static boolean isShowUpdateNotification() {
        return PrefsHelper.with().getBoolean(KEY_SHOW_UPDATE_NOTIFICATION, true);
    }
}
