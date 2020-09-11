package com.zpj.fragmentation.dialog.impl;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zpj.fragmentation.SupportActivity;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.fragmentation.dialog.base.CenterDialogFragment;
import com.zpj.popup.R;
import com.zpj.popup.XPopup;
import com.zpj.popup.util.XPopupUtils;

public class AlertDialogFragment extends CenterDialogFragment implements View.OnClickListener {

    protected TextView tv_title, tv_cancel, tv_confirm;
    protected String title, cancelText, confirmText;

    protected CharSequence content;

    protected View contentView;

    protected OnNegativeButtonClickListener cancelListener;
    protected OnPositiveButtonClickListener confirmListener;

    protected boolean isHideCancel = false;
    protected boolean autoDismiss = true;

    protected OnViewCreateListener onViewCreateListener;

    private Runnable runnable;

    @Override
    protected int getContentLayoutId() {
        return R.layout._dialog_layout_center_impl_alert;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        FrameLayout flContent = findViewById(R.id.fl_content);
        tv_title = findViewById(R.id.tv_title);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_confirm = findViewById(R.id.tv_confirm);


        if (contentView == null && !TextUtils.isEmpty(content)) {
            TextView textView = new TextView(context);
            textView.setText(content);
//        textView.setTextColor(context.getResources().getColor(R.color._xpopup_text_normal_color));
            textView.setTextSize(14);
            textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int padding = XPopupUtils.dp2px(context, 24);
                    textView.setPadding(padding, padding, padding, padding);
                }
            });
            textView.setMinHeight(XPopupUtils.dp2px(context, 80));
            textView.setLineSpacing(6, 1);
            this.contentView = textView;
        }

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
            tv_title.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(cancelText)) {
            tv_cancel.setText(cancelText);
        }
        if (!TextUtils.isEmpty(confirmText)) {
            tv_confirm.setText(confirmText);
        }

        if (isHideCancel) tv_cancel.setVisibility(View.GONE);

    }

    @Override
    public void onDismiss() {
        super.onDismiss();
//        if (runnable != null) {
//            runnable.run();
//        }
    }

    @Override
    public void onClick(View v) {
        if (v == tv_cancel) {
//            runnable = () -> {
//                if (cancelListener != null) cancelListener.onClick(AlertDialogFragment.this);
//            };
            if (autoDismiss) {
                dismiss();
            }
            if (cancelListener != null) cancelListener.onClick(this);

        } else if (v == tv_confirm) {
//            runnable = () -> {
//                if (confirmListener != null) confirmListener.onClick(AlertDialogFragment.this);
//            };
            if (autoDismiss) {
                dismiss();
            }
            if (confirmListener != null) confirmListener.onClick(this);

        }
    }

    protected void applyPrimaryColor() {
        tv_cancel.setTextColor(XPopup.getPrimaryColor());
        tv_confirm.setTextColor(getColorPrimary());
    }

    public int getColorPrimary(){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    public AlertDialogFragment setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
        return this;
    }

    public AlertDialogFragment setPositiveButton(OnPositiveButtonClickListener listener) {
        this.confirmListener = listener;
        return this;
    }

    public AlertDialogFragment setPositiveButton(String btnStr, OnPositiveButtonClickListener listener) {
        this.confirmText = btnStr;
        this.confirmListener = listener;
        return this;
    }

    public AlertDialogFragment setPositiveButton(int btnStrId, OnPositiveButtonClickListener listener) {
        this.confirmText = context.getString(btnStrId);
        this.confirmListener = listener;
        return this;
    }

    public AlertDialogFragment setNegativeButton(OnNegativeButtonClickListener listener) {
        this.cancelListener = listener;
        return this;
    }

    public AlertDialogFragment setNegativeButton(String btnStr, OnNegativeButtonClickListener listener) {
        this.cancelText = btnStr;
        this.cancelListener = listener;
        return this;
    }

    public AlertDialogFragment setNegativeButton(int btnStrId, OnNegativeButtonClickListener listener) {
        this.cancelText = context.getString(btnStrId);
        this.cancelListener = listener;
        return this;
    }

    public AlertDialogFragment setContent(CharSequence content) {
        this.content = content;
        return this;
    }

    public AlertDialogFragment setContent(@LayoutRes int resId) {
        this.contentView = LayoutInflater.from(context).inflate(resId, null, false);
        return this;
    }

    public AlertDialogFragment setContent(View view) {
        this.contentView = view;
        return this;
    }

    public AlertDialogFragment setTitle(String title) {
        this.title = title;
        return this;
    }

    public AlertDialogFragment setTitle(int titleRes) {
        this.title = context.getString(titleRes);
        return this;
    }

    public AlertDialogFragment setCancelText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    public AlertDialogFragment setConfirmText(String confirmText) {
        this.confirmText = confirmText;
        return this;
    }

    public AlertDialogFragment hideCancelBtn() {
        isHideCancel = true;
        return this;
    }

    public AlertDialogFragment onViewCreate(OnViewCreateListener onViewCreateListener) {
        this.onViewCreateListener = onViewCreateListener;
        return this;
    }


    public interface OnViewCreateListener {
        void onViewCreate(AlertDialogFragment fragment, View view);
    }

    public interface OnPositiveButtonClickListener  {
        void onClick(AlertDialogFragment fragment);
    }

    public interface OnNegativeButtonClickListener {
        void onClick(AlertDialogFragment fragment);
    }

}
