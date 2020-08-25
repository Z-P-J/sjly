package com.zpj.shouji.market.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.felix.atoast.library.AToast;
import com.zpj.shouji.market.model.InstalledAppInfo;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class AppUtil {

    public static final int INSTALL_REQUEST_CODE = 1;
    public static final int UNINSTALL_REQUEST_CODE = 2;

    public final static String SHA1 = "SHA1";


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

    public static void installApk(Context context, String path) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = "application/vnd.android.package-archive";
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(new File(path)), type);
        //跳转
        context.startActivity(intent);
//        context.startActivityForResult(intent, INSTALL_REQUEST_CODE);
    }

    public static void deleteApk(String path) {
        File file = new File(path);
        if (file.exists() && file.delete()) {
            AToast.success("删除成功！");
        } else {
            AToast.warning("删除失败！");
        }
    }

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
        FileUtils.copyFile(new File(appInfo.getApkFilePath()), getAppBackupFile(appInfo));
    }

    public static String getSignatureString(Signature sig, String type) {
        byte[] hexBytes = sig.toByteArray();
        String fingerprint = "error!";
        try {
            MessageDigest digest = MessageDigest.getInstance(type);
            if (digest != null) {
                byte[] digestBytes = digest.digest(hexBytes);
                StringBuilder sb = new StringBuilder();
                for (byte digestByte : digestBytes) {
                    sb.append((Integer.toHexString((digestByte & 0xFF) | 0x100)).substring(1, 3));
                }
                fingerprint = sb.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return fingerprint;
    }

    public static String getSingInfo(Context context, String packageName, String type) {
        String tmp = null;
        Signature[] signs = getSignatures(context, packageName);
        if (signs == null) {
            return null;
        }
        for (Signature sig : signs) {
            if (SHA1.equals(type)) {
                tmp = getSignatureString(sig, SHA1);
                break;
            }
        }
        return tmp;
    }

    public static String getSignature(Context context, String packageName) {
        String tmp = null;
        Signature[] signs = getSignatures(context, packageName);
        if (signs == null) {
            return null;
        }
        for (Signature sig : signs) {
//            if (SHA1.equals(type)) {
//                tmp = getSignatureString(sig, SHA1);
//                break;
//            }
            tmp = getSignatureString(sig, SHA1);
            break;
        }
        return tmp;
    }

    public static Signature[] getSignatures(Context context, String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return packageInfo.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
