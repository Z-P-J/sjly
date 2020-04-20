package com.zpj.popup.impl;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;

import com.zpj.popup.R;
import com.zpj.popup.XPopup;
import com.zpj.popup.util.XPopupUtils;

/**
 * Description: 带输入框，确定和取消的对话框
 * Create by dance, at 2018/12/16
 */
public class AbstractInputPopup<T extends AbstractAlertPopup> extends AbstractAlertPopup<T> implements View.OnClickListener{

    private boolean autoShowKeyboard = true;
    private boolean emptyable = false;
    private int selectionStart = 0;
    private int selectionEnd = -1;

    public AbstractInputPopup(@NonNull Context context) {
        super(context);
    }

//    /**
//     * 绑定已有布局
//     * @param layoutId 在Confirm弹窗基础上需要增加一个id为et_input的EditText
//     * @return
//     */
//    public InputPopup bindLayout(int layoutId){
//        bindLayoutId = layoutId;
//        return this;
//    }


    @Override
    protected int getImplLayoutId() {
        return R.layout._xpopup_center_impl_input;
    }

    private AppCompatEditText et_input;
    public String inputContent;
    private String hint;
    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        et_input = findViewById(R.id.et_input);
        et_input.setVisibility(VISIBLE);
        if(!TextUtils.isEmpty(hint)){
            et_input.setHint(hint);
        }
        if(!TextUtils.isEmpty(inputContent)){
            et_input.setText(inputContent);
            et_input.setSelection(inputContent.length());
        }
        applyPrimary();
    }



    public T setEditText(String inputContent) {
        this.inputContent = inputContent;
        return self();
    }

    public T setHint(String hint) {
        this.hint = hint;
        return self();
    }

    public T setEmptyable(boolean emptyable) {
        this.emptyable = emptyable;
        return self();
    }

    public T setSelection(int start, int stop) {
        this.selectionStart = start;
        this.selectionEnd = stop;
        return self();
    }

    //    @Override
//    public InputPopup setTitle(int titleRes) {
//        super.setTitle(titleRes);
//        return this;
//    }
//
//    @Override
//    public InputPopup setTitle(String title) {
//        super.setTitle(title);
//        return this;
//    }

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
        XPopupUtils.setCursorDrawableColor(et_input, XPopup.getPrimaryColor());
        et_input.post(new Runnable() {
            @Override
            public void run() {
                BitmapDrawable defaultDrawable = XPopupUtils.createBitmapDrawable(getResources(), et_input.getMeasuredWidth(), Color.parseColor("#888888"));
                BitmapDrawable focusDrawable = XPopupUtils.createBitmapDrawable(getResources(), et_input.getMeasuredWidth(), XPopup.getPrimaryColor());
                et_input.setBackgroundDrawable(XPopupUtils.createSelector(defaultDrawable, focusDrawable));
            }
        });

    }

//    OnCancelListener cancelListener;
//    OnInputConfirmListener inputConfirmListener;
//    public void setListener( OnInputConfirmListener inputConfirmListener,OnCancelListener cancelListener){
//        this.cancelListener = cancelListener;
//        this.inputConfirmListener = inputConfirmListener;
//    }

//    @Override
//    public void onClick(View v) {
//        if(v==tv_cancel){
//            if(cancelListener!=null)cancelListener.onCancel();
//            dismiss();
//        }else if(v==tv_confirm){
//            if(inputConfirmListener!=null)inputConfirmListener.onConfirm(et_input.getText().toString().trim());
//            if(popupInfo.autoDismiss)dismiss();
//        }
//    }
}
