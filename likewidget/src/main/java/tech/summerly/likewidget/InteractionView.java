package tech.summerly.likewidget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * author : Summer
 * date   : 2017/10/13
 */

public class InteractionView extends View {

    //最大放大缩小系数
    private static final float MAX_COEFFICIENT = 1.2f;
    private static final float MIN_COEFFICIENT = 0.8f;

    private boolean isSelected = false;

    private Drawable indicatorSelected;

    private Drawable indicatorNormal;

    private Drawable drawableCircle;

    private final Rect indicatorRect = new Rect();

    private final Rect scaleCircleRect = new Rect();

    private float scaleCoefficient = 1f;

    private boolean isExpanding = false;

    public InteractionView(Context context) {
        this(context, null);
    }

    public InteractionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InteractionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        indicatorSelected = getResources().getDrawable(R.drawable.like_view_ic_like);
        indicatorNormal = getResources().getDrawable(R.drawable.like_view_ic_like_normal);
        drawableCircle = getResources().getDrawable(R.drawable.like_view_expand_circle);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                animateShrink();
                break;
            case MotionEvent.ACTION_UP:
                animateExpand();
                break;
        }
        return super.onTouchEvent(event);
    }


    private ValueAnimator shrinkAnimator;

    private ValueAnimator expandAnimator;

    /**
     * 进行缩小动画.
     */
    private void animateShrink() {
        if (shrinkAnimator != null && shrinkAnimator.isRunning()) {
            shrinkAnimator.end();
        }
        if (expandAnimator != null && expandAnimator.isRunning()) {
            expandAnimator.end();
        }
        shrinkAnimator = ValueAnimator.ofFloat(1, MIN_COEFFICIENT);
        shrinkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scaleCoefficient = ((float) animation.getAnimatedValue());
                invalidate();
            }
        });
        shrinkAnimator.start();
    }

    /**
     * 进行放大动画.
     */
    private void animateExpand() {
        //如果正在缩小动画中,则等待缩小动画完成再进行扩大动画.
        if (shrinkAnimator != null && shrinkAnimator.isRunning()) {
            shrinkAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animateExpand();
                }
            });
            return;
        }
        if (expandAnimator != null && expandAnimator.isRunning()) {
            expandAnimator.end();
        }
        expandAnimator = ValueAnimator.ofFloat(MIN_COEFFICIENT, 1);
        expandAnimator.setInterpolator(new OvershootInterpolator(10f));
        expandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scaleCoefficient = ((float) animation.getAnimatedValue());
                invalidate();
            }
        });
        expandAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isExpanding = false;
            }
        });
        isExpanding = true;
        expandAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        indicatorRect.set(0, 0, w, h);
        scaleCircleRect.set(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        indicatorNormal.setBounds(indicatorRect);
        indicatorSelected.setBounds(indicatorRect);
        canvas.save();
        canvas.scale(scaleCoefficient, scaleCoefficient, getWidth() / 2, getHeight() / 2);
        if (isSelected) {
            indicatorSelected.draw(canvas);
        } else {
            indicatorNormal.draw(canvas);
        }
        canvas.restore();

        //在扩张动画中绘制一个扩散的圆圈.
        if (isExpanding && isSelected && scaleCoefficient <= 1) {
            //由于scaleCoefficient的扩展时变化的范围是 [0.8,1]
            //所以先将其映射成 [0,1]的区间
            final float scale = 1 - (1 - scaleCoefficient) * 5;
            //对圆圈进行放大或者缩小
            scale(scaleCircleRect, scale * MAX_COEFFICIENT);
            //让圆圈绘制在中央
            int dx = getWidth() / 2 - scaleCircleRect.width() / 2;
            int dy = getHeight() / 2 - scaleCircleRect.height() / 2;
            scaleCircleRect.offset(dx, dy);
            drawableCircle.setBounds(scaleCircleRect);
            //圆圈在扩散过程中,慢慢变得透明
            drawableCircle.setAlpha((int) ((1 - scale) * 255));
            drawableCircle.draw(canvas);
            //重置scaleCircleRect
            scaleCircleRect.set(0, 0, getWidth(), getHeight());
        }
    }

    public void setSelectedState(boolean isSelected) {
        this.isSelected = isSelected;
        invalidate();
    }

    public void animateToState(boolean isSelected) {
        animateShrink();
        this.isSelected = isSelected;
        animateExpand();
    }

    public boolean isSelectedState() {
        return isSelected;
    }

    private void scale(Rect rect, float fraction) {
        rect.left *= fraction;
        rect.right *= fraction;
        rect.top *= fraction;
        rect.bottom *= fraction;
    }
}
