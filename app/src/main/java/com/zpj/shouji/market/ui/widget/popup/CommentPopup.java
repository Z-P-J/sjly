package com.zpj.shouji.market.ui.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import com.zpj.shouji.market.ui.fragment.profile.UserPickerFragment;
import com.zpj.shouji.market.ui.widget.MaxHeightLayout;
import com.zpj.shouji.market.ui.widget.ReplyPanel;
import com.zpj.utils.ScreenUtils;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class CommentPopup extends BottomPopup<CommentPopup>
        implements ReplyPanel.OnOperationListener,
        IHttp.OnSuccessListener<Document>,
        IHttp.OnErrorListener {

    protected ReplyPanel replyPanel;
    protected MaxHeightLayout maxHeightLayout;

    protected String replyId;
    protected String replyUser;
    protected String contentType;

    public static CommentPopup with(Context context, String replyId, String replyUser, String contentType) {
        CommentPopup popup = new CommentPopup(context);
        popup.setReplyId(replyId);
        popup.setReplyUser(replyUser);
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

        replyPanel = findViewById(R.id.panel_reply);
        replyPanel.setOnOperationListener(this);
        replyPanel.getEditor().setMinLines(6);
        replyPanel.getEditor().setMaxLines(36);

        replyPanel.addAction(R.drawable.ic_at_black_24dp, v -> {
            KeyboardUtils.hideSoftInput(replyPanel.getEditor());
            UserPickerFragment.start(content -> {
                replyPanel.getEditor().append(content);
                KeyboardUtils.showSoftInput(replyPanel.getEditor());
            });
        });

        if (!TextUtils.isEmpty(replyUser)) {
            replyPanel.getEditor().setHint("回复：" + replyUser);
        }

        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());
        maxHeightLayout = findViewById(R.id.layout_max_height);
//        LinearLayout llContent = findViewById(R.id.ll_scroll_content);

        com.zpj.popup.util.KeyboardUtils.removeLayoutChangeListener(popupInfo.decorView, this);
        com.zpj.popup.util.KeyboardUtils.registerSoftInputChangedListener(activity, this, height -> {
            getPopupImplView().setTranslationY(0);
            Log.d("CommentPopup", "height=" + height);
//            if (height > 0) {
//
//            }
            int temp = height;

            height = ScreenUtils.getScreenHeight(context) - height - ScreenUtils.getStatusBarHeight(context);
            height -= findViewById(R.id.ll_top).getMeasuredHeight();
            height -= findViewById(R.id.rl_actions).getMeasuredHeight();
            int maxHeight = height;
            maxHeightLayout.post(() -> maxHeightLayout.setMaxHeight(maxHeight));
            int contentHeight = replyPanel.getEditor().getMeasuredHeight() + findViewById(R.id.rv_img).getMeasuredHeight();
            Log.d("CommentPopup", "maxHeight=" + maxHeight + " contentHeight=" + contentHeight);
//            ViewGroup.LayoutParams params = maxHeightLayout.getLayoutParams();
//            if (contentHeight <= maxHeight) {
//                params.height = contentHeight;
//            } else {
//                params.height = WRAP_CONTENT;
//            }

            replyPanel.onKeyboardHeightChanged(temp, 0);

        });
    }

    @Override
    protected void onShow() {
        super.onShow();
        if (TextUtils.isEmpty(replyId) || TextUtils.isEmpty(contentType)) {
            AToast.warning("出错了");
            dismiss();
        } else {
            KeyboardUtils.showSoftInput(replyPanel.getEditor());
        }
    }

    @Override
    public void dismiss() {
        KeyboardUtils.hideSoftInput(replyPanel.getEditor());
        super.dismiss();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        KeyboardUtils.hideSoftInput(replyPanel.getEditor());
        replyPanel.setOnOperationListener(null);
    }

    @Override
    public void sendText(String content) {
        KeyboardUtils.hideSoftInput(replyPanel.getEditor());
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

    public void setReplyUser(String replyUser) {
        this.replyUser = replyUser;
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
