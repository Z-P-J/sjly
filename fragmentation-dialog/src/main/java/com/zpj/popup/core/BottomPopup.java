package com.zpj.popup.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpj.popup.XPopup;
import com.zpj.popup.animator.PopupAnimator;
import com.zpj.popup.enums.PopupStatus;
import com.zpj.popup.util.KeyboardUtils;
import com.zpj.popup.util.XPopupUtils;
import com.zpj.popup.widget.SmartDragLayout;
import com.zpj.popup.R;

/**
 * Description: 在底部显示的Popup
 * Create by lxj, at 2018/12/11
 */
public class BottomPopup<T extends BottomPopup> extends BasePopup<T> {

    protected SmartDragLayout bottomPopupContainer;
    public BottomPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getPopupLayoutId() {
        return R.layout._xpopup_bottom_popup_view;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        bottomPopupContainer = findViewById(R.id.bottomPopupContainer);
        View contentView = LayoutInflater.from(getContext()).inflate(getImplLayoutId(), bottomPopupContainer, false);
        bottomPopupContainer.addView(contentView);
        bottomPopupContainer.enableDrag(popupInfo.enableDrag);
        bottomPopupContainer.dismissOnTouchOutside(popupInfo.isDismissOnTouchOutside);
        bottomPopupContainer.handleTouchOutsideEvent(popupInfo.handleTouchOutsideEvent);
        bottomPopupContainer.hasShadowBg(popupInfo.hasShadowBg);

        getPopupImplView().setTranslationX(popupInfo.offsetX);
        getPopupImplView().setTranslationY(popupInfo.offsetY);

        XPopupUtils.applyPopupSize((ViewGroup) getPopupContentView(), getMaxWidth(), getMaxHeight());

        if (popupInfo.isDismissOnTouchOutside) {
            bottomPopupContainer.setOnCloseListener(new SmartDragLayout.OnCloseListener() {
                @Override
                public void onClose() {
                    doAfterDismiss();
                }
                @Override
                public void onOpen() {
                    BottomPopup.super.doAfterShow();
                }
            });

            bottomPopupContainer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }


    @Override
    protected void doAfterShow() {
        if(popupInfo.enableDrag){
            //do nothing self.
        }else {
            super.doAfterShow();
        }
    }

    @Override
    public void doShowAnimation() {
        if (popupInfo.enableDrag) {
            bottomPopupContainer.open();
        } else {
            super.doShowAnimation();
        }
    }

    @Override
    public void doDismissAnimation() {
        if (popupInfo.enableDrag) {
            bottomPopupContainer.close();
        } else {
            super.doDismissAnimation();
        }
    }

    /**
     * 动画是跟随手势发生的，所以不需要额外的动画器，因此动画时间也清零
     *
     * @return
     */
    @Override
    public int getAnimationDuration() {
        return popupInfo.enableDrag ? 0 : super.getAnimationDuration();
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        // 移除默认的动画器
        return popupInfo.enableDrag ? null : super.getPopupAnimator();
    }

    @Override
    public void dismiss() {
        if (popupInfo.enableDrag) {
            if (popupStatus == PopupStatus.Dismissing) return;
            popupStatus = PopupStatus.Dismissing;
            if (popupInfo.autoOpenSoftInput) KeyboardUtils.hideSoftInput(this);
            clearFocus();
            // 关闭Drawer，由于Drawer注册了关闭监听，会自动调用dismiss
            bottomPopupContainer.close();
            postDelayed(this::onDismiss, XPopup.getAnimationDuration());
        } else {
            super.dismiss();
        }
    }

    /**
     * 具体实现的类的布局
     *
     * @return
     */
    protected int getImplLayoutId() {
        return 0;
    }

    protected int getMaxWidth() {
        return popupInfo.maxWidth == 0 ? XPopupUtils.getWindowWidth(getContext())
                : popupInfo.maxWidth;
    }

    @Override
    protected View getTargetSizeView() {
        return getPopupImplView();
    }

}
