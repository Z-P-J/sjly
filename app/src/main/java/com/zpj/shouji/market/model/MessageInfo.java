package com.zpj.shouji.market.model;

import android.support.annotation.Keep;

import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.BeanUtils.Select;

@Keep
public class MessageInfo {

    // 评论数量
    @Select(selector = "message")
    private int messageCount;
    // @数量
    @Select(selector = "aite")
    private int aiteCount;
    // 发现数量
    @Select(selector = "faxian")
    private int discoverCount;
    // 私信数量
    @Select(selector = "private")
    private int privateLetterCount;
    // 点赞数量
    @Select(selector = "flower")
    private int likeCount;
    // 粉丝数量
    @Select(selector = "fensi")
    private int fanCount;
    @Select(selector = "haoyoucontent")
    private int friendMsgCount;

    public static MessageInfo from(Document doc) {
        if ("success".equals(doc.selectFirst("result").text())) {
//            MessageInfo info = new MessageInfo();
//            info.setMessageCount(Integer.parseInt(doc.selectFirst("message").text()));
//            info.setAiteCount(Integer.parseInt(doc.selectFirst("aite").text()));
//            info.setDiscoverCount(Integer.parseInt(doc.selectFirst("faxian").text()));
//            info.setPrivateLetterCount(Integer.parseInt(doc.selectFirst("private").text()));
//            info.setLikeCount(Integer.parseInt(doc.selectFirst("flower").text()));
//            info.setFanCount(Integer.parseInt(doc.selectFirst("fensi").text()));
//            info.setFriendMsgCount(Integer.parseInt(doc.selectFirst("haoyoucontent").text()));
            return BeanUtils.createBean(doc, MessageInfo.class);
        }
        return new MessageInfo();
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

    public int getPrivateLetterCount() {
        return privateLetterCount;
    }

    public void setPrivateLetterCount(int privateLetterCount) {
        this.privateLetterCount = privateLetterCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
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
        return messageCount + aiteCount + discoverCount + privateLetterCount + likeCount + friendMsgCount;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "messageCount=" + messageCount +
                ", aiteCount=" + aiteCount +
                ", discoverCount=" + discoverCount +
                ", chatCount=" + privateLetterCount +
                ", followerCount=" + likeCount +
                ", friendMsgCount=" + friendMsgCount +
                '}';
    }
}
