package com.zpj.shouji.market.ui.widget.ninegrid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.transition.Transition;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.glide.ImageViewDrawableTarget;
import com.zpj.utils.ScreenUtils;
import com.zxy.skin.sdk.SkinEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Ziwen Lan
 * Date : 2020/3/30
 * Time : 14:43
 * Introduction :
 * 仿钉钉圈子九宫图样式
 * 该view继承于CardView以便于实现四周圆角效果，并在初始化时取消掉了cardView自带的阴影效果
 * 最多加载九张图片，超过九张只显示前九张;
 *
 * modified by : Z-P-J
 * 新增编辑模式，代码优化，性能优化
 */
public class NineGridView extends CardView {

    /**
     * 间隔
     */
    private int mSpace;
    /**
     * 多余的间隔
     */
    private int mOverSpace;
    /**
     * 最多加载九张图片，超过九张只显示前九张
     */
    private final List<String> mUrls = new ArrayList<>();
//    private final List<NineGirdImageContainer> mImageViews = new ArrayList<>();
    /**
     * 单张大图
     */
    private NineGirdImageContainer mBigImageView;
    private int bigPictureMaxHeight;
    private int minWidth;
    private int bigPictureMinHeight;
    /**
     * 九张图片时的ImageView宽度，定为基础长度
     */
    private int mBaseLength;

    private NineGirdImageContainer addImageView;

    /**
     * 当前view实际展示宽度(已减去左右padding)
     */
    private int mMeasuredShowWidth;
    private int mPaddingTop, mPaddingLeft, mPaddingRight, mPaddingBottom;

    private boolean isEditMode = false;


    public NineGridView(Context context) {
        this(context, null);
    }

