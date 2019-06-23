package com.qyh.qtablayoutlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;


public class QTabItem extends View {
    public final CharSequence mText;
    public final Drawable mIcon;
    public final int mCustomLayout;

    public QTabItem(Context context) {
        this(context, null);
    }

    public QTabItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.QTabItem, 0, 0);
        mText = a.getText(R.styleable.QTabItem_android_text);
        mIcon = a.getDrawable(R.styleable.QTabItem_android_icon);
        mCustomLayout = a.getResourceId(R.styleable.QTabItem_android_layout, 0);
        a.recycle();
    }
}
