package com.lxj.xpopup.impl;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.zpj.popupmenuview.R;
import com.zpj.utils.ScreenUtils;

public class AlertPopup extends CenterPopupView implements View.OnClickListener {

    OnCancelListener cancelListener;
    OnConfirmListener confirmListener;
    TextView tv_title, tv_cancel, tv_confirm;
    String title, cancelText, confirmText;
    boolean isHideCancel = false;

    private View contentView;

    protected OnViewCreateListener onViewCreateListener;

    public AlertPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout._xpopup_center_impl_alert;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        FrameLayout flContent = findViewById(R.id.fl_content);
        tv_title = findViewById(R.id.tv_title);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_confirm = findViewById(R.id.tv_confirm);

        if (contentView != null) {
            flContent.addView(contentView);
            if (onViewCreateListener != null) {
                onViewCreateListener.onViewCreate(this, contentView);
            }
        }

        applyPrimaryColor();

        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);

        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        } else {
            tv_title.setVisibility(GONE);
        }

        if (!TextUtils.isEmpty(cancelText)) {
            tv_cancel.setText(cancelText);
        }
        if (!TextUtils.isEmpty(confirmText)) {
            tv_confirm.setText(confirmText);
        }
        if (isHideCancel) tv_cancel.setVisibility(GONE);
    }

    protected void applyPrimaryColor() {
        tv_cancel.setTextColor(XPopup.getPrimaryColor());
        tv_confirm.setTextColor(XPopup.getPrimaryColor());
    }

    public AlertPopup setConfirmButton(OnConfirmListener listener) {
        this.confirmListener = listener;
        return this;
    }

    public AlertPopup setConfirmButton(String btnStr, OnConfirmListener listener) {
        this.confirmText = btnStr;
        this.confirmListener = listener;
        return this;
    }

    public AlertPopup onCancel(OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }

    public AlertPopup setListener(OnConfirmListener confirmListener, OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        this.confirmListener = confirmListener;
        return this;
    }

    public AlertPopup setContent(String content) {
        TextView textView = new TextView(context);
        textView.setText(content);
        textView.setTextColor(context.getResources().getColor(R.color._xpopup_content_color));
        textView.setTextSize(16);
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int padding = ScreenUtils.dp2pxInt(context, 16);
                textView.setPadding(padding, padding, padding, padding);
            }
        });
        this.contentView = textView;
        return this;
    }

    public AlertPopup setContent(@LayoutRes int resId) {
        this.contentView = LayoutInflater.from(context).inflate(resId, null, false);
        return this;
    }

    public AlertPopup setContent(View view) {
        this.contentView = view;
        return this;
    }

    public AlertPopup setTitle(String title) {
        this.title = title;
        return this;
    }

    public AlertPopup setCancelText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    public AlertPopup setConfirmText(String confirmText) {
        this.confirmText = confirmText;
        return this;
    }

    public AlertPopup hideCancelBtn() {
        isHideCancel = true;
        return this;
    }

    public AlertPopup onViewCreate(OnViewCreateListener onViewCreateListener) {
        this.onViewCreateListener = onViewCreateListener;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v == tv_cancel) {
            if (cancelListener != null) cancelListener.onCancel();
            dismiss();
        } else if (v == tv_confirm) {
            if (confirmListener != null) confirmListener.onConfirm();
            if (popupInfo.autoDismiss) dismiss();
        }
    }

    public interface OnViewCreateListener {
        void onViewCreate(BasePopupView popup, View view);
    }
}
