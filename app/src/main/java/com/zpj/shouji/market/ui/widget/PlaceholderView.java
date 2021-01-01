package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.zpj.utils.ScreenUtils;

public class PlaceholderView extends FrameLayout {

    private final int height;

    public PlaceholderView(Context context) {
        this(context, null);
    }

    public PlaceholderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlaceholderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        height = ScreenUtils.getStatusBarHeight(context) + ScreenUtils.dp2pxInt(context, 56);
        setMinimumHeight(height);
//        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, height);
//    }
}
