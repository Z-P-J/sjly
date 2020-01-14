package com.zpj.zdialog;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zpj.utils.AnimHelper;
import com.zpj.utils.ScreenUtils;
import com.zpj.zdialog.base.DialogFragment;
import com.zpj.zdialog.base.IDialog;

/**
 * @author Z-P-J
 * @date 2019/5/15 23:10
 */
public class ZLoadingDialog implements IDialog {

    private final Context context;

    private final ZDialog dialog;

    private CharSequence content;

    private float screenWidthPercent = 0.3f;

    private boolean isCancelable = false;
    private boolean isCancelableOutside = false;
    private boolean swipable = false;

    private OnDismissListener onDismissListener;
    private OnCancelListener onCancelListener;

    private ZLoadingDialog(Context context) {
        this.context = context;
        dialog = ZDialog.with(context);
    }

    public static ZLoadingDialog with(Context context) {
        return new ZLoadingDialog(context);
    }

    public ZLoadingDialog setGravity(int gravity) {
        dialog.setGravity(gravity);
        return this;
    }

    public ZLoadingDialog setAnimatorCreateListener(DialogFragment.OnAnimatorCreateListener onAnimatorCreateListener) {
        dialog.setAnimatorCreateListener(onAnimatorCreateListener);
        return this;
    }

    public ZLoadingDialog setContent(CharSequence content) {
        return setContent(content, Color.parseColor("#525a66"));
    }

    public ZLoadingDialog setContent(CharSequence content, @ColorInt int textColor) {
        this.content = content;
        return this;
    }

    public ZLoadingDialog setContent(@StringRes int content) {
        return setContent(context.getResources().getString(content));
    }

    public ZLoadingDialog setContent(@StringRes int content, @ColorInt int textColor) {
        return setContent(context.getResources().getString(content), textColor);
    }

    public ZLoadingDialog setScreenWidthP(float percentage) {
        screenWidthPercent = percentage;
        return this;
    }

    public ZLoadingDialog setSwipable(boolean swipable) {
        this.swipable = swipable;
        return this;
    }

    public ZLoadingDialog setCancelable(boolean cancelable) {
        isCancelable = cancelable;
        return this;
    }

    public ZLoadingDialog setCancelableOutside(boolean cancelableOutside) {
        isCancelableOutside = cancelableOutside;
        return this;
    }

    public ZLoadingDialog setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    public ZLoadingDialog setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return this;
    }

    public void uploadContent(CharSequence content) {
        this.content = content;
        ((TextView) dialog.getView(R.id.contentTextView)).setText(content);
    }

    @Override
    public ZLoadingDialog show() {
        dialog.setContentView(R.layout.easy_layout_dialog_loading)
                .setWindowBackgroundP(0.2f)
                .setHeight(ScreenUtils.getScreenWidth(context) * screenWidthPercent)
                .setWidth(ScreenUtils.getScreenWidth(context) * screenWidthPercent)
                .setDialogCancelable(isCancelable)
                .setCancelableOutSide(isCancelableOutside)
                .setSwipeEnable(swipable)
//                .setGravity(Gravity.BOTTOM)
                .setAnimatorCreateListener(new DialogFragment.OnAnimatorCreateListener() {
                    @Override
                    public Animator createInAnimator(View view) {
                        return AnimHelper.createBottomInAnim(view);
                    }

                    @Override
                    public Animator createOutAnimator(View view) {
                        return AnimHelper.createBottomOutAnim(view);
                    }
                })
                .setOnViewCreateListener(new OnViewCreateListener() {
                    @Override
                    public void onViewCreate(final IDialog dialog, View view) {
                        ((TextView) dialog.getView(R.id.contentTextView)).setText(content);
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
