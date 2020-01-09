package com.zpj.shouji.market.bean;

import android.graphics.drawable.Drawable;

import com.zpj.shouji.market.utils.FileScanner;

public class AppPackageInfo extends BaseAppInfo implements FileScanner.FileItem {

    private String id;
    private String versionName;
    private String formattedAppSize;

    private String installTime;

    private String recentUpdateTime;

    private String sortName;
    private String apkFilePath;

    private boolean isTempXPK;
    private boolean isTempInstalled;

    private Drawable iconDrawable;

    @Override
    public String getFilePath() {
        return apkFilePath;
    }

    @Override
    public long getFileLength() {
        return Long.parseLong(getAppSize());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isInstalledApp() {
        return false;
    }

    @Override
    public boolean isApkPackage() {
        return false;
    }

    @Override
    public boolean isXpkPackage() {
        return false;
    }

    @Override
    public boolean isMarketApp() {
        return false;
    }

    public void setIdAndType(String idAndType) {
        if (idAndType == null) {
            return;
        }
        id = idAndType.substring(7);
        setAppType(idAndType.substring(0, 4));
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
