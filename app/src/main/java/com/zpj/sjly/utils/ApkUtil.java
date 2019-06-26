package com.zpj.sjly.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApkUtil {

//    private static final Map<String, Drawable> map  = new HashMap<>();

    public static void loadAppIcons(Context context) {
        long tempTime = System.currentTimeMillis();
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> packageInfoList = manager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfoList) {
            getAppIcon(context, packageInfo.packageName);
        }
        Log.d("loadAppIcons", "耗时：" + (System.currentTimeMillis() - tempTime));
    }

    public static Drawable getAppIcon(Context context, String packageName) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext()
                    .getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    packageName, 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return packageManager.getApplicationIcon(applicationInfo);
    }

    public static synchronized String getAppName(Context context, String packageName){
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.applicationInfo.loadLabel(packageManager).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return packageName;
        }
    }

    public static synchronized String getVersionName(Context context, String packageName){
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Drawable readApkIcon(Context context, String apkFilePath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo == null) {
            return null;
        }
//        String packageName = packageInfo.packageName;
//        if (map.containsKey(packageName)) {
//            return map.get(packageName);
//        }

        packageInfo.applicationInfo.sourceDir = apkFilePath;
        packageInfo.applicationInfo.publicSourceDir = apkFilePath;

        Drawable drawable = null;
        try {
            drawable = packageManager.getApplicationIcon(packageInfo.applicationInfo);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    public static void clearCache() {
//        map.clear();
    }

}
