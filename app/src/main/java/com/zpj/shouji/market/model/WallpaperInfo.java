package com.zpj.shouji.market.model;

import android.support.annotation.Keep;
import android.text.TextUtils;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.BeanUtils.Select;

import java.util.ArrayList;
import java.util.List;

@Keep
public class WallpaperInfo {

    private String id;
    private String type;
    @Select(selector = "membericon")
    private String memberIcon;
    @Select(selector = "nickname")
    private String nickName;
    @Select(selector = "iconstate")
    private String iconState;
    @Select(selector = "memberid")
    private String memberId;
    private String time;
    private String content;
    private String tag;
    @Select(selector = "pwidth")
    private String width;
    @Select(selector = "pheight")
    private String height;
    @Select(selector = "imagecount")
    private int imageCount;
    private String spic;
    private String pic;
    @Select(selector = "supportcount")
    private int supportCount = 0;
    @Select(selector = "replycount")
    private int replyCount = 0;
    private boolean isLike;
    private final List<SupportUserInfo> supportUserInfoList = new ArrayList<>();

    public static WallpaperInfo create(Element element) {
//        WallpaperInfo info = new WallpaperInfo();
//        info.id = element.selectFirst("id").text();
//        info.type = element.selectFirst("type").text();
//        info.memberIcon = element.selectFirst("membericon").text();
//        info.nickName = element.selectFirst("nickname").text();
//        info.iconState = element.selectFirst("iconstate").text();
//        info.memberId = element.selectFirst("memberid").text();
//        info.time = element.selectFirst("time").text();
//        info.content = element.selectFirst("content").text();
//        info.tag = element.selectFirst("tag").text();
//        info.width = element.selectFirst("pwidth").text();
//        info.height = element.selectFirst("pheight").text();
//        info.spic = element.selectFirst("spic").text();
//        info.pic = element.selectFirst("pic").text();
//        info.supportCount = Long.parseLong(element.selectFirst("supportcount").text());
        WallpaperInfo info = BeanUtils.createBean(element, WallpaperInfo.class);
        info.memberIcon = info.getMemberIcon().replace("https://avataro.tljpxm.comhttps://", "https://")
                .replace("https://avataro.tljpxm.comhttp://", "http://");
        String userId = UserManager.getInstance().getUserId();
        if (!TextUtils.isEmpty(userId)) {
            for (Element support : element.selectFirst("supportusers").select("supportuser")) {
                SupportUserInfo supportUserInfo = BeanUtils.createBean(support, SupportUserInfo.class);
                info.supportUserInfoList.add(supportUserInfo);
                if (!TextUtils.isEmpty(userId)
                        && TextUtils.equals(supportUserInfo.getUserId(), userId)) {
                    info.isLike = true;
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

    public int getImageCount() {
        return imageCount;
    }

    public String getSpic() {
        return spic;
    }

    public String getPic() {
        return pic;
    }

    public int getSupportCount() {
        return supportCount;
    }

    public void setSupportCount(int supportCount) {
        this.supportCount = supportCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean isLike() {
        return isLike;
    }

    public List<SupportUserInfo> getSupportUserInfoList() {
        return supportUserInfoList;
    }

    @Override
    public String toString() {
        return "WallpaperInfo{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", memberIcon='" + memberIcon + '\'' +
                ", nickName='" + nickName + '\'' +
                ", iconState='" + iconState + '\'' +
                ", memberId='" + memberId + '\'' +
                ", time='" + time + '\'' +
                ", content='" + content + '\'' +
                ", tag='" + tag + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                ", spic='" + spic + '\'' +
                ", pic='" + pic + '\'' +
                ", supportCount=" + supportCount +
                ", replyCount=" + replyCount +
                ", isLike=" + isLike +
                ", supportUserInfoList=" + supportUserInfoList +
                '}';
    }
}
