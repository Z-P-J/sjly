package it.liuting.imagetrans;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import it.liuting.imagetrans.evaluator.MatrixEvaluator;
import it.liuting.imagetrans.evaluator.RectFEvaluator;
import it.liuting.imagetrans.listener.OnTransformListener;

/**
 * Created by liuting on 18/3/16.
 */

class TransformAttacher {
    private final static int ANIM_TIME = 300;

    private TransImageView imageView;
    private ThumbConfig thumbConfig;
    private ITConfig itConfig;
    private StateInfo currentStateInfo = new StateInfo(TransState.DEFAULT);
    private boolean running;
    private boolean drawing;
    private RectF transformRect = new RectF();
    private Matrix transformMatrix = new Matrix();
    private TransState transformState = TransState.DEFAULT;
    private TransStateChangeListener listener;

    TransformAttacher(TransImageView imageView) {
        this.imageView = imageView;
    }

    void settingConfig(ITConfig itConfig, ThumbConfig thumbConfig) {
        this.itConfig = itConfig;
        this.thumbConfig = thumbConfig;
    }

    void showThumb(boolean needTrans) {
        if (needTrans)
            changeState(TransState.OPEN_TO_THUMB);
        else
            changeState(TransState.THUMB);
    }

    void showImage(boolean needTrans) {
        if (needTrans) {
            if (currentStateInfo.state == TransState.DEFAULT) {
                changeState(TransState.OPEN_TO_ORI);
            } else if (currentStateInfo.state == TransState.THUMB) {
                changeState(TransState.THUMB_TO_ORI);
            }
        } else {
            changeState(TransState.ORI);
        }
    }

    void showClose() {
        if (currentStateInfo.state == TransState.THUMB) {
            changeState(TransState.THUMB_TO_CLOSE);
        } else if (currentStateInfo.state == TransState.ORI) {
            changeState(TransState.ORI_TO_CLOSE);
        } else if (currentStateInfo.state == TransState.DEFAULT) {
            changeState(TransState.CLOSEED);
        }
    }

    void changeState(TransState state) {
        if (running) return;
        running = true;
        boolean autoChange = false;
        if (state != TransState.CLOSEED && state != TransState.DEFAULT) {
            currentStateInfo = currentStateInfo.nextStateInfo(state);
            if (currentStateInfo.needTrans) {
                runTransform();
            } else {
                transformRect = new RectF(currentStateInfo.endF);
                transformMatrix = new Matrix(currentStateInfo.endM);
                transformState = currentStateInfo.state;
                running = false;
                autoChange = true;
            }
        }
        if (listener != null) listener.onChange(state);
        if (autoChange && state == TransState.THUMB_TO_ORI) {
            changeState(TransState.ORI);
        }
    }

    private void runTransform() {
        TransformAnimation animation = new TransformAnimation(currentStateInfo);
        animation.setListener(new OnTransformListener() {
            @Override
            public void transformStart() {
                drawing = true;
            }

            @Override
            public void transformEnd() {
                running = false;
                drawing = false;
                autoChangeState();
            }
        });
        animation.start();
    }

    private void autoChangeState() {
        if (currentStateInfo.state == TransState.OPEN_TO_THUMB) {
            changeState(TransState.THUMB);
        } else if (currentStateInfo.state == TransState.OPEN_TO_ORI || currentStateInfo.state == TransState.THUMB_TO_ORI) {
            changeState(TransState.ORI);
        } else if (currentStateInfo.state == TransState.THUMB_TO_CLOSE || currentStateInfo.state == TransState.ORI_TO_CLOSE) {
            changeState(TransState.CLOSEED);
        }
    }


