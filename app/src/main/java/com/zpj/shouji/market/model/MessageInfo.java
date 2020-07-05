package com.zpj.shouji.market.model;

import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.event.BaseEvent;

public class MessageInfo extends BaseEvent {

    // 评论数量
    private int messageCount;
    // @数量
    private int aiteCount;
    // 发现数量
    private int discoverCount;
    // 私信数量
    private int chatCount;
    // 点赞数量
    private int followerCount;
    // 粉丝数量
    private int fanCount;
    private int friendMsgCount;

    public static MessageInfo from(Document doc) {
        MessageInfo info = new MessageInfo();
        if ("success".equals(doc.selectFirst("result").text())) {
            info.setMessageCount(Integer.parseInt(doc.selectFirst("message").text()));
            info.setAiteCount(Integer.parseInt(doc.selectFirst("aite").text()));
            info.setDiscoverCount(Integer.parseInt(doc.selectFirst("faxian").text()));
            info.setChatCount(Integer.parseInt(doc.selectFirst("private").text()));
            info.setFollowerCount(Integer.parseInt(doc.selectFirst("flower").text()));
            info.setFanCount(Integer.parseInt(doc.selectFirst("fensi").text()));
            info.setFriendMsgCount(Integer.parseInt(doc.selectFirst("haoyoucontent").text()));
        }
        return info;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public int getAiteCount() {
        return aiteCount;
    }

    public void setAiteCount(int aiteCount) {
        this.aiteCount = aiteCount;
    }

    public int getDiscoverCount() {
        return discoverCount;
    }

    public void setDiscoverCount(int discoverCount) {
        this.discoverCount = discoverCount;
    }

    public int getChatCount() {
        return chatCount;
    }

    public void setChatCount(int chatCount) {
        this.chatCount = chatCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFanCount() {
        return fanCount;
    }

    public void setFanCount(int fanCount) {
        this.fanCount = fanCount;
    }

    public int getFriendMsgCount() {
        return friendMsgCount;
    }

    public void setFriendMsgCount(int friendMsgCount) {
        this.friendMsgCount = friendMsgCount;
    }

    public int getTotalCount() {
        return messageCount + aiteCount + discoverCount + chatCount + followerCount + friendMsgCount;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "messageCount=" + messageCount +
                ", aiteCount=" + aiteCount +
                ", discoverCount=" + discoverCount +
                ", chatCount=" + chatCount +
                ", followerCount=" + followerCount +
                ", friendMsgCount=" + friendMsgCount +
                '}';
    }
}
