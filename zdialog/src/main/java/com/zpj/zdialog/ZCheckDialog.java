package com.zpj.zdialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.zdialog.base.IDialog;
import com.zpj.zdialog.view.SmoothCheckBox;

/**
 * @author Z-P-J
 * @date 2019/5/15 23:10
 */
public class ZCheckDialog {

    public interface OnClickListener {
        void onClick(IDialog dialog, boolean isChecked);
    }

    private Context context;

    private String title;

    private String content;

    private String checkTitle;

    private boolean isChecked;

    private String negativBtnStr = "取消";

    private String positiveBtnStr = "确定";

    private OnClickListener positiveBtnListener;
    private OnClickListener negativeBtnListener;
    private SmoothCheckBox.OnCheckedChangeListener onCheckedChangeListener;

    private ZCheckDialog(Context context) {
        this.context = context;
    }

    public static ZCheckDialog with(Context context) {
        return new ZCheckDialog(context);
    }

    public ZCheckDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public ZCheckDialog setContent(String content) {
        this.content = content;
        return this;
    }

    public ZCheckDialog setCheckTitle(String checkTitle) {
        this.checkTitle = checkTitle;
        return this;
    }

    public ZCheckDialog setChecked(boolean checked) {
        isChecked = checked;
        return this;
    }

    public ZCheckDialog setOnCheckedChangeListener(SmoothCheckBox.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
        return this;
    }

    public ZCheckDialog setPositiveButton(OnClickListener onclickListener) {
        return setPositiveButton("确定", onclickListener);
    }

    public ZCheckDialog setPositiveButton(String btnStr, OnClickListener onclickListener) {
        this.positiveBtnStr = btnStr;
        this.positiveBtnListener = onclickListener;
        return this;
    }

    public ZCheckDialog setNegativeButton(OnClickListener onclickListener) {
        return setNegativeButton("取消", onclickListener);
    }

    public ZCheckDialog setNegativeButton(String btnStr, OnClickListener onclickListener) {
        this.negativBtnStr = btnStr;
        this.negativeBtnListener = onclickListener;
        return this;
    }

    public void show() {
        ZDialog.with(context)
                .setContentView(R.layout.easy_layout_dialog_check)
                .setWindowBackgroundP(0.4f)
                .setScreenWidthP(0.9f)
                .setOnViewCreateListener(new IDialog.OnViewCreateListener() {
                    @Override
                    public void onViewCreate(final IDialog dialog, View view) {

                        LinearLayout checkLayout = dialog.getView(R.id.layout_check);
                        final SmoothCheckBox smoothCheckBox = dialog.getView(R.id.check_box);
                        smoothCheckBox.setChecked(isChecked);
                        smoothCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
                        TextView checkTitleView = dialog.getView(R.id.check_title);
                        checkTitleView.setText(checkTitle);
                        checkLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                smoothCheckBox.performClick();
                            }
                        });

                        Button cancelBtn = dialog.getView(R.id.btn_cancel);
                        Button okBtn = dialog.getView(R.id.btn_ok);
                        okBtn.setText(positiveBtnStr);
                        cancelBtn.setText(negativBtnStr);
                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (positiveBtnListener != null) {
                                    positiveBtnListener.onClick(dialog, smoothCheckBox.isChecked());
                                } else {
                                    dialog.dismiss();
                                }
                            }
                        });
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (negativeBtnListener != null) {
                                    negativeBtnListener.onClick(dialog, smoothCheckBox.isChecked());
                                } else {
                                    dialog.dismiss();
                                }
                            }
                        });

                        TextView titleText = dialog.getView(R.id.text_title);
                        TextView contentText = dialog.getView(R.id.text_content);
                        titleText.setText(title);
                        contentText.setText(content);
                    }
                })
                .show();
    }

}
