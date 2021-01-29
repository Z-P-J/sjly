package com.zpj.shouji.market.ui.widget.count;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zpj.shouji.market.R;

/**
 * Created by sunbinqiang on 16/10/2017.
 * github: https://github.com/binqiangsun/IconCountView
 */
public class IconCountView extends LinearLayout {

    protected boolean mIsSelected;
    protected final ImageView mImageView;
    protected final CountView mCountView;
    protected int textSelectedColor;
    private int mNormalRes;
    private int mSelectedRes;
    private OnSelectedStateChangedListener mSelectedStateChanged;

    public IconCountView(Context context) {
        this(context, null);
    }

    public IconCountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconCountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_icon_count, this);
        mCountView = view.findViewById(R.id.count_view);
        mImageView = view.findViewById(R.id.image_view);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconCountView, defStyleAttr, 0);
        boolean isChecked = a.getBoolean(R.styleable.IconCountView_checked, false);
        int normalRes = a.getResourceId(R.styleable.IconCountView_normalRes, R.drawable.ic_good);
        int selectedRes = a.getResourceId(R.styleable.IconCountView_selectedRes, R.drawable.ic_good_checked);
        long count = a.getInt(R.styleable.IconCountView_count, 0);
        String zeroText = a.getString(R.styleable.IconCountView_zeroText);
        int textNormalColor = a.getColor(R.styleable.IconCountView_textNormalColor, Color.LTGRAY);
        textSelectedColor = a.getColor(R.styleable.IconCountView_textSelectedColor, Color.RED);
        int textSize = a.getDimensionPixelSize(R.styleable.IconCountView_textSize, getResources().getDimensionPixelOffset(R.dimen.text_size_normal));
        a.recycle();

        //设置初始状态
        setIconRes(normalRes, selectedRes);
        initCountView(zeroText, count, textNormalColor, textSelectedColor, textSize, isChecked);
        setSelected(isChecked);


        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                praiseChange(!mIsSelected);
            }
        });
    }

    //初始化icon
    public void setIconRes(int normalRes, int selectedRes) {
        mNormalRes = normalRes;
        mSelectedRes = selectedRes;
        mImageView.setImageResource(mNormalRes);
    }

    //初始化countView
    private void initCountView(String zeroText,
                               long count,
                               int textNormalColor,
                               int textSelectedColor,
                               int textSize,
                               boolean isSelected) {
        mCountView.setZeroText(zeroText);
        mCountView.setCount(count);
        mCountView.setTextNormalColor(textNormalColor);
        mCountView.setTextSelectedColor(textSelectedColor);
        mCountView.setTextSize(textSize);
        mCountView.setIsSelected(isSelected);
    }

    //初始化数量
    public void setCount(long count) {
        mCountView.setCount(count);
    }

    public long getCount() {
        return mCountView.getCount();
    }

    //初始化数量为0时的文字
    public void setZeroText(String zeroText) {
        mCountView.setZeroText(zeroText);
    }

    //初始化状态
    public void setState(boolean isSelected) {
        mIsSelected = isSelected;
        mImageView.setImageResource(mIsSelected ? mSelectedRes : mNormalRes);
        mCountView.setIsSelected(isSelected);
    }

    /**
     * 设置回调接口
     * @param onStateChangedListener
     */
    public void setOnStateChangedListener (OnSelectedStateChangedListener onStateChangedListener) {
        mSelectedStateChanged = onStateChangedListener;
    }

    //状态变化
//    private void praiseChange(boolean isPraised) {
//        mIsSelected = isPraised;
//        //icon变化
//        mImageView.setImageResource(isPraised ? mSelectedRes : mNormalRes);
//        animImageView(isPraised);
//        //数字变化
//        if (isPraised) {
//            mCountView.addCount();
//        } else {
//            mCountView.minusCount();
//        }
//        mCountView.setIsSelected(isPraised);
//        //接口回调
//        if (mSelectedStateChanged != null) {
//            mSelectedStateChanged.select(mIsSelected);
//        }
//    }

    private void praiseChange(boolean isPraised) {
        animImageView(isPraised);
        //数字变化
        if (isPraised) {
            mCountView.addCount();
        } else {
            mCountView.minusCount();
        }
        setState(isPraised);
        //接口回调
        if (mSelectedStateChanged != null) {
            mSelectedStateChanged.select(mIsSelected);
        }
    }

    /**
     * 点赞icon动画
     * @param isPraised
     */
    private void animImageView(boolean isPraised) {
        //图片动画
        float toScale = isPraised ? 1.2f : 0.9f;
        PropertyValuesHolder propertyValuesHolderX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, toScale, 1.0f);
        PropertyValuesHolder propertyValuesHolderY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, toScale, 1.0f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mImageView,
                propertyValuesHolderX, propertyValuesHolderY);
        objectAnimator.start();
    }

    /**
     * 外部回调
     * 例如：处理点赞事件的网络请求
     */
    public interface OnSelectedStateChangedListener {
        void select(boolean isSelected);
    }
}
