package com.zpj.shouji.market.model;

import android.support.annotation.Keep;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.BeanUtils.Select;

import java.util.ArrayList;
import java.util.List;

@Keep
public class CollectionInfo {

    private String id;
    private String parent;
    @Select(selector = "contenttype")
    private String contentType;
    private String type;
    private List<String> icons = new ArrayList<>(0);
    private String title;
    private String comment;
    private int size;
    @Select(selector = "appsize")
    private int appSize;
    @Select(selector = "memberid")
    private String memberId;
    @Select(selector = "nickname")
    private String nickName;
    @Select(selector = "viewcount")
    private int viewCount;
    @Select(selector = "favcount")
    private int favCount;
    @Select(selector = "supportcount")
    private int supportCount;
    private String time;
    @Select(selector = "replycount")
    private int replyCount;
//    private List<SupportUserInfo> supportUserList = new ArrayList<>(0);

    public static CollectionInfo create(Element item) {
//        CollectionInfo collectionItem = new CollectionInfo();
//        collectionItem.setId(item.selectFirst("id").text());
//        collectionItem.setParent(item.selectFirst("parent").text());
//        collectionItem.setContentType(item.selectFirst("contenttype").text());
//        collectionItem.setType(item.selectFirst("type").text());
//        collectionItem.setTitle(item.selectFirst("title").text());
//        collectionItem.setComment(item.selectFirst("comment").text());
//        collectionItem.setSize(Integer.parseInt(item.selectFirst("size").text()));
//        collectionItem.setMemberId(item.selectFirst("memberid").text());
//        collectionItem.setNickName(item.selectFirst("nickname").text());
//        collectionItem.setFavCount(Integer.parseInt(item.selectFirst("favcount").text()));
//        collectionItem.setAppSize(Integer.parseInt(item.selectFirst("appsize").text()));
//        collectionItem.setSupportCount(Integer.parseInt(item.selectFirst("supportcount").text()));
//        collectionItem.setViewCount(Integer.parseInt(item.selectFirst("viewcount").text()));
//        collectionItem.setReplyCount(Integer.parseInt(item.selectFirst("replycount").text()));
//        collectionItem.setTime(item.selectFirst("time").text());

        CollectionInfo collectionItem = BeanUtils.createBean(item, CollectionInfo.class);
        if (collectionItem == null) {
            return null;
        }
//                    collectionItem.setSupportCount(Integer.valueOf(item.select("supportcount").text()));
//                    collectionItem.setSupportCount(Integer.valueOf(item.select("supportcount").text()));
        Elements icons = item.selectFirst("icons").select("icon");
        for (Element element : icons) {
            collectionItem.addIcon(element.text());
        }
        return collectionItem;
    }

    public static CollectionInfo buildSimilarCollection(Element item) {
        CollectionInfo collectionItem = new CollectionInfo();
        collectionItem.setId(item.selectFirst("yyjid").text());
        collectionItem.setParent(item.selectFirst("yyjparent").text());
        collectionItem.setContentType(item.selectFirst("yyjcontenttype").text());
//        collectionItem.setType(item.select("type").text());
        collectionItem.setTitle(item.selectFirst("yyjtitle").text());
        collectionItem.setComment(item.selectFirst("yyjcomment").text());
        collectionItem.setSize(Integer.parseInt(item.selectFirst("yyjsize").text()));
        collectionItem.setMemberId(item.selectFirst("yyjmemberid").text());
        collectionItem.setNickName(item.selectFirst("yyjnickname").text());
        collectionItem.setFavCount(Integer.parseInt(item.selectFirst("yyjfavcount").text()));
        collectionItem.setAppSize(Integer.parseInt(item.selectFirst("yyjappsize").text()));
        collectionItem.setSupportCount(Integer.parseInt(item.selectFirst("yyjsupportcount").text()));
        collectionItem.setViewCount(Integer.parseInt(item.selectFirst("yyjviewcount").text()));
        collectionItem.setReplyCount(Integer.parseInt(item.selectFirst("yyjreplycount").text()));
        collectionItem.setTime(item.selectFirst("yyjtime").text());
        Elements icons = item.selectFirst("icons").select("yyjicon");
        for (Element element : icons) {
            collectionItem.addIcon(element.text());
        }
        return collectionItem;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getAppSize() {
        return appSize;
    }

    public void setAppSize(int appSize) {
        this.appSize = appSize;
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

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getFavCount() {
        return favCount;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
    }

    public int getSupportCount() {
        return supportCount;
    }

    public void setSupportCount(int supportCount) {
        this.supportCount = supportCount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public List<String> getIcons() {
        return icons;
    }

    public void addIcon(String icon) {
        this.icons.add(icon);
    }

//    public List<SupportUserInfo> getSupportUserList() {
//        return supportUserList;
//    }

//    public void addSupportUserList(SupportUserInfo supportUser) {
//        this.supportUserList.add(supportUser);
//    }

}
