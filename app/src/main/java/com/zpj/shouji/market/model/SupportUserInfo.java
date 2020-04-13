package com.zpj.shouji.market.model;

import com.zpj.http.parser.html.nodes.Element;

public class SupportUserInfo {

    private String userId;
    private String nickName;
    private String userLogo;

    public static SupportUserInfo from(Element element) {
        SupportUserInfo info = new SupportUserInfo();
        info.userId = element.selectFirst("supportuserid").text();
        info.nickName = element.selectFirst("supportusernickname").text();
        info.userLogo = element.selectFirst("supportuserlogo").text();
        return info;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickName() {
        return nickName;
    }

    public String getUserLogo() {
        return userLogo;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setUserLogo(String userLogo) {
        this.userLogo = userLogo;
    }
}
