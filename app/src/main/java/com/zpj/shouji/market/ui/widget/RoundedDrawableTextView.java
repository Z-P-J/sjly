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

public class RoundedDrawableTextView extends AppCompatTextView {

    private int backgroundColor = Color.BLACK;


    public RoundedDrawableTextView(Context context) {
        this(context, null);
    }

    public RoundedDrawableTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedDrawableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundTagTextView);
            backgroundColor = ta.getColor(R.styleable.RoundTagTextView_tag_background_tint_color, Color.BLACK);
            ta.recycle();
        }
        Drawable drawable = context.getResources().getDrawable(R.drawable.bg_button_round_green);
        tintDrawables(drawable);
    }

    public void setTintColor(int color) {
        this.backgroundColor = color;
        tintDrawables(getBackground());
    }

    private void tintDrawables(Drawable drawable) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(backgroundColor));
        setBackground(wrappedDrawable);
    }



}
