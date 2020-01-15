package it.liuting.imagetrans;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.OverScroller;

import it.liuting.imagetrans.listener.OnGestureListener;
import it.liuting.imagetrans.listener.OnPullCloseListener;

/**
 * Created by liuting on 17/6/9.
 * modify base on PhotoView
 * 基于PhotoView修改，增加的下拉拖动关闭手势
 */

class ImageGesturesAttacher implements View.OnTouchListener, OnGestureListener {
    //默认最大缩放比例
    private static float DEFAULT_MAX_SCALE = 3.0f;
    //默认中等缩放比例
    private static float DEFAULT_MID_SCALE = 1.75f;
    //默认最小缩放比例
    private static float DEFAULT_MIN_SCALE = 1.0f;
    //默认手势最小缩放比例，松开手回到 DEFAULT_MIN_SCALE
    private static float DEFAULT_GESTURE_MIN_SCALE = 0.2f;
    //默认缩放间隔时间
    private static int DEFAULT_ANIM_DURATION = 200;
    private static final int EDGE_NONE = -1;
    private static final int EDGE_LEFT = 0;
    private static final int EDGE_RIGHT = 1;
    private static final int EDGE_BOTH = 2;
    private TransImageView mImageView;
    private float gestureMinScale = DEFAULT_GESTURE_MIN_SCALE;
    //拖动关闭手势的边界值，超过这个边界值则执行关闭动画，反之回到基础位置
    private int pullCloseEdge;
    //基础旋转角度
    private float mBaseRotation;
    private int mAnimDuration = DEFAULT_ANIM_DURATION;
    //动画时间插值器
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    //是否是长图
    private boolean isLongImage = false;
    //图像显示的基础矩阵
    private final Matrix mBaseMatrix = new Matrix();
    //图像显示的当前矩阵
    private final Matrix mDrawMatrix = new Matrix();
    //图像显示的变换矩阵
    private final Matrix mSuppMatrix = new Matrix();
    //当前显示的矩形
    private final RectF mDisplayRect = new RectF();
    //用于缓存矩阵的九宫格的值
    private final float[] mMatrixValues = new float[9];
    private boolean mAllowParentInterceptOnEdge = true;
    private boolean mBlockParentIntercept = false;
    //是否执行拖动关闭手势
    private boolean isPullDownAction = false;
    //图像的ScaleType
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_CENTER;
    private int mScrollEdge = EDGE_BOTH;
    private float mMinScale = DEFAULT_MIN_SCALE;
    private float mMidScale = DEFAULT_MID_SCALE;
    private float mMaxScale = DEFAULT_MAX_SCALE;
    // 手势检测器
    private GestureDetector mGestureDetector;
    private CustomGestureDetector mScaleDragDetector;
    //手势长按监听
    private View.OnLongClickListener mLongClickListener;
    //手势点击监听
    private View.OnClickListener mOnClickListener;
    //下拉拖动关闭手势的关闭回调
    private OnPullCloseListener mOnPullCloseListener;
    //快速滑动的动画
    private FlingRunnable mCurrentFlingRunnable;
    private ITConfig itConfig;
    private boolean mAllowInterceptOnTouch = false;

