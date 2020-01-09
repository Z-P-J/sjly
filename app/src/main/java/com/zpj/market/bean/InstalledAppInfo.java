package com.zpj.market.bean;

import android.graphics.drawable.Drawable;

import com.zpj.market.utils.FileScanner;

public class InstalledAppInfo implements FileScanner.FileItem {

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

    private Drawable iconDrawable;

    @Override
    public String getFilePath() {
        return apkFilePath;
    }

    @Override
    public long getFileLength() {
        return appSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }
}
