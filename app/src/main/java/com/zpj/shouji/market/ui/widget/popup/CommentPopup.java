package com.zpj.shouji.market.ui.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.felix.atoast.library.AToast;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.popup.animator.EmptyAnimator;
import com.zpj.popup.animator.PopupAnimator;
import com.zpj.popup.core.BottomPopup;
import com.zpj.popup.util.ActivityUtils;
import com.zpj.popup.util.KeyboardUtils;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.RefreshEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.ui.widget.ChatPanel;

public class CommentPopup extends BottomPopup<CommentPopup>
        implements ChatPanel.OnOperationListener,
        IHttp.OnSuccessListener<Document>,
        IHttp.OnErrorListener {

    protected ChatPanel chatPanel;

    protected String replyId;
    protected String contentType;

    public static CommentPopup with(Context context, String replyId, String contentType) {
        CommentPopup popup = new CommentPopup(context);
        popup.setReplyId(replyId);
        popup.setContentType(contentType);
        return popup;
    }

    public CommentPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_popup_comment;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        Activity activity = ActivityUtils.getActivity(context);
        hasMoveUp = true;

        chatPanel = findViewById(R.id.chat_panel);
        chatPanel.setOnOperationListener(this);

        com.zpj.popup.util.KeyboardUtils.removeLayoutChangeListener(popupInfo.decorView, this);
        com.zpj.popup.util.KeyboardUtils.registerSoftInputChangedListener(activity, this, height -> {
            getPopupImplView().setTranslationY(0);
            chatPanel.onKeyboardHeightChanged(height, 0);
        });
    }

    @Override
    protected void onShow() {
        super.onShow();
        if (TextUtils.isEmpty(replyId) || TextUtils.isEmpty(contentType)) {
            AToast.warning("出错了");
            dismiss();
        } else {
            KeyboardUtils.showSoftInput(chatPanel.getEditor());
        }
    }

    @Override
    public void dismiss() {
        KeyboardUtils.hideSoftInput(chatPanel.getEditor());
        super.dismiss();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        KeyboardUtils.hideSoftInput(chatPanel.getEditor());
        chatPanel.setOnOperationListener(null);
    }

    @Override
    public void sendText(String content) {
        KeyboardUtils.hideSoftInput(chatPanel.getEditor());
        Log.d("sendText", "content=" + content + " replyId=" + replyId + " contentType=" + contentType);
        ShowLoadingEvent.post("评论中...");
        if ("discuss".equals(contentType)) {
            HttpApi.discussCommentApi(replyId, content)
                    .onSuccess(this)
                    .onError(this)
                    .subscribe();
        } else if ("review".equals(contentType)) {
            HttpApi.commentApi(replyId, content)
                    .onSuccess(this)
                    .onError(this)
                    .subscribe();
        }

    }

    @Override
    public void onEmojiSelected(String key) {

    }

    @Override
    public void onStickerSelected(String categoryName, String stickerName, String stickerBitmapPath) {

    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    protected PopupAnimator genAnimatorByPopupType() {
        return new EmptyAnimator();
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return new EmptyAnimator();
    }

    @Override
    public void onError(Throwable throwable) {
        AToast.error(throwable.getMessage());
        HideLoadingEvent.postEvent();
    }

    @Override
    public void onSuccess(Document data) throws Exception {
        String info = data.selectFirst("info").text();
        if ("success".equals(data.selectFirst("result").text())) {
            AToast.success(info);
            RefreshEvent.postEvent();
            dismiss();
        } else {
            AToast.error(info);
        }
        HideLoadingEvent.postEvent();
    }
}
