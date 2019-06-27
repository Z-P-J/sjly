package com.zpj.sjly.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.felix.atoast.library.AToast;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AppUtil {

    public static final int UNINSTALL_REQUEST_CODE = 1;

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

    public static void openApp(Context context, String packageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            AToast.error("打开应用失败！" + e.getMessage());
        }
    }

    public static void uninstallApp(Activity context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        context.startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
    }

    public static void shareApk(Context context, String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
        intent.setType("application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "分享应用"));
    }

}
