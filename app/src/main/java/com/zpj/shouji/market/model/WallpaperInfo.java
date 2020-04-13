package com.zpj.shouji.market.model;

import android.text.TextUtils;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.manager.UserManager;

public class WallpaperInfo {

    private String id;
    private String type;
    private String memberIcon;
    private String nickName;
    private String iconState;
    private String memberId;
    private String time;
    private String content;
    private String tag;
    private String width;
    private String height;
    private String spic;
    private String pic;
    private long supportCount = 0;
    private boolean isLike;

    public static WallpaperInfo create(Element element) {
        WallpaperInfo info = new WallpaperInfo();
        info.id = element.selectFirst("id").text();
        info.type = element.selectFirst("type").text();
        info.memberIcon = element.selectFirst("membericon").text();
        info.nickName = element.selectFirst("nickname").text();
        info.iconState = element.selectFirst("iconstate").text();
        info.memberId = element.selectFirst("memberid").text();
        info.time = element.selectFirst("time").text();
        info.content = element.selectFirst("content").text();
        info.tag = element.selectFirst("tag").text();
        info.width = element.selectFirst("pwidth").text();
        info.height = element.selectFirst("pheight").text();
        info.spic = element.selectFirst("spic").text();
        info.pic = element.selectFirst("pic").text();
        info.supportCount = Long.parseLong(element.selectFirst("supportcount").text());
        String userId = UserManager.getInstance().getUserId();
        if (!TextUtils.isEmpty(userId)) {
            for (Element support : element.selectFirst("supportusers").select("supportuser")) {
                String supportUserId = support.selectFirst("supportuserid").text();
                if (!TextUtils.isEmpty(userId)
                        && TextUtils.equals(supportUserId, userId)) {
                    info.isLike = true;
                    break;
                }
            }
        }

        return info;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getMemberIcon() {
        return memberIcon;
    }

    public String getNickName() {
        return nickName;
    }

    public String getIconState() {
        return iconState;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getTag() {
        return tag;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getSpic() {
        return spic;
    }

    public String getPic() {
        return pic;
    }

    public long getSupportCount() {
        return supportCount;
    }

    public void setSupportCount(long supportCount) {
        this.supportCount = supportCount;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean isLike() {
        return isLike;
    }
}