    public ImageGesturesAttacher(TransImageView imageView) {
        this.mImageView = imageView;
        //初始化下拉的边界值
        pullCloseEdge = Util.dpToPx(100, imageView.getContext());
        //设置旋转角度为0
        mBaseRotation = 0.0f;
        mImageView.setOnTouchListener(this);
        //新建手势监听器
        mScaleDragDetector = new CustomGestureDetector(imageView.getContext(), this);
        mGestureDetector = new GestureDetector(imageView.getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {
                if (mLongClickListener != null) {
                    mLongClickListener.onLongClick(mImageView);
                }
            }
        });
        mGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(mImageView);
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent ev) {
                try {
                    float scale = getScale();
                    float x = ev.getX();
                    float y = ev.getY();

                    if (scale < getMediumScale()) {
                        setScale(getMediumScale(), x, y, true);
                    } else if (scale >= getMediumScale() && scale < getMaximumScale()) {
                        setScale(getMaximumScale(), x, y, true);
                    } else {
                        setScale(getMinimumScale(), x, y, true);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    // 这个异常有时会发生在 getX() 和 getY() 这俩个方法上
                }

                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });
    }

    public void settingConfig(@NonNull ITConfig itConfig) {
        this.itConfig = itConfig;
    }

    /**
     * 更新drawable的默认显示矩阵
     */
    public void update(Drawable drawable, boolean show) {
        // 根据当前的drawable更新显示矩阵
        updateBaseMatrix(drawable);
        // 重置矩阵
        resetSuppMatrix();
        if (show) {
            setRotationBy(mBaseRotation);
            setImageViewMatrix(getDrawMatrix());
            checkMatrixBounds();
        }
    }

    public void update() {
        if (mImageView.getDrawable() != null)
            update(mImageView.getDrawable(), true);
    }

    /**
     * 重置矩阵
     */
    public void resetMatrix() {
        resetSuppMatrix();
        setRotationBy(mBaseRotation);
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    /**
     * 重置变形矩阵，如果是长图就直接变形成适宽模式
     */
    private void resetSuppMatrix() {
        mSuppMatrix.reset();
        if (isLongImage) {
            mSuppMatrix.setScale(getMediumScale(), getMediumScale());
        }
    }

    /**
     * 计算基础显示矩阵
     *
     * @param drawable
     */
    private void updateBaseMatrix(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        final float viewWidth = getImageViewWidth(mImageView);
        final float viewHeight = getImageViewHeight(mImageView);
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();

        mBaseMatrix.reset();

        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;

        //根据ScaleType的类型得到相应的矩阵
        if (mScaleType == ImageView.ScaleType.CENTER) {
            mBaseMatrix.postTranslate((viewWidth - drawableWidth) / 2F,
                    (viewHeight - drawableHeight) / 2F);

        } else if (mScaleType == ImageView.ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else if (mScaleType == ImageView.ScaleType.CENTER_INSIDE) {
            float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else {
            RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
            RectF mTempDst = new RectF(0, 0, viewWidth, viewHeight);

            if ((int) mBaseRotation % 180 != 0) {
                mTempSrc = new RectF(0, 0, drawableHeight, drawableWidth);
            }
            switch (mScaleType) {
                case FIT_CENTER:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.CENTER);
                    break;
                case FIT_START:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.START);
                    break;

                case FIT_END:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.END);
                    break;

                case FIT_XY:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.FILL);
                    break;

                default:
                    break;
            }
        }
        if (itConfig.isReadMode) {
            //是在阅读模式下
            float maxScale = Math.max(widthScale, heightScale);
            //判断是否是竖长图
            if (maxScale * drawableHeight > itConfig.readModeRule * viewHeight) {
                isLongImage = true;
                mMinScale = 1f;
                //设置中等缩放为适宽的缩放
                mMidScale = widthScale / heightScale;
                mMaxScale = 1.5f * mMidScale;
            }
        }
    }

    /**
     * 根据矩阵得到当前的drawable应该占用的矩形区域
     *
     * @param matrix
     * @return RectF - 占用的矩阵区域
     */
    private RectF getDisplayRect(Matrix matrix) {
        Drawable d = mImageView.getImageDrawable();
        if (d != null) {
            mDisplayRect.set(0, 0, d.getIntrinsicWidth(),
                    d.getIntrinsicHeight());
            matrix.mapRect(mDisplayRect);
            return mDisplayRect;
        }
        return null;
    }

    /**
     * 获取当前矩阵的矩形范围
     *
     * @return
     */
    public RectF getDisplayRect(boolean checkBounds) {
        if (checkBounds) checkMatrixBounds();
        return getDisplayRect(getDrawMatrix());
    }

    /**
     * 检查矩阵，纠正矩阵的位移
     *
     * @return
     */
    private boolean checkMatrixBounds() {
        final RectF rect = getDisplayRect(getDrawMatrix());
        if (rect == null) {
            return false;
        }
        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;

        final int viewHeight = getImageViewHeight(mImageView);
        if (height <= viewHeight) {
            switch (mScaleType) {
                case FIT_START:
                    deltaY = -rect.top;
                    break;
                case FIT_END:
                    deltaY = viewHeight - height - rect.top;
                    break;
                default:
                    deltaY = (viewHeight - height) / 2 - rect.top;
                    break;
            }
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }

        final int viewWidth = getImageViewWidth(mImageView);
        if (width <= viewWidth) {
            switch (mScaleType) {
                case FIT_START:
                    deltaX = -rect.left;
                    break;
                case FIT_END:
                    deltaX = viewWidth - width - rect.left;
                    break;
                default:
                    deltaX = (viewWidth - width) / 2 - rect.left;
                    break;
            }
            mScrollEdge = EDGE_BOTH;
        } else if (rect.left > 0) {
            mScrollEdge = EDGE_LEFT;
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
            mScrollEdge = EDGE_RIGHT;
        } else {
            mScrollEdge = EDGE_NONE;
        }
        //纠正矩形的位移
        mSuppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    /**
     * 设置ImageView的矩阵
     *
     * @param matrix
     */
    private void setImageViewMatrix(Matrix matrix) {
        mImageView.setImageMatrix(matrix);
    }


    /**
     * 把变形矩阵右乘基础矩阵得到显示矩阵
     *
     * @return 最终显示的矩阵
     */
    public Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    public void setRotationBy(float degrees) {
        mSuppMatrix.postRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(getDrawMatrix());
        }
    }

    private int getImageViewWidth(ImageView imageView) {
        return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    private int getImageViewHeight(ImageView imageView) {
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }

    private void cancelFling() {
        if (mCurrentFlingRunnable != null) {
            mCurrentFlingRunnable.cancelFling();
            mCurrentFlingRunnable = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean handled = false;

        if (!mAllowInterceptOnTouch && Util.hasDrawable((ImageView) v)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ViewParent parent = v.getParent();
                    // 首先，拦截父 view 的 touch 事件
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    // 如果正在快速滑动的状态，如果用户执行DOWM操作，则取消Fling状态
                    cancelFling();
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (isPullDownAction) {
                        //当前是拖动关闭手势中，则计算是否关闭判断
                        checkPullClose();
                    } else if (getScale() < mMinScale) {
                        // 如果当前scale 小于最小scale 则开始缩放动画返回minScale
                        RectF rect = getDisplayRect(true);
                        if (rect != null) {
                            v.post(new AnimatedZoomRunnable(getScale(), mMinScale,
                                    rect.centerX(), rect.centerY()));
                            handled = true;
                        }
                    }
                    break;
            }
            // Try the Scale/Drag detector
            if (mScaleDragDetector != null) {
                boolean wasScaling = mScaleDragDetector.isScaling();
                boolean wasDragging = mScaleDragDetector.isDragging();

                handled = mScaleDragDetector.onTouchEvent(event);

                boolean didntScale = !wasScaling && !mScaleDragDetector.isScaling();
                boolean didntDrag = !wasDragging && !mScaleDragDetector.isDragging();

                mBlockParentIntercept = didntScale && didntDrag;
            }
            // Check to see if the user double tapped
            if (mGestureDetector != null && mGestureDetector.onTouchEvent(event)) {
                handled = true;
            }

        }

        return handled;
    }

    public void requestDisallowInterceptTouchEvent(boolean allow) {
        mAllowInterceptOnTouch = allow;
    }

    @Override
    public void onDrag(float dx, float dy) {
        if (mScaleDragDetector.isScaling()) {
            return;//在缩放状态下禁止拖动
        }
        if (getScale() <= getMinimumScale()) {
            //当前Scale小于或等于最小Scale,则执行下拉拖动关闭手势
            onPullDown(dx, dy);
        } else {
            mSuppMatrix.postTranslate(dx, dy);
            checkAndDisplayMatrix();
        }
        /*
         * Here we decide whether to let the ImageView's parent to start taking
         * over the touch event.
         *
         * First we check whether this function is enabled. We never want the
         * parent to take over if we're scaling. We then check the edge we're
         * on, and the direction of the scroll (i.e. if we're pulling against
         * the edge, aka 'overscrolling', let the parent take over).
         */
        ViewParent parent = mImageView.getParent();
        if (mAllowParentInterceptOnEdge && !mScaleDragDetector.isScaling() && !mBlockParentIntercept) {
            if (mScrollEdge == EDGE_BOTH
                    || (mScrollEdge == EDGE_LEFT && dx >= 1f)
                    || (mScrollEdge == EDGE_RIGHT && dx <= -1f)) {
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            }
        } else {
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    @Override
    public void onFling(float startX, float startY, float velocityX, float velocityY) {
        mCurrentFlingRunnable = new FlingRunnable(mImageView.getContext());
        mCurrentFlingRunnable.fling(getImageViewWidth(mImageView),
                getImageViewHeight(mImageView), (int) velocityX, (int) velocityY);
        mImageView.post(mCurrentFlingRunnable);
    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        if ((getScale() < mMaxScale || scaleFactor < 1f) && (getScale() > gestureMinScale || scaleFactor > 1f)) {
            mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            checkAndDisplayMatrix();
        }
    }

    /**
     * 下拉拖动关闭手势
     *
     * @param dx
     * @param dy
     */
    private void onPullDown(float dx, float dy) {
        if (!isPullDownAction) {
            //当前不是下拉关闭手势，先计算是否开启下拉手势
            if (Math.abs(dx) < Math.abs(dy)) {
                isPullDownAction = true;
            } else {
                checkAndDisplayMatrix();
            }
        } else {
            //当前已经是下拉手势，则计算当前下拉移动的进度值
            float mCurrentY = getValue(mDrawMatrix, Matrix.MTRANS_Y) + getDisplayRect(mBaseMatrix).height() / 2;
            float centerY = mImageView.getHeight() / 2;
            if (mCurrentY - centerY >= 0) {
                //当下拉时，改变图片的scale
                dx = dx + getValue(mSuppMatrix, Matrix.MTRANS_X);//计算当前应该移动的距离X
                dy = dy + getValue(mSuppMatrix, Matrix.MTRANS_Y);//计算当前应该移动的距离Y
                float preScale = 1 - getValue(mSuppMatrix, Matrix.MSCALE_X);//得到上一次的scale
                float minProgress = centerY / 5;//下拉进度的单位距离
                float factor = Math.abs(mCurrentY - centerY) / minProgress * 0.1f;//下拉scale进度
                if (mOnPullCloseListener != null)
                    mOnPullCloseListener.onPull(factor > 1 ? 1 : factor);
                if (factor > 1 - gestureMinScale) {
                    factor = 1 - gestureMinScale;
                }
                float scale = 1 - factor;
                mImageView.setBackgroundColor(Color.argb((int) (255 - 200 * factor), 0, 0, 0));
                //这里避免矩阵乘法导致 MTRANS_Y 错误，直接设置scale，清空其他信息
                mSuppMatrix.setScale(scale, scale);
                /**由于 在{@link #getDrawMatrix()}中 mBaseMatrix 和 mSuppMatrix 相乘导致的 MTRANS_Y先乘了scale,造成Y轴坐标不正确，在这里进行补齐 **/
                dy += getValue(mBaseMatrix, Matrix.MTRANS_Y) * (factor - preScale);
                dx += mImageView.getWidth() * (1 - scale - preScale) * .2f;
            }
            mSuppMatrix.postTranslate(dx, dy);
            setImageViewMatrix(getDrawMatrix());
        }
    }

    /**
     * 计算当前拖动的距离，如果大于 {@link #pullCloseEdge} 则执行关闭，反之则执行缩放位移动画回到原位
     */
    private void checkPullClose() {
        float mCurrentY = getValue(mDrawMatrix, Matrix.MTRANS_Y) + getDisplayRect(mBaseMatrix).height() / 2;
        float centerY = mImageView.getHeight() / 2;
        float factor = Math.abs(mCurrentY - centerY) / pullCloseEdge;
        if (factor >= 1) {
            if (mOnPullCloseListener != null) mOnPullCloseListener.onClose();
        } else {
            if (mOnPullCloseListener != null) mOnPullCloseListener.onCancel();
            isPullDownAction = false;
            RectF srcRect = new RectF(getDisplayRect(mDrawMatrix));
            RectF dstRect = new RectF(getDisplayRect(mBaseMatrix));
            float srcScale = getValue(mSuppMatrix, Matrix.MSCALE_X);
            if (srcRect != null && dstRect != null) {
                mImageView.post(new ZoomAndTransRunnable(srcRect, dstRect,
                        srcScale, mMinScale));
            }
        }

    }

    /**
     * 设置缩放
     *
     * @param scale   缩放值
     * @param focalX  基于的X点
     * @param focalY  基于的Y点
     * @param animate 是否执行缩放动画
     */
    public void setScale(float scale, float focalX, float focalY,
                         boolean animate) {
        // Check to see if the scale is within bounds
        if (scale < mMinScale || scale > mMaxScale) {
            throw new IllegalArgumentException("Scale must be within the range of minScale and maxScale");
        }
        if (animate) {
            mImageView.post(new AnimatedZoomRunnable(getScale(), scale,
                    focalX, focalY));
        } else {
            mSuppMatrix.setScale(scale, scale, focalX, focalY);
            checkAndDisplayMatrix();
        }
    }

    public Matrix getMinMatrix() {
        Matrix matrix = new Matrix();
        matrix.setScale(mMinScale, mMinScale);
        matrix.preConcat(mBaseMatrix);
        return matrix;
    }

    public float getMinimumScale() {
        return mMinScale;
    }

    public float getMediumScale() {
        return mMidScale;
    }

    public float getMaximumScale() {
        return mMaxScale;
    }

    public float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(mSuppMatrix, Matrix.MSCALE_X), 2) + (float) Math.pow(getValue(mSuppMatrix, Matrix.MSKEW_Y), 2));
    }

    /**
     * 获取矩阵的某一宫格的值
     *
     * @param matrix
     * @param whichValue
     * @return
     */
    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    public void setMinimumScale(float minimumScale) {
        Util.checkZoomLevels(minimumScale, mMidScale, mMaxScale);
        mMinScale = minimumScale;
    }

    public void setMediumScale(float mediumScale) {
        Util.checkZoomLevels(mMinScale, mediumScale, mMaxScale);
        mMidScale = mediumScale;
    }

    public void setMaximumScale(float maximumScale) {
        Util.checkZoomLevels(mMinScale, mMidScale, maximumScale);
        mMaxScale = maximumScale;
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        Util.checkZoomLevels(minimumScale, mediumScale, maximumScale);
        mMinScale = minimumScale;
        mMidScale = mediumScale;
        mMaxScale = maximumScale;
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        if (Util.isSupportedScaleType(scaleType) && scaleType != mScaleType) {
            mScaleType = scaleType;
            update(mImageView.getDrawable(), true);
        }
    }

    /************listener start*****************/
    public void setOnLongClickListener(View.OnLongClickListener listener) {
        mLongClickListener = listener;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnPullCloseListener(OnPullCloseListener listener) {
        mOnPullCloseListener = listener;
    }

    /************listener end*****************/

    /*******************动画集合*********************/
    private class AnimatedZoomRunnable implements Runnable {

        private final float mFocalX, mFocalY;
        private final long mStartTime;
        private final float mZoomStart, mZoomEnd;

        public AnimatedZoomRunnable(final float currentZoom, final float targetZoom,
                                    final float focalX, final float focalY) {
            mFocalX = focalX;
            mFocalY = focalY;
            mStartTime = System.currentTimeMillis();
            mZoomStart = currentZoom;
            mZoomEnd = targetZoom;
        }

        @Override
        public void run() {

            float t = interpolate();
            float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
            float deltaScale = scale / getScale();

            onScale(deltaScale, mFocalX, mFocalY);

            // We haven't hit our target scale yet, so post ourselves again
            if (t < 1f) {
                Compat.postOnAnimation(mImageView, this);
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / mAnimDuration;
            t = Math.min(1f, t);
            t = mInterpolator.getInterpolation(t);
            return t;
        }
    }

    private class FlingRunnable implements Runnable {

        private final OverScroller mScroller;
        private int mCurrentX, mCurrentY;

        public FlingRunnable(Context context) {
            mScroller = new OverScroller(context);
        }

        public void cancelFling() {
            mScroller.forceFinished(true);
        }

        public void fling(int viewWidth, int viewHeight, int velocityX,
                          int velocityY) {
            final RectF rect = getDisplayRect(true);
            if (rect == null) {
                return;
            }

            final int startX = Math.round(-rect.left);
            final int minX, maxX, minY, maxY;

            if (viewWidth < rect.width()) {
                minX = 0;
                maxX = Math.round(rect.width() - viewWidth);
            } else {
                minX = maxX = startX;
            }

            final int startY = Math.round(-rect.top);
            if (viewHeight < rect.height()) {
                minY = 0;
                maxY = Math.round(rect.height() - viewHeight);
            } else {
                minY = maxY = startY;
            }

            mCurrentX = startX;
            mCurrentY = startY;

            // If we actually can move, fling the scroller
            if (startX != maxX || startY != maxY) {
                mScroller.fling(startX, startY, velocityX, velocityY, minX,
                        maxX, minY, maxY, 0, 0);
            }
        }

        @Override
        public void run() {
            if (mScroller.isFinished()) {
                return; // remaining post that should not be handled
            }

            if (mScroller.computeScrollOffset()) {

                final int newX = mScroller.getCurrX();
                final int newY = mScroller.getCurrY();

                mSuppMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
                setImageViewMatrix(getDrawMatrix());

                mCurrentX = newX;
                mCurrentY = newY;

                // Post On animation
                Compat.postOnAnimation(mImageView, this);
            }
        }
    }

    private class ZoomAndTransRunnable implements Runnable {
        private final RectF srcRectF;
        private final RectF dstRectF;
        private final long mStartTime;
        private final float mStartScale;
        private final float mEndsScale;
        private final int mStartAlpha;

        public ZoomAndTransRunnable(RectF src, RectF dst, float srcScale, float dstScale) {
            this.srcRectF = src;
            this.dstRectF = dst;
            this.mStartScale = srcScale;
            this.mEndsScale = dstScale;
            this.mStartAlpha = Compat.getBackGroundAlpha(mImageView.getBackground());
            mStartTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            float t = interpolate();
            float x = srcRectF.left + t * (dstRectF.left - srcRectF.left);
            float deltaX = x - getValue(mDrawMatrix, Matrix.MTRANS_X);
            float y = srcRectF.top + t * (dstRectF.top - srcRectF.top);
            float deltaY = y - getValue(mDrawMatrix, Matrix.MTRANS_Y);

            mSuppMatrix.postTranslate(deltaX, deltaY);

            float scale = mStartScale + t * (mEndsScale - mStartScale);
            float width = srcRectF.width() * scale;
            float height = srcRectF.height() * scale;
            RectF curRectF = new RectF(deltaX, deltaY, deltaX + width, deltaY + height);
            float deltaScale = scale / getScale();
            mSuppMatrix.postScale(deltaScale, deltaScale, curRectF.centerX(), curRectF.centerY());
            setImageViewMatrix(getDrawMatrix());
            //设置背景颜色的透明度
            mImageView.setBackgroundColor(Color.argb((int) (mStartAlpha + (255 - mStartAlpha) * t), 0, 0, 0));
            if (t < 1f) {
                Compat.postOnAnimation(mImageView, this);
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / mAnimDuration;
            t = Math.min(1f, t);
            t = mInterpolator.getInterpolation(t);
            return t;
        }
    }
}
