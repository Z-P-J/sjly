package com.zpj.sjly.bean;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;

public class AppUpdateInfo {

    private String appName;

    private String packageName;

    private String id;

    private String appType;

    private String downloadUrl;

    private String oldVersionName;

    private String newVersionName;

    private String newSize;

    private String updateTime;

    private String updateTimeInfo;

    private String updateInfo;

    private Drawable iconDrawable;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
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

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getOldVersionName() {
        return oldVersionName;
    }

    public void setOldVersionName(String oldVersionName) {
        this.oldVersionName = oldVersionName;
    }

    public String getNewVersionName() {
        return newVersionName;
    }

    public void setNewVersionName(String newVersionName) {
        this.newVersionName = newVersionName;
    }

    public String getNewSize() {
        return newSize;
    }

    public void setNewSize(String newSize) {
        this.newSize = newSize;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateTimeInfo() {
        return updateTimeInfo;
    }

    public void setUpdateTimeInfo(String updateTimeInfo) {
        this.updateTimeInfo = updateTimeInfo;
    }

    public String getUpdateInfo() {
        if (TextUtils.isEmpty(updateInfo)) {
            return "暂无新版特性";
        }
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    @Override
    public String toString() {
        return "AppUpdateInfo{" +
                "packageName='" + packageName + '\'' +
                ", id='" + id + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", newVersionName='" + newVersionName + '\'' +
                ", newSize='" + newSize + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", updateInfo='" + updateInfo + '\'' +
                '}';
    }
}
