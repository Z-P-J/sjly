package com.zpj.zdialog.base;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.zpj.utils.ScreenUtils;

public class ZAbstractDialog<T extends ZAbstractDialog<T>> extends DialogFragment implements IDialog {

    FragmentManager fragmentManager;
    int layoutRes;
    int dialogWidth;
    int dialogHeight;
    float dimAmount = 0.2f;
    public int gravity = Gravity.CENTER;

    View contentView;
    FragmentActivity activity;
    private OnViewCreateListener onViewCreateListener;
    private OnDismissListener onDismissListener;
    private OnCancelListener onCancelListener;
    private OnDialogStartListener onDialogStartListener;
    private static final String FTag = "dialogTag";

    public static ZAbstractDialog with(Context context) {
        ZAbstractDialog dialog = new ZAbstractDialog();
        FragmentActivity activity;
        if (context instanceof FragmentActivity) {
            activity = (FragmentActivity) context;
        } else {
            activity = ((FragmentActivity) ((ContextWrapper) context).getBaseContext());
        }
        dialog.setFragmentActivity(activity);
        return dialog;
    }

    public ZAbstractDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if (getLayoutRes() > 0) {
            //调用方通过xml获取view
            view = inflater.inflate(getLayoutRes(), container, false);
            contentView = view;
        } else if (getContentView() != null) {
            //调用方直接传入view
            view = getContentView();
        } else {
            view =  super.onCreateView(inflater, container, savedInstanceState);
//            throw new RuntimeException("You must call the setContentView");
            contentView = view;
        }
        return view;
    }

    protected int getLayoutRes() {
        return layoutRes;
    }

    protected View getContentView() {
        return contentView;
    }

    protected int getDialogWidth() {
        return dialogWidth;
    }

    protected int getDialogHeight() {
        return dialogHeight;
    }

    public float getDimAmount() {
        return dimAmount;
    }

    protected int getGravity() {
        return gravity;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置默认子View布局
//        contentView = view;
        //回调给调用者，用来设置子View及点击事件等
        if (onViewCreateListener != null) {
            onViewCreateListener.onViewCreate(this, view);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        onDialogStart();
    }

    protected void onDialogStart() {
        Window window = getDialog().getWindow();
        if (window == null) {
            return;
        }
        //设置背景色透明
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        //设置Dialog动画效果
//        if (getAnimRes() > 0) {
//            window.setWindowAnimations(getAnimRes());
//        }
        WindowManager.LayoutParams params = window.getAttributes();
        //设置Dialog的Width
        if (getDialogWidth() > 0) {
            params.width = getDialogWidth();
        } else {
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        //设置Dialog的Height
        if (getDialogHeight() > 0) {
            params.height = getDialogHeight();
        } else {
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        //设置屏幕透明度 0.0f~1.0f(完全透明~完全不透明)
        params.dimAmount = getDimAmount();
        params.gravity = getGravity();
        window.setAttributes(params);
        if (onDialogStartListener != null) {
            onDialogStartListener.onStart();
        }
    }
    
    private T self() {
        return (T) this;
    }

    protected void setFragmentActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    public T setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        return self();
    }

    /**
     * 设置DialogView
     *
     * @param layoutRes 布局文件
     * @return Builder
     */
    public T setContentView(@LayoutRes int layoutRes) {
        this.layoutRes = layoutRes;
        return self();
    }

    /**
     * 设置DialogView
     *
     * @param dialogView View
     * @return Builder
     */
    public T setContentView(View dialogView) {
        this.contentView = dialogView;
        return self();
    }


    public T setSwipeEnable(boolean swipeable) {
        setSwipeable(swipeable);
        return self();
    }

    /**
     * 设置屏幕宽度百分比
     *
     * @param percentage 0.0f~1.0f
     * @return Builder
     */
    public T setScreenWidthP(float percentage) {
        this.dialogWidth = (int) (ScreenUtils.getScreenWidth(activity) * percentage);
        return self();
    }

    /**
     * 设置屏幕高度百分比
     *
     * @param percentage 0.0f~1.0f
     * @return Builder
     */
    public T setScreenHeightP(float percentage) {
        this.dialogHeight = (int) (ScreenUtils.getScreenHeight(activity) * percentage);
        return self();
    }

    /**
     * 设置Dialog的宽度
     *
     * @param width 宽度
     * @return Builder
     */
    public T setWidth(float width) {
        this.dialogWidth = (int) width;
        return self();
    }

    /**
     * 设置Dialog的高度
     *
     * @param height 高度
     * @return Builder
     */
    public T setHeight(float height) {
        this.dialogHeight = (int) height;
        return self();
    }

    /**
     * 设置背景色色值
     *
     * @param percentage 0.0f~1.0f 1.0f为完全不透明
     * @return Builder
     */
    public T setWindowBackgroundP(float percentage) {
        this.dimAmount = percentage;
        return self();
    }

    /**
     * 设置Gravity
     *
     * @param gravity Gravity
     * @return Builder
     */
    public T setGravity(int gravity) {
        this.gravity = gravity;
        return self();
    }

    /**
     * 设置dialog外点击是否可以让dialog消失
     *
     * @param cancelableOutSide true 则在dialog屏幕外点击可以使dialog消失
     * @return Builder
     */
    public T setCancelableOutSide(boolean cancelableOutSide) {
        setCanceledOnTouchOutside(cancelableOutSide);
        return self();
    }

    /**
     * 设置是否屏蔽物理返回键
     *
     * @param cancelable true 点击物理返回键可以让dialog消失；反之不消失
     * @return Builder
     */
    public T setDialogCancelable(boolean cancelable) {
        setCancelable(cancelable);
        return self();
    }

    /**
     * 构建子View的listener
     *
     * @param listener IDialog.OnViewCreateListener
     * @return Builder
     */
    public T setOnViewCreateListener(OnViewCreateListener listener) {
        this.onViewCreateListener = listener;
        return self();
    }

    public T setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return self();
    }

    public T setOnCancleListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return self();
    }

    public T setOnDialogStartListener(OnDialogStartListener onDialogStartListener) {
        this.onDialogStartListener = onDialogStartListener;
        return self();
    }

    public T setAnimatorCreateListener(OnAnimatorCreateListener onAnimatorCreateListener) {
        setOnAnimatorCreateListener(onAnimatorCreateListener);
        return self();
    }

    public T show() {
        if (getDialog() != null) {
            getDialog().show();
            return self();
        }
//        if (layoutRes <= 0 && contentView == null) {
//            //如果没有设置布局 提供默认设置
//            setDefaultOption();
//        }
        if (fragmentManager == null) {
            fragmentManager = activity.getSupportFragmentManager();
        }
        show(fragmentManager, FTag);
        return self();
    }

    @Override
    public void onBeginDismiss() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(this);
        }
    }

    @Override
    public void onBeginCancel() {
        if (onCancelListener != null) {
            onCancelListener.onCancel(this);
        }
    }

    /**
     * 移除之前的dialog
     */
    private void removePreDialog() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(FTag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    public <S extends View> S getView(int id) {
        return contentView.findViewById(id);
    }
}
