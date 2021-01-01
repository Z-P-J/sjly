package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.zpj.shouji.market.R;
import com.zpj.utils.ScreenUtils;

public class NineGirdImageContainer extends FrameLayout {

    private int mWidth = 0, mHeight = 0;
    private final ImageView mImageView;
    private ImageView mImgDelete;
    private ImageView gifIcon;
    protected boolean mIsDeleteMode;
    private onClickDeleteListener mListener;
    private int mImageWidth, mImageHeight;
    private int mIcDelete = R.drawable.ic_delete;
    private float mRatio = 0.35f;

    public NineGirdImageContainer(Context context) {
        this(context, null);
    }

    public NineGirdImageContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        mImageView = new ImageView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(mImageView, params);
        setIsDeleteMode(mIsDeleteMode);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);


        //根据模式设置显示图片的尺寸
        mImageWidth = 0;
        mImageHeight = 0;
        int imgMode = MeasureSpec.EXACTLY, imgWidthSpec = 0, imgHeightSpec = 0;
        if (mIsDeleteMode) {

//            if (mImgDelete != null) {
//                //设置删除图片的尺寸
//                int delSize = (int) (mWidth * mRatio);
//                int delMode = MeasureSpec.EXACTLY;
//                int delSpec = MeasureSpec.makeMeasureSpec(delSize, delMode);
//                mImgDelete.measure(delSpec, delSpec);
//            }

            int size = ScreenUtils.dp2pxInt(getContext(), 16);
            mImageWidth = mWidth - size;
            mImageHeight = mHeight - size;
            if (gifIcon != null) {
                gifIcon.setTranslationX(size / 2f);
            }

//            mImageWidth = mWidth * 4 / 5;
//            mImageHeight = mHeight * 4 / 5;
        } else {
            mImageWidth = mWidth;
            mImageHeight = mHeight;
            if (gifIcon != null) {
                gifIcon.setTranslationX(0);
            }
        }
        imgWidthSpec = MeasureSpec.makeMeasureSpec(mImageWidth, imgMode);
        imgHeightSpec = MeasureSpec.makeMeasureSpec(mImageHeight, imgMode);
        mImageView.measure(imgWidthSpec, imgHeightSpec);
    }

    public void showGifIcon() {
        gifIcon = new ImageView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        int margin = ScreenUtils.dp2pxInt(getContext(), 8);
        params.setMargins(margin, margin, margin, margin);
        addView(gifIcon, params);
        gifIcon.setImageResource(R.drawable.ic_gif);
    }

//    public int getImageWidth() {
//        return mImageWidth;
//    }
//
//    public int getImageHeight() {
//        return mImageHeight;
//    }

    /**
     * 设置显示图片的ScaleType
     */
    private void setScaleType(ImageView.ScaleType scanType) {
        if (mImageView != null) {
            mImageView.setScaleType(scanType);
        }
    }

    /**
     * 设置是否为删除模式
     */
    public void setIsDeleteMode(boolean b) {
        this.mIsDeleteMode = b;
        if (mIsDeleteMode) {
            if (mImgDelete == null) {
                mImgDelete = new ImageView(getContext());
                mImgDelete.setScaleType(ImageView.ScaleType.FIT_XY);
                mImgDelete.setImageResource(mIcDelete);
                mImgDelete.setOnClickListener(view -> {
                    if (mListener != null) {
                        mListener.onClickDelete();
                    }
                });
                int size = ScreenUtils.dp2pxInt(getContext(), 24);
                LayoutParams params = new LayoutParams(size, size);
                params.gravity = Gravity.TOP | Gravity.END;
                addView(mImgDelete, params);

//                int delSize = (int) (mWidth * mRatio);
//                int delMode = MeasureSpec.EXACTLY;
//                int delSpec = MeasureSpec.makeMeasureSpec(delSize, delMode);
//                mImgDelete.measure(delSpec, delSpec);
            }
        } else {
            if (mImgDelete != null) {
                removeView(mImgDelete);
            }
        }
        requestLayout();
    }

    /**
     * 设置删除图片的资源id
     */
    public void setDeleteIcon(int resId) {
        this.mIcDelete = resId;
        if (mImgDelete != null) {
            mImgDelete.setImageResource(resId);
//            if (resId > 0) {
//                mImgDelete.setImageResource(resId);
//            } else {
//                removeView(mImgDelete);
//            }
        }
    }

    /**
     * 当前是否为删除模式
     */
    public boolean isDeleteMode() {
        return mIsDeleteMode;
    }

    /**
     * 设置删除图片的尺寸占父容器比例
     *
     * @param ratio
     */
    public void setRatioOfDeleteIcon(float ratio) {
        this.mRatio = ratio;
    }

    /**
     * 获取实际显示图片的ImageView
     */
    public ImageView getImageView() {
        return mImageView;
    }

    public interface onClickDeleteListener {
        void onClickDelete();
    }

    public void setOnClickDeleteListener(onClickDeleteListener listener) {
        this.mListener = listener;
    }

}
