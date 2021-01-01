package com.zpj.shouji.market.model;

import android.support.annotation.Keep;

import com.zpj.shouji.market.utils.BeanUtils.Select;

@Keep
public class SubjectInfo {

    private String id;
    private String icon;
    private String title;
    private String m;
    @Select(selector = "viewtype")
    private String viewType;
    @Select(selector = "apptype")
    private String appType;
    private String comment;

//    public static SubjectInfo create(Element element) {
//        SubjectInfo item = new SubjectInfo();
//        item.setId(element.selectFirst("id").text());
//        item.setIcon(element.selectFirst("icon").text());
//        item.setTitle(element.selectFirst("title").text());
//        item.setViewType(element.selectFirst("viewtype").text());
//        item.setAppType(element.selectFirst("apptype").text());
//        item.setComment(element.selectFirst("comment").text());
//        item.setM(element.selectFirst("m").text());
//        return item;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
