package com.zpj.fragmentation.dialog.impl;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;
import com.zpj.utils.KeyboardObserver;
import com.zpj.utils.ScreenUtils;

public class InputDialogFragment extends AlertDialogFragment implements View.OnClickListener{

    private boolean autoShowKeyboard = true;
    private boolean emptyable = false;
    private int selectionStart = 0;
    private int selectionEnd = -1;

    private AppCompatEditText et_input;
    public String inputContent;
    private String hint;

    @Override
    protected int getContentLayoutId() {
        return R.layout._dialog_layout_center_impl_input;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        et_input = findViewById(R.id.et_input);
        et_input.setVisibility(View.VISIBLE);
        int dp16 = ScreenUtils.dp2pxInt(context, 16);
        FrameLayout centerPopupContainer = findViewById(R.id.centerPopupContainer);
        KeyboardObserver.registerSoftInputChangedListener(_mActivity, centerPopupContainer, height -> {
            if (height > 0 && isSupportVisible()) {
                Rect rect = new Rect();
                et_input.getGlobalVisibleRect(rect);
                float bottom = ScreenUtils.getScreenHeight(context) - rect.bottom - dp16;
                if (bottom < height) {
                    centerPopupContainer.setTranslationY(bottom - height);
                }
            } else {
                centerPopupContainer.setTranslationY(0);
            }
        });
        if(!TextUtils.isEmpty(hint)){
            et_input.setHint(hint);
        }
        if(!TextUtils.isEmpty(inputContent)){
            et_input.setText(inputContent);
            if (selectionStart < 0) {
                selectionStart = 0;
            }
            if (selectionEnd < selectionStart) {
                selectionEnd = inputContent.length();
            }
            if (selectionStart <= selectionEnd) {
                et_input.setSelection(selectionStart, selectionEnd);
            }
        }
        applyPrimary();
        et_input.setTextColor(DialogThemeUtils.getMajorTextColor(context));
        et_input.setHintTextColor(DialogThemeUtils.getNormalTextColor(context));
        et_input.post(new Runnable() {
            @Override
            public void run() {
                if (autoShowKeyboard) {
                    showSoftInput(et_input);
                }
            }
        });

    }

    @Override
    protected View createContentView(CharSequence content) {
        TextView textView = new TextView(context);
        textView.setText(content);
        textView.setTextColor(DialogThemeUtils.getNormalTextColor(context));
        textView.setTextSize(14);
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int padding = ScreenUtils.dp2pxInt(context, 24);
                textView.setPadding(padding, padding, padding, 0);
            }
        });
        textView.setLineSpacing(6, 1);
        return textView;
    }

    @Override
    public void dismiss() {
        hideSoftInput();
        super.dismiss();
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
            if (!emptyable && TextUtils.isEmpty(getText())) {
                Toast.makeText(context, "输入不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (confirmListener != null) {
                confirmListener.onClick(this);
            }
            if (autoDismiss) {
                dismiss();
            }
        }
    }

    public InputDialogFragment setEditText(String inputContent) {
        this.inputContent = inputContent;
        return this;
    }

    public InputDialogFragment setHint(String hint) {
        this.hint = hint;
        return this;
    }

    public InputDialogFragment setEmptyable(boolean emptyable) {
        this.emptyable = emptyable;
        return this;
    }

    public InputDialogFragment setSelection(int start, int stop) {
        this.selectionStart = start;
        this.selectionEnd = stop;
        return this;
    }

    public InputDialogFragment setAutoShowKeyboard(boolean autoShowKeyboard) {
        this.autoShowKeyboard = autoShowKeyboard;
        return this;
    }

    public AppCompatEditText getEditText() {
        return et_input;
    }

    public String getText() {
        if (getEditText().getText() == null) {
            return "";
        }
        return getEditText().getText().toString();
    }

    protected void applyPrimary(){
        super.applyPrimaryColor();
//        XPopupUtils.setCursorDrawableColor(et_input, getColorPrimary());
    }
}
