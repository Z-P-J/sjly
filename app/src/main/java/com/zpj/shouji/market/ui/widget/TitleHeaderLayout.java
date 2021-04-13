package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zpj.shouji.market.R;
import com.zpj.utils.ScreenUtils;
import com.zpj.skin.SkinEngine;

public class TitleHeaderLayout extends FrameLayout {

    private final TextView tvTitle;
    private final DrawableTintTextView tvMore;

    public TitleHeaderLayout(@NonNull Context context) {
        this(context, null);
    }

    public TitleHeaderLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleHeaderLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleHeaderLayout);
        String title = typedArray.getString(R.styleable.TitleHeaderLayout_title_header_title);
        boolean showMore = typedArray.getBoolean(R.styleable.TitleHeaderLayout_title_header_show_more, false);
        typedArray.recycle();

        tvTitle = new TextView(context);
        tvTitle.setText(title);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setTextSize(16);
        tvTitle.getPaint().setFakeBoldText(true);
        SkinEngine.setTextColor(tvTitle, R.attr.textColorMajor);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        params.leftMargin = ScreenUtils.dp2pxInt(context, 2);
        addView(tvTitle, params);


        View view = new View(context);
        view.setBackgroundResource(R.drawable.bg_button_round_pink);
        params = new LayoutParams(ScreenUtils.dp2pxInt(context, 20), ScreenUtils.dp2pxInt(context, 14));
        params.gravity = Gravity.BOTTOM;
        addView(view, params);

        int primaryColor = ContextCompat.getColor(context, R.color.colorPrimary);
        tvMore = new DrawableTintTextView(context);
        tvMore.setText(R.string.text_more);
        showMoreButton(showMore);
        tvMore.setTextColor(primaryColor);
        tvMore.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_enter_bak), null);
        tvMore.setDrawableTintColor(primaryColor);
        tvMore.getPaint().setFakeBoldText(true);
        tvMore.setGravity(Gravity.CENTER);
        tvMore.setBackground(SkinEngine.getDrawable(context, R.attr.actionBarItemBackground));
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        addView(tvMore, params);


        int padding = ScreenUtils.dp2pxInt(context, 16);
        setPadding(padding, padding, padding, padding);
    }

    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    public void setOnMoreClickListener(OnClickListener listener) {
        showMoreButton(listener != null);
        tvMore.setOnClickListener(listener);
    }

    public void showMoreButton(boolean showMore) {
        tvMore.setVisibility(showMore ? VISIBLE : GONE);
    }



}
