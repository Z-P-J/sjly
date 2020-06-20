package com.zpj.popup.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpj.popup.animator.PopupAnimator;
import com.zpj.popup.animator.ScaleAlphaAnimator;
import com.zpj.popup.impl.LoadingPopup;
import com.zpj.popup.util.XPopupUtils;
import com.zpj.popup.R;

import static com.zpj.popup.enums.PopupAnimation.ScaleAlphaFromCenter;

/**
 * Description: 在中间显示的Popup
 * Create by dance, at 2018/12/8
 */
public class CenterPopup<T extends CenterPopup> extends BasePopup<T> {
    protected CardView centerPopupContainer;
    protected int bindLayoutId;
    protected int bindItemLayoutId;
    private View contentView;

    public CenterPopup(@NonNull Context context) {
        super(context);
        centerPopupContainer = findViewById(R.id.centerPopupContainer);
    }

    @Override
    protected int getPopupLayoutId() {
        return R.layout._xpopup_center_popup_view;
    }

    public T setContentView(View contentView) {
        this.contentView = contentView;
        return self();
    }

    public T bindLayout(int layoutId){
        bindLayoutId = layoutId;
        return (T) this;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        View contentView = getContentView();
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        LayoutParams params;
        if (layoutParams == null) {
            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            params = (LayoutParams) layoutParams;
        }
        params.gravity = Gravity.CENTER;
        centerPopupContainer.addView(contentView, params);
        getPopupContentView().setTranslationX(popupInfo.offsetX);
        getPopupContentView().setTranslationY(popupInfo.offsetY);
        XPopupUtils.applyPopupSize((ViewGroup) getPopupContentView(), getMaxWidth(), getMaxHeight());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setTranslationY(0);
    }

    protected View getContentView() {
        if (contentView != null) {
            return contentView;
        }
        return LayoutInflater.from(getContext()).inflate(getImplLayoutId(), centerPopupContainer, false);
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
        return popupInfo.maxWidth==0 ? (int) (XPopupUtils.getWindowWidth(getContext()) * 0.86f)
                : popupInfo.maxWidth;
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return new ScaleAlphaAnimator(getPopupContentView(), ScaleAlphaFromCenter);
    }
}
