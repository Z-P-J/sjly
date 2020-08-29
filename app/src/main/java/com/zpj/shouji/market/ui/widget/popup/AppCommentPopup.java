package com.zpj.shouji.market.ui.widget.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.zpj.http.core.IHttp;
import com.zpj.popup.util.KeyboardUtils;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.CommentApi;
import com.zpj.shouji.market.event.ShowLoadingEvent;

public class AppCommentPopup extends CommentPopup {

    private String appId;
    private String appType;
    private String appPackage;

    public static AppCommentPopup with(Context context, String appId, String appType, String appPackage, Runnable successRunnable) {
        AppCommentPopup popup = new AppCommentPopup(context);
        popup.setAppId(appId);
        popup.setAppType(appType);
        popup.setAppPackage(appPackage);
        popup.successRunnable = successRunnable;
        return popup;
    }

    public AppCommentPopup(@NonNull Context context) {
        super(context);
        setReplyId("0");
        setContentType("app_comment");
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("评论");
        replyPanel.getEditor().setHint("评论一下吧...");
        replyPanel.removeAppAction();
    }

    @Override
    public void sendText(String content) {
        KeyboardUtils.hideSoftInput(replyPanel.getEditor());
        Log.d("sendText", "content=" + content + " appId=" + appId + " appType=" + appType + " appPackage=" + appPackage);
//        ShowLoadingEvent.post("评论中...");
//        if (replyPanel.getImgList().isEmpty()) {
//            CommentApi.appCommentApi(content, "0", appId, appType, appPackage)
//                    .onSuccess(this)
//                    .onError(this)
//                    .subscribe();
//        } else {
//
//        }

        CommentApi.appCommentWithFileApi(
                context,
                content,
                "0",
                appId,
                appType,
                appPackage,
                replyPanel.getImgList(),
                this::dismiss,
                new IHttp.OnStreamWriteListener() {
                    @Override
                    public void onBytesWritten(int bytesWritten) {

                    }

                    @Override
                    public boolean shouldContinue() {
                        return true;
                    }
                }
        );

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
