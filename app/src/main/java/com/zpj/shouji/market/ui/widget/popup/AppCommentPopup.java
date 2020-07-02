package com.zpj.shouji.market.ui.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.felix.atoast.library.AToast;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.popup.core.BottomPopup;
import com.zpj.popup.util.ActivityUtils;
import com.zpj.popup.util.KeyboardUtils;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.RefreshEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.ui.fragment.detail.AppCommentFragment;
import com.zpj.shouji.market.ui.widget.ChatPanel;

public class AppCommentPopup extends CommentPopup {

    private String appId;
    private String appType;
    private String appPackage;

    public static AppCommentPopup with(Context context, String appId, String appType, String appPackage) {
        AppCommentPopup popup = new AppCommentPopup(context);
        popup.setAppId(appId);
        popup.setAppType(appType);
        popup.setAppPackage(appPackage);
        return popup;
    }

    public AppCommentPopup(@NonNull Context context) {
        super(context);
        setReplyId("0");
        setContentType("app_comment");
    }


    @Override
    public void sendText(String content) {
        KeyboardUtils.hideSoftInput(chatPanel.getEditor());
        Log.d("sendText", "content=" + content + " appId=" + appId + " appType=" + appType + " appPackage=" + appPackage);
        ShowLoadingEvent.post("评论中...");
        HttpApi.appCommentApi(content, appId, appType, appPackage)
                .onSuccess(this)
                .onError(this)
                .subscribe();

    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }
}
