package com.zpj.shouji.market.utils;

import android.content.Context;
import android.content.pm.PackageManager;
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

//    public static boolean isAppInstalled(Context context, String packageName) {
//        PackageManager pm = context.getPackageManager();
//        boolean installed;
//        try {
//            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
//            installed = true;
//        } catch (PackageManager.NameNotFoundException e) {
//            installed = false;
//        }
//        return installed;
//    }

    public static boolean compareVersions(String oldVersion, String newVersion) {
        //返回结果: -2 错误,-1 ,0 ,1
        int result = 0;
        String matchStr = "[0-9]+(\\.[0-9]+)*";
        oldVersion = oldVersion.trim();
        newVersion = newVersion.trim();
        //非版本号格式,返回error
        if (!oldVersion.matches(matchStr) || !newVersion.matches(matchStr)) {
//            return -2;
            return false;
        }
        String[] oldVs = oldVersion.split("\\.");
        String[] newVs = newVersion.split("\\.");
        int oldLen = oldVs.length;
        int newLen = newVs.length;

        if (oldLen == newLen) {
            for (int i = 0; i < newLen; i++) {
                int oldNum = Integer.parseInt(oldVs[i]);
                int newNum = Integer.parseInt(newVs[i]);
                if (newNum > oldNum) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNewVersion(String oldVersion, String newVersion) {

        String[] strings11 = newVersion.split("\\.");
        String[] strings21 = oldVersion.split("\\.");
        int x = strings11.length - strings21.length;
        if (x > 0) {
            for (int i = 0; i < x; i++) {
                oldVersion += ".0";
            }
        }
        if (x < 0) {
            x = -x;
            for (int i = 0; i < x; i++) {
                newVersion += ".0";
            }
        }
        String[] strings1 = newVersion.split("\\.");
        String[] strings2 = oldVersion.split("\\.");
        int index;
        if (strings1.length <= strings2.length) {
            index = strings1.length;
        } else {
            index = strings2.length;
        }
        boolean a = false;
        boolean b = false;
        for (int j = 0; j < index; j++) {
            char[] s1 = strings1[j].toCharArray();
            char[] s2 = strings2[j].toCharArray();
            if (s1.length > s2.length) {
                return true;
            } else if (s1.length < s2.length) {
                return false;
            } else {
                for (int i = 0; i < s1.length; i++) {
                    if (s1[i] - s2[i] == 0) {
                        continue;
                    } else if (s1[i] - s2[i] > 0) {
                        a = true;
                        break;
                    } else {
                        b = true;
                        break;
                    }
                }
            }
            if (a) {
                return true;
            }
            if (b) {
                return false;
            }
            if (!a && !b) {
                continue;
            }
        }
        return false;
    }


}
