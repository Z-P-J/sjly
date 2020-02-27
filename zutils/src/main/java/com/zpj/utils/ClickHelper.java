package com.zpj.utils;

import android.view.MotionEvent;
import android.view.View;

public final class ClickHelper implements View.OnTouchListener, View.OnClickListener, View.OnLongClickListener {

    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;
    private final View view;

    private float lastX;
    private float lastY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = event.getRawX();
            lastY = event.getRawY();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            onClickListener.onClick(v, lastX, lastY);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (onLongClickListener != null) {
            return onLongClickListener.onLongClick(v, lastX, lastY);
        }
        return false;
    }

    public interface OnClickListener {
        void onClick(View v, float x, float y);
    }

    public interface OnLongClickListener{
        boolean onLongClick(View v, float x, float y);
    }

    private ClickHelper(View view) {
        this.view = view;
        view.setOnTouchListener(this);
    }

    public static ClickHelper with(View view) {
        return new ClickHelper(view);
    }

    public ClickHelper setOnClickListener(OnClickListener onClickListener) {
        view.setOnClickListener(this);
        this.onClickListener = onClickListener;
        return this;
    }

    public ClickHelper setOnLongClickListener(OnLongClickListener onLongClickListener) {
        view.setOnLongClickListener(this);
        this.onLongClickListener = onLongClickListener;
        return this;
    }
}
