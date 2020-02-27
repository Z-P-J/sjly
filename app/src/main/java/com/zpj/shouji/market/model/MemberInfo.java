package com.zpj.shouji.market.model;

import android.text.TextUtils;

import com.zpj.http.parser.html.nodes.Document;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public class MemberInfo {

    private String result;
    private String info;
    @Select(selector = "jsession")
    private String sessionId;
    private String autoUser;
    private boolean bindQQ;
    private String bindQQName;
    private boolean bindWX;
    private String bindWXName;
    private String memberName;
    private String memberId;
    private String shielding;
    private String memberNickName;
    private String memberAvatar;
    private String memberBackGround;
    private String memberSignature;
    @Select(selector = "iconstate")
    private String iconState;
    @Select(selector = "memberLeval")
    private String memberLevel;
    private String memberEmail;
    private String memberScoreInfo;
    private boolean canSigned;
    private boolean canUploadFile;
    private boolean canReview;
    private String reviewInfo;
    private boolean canPublish;
    private String publishInfo;
    private boolean canApplyApp;
    private String applyApp;
    private boolean canChangeNick;
    @Select(selector = "favCount")
    private String myFavCount;
    @Select(selector = "reviewCount")
    private String myReviewCount;
    @Select(selector = "downCount")
    private String myDownCount;
    @Select(selector = "fensiCount")
    private String myFansCount;
    private String registerDate;
    private String loginDate;

    @Select(selector = "message")
    private int messageCount;
    @Select(selector = "aite")
    private int atCount;
    @Select(selector = "faxian")
    private int discoverCount;
    @Select(selector = "private")
    private int privateCount;
    @Select(selector = "flower")
    private int followerCount;
    @Select(selector = "fensi")
    private int fansCount;
    @Select(selector = "haoyoucontent")
    private int haoyoucontentCount;
    private int type;
    private boolean isTestUser;
    private String sn;

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Select {
        String selector() default "";
    }

    public static MemberInfo from(Document doc) {
        MemberInfo info = new MemberInfo();
        try {
            for(Field field : info.getClass().getDeclaredFields()) {
                String name = field.getName();
                String type = field.getGenericType().toString();
                Select selectAnnotation = field.getAnnotation(Select.class);
                String selector = "";
                if (selectAnnotation != null) {
                    selector = selectAnnotation.selector();
                }
                if (TextUtils.isEmpty(selector)) {
                    selector = name;
                }
                if (type.equals("class java.lang.Boolean")) {
                    field.setAccessible(true);
                    field.setBoolean(info, !"0".equals(doc.selectFirst(selector).text()));
                } else if (type.equals("class java.lang.Integer")) {
                    field.setAccessible(true);
                    field.setInt(info, Integer.parseInt(doc.selectFirst(selector).text()));
                } else if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class
                    field.setAccessible(true);
                    field.set(info, doc.selectFirst(selector).text());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return info;
    }

    public String getResult() {
        return result;
    }

    public String getInfo() {
        return info;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getAutoUser() {
        return autoUser;
    }

    public boolean isBindQQ() {
        return bindQQ;
    }

    public String getBindQQName() {
        return bindQQName;
    }

    public boolean isBindWX() {
        return bindWX;
    }

    public String getBindWXName() {
        return bindWXName;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getShielding() {
        return shielding;
    }

    public String getMemberNickName() {
        return memberNickName;
    }

    public String getMemberAvatar() {
        return memberAvatar;
    }

    public String getMemberBackGround() {
        return memberBackGround;
    }

    public String getMemberSignature() {
        return memberSignature;
    }

    public String getIconState() {
        return iconState;
    }

    public String getMemberLevel() {
        return memberLevel;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public String getMemberScoreInfo() {
        return memberScoreInfo;
    }

    public boolean isCanSigned() {
        return canSigned;
    }

    public boolean isCanUploadFile() {
        return canUploadFile;
    }

    public boolean isCanReview() {
        return canReview;
    }

    public String getReviewInfo() {
        return reviewInfo;
    }

    public boolean isCanPublish() {
        return canPublish;
    }

    public String getPublishInfo() {
        return publishInfo;
    }

    public boolean isCanApplyApp() {
        return canApplyApp;
    }

    public String getApplyApp() {
        return applyApp;
    }

    public boolean isCanChangeNick() {
        return canChangeNick;
    }

    public String getMyFavCount() {
        return myFavCount;
    }

    public String getMyReviewCount() {
        return myReviewCount;
    }

    public String getMyDownCount() {
        return myDownCount;
    }

    public String getMyFansCount() {
        return myFansCount;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public int getAtCount() {
        return atCount;
    }

    public int getDiscoverCount() {
        return discoverCount;
    }

    public int getPrivateCount() {
        return privateCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFansCount() {
        return fansCount;
    }

    public int getHaoyoucontentCount() {
        return haoyoucontentCount;
    }

    public int getType() {
        return type;
    }

    public boolean isTestUser() {
        return isTestUser;
    }

    public String getSn() {
        return sn;
    }

    @Override
    public String toString() {
        return "MemberInfo{" +
                "result='" + result + '\'' +
                ", info='" + info + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", autoUser='" + autoUser + '\'' +
                ", bindQQ=" + bindQQ +
                ", bindQQName='" + bindQQName + '\'' +
                ", bindWX=" + bindWX +
                ", bindWXName='" + bindWXName + '\'' +
                ", memberName='" + memberName + '\'' +
                ", memberId='" + memberId + '\'' +
                ", shielding='" + shielding + '\'' +
                ", memberNickName='" + memberNickName + '\'' +
                ", memberAvatar='" + memberAvatar + '\'' +
                ", memberBackGround='" + memberBackGround + '\'' +
                ", memberSignature='" + memberSignature + '\'' +
                ", iconstate='" + iconState + '\'' +
                ", memberLevel='" + memberLevel + '\'' +
                ", memberEmail='" + memberEmail + '\'' +
                ", memberScoreInfo='" + memberScoreInfo + '\'' +
                ", canSigned=" + canSigned +
                ", canUploadFile=" + canUploadFile +
                ", canReview=" + canReview +
                ", reviewInfo='" + reviewInfo + '\'' +
                ", canPublish=" + canPublish +
                ", publishInfo='" + publishInfo + '\'' +
                ", canApplyApp=" + canApplyApp +
                ", applyApp='" + applyApp + '\'' +
                ", canChangeNick=" + canChangeNick +
                ", myFavCount='" + myFavCount + '\'' +
                ", myReviewCount='" + myReviewCount + '\'' +
                ", myDownCount='" + myDownCount + '\'' +
                ", myFensiCount='" + myFansCount + '\'' +
                ", registerDate='" + registerDate + '\'' +
                ", loginDate='" + loginDate + '\'' +
                ", messageCount=" + messageCount +
                ", aiteCount=" + atCount +
                ", discoverCount=" + discoverCount +
                ", privateCount=" + privateCount +
                ", followerCount=" + followerCount +
                ", fensiCount=" + fansCount +
                ", haoyoucontentCount=" + haoyoucontentCount +
                ", type=" + type +
                ", isTestUser=" + isTestUser +
                ", sn='" + sn + '\'' +
                '}';
    }
}
