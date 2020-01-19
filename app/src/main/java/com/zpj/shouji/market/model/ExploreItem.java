package com.zpj.shouji.market.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ExploreItem {

    private String id;

    private String parent;

    private List<ExploreItem> children = new ArrayList<>(0);

    private ExploreItem parentItem;

    private String contentType;

    private String type;

    private String reviewType;

    private String mmd;

    private String icon;

    private String memberId;

    private String nickName;

    private String toNickName;

    private String memberType;

    private String iconState;

    private String time;

    private String lt;

    private String replyCount;

    private boolean isNewMessage;

    private boolean isCollection;

    private boolean isDel;

    private boolean isReturn;

    private String phone;

    private String supportCount;

    private String content;

    private String isShared;

    private String shareInfo;

    private boolean isLast;

    private boolean isDetail;

    private String[] supportUsers;

    private String appId;

    private String softId;

    private String appName;

    private String appPackageName;

    private String articleNum;

    private String appIcon;

    private boolean isApkExist;

    private String shoulusq;

    private String cause;

    private String appUrl;

    private String apkUrl;

    private String appSize;

    private int shareCount;

    private List<String> sharePics = new ArrayList<>(0);

    private List<String> sharePns = new ArrayList<>(0);

    private String admin;

    private String shielding;

    private List<String> pics = new ArrayList<>(0);

    private List<String> spics = new ArrayList<>(0);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public String getMmd() {
        return mmd;
    }

    public void setMmd(String mmd) {
        this.mmd = mmd;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getIconState() {
        if (TextUtils.isEmpty(iconState)) {
            return "Lv.0";
        } else {
            return iconState.replace("L", "Lv.");
        }
    }

    public void setIconState(String iconState) {
        this.iconState = iconState;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLt() {
        return lt;
    }

    public void setLt(String lt) {
        this.lt = lt;
    }

    public String getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(String replyCount) {
        this.replyCount = replyCount;
    }

    public boolean isNewMessage() {
        return isNewMessage;
    }

    public void setNewMessage(boolean newMessage) {
        isNewMessage = newMessage;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public boolean isDel() {
        return isDel;
    }

    public void setDel(boolean del) {
        isDel = del;
    }

    public boolean isReturn() {
        return isReturn;
    }

    public void setReturn(boolean aReturn) {
        isReturn = aReturn;
    }

    public String getPhone() {
        if (TextUtils.isEmpty(phone)) {
            return "";
        } else {
            return "来自 " + phone;
        }
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSupportCount() {
        return supportCount;
    }

    public void setSupportCount(String supportCount) {
        this.supportCount = supportCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsShared() {
        return isShared;
    }

    public void setIsShared(String isShared) {
        this.isShared = isShared;
    }

    public String getShareInfo() {
        return shareInfo;
    }

    public void setShareInfo(String shareInfo) {
        this.shareInfo = shareInfo;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public boolean isDetail() {
        return isDetail;
    }

    public void setDetail(boolean detail) {
        isDetail = detail;
    }

    public String[] getSupportUsers() {
        return supportUsers;
    }

    public void setSupportUsers(String[] supportUsers) {
        this.supportUsers = supportUsers;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSoftId() {
        return softId;
    }

    public void setSoftId(String softId) {
        this.softId = softId;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String apppackagename) {
        this.appPackageName = apppackagename;
    }

    public String getArticleNum() {
        return articleNum;
    }

    public void setArticleNum(String articleNum) {
        this.articleNum = articleNum;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public boolean isApkExist() {
        return isApkExist;
    }

    public void setApkExist(boolean apkExist) {
        isApkExist = apkExist;
    }

    public String getShoulusq() {
        return shoulusq;
    }

    public void setShoulusq(String shoulusq) {
        this.shoulusq = shoulusq;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public List<String> getSharePics() {
        return sharePics;
    }

    public void addSharePic(String sharePic) {
        this.sharePics.add(sharePic);
    }

    public List<String> getSharePns() {
        return sharePns;
    }

    public void addSharePn(String sharePn) {
        this.sharePns.add(sharePn);
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getShielding() {
        return shielding;
    }

    public void setShielding(String shielding) {
        this.shielding = shielding;
    }

    public List<ExploreItem> getChildren() {
        return children;
    }

    public void addChild(ExploreItem child) {
        this.children.add(child);
    }

    public ExploreItem getParentItem() {
        return parentItem;
    }

    public void setParentItem(ExploreItem parentItem) {
        this.parentItem = parentItem;
    }

    public String getToNickName() {
        return toNickName;
    }

    public void setToNickName(String toNickName) {
        this.toNickName = toNickName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public List<String> getSpics() {
        return spics;
    }

    public void addSpic(String spic) {
        this.spics.add(spic);
    }

    public List<String> getPics() {
        return pics;
    }

    public void addPic(String pic) {
        this.pics.add(pic);
    }
}
