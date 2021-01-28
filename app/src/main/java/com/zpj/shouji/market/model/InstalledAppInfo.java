package com.zpj.shouji.market.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.text.TextUtils;

import com.github.promeg.pinyinhelper.Pinyin;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.utils.AppUtils;
import com.zpj.utils.FormatUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.zip.ZipFile;

public class InstalledAppInfo {

    private String name;
    private String packageName;

    private String id;
    private String appType;
    private String versionName;
    private String formattedAppSize;

    private String installTime;

    private String recentUpdateTime;

    private String sortName;
    private String apkFilePath;

    private int versionCode;
    private long appSize;
    private boolean isTempXPK;
    private boolean isTempInstalled;

    private boolean enabled = true;
    private boolean isBackuped = false;
    private boolean isUserApp = true;
    private boolean isDamaged = false;

    private long firstInstallTime;
    private long lastUpdateTime;

    private String letter;

    private int letterAscii;

    private int targetSdk;
    private int minSdk;

    public static InstalledAppInfo parseFromPackageInfo(PackageManager manager, PackageInfo packageInfo) {
        InstalledAppInfo installedAppInfo = new InstalledAppInfo();
        installedAppInfo.setName(packageInfo.applicationInfo.loadLabel(manager).toString());
        installedAppInfo.setPackageName(packageInfo.packageName);
        installedAppInfo.setSortName(installedAppInfo.getName());
        installedAppInfo.setIdAndType(AppUpdateManager.getInstance().getAppIdAndType(installedAppInfo.getPackageName()));
        installedAppInfo.setVersionName(packageInfo.versionName);
        installedAppInfo.setApkFilePath(packageInfo.applicationInfo.publicSourceDir);
        long size = new File(installedAppInfo.getApkFilePath()).length();
        installedAppInfo.setAppSize(size);
        installedAppInfo.setFormattedAppSize(FormatUtils.formatSize(size));
        installedAppInfo.setVersionCode(packageInfo.versionCode);
        installedAppInfo.setTempXPK(false);
        installedAppInfo.setTempInstalled(true);
        installedAppInfo.setEnabled(packageInfo.applicationInfo.enabled);
        installedAppInfo.setBackuped(new File(AppUtil.getDefaultAppBackupFolder() + installedAppInfo.getName() + "_" + installedAppInfo.getVersionName() + ".apk").exists());
        installedAppInfo.setUserApp((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0);
        installedAppInfo.setFirstInstallTime(packageInfo.firstInstallTime);
        installedAppInfo.setLastUpdateTime(packageInfo.lastUpdateTime);
        installedAppInfo.targetSdk = packageInfo.applicationInfo.targetSdkVersion;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installedAppInfo.minSdk = packageInfo.applicationInfo.minSdkVersion;
        } else {
            installedAppInfo.minSdk = getMinSdkVersion(installedAppInfo.getApkFilePath());
        }
        return installedAppInfo;
    }

