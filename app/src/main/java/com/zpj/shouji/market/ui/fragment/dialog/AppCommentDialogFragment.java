package com.zpj.shouji.market.ui.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zpj.http.core.IHttp;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.CommentApi;

public class AppCommentDialogFragment extends CommentDialogFragment {

    private String appId;
    private String appType;
    private String appPackage;

    public static AppCommentDialogFragment with(Context context, String appId, String appType, String appPackage, Runnable successRunnable) {
        AppCommentDialogFragment popup = new AppCommentDialogFragment();
        popup.setAppId(appId);
        popup.setAppType(appType);
        popup.setAppPackage(appPackage);
        popup.successRunnable = successRunnable;
        return popup;
    }

    public AppCommentDialogFragment() {
        super();
        setReplyId("0");
        setContentType("app_comment");
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("评论");
        replyPanel.getEditor().setHint("评论一下吧...");
        replyPanel.removeAppAction();
    }

    @Override
    public void sendText(String content) {
        hideSoftInput();
        Log.d("sendText", "content=" + content + " appId=" + appId + " appType=" + appType + " appPackage=" + appPackage);
        CommentApi.appCommentWithFileApi(
                context,
                content,
                "0",
                appId,
                appType,
                appPackage,
                replyPanel.getImgList(),
                () -> {
                    if (successRunnable != null) {
                        successRunnable.run();
                    }
                    dismiss();
                },
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
