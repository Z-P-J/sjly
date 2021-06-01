package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.shouji.market.R;

public class DialogHeaderLayout extends LinearLayout {

    private final TextView mTvTitle;
    private final ImageButton mBtnClose;

    private CharSequence mTitle;
    private OnClickListener mListener;

    public DialogHeaderLayout(@NonNull Context context) {
        this(context, null);
    }

    public DialogHeaderLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialogHeaderLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.layout_dialog_header, this, true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DialogHeaderLayout);
        mTitle = typedArray.getString(R.styleable.DialogHeaderLayout_dialog_header_title);
        typedArray.recycle();


        mTvTitle = findViewById(R.id.tv_title);
        mTvTitle.setText(mTitle);
        mBtnClose = findViewById(R.id.btn_close);
        mBtnClose.setOnClickListener(mListener);
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        if (mTvTitle != null) {
            mTvTitle.setText(title);
        }
    }

    public void setOnCloseClickListener(OnClickListener listener) {
        mListener = listener;
        if (mBtnClose != null) {
            mBtnClose.setOnClickListener(listener);
        }
    }


}