    private int getImageViewWidth(ImageView imageView) {
        return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    private int getImageViewHeight(ImageView imageView) {
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }

    private int getThumbDrawableWidth() {
        return thumbConfig.thumbnailWeakRefe == null || thumbConfig.thumbnailWeakRefe.get() == null ? 0 : thumbConfig.thumbnailWeakRefe.get().getIntrinsicWidth();
    }

    private int getThumbDrawableHeight() {
        return thumbConfig.thumbnailWeakRefe == null || thumbConfig.thumbnailWeakRefe.get() == null ? 0 : thumbConfig.thumbnailWeakRefe.get().getIntrinsicHeight();
    }

    private int getDrawableWidth(TransState state) {
        Drawable drawable = getDrawable(state);
        return drawable == null ? 0 : drawable.getIntrinsicWidth();
    }

    private int getDrawableHeight(TransState state) {
        Drawable drawable = getDrawable(state);
        return drawable == null ? 0 : drawable.getIntrinsicHeight();
    }

    public interface TransStateChangeListener {
        void onChange(TransState state);
    }

    public void setTransStateChangeListener(TransStateChangeListener listener) {
        this.listener = listener;
    }

    boolean needIntercept() {
        return currentStateInfo.state != TransState.ORI;
    }

    boolean isDrawing() {
        return drawing;
    }

    class StateInfo {
        RectF startF;
        RectF endF;
        Matrix startM;
        Matrix endM;
        int alpha;
        TransState state;
        boolean needTrans = false;

        StateInfo(TransState state) {
            this.state = state;
        }

        StateInfo nextStateInfo(TransState nextState) {
            StateInfo info = new StateInfo(nextState);
            info.startF = getStartRectF(nextState, this);
            info.startM = getStartMatrix(nextState, info.startF, this);
            info.endF = getEndRectF(nextState, this);
            info.endM = getEndMatrix(nextState, info.endF, this);
            info.alpha = getEndAlpha(nextState);
            if (info.startF == null || info.startM == null || info.endF == null || info.endM == null) {
                info.needTrans = false;
            } else {
                if (nextState == TransState.THUMB_TO_ORI) {
                    if (info.startF.left == info.endF.left &&
                            info.startF.top == info.endF.top &&
                            info.startF.right == info.endF.right &&
                            info.startF.bottom == info.endF.bottom) {
                        //图片比例和位置没有变化，就不需要进行预览图到原图的变形
                        info.needTrans = false;
                    }
                }
            }

            if (nextState == TransState.DEFAULT || nextState == TransState.THUMB || nextState == TransState.ORI) {
                info.needTrans = false;
            } else if (info.startF == null || info.startM == null || info.endF == null || info.endM == null) {
                info.needTrans = false;
            } else if (nextState == TransState.THUMB_TO_ORI
                    && info.startF.left == info.endF.left
                    && info.startF.top == info.endF.top
                    && info.startF.right == info.endF.right
                    && info.startF.bottom == info.endF.bottom) {
                //图片比例和位置没有变化，就不需要进行预览图到原图的变形
                info.needTrans = false;
            } else {
                info.needTrans = true;
            }
            return info;
        }

        RectF getThumbRectF() {
            int drawableWidth = getThumbDrawableWidth();
            int drawableHeight = getThumbDrawableHeight();
            int viewWidth = getImageViewWidth(imageView);
            int viewHeight = getImageViewHeight(imageView);
            float thumbScale = 0.5f;
            if (itConfig.thumbLarge) {
                thumbScale = 1f;
            }
            if (drawableWidth * 1f / drawableHeight >= viewWidth * 1f / viewHeight) {
                float tempWidth = viewWidth * thumbScale;
                float tempHeight = tempWidth * drawableHeight / drawableWidth;
                float left = (viewWidth - tempWidth) * .5f;
                float top = (viewHeight - tempHeight) * .5f;
                return new RectF(left, top, left + tempWidth, top + tempHeight);
            } else {
                float tempHeight = viewHeight * thumbScale;
                float tempWidth = tempHeight * drawableWidth / drawableHeight;
                float left = (viewWidth - tempWidth) * .5f;
                float top = (viewHeight - tempHeight) * .5f;
                return new RectF(left, top, tempWidth + left, tempHeight + top);
            }
        }

        private RectF getStartRectF(TransState state, StateInfo prevStateInfo) {
            switch (state) {
                case OPEN_TO_THUMB:
                case OPEN_TO_ORI:
                    return thumbConfig.imageRectF;
                case THUMB_TO_ORI:
                case THUMB_TO_CLOSE: {
                    return prevStateInfo.endF;
                }
                case ORI_TO_CLOSE: {
                    return imageView.getDisplayRect(false);
                }
                case THUMB:
                    return prevStateInfo.endF;
            }
            return null;
        }

        private RectF getEndRectF(TransState state, StateInfo prevStateInfo) {
            switch (state) {
                case OPEN_TO_THUMB: {
                    return getThumbRectF();
                }
                case THUMB_TO_ORI:
                case OPEN_TO_ORI: {
                    return imageView.getDisplayRect(true);
                }
                case THUMB_TO_CLOSE:
                case ORI_TO_CLOSE: {
                    return thumbConfig.imageRectF;
                }
                case THUMB:
                    if (prevStateInfo.state == TransState.DEFAULT) {
                        return getThumbRectF();
                    }
                    return prevStateInfo.endF;
            }
            return null;
        }

        private Matrix getStartMatrix(TransState state, RectF rectF, StateInfo prevStateInfo) {
//            if (rectF == null) return null;
            switch (state) {
                case OPEN_TO_THUMB:
                case OPEN_TO_ORI:
                case THUMB_TO_ORI: {
                    return getMatrix(rectF, getDrawableWidth(state), getDrawableHeight(state), 1);
                }
                case THUMB_TO_CLOSE: {
                    return prevStateInfo.endM;
                }
                case ORI_TO_CLOSE: {
                    Matrix matrix = new Matrix(imageView.getDrawMatrix());
                    matrix.postTranslate(-Util.getValue(matrix, Matrix.MTRANS_X), -Util.getValue(matrix, Matrix.MTRANS_Y));
                    return matrix;
                }
                case THUMB:
                    return prevStateInfo.endM;
            }
            return null;
        }


        private Matrix getEndMatrix(TransState state, RectF rectF, StateInfo prevStateInfo) {
//            if (rectF == null) return null;
            switch (state) {
                case OPEN_TO_THUMB:
                case THUMB_TO_CLOSE: {
                    return getMatrix(rectF, getDrawableWidth(state), getDrawableHeight(state), 1f);
                }
                case THUMB_TO_ORI:
                case OPEN_TO_ORI: {
                    Matrix endMatrix = new Matrix(imageView.getDrawMatrix());
                    endMatrix.postTranslate(-Util.getValue(endMatrix, Matrix.MTRANS_X), -Util.getValue(endMatrix, Matrix.MTRANS_Y));
                    return endMatrix;
                }
                case ORI_TO_CLOSE: {
                    RectF initRectF = imageView.getDisplayRect(false);
                    return getMatrix(rectF, initRectF.width(), initRectF.height(), Util.getValue(imageView.getDrawMatrix(), Matrix.MSCALE_X));
                }
                case THUMB:
                    if (prevStateInfo.state == TransState.DEFAULT) {
                        return getMatrix(rectF, getDrawableWidth(state), getDrawableHeight(state), 1f);
                    }
                    return prevStateInfo.endM;
            }
            return null;
        }

        private int getEndAlpha(TransState state) {
            switch (state) {
                case OPEN_TO_THUMB:
                case OPEN_TO_ORI:
                case THUMB_TO_ORI:
                    return 255;
                case ORI_TO_CLOSE:
                case THUMB_TO_CLOSE:
                    return 0;
                default:
                    return 255;
            }
        }

        private Matrix getMatrix(RectF rectf, float width, float height, float oScale) {
            //新建结束图像的矩阵
            Matrix matrix = new Matrix();
            //得到目标矩阵相对于当前矩阵的宽和高的缩放比例
            float scaleX = rectf.width() / width;
            float scaleY = rectf.height() / height;
            //由于图片比例不定,这里得到最匹配目标矩形的scale
            float scale = Math.max(scaleX, scaleY);
            //得到最终的矩阵scale
            float tempScale = scale * oScale;
            matrix.setScale(tempScale, tempScale);
            ScaleType type = thumbConfig.scaleType;
            //根据不同的裁剪类型
            switch (type) {
                case CENTER_CROP: {
                    //当预览图是居中裁剪
                    float dx = (width * scale - rectf.width()) * .5f;
                    float dy = (height * scale - rectf.height()) * .5f;
                    matrix.postTranslate(-dx, -dy);
                    break;
                }
                case START_CROP: {
                    //当预览图是顶部裁剪
                    if (width > height) {
                        float dy = (height * scale - rectf.height()) * .5f;
                        matrix.postTranslate(0, -dy);
                    } else {
                        float dx = (width * scale - rectf.width()) * .5f;
                        matrix.postTranslate(-dx, 0);
                    }
                    break;
                }
                case END_CROP: {
                    //当预览图是尾部裁剪
                    if (width > height) {
                        float dx = width * scale - rectf.width();
                        float dy = (height * scale - rectf.height()) * .5f;
                        matrix.postTranslate(-dx, -dy);
                    } else {
                        float dx = (width * scale - rectf.width()) * .5f;
                        float dy = height * scale - rectf.height();
                        matrix.postTranslate(-dx, -dy);
                    }
                    break;
                }
                case FIT_XY: {
                    //当预览图是充满宽高
                    matrix.setScale(scaleX * oScale, scaleY * oScale);
                    break;
                }
                default: {
                    //尚未支持其他裁剪方式
                    break;
                }
            }
            return matrix;
        }

    }

    public enum TransState {
        DEFAULT, OPEN_TO_THUMB, THUMB, OPEN_TO_ORI, ORI, THUMB_TO_ORI, THUMB_TO_CLOSE, ORI_TO_CLOSE, CLOSEED;
    }


    /**
     * 绘制变形过程中的图形
     *
     * @param canvas
     */
    void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable(transformState);
        if (drawable == null) return;
        int saveCount = canvas.getSaveCount();
        canvas.save();
        canvas.translate(transformRect.left, transformRect.top);
        canvas.clipRect(0, 0, transformRect.width(), transformRect.height());
        canvas.concat(transformMatrix);
        drawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    private Drawable getDrawable(TransState state) {
        switch (state) {
            case OPEN_TO_THUMB:
            case THUMB_TO_CLOSE:
            case THUMB:
                return thumbConfig.thumbnailWeakRefe == null ? null : thumbConfig.thumbnailWeakRefe.get();
            default:
                return imageView.getImageDrawable();
        }
    }

    /**
     * 图片变形动画
     */
    class TransformAnimation implements Runnable {
        private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
        private final RectF srcRectF;
        private final RectF dstRectF;
        private final Matrix srcMatrix;
        private final Matrix dstMatrix;
        private final int mStartAlpha;
        private final int mEndAlpha;
        private final long mStartTime;
        private RectFEvaluator rectFEvaluator;
        private MatrixEvaluator matrixEvaluator;
        private OnTransformListener listener;
        protected boolean isRunning = false;

        TransformAnimation(StateInfo stateInfo) {
            this.srcRectF = stateInfo.startF;
            this.dstRectF = stateInfo.endF;
            this.srcMatrix = stateInfo.startM;
            this.dstMatrix = stateInfo.endM;
            this.mEndAlpha = stateInfo.alpha;
            transformRect = new RectF(srcRectF);
            transformMatrix = new Matrix(srcMatrix);
            transformState = stateInfo.state;
            mStartAlpha = Compat.getBackGroundAlpha(imageView.getBackground());
            mStartTime = System.currentTimeMillis();
            rectFEvaluator = new RectFEvaluator(transformRect);
            matrixEvaluator = new MatrixEvaluator(transformMatrix);
        }

        void setListener(OnTransformListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            if (!isRunning) return;
            float t = interpolate();
            rectFEvaluator.evaluate(t, srcRectF, dstRectF);
            matrixEvaluator.evaluate(t, srcMatrix, dstMatrix);

            //设置背景颜色的透明度
            imageView.setBackgroundAlpha((int) (mStartAlpha + (mEndAlpha - mStartAlpha) * t));
            ViewCompat.postInvalidateOnAnimation(imageView);
            if (t < 1f) {
                Compat.postOnAnimation(imageView, this);
            } else {
                isRunning = false;
                if (listener != null) listener.transformEnd();
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / ANIM_TIME;
            t = Math.min(1f, t);
            t = mInterpolator.getInterpolation(t);
            return t;
        }

        void start() {
            isRunning = true;
            if (listener != null) listener.transformStart();
            Compat.postOnAnimation(imageView, this);
        }
    }
}
