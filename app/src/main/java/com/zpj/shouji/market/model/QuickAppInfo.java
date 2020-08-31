package com.zpj.shouji.market.model;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.BeanUtils.Select;

public class QuickAppInfo {


    @Select(selector = "title")
    private String appTitle;
    @Select(selector = "id")
    private String appId;
    @Select(selector = "apptype")
    private String appType;
    @Select(selector = "package")
    private String appPackage;
    @Select(selector = "yunUrl")
    private String yunUrl;

    public static QuickAppInfo parse(Element item) {
        return BeanUtils.createBean(item, QuickAppInfo.class);
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public void setYunUrl(String yunUrl) {
        this.yunUrl = yunUrl;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppType() {
        return appType;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public String getYunUrl() {
        return yunUrl;
    }

}
