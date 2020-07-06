package com.zpj.shouji.market.model;

import android.support.annotation.Keep;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.BeanUtils.Select;

@Keep
public class UserInfo {

    private String id;
    @Select(selector = "memberid")
    private String memberId;
    @Select(selector = "membername")
    private String memberName;
    @Select(selector = "avatar")
    private String avatarUrl;
    @Select(selector = "nickname")
    private String nickName;
    private boolean online;
    private boolean imgFormat;
    private String signature;

//    public static UserInfo from(Element element) {
//
//        UserInfo info = new UserInfo();
//        info.id = element.selectFirst("id").text();
//        info.memberId = element.selectFirst("memberid").text();
//        info.avatarUrl = element.selectFirst("avatar").text();
//        info.nickName = element.selectFirst("nickname").text();
//        info.memberName = element.selectFirst("membername").text();
//        info.online = "1".equals(element.selectFirst("online").text());
//        info.imgFormat = "true".equals(element.selectFirst("imgFormat").text());
//        info.signature = element.selectFirst("signature").text();
//        return info;
//    }

    public String getId() {
        return id;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean isImgFormat() {
        return imgFormat;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", memberId='" + memberId + '\'' +
                ", memberName='" + memberName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", nickName='" + nickName + '\'' +
                ", online=" + online +
                ", imgFormat=" + imgFormat +
                ", signature='" + signature + '\'' +
                '}';
    }
}
