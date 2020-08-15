package com.zpj.shouji.market.model;

import android.text.TextUtils;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.utils.BeanUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscoverInfo {

    private String id;

    private String parent;

    private List<DiscoverInfo> children = new ArrayList<>(0);

    private DiscoverInfo parentItem;

    private String contentType;

    private String type;

    private String reviewType;

    private String mmd;

    private String icon;

    private String memberId;

    private String nickName;

    private String toNickName;

    private String toMemberId;

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

    private String shareCount;

    private String shareTitle;

    private boolean isLast;

    private boolean isDetail;

    private String[] supportUsers;

    private String appId;

    private String softId;

    private String appType;

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

    private List<String> sharePics = new ArrayList<>(0);

    private List<String> sharePns = new ArrayList<>(0);

    private String admin;

    private String shielding;

    private List<String> pics = new ArrayList<>(0);

    private List<String> spics = new ArrayList<>(0);

    private final List<SupportUserInfo> supportUserInfoList = new ArrayList<>();

    private boolean isLike;


    public static DiscoverInfo from(Element element) {
        String type = element.selectFirst("type").text();
        if ("tag".equals(type) || "lable".equals(type)) {
            return null;
        }
        String id = element.selectFirst("id").text();
        String parent = element.selectFirst("parent").text();
        if (id == null && parent == null) {
            return null;
        }
        DiscoverInfo info = new DiscoverInfo();
        info.setId(id);
        info.setParent(parent);
        info.setMemberId(element.selectFirst("memberid").text());
        info.setContentType(element.selectFirst("contenttype").text());

        info.setIcon(element.selectFirst("icon").text());
        info.setIconState(element.selectFirst("iconstate").text());
        info.setNickName(element.selectFirst("nickname").text());
        info.setTime(element.selectFirst("time").text());
        info.setContent(element.selectFirst("content").text());
        info.setPhone(element.selectFirst("phone").text());
        info.setCollection("1".equals(element.selectFirst("iscollection").text()));
        info.setIsShared(element.selectFirst("isShared").text());
        info.setLast("1".equals(element.selectFirst("islast").text()));
        info.setDetail("1".equals(element.selectFirst("isdetail").text()));

        Elements pics = element.select("pics");
        Elements spics = element.select("spics");
        if (!pics.isEmpty()) {
            for (Element pic : element.selectFirst("pics").select("pic")) {
                info.addPic(pic.text());
            }
        }
        if (!spics.isEmpty()) {
            for (Element spic : element.selectFirst("spics").select("spic")) {
                info.addSpic(spic.text());
            }
        }
        if ("theme".equals(type)) {
            // 分享应用
            Elements appNames = element.select("appname");
            if (!appNames.isEmpty()) {
                info.setAppName(appNames.get(0).text());
                info.setAppPackageName(element.selectFirst("apppackagename").text());
                info.setAppIcon(element.selectFirst("appicon").text()
                        .replaceAll("img.shouji.com.cn", "imgo.tljpxm.com"));
                info.setApkExist("1".equals(element.selectFirst("isApkExist").text()));
                info.setAppUrl(element.selectFirst("appurl").text());
                info.setApkUrl(element.selectFirst("apkurl").text());
                info.setAppSize(element.selectFirst("apksize").text());
                info.setAppId(element.selectFirst("appid").text());
                info.setSoftId(element.selectFirst("softid").text());
                info.setAppType(element.selectFirst("apptype").text());
            }

            // 分享应用集
            info.setShareCount(element.selectFirst("sharecount").text());
            info.setShareTitle(element.selectFirst("sharetit").text());


            for (Element sharePic : element.select("sharepics").select("sharepic")) {
                info.addSharePic(sharePic.text());
            }


            for (Element sharePn : element.select("sharepics").select("sharepn")) {
                info.addSharePn(sharePn.text());
            }

        } else if ("reply".equals(type)) {
            String toNickName = element.selectFirst("tonickname").text();
            info.setToNickName(toNickName);
            info.setToMemberId(element.selectFirst("tomemberid").text());
        }

        Elements supportCountElements = element.select("supportcount");
        info.setSupportCount(supportCountElements.isEmpty() ? "0" : supportCountElements.get(0).text());
        Elements replayCountElements = element.select("replycount");
        info.setReplyCount(replayCountElements.isEmpty() ? "0" : replayCountElements.get(0).text());
        String userId = UserManager.getInstance().getUserId();
        for (Element support : element.selectFirst("supportusers").select("supportuser")) {
            SupportUserInfo supportUserInfo = BeanUtils.createBean(support, SupportUserInfo.class);
            info.supportUserInfoList.add(supportUserInfo);
            if (!TextUtils.isEmpty(userId)
                    && TextUtils.equals(supportUserInfo.getUserId(), userId)) {
                info.isLike = true;
            }
        }
        return info;
    }


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

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
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

    public String getShareCount() {
        return shareCount;
    }

    public void setShareCount(String shareCount) {
        this.shareCount = shareCount;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareTitle() {
        return shareTitle;
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

    public List<DiscoverInfo> getChildren() {
        return children;
    }

    public void addChild(DiscoverInfo child) {
        this.children.add(child);
    }

    public DiscoverInfo getParentItem() {
        return parentItem;
    }

    public void setParentItem(DiscoverInfo parentItem) {
        this.parentItem = parentItem;
    }

    public String getToNickName() {
        return toNickName;
    }

    public void setToNickName(String toNickName) {
        this.toNickName = toNickName;
    }

    public String getToMemberId() {
        return toMemberId;
    }

    public void setToMemberId(String toMemberId) {
        this.toMemberId = toMemberId;
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

    public List<SupportUserInfo> getSupportUserInfoList() {
        return supportUserInfoList;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean isLike() {
        return isLike;
    }

    @Override
    public String toString() {
        return "DiscoverInfo{" +
                "id='" + id + '\'' +
                ", parent='" + parent + '\'' +
                ", children=" + children +
                ", parentItem=" + parentItem +
                ", contentType='" + contentType + '\'' +
                ", type='" + type + '\'' +
                ", reviewType='" + reviewType + '\'' +
                ", mmd='" + mmd + '\'' +
                ", icon='" + icon + '\'' +
                ", memberId='" + memberId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", toNickName='" + toNickName + '\'' +
                ", memberType='" + memberType + '\'' +
                ", iconState='" + iconState + '\'' +
                ", time='" + time + '\'' +
                ", lt='" + lt + '\'' +
                ", replyCount='" + replyCount + '\'' +
                ", isNewMessage=" + isNewMessage +
                ", isCollection=" + isCollection +
                ", isDel=" + isDel +
                ", isReturn=" + isReturn +
                ", phone='" + phone + '\'' +
                ", supportCount='" + supportCount + '\'' +
                ", content='" + content + '\'' +
                ", isShared='" + isShared + '\'' +
                ", shareInfo='" + shareInfo + '\'' +
                ", isLast=" + isLast +
                ", isDetail=" + isDetail +
                ", supportUsers=" + Arrays.toString(supportUsers) +
                ", appId='" + appId + '\'' +
                ", softId='" + softId + '\'' +
                ", appName='" + appName + '\'' +
                ", appPackageName='" + appPackageName + '\'' +
                ", articleNum='" + articleNum + '\'' +
                ", appIcon='" + appIcon + '\'' +
                ", isApkExist=" + isApkExist +
                ", shoulusq='" + shoulusq + '\'' +
                ", cause='" + cause + '\'' +
                ", appUrl='" + appUrl + '\'' +
                ", apkUrl='" + apkUrl + '\'' +
                ", appSize='" + appSize + '\'' +
                ", shareCount=" + shareCount +
                ", sharePics=" + sharePics +
                ", sharePns=" + sharePns +
                ", admin='" + admin + '\'' +
                ", shielding='" + shielding + '\'' +
                ", pics=" + pics +
                ", spics=" + spics +
                ", supportUserInfoList=" + supportUserInfoList +
                '}';
    }
}
