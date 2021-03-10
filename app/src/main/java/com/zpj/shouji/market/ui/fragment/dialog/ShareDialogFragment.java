package com.zpj.shouji.market.ui.fragment.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.base.BottomDragDialogFragment;
import com.zpj.shouji.market.R;
import com.zpj.toast.ZToast;
import com.zpj.utils.Callback;
import com.zpj.utils.ShareUtils;

import java.io.File;

public class ShareDialogFragment extends BottomDragDialogFragment
        implements View.OnClickListener, Callback<String> {

    private String shareContent;
    private File shareFile;

    @Override
    protected int getContentLayoutId() {
        return R.layout.dialog_fragment_share;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());

        TextView tvTitle = findViewById(R.id.tv_title);

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

        if (TextUtils.isEmpty(shareContent)) {
            if (shareFile == null) {
                ZToast.error("出错了！获取分享内容失败！");
                dismiss();
            } else {
                tvCopy.setText("复制路径");
                tvTitle.setText("分享文件至");
                shareContent = shareFile.getAbsolutePath();
            }
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.tv_qq:
//                ZToast.normal("TODO QQ分享");
                if (shareFile == null) {
                    ShareUtils.shareTextToQQFriend(context, shareContent, this);
                } else {
                    ShareUtils.sharePictureToQQFriend(context, shareFile, this);
                }
                break;
            case R.id.tv_wechat:
//                ZToast.normal("TODO 微信分享");
                if (shareFile == null) {
                    ShareUtils.shareTextToWechatFriend(context, shareContent, this);
                } else {
                    ShareUtils.sharePictureToWechatFriend(context, shareFile, this);
                }
                break;
            case R.id.tv_moments:
//                ZToast.normal("TODO 朋友圈分享");
                if (shareFile == null) {
                    ShareUtils.shareTextToTimeLine(context, shareContent, this);
                } else {
                    ShareUtils.sharePictureToTimeLine(context, shareFile, this);
                }
                break;
            case R.id.tv_weibo:
//                ZToast.normal("TODO 微博分享");
                if (shareFile == null) {
                    ShareUtils.shareTextToSina(context, shareContent, this);
                } else {
                    ShareUtils.sharePictureToSina(context, shareFile, this);
                }
                break;
            case R.id.tv_copy:
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText(null, shareContent));
                ZToast.success("已复制到粘贴板");
                break;
            case R.id.tv_more:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                if (shareFile == null) {
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                } else {
                    Uri imageUri = Uri.fromFile(shareFile);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.setType("image/*");
                }
                context.startActivity(Intent.createChooser(shareIntent, "分享至"));
                break;
        }
    }

    @Override
    public void onCallback(String msg) {
        ZToast.warning(msg);
    }

    public ShareDialogFragment setShareContent(String shareContent) {
        this.shareContent = shareContent;
        return this;
    }

    public ShareDialogFragment setShareFile(File shareFile) {
        this.shareFile = shareFile;
        return this;
    }

}
