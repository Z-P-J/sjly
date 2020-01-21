package com.zpj.shouji.market.model;

import com.zpj.http.parser.html.nodes.Element;

public class CollectionAppInfo {

    private String itemId;
    private String icon;
    private String title;
    private String id;
    private String viewType;
    private String appType;
    private String packageName;
    private String yunUrl;
    private String m;
    private String r;
    private String comment;
    private String cType;
    private boolean isApkExist;

    public static CollectionAppInfo from(Element element) {
        if (!"app".equals(element.selectFirst("viewtype").text())) {
            return null;
        }
        CollectionAppInfo info = new CollectionAppInfo();
        info.itemId = element.selectFirst("itemid").text();
        info.icon = element.selectFirst("icon").text();
        info.title = element.selectFirst("title").text();
        info.id = element.selectFirst("id").text();
        info.viewType = element.selectFirst("viewtype").text();
        info.appType = element.selectFirst("apptype").text();
        info.packageName = element.selectFirst("package").text();
        info.yunUrl = element.selectFirst("yunUrl").text();
        info.m = element.selectFirst("m").text();
        info.r = element.selectFirst("r").text();
        info.comment = element.selectFirst("comment").text();
        info.cType = element.selectFirst("ctype").text();
        info.isApkExist = "1".equals(element.selectFirst("isApkExist").text());
        return info;
    }

    public String getItemId() {
        return itemId;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getViewType() {
        return viewType;
    }

    public String getAppType() {
        return appType;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getYunUrl() {
        return yunUrl;
    }

    public String getM() {
        return m;
    }

    public String getR() {
        return r;
    }

    public String getComment() {
        return comment;
    }

    public String getcType() {
        return cType;
    }

    public boolean isApkExist() {
        return isApkExist;
    }
}
