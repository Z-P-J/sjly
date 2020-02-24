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
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.zpj.utils.ScreenUtils;
import com.zpj.widget.R;
import com.zpj.widget.SimpleAnimatorListener;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseSwitcher extends View {

    protected float switcherRadius = 0f;
    protected float iconRadius = 0f;
    protected float iconClipRadius = 0f;
    protected float iconCollapsedWidth = 0f;
    protected int defHeight = 0;
    protected int defWidth = 0;
    boolean isChecked = true;

    @ColorInt
    protected int onColor = 0;
    @ColorInt
    protected int offColor = 0;
    @ColorInt
    protected int iconColor = 0;

    protected Paint switcherPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    protected RectF iconRect = new RectF(0f, 0f, 0f, 0f);
    protected RectF iconClipRect = new RectF(0f, 0f, 0f, 0f);
    protected Paint iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    protected Paint iconClipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    protected AnimatorSet animatorSet = new AnimatorSet();

    protected Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected Bitmap shadow = null;
    protected float shadowOffset = 0f;

    @ColorInt
    protected int currentColor = 0;

    protected float switchElevation = 0f;
    protected float iconHeight = 0f;

    // from rounded rect to circle and back
    protected float iconProgress = 0f;

    protected float iconTranslateX = 0f;

    protected OnCheckedChangeListener listener = null;

    BaseSwitcher(Context context) {
        this(context, null);
    }

    BaseSwitcher(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    BaseSwitcher(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs, defStyleAttr);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateSwitch();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            width = defWidth;
            height = defHeight;
        }

        if (!Utils.isLollipopAndAbove()) {
            width += (int) switchElevation * 2;
            height += (int) switchElevation * 2;
        }

        setMeasuredDimension(width, height);
    }

    protected void initAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
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
            shadowPaint.setAlpha(51);
            setShadowBlurRadius(switchElevation);
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    protected void generateAnimates(Set<Animator> animatorSet) {

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
        animatorSet.add(iconAnimator);

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

        animatorSet.add(colorAnimator);
    }


    private void animateSwitch() {
        animatorSet.cancel();
        animatorSet = new AnimatorSet();

        Set<Animator> set = new HashSet<>();
        generateAnimates(set);

        animatorSet.addListener(new SimpleAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                isChecked = !isChecked;
                if (listener != null) {
                    listener.onChange(isChecked);
                }
            }
        });
        animatorSet.playTogether(set);
        animatorSet.start();
    }



    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
        switcherPaint.setColor(currentColor);
        iconClipPaint.setColor(currentColor);
    }

    public boolean isChecked() {
        return isChecked;
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

    @Override
    protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (Utils.isLollipopAndAbove()) {
            setOutlineProvider(getSwitchOutline(w, h));
            setElevation(switchElevation);
        } else {
            shadowOffset = switchElevation;
        }

        onSizeChanged(w, h);

        if (!Utils.isLollipopAndAbove()) generateShadow();
    }

    @Override
    protected final void onDraw(Canvas canvas) {
        // shadow
        if (!Utils.isLollipopAndAbove() && switchElevation > 0f && !isInEditMode()) {
            canvas.drawBitmap(shadow, 0f, shadowOffset, null);
        }

        // switcher
        drawRect(canvas, switcherPaint);

        // icon
        int checkpoint = canvas.save();
        canvas.translate(iconTranslateX, 0f);
        try {
            canvas.drawRoundRect(iconRect, switcherRadius, switcherRadius, iconPaint);
            if (iconClipRect.width() > iconCollapsedWidth)
                canvas.drawRoundRect(iconClipRect, iconRadius, iconRadius, iconClipPaint);
        } finally {
            canvas.restoreToCount(checkpoint);
        }
    }

    protected SwitchOutline getSwitchOutline(int w, int h) {
        return new SwitchOutline(w, h) {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, width, height, switcherRadius);
            }
        };
    }

    protected final void generateShadow() {
        if (switchElevation == 0f) return;
        if (!isInEditMode()) {
            if (shadow == null) {
                shadow = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ALPHA_8);
            } else {
                shadow.eraseColor(Color.TRANSPARENT);
            }
            Canvas c = new Canvas(shadow);

            drawRect(c, shadowPaint);


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

    public abstract void setIconProgress(float iconProgress);

    protected abstract void onSizeChanged(int w, int h);

    protected abstract void drawRect(Canvas canvas, @NonNull Paint paint);

}