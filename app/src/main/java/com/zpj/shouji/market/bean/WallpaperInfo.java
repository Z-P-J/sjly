package com.zpj.shouji.market.bean;

import com.zpj.http.parser.html.nodes.Element;

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
}
