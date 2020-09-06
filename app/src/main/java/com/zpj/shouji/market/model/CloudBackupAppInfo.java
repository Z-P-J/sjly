package com.zpj.shouji.market.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.text.TextUtils;

import com.geek.banner.loader.BannerEntry;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.BeanUtils.Select;

public class CloudBackupAppInfo {

    @Select(selector = "icon")
    private String icon;
    @Select(selector = "appname")
    private String name;
    @Select(selector = "id")
    private String id;
    @Select(selector = "appid")
    private String appId;
    @Select(selector = "viewtype")
    private String viewType;
    @Select(selector = "apptype")
    private String appType;
    @Select(selector = "package")
    private String packageName;
    @Select(selector = "comment")
    private String comment;
    @Select(selector = "shoulu")
    private String exist;

    public static CloudBackupAppInfo from(Element item) {
        return BeanUtils.createBean(item, CloudBackupAppInfo.class);
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getAppId() {
        return appId;
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

    public String getComment() {
        if (TextUtils.isEmpty(comment)) {

        }
        return comment;
    }

    public String getExist() {
        return exist;
    }
}
