package com.zpj.popup.impl;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zpj.popup.R;
import com.zpj.popup.XPopup;
import com.zpj.popup.core.BasePopup;
import com.zpj.popup.core.CenterPopup;
import com.zpj.popup.interfaces.OnCancelListener;
import com.zpj.popup.interfaces.OnConfirmListener;
import com.zpj.popup.util.XPopupUtils;

public abstract class AbstractAlertPopup<T extends AbstractAlertPopup> extends CenterPopup<T>
        implements View.OnClickListener {

    OnCancelListener cancelListener;
    OnConfirmListener<T> confirmListener;
    TextView tv_title, tv_cancel, tv_confirm;
    String title, cancelText, confirmText;
    boolean isHideCancel = false;
    private boolean autoDismiss = true;

    private View contentView;

    protected OnViewCreateListener onViewCreateListener;

    public AbstractAlertPopup(@NonNull Context context) {
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
        tv_confirm.setTextColor(getColorPrimary());
    }

    public int getColorPrimary(){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

//    protected T self() {
//        return (T) this;
//    }

    public T setConfirmButton(OnConfirmListener<T> listener) {
        this.confirmListener = listener;
        return self();
    }

    public T setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
        return self();
    }

    public T setConfirmButton(String btnStr, OnConfirmListener<T> listener) {
        this.confirmText = btnStr;
        this.confirmListener = listener;
        return self();
    }

    public T setConfirmButton(int btnStrId, OnConfirmListener<T> listener) {
        this.confirmText = context.getString(btnStrId);
        this.confirmListener = listener;
        return self();
    }

    public T setCancelButton(OnCancelListener listener) {
        this.cancelListener = listener;
        return self();
    }

    public T setCancelButton(String btnStr, OnCancelListener listener) {
        this.cancelText = btnStr;
        this.cancelListener = listener;
        return self();
    }

    public T setCancelButton(int btnStrId, OnCancelListener listener) {
        this.cancelText = context.getString(btnStrId);
        this.cancelListener = listener;
        return self();
    }

//    public AlertPopup onCancel(OnCancelListener cancelListener) {
//        this.cancelListener = cancelListener;
//        return this;
//    }

    public T setListener(OnConfirmListener<T> confirmListener, OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        this.confirmListener = confirmListener;
        return self();
    }

    public T setContent(CharSequence content) {
        TextView textView = new TextView(context);
        textView.setText(content);
        textView.setTextColor(context.getResources().getColor(R.color._xpopup_content_color));
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
        return self();
    }

    public T setContent(@LayoutRes int resId) {
        this.contentView = LayoutInflater.from(context).inflate(resId, null, false);
        return self();
    }

    public T setContent(View view) {
        this.contentView = view;
        return self();
    }

    public T setTitle(String title) {
        this.title = title;
        return self();
    }

    public T setTitle(int titleRes) {
        this.title = context.getString(titleRes);
        return self();
    }

    public T setCancelText(String cancelText) {
        this.cancelText = cancelText;
        return self();
    }

    public T setConfirmText(String confirmText) {
        this.confirmText = confirmText;
        return self();
    }

    public T hideCancelBtn() {
        isHideCancel = true;
        return self();
    }

    public T onViewCreate(OnViewCreateListener onViewCreateListener) {
        this.onViewCreateListener = onViewCreateListener;
        return self();
    }

    @Override
    public void onClick(View v) {
        if (v == tv_cancel) {
            if (cancelListener != null) cancelListener.onCancel();
            if (autoDismiss) dismiss();
        } else if (v == tv_confirm) {
            if (confirmListener != null) confirmListener.onConfirm(self());
            if (autoDismiss) dismiss();
        }
    }

    public interface OnViewCreateListener {
        void onViewCreate(BasePopup popup, View view);
    }
}
