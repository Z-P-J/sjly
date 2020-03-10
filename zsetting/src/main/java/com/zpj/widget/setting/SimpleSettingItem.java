package com.zpj.widget.setting;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;


/**
 * 作者：Leon
 * 时间：2016/12/21 10:32
 * Modified by Z-P-J
 */
public class SimpleSettingItem extends AppCompatTextView {

    protected String mTitleText;
    protected float mTitleTextSize;
    protected int mTitleTextColor;

    protected Drawable mLeftIcon;
    protected Drawable mRightIcon;

    public SimpleSettingItem(Context context) {
        this(context, null);
    }

    public SimpleSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleSettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (getMinimumHeight() == 0) {
            setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.z_setting_item_min_height));
        }
        if (getPaddingStart() == 0 || getPaddingEnd() == 0) {
            int padding = getResources().getDimensionPixelSize(R.dimen.z_setting_item_default_padding);
            setPadding(padding, getPaddingTop(), padding, getPaddingBottom());
        }

        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        getPaint().setFakeBoldText(true);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SimpleSettingItem);
        mTitleText = array.getString(R.styleable.SimpleSettingItem_z_setting_titleText);
        if (TextUtils.isEmpty(mTitleText)) {
            mTitleText = "Title";
        }
        setText(mTitleText);

        mTitleTextSize = array.getDimension(R.styleable.SimpleSettingItem_z_setting_titleTextSize, 14);

        setTextSize(mTitleTextSize);

        mTitleTextColor = array.getColor(R.styleable.SimpleSettingItem_z_setting_titleTextColor, Color.parseColor("#222222"));

        setTextColor(mTitleTextColor);

        Drawable background = array.getDrawable(R.styleable.SimpleSettingItem_z_setting_background);

        mLeftIcon = array.getDrawable(R.styleable.SimpleSettingItem_z_setting_leftIcon);
        mRightIcon = array.getDrawable(R.styleable.SimpleSettingItem_z_setting_rightIcon);
        array.recycle();

        if (mRightIcon == null) {
            mRightIcon = getResources().getDrawable(R.drawable.ic_keyboard_arrow_right_grey_24dp);
        }
        setCompoundDrawablesWithIntrinsicBounds(mLeftIcon, null, mRightIcon, null);

        setGravity(Gravity.CENTER_VERTICAL);

        if (background == null) {
            TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
            background = typedArray.getDrawable(0);
            typedArray.recycle();
//            TypedValue typedValue = new TypedValue();
//            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
//            int[] attribute = new int[]{android.R.attr.selectableItemBackground};
//            TypedArray typedArray = context.getTheme().obtainStyledAttributes(typedValue.resourceId, attribute);
//            background = typedArray.getDrawable(0);
//            typedArray.recycle();
        }

        setBackground(background);

    }


}

