package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.shouji.market.R;

public class MenuActionButton extends LinearLayout {

    private final FloatingActionButton actionButton;
    private final TextView tvMenuText;

    public MenuActionButton(Context context) {
        this(context, null);
    }

    public MenuActionButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuActionButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.item_icon,this, true);

        actionButton = findViewById(R.id.btn_action);
        tvMenuText = findViewById(R.id.tv_menu_text);


        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MenuActionButton);
            tvMenuText.setText(ta.getString(R.styleable.MenuActionButton_menu_action_title));
            actionButton.setImageDrawable(ta.getDrawable(R.styleable.MenuActionButton_menu_action_icon));
            int color = ta.getColor(R.styleable.MenuActionButton_menu_action_color, Color.BLACK);
            tvMenuText.setTextColor(color);
            actionButton.setBackgroundTintList(ColorStateList.valueOf(color));
            ta.recycle();
        }

    }

}