    public static int getMinSdkVersion(String path) {
        try {
            final Class assetManagerClass = Class.forName("android.content.res.AssetManager");
            final AssetManager assetManager = (AssetManager) assetManagerClass.newInstance();
            final Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            final int cookie = (Integer) addAssetPath.invoke(assetManager, path);
            final XmlResourceParser parser = assetManager.openXmlResourceParser(cookie, "AndroidManifest.xml");
            while (parser.next() != XmlPullParser.END_DOCUMENT)
                if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("uses-sdk"))
                    for (int i = 0; i < parser.getAttributeCount(); ++i)
                        if (parser.getAttributeNameResource(i) == android.R.attr.minSdkVersion)//alternative, which works most of the times: "minSdkVersion".equals(parser.getAttributeName(i)))
                            return parser.getAttributeIntValue(i, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static InstalledAppInfo parseFromApk(Context context, File file) {
        if (context == null) {
            return null;
        }
        PackageInfo packageInfo = null;
        PackageManager manager = context.getPackageManager();
        try {
            packageInfo = manager.getPackageArchiveInfo(file.getPath(), PackageManager.GET_ACTIVITIES);
        } catch (Throwable e) {
            e.printStackTrace();
//            return null;
        }

        InstalledAppInfo appInfo = new InstalledAppInfo();
        appInfo.setApkFilePath(file.getPath());
        appInfo.setAppSize(file.length());
        appInfo.setLastUpdateTime(file.lastModified());
        appInfo.setFormattedAppSize(FormatUtils.formatSize(file.length()));
        appInfo.setTempXPK(true);
        appInfo.setTempInstalled(false);
        if (packageInfo == null) {
            appInfo.setDamaged(true);
            appInfo.setName(file.getName());
            return appInfo;
        }


        packageInfo.applicationInfo.sourceDir = file.getPath();
        packageInfo.applicationInfo.publicSourceDir = file.getPath();
        appInfo.setName(String.valueOf(packageInfo.applicationInfo.loadLabel(manager)));

//        Log.d("parseFromApk", "name=" + appInfo.getName());
        appInfo.setPackageName(packageInfo.packageName);
        appInfo.setIdAndType(AppUpdateManager.getInstance().getAppIdAndType(appInfo.getPackageName()));
        appInfo.setVersionName(packageInfo.versionName);
        appInfo.setVersionCode(packageInfo.versionCode);

        appInfo.setFirstInstallTime(packageInfo.firstInstallTime);
        appInfo.setLastUpdateTime(packageInfo.lastUpdateTime);

        appInfo.targetSdk = packageInfo.applicationInfo.targetSdkVersion;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            appInfo.minSdk = packageInfo.applicationInfo.minSdkVersion;
        } else {
            appInfo.minSdk = getMinSdkVersion(appInfo.getApkFilePath());
        }

        return appInfo;
    }

    public static InstalledAppInfo parseFromXpk(File file) {
        XpkInfo xpkInfo;
        try {
            xpkInfo = XpkInfo.getXPKManifestDom(new ZipFile(file));
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            return null;
        }
        InstalledAppInfo appInfo = new InstalledAppInfo();
        appInfo.setApkFilePath(file.getPath());
        appInfo.setName(xpkInfo.getAppName());
        appInfo.setPackageName(xpkInfo.getPackageName());
        appInfo.setId(AppUpdateManager.getInstance().getAppIdAndType(appInfo.getPackageName()));
        appInfo.setVersionName(xpkInfo.getVersionName());
        appInfo.setVersionCode(xpkInfo.getVersionCode());
        appInfo.setAppSize(file.length());
        appInfo.setLastUpdateTime(file.lastModified());
        appInfo.setFormattedAppSize(FormatUtils.formatSize(file.length()));
        appInfo.setTempXPK(true);
        appInfo.setTempInstalled(false);
        return appInfo;
    }

    public String getFilePath() {
        return apkFilePath;
    }

    public long getFileLength() {
        return appSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

        if (!TextUtils.isEmpty(name)) {
            char ch = Pinyin.toPinyin(name.charAt(0)).toUpperCase().charAt(0);
            if (Character.isLetter(ch)) {
                setLetter(String.valueOf(ch));
                return;
            }
        }
        setLetter("#");
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getAppType() {
        return appType;
    }

    public void setIdAndType(String idAndType) {
        if (idAndType == null) {
            return;
        }
        id = idAndType.substring(7);
        appType = idAndType.substring(0, 4);
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getFormattedAppSize() {
        return formattedAppSize;
    }

    public void setFormattedAppSize(String formattedAppSize) {
        this.formattedAppSize = formattedAppSize;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getApkFilePath() {
        return apkFilePath;
    }

    public void setApkFilePath(String apkFilePath) {
        this.apkFilePath = apkFilePath;
    }

    public void setInstallTime(String installTime) {
        this.installTime = installTime;
    }

    public String getInstallTime() {
        return installTime;
    }

    public void setRecentUpdateTime(String recentUpdateTime) {
        this.recentUpdateTime = recentUpdateTime;
    }

    public String getRecentUpdateTime() {
        return recentUpdateTime;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public boolean isTempXPK() {
        return isTempXPK;
    }

    public void setTempXPK(boolean tempXPK) {
        isTempXPK = tempXPK;
    }

    public boolean isTempInstalled() {
        return isTempInstalled;
    }

    public void setTempInstalled(boolean tempInstalled) {
        isTempInstalled = tempInstalled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setBackuped(boolean backuped) {
        isBackuped = backuped;
    }

    public boolean isBackuped() {
        return isBackuped;
    }

    public void setUserApp(boolean userApp) {
        isUserApp = userApp;
    }

    public boolean isUserApp() {
        return isUserApp;
    }

    public void setDamaged(boolean damaged) {
        isDamaged = damaged;
    }

    public boolean isDamaged() {
        return isDamaged;
    }

    public void setFirstInstallTime(long firstInstallTime) {
        this.firstInstallTime = firstInstallTime;
    }

    public long getFirstInstallTime() {
        return firstInstallTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public int getLetterAscii() {
        return letterAscii;
    }

    public void setLetterAscii(int letterAscii) {
        this.letterAscii = letterAscii;
    }

    public int getTargetSdk() {
        return targetSdk;
    }

    public int getMinSdk() {
        return minSdk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InstalledAppInfo that = (InstalledAppInfo) o;
        return TextUtils.equals(this.packageName, that.packageName);
    }

    @Override
    public int hashCode() {
        return packageName != null ? packageName.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "InstalledAppInfo{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", id='" + id + '\'' +
                ", appType='" + appType + '\'' +
                ", versionName='" + versionName + '\'' +
                ", formattedAppSize='" + formattedAppSize + '\'' +
                ", installTime='" + installTime + '\'' +
                ", recentUpdateTime='" + recentUpdateTime + '\'' +
                ", sortName='" + sortName + '\'' +
                ", apkFilePath='" + apkFilePath + '\'' +
                ", versionCode=" + versionCode +
                ", appSize=" + appSize +
                ", isTempXPK=" + isTempXPK +
                ", isTempInstalled=" + isTempInstalled +
                ", enabled=" + enabled +
                ", isBackuped=" + isBackuped +
                ", isUserApp=" + isUserApp +
                ", isDamaged=" + isDamaged +
                '}';
    }
}
