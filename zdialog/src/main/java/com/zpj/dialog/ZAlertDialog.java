package com.zpj.dialog;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zpj.dialog.base.DialogFragment;
import com.zpj.dialog.base.IDialog;
import com.zpj.dialog.base.ZAbstractDialog;

/**
 * @author Z-P-J
 * @date 2019/5/15 23:10
 */
public class ZAlertDialog extends ZAbstractDialog<ZAlertDialog> {

    private String title;
    private int titleTextColor = Color.BLACK;

    private String negativBtnStr = "取消";
    private int negativeBtnTextColor = Color.BLACK;

    private String positiveBtnStr = "确定";
    private int positiveBtnTextColor = Color.parseColor("#4285F4");

    private View contentView;


    private OnClickListener positiveBtnListener;
    private OnClickListener negativeBtnListener;

    public ZAlertDialog() {
        layoutRes = R.layout.easy_layout_dialog_alert;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        OnViewCreateListener tempOnViewCreateListener = onViewCreateListener;
        onViewCreateListener = null;
        super.onViewCreated(view, savedInstanceState);
        Button cancelBtn = getView(R.id.btn_cancel);
        Button okBtn = getView(R.id.btn_ok);
        okBtn.setText(positiveBtnStr);
        okBtn.setTextColor(positiveBtnTextColor);
        cancelBtn.setText(negativBtnStr);
        cancelBtn.setTextColor(negativeBtnTextColor);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveBtnListener != null) {
                    positiveBtnListener.onClick(ZAlertDialog.this);
                } else {
                    dismiss();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (negativeBtnListener != null) {
                    negativeBtnListener.onClick(ZAlertDialog.this);
                } else {
                    dismiss();
                }
            }
        });

        TextView titleText = getView(R.id.easy_text_title);
        titleText.setText(title);
        titleText.setTextColor(titleTextColor);

        if (tempOnViewCreateListener != null) {
            tempOnViewCreateListener.onViewCreate(ZAlertDialog.this, contentView);
        }
        FrameLayout container = getView(R.id.easy_layout_container);
        container.removeAllViews();
        container.addView(contentView);
    }

    public static ZAlertDialog with(Context context) {
        ZAlertDialog dialog = new ZAlertDialog();
        FragmentActivity activity;
        if (context instanceof FragmentActivity) {
            activity = (FragmentActivity) context;
        } else {
            activity = ((FragmentActivity) ((ContextWrapper) context).getBaseContext());
        }
        dialog.setFragmentActivity(activity);
        return dialog;
    }

    @Override
    public ZAlertDialog setContentView(View contentView) {
        this.contentView = contentView;
        return this;
    }

    @Override
    public ZAlertDialog setContentView(@LayoutRes int resId) {
        this.contentView = LayoutInflater.from(getContext()).inflate(resId, null);
        return this;
    }

    public ZAlertDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public ZAlertDialog setTitle(@StringRes int title) {
        this.title = getContext().getResources().getString(title);
        return this;
    }

    public ZAlertDialog setTitleTextColor(@ColorInt int titleTextColor) {
        this.titleTextColor = titleTextColor;
        return this;
    }

    public ZAlertDialog setContent(CharSequence content) {
        return setContent(content, Color.parseColor("#525a66"));
    }

    public ZAlertDialog setContent(CharSequence content, @ColorInt int textColor) {
        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.easy_content_text_view, null, false);
        textView.setText(content);
        textView.setTextColor(textColor);
        return setContentView(textView);
    }

    public ZAlertDialog setContent(@StringRes int content) {
        return setContent(getContext().getResources().getString(content));
    }

    public ZAlertDialog setContent(@StringRes int content, @ColorInt int textColor) {
        return setContent(getContext().getResources().getString(content), textColor);
    }

    public ZAlertDialog setPositiveButton(OnClickListener onclickListener) {
        return setPositiveButton("确定", onclickListener);
    }

    public ZAlertDialog setPositiveButton(String btnStr, OnClickListener onclickListener) {
        this.positiveBtnStr = btnStr;
        this.positiveBtnListener = onclickListener;
        return this;
    }

    public ZAlertDialog setPositiveButtonTextColor(@ColorInt int textColor) {
        this.positiveBtnTextColor = textColor;
        return this;
    }

    public ZAlertDialog setPositiveButton(@StringRes int strRes, OnClickListener onclickListener) {
        return setPositiveButton(getContext().getResources().getString(strRes), onclickListener);
    }

    public ZAlertDialog setNegativeButton(OnClickListener onclickListener) {
        return setNegativeButton("取消", onclickListener);
    }

    public ZAlertDialog setNegativeButton(String btnStr, OnClickListener onclickListener) {
        this.negativBtnStr = btnStr;
        this.negativeBtnListener = onclickListener;
        return this;
    }

    public ZAlertDialog setNegativeButton(@StringRes int strRes, OnClickListener onclickListener) {
        return setNegativeButton(getContext().getResources().getString(strRes), onclickListener);
    }

    public ZAlertDialog setNegativeButtonTextColor(@ColorInt int textColor) {
        this.negativeBtnTextColor = textColor;
        return this;
    }

}
