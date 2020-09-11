package com.zpj.popup.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zpj.popup.R;
import com.zpj.popup.util.KeyboardUtils;
import com.zpj.popup.util.XPopupUtils;
import com.zpj.widget.checkbox.SmoothCheckBox;

/**
 * Description: 带输入框，确定和取消的对话框
 * Create by dance, at 2018/12/16
 */
public class AbstractCheckPopup<T extends AbstractCheckPopup> extends AbstractAlertPopup<T> implements View.OnClickListener{

    private SmoothCheckBox checkBox;
    private TextView tvTitle;

    private String checkTitle;

    private boolean isChecked;

    private SmoothCheckBox.OnCheckedChangeListener onCheckedChangeListener;

    public AbstractCheckPopup(@NonNull Context context) {
        super(context);
    }


    @Override
    protected int getImplLayoutId() {
        return R.layout._xpopup_center_impl_check;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        checkBox = findViewById(R.id.check_box);
        checkBox.setChecked(isChecked);
        tvTitle = findViewById(R.id.check_title);
        tvTitle.setText(checkTitle);
        checkBox.setOnCheckedChangeListener(onCheckedChangeListener);

        LinearLayout checkLayout = findViewById(R.id.layout_check);
        checkLayout.setOnClickListener(v -> checkBox.performClick());
    }

    @Override
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

        textView.setLineSpacing(6, 1);
        return setContent(textView);
    }

    //    @Override
//    public void onClick(View v) {
//        super.onClick(v);
//        if (v == tv_cancel) {
//            if (cancelListener != null) cancelListener.onCancel();
//            dismiss();
//        } else if (v == tv_confirm) {
//            if (confirmListener != null) confirmListener.onConfirm(self());
//            dismiss();
//        }
//    }

    @Override
    public T show() {
        return super.show();
    }


    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public T setCheckTitle(String checkTitle) {
        this.checkTitle = checkTitle;
        return self();
    }

    public T setChecked(boolean checked) {
        isChecked = checked;
        return self();
    }

    public T setOnCheckedChangeListener(SmoothCheckBox.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
        return self();
    }

}
