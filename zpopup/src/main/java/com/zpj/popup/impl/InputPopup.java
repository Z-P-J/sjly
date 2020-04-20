package com.zpj.popup.impl;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;

import com.zpj.popup.XPopup;
import com.zpj.popup.interfaces.OnCancelListener;
import com.zpj.popup.interfaces.OnInputConfirmListener;
import com.zpj.popup.util.XPopupUtils;
import com.zpj.popup.R;

/**
 * Description: 带输入框，确定和取消的对话框
 * Create by dance, at 2018/12/16
 */
public final class InputPopup extends AbstractInputPopup<InputPopup> {

    public InputPopup(@NonNull Context context) {
        super(context);
    }

}
