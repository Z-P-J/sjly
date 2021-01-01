/*
 Copyright 2011, 2012 Chris Banes.
 <p>
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 <p>
 http://www.apache.org/licenses/LICENSE-2.0
 <p>
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.zpj.fragmentation.dialog.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class PlaceholderImageView2 extends AppCompatImageView implements View.OnLayoutChangeListener {

    private final Matrix mBaseMatrix = new Matrix();
    private final Matrix mDrawMatrix = new Matrix();
    private final Matrix mSuppMatrix = new Matrix();
    private final RectF mDisplayRect = new RectF();

    private ScaleType mScaleType = ScaleType.FIT_CENTER;

    public PlaceholderImageView2(Context context) {
        this(context, null);
    }

    public PlaceholderImageView2(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public PlaceholderImageView2(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        super.setScaleType(ScaleType.MATRIX);
        addOnLayoutChangeListener(this);
    }

    @Override
    public ScaleType getScaleType() {
        return mScaleType;
    }

    @Override
    public Matrix getImageMatrix() {
        return mDrawMatrix;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != null && scaleType != ScaleType.MATRIX && scaleType != mScaleType) {
            mScaleType = scaleType;
            update();
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        update();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        update();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        update();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            update();
        }
        return changed;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
            update();
        }
    }

    public boolean setSupportMatrix(final Matrix finalMatrix) {
        if (finalMatrix == null) {
            return false;
//            throw new IllegalArgumentException("Matrix cannot be null");
        }
        if (getDrawable() == null) {
            return false;
        }
        mSuppMatrix.set(finalMatrix);
        checkAndDisplayMatrix();
        return true;
    }

    private void update() {
        updateBaseMatrix(getDrawable());
    }

    private void resetMatrix() {
        mSuppMatrix.reset();
        setImageMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    private RectF getDisplayRect(Matrix matrix) {
        Drawable d = getDrawable();
        if (d != null) {
            mDisplayRect.set(0, 0, d.getIntrinsicWidth(),
                    d.getIntrinsicHeight());
            matrix.mapRect(mDisplayRect);
            return mDisplayRect;
        }
        return null;
    }

    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageMatrix(getDrawMatrix());
        }
    }

    private Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }




    private void updateBaseMatrix(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        final float viewWidth = getImageViewWidth(this);
        final float viewHeight = getImageViewHeight(this);
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        mBaseMatrix.reset();
        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;
        if (mScaleType == ScaleType.CENTER) {
            mBaseMatrix.postTranslate((viewWidth - drawableWidth) / 2F,
                    (viewHeight - drawableHeight) / 2F);

        } else if (mScaleType == ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else if (mScaleType == ScaleType.CENTER_INSIDE) {
            float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else {
            RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
            RectF mTempDst = new RectF(0, 0, viewWidth, viewHeight);
            switch (mScaleType) {
                case FIT_CENTER:
                    // for long image, 图片高>view高，比例也大于view的高/宽，则认为是长图
//                    if (drawableHeight > viewHeight && drawableHeight * 1f / drawableWidth > viewHeight * 1f / viewWidth) {
////                        mBaseMatrix.postScale(widthScale, widthScale);
////                        setScale(widthScale);
//                        //长图特殊处理，宽度撑满屏幕，并且顶部对齐
//                        isLongImage = true;
//                        mBaseMatrix.setRectToRect(mTempSrc, new RectF(0, 0, viewWidth, drawableHeight * widthScale), ScaleToFit.START);
//                    } else {
//                        mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.CENTER);
//                    }
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
        resetMatrix();
    }

    private boolean checkMatrixBounds() {
        final RectF rect = getDisplayRect(getDrawMatrix());
        if (rect == null) {
            return false;
        }
        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;
        final int viewHeight = getImageViewHeight(this);
        if (height <= viewHeight && rect.top >= 0) {
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
        } else if (rect.top >= 0) {
            deltaY = -rect.top;
        } else if (rect.bottom <= viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }
        final int viewWidth = getImageViewWidth(this);
//        Log.e("tag", "rect: " + rect.toShortString() + " viewWidth: " + viewWidth + " viewHeight: " + viewHeight
//                + " recLeft: " + rect.left + "  recRight: " + rect.right + " mHorizontalScrollEdge: " + mHorizontalScrollEdge);
        if (width <= viewWidth && rect.left >= 0) {
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
        } else if (rect.left >= 0) {
            deltaX = -rect.left;
        } else if (rect.right <= viewWidth) {
            deltaX = viewWidth - rect.right;
        }
        // Finally actually translate the matrix
        mSuppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    private int getImageViewWidth(ImageView imageView) {
        return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    private int getImageViewHeight(ImageView imageView) {
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }



}
