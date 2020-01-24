package com.zpj.shouji.market.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.zpj.http.parser.html.nodes.Element;

public class AppInfo implements Parcelable {
    private String appIcon;
    private String appTitle;
    private String appId;
    private String appViewType;
    private String appType;
    private String appPackage;
    private String appArticleNum;
    private String appNum;
    private String appMinSdk;
    private String appSize;
    private String appInfo;
    private String appComment;

    public static AppInfo create(Element item) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppIcon(item.select("icon").text());
        appInfo.setAppTitle(item.select("title").text());
        appInfo.setAppId(item.select("id").text());
        appInfo.setAppViewType(item.select("viewtype").text());
        appInfo.setAppType(item.select("apptype").text());
        appInfo.setAppPackage(item.select("package").text());
        appInfo.setAppArticleNum(item.select("articleNum").text());
        appInfo.setAppNum(item.select("appNum").text());
        appInfo.setAppMinSdk(item.select("msdk").text());
        appInfo.setAppSize(item.select("m").text());
        appInfo.setAppInfo(item.select("r").text());
        appInfo.setAppComment(item.select("comment").text());
        return appInfo;
    }

    public AppInfo() {
        super();
    }

    protected AppInfo(Parcel in) {
        appIcon = in.readString();
        appTitle = in.readString();
        appId = in.readString();
        appViewType = in.readString();
        appType = in.readString();
        appPackage = in.readString();
        appArticleNum = in.readString();
        appNum = in.readString();
        appMinSdk = in.readString();
        appSize = in.readString();
        appInfo = in.readString();
        appComment = in.readString();
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppViewType() {
        return appViewType;
    }

    public void setAppViewType(String appViewType) {
        this.appViewType = appViewType;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getAppArticleNum() {
        return appArticleNum;
    }

    public void setAppArticleNum(String appArticleNum) {
        this.appArticleNum = appArticleNum;
    }

    public String getAppNum() {
        return appNum;
    }

    public void setAppNum(String appNum) {
        this.appNum = appNum;
    }

    public String getAppMinSdk() {
        return appMinSdk;
    }

    public void setAppMinSdk(String appMinSdk) {
        this.appMinSdk = appMinSdk;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(String appInfo) {
        this.appInfo = appInfo;
    }

    public String getAppComment() {
        return appComment;
    }

    public void setAppComment(String appComment) {
        this.appComment = appComment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appIcon);
        dest.writeString(appTitle);
        dest.writeString(appId);
        dest.writeString(appViewType);
        dest.writeString(appType);
        dest.writeString(appPackage);
        dest.writeString(appArticleNum);
        dest.writeString(appNum);
        dest.writeString(appMinSdk);
        dest.writeString(appSize);
        dest.writeString(appInfo);
        dest.writeString(appComment);
    }
}
