package com.zpj.shouji.market.model;

import com.zpj.shouji.market.utils.BeanUtils.Select;

public class BookingAppInfo {

    @Select(selector = "icon")
    private String appIcon;
    @Select(selector = "title")
    private String appName;
    @Select(selector = "id")
    private String appId;
    @Select(selector = "apptype")
    private String appType;
    @Select(selector = "viewtype")
    private String viewType;
    @Select(selector = "num")
    private String bookingCount;
    @Select(selector = "autodownload")
    private String autoDownload;
    @Select(selector = "isYuyue")
    private boolean isBooking;
    @Select(selector = "comment")
    private String comment;
    @Select(selector = "r")
    private String bookingInfo;

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(String bookingCount) {
        this.bookingCount = bookingCount;
    }

    public boolean isAutoDownload() {
        return "3".equals(autoDownload);
    }

    public void setAutoDownload(boolean autoDownload) {
        this.autoDownload = autoDownload ? "3" : "1";
    }

    public void setBooking(boolean booking) {
        isBooking = booking;
    }

    public boolean isBooking() {
        return isBooking;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setBookingInfo(String bookingInfo) {
        this.bookingInfo = bookingInfo;
    }

    public String getBookingInfo() {
        return bookingInfo;
    }
}
