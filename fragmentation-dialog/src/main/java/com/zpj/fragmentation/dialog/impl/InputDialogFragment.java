package com.zpj.fragmentation.dialog.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.zpj.fragmentation.dialog.R;

/**
 * Description: 带输入框，确定和取消的对话框
 * Create by dance, at 2018/12/16
 */
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
        if(!TextUtils.isEmpty(hint)){
            et_input.setHint(hint);
        }
        if(!TextUtils.isEmpty(inputContent)){
            et_input.setText(inputContent);
            if (selectionStart < 0) {
                selectionStart = 0;
            }
            if (selectionEnd < selectionStart) {
                selectionEnd = inputContent.length() - 1;
            }
            if (selectionStart <= selectionEnd) {
                et_input.setSelection(selectionStart, selectionEnd);
            }
        }
        applyPrimary();
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
    public void dismiss() {
        hideSoftInput();
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v == tv_cancel) {
            dismiss();
            if (cancelListener != null) cancelListener.onClick(this);
        } else if (v == tv_confirm) {
            if (!emptyable && TextUtils.isEmpty(getText())) {
                Toast.makeText(context, "输入不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            dismiss();
            if (confirmListener != null) confirmListener.onClick(this);
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
