package tech.summerly.likewidget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.Arrays;

/**
 * author : Summer
 * date   : 2017/10/13
 */

public class SlidingNumberView extends View {

    private static final boolean DEBUG = false;

    private static final String TAG = "SlidingNumberView";

    private TextPaint textPaint;

    private int number;

    private int[] numbers;

    private int[] offsets;

    private int[] offsetsMax;

    private final static String[] DIGITS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    private StaticLayout[] digitStaticLayouts = new StaticLayout[10];

    private boolean isLayoutAvailable = false;

    private int startX;

    private int startY;

    public SlidingNumberView(Context context) {
        this(context, null);
    }

    public SlidingNumberView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        textPaint = new TextPaint();
        textPaint.setTextSize(48);
        textPaint.setAntiAlias(true);
        setNumber(100);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                buildLayout();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        ensureNumberInCenter();
        invalidate();
    }

    private void buildLayout() {
        //初始化0-9个数字的StaticLayout.（TODO:只需要初始化一次就行了。
        for (int i = 0; i < digitStaticLayouts.length; i++) {
            digitStaticLayouts[i] = new StaticLayout(DIGITS[i], textPaint, (int) textPaint.measureText(DIGITS[i]),
                    Layout.Alignment.ALIGN_CENTER, 1f, 0, false);
        }
        isLayoutAvailable = true;
    }

    /**
     * 确保数字居中显示
     */
    private void ensureNumberInCenter() {
        //计算数字的宽度和高度，以便居中显示
        if (!isLayoutAvailable) {
            buildLayout();
            ensureNumberInCenter();
            return;
        }
        int totalNumberWidth = 0;
        for (int i : numbers) {
            totalNumberWidth += getDigitStaticLayout(i).getWidth();
        }
        startY = getHeight() / 2 - getDigitStaticLayout(0).getHeight() / 2;
        startX = (getWidth() - totalNumberWidth) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isLayoutAvailable) {
            buildLayout();
            return;
        }
        canvas.translate(startX, startY);
        int offsetX = 0;
        for (int i = 0; i < numbers.length; i++) {
            drawDigitColumn(canvas, i, offsetX);
            offsetX += getDigitStaticLayout(0).getWidth();
        }
    }

    /**
     * 把数字分为多列
     * 绘制出指定一列的数字
     *
     * @param i       数字在 [numbers] 中的序号
     * @param offsetX 该数字绘制时的起始横坐标
     */
    private void drawDigitColumn(Canvas canvas, int i, final int offsetX) {
        if (DEBUG) {
            Log.i(TAG, "开始绘制第 " + i + " 列数字");
        }
        //当前需要绘制的数字
        final int currentDigit = numbers[i];
        //找到目的数字的偏移
        int offsetY = offsets[i];
        if (offsetY > 0) {
            //如果需要绘制的数字的偏移大于0
            //则表明此时的动画状态在处于将目的数字向下滚动，如将0向下滚动，替换掉当前显示的1

            //由于数字滚动的距离可能会跨越其他数字，比如 0 向下滚动替换掉 8，所以需要一个循环
            int n = currentDigit;
            while (true) {
                int height = drawDigit(canvas, n, offsetX, offsetY);
                offsetY -= height;
                if (offsetY < -height) {
                    break;
                }
                n = getPreviousDigit(n);
            }

        } else {
            //表示需要向上滚动
            int n = currentDigit;
            while (true) {
                int height = drawDigit(canvas, n, offsetX, offsetY);
                offsetY += height;
                if (offsetY >= height) {
                    break;
                }
                n = getNextDigit(n);
            }
        }

    }

    /**
     * 在 canvas 偏移 offsetX 和 offsetY 处绘制数字 number
     *
     * @return 所绘制数字的像素高度
     */
    private int drawDigit(Canvas canvas, int number, int offsetX, int offsetY) {
        canvas.save();
        canvas.translate(offsetX, offsetY);
        final StaticLayout staticLayout = getDigitStaticLayout(number);
        //计算数字绘制时的透明度
        final int alpha = calculateDigitAlpha(offsetY, staticLayout.getHeight());
        if (DEBUG) {
            Log.i(TAG, "drawDigit: 绘制数字 ：" + number + " alpha = " + alpha);
        }
        if (alpha > 0) {
            textPaint.setAlpha(alpha);
            staticLayout.draw(canvas);
        }
        canvas.restore();
        return staticLayout.getHeight();
    }


    /**
     * 根据数字的像素高度和纵坐标的偏移量来计算数字绘制的透明度
     *
     * @param offsetY 偏移正确位置的偏移量
     *                为 0 时表示在正确位置，不透明。
     *                当 |offsetY| >= height 时，表示离正确位置过远，alpha为 0
     *                中间取值为线性过渡
     * @param height  数字的高度
     * @return 透明度，取值范围为 [0,255]
     */
    private int calculateDigitAlpha(final int offsetY, final int height) {
        final int abs = Math.abs(offsetY);
        if (abs > height) {
            return 0;
        }
        return (int) ((1f - ((float) abs) / height) * 255);
    }

    public void setNumber(int number) {
        this.number = number;
        String numberText = String.valueOf(number);
        numbers = new int[numberText.length()];
        offsets = new int[numberText.length()];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = numberText.charAt(i) - '0';
        }
        ensureNumberInCenter();
        invalidate();
    }

    private ValueAnimator animator;

    public synchronized void animateToNumber(int number) {
        Log.i(TAG, "animateToNumber: " + number);
        //判断值变大还是变小
        final boolean smaller = number < this.number;
        this.number = number;
        String numberText = String.valueOf(number);
        int[] numbers = new int[numberText.length()];
        final int[] offsets = new int[numberText.length()];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = numberText.charAt(i) - '0';
        }
        calculateOffsets(offsets, numbers, smaller, getLineHeight());
        this.numbers = numbers;
        this.offsets = offsets;
        Log.i(TAG, "animateToNumber: offset" + Arrays.toString(offsets));
        if (animator != null && animator.isRunning()) {
            animator.end();
        }
        ensureNumberInCenter();
        animator = ValueAnimator.ofFloat(1, 0);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float fraction = (float) animation.getAnimatedValue();
                for (int i = 0; i < SlidingNumberView.this.offsets.length; i++) {
                    SlidingNumberView.this.offsets[i] =
                            (int) (fraction * SlidingNumberView.this.offsetsMax[i]);
                }
                invalidate();
            }
        });
        animator.start();
    }

    /**
     * 计算与上一次相比，每一列数字中目标数字对于正确位置0的偏移
     *
     * @param height  每一个数字的高度。
     * @param smaller 整体数字变大还是变小。
     *                如上一个数字是 110 ，要变换为 111，则整体数字变大，取值为 false。此时最后一个数的偏移就是 1 * height
     *                如上一个数字是 110 , 要变换为 109，则整体数字变小，取值为 true。此时最后一个数的偏移就是 -9 * height
     */
    private void calculateOffsets(int[] offsets, int[] numbers, final boolean smaller, int height) {
        for (int i = 0; i < numbers.length; i++) {
            final int current = numbers[i];
            final int former;
            if (i < this.numbers.length) {
                former = this.numbers[i];
            } else {
                former = 0;
            }
            //当前值与之前值的差
            final int diff = current - former;

            final int offset;
            if (smaller && diff < 0) {
                offset = diff * height;
            } else if (smaller && diff > 0) {
                offset = -(10 - diff) * height;
            } else if (!smaller && diff < 0) {
                offset = (10 + diff) * height;
            } else if (!smaller && diff > 0) {
                offset = diff * height;
            } else {
                offset = 0;
            }
            offsets[i] = offset;
        }
        offsetsMax = offsets.clone();
    }

    private int getLineHeight() {
        return getDigitStaticLayout(0).getHeight();
    }

    @SuppressWarnings("SameParameterValue")
    public void setTextSize(float size) {
        textPaint.setTextSize(size);
        buildLayout();
        ensureNumberInCenter();
        invalidate();
    }


    private StaticLayout getDigitStaticLayout(int number) {
        checkNumber(number);
        return digitStaticLayouts[number];
    }


    private static int getNextDigit(int n) {
        checkNumber(n);
        if (n == 9) {
            return 0;
        }
        return n + 1;
    }

    private static int getPreviousDigit(int n) {
        checkNumber(n);
        if (n == 0) {
            return 9;
        }
        return n - 1;
    }

    private static void checkNumber(int number) {
        if (number > 9 || number < 0) {
            throw new IllegalArgumentException(number + " must in 0..9");
        }
    }

    public int getNumber() {
        return this.number;
    }
}
