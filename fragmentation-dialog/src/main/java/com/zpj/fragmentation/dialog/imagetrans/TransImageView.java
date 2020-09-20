package com.zpj.fragmentation.dialog.imagetrans;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.zpj.fragmentation.dialog.imagetrans.listener.OnPullCloseListener;
import com.zpj.fragmentation.dialog.imagetrans.listener.OnTransformListener;

/**
 * Created by liuting on 18/3/12.
 */

public class TransImageView extends ImageView implements TransformAttacher.TransStateChangeListener, View.OnLayoutChangeListener {

    private ImageGesturesAttacher imageGesturesAttacher;
    private TransformAttacher transformAttacher;
    private TransformAttacher.TransStateChangeListener transStateChangeListener;
    private OnPullCloseListener onPullCloseListener;
    private OnClickListener onClickListener;
    private Drawable mDrawable = null;
    private OnTransformListener onTransformListener;

    public TransImageView(Context context) {
        this(context, null);
    }

    public TransImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public TransImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setBackgroundAlpha(0);
        imageGesturesAttacher = new ImageGesturesAttacher(this);
        imageGesturesAttacher.setOnPullCloseListener(new OnPullCloseListener() {
            @Override
            public void onClose() {
                showCloseTransform();
                if (onPullCloseListener != null) onPullCloseListener.onClose();
            }

            @Override
            public void onPull(float range) {
                if (onPullCloseListener != null) onPullCloseListener.onPull(range);
            }

            @Override
            public void onCancel() {
                if (onPullCloseListener != null) onPullCloseListener.onCancel();
            }
        });
        imageGesturesAttacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null || !onClickListener.onClick(v)) {
                    showCloseTransform();
                }
            }
        });
        imageGesturesAttacher.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                performLongClick();
                return false;
            }
        });
        transformAttacher = new TransformAttacher(this);
        transformAttacher.setTransStateChangeListener(this);
        super.setScaleType(ScaleType.MATRIX);
        addOnLayoutChangeListener(this);
    }

    public void setOnTransformListener(OnTransformListener onTransformListener) {
        this.onTransformListener = onTransformListener;
    }

    public void settingConfig(@NonNull ITConfig itConfig, @NonNull ThumbConfig thumbConfig) {
        imageGesturesAttacher.settingConfig(itConfig);
        transformAttacher.settingConfig(itConfig, thumbConfig);
    }

    public void showThumb(final boolean needTrans) {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                transformAttacher.showThumb(needTrans);
                getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
        postInvalidate();
    }

    public void showImage(Drawable drawable, final boolean needTrans) {
        if (drawable == null) return;
        mDrawable = drawable;
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageGesturesAttacher.update(mDrawable, false);
                transformAttacher.showImage(needTrans);
                getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
        postInvalidate();
    }

    public void showCloseTransform() {
        transformAttacher.showClose();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        imageGesturesAttacher.resetMatrix();
    }

    public void setBackgroundAlpha(@IntRange(from = 0, to = 255) int alpha) {
        if (onTransformListener != null) {
            onTransformListener.onTransform((float) alpha / 255f);
        }
        setBackgroundColor(Color.argb(alpha, 0, 0, 0));
    }

    public Drawable getImageDrawable() {
        return mDrawable;
    }

    public RectF getDisplayRect(boolean checkbound) {
        return new RectF(imageGesturesAttacher.getDisplayRect(checkbound));
    }

    @Override
    public Matrix getImageMatrix() {
        if (transformAttacher.isDrawing()) {
            return imageGesturesAttacher.getMinMatrix();
        }
        return imageGesturesAttacher.getDrawMatrix();
    }

    public Matrix getDrawMatrix() {
        return imageGesturesAttacher.getDrawMatrix();
    }

    @Override
    public void onChange(TransformAttacher.TransState state) {
        switch (state) {
            case OPEN_TO_THUMB:
            case OPEN_TO_ORI:
                if (getParent() != null) {
                    imageGesturesAttacher.requestDisallowInterceptTouchEvent(true);
                }
                break;
            case THUMB:
                if (getParent() != null) {
                    imageGesturesAttacher.requestDisallowInterceptTouchEvent(false);
                }
                if (mDrawable != null)
                    transformAttacher.showImage(true);
                setBackgroundAlpha(255);
                break;
            case ORI:
                if (getParent() != null) {
                    imageGesturesAttacher.requestDisallowInterceptTouchEvent(false);
                }
                setImageDrawable(mDrawable);
                if (mDrawable instanceof GifDrawable) {
                    ((GifDrawable) mDrawable).start();
                }
                setBackgroundAlpha(255);
                break;
            case THUMB_TO_CLOSE:
            case ORI_TO_CLOSE:
                if (getParent() != null) {
                    imageGesturesAttacher.requestDisallowInterceptTouchEvent(true);
                }
                break;
        }
        if (transStateChangeListener != null) transStateChangeListener.onChange(state);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (transformAttacher.needIntercept()) {
            transformAttacher.onDraw(canvas);
        } else {
            super.onDraw(canvas);
        }
    }


    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
            imageGesturesAttacher.update();
        }
    }

    public void setTransStateChangeListener(TransformAttacher.TransStateChangeListener listener) {
        this.transStateChangeListener = listener;
    }

    public void setOnPullCloseListener(OnPullCloseListener listener) {
        this.onPullCloseListener = listener;
    }

    @Deprecated
    @Override
    public void setOnClickListener(@Nullable View.OnClickListener l) {
        super.setOnClickListener(l);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    public interface OnClickListener {
        boolean onClick(View v);
    }
}
