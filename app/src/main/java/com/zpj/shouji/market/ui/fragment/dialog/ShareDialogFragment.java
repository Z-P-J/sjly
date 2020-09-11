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

import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.dialog.base.BottomDialogFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.ShareUtils;

import java.io.File;

public class ShareDialogFragment extends BottomDialogFragment
        implements View.OnClickListener {

    private String shareContent;
    private File shareFile;

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_popup_share;
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
                AToast.error("出错了！获取分享内容失败！");
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
//                AToast.normal("TODO QQ分享");
                if (shareFile == null) {
                    ShareUtils.shareTextToQQFriend(context, shareContent);
                } else {
                    ShareUtils.sharePictureToQQFriend(context, shareFile);
                }
                break;
            case R.id.tv_wechat:
//                AToast.normal("TODO 微信分享");
                if (shareFile == null) {
                    ShareUtils.shareTextToWechatFriend(context, shareContent);
                } else {
                    ShareUtils.sharePictureToWechatFriend(context, shareFile);
                }
                break;
            case R.id.tv_moments:
//                AToast.normal("TODO 朋友圈分享");
                if (shareFile == null) {
                    ShareUtils.shareTextToTimeLine(context, shareContent);
                } else {
                    ShareUtils.sharePictureToTimeLine(context, shareFile);
                }
                break;
            case R.id.tv_weibo:
//                AToast.normal("TODO 微博分享");
                if (shareFile == null) {
                    ShareUtils.shareTextToSina(context, shareContent);
                } else {
                    ShareUtils.sharePictureToSina(context, shareFile);
                }
                break;
            case R.id.tv_copy:
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText(null, shareContent));
                AToast.success("已复制到粘贴板");
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

    public ShareDialogFragment setShareContent(String shareContent) {
        this.shareContent = shareContent;
        return this;
    }

    public ShareDialogFragment setShareFile(File shareFile) {
        this.shareFile = shareFile;
        return this;
    }

}
