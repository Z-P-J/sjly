package com.zpj.fragmentation.dialog.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.animator.PopupAnimator;
import com.zpj.fragmentation.dialog.enums.PopupPosition;
import com.zpj.fragmentation.dialog.widget.PopupDrawerLayout;

public abstract class DrawerDialogFragment extends BaseDialogFragment {


    protected PopupPosition popupPosition = PopupPosition.Left;
    protected PopupDrawerLayout drawerLayout;
    protected FrameLayout drawerContentContainer;

    private boolean enableShadow = true;
    private boolean isDrawStatusBarShadow = false;

    @Override
    protected int getImplLayoutId() {
        return R.layout._dialog_layout_drawer_view;
    }

    protected abstract int getContentLayoutId();

    @Override
    protected PopupAnimator getDialogAnimator(ViewGroup contentView) {
        return null;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerContentContainer = findViewById(R.id.drawerContentContainer);
        View contentView = LayoutInflater.from(getContext()).inflate(getContentLayoutId(), drawerContentContainer, false);
        drawerContentContainer.addView(contentView);


        drawerLayout.enableShadow = enableShadow;
        drawerLayout.isCanClose = cancelableInTouchOutside;
        drawerLayout.setOnCloseListener(new PopupDrawerLayout.OnCloseListener() {
            @Override
            public void onClose() {
                dismiss();
            }

            @Override
            public void onOpen() {

            }

            @Override
            public void onDismissing(float fraction) {
                drawerLayout.isDrawStatusBarShadow = isDrawStatusBarShadow;
            }
        });

        drawerLayout.setDrawerPosition(popupPosition);
        drawerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.close();
            }
        });
    }

    @Override
    public void doShowAnimation() {
        drawerLayout.open();
    }

    @Override
    public void doDismissAnimation() {
        drawerLayout.close();
    }

    public DrawerDialogFragment setEnableShadow(boolean enableShadow) {
        this.enableShadow = enableShadow;
        return this;
    }

    public DrawerDialogFragment setDrawStatusBarShadow(boolean drawStatusBarShadow) {
        isDrawStatusBarShadow = drawStatusBarShadow;
        return this;
    }

    public DrawerDialogFragment setPopupPosition(PopupPosition popupPosition) {
        this.popupPosition = popupPosition;
        return this;
    }
}
