package com.zpj.fragmentation.dialog.impl;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.base.CenterDialogFragment;
import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.ScreenUtils;

public class AlertDialogFragment extends CenterDialogFragment
        implements View.OnClickListener {

    protected TextView tv_title, tv_cancel, tv_confirm, tv_neutral;
    protected String title, cancelText, neutralText, confirmText;
    protected int positionBtnColor, neutralBtnColor, negativeBtnColor;

    protected CharSequence content;

    protected View contentView;
    private LinearLayout llButtons;

    protected OnButtonClickListener cancelListener;
    protected OnButtonClickListener confirmListener;
    protected OnButtonClickListener onNeutralButtonClickListener;

    protected boolean isHideCancel = false;
    protected boolean autoDismiss = true;

    protected OnViewCreateListener onViewCreateListener;

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
        tv_neutral = findViewById(R.id.tv_neutral);
        if (onNeutralButtonClickListener != null) {
            tv_neutral.setVisibility(View.VISIBLE);
        }
        tv_confirm = findViewById(R.id.tv_confirm);
        tv_title.setTextColor(DialogThemeUtils.getMajorTextColor(context));

        llButtons = findViewById(R.id.ll_buttons);


        if (contentView == null && !TextUtils.isEmpty(content)) {
            TextView textView = new TextView(context);
            textView.setText(content);
            textView.setTextColor(DialogThemeUtils.getNormalTextColor(context));
//        textView.setTextColor(context.getResources().getColor(R.color._xpopup_text_normal_color));
            textView.setTextSize(14);
            textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int padding = ScreenUtils.dp2pxInt(context, 24);
                    textView.setPadding(padding, padding, padding, padding);
                }
            });
            textView.setMinHeight(ScreenUtils.dp2pxInt(context, 80));
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
        tv_neutral.setOnClickListener(this);

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
        if (!TextUtils.isEmpty(neutralText)) {
            tv_neutral.setText(neutralText);
        }

        if (isHideCancel) tv_cancel.setVisibility(View.GONE);


//        MaxHeightScrollView scrollView = findViewById(R.id.scroll_view);
//        contentView
//                .getViewTreeObserver()
//                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                        Log.d("contentView.contentV", "tv_title.getMeasuredHeight()=" + tv_title.getHeight() + " llButtons.getMeasuredHeight()=" + llButtons.getHeight());
//                        int max = getMaxHeight() - tv_title.getHeight() - ScreenUtils.dp2pxInt(context, 60);
//                        if (contentView.getMeasuredHeight() < max) {
//                            max = contentView.getMeasuredHeight();
//                        }
//                        scrollView.setMaxHeight(max);
////                        scrollView.setMaxHeight(contentView.getHeight());
//                    }
//                });

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
            if (cancelListener != null) {
                cancelListener.onClick(this);
            }
            if (autoDismiss) {
                dismiss();
            }

        } else if (v == tv_confirm) {
            if (confirmListener != null) {
                confirmListener.onClick(this);
            }
            if (autoDismiss) {
                dismiss();
            }

        }  else if (v == tv_neutral) {
            if (onNeutralButtonClickListener != null) {
                onNeutralButtonClickListener.onClick(this);
            }
            if (autoDismiss) {
                dismiss();
            }
        }
    }

    protected void applyPrimaryColor() {
        if (positionBtnColor == 0) {
            tv_confirm.setTextColor(DialogThemeUtils.getPositiveTextColor(context));
        } else {
            tv_confirm.setTextColor(positionBtnColor);
        }

        if (neutralBtnColor == 0) {
            tv_cancel.setTextColor(DialogThemeUtils.getNegativeTextColor(context));
        } else {
            tv_neutral.setTextColor(neutralBtnColor);
        }

        if (negativeBtnColor == 0) {
            tv_cancel.setTextColor(DialogThemeUtils.getNegativeTextColor(context));
        } else {
            tv_cancel.setTextColor(negativeBtnColor);
        }
    }

    public AlertDialogFragment setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
        return this;
    }

    public AlertDialogFragment setPositiveButton(OnButtonClickListener listener) {
        this.confirmListener = listener;
        return this;
    }

    public AlertDialogFragment setPositiveButton(String btnStr, OnButtonClickListener listener) {
        this.confirmText = btnStr;
        this.confirmListener = listener;
        return this;
    }

    public AlertDialogFragment setPositiveButton(int btnStrId, OnButtonClickListener listener) {
        this.confirmText = ContextUtils.getApplicationContext().getString(btnStrId);
        this.confirmListener = listener;
        return this;
    }

    public AlertDialogFragment setNegativeButton(OnButtonClickListener listener) {
        this.cancelListener = listener;
        return this;
    }

    public AlertDialogFragment setNegativeButton(String btnStr, OnButtonClickListener listener) {
        this.cancelText = btnStr;
        this.cancelListener = listener;
        return this;
    }

    public AlertDialogFragment setNegativeButton(int btnStrId, OnButtonClickListener listener) {
        this.cancelText = ContextUtils.getApplicationContext().getString(btnStrId);
        this.cancelListener = listener;
        return this;
    }

    public AlertDialogFragment setNeutralButton(OnButtonClickListener listener) {
        this.onNeutralButtonClickListener = listener;
        return this;
    }

    public AlertDialogFragment setNeutralButton(String btnStr, OnButtonClickListener listener) {
        this.neutralText = btnStr;
        this.onNeutralButtonClickListener = listener;
        return this;
    }

    public AlertDialogFragment setNeutralButton(int btnStrId, OnButtonClickListener listener) {
        this.neutralText = ContextUtils.getApplicationContext().getString(btnStrId);
        this.onNeutralButtonClickListener = listener;
        return this;
    }

    public AlertDialogFragment setContent(CharSequence content) {
        this.content = content;
        return this;
    }

    public AlertDialogFragment setContent(@LayoutRes int resId) {
        this.contentView = LayoutInflater.from(ContextUtils.getApplicationContext()).inflate(resId, null, false);
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
        this.title = ContextUtils.getApplicationContext().getString(titleRes);
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

    public AlertDialogFragment setPositionButtonnColor(int positionBtnColor) {
        this.positionBtnColor = positionBtnColor;
        return this;
    }

    public AlertDialogFragment setNeutralButtonColor(int neutralBtnColor) {
        this.neutralBtnColor = neutralBtnColor;
        return this;
    }

    public AlertDialogFragment setNegativeButtonColor(int negativeBtnColor) {
        this.negativeBtnColor = negativeBtnColor;
        return this;
    }

    public interface OnViewCreateListener {
        void onViewCreate(AlertDialogFragment fragment, View view);
    }

//    public interface OnPositiveButtonClickListener  {
//        void onClick(AlertDialogFragment fragment);
//    }
//
//    public interface OnNegativeButtonClickListener {
//        void onClick(AlertDialogFragment fragment);
//    }

    public interface OnButtonClickListener {
        void onClick(AlertDialogFragment fragment);
    }

}
