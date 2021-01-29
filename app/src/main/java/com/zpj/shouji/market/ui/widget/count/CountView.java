package com.zpj.shouji.market.ui.widget.count;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.zpj.shouji.market.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunbinqiang on 15/10/2017.
 * github: https://github.com/binqiangsun/IconCountView
 */
public class CountView extends View {

    private static final int PADDING_WIDTH = 12;
    private static final int PADDING_HEIGHT = 40;
    private static final int PADDING_SPACE = 3;
    private final int DEFAULT_TEXT_SIZE = getResources().getDimensionPixelSize(R.dimen.text_size_normal);

    private ValueAnimator mObjectAnimator;
    private float mCurAniValue;    //当前属性动画数值

    private final Rect mRect = new Rect(); // 当前文字的区域
    private final Rect mDigitalRect = new Rect(); // 单个数字的区域

    private final Paint mTextNormalPaint;
    private final Paint mTextSelectedPaint;

    private int textSize = DEFAULT_TEXT_SIZE;




    private long mCurCount; // 当前数量
    private long mNewCount; // 即将变化的数量
    private String mStrNewCount = "0";
    private String mZeroText = "0"; //当数字为0时显示文字
    private final List<String> mCurDigitalList = new ArrayList<>(); // 当前数量各位数字的列表
    private final List<String> mNewDigitalList = new ArrayList<>(); // 即将变化数量各位数字列表
    private boolean mIsSelected; //当前状态是否选中

    public CountView(Context context) {
        this(context, null);
    }

