package com.zpj.shouji.market.ui.widget.count;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.zpj.shouji.market.R;

public class GoodView extends SkinIconCountView {

    public GoodView(Context context) {
        this(context, null);
    }

    public GoodView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoodView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCountView.setZeroText("赞");
        mCountView.setTextSelectedColor(Color.RED);
        setIconRes(R.drawable.ic_good, R.drawable.ic_good_checked);
        initColor();
    }

}
