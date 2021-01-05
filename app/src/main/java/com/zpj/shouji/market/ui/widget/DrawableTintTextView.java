package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.zpj.shouji.market.R;

public class DrawableTintTextView extends AppCompatTextView {

    private int drawableTintColor;

    public DrawableTintTextView(Context context) {
        this(context, null);
    }

    public DrawableTintTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableTintTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawableTintTextView);
        drawableTintColor = typedArray.getColor(R.styleable.DrawableTintTextView_drawable_tint_color, Color.TRANSPARENT);
        typedArray.recycle();
        tintDrawables();
    }

//    @Override
//    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
//        super.setCompoundDrawables(left, top, right, bottom);
//        tintDrawables();
//    }
//
//    @Override
//    public void setCompoundDrawablesRelative(@Nullable Drawable start, @Nullable Drawable top, @Nullable Drawable end, @Nullable Drawable bottom) {
//        super.setCompoundDrawablesRelative(start, top, end, bottom);
//        tintDrawables();
//    }
//
//    @Override
//    public void setCompoundDrawablesRelativeWithIntrinsicBounds(int start, int top, int end, int bottom) {
//        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
//    }

    public void setDrawableTintColor(int drawableTintColor) {
        this.drawableTintColor = drawableTintColor;
        tintDrawables();
    }

    private void tintDrawables() {
        tintDrawables(getCompoundDrawablesRelative());
    }

    private void tintDrawables(Drawable[] drawables) {
        if (drawableTintColor != Color.TRANSPARENT) {
            for (int i = 0; i < drawables.length; i++) {
                Drawable drawable = drawables[i];
                if (drawable != null) {
                    final Drawable wrappedDrawable = DrawableCompat.wrap(drawable.mutate());
                    DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(drawableTintColor));
                    drawables[i] = wrappedDrawable;
                }
            }
            super.setCompoundDrawablesRelativeWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], drawables[3]);
        }
    }

}
