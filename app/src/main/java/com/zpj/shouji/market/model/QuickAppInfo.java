package com.zpj.shouji.market.model;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.download.MissionDelegate;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.BeanUtils.Select;

public class QuickAppInfo extends MissionDelegate {


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

//    public static QuickAppInfo parse(Element item) {
//        QuickAppInfo info = BeanUtils.createBean(item, QuickAppInfo.class);
//        info.init();
//        return info;
//    }

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

    @Override
    public String getAppName() {
        return appTitle;
    }

    @Override
    public String getAppType() {
        return appType;
    }

    @Override
    public String getPackageName() {
        return appPackage;
    }

    @Override
    public String getAppIcon() {
        return null;
    }

    @Override
    public boolean isShareApp() {
        return false;
    }

    public String getAppPackage() {
        return appPackage;
    }

    @Override
    public String getYunUrl() {
        return yunUrl;
    }

}
