package com.zpj.shouji.market.model;

import android.support.annotation.Keep;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.BeanUtils.Select;

@Keep
public class GuessAppInfo {

    @Select(selector = "icon")
    private String appIcon;
    @Select(selector = "title")
    private String appTitle;
    @Select(selector = "id")
    private String appId;
    @Select(selector = "subviewtype")
    private String appViewType;
    @Select(selector = "subapptype")
    private String appType;
    @Select(selector = "package")
    private String appPackage;
    @Select(selector = "m")
    private String appSize;
    @Select(selector = "comment")
    private String appComment;

    public static GuessAppInfo parse(Element item) {
        return BeanUtils.createBean(item, GuessAppInfo.class);
    }

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

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getAppComment() {
        return appComment;
    }

    public void setAppComment(String appComment) {
        this.appComment = appComment;
    }


}
