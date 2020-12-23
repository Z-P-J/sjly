package com.zpj.shouji.market.constant;

import android.os.Environment;

import com.zpj.downloader.ZDownloader;
import com.zpj.utils.PrefsHelper;

public final class AppConfig {

    private static final String DEFAULT_DOWNLOAD_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/sjly/ProDownload/";

    private static final String KEY_NIGHT_MODE = "is_night_mode";

    private static final String KEY_AUTO_SAVE_TRAFFIC = "auto_save_traffic";
    private static final String KEY_SHOW_ORIGINAL_IMAGE = "show_original_image";
    private static final String KEY_COMPRESS_UPLOAD_IMAGE = "compress_upload_image";
    private static final String KEY_SHOW_SPLASH = "show_splash";
    private static final String KEY_SHOW_UPDATE_NOTIFICATION = "show_update_notification";
    private static final String KEY_SHOW_DOWNLOAD_NOTIFICATION = "show_download_notification";
    private static final String KEY_DOWNLOAD_PATH = "download_directory";
    private static final String KEY_MAX_DOWNLOAD_CONCURRENT_COUNT = "max_download_concurrent_count";
    private static final String KEY_MAX_DOWNLOAD_THREAD_COUNT = "max_download_thread_count";
    private static final String KEY_INSTALL_AFTER_DOWNLOADED = "install_after_download";
    private static final String KEY_AUTO_DELETE_AFTER_INSTALLED = "auto_delete_after_installed";
    private static final String KEY_ACCESSIBILITY_INSTALL = "accessibility_install";
    private static final String KEY_ROOT_INSTALL = "root_install";
    private static final String KEY_CHECK_SIGNATURE = "check_signature";
//    private static final String KEY_SHOW_DOWNLOADED_RING = "show_downloaded_ring";

    private AppConfig() {

    }

    public static boolean isNightMode() {
        return PrefsHelper.with().getBoolean(KEY_NIGHT_MODE, false);
    }

    public static void toggleThemeMode() {
        PrefsHelper.with().putBoolean(KEY_NIGHT_MODE, !isNightMode());
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
        return PrefsHelper.with().getBoolean(KEY_SHOW_SPLASH, false);
    }

    public static void setShowUpdateNotification(boolean value) {
        PrefsHelper.with().putBoolean(KEY_SHOW_UPDATE_NOTIFICATION, value);
    }

    public static boolean isShowUpdateNotification() {
        return PrefsHelper.with().getBoolean(KEY_SHOW_UPDATE_NOTIFICATION, true);
    }

    public static void setShowDownloadNotification(boolean value) {
        PrefsHelper.with().putBoolean(KEY_SHOW_DOWNLOAD_NOTIFICATION, value);
    }

    public static boolean isShowDownloadNotification() {
        return PrefsHelper.with().getBoolean(KEY_SHOW_DOWNLOAD_NOTIFICATION, true);
    }

    public static void setDownloadPath(String path) {
        PrefsHelper.with().putString(KEY_DOWNLOAD_PATH, path);
    }

    public static String getDownloadPath() {
        return PrefsHelper.with().getString(KEY_DOWNLOAD_PATH, DEFAULT_DOWNLOAD_PATH);
    }

    public static void setMaxDownloadConcurrentCount(int count) {
        PrefsHelper.with().putInt(KEY_MAX_DOWNLOAD_CONCURRENT_COUNT, count);
        ZDownloader.setMaxDownloadConcurrentCount(count);
    }

    public static int getMaxDownloadConcurrentCount() {
        return PrefsHelper.with().getInt(KEY_MAX_DOWNLOAD_CONCURRENT_COUNT, 3);
    }

    public static void setMaxDownloadThreadCount(int count) {
        PrefsHelper.with().putInt(KEY_MAX_DOWNLOAD_THREAD_COUNT, count);
        ZDownloader.setMaxDownloadThreadCount(count);
    }

    public static int getMaxDownloadThreadCount() {
        return PrefsHelper.with().getInt(KEY_MAX_DOWNLOAD_THREAD_COUNT, 3);
    }

    public static void setInstallAfterDownloaded(boolean value) {
        PrefsHelper.with().putBoolean(KEY_INSTALL_AFTER_DOWNLOADED, value);
    }

    public static boolean isInstallAfterDownloaded() {
        return PrefsHelper.with().getBoolean(KEY_INSTALL_AFTER_DOWNLOADED, true);
    }

    public static void setAutoDeleteAfterInstalled(boolean value) {
        PrefsHelper.with().putBoolean(KEY_AUTO_DELETE_AFTER_INSTALLED, value);
    }

    public static boolean isAutoDeleteAfterInstalled() {
        return PrefsHelper.with().getBoolean(KEY_AUTO_DELETE_AFTER_INSTALLED, true);
    }

    public static void setAccessibilityInstall(boolean value) {
        PrefsHelper.with().putBoolean(KEY_ACCESSIBILITY_INSTALL, value);
    }

    public static boolean isAccessibilityInstall() {
        return PrefsHelper.with().getBoolean(KEY_ACCESSIBILITY_INSTALL, false);
    }

    public static void setRootInstall(boolean value) {
        PrefsHelper.with().putBoolean(KEY_ROOT_INSTALL, value);
    }

    public static boolean isRootInstall() {
        return PrefsHelper.with().getBoolean(KEY_ROOT_INSTALL, true);
    }

    public static void setCheckSignature(boolean value) {
        PrefsHelper.with().putBoolean(KEY_CHECK_SIGNATURE, value);
    }

    public static boolean isCheckSignature() {
        return PrefsHelper.with().getBoolean(KEY_CHECK_SIGNATURE, true);
    }

}
