package com.zpj.shouji.market.model;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.BeanUtils.Select;

public class PickedGameInfo {

    @Select(selector = "icon")
    private String appIcon;
    @Select(selector = "title")
    private String appName;
    @Select(selector = "id")
    private String appId;
    @Select(selector = "viewtype")
    private String viewType;
    @Select(selector = "apptype")
    private String appType;
    @Select(selector = "package")
    private String packageName;
    @Select(selector = "articleNum")
    private String articleNum;
    @Select(selector = "appNum")
    private String appNum;
    @Select(selector = "m")
    private String appSize;
    @Select(selector = "r")
    private String appInfo;
    @Select(selector = "comment")
    private String comment;
    @Select(selector = "favcount")
    private String favCount;
    @Select(selector = "reviewcount")
    private String reviewCount;
    @Select(selector = "viewcount")
    private String viewCount;
    @Select(selector = "memberid")
    private String memberId;
    @Select(selector = "nickname")
    private String nickname;
    @Select(selector = "memberavatar")
    private String memberAvatar;
    @Select(selector = "time")
    private String time;

    public static PickedGameInfo from(Element element) {
        return BeanUtils.createBean(element, PickedGameInfo.class);
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setArticleNum(String articleNum) {
        this.articleNum = articleNum;
    }

    public void setAppNum(String appNum) {
        this.appNum = appNum;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public void setAppInfo(String appInfo) {
        this.appInfo = appInfo;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setFavCount(String favCount) {
        this.favCount = favCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setMemberAvatar(String memberAvatar) {
        this.memberAvatar = memberAvatar;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public String getAppName() {
        return appName;
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

    public String getArticleNum() {
        return articleNum;
    }

    public String getAppNum() {
        return appNum;
    }

    public String getAppSize() {
        return appSize;
    }

    public String getAppInfo() {
        return appInfo;
    }

    public String getComment() {
        return comment;
    }

    public String getFavCount() {
        return favCount;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public String getViewCount() {
        return viewCount;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getMemberAvatar() {
        return memberAvatar;
    }

    public String getTime() {
        return time;
    }
}
