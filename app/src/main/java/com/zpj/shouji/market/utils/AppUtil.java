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
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.toast.ZToast;
import com.zpj.utils.ContextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

    public static synchronized String getApkVersionName(Context context, String filePath){
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            return info.versionName;
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
            ZToast.error("打开应用失败！" + e.getMessage());
        }
    }

    public static void uninstallApp(Context context, String packageName) {
        Activity activity = ContextUtils.getActivity(context);
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        activity.startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
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
            ZToast.success("删除成功！");
        } else {
            ZToast.warning("删除失败！");
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




    public static String getAppSignatureMD5(Context context, String pkgName) {
        return getAppSignature(context, pkgName, "MD5");
    }

    public static String getAppSignatureSHA1(Context context, String pkgName) {
        return getAppSignature(context, pkgName, "SHA1");
    }

    public static String getAppSignatureSHA256(Context context, String pkgName) {
        return getAppSignature(context, pkgName, "SHA256");
    }

    public static String getAppSignature(Context context, String pkgName, String algorithm) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(
                    pkgName, PackageManager.GET_SIGNATURES);
//            Signature[] signs;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                signs = packageInfo.signingInfo.getApkContentsSigners();
//            } else {
//                signs = packageInfo.signatures;
//            }
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            String signStr = hexDigest(sign.toByteArray(), algorithm);
            return signStr;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getApkSignatureMD5(Context context, String apkPath) throws Exception {
        byte[] bytes = getSignaturesFromApk(context, apkPath);
        String sign = hexDigest(bytes, "MD5");
        return sign;
    }

    public static String getApkSignatureSHA1(Context context, String apkPath) throws Exception {
        byte[] bytes = getSignaturesFromApk(context, apkPath);
        String sign = hexDigest(bytes, "SHA1");
        return sign;
    }

    public static String getApkSignatureSHA256(Context context, String apkPath) throws Exception {
        byte[] bytes = getSignaturesFromApk(context, apkPath);
        String sign = hexDigest(bytes, "SHA256");
        return sign;
    }

    public static byte[] getSignaturesFromApk(Context context, String apkPath) throws IOException {
        Log.d("getSignaturesFromApk", "apkPath=" + apkPath);
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES);
            Signature[] signatures = info.signatures;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                signatures = info.signingInfo.getSigningCertificateHistory();
//                if (signatures == null) {
//                    signatures = info.signatures;
//                }
//            } else {
//                signatures = info.signatures;
//            }
            return signatures[0].toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


//        File file = new File(apkPath);
//        JarFile jarFile = new JarFile(file);
//        try {
//            JarEntry je = jarFile.getJarEntry("AndroidManifest.xml");
//            byte[] readBuffer = new byte[8192];
//            Certificate[] certs = loadCertificates(jarFile, je, readBuffer);
//            if (certs != null) {
//                for (Certificate c : certs) {
//                    return c.getEncoded();
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//        return getApkSignature(apkPath).toByteArray();
//        return getPackageArchiveInfo(apkPath);
    }

    public static Signature getApkSignature(String apkPath) {
        String PATH_PackageParser = "android.content.pm.PackageParser";
        try {
            // apk文件的文件路径
            // 这是一个Package解释器, 是隐藏的
            // 构造函数的参数只有一个, apk文件的路径
            // PackageParser packageParser = new PackageParser(apkPath);
            Class pkgParserCls = Class.forName(PATH_PackageParser);
            Class[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            // 这个是与显示有关的, 里面涉及到一些像素显示等等, 我们使用默认的情况
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            Constructor pkgParserCt;
            Object pkgParser;
            if (Build.VERSION.SDK_INT > 20) {
                pkgParserCt = pkgParserCls.getConstructor();
                pkgParser = pkgParserCt.newInstance();
                Method pkgParser_parsePackageMtd = pkgParserCls
                        .getDeclaredMethod("parsePackage", File.class, int.class);
                Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
                        new File(apkPath), PackageManager.GET_SIGNATURES);
                Method pkgParser_collectCertificatesMtd = pkgParserCls
                        .getDeclaredMethod("collectCertificates",
                                pkgParserPkg.getClass(), Integer.TYPE);
                if (Build.VERSION.SDK_INT >= 28) {
                    pkgParser_collectCertificatesMtd.invoke(pkgParser, pkgParserPkg,
                            Build.VERSION.SDK_INT > 28);
                    Field mSigningDetailsField = pkgParserPkg.getClass()
                            .getDeclaredField("mSigningDetails"); // SigningDetails
                    mSigningDetailsField.setAccessible(true);
                    Object mSigningDetails = mSigningDetailsField.get(pkgParserPkg);
                    Field infoField = mSigningDetails.getClass().getDeclaredField("signatures");
                    infoField.setAccessible(true);
                    Signature[] info = (Signature[]) infoField.get(mSigningDetails);
                    return info[0];
                } else {
                    pkgParser_collectCertificatesMtd.invoke(pkgParser, pkgParserPkg,
                            PackageManager.GET_SIGNATURES);
                    Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField("mSignatures");
                    Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
                    return info[0];
                }
            } else {
                pkgParserCt = pkgParserCls.getConstructor(typeArgs);
                pkgParser = pkgParserCt.newInstance(apkPath);
                Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage",
                        File.class, String.class, DisplayMetrics.class, Integer.TYPE);
                Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, new File(apkPath),
                        apkPath, metrics, PackageManager.GET_SIGNATURES);
                Method pkgParser_collectCertificatesMtd = pkgParserCls
                        .getDeclaredMethod("collectCertificates", pkgParserPkg.getClass(), Integer.TYPE);
                pkgParser_collectCertificatesMtd.invoke(pkgParser, pkgParserPkg, PackageManager.GET_SIGNATURES);
                // 应用程序信息包, 这个公开的, 不过有些函数, 变量没公开
                Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField("mSignatures");
                Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
                return info[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static byte[] getPackageArchiveInfo(String apkFile){

        //这个是与显示有关的, 里面涉及到一些像素显示等等, 我们使用默认的情况
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.setToDefaults();
        Object pkgParserPkg = null;
        Class[] typeArgs = null;
        Object[] valueArgs = null;
        try {
            Class<?> packageParserClass = Class.forName("android.content.pm.PackageParser");
            Constructor<?> packageParserConstructor = null;
            Object packageParser = null;
            //由于SDK版本问题，这里需做适配，来生成不同的构造函数
            if (Build.VERSION.SDK_INT > 20) {
                //无参数 constructor
                packageParserConstructor = packageParserClass.getDeclaredConstructor();
                packageParser = packageParserConstructor.newInstance();
                packageParserConstructor.setAccessible(true);//允许访问

                typeArgs = new Class[2];
                typeArgs[0] = File.class;
                typeArgs[1] = int.class;
                Method pkgParser_parsePackageMtd = packageParserClass.getDeclaredMethod("parsePackage", typeArgs);
                pkgParser_parsePackageMtd.setAccessible(true);

                valueArgs = new Object[2];
                valueArgs[0] = new File(apkFile);
                valueArgs[1] = PackageManager.GET_SIGNATURES;
                pkgParserPkg = pkgParser_parsePackageMtd.invoke(packageParser, valueArgs);
            } else {
                //低版本有参数 constructor
                packageParserConstructor = packageParserClass.getDeclaredConstructor(String.class);
                Object[] fileArgs = { apkFile };
                packageParser = packageParserConstructor.newInstance(fileArgs);
                packageParserConstructor.setAccessible(true);//允许访问

                typeArgs = new Class[4];
                typeArgs[0] = File.class;
                typeArgs[1] = String.class;
                typeArgs[2] = DisplayMetrics.class;
                typeArgs[3] = int.class;

                Method pkgParser_parsePackageMtd = packageParserClass.getDeclaredMethod("parsePackage", typeArgs);
                pkgParser_parsePackageMtd.setAccessible(true);

                valueArgs = new Object[4];
                valueArgs[0] = new File(apkFile);
                valueArgs[1] = apkFile;
                valueArgs[2] = metrics;
                valueArgs[3] = PackageManager.GET_SIGNATURES;
                pkgParserPkg = pkgParser_parsePackageMtd.invoke(packageParser, valueArgs);
            }

            typeArgs = new Class[2];
            typeArgs[0] = pkgParserPkg.getClass();
            typeArgs[1] = int.class;
            Method pkgParser_collectCertificatesMtd = packageParserClass.getDeclaredMethod("collectCertificates", typeArgs);
            valueArgs = new Object[2];
            valueArgs[0] = pkgParserPkg;
            valueArgs[1] = PackageManager.GET_SIGNATURES;
            pkgParser_collectCertificatesMtd.invoke(packageParser, valueArgs);
            // 应用程序信息包, 这个公开的, 不过有些函数变量没公开
            Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField("mSignatures");
            Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
            return info[0].toByteArray();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加载签名
     *
     * @param jarFile
     * @param je
     * @param readBuffer
     * @return
     */
    public static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        try {
            InputStream is = jarFile.getInputStream(je);
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String hexDigest(byte[] bytes, String algorithm) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        byte[] md5Bytes = md5.digest(bytes);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

}
