package com.zpj.zdialog;

import android.content.Context;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zpj.zdialog.base.IDialog;
import com.zpj.zdialog.utils.KeyboardUtil;

/**
 * @author Z-P-J
 * @date 2019/5/15 23:10
 */
public class ZEditDialog {

    private Context context;

    private String title;

    private String text;

    private String hint;

    private String negativBtnStr = "取消";

    private String positiveBtnStr = "确定";

    private boolean autoShowKeyboard = true;

    private boolean emptyable = false;

    IDialog.OnPositiveButtonClickListener positiveBtnListener;
    IDialog.OnClickListener negativeBtnListener;

    private TextWatcher watcher;

    private ZEditDialog(Context context) {
        this.context = context;
    }

    public static ZEditDialog with(Context context) {
        return new ZEditDialog(context);
    }

    public ZEditDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public ZEditDialog setEditText(String editText) {
        this.text = editText;
        return this;
    }

    public ZEditDialog setHint(String hint) {
        this.hint = hint;
        return this;
    }

    public ZEditDialog setAutoShowKeyboard(boolean autoShowKeyboard) {
        this.autoShowKeyboard = autoShowKeyboard;
        return this;
    }

    public ZEditDialog setEmptyable(boolean emptyable) {
        this.emptyable = emptyable;
        return this;
    }

    public ZEditDialog setNegativeButton(IDialog.OnClickListener onclickListener) {
        return setNegativeButton("取消", onclickListener);
    }

    public ZEditDialog setNegativeButton(String btnStr, IDialog.OnClickListener onclickListener) {
        this.negativBtnStr = btnStr;
        this.negativeBtnListener = onclickListener;
        return this;
    }

    public ZEditDialog setPositiveButton(IDialog.OnPositiveButtonClickListener onclickListener) {
        return setPositiveButton("确定", onclickListener);
    }

    public ZEditDialog setPositiveButton(String btnStr, IDialog.OnPositiveButtonClickListener onclickListener) {
        this.positiveBtnStr = btnStr;
        this.positiveBtnListener = onclickListener;
        return this;
    }

    public ZEditDialog setOnTextChangedListener(TextWatcher watcher) {
        this.watcher = watcher;
        return this;
    }

    public void show() {
        ZDialog.with(context)
                .setContentView(R.layout.layout_dialog_edit)
                .setWindowBackgroundP(0.2f)
                .setScreenWidthP(0.9f)
                .setOnViewCreateListener(new IDialog.OnViewCreateListener() {
                    @Override
                    public void onViewCreate(final IDialog dialog, View view) {
                        TextView titleText = view.findViewById(R.id.text_title);
                        final EditText editText = view.findViewById(R.id.text_edit);
                        editText.requestFocus();
                        titleText.setText(title);
                        editText.setText(text);
                        editText.setHint(hint);
                        if (watcher != null) {
                            editText.addTextChangedListener(watcher);
                        }

                        if (autoShowKeyboard) {
                            KeyboardUtil.showKeyboard(editText);
                        }

                        Button cancelBtn = view.findViewById(R.id.btn_cancel);
                        Button okBtn = view.findViewById(R.id.btn_ok);
                        okBtn.setText(positiveBtnStr);
                        cancelBtn.setText(negativBtnStr);
                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!emptyable && TextUtils.isEmpty(editText.getText().toString())) {
                                    Toast.makeText(context, "输入不能为空！", Toast.LENGTH_SHORT).show();
                                } else {
                                    KeyboardUtil.hideKeyboard(editText);
                                    if (positiveBtnListener != null) {
                                        positiveBtnListener.onClick(dialog, editText.getText().toString().trim());
                                    } else {
                                        dialog.dismiss();
                                    }
                                }
                            }
                        });
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                KeyboardUtil.hideKeyboard(editText);
                                if (negativeBtnListener != null) {
                                    negativeBtnListener.onClick(dialog);
                                } else {
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                })
                .show();
    }

}
