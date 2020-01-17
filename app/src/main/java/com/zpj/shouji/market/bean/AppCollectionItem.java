package com.zpj.shouji.market.bean;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppCollectionItem {

    public class SupportUser {

        private String id;
        private String nickName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }

    private String id;
    private String parent;
    private String contentType;
    private String type;
    private List<String> icons = new ArrayList<>(0);
    private String title;
    private String comment;
    private int size;
    private int appSize;
    private String memberId;
    private String nickName;
    private int viewCount;
    private int favCount;
    private int supportCount;
    private String time;
    private int replyCount;
    private List<SupportUser> supportUserList = new ArrayList<>(0);
    private SparseArray<Bitmap> bitmapSparseArray = new SparseArray<>(0);
    private SparseArray<Drawable> drawableSparseArray = new SparseArray<>(0);

    public static AppCollectionItem create(Element item) {
        AppCollectionItem collectionItem = new AppCollectionItem();
        collectionItem.setId(item.select("id").text());
        collectionItem.setParent(item.select("parent").text());
        collectionItem.setContentType(item.select("contenttype").text());
        collectionItem.setType(item.select("type").text());
        collectionItem.setTitle(item.select("title").text());
        collectionItem.setComment(item.select("comment").text());
        collectionItem.setSize(Integer.valueOf(item.select("size").text()));
        collectionItem.setMemberId(item.select("memberid").text());
        collectionItem.setNickName(item.select("nickname").text());
        collectionItem.setFavCount(Integer.valueOf(item.select("favcount").text()));
        collectionItem.setAppSize(Integer.valueOf(item.select("appsize").text()));
        collectionItem.setSupportCount(Integer.valueOf(item.selectFirst("supportcount").text()));
        collectionItem.setViewCount(Integer.valueOf(item.select("viewcount").text()));
        collectionItem.setReplyCount(Integer.valueOf(item.select("replycount").text()));
        collectionItem.setTime(item.select("time").text());
//                    collectionItem.setSupportCount(Integer.valueOf(item.select("supportcount").text()));
//                    collectionItem.setSupportCount(Integer.valueOf(item.select("supportcount").text()));
        Elements icons = item.selectFirst("icons").select("icon");
        for (Element element : icons) {
            collectionItem.addIcon(element.text());
        }
        return collectionItem;
    }

    public static AppCollectionItem buildSimilarCollection(Element item) {
        AppCollectionItem collectionItem = new AppCollectionItem();
        collectionItem.setId(item.select("yyjid").text());
        collectionItem.setParent(item.select("yyjparent").text());
        collectionItem.setContentType(item.select("yyjcontenttype").text());
//        collectionItem.setType(item.select("type").text());
        collectionItem.setTitle(item.select("yyjtitle").text());
        collectionItem.setComment(item.select("yyjcomment").text());
        collectionItem.setSize(Integer.valueOf(item.select("yyjsize").text()));
        collectionItem.setMemberId(item.select("yyjmemberid").text());
        collectionItem.setNickName(item.select("yyjnickname").text());
        collectionItem.setFavCount(Integer.valueOf(item.select("yyjfavcount").text()));
        collectionItem.setAppSize(Integer.valueOf(item.select("yyjappsize").text()));
        collectionItem.setSupportCount(Integer.valueOf(item.selectFirst("yyjsupportcount").text()));
        collectionItem.setViewCount(Integer.valueOf(item.select("yyjviewcount").text()));
        collectionItem.setReplyCount(Integer.valueOf(item.select("yyjreplycount").text()));
        collectionItem.setTime(item.select("yyjtime").text());
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

    public List<SupportUser> getSupportUserList() {
        return supportUserList;
    }

    public void addIcon(String icon) {
        this.icons.add(icon);
    }

    public void addSupportUserList(SupportUser supportUser) {
        this.supportUserList.add(supportUser);
    }

    public void putBitmap(int key, Bitmap bitmap) {
        this.bitmapSparseArray.put(key, bitmap);
    }

    public Bitmap getBitmap(int key) {
        return this.bitmapSparseArray.get(key);
    }

    public void putDrawable(int key, Drawable drawable) {
        this.drawableSparseArray.put(key, drawable);
    }

    public Drawable getDrawable(int key) {
        return this.drawableSparseArray.get(key);
    }
}
