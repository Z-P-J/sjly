package com.zpj.shouji.market.model;

import android.support.annotation.Keep;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.BeanUtils.Select;

@Keep
public class SupportUserInfo {

    @Select(selector = "supportuserid")
    private String userId;
    @Select(selector = "supportusernickname")
    private String nickName;
    @Select(selector = "supportuserlogo")
    private String userLogo;

//    public static SupportUserInfo from(Element element) {
//        SupportUserInfo info = new SupportUserInfo();
//        info.userId = element.selectFirst("supportuserid").text();
//        info.nickName = element.selectFirst("supportusernickname").text();
//        info.userLogo = element.selectFirst("supportuserlogo").text();
//        return info;
//    }

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
