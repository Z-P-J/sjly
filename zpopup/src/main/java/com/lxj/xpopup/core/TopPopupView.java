package com.lxj.xpopup.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.animator.ScaleAlphaAnimator;
import com.lxj.xpopup.animator.TranslateAlphaAnimator;
import com.lxj.xpopup.enums.PopupStatus;
import com.lxj.xpopup.util.KeyboardUtils;
import com.lxj.xpopup.util.XPopupUtils;
import com.lxj.xpopup.widget.SmartDragLayout;
import com.zpj.popupmenuview.R;

import static com.lxj.xpopup.enums.PopupAnimation.ScaleAlphaFromCenter;
import static com.lxj.xpopup.enums.PopupAnimation.TranslateAlphaFromTop;

/**
 * Description: 在底部显示的Popup
 * Create by lxj, at 2018/12/11
 */
public class TopPopupView extends BasePopupView {

    protected FrameLayout topPopupContainer;

    public TopPopupView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getPopupLayoutId() {
        return R.layout._xpopup_top_popup_view;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        topPopupContainer = findViewById(R.id.topPopupContainer);
        View contentView = LayoutInflater.from(getContext()).inflate(getImplLayoutId(), topPopupContainer, false);
        LayoutParams params = (LayoutParams) contentView.getLayoutParams();
        params.gravity = Gravity.TOP;
        topPopupContainer.addView(contentView);

        getPopupImplView().setTranslationX(popupInfo.offsetX);
        getPopupImplView().setTranslationY(popupInfo.offsetY);

        XPopupUtils.applyPopupSize((ViewGroup) getPopupContentView(), getMaxWidth(), getMaxHeight());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setTranslationY(0);
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
        return popupInfo.maxWidth==0 ? XPopupUtils.getWindowWidth(getContext())
                : popupInfo.maxWidth;
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return new TranslateAlphaAnimator(getPopupContentView(), TranslateAlphaFromTop);
    }
}