    public CountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextNormalPaint = new Paint();
        mTextSelectedPaint = new Paint();
        mTextNormalPaint.setColor(Color.GRAY);
        mTextNormalPaint.setTextSize(textSize);
        mTextNormalPaint.setStyle(Paint.Style.FILL);
        mTextNormalPaint.setAntiAlias(true);
        mTextSelectedPaint.setColor(Color.GRAY);
        mTextSelectedPaint.setTextSize(textSize);
        mTextSelectedPaint.setStyle(Paint.Style.FILL);
        mTextSelectedPaint.setAntiAlias(true);
        mTextNormalPaint.getTextBounds("0", 0, 1, mDigitalRect);
        initAnimator();
    }

    public void initAnimator() {
        mObjectAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mObjectAnimator.setDuration(500);
        mObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurAniValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        mObjectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //动画结束， 数值更新
                mCurCount = mNewCount;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /**
     * initial mCurCount
     *
     * @param count
     */
    public void setCount(long count) {
        mCurCount = count;
        changeCount(0);
    }

    public long getCount() {
        return mCurCount;
    }

    /**
     * 设置数字为0时的文本
     * @param zeroText
     */
    public void setZeroText(String zeroText) {
        if (zeroText == null) {
            return;
        }
        mZeroText = zeroText;
    }

    public void setTextNormalColor(int normalColor) {
        mTextNormalPaint.setColor(normalColor);
    }

    public void setTextSelectedColor(int selectedColor) {
        mTextSelectedPaint.setColor(selectedColor);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        mTextNormalPaint.setTextSize(textSize);
        mTextSelectedPaint.setTextSize(textSize);
    }

    public void setIsSelected(boolean isSelected) {
        mIsSelected = isSelected;
        postInvalidate();
    }

    /**
     * +1
     */
    public void addCount() {
        changeCount(1);
    }

    /**
     * -1
     */
    public void minusCount() {
        changeCount(-1);
    }

    public void changeCount(long change) {
        //先结束当前动画
        if (mObjectAnimator != null && mObjectAnimator.isRunning()) {
            mObjectAnimator.end();
        }
        this.mNewCount = mCurCount + change;
        toDigitals(mCurCount, mCurDigitalList);
        toDigitals(mNewCount, mNewDigitalList);
        if (mNewCount > 0) {
            mStrNewCount = String.valueOf(mNewCount);
        } else {
            mStrNewCount = mZeroText;
        }
        if (mNewCount != mCurCount) {
            if (mObjectAnimator != null) {
                mObjectAnimator.start();
            }
        } else {
            requestLayout();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float initX = 0.0f;
        float initY = PADDING_HEIGHT + mDigitalRect.height();
        float unitX = mDigitalRect.width() + PADDING_SPACE;
        float curAniValue = mCurAniValue;

        int len = mNewDigitalList.size();
        for (int i = 0; i < len; i++) {
            String newDigital = mNewDigitalList.get(i);
            String oldDigital = "";
            if (mCurDigitalList.size() > i) {
                oldDigital = mCurDigitalList.get(i);
            }
            float x = unitX * i + initX;
            if (newDigital.equals(oldDigital)) {
                //只绘制新的数字
                canvas.drawText(newDigital, x, initY, getCurPaint(mIsSelected));
            } else if (mNewCount > mCurCount) {
                //旧数字消失动画
                if (!TextUtils.isEmpty(oldDigital)) {
                    drawOut(canvas, oldDigital, x, initY - (curAniValue * PADDING_HEIGHT), mIsSelected);
                }
                //新数字进入动画绘制
                drawIn(canvas, newDigital, x, initY + (PADDING_HEIGHT - curAniValue * PADDING_HEIGHT), mIsSelected);
            } else {
                if (!TextUtils.isEmpty(oldDigital)) {
                    drawOut(canvas, oldDigital, x, initY + (curAniValue * PADDING_HEIGHT), mIsSelected);
                }
                drawIn(canvas, newDigital, x, initY - (PADDING_HEIGHT - curAniValue * PADDING_HEIGHT), mIsSelected);
            }
        }

    }

    /**
     * @param canvas
     * @param digital
     * @param x
     * @param y
     */
    public void drawIn(Canvas canvas, String digital, float x, float y, boolean isSelected) {
        Paint inPaint = getCurPaint(isSelected);
        inPaint.setAlpha((int) (mCurAniValue * 255));
        inPaint.setTextSize(textSize * (mCurAniValue * 0.5f + 0.5f));
        canvas.drawText(digital, x, y, inPaint);
        inPaint.setAlpha(255);
        inPaint.setTextSize(textSize);
    }

    /**
     * @param canvas
     * @param digital
     * @param x
     * @param y
     */
    public void drawOut(Canvas canvas, String digital, float x, float y, boolean isSelected) {
        Paint outPaint = getCurPaint(!isSelected);
        outPaint.setAlpha(255 - (int) (mCurAniValue * 255));
        outPaint.setTextSize(textSize * (1.0f - mCurAniValue * 0.5f));
        canvas.drawText(digital, x, y, outPaint);
        outPaint.setAlpha(255);
        outPaint.setTextSize(textSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mRect.setEmpty();
        mTextNormalPaint.getTextBounds(getText(), 0, getText().length(), mRect);
        int textWidth = mRect.width() + PADDING_WIDTH * 2;
        int textHeight = mRect.height() + PADDING_HEIGHT * 2;
        final int dw = resolveSizeAndState(textWidth, widthMeasureSpec, 0);
        final int dh = resolveSizeAndState(textHeight, heightMeasureSpec, 0);
        setMeasuredDimension(dw, dh);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mObjectAnimator.end();
    }

    /**
     * 数字转为字符串列表
     * @param num
     * @param digitalList
     */
    private void toDigitals(long num, List<String> digitalList) {
        digitalList.clear();
        if (num == 0) {
            digitalList.add(mZeroText);
        }
        while (num > 0) {
            digitalList.add(0, String.valueOf(num % 10));
            num = num / 10;
        }
    }



    private Paint getCurPaint(boolean isSelected) {
        return isSelected ? mTextSelectedPaint : mTextNormalPaint;
    }

    public String getText() {
        return mStrNewCount;
    }

}
