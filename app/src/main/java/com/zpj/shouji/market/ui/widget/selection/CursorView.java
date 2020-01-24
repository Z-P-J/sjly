package com.zpj.shouji.market.ui.widget.selection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.zpj.shouji.market.R;

/**
 * Created by wangyang53 on 2018/3/26.
 */

public class CursorView extends View {
    private boolean isLeft;
    private Paint mPaint;
    private final static int CIRCLE_RADIUS = 36;
    private final static int PADDING = 2;
    private OnCursorTouchListener listener;

    public CursorView(Context context, boolean isLeft) {
        super(context);
        this.isLeft = isLeft;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.colorPrimary));
        setLayoutParams(new ViewGroup.LayoutParams(getFixWidth(), getFixHeight()));
        setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(CIRCLE_RADIUS + PADDING, CIRCLE_RADIUS, CIRCLE_RADIUS, mPaint);
        if (isLeft) {
            canvas.drawRect(CIRCLE_RADIUS + PADDING, 0, CIRCLE_RADIUS * 2 + PADDING, CIRCLE_RADIUS, mPaint);
        } else {
            canvas.drawRect(PADDING, 0, CIRCLE_RADIUS + PADDING, CIRCLE_RADIUS, mPaint);
        }
    }


    public void setOnCursorTouchListener(final OnCursorTouchListener l) {
        this.listener = l;
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return listener.onCursorTouch(isLeft, view, motionEvent);
            }
        });
    }

    public interface OnCursorTouchListener {
        boolean onCursorTouch(boolean isLeft, View view, MotionEvent event);
    }

    public static int getFixWidth() {
        return CIRCLE_RADIUS * 2 + PADDING * 2;
    }

    public static int getFixHeight() {
        return CIRCLE_RADIUS * 2 + PADDING / 2;
    }
}
