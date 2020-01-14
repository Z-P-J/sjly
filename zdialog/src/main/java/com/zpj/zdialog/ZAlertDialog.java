package com.zpj.zdialog;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorLong;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.zdialog.base.DialogFragment;
import com.zpj.zdialog.base.IDialog;

/**
 * @author Z-P-J
 * @date 2019/5/15 23:10
 */
public class ZAlertDialog implements IDialog {

    private final Context context;

    private final ZDialog dialog;

    private String title;
    private int titleTextColor = Color.BLACK;

    private View contentView;

    private String negativBtnStr = "取消";
    private int negativeBtnTextColor = Color.BLACK;

    private String positiveBtnStr = "确定";
    private int positiveBtnTextColor = Color.parseColor("#4285F4");

    private float screenWidthPercent = 0.9f;

    private boolean isCancelable = true;
    private boolean isCancelableOutside = true;
    private boolean swipable = true;

    private OnViewCreateListener onViewCreateListener;
    private OnClickListener positiveBtnListener;
    private OnClickListener negativeBtnListener;
    private OnDismissListener onDismissListener;
    private OnCancelListener onCancelListener;

    private ZAlertDialog(Context context) {
        this.context = context;
        dialog = ZDialog.with(context);
    }

    public static ZAlertDialog with(Context context) {
        return new ZAlertDialog(context);
    }

    public ZAlertDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public ZAlertDialog setTitle(@StringRes int title) {
        this.title = context.getResources().getString(title);
        return this;
    }

    public ZAlertDialog setTitleTextColor(@ColorInt int titleTextColor) {
        this.titleTextColor = titleTextColor;
        return this;
    }

    public ZAlertDialog setContentView(View contentView) {
        this.contentView = contentView;
        return this;
    }

    public ZAlertDialog setContentView(@LayoutRes int resId) {
        this.contentView = LayoutInflater.from(context).inflate(resId, null);
        return this;
    }

    public ZAlertDialog setGravity(int gravity) {
        dialog.setGravity(gravity);
        return this;
    }

    public ZAlertDialog setAnimatorCreateListener(DialogFragment.OnAnimatorCreateListener onAnimatorCreateListener) {
        dialog.setAnimatorCreateListener(onAnimatorCreateListener);
        return this;
    }

    public ZAlertDialog setOnViewCreateListener(OnViewCreateListener listener) {
        this.onViewCreateListener = listener;
        return this;
    }

    public ZAlertDialog setContent(CharSequence content) {
        return setContent(content, Color.parseColor("#525a66"));
    }

    public ZAlertDialog setContent(CharSequence content, @ColorInt int textColor) {
        TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.easy_content_text_view, null, false);
        textView.setText(content);
        textView.setTextColor(textColor);
        return setContentView(textView);
    }

    public ZAlertDialog setContent(@StringRes int content) {
        return setContent(context.getResources().getString(content));
    }

    public ZAlertDialog setContent(@StringRes int content, @ColorInt int textColor) {
        return setContent(context.getResources().getString(content), textColor);
    }

    public ZAlertDialog setScreenWidthP(float percentage) {
        screenWidthPercent = percentage;
        return this;
    }

    public ZAlertDialog setSwipable(boolean swipable) {
        this.swipable = swipable;
        return this;
    }

    public ZAlertDialog setCancelable(boolean cancelable) {
        isCancelable = cancelable;
        return this;
    }

    public ZAlertDialog setCancelableOutside(boolean cancelableOutside) {
        isCancelableOutside = cancelableOutside;
        return this;
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
        return setPositiveButton(context.getResources().getString(strRes), onclickListener);
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
        return setNegativeButton(context.getResources().getString(strRes), onclickListener);
    }

    public ZAlertDialog setNegativeButtonTextColor(@ColorInt int textColor) {
        this.negativeBtnTextColor = textColor;
        return this;
    }

    public ZAlertDialog setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    public ZAlertDialog setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return this;
    }

    @Override
    public ZAlertDialog show() {
        dialog.setContentView(R.layout.easy_layout_dialog_alert)
                .setWindowBackgroundP(0.2f)
                .setScreenWidthP(screenWidthPercent)
                .setDialogCancelable(isCancelable)
                .setCancelableOutSide(isCancelableOutside)
                .setSwipeEnable(swipable)
                .setOnViewCreateListener(new OnViewCreateListener() {
                    @Override
                    public void onViewCreate(final IDialog dialog, View view) {
                        Button cancelBtn = dialog.getView(R.id.btn_cancel);
                        Button okBtn = dialog.getView(R.id.btn_ok);
                        okBtn.setText(positiveBtnStr);
                        okBtn.setTextColor(positiveBtnTextColor);
                        cancelBtn.setText(negativBtnStr);
                        cancelBtn.setTextColor(negativeBtnTextColor);
                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (positiveBtnListener != null) {
                                    positiveBtnListener.onClick(dialog);
                                } else {
                                    dialog.dismiss();
                                }
                            }
                        });
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (negativeBtnListener != null) {
                                    negativeBtnListener.onClick(dialog);
                                } else {
                                    dialog.dismiss();
                                }
                            }
                        });

                        TextView titleText = dialog.getView(R.id.easy_text_title);
                        titleText.setText(title);
                        titleText.setTextColor(titleTextColor);

                        if (onViewCreateListener != null) {
                            onViewCreateListener.onViewCreate(dialog, contentView);
                        }
                        FrameLayout container = dialog.getView(R.id.easy_layout_container);
                        container.removeAllViews();
                        container.addView(contentView);
                    }
                })
                .setOnDismissListener(onDismissListener)
                .setOnCancleListener(onCancelListener)
                .show();
        return this;
    }

    @Override
    public void dismissWithoutAnim() {
        dialog.dismissWithoutAnim();
    }

    @Override
    public <T extends View> T getView(int id) {
        return dialog.getView(id);
    }

    @Override
    public void dismiss() {
        dialog.dismiss();
    }

    @Override
    public void hide() {
        dialog.hide();
    }

}
