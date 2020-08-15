package com.zpj.shouji.market.model;

import android.support.annotation.Keep;

import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.event.BaseEvent;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.BeanUtils.Select;

import java.util.ArrayList;
import java.util.List;

@Keep
public class PrivateLetterInfo extends BaseEvent {

    private String id;
    private String avatar;
    @Select(selector = "iconstate")
    private String iconState;
    @Select(selector = "nickname")
    private String nikeName;
    private boolean online;
    private String title;
    private String sender;
    @Select(selector = "sendid")
    private String sendId;
    private String time;
    @Select(selector = "read")
    private boolean isRead;
    private String content;
    private final List<String> pics = new ArrayList<>(0);
    private final List<String> spics = new ArrayList<>(0);
    private final List<String> sizes = new ArrayList<>(0);


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIconState() {
        return iconState;
    }

    public void setIconState(String iconState) {
        this.iconState = iconState;
    }

    public String getNikeName() {
        return nikeName;
    }

    public void setNikeName(String nikeName) {
        this.nikeName = nikeName;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String senderId) {
        this.sendId = senderId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getSpics() {
        return spics;
    }

    public void addSpic(String spic) {
        this.spics.add(spic);
    }

    public List<String> getPics() {
        return pics;
    }

    public void addPic(String pic) {
        this.pics.add(pic);
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void addSize(String size) {
        this.sizes.add(size);
    }

    @Override
    public String toString() {
        return "PrivateLetterInfo{" +
                "id='" + id + '\'' +
                ", avatar='" + avatar + '\'' +
                ", iconState='" + iconState + '\'' +
                ", nikeName='" + nikeName + '\'' +
                ", online=" + online +
                ", title='" + title + '\'' +
                ", sender='" + sender + '\'' +
                ", sendId='" + sendId + '\'' +
                ", time='" + time + '\'' +
                ", isRead=" + isRead +
                ", content='" + content + '\'' +
                '}';
    }
}
