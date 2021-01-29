package com.zpj.shouji.market.ui.widget.count;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.EventBus;
import com.zxy.skin.sdk.SkinEngine;

import io.reactivex.functions.Consumer;

public class SkinIconCountView extends IconCountView {

    public SkinIconCountView(Context context) {
        this(context, null);
    }

    public SkinIconCountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinIconCountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        EventBus.onSkinChangeEvent(this, s -> initColor());
        initColor();
    }

    @Override
    public void setState(boolean isSelected) {
        super.setState(isSelected);
        initColor();
    }

    protected void initColor() {
        int color = SkinEngine.getColor(getContext(), R.attr.textColorNormal);
        mCountView.setTextNormalColor(color);
        if (mIsSelected) {
            mImageView.setColorFilter(textSelectedColor);
        } else {
            mImageView.setColorFilter(color);
        }
        mCountView.requestLayout();
    }

}
