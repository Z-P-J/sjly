package com.zpj.shouji.market.model;

import com.zpj.http.parser.html.nodes.Element;

public class BlacklistInfo {

    private String id;
    private String memberId;
    private String avatarUrl;
    private String nickName;
    private boolean online;
    private String signature;

    public static BlacklistInfo from(Element element) {
        BlacklistInfo info = new BlacklistInfo();
        info.id = element.selectFirst("id").text();
        info.memberId = element.selectFirst("memberid").text();
        info.avatarUrl = element.selectFirst("avatar").text();
        info.nickName = element.selectFirst("nickname").text();
        info.online = "1".equals(element.selectFirst("online").text());
        info.signature = element.selectFirst("signature").text();
        return info;
    }

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

    public String getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", memberId='" + memberId + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", nickName='" + nickName + '\'' +
                ", online=" + online +
                ", signature='" + signature + '\'' +
                '}';
    }
}
