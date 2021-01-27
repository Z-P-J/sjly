package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.utils.ScreenUtils;

/**
 * Created by lzj on 2019/12/31
 * Describe ：字母排序索引
 * 来源：https://github.com/lzjin/SideBarView/
 * modify by: Z-P-J
 */
public class LetterSortSideBar extends View {

    public static String[] mList = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    private Canvas mCanvas;
    private int mSelectIndex = 0;
    private float mTextSize;
    private int mTextColor;
    private float mTextSizeChoose;
    private int mTextColorChoose;

    public Paint paint = new Paint();

    private OnIndexChangedListener mClickListener;

    public LetterSortSideBar(Context context) {
        this(context, null);
    }

    public LetterSortSideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LetterSortSideBar);
            mTextColor = ta.getColor(R.styleable.LetterSortSideBar_sidebarUnSelectTextColor, Color.LTGRAY);
            mTextColorChoose = ta.getColor(R.styleable.LetterSortSideBar_sidebarSelectTextColor, context.getResources().getColor(R.color.colorPrimary));
            mTextSize = ta.getDimension(R.styleable.LetterSortSideBar_sidebarUnSelectTextSize, ScreenUtils.dp2px(context, 10));
            mTextSizeChoose = ta.getDimension(R.styleable.LetterSortSideBar_sidebarSelectTextSize, ScreenUtils.dp2px(context, 12));
            ta.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mCanvas = canvas;
        paintText();
    }

    private void paintText() {
        //计算每一个字母的高度,总告诉除以字母集合的高度就可以
        int height = (getHeight()) / mList.length;
        for (int i = 0; i < mList.length; i++) {
            if (i == mSelectIndex) {
                paint.setColor(mTextColorChoose);
                paint.setTextSize(mTextSizeChoose);
            } else {
                paint.setColor(mTextColor);
                paint.setTextSize(mTextSize);
            }
            paint.setAntiAlias(true);//设置抗锯齿
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            //计算每一个字母x轴
            float paintX = getWidth() / 2F - paint.measureText(mList[i]) / 2;
            //计算每一个字母Y轴
            int paintY = height * i + height;
            //绘画出来这个TextView
            mCanvas.drawText(mList[i], paintX, paintY, paint);
            //画完一个以后重置画笔
            paint.reset();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int index = (int) (event.getY() / getHeight() * mList.length);
                if (index >= 0 && index < mList.length) {
                    if (mClickListener != null) {
                        mClickListener.onSideBarScrollUpdateItem(mList[index]);
                    }
                    mSelectIndex = index;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mClickListener != null) {
                    mClickListener.onSideBarScrollEndHideText();
                }
                break;
        }
        return true;
    }

    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public void setTextSizeChoose(float mTextSizeChoose) {
        this.mTextSizeChoose = mTextSizeChoose;
    }

    public void setTextColorChoose(int mTextColorChoose) {
        this.mTextColorChoose = mTextColorChoose;
    }

    public interface OnIndexChangedListener {
        void onSideBarScrollUpdateItem(String word); //滚动位置

        void onSideBarScrollEndHideText(); //隐藏提示文本
    }

    public void setIndexChangedListener(OnIndexChangedListener listener) {
        this.mClickListener = listener;
    }

    public void onItemScrollUpdateText(String word) {
        for (int i = 0; i < mList.length; i++) {
            if (mList[i].equals(word) && mSelectIndex != i) {
                mSelectIndex = i;
                invalidate();
            }
        }
    }

}
