package com.zpj.shouji.market.model;

import android.support.annotation.Keep;

import com.zpj.shouji.market.utils.BeanUtils.Select;

@Keep
public class UserDownloadedAppInfo {

    private String id;
    @Select(selector = "downid")
    private String downId;
    private String title;
    @Select(selector = "package")
    private String packageName;
    @Select(selector = "apptype")
    private String appType;
    @Select(selector = "m")
    private String appSize;
    @Select(selector = "r")
    private String downloadTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDownId() {
        return downId;
    }

    public void setDownId(String downId) {
        this.downId = downId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(String downloadTime) {
        this.downloadTime = downloadTime;
    }
}
