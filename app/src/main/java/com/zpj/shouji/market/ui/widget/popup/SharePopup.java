package com.zpj.shouji.market.ui.widget.popup;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.zpj.popup.core.BottomPopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.widget.input.AccountInputView;
import com.zpj.shouji.market.ui.widget.input.SubmitView;
import com.zpj.widget.editor.validator.LengthValidator;

public class SharePopup extends BottomPopup<SharePopup> implements View.OnClickListener {

    private String shareContent;

    public static SharePopup with(Context context) {
        return new SharePopup(context);
    }

    public SharePopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_popup_share;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());


        TextView tvQQ = findViewById(R.id.tv_qq);
        TextView tvWechat = findViewById(R.id.tv_wechat);
        TextView tvMoments = findViewById(R.id.tv_moments);
        TextView tvWeibo = findViewById(R.id.tv_weibo);
        TextView tvCopy = findViewById(R.id.tv_copy);
        TextView tvMore = findViewById(R.id.tv_more);

        tvQQ.setOnClickListener(this);
        tvWechat.setOnClickListener(this);
        tvMoments.setOnClickListener(this);
        tvWeibo.setOnClickListener(this);
        tvCopy.setOnClickListener(this);
        tvMore.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.tv_qq:
                AToast.normal("TODO QQ分享");
                break;
            case R.id.tv_wechat:
                AToast.normal("TODO 微信分享");
                break;
            case R.id.tv_moments:
                AToast.normal("TODO 朋友圈分享");
                break;
            case R.id.tv_weibo:
                AToast.normal("TODO 微博分享");
                break;
            case R.id.tv_copy:
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText(null, shareContent));
                AToast.success("已复制到粘贴板");
                break;
            case R.id.tv_more:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                shareIntent = Intent.createChooser(shareIntent, "手机乐园分享");
                context.startActivity(shareIntent);
                break;
        }
    }

    public SharePopup setShareContent(String shareContent) {
        this.shareContent = shareContent;
        return this;
    }
}
