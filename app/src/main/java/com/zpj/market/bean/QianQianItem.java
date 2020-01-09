package com.zpj.market.bean;

public class QianQianItem {

    private String app_site;
    private String app_img_site;
    private String app_title;
    private String app_description;
    private String app_type;
    private String app_info;

    public QianQianItem(String app_site, String app_img_site, String app_title, String app_description, String app_type, String app_info){
        this.app_site = app_site;
        this.app_img_site = app_img_site;
        this.app_title = app_title;
        this.app_description = app_description;
        this.app_type = app_type;
        this.app_info = app_info;
    }

    public void setApp_site(String app_site) {
        this.app_site = app_site;
    }

    public String getApp_site() {
        return app_site;
    }

    public void setApp_img_site(String app_img_site) {
        this.app_img_site = app_img_site;
    }

    public String getApp_img_site() {
        return app_img_site;
    }

    public void setApp_title(String app_title) {
        this.app_title = app_title;
    }

    public String getApp_title() {
        return app_title;
    }

    public void setApp_description(String app_description) {
        this.app_description = app_description;
    }

    public String getApp_description() {
        return app_description;
    }

    public void setApp_type(String app_type) {
        this.app_type = app_type;
    }

    public String getApp_type() {
        return app_type;
    }

    public void setApp_info(String app_info) {
        this.app_info = app_info;
    }

    public String getApp_info() {
        return app_info;
    }
}