    public NineGridView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NineGridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //取消阴影效果
        setCardElevation(0f);
//        setUseCompatPadding(true);
        setCardBackgroundColor(Color.TRANSPARENT);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NineGridView, defStyleAttr,
                R.style.CardView);
        mSpace = typedArray.getDimensionPixelOffset(R.styleable.NineGridView_pictureSpace, 4);
        bigPictureMaxHeight = typedArray.getDimensionPixelOffset(R.styleable.NineGridView_bigPictureMaxHeight, ScreenUtils.getScreenHeight() / 2);
        bigPictureMinHeight = typedArray.getDimensionPixelOffset(R.styleable.NineGridView_bigPictureMinHeight, 100);
        typedArray.recycle();

        mPaddingTop = getContentPaddingTop();
        mPaddingLeft = getContentPaddingLeft();
        mPaddingRight = getContentPaddingRight();
        mPaddingBottom = getContentPaddingBottom();

        if (getLayoutParams() == null) {
            setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        minWidth = (int) ((float)bigPictureMaxHeight / ScreenUtils.getScreenHeight() * ScreenUtils.getScreenWidth());
        setMinimumWidth(minWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获得父View提供的可绘制宽高(含padding)
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthSize < minWidth) {
            widthSize = minWidth;
        }
        if (getChildCount() == 0) {
            setMeasuredDimension(widthSize, getRealHeight());
        } else if (getChildCount() == 1) {
            Log.d("NineGridView", "onMeasure widthMeasureSpec=" + widthMeasureSpec + " heightMeasureSpec=" + heightMeasureSpec);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            View childView = getChildAt(0);
//            mMeasuredShowWidth = widthSize - this.mPaddingLeft - mPaddingRight;
//
//
//            childView.measure(MeasureSpec.makeMeasureSpec(mMeasuredShowWidth, MeasureSpec.UNSPECIFIED),
//                    MeasureSpec.makeMeasureSpec(bigPictureMaxHeight, MeasureSpec.UNSPECIFIED));
//            setMeasuredDimension(MeasureSpec.makeMeasureSpec(mMeasuredShowWidth, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(bigPictureMaxHeight, MeasureSpec.UNSPECIFIED));


            View child = getChildAt(0);

            int height;
            int width;
            if (isEditMode) {
                height = widthSize / 2;
                width = widthSize / 2;
            } else {
                NineGirdImageContainer container = (NineGirdImageContainer) child;
                Drawable resource = container.getImageView().getDrawable();

                if (resource == null) {
                    width = widthSize;
                    height = width;
                } else {
                    height = resource.getIntrinsicHeight();
                    width = resource.getIntrinsicWidth();
                }
                if (width > widthSize) {
                    height = (int) ((float) widthSize / width * height);
                    width = getMeasuredWidth();
                } else if (width * 2 < widthSize) {
                    widthSize = widthSize / 2;
                    height = (int) ((float) widthSize / width * height);
                    width = widthSize;

                }

                if (height > bigPictureMaxHeight) {
                    width = (int) ((float) bigPictureMaxHeight / height * width);
                    height = bigPictureMaxHeight;
                }
            }




            child.measure(width, height);
            setMeasuredDimension(width, height);

        } else {
            //绘制子view宽高
            measureChildView(widthSize);
            setMeasuredDimension(widthSize, getRealHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getChildCount() > 1) {
            //摆放子view位置
            layoutChildView();
        }
    }

    /**
     * 根据子view获得真实高度
     */
    private int getRealHeight() {
        int realHeight = 0;
        switch (getChildCount()) {
            default:
            case 0:
            case 1:
                break;
            case 2:
                realHeight = ((mMeasuredShowWidth - mSpace) / 2);
                break;
            case 3:
                realHeight = mSpace + (mBaseLength * 2);
                break;
            case 4:
                realHeight = mMeasuredShowWidth;
                break;
            case 5:
                realHeight = ((mMeasuredShowWidth - mSpace) / 2) + mSpace + mBaseLength;
                break;
            case 6:
                realHeight = (mBaseLength * 3) + (2 * mSpace);
                break;
            case 7:
                realHeight = (2 * mSpace) + (int) (3.5 * mBaseLength);
                break;
            case 8:
                realHeight = ((mMeasuredShowWidth - mSpace) / 2) + (2 * mSpace) + (2 * mBaseLength);
                break;
            case 9:
                realHeight = (mBaseLength * 3) + (mSpace * 2);
                break;
        }
        realHeight = realHeight + mPaddingTop + mPaddingBottom;
        return realHeight;
    }

    /**
     * 计算子View宽高
     *
     * @param widthSize 当前view可使用的最大绘制宽度(含padding)
     */
    private void measureChildView(int widthSize) {
        //当前View实际可展示内容宽度
        mMeasuredShowWidth = widthSize - this.mPaddingLeft - mPaddingRight;
        //九宫图单张图片基本长度
        mBaseLength = (mMeasuredShowWidth - (2 * mSpace)) / 3;
        mOverSpace = (mMeasuredShowWidth - (2 * mSpace)) % 3;
        switch (getChildCount()) {
            default:
            case 0:
            case 1:
                break;
            case 2:
                int length = (mMeasuredShowWidth - mSpace) / 2;
                mOverSpace = (mMeasuredShowWidth - mSpace) % 2;
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    int width = 0;
                    int height = 0;
                    if (i < 2) {
                        width = length;
                        height = length;
                    }
                    childView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                }
                break;
            case 3:
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    int width = 0;
                    int height = 0;
                    if (i == 0) {
                        width = mMeasuredShowWidth - mSpace - mBaseLength;
                        height = (2 * mBaseLength) + mSpace;
                    } else if (i < 3) {
                        width = mBaseLength;
                        height = mBaseLength;
                    }
                    childView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                }
                break;
            case 4:
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    int width = 0;
                    int height = 0;
                    if (i == 0) {
                        width = mMeasuredShowWidth;
                        height = mMeasuredShowWidth - mSpace - mBaseLength;
                    } else if (i < 4) {
                        width = mBaseLength;
                        height = mBaseLength;
                    }
                    childView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                }
                break;
            case 5:
                mOverSpace = (mMeasuredShowWidth - mSpace) % 2;
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    int width = 0;
                    int height = 0;
                    if (i < 2) {
                        width = (mMeasuredShowWidth - mSpace) / 2;
                        height = (mMeasuredShowWidth - mSpace) / 2;
                    } else if (i < 5) {
                        width = mBaseLength;
                        height = mBaseLength;
                    }
                    childView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                }
                break;
            case 6:
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    int width = 0;
                    int height = 0;
                    if (i == 0) {
                        width = mMeasuredShowWidth - mSpace - mBaseLength;
                        height = (2 * mBaseLength) + mSpace;
                    } else if (i < 6) {
                        width = mBaseLength;
                        height = mBaseLength;
                    }
                    childView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                }
                break;
            case 7:
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    int width = 0;
                    int height = 0;
                    if (i == 0) {
                        width = mMeasuredShowWidth;
                        height = (int) (1.5 * mBaseLength);
                    } else if (i < 7) {
                        width = mBaseLength;
                        height = mBaseLength;
                    }
                    childView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                }
                break;
            case 8:
                mOverSpace = (mMeasuredShowWidth - mSpace) % 2;
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    int width = 0;
                    int height = 0;
                    if (i < 2) {
                        width = (mMeasuredShowWidth - mSpace) / 2;
                        height = (mMeasuredShowWidth - mSpace) / 2;
                    } else if (i < 8) {
                        width = mBaseLength;
                        height = mBaseLength;
                    }
                    childView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                }
                break;
            case 9:
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    childView.measure(MeasureSpec.makeMeasureSpec(mBaseLength, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(mBaseLength, MeasureSpec.EXACTLY));
                }
                break;
        }
    }

    /**
     * 子View位置摆放
     */
    private void layoutChildView() {
        //布局时要考虑 padding
        switch (getChildCount()) {
            default:
            case 0:
            case 1:
                break;
            case 2:
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    if (i < 2) {
                        int width = childView.getMeasuredWidth();
                        childView.layout(mPaddingLeft + (i * (width + mSpace + mOverSpace)),
                                mPaddingTop,
                                mPaddingLeft + (i * (width + mSpace + mOverSpace)) + width,
                                mPaddingTop + width);
                    } else {
                        childView.layout(0, 0, 0, 0);
                    }
                }
                break;
            case 3:
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    if (i < 3) {
                        int width = childView.getMeasuredWidth();
                        int height = childView.getMeasuredHeight();
                        if (i == 0) {
                            childView.layout(mPaddingLeft, mPaddingTop, width + mPaddingLeft, mPaddingTop + height);
                        } else {
                            int count = i - 1;
                            childView.layout(mPaddingLeft + mMeasuredShowWidth - width,
                                    mPaddingTop + (count * mSpace) + (count * height),
                                    mPaddingLeft + mMeasuredShowWidth,
                                    mPaddingTop + ((count + 1) * height) + (count * mSpace));
                        }
                    } else {
                        childView.layout(0, 0, 0, 0);
                    }
                }
                break;
            case 4:
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    if (i < 4) {
                        int width = childView.getMeasuredWidth();
                        int height = childView.getMeasuredHeight();
                        if (i == 0) {
                            childView.layout(mPaddingLeft, mPaddingTop, width + mPaddingLeft, mPaddingTop + height);
                        } else {
                            int count = i - 1;
                            childView.layout(mPaddingLeft + (count * mSpace) + (count * width),
                                    mMeasuredShowWidth - mBaseLength + mPaddingTop,
                                    mPaddingLeft + (count * mSpace) + ((count + 1) * width),
                                    mMeasuredShowWidth + mPaddingTop);
                        }
                    } else {
                        childView.layout(0, 0, 0, 0);
                    }
                }
                break;
            case 5:
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    if (i < 5) {
                        int width = childView.getMeasuredWidth();
                        if (i < 2) {
                            childView.layout(mPaddingLeft + (i * (width + mSpace + mOverSpace)),
                                    mPaddingTop,
                                    mPaddingLeft + (i * (width + mSpace + mOverSpace)) + width,
                                    mPaddingTop + width);
                        } else {
                            int count = i - 2;
                            childView.layout(mPaddingLeft + (count * mSpace) + (count * width),
                                    mPaddingTop + ((mMeasuredShowWidth - mSpace) / 2) + mSpace,
                                    mPaddingLeft + (count * mSpace) + ((count + 1) * width),
                                    mPaddingTop + ((mMeasuredShowWidth - mSpace) / 2) + mSpace + width);
                        }
                    } else {
                        childView.layout(0, 0, 0, 0);
                    }
                }
                break;
            case 6:
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    if (i < 6) {
                        int width = childView.getMeasuredWidth();
                        int height = childView.getMeasuredHeight();
                        if (i == 0) {
                            childView.layout(mPaddingLeft, mPaddingTop, mPaddingLeft + width, height + mPaddingTop);
                        } else if (i < 3) {
                            int count = i - 1;
                            childView.layout(mPaddingLeft + mMeasuredShowWidth - width,
                                    mPaddingTop + (count * mSpace) + (count * height),
                                    mPaddingLeft + mMeasuredShowWidth,
                                    mPaddingTop + ((count + 1) * height) + (count * mSpace));
                        } else {
                            int count = i - 3;
                            childView.layout(mPaddingLeft + (count * mSpace) + (count * width),
                                    mPaddingTop + (2 * mSpace) + (2 * mBaseLength),
                                    mPaddingLeft + (count * mSpace) + ((count + 1) * width),
                                    mPaddingTop + mMeasuredShowWidth);
                        }
                    } else {
                        childView.layout(0, 0, 0, 0);
                    }
                }
                break;
            case 7:
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    if (i < 7) {
                        int width = childView.getMeasuredWidth();
                        int height = childView.getMeasuredHeight();
                        if (i == 0) {
                            childView.layout(mPaddingLeft, mPaddingTop, mPaddingLeft + width, height + mPaddingTop);
                        } else if (i < 4) {
                            int count = i - 1;
                            childView.layout(mPaddingLeft + (count * mSpace) + (count * width),
                                    mPaddingTop + (int) (1.5 * height) + mSpace,
                                    mPaddingLeft + (count * mSpace) + ((count + 1) * width),
                                    mPaddingTop + (int) (2.5 * height) + mSpace);
                        } else {
                            int count = i - 4;
                            childView.layout(mPaddingLeft + (count * mSpace) + (count * width),
                                    mPaddingTop + (int) (2.5 * height) + (2 * mSpace),
                                    mPaddingLeft + (count * mSpace) + ((count + 1) * width),
                                    mPaddingTop + (int) (3.5 * height) + (2 * mSpace));
                        }
                    } else {
                        childView.layout(0, 0, 0, 0);
                    }
                }
                break;
            case 8:
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    if (i < 8) {
                        int width = childView.getMeasuredWidth();
                        if (i < 2) {
                            childView.layout(mPaddingLeft + (i * (width + mSpace + mOverSpace)),
                                    mPaddingTop,
                                    mPaddingLeft + (i * (width + mSpace + mOverSpace)) + width,
                                    mPaddingTop + width);
                        } else if (i < 5) {
                            int count = i - 2;
                            childView.layout(mPaddingLeft + (count * mSpace) + (count * width),
                                    mPaddingTop + ((mMeasuredShowWidth - mSpace) / 2) + mSpace,
                                    mPaddingLeft + (count * mSpace) + ((count + 1) * width),
                                    mPaddingTop + ((mMeasuredShowWidth - mSpace) / 2) + mSpace + width);
                        } else {
                            int count = i - 5;
                            childView.layout(mPaddingLeft + (count * mSpace) + (count * width),
                                    mPaddingTop + ((mMeasuredShowWidth - mSpace) / 2) + width + (2 * mSpace),
                                    mPaddingLeft + (count * mSpace) + ((count + 1) * width),
                                    mPaddingTop + ((mMeasuredShowWidth - mSpace) / 2) + (2 * width) + (2 * mSpace));
                        }
                    } else {
                        childView.layout(0, 0, 0, 0);
                    }
                }
                break;
            case 9:
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    int width = childView.getMeasuredWidth();
                    int height = childView.getMeasuredHeight();
                    if (i < 3) {
                        childView.layout(mPaddingLeft + (i * mSpace) + (i * width),
                                mPaddingTop,
                                mPaddingLeft + (i * mSpace) + ((i + 1) * width),
                                mPaddingTop + height);
                    } else if (i < 6) {
                        int count = i - 3;
                        childView.layout(mPaddingLeft + (count * mSpace) + (count * width),
                                mPaddingTop + height + mSpace,
                                mPaddingLeft + (count * mSpace) + ((count + 1) * width),
                                mPaddingTop + (2 * height) + mSpace);
                    } else {
                        int count = i - 6;
                        childView.layout(mPaddingLeft + (count * mSpace) + (count * width),
                                mPaddingTop + (2 * height) + (2 * mSpace),
                                mPaddingLeft + (count * mSpace) + ((count + 1) * width),
                                mPaddingTop + (3 * height) + (2 * mSpace));
                    }
                }
                break;
        }
    }

    public void setUrls(List<String> urls) {
        if (urls == null) {
            return;
        }
        removeAllViews();
        mUrls.clear();
        mBigImageView = null;
//        mImageViews.clear();
        if (urls.size() > 9) {
            urls = urls.subList(0, 9);
        }
        if (!urls.isEmpty()) {
            mUrls.addAll(urls);
        }

        if (mUrls.size() == 1 && !isEditMode) {
            getLayoutParams().width = LayoutParams.WRAP_CONTENT;
        } else {
            getLayoutParams().width = LayoutParams.MATCH_PARENT;
        }

        for (int i = 0; i < mUrls.size(); i++) {
            NineGirdImageContainer container = new NineGirdImageContainer(getContext());
            container.setIsDeleteMode(isEditMode);
            container.getImageView().setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.getImageView().setOnClickListener(v -> {
                if (callback != null) {
                    callback.onImageItemClicked(indexOfChild(container), mUrls);
                }
            });
            container.setOnClickDeleteListener(() -> {
                int pos = indexOfChild(container);
                String url = mUrls.remove(pos);
//                mImageViews.remove(imageView);
                removeView(container);
                addEditImageView();
                if (callback != null) {
                    callback.onImageItemDelete(pos, url);
                }
            });
            addView(container);
            Glide.with(this)
                    .load(mUrls.get(i))
                    .apply(GlideRequestOptions.getImageOption())
                    .into(new ImageViewDrawableTarget(container.getImageView()) {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            super.onResourceReady(resource, transition);
                            if (resource instanceof GifDrawable) {
                                container.showGifIcon();
                            }
                        }
                    });
        }

        setEditMode(isEditMode);
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
        removeEditImageView();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof NineGirdImageContainer) {
                ((NineGirdImageContainer) child).setIsDeleteMode(editMode);
            }
        }
        if (editMode) {
            addEditImageView();
        } else {
            removeEditImageView();
        }
    }

    public void addUrl(String url) {

    }

    private void addEditImageView() {
        if (!isEditMode || addImageView != null) {
            return;
        }
        removeEditImageView();
        if (getChildCount() < 9) {
            addImageView = new NineGirdImageContainer(getContext()) {
                @Override
                public void setIsDeleteMode(boolean b) {
                    this.mIsDeleteMode = b;
                    this.requestLayout();
                }
            };
            addImageView.getImageView().setImageResource(R.drawable.ic_add_picture);
//            addImageView.getImageView().setColorFilter();
            SkinEngine.setTint(addImageView.getImageView(), R.attr.textColorNormal);
            addImageView.getImageView().setScaleType(ImageView.ScaleType.FIT_XY);
            addImageView.setIsDeleteMode(true);
            addImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.onAddItemClicked(indexOfChild(addImageView));
                    }
                }
            });
            addView(addImageView);
        }
    }

    private void removeEditImageView() {
        if (addImageView != null) {
            removeView(addImageView);
            addImageView = null;
        }
    }

    public ImageView getImageView(int pos) {
        View view = getChildAt(pos);
        if (view instanceof NineGirdImageContainer) {
            return ((NineGirdImageContainer) view).getImageView();
        }
        return null;
//        if (mUrls.size() == 1) {
//            return mBigImageView.getImageView();
//        }
//        return mImageViews.get(pos).getImageView();
    }


    //--------  点击事件回调  --------

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onImageItemClicked(int position, List<String> urls);
        void onAddItemClicked(int position);
        void onImageItemDelete(int position, String url);
    }

    public abstract static class SimpleCallback implements Callback {

        @Override
        public void onAddItemClicked(int position) {

        }

        @Override
        public void onImageItemDelete(int position, String url) {

        }
    }

}
