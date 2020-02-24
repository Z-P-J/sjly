package com.zpj.widget.switcher;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zpj.widget.R;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.SimpleAnimatorListener;

public class SwitcherC extends View {

    private float switcherRadius = 0f;
    private float iconRadius = 0f;
    private float iconClipRadius = 0f;
    private float iconCollapsedWidth = 0f;
    private int defHeight = 0;
    private int defWidth = 0;
    boolean isChecked = true;

    @ColorInt
    private int onColor = 0;
    @ColorInt
    private int offColor = 0;
    @ColorInt
    private int iconColor = 0;

    private Paint switcherPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private RectF iconRect = new RectF(0f, 0f, 0f, 0f);
    private RectF iconClipRect = new RectF(0f, 0f, 0f, 0f);
    private Paint iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint iconClipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private AnimatorSet animatorSet = new AnimatorSet();

    private Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap shadow = null;
    private float shadowOffset = 0f;

    @ColorInt
    private int currentColor = 0;

    private float switchElevation = 0f;
    private float iconHeight = 0f;

    // from rounded rect to circle and back
    private float iconProgress = 0f;

    private OnCheckedChangeListener listener = null;

    public SwitcherC(Context context) {
        this(context, null);
    }

    public SwitcherC(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitcherC(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        retrieveAttributes(context, attrs, defStyleAttr);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateSwitch();
            }
        });
    }

    private void retrieveAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Switcher,
                defStyleAttr, R.style.Switcher);

        switchElevation = typedArray.getDimension(R.styleable.Switcher_switcher_elevation, 0f);

        onColor = typedArray.getColor(R.styleable.Switcher_switcher_on_color, 0);
        offColor = typedArray.getColor(R.styleable.Switcher_switcher_off_color, 0);
        iconColor = typedArray.getColor(R.styleable.Switcher_switcher_icon_color, 0);

        isChecked = typedArray.getBoolean(R.styleable.Switcher_android_checked, true);

        if (!isChecked) {
            setIconProgress(1f);
        }

        setCurrentColor(isChecked ? onColor :offColor);

        iconPaint.setColor(iconColor);

        defHeight = typedArray.getDimensionPixelOffset(R.styleable.Switcher_switcher_height, 0);
        defWidth = typedArray.getDimensionPixelOffset(R.styleable.Switcher_switcher_width, 0);

        typedArray.recycle();

        if (!Utils.isLollipopAndAbove() && switchElevation > 0f) {
            shadowPaint.setColorFilter(new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN));
            shadowPaint.setAlpha(51); // 20%
            setShadowBlurRadius(switchElevation);
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            int min = Math.min(defWidth, defHeight);
            width = min;
            height = min;
        }

        if (!Utils.isLollipopAndAbove()) {
            width += (int) switchElevation * 2;
            height += (int) switchElevation * 2;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (Utils.isLollipopAndAbove()) {
            setOutlineProvider(new SwitchOutline(w, h) {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, (int) (switcherRadius * 2), (int) (switcherRadius * 2), switcherRadius);
                }
            });
            setElevation(switchElevation);
        } else {
            shadowOffset = switchElevation;
        }

        switcherRadius = (Math.min(w, h) / 2f) - shadowOffset;

        iconRadius = switcherRadius * 0.5f;
        iconClipRadius = iconRadius / 2.25f;
        iconCollapsedWidth = (iconRadius - iconClipRadius) * 1.1f;

        iconHeight = iconRadius * 2f;

        iconRect.set(
                (switcherRadius - iconCollapsedWidth / 2f) + shadowOffset,
                ((switcherRadius * 2f - iconHeight) / 2f) + shadowOffset / 2,
                (switcherRadius + iconCollapsedWidth / 2f) + shadowOffset,
                (switcherRadius * 2f - (switcherRadius * 2f - iconHeight) / 2f) + shadowOffset / 2
        );

        if (!isChecked) {
            iconRect.left = (switcherRadius - iconCollapsedWidth / 2f - (iconRadius - iconCollapsedWidth / 2f)) + shadowOffset;
            iconRect.right = (switcherRadius + iconCollapsedWidth / 2f + (iconRadius - iconCollapsedWidth / 2f)) + shadowOffset;

            iconClipRect.set(
                    iconRect.centerX() - iconClipRadius,
                    iconRect.centerY() - iconClipRadius,
                    iconRect.centerX() + iconClipRadius,
                    iconRect.centerY() + iconClipRadius
            );
        }

        if (!Utils.isLollipopAndAbove()) generateShadow();
    }

    private void generateShadow() {
        if (switchElevation == 0f) return;
        if (!isInEditMode()) {
            if (shadow == null) {
                shadow = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ALPHA_8);
            } else {
                shadow.eraseColor(Color.TRANSPARENT);
            }
            Canvas c = new Canvas(shadow);

            c.drawCircle(switcherRadius + shadowOffset, switcherRadius + shadowOffset / 2,
                    switcherRadius, shadowPaint);
            RenderScript rs = RenderScript.create(getContext());
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8(rs));
            Allocation input = Allocation.createFromBitmap(rs, shadow);
            Allocation output = Allocation.createTyped(rs, input.getType());
            blur.setRadius(switchElevation);
            blur.setInput(input);
            blur.forEach(output);
            output.copyTo(shadow);
            input.destroy();
            output.destroy();
            blur.destroy();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // shadow
        if (!Utils.isLollipopAndAbove() && switchElevation > 0f && !isInEditMode()) {
            canvas.drawBitmap(shadow, 0f, shadowOffset, null);
        }

        // switcher
        canvas.drawCircle(switcherRadius + shadowOffset, switcherRadius + shadowOffset / 2,
                switcherRadius, switcherPaint);

        // icon
        canvas.drawRoundRect(iconRect, switcherRadius, switcherRadius, iconPaint);
        /* don't draw clip path if icon is collapsed (to prevent drawing small circle
        on rounded rect when switch is isChecked)*/
        if (iconClipRect.width() > iconCollapsedWidth)
            canvas.drawRoundRect(iconClipRect, iconRadius, iconRadius, iconClipPaint);
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
        switcherPaint.setColor(currentColor);
        iconClipPaint.setColor(currentColor);
    }

    public void setIconProgress(float iconProgress) {
        if (this.iconProgress != iconProgress) {
            this.iconProgress = iconProgress;

            float iconOffset = Utils.lerp(0f, iconRadius - iconCollapsedWidth / 2, iconProgress);
            iconRect.left = (switcherRadius - iconCollapsedWidth / 2 - iconOffset) + shadowOffset;
            iconRect.right = (switcherRadius + iconCollapsedWidth / 2 + iconOffset) + shadowOffset;

            float clipOffset = Utils.lerp(0f, iconClipRadius, iconProgress);
            iconClipRect.set(
                    iconRect.centerX() - clipOffset,
                    iconRect.centerY() - clipOffset,
                    iconRect.centerX() + clipOffset,
                    iconRect.centerY() + clipOffset
            );
            postInvalidateOnAnimation();
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    private void animateSwitch() {
        animatorSet.cancel();
        animatorSet = new AnimatorSet();

        double amplitude = Utils.BOUNCE_ANIM_AMPLITUDE_IN;
        double frequency = Utils.BOUNCE_ANIM_FREQUENCY_IN;
        float newProgress = 1f;

        if (!isChecked) {
            amplitude = Utils.BOUNCE_ANIM_AMPLITUDE_OUT;
            frequency = Utils.BOUNCE_ANIM_FREQUENCY_OUT;
            newProgress = 0f;
        }

        ValueAnimator iconAnimator = ValueAnimator.ofFloat(iconProgress, newProgress);
        iconAnimator.setInterpolator(new BounceInterpolator(amplitude, frequency));
        iconAnimator.setDuration(Utils.SWITCHER_ANIMATION_DURATION);
        iconAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setIconProgress((float) animation.getAnimatedValue());
            }
        });

        int toColor =  isChecked ? offColor : onColor;

        iconClipPaint.setColor(toColor);

        ValueAnimator colorAnimator = new ValueAnimator();
        colorAnimator.setIntValues(currentColor, toColor);
        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.setDuration(Utils.COLOR_ANIMATION_DURATION);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setCurrentColor((int) animation.getAnimatedValue());
            }
        });

        animatorSet.addListener(new SimpleAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                isChecked = !isChecked;
//                setChecked(isChecked, false);
                if (listener != null) {
                    listener.onChange(isChecked);
                }
            }
        });
        animatorSet.playTogether(iconAnimator, colorAnimator);
        animatorSet.start();
    }




    /**
     * Register a callback to be invoked when the isChecked state of this switch
     * changes.
     *
     * @param listener the callback to call on isChecked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    /**
     * <p>Changes the isChecked state of this switch.</p>
     *
     * @param checked true to check the switch, false to uncheck it
     * @param withAnimation use animation
     */
    public void setChecked(boolean checked, boolean withAnimation) {
        if (this.isChecked != checked) {
            if (withAnimation) {
                animateSwitch();
            } else {
                this.isChecked = checked;
                if (!checked) {
                    setCurrentColor(offColor);
                    setIconProgress(1f);
                } else {
                    setCurrentColor(onColor);
                    setIconProgress(0f);
                }
            }
        }
    }

    public void setChecked(boolean checked) {
        setChecked(checked, true);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Utils.STATE, super.onSaveInstanceState());
        bundle.putBoolean(Utils.KEY_CHECKED, isChecked);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if (state instanceof Bundle) {
            super.onRestoreInstanceState(((Bundle) state).getParcelable(Utils.STATE));
            isChecked = ((Bundle) state).getBoolean(Utils.KEY_CHECKED);
            if (!isChecked) forceUncheck();
        }
    }

    private void forceUncheck() {
        setCurrentColor(offColor);
        setIconProgress(1f);
    }

    private void setShadowBlurRadius(float elevation) {
        float maxElevation = ScreenUtils.dp2px(getContext(), 24f);
        switchElevation = Math.min(25f * (elevation / maxElevation), 25f);
    }

}