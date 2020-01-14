package com.zpj.zdialog.base;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * @author Z-P-J
 * @date 2019/5/16 21:36
 */
public class OutsideClickDialog extends Dialog {

    public interface OnTouchOutsideListener {
        void onTouchOutside();
    }

    protected OnTouchOutsideListener onTouchOutsideListener;

    public OutsideClickDialog(@NonNull Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    public OutsideClickDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    protected OutsideClickDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }


    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        //点击弹窗外部区域
        if (isOutOfBounds(getContext(), event) && onTouchOutsideListener != null) {
            onTouchOutsideListener.onTouchOutside();
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void setOnTouchOutsideListener(OnTouchOutsideListener onTouchOutsideListener) {
        this.onTouchOutsideListener = onTouchOutsideListener;
    }

    private boolean isOutOfBounds(Context context, MotionEvent event) {
        //相对弹窗左上角的x坐标
        final int x = (int) event.getX();
        //相对弹窗左上角的y坐标
        final int y = (int) event.getY();
        //最小识别距离
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        //弹窗的根View
        final View decorView = getWindow().getDecorView();
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop))
                || (y > (decorView.getHeight() + slop));
    }

}
