package com.zpj.shouji.market.utils;

import android.os.Environment;

import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class AppUtil {

    public static final int INSTALL_REQUEST_CODE = 1;
    public static final int UNINSTALL_REQUEST_CODE = 2;

    public static String getDefaultAppBackupFolder() {
        String path = Environment.getExternalStorageDirectory() + "/sjly/backups/";
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return path;
    }

    public static File getAppBackupFile(InstalledAppInfo appInfo) {
        return new File(AppUtil.getDefaultAppBackupFolder() + appInfo.getName() + "_" + appInfo.getVersionName() + ".apk");
    }

    public static void backupApp(InstalledAppInfo appInfo) throws IOException {
        FileUtils.copyFileFast(new File(appInfo.getApkFilePath()), getAppBackupFile(appInfo));
    }

}
