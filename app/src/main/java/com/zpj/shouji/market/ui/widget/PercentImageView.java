package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.zpj.shouji.market.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/9/18
 */
public class PercentImageView extends AppCompatImageView {

    public static final int BASICS_WIDTH = 0;
    public static final int BASICS_HEIGHT = 1;

    @Basics
    private int mBasics;
    private float mPercent;
    private int mWidthMeasureSize;
    private int mHeightMeasureSize;

    public PercentImageView(Context context) {
        this(context, null);
    }

    public PercentImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercentImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PercentImageView);
        mBasics = typedArray.getInt(R.styleable.PercentImageView_piv_basics, BASICS_WIDTH);
        mPercent = typedArray.getFloat(R.styleable.PercentImageView_piv_percent, 1.0F);
        typedArray.recycle();
    }

    public void setBasics(int basics) {
        if (mBasics == basics){
            return;
        }
        mBasics = basics;
        resetNewSize();
    }

    public void setPercent(float percent) {
        if (mPercent == percent){
            return;
        }
        mPercent = percent;
        resetNewSize();
    }

    public void setPercent(@Basics int basics, float percent) {
        if (mBasics == basics && mPercent == percent){
            return;
        }
        mBasics = basics;
        mPercent = percent;
        resetNewSize();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
        mHeightMeasureSize = MeasureSpec.getSize(heightMeasureSpec);
        int[] size = calculateNewSize();
        setMeasuredDimension(size[0], size[1]);
    }

    private int[] calculateNewSize(){
        int[] size = new int[]{mWidthMeasureSize, mHeightMeasureSize};
        if (mBasics == BASICS_WIDTH){
            size[1] = (int) (mWidthMeasureSize * mPercent);
        } else if (mBasics == BASICS_HEIGHT){
            size[0] = (int) (mHeightMeasureSize * mPercent);
        }
        return size;
    }

    private void resetNewSize(){
        int[] size = calculateNewSize();
        getLayoutParams().width = size[0];
        getLayoutParams().height = size[1];
        requestLayout();
    }

    @IntDef({BASICS_WIDTH, BASICS_HEIGHT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Basics {
    }
}
