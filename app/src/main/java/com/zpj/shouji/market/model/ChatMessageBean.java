package com.zpj.shouji.market.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.zpj.shouji.market.database.ChatManager;
import com.zpj.shouji.market.ui.fragment.chat.ChatConst;

@Table(database = ChatManager.class)
public class ChatMessageBean extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private long id;
    @Column
    private String UserId;
    @Column
    private String UserName;
    @Column
    private String UserHeadIcon;
    @Column
    private String UserContent;
    @Column
    private String time;
    @Column
    private int type;
    @Column
    private int messagetype;
    @Column
    private float UserVoiceTime;
    @Column
    private String UserVoicePath;
    @Column
    private String UserVoiceUrl;
    @Column
    private @ChatConst.SendState int sendState;
    private String imageUrl;
    @Column
    private String imageIconUrl;
    @Column
    private String imageLocal;

    public static ChatMessageBean build(long id, String UserId, String UserName,
                                        String UserHeadIcon, String UserContent, String time, int type,
                                        int messagetype, float UserVoiceTime, String UserVoicePath,
                                        String UserVoiceUrl, int sendState, String imageUrl,
                                        String imageIconUrl, String imageLocal) {
        ChatMessageBean bean = new ChatMessageBean();
        bean.id = id;
        bean.UserId = UserId;
        bean.UserName = UserName;
        bean.UserHeadIcon = UserHeadIcon;
        bean.UserContent = UserContent;
        bean.time = time;
        bean.type = type;
        bean.messagetype = messagetype;
        bean.UserVoiceTime = UserVoiceTime;
        bean.UserVoicePath = UserVoicePath;
        bean.UserVoiceUrl = UserVoiceUrl;
        bean.sendState = sendState;
        bean.imageUrl = imageUrl;
        bean.imageIconUrl = imageIconUrl;
        bean.imageLocal = imageLocal;
        return bean;
    }

//
//    public ChatMessageBean(long id, String UserId, String UserName,
//                           String UserHeadIcon, String UserContent, String time, int type,
//                           int messagetype, float UserVoiceTime, String UserVoicePath,
//                           String UserVoiceUrl, int sendState, String imageUrl,
//                           String imageIconUrl, String imageLocal) {
//        this.id = id;
//        this.UserId = UserId;
//        this.UserName = UserName;
//        this.UserHeadIcon = UserHeadIcon;
//        this.UserContent = UserContent;
//        this.time = time;
//        this.type = type;
//        this.messagetype = messagetype;
//        this.UserVoiceTime = UserVoiceTime;
//        this.UserVoicePath = UserVoicePath;
//        this.UserVoiceUrl = UserVoiceUrl;
//        this.sendState = sendState;
//        this.imageUrl = imageUrl;
//        this.imageIconUrl = imageIconUrl;
//        this.imageLocal = imageLocal;
//    }
//
//    public ChatMessageBean() {
//    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getUserName() {
        return this.UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getUserHeadIcon() {
        return this.UserHeadIcon;
    }

    public void setUserHeadIcon(String UserHeadIcon) {
        this.UserHeadIcon = UserHeadIcon;
    }

    public String getUserContent() {
        return this.UserContent;
    }

    public void setUserContent(String UserContent) {
        this.UserContent = UserContent;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMessagetype() {
        return this.messagetype;
    }

    public void setMessagetype(int messagetype) {
        this.messagetype = messagetype;
    }

    public float getUserVoiceTime() {
        return this.UserVoiceTime;
    }

    public void setUserVoiceTime(float UserVoiceTime) {
        this.UserVoiceTime = UserVoiceTime;
    }

    public String getUserVoicePath() {
        return this.UserVoicePath;
    }

    public void setUserVoicePath(String UserVoicePath) {
        this.UserVoicePath = UserVoicePath;
    }

    public String getUserVoiceUrl() {
        return this.UserVoiceUrl;
    }

    public void setUserVoiceUrl(String UserVoiceUrl) {
        this.UserVoiceUrl = UserVoiceUrl;
    }

    public int getSendState() {
        return this.sendState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageIconUrl() {
        return this.imageIconUrl;
    }

    public void setImageIconUrl(String imageIconUrl) {
        this.imageIconUrl = imageIconUrl;
    }

    public String getImageLocal() {
        return this.imageLocal;
    }

    public void setImageLocal(String imageLocal) {
        this.imageLocal = imageLocal;
    }
}
