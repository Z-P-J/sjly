package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.shouji.market.R;

public class BottomBarTab extends LinearLayout {

    private FillImageView mIvIcon;
    private TextView mTvTitle;

    @DrawableRes
    private int mNormalIcon;

    @DrawableRes
    private int mSelectedIcon;

    @ColorInt
    private int mNormalTextColor;

    @ColorInt
    private int mSelectedTextColor;

    private int mTabPosition = -1;

    public static BottomBarTab build(Context context, String title, @DrawableRes int normalIcon, @DrawableRes int selectedIcon) {
        BottomBarTab tab = new BottomBarTab(context);
        tab.setNormalIcon(normalIcon);
        tab.setSelectedIcon(selectedIcon);
        tab.setTitle(title);
        return tab;
    }

    public BottomBarTab(Context context) {
        this(context, null);
    }

    public BottomBarTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBarTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        init(context);
    }

    private void init(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackgroundBorderless});
        Drawable drawable = typedArray.getDrawable(0);
        setBackground(drawable);
        typedArray.recycle();

        mIvIcon = new FillImageView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mIvIcon.setLayoutParams(params);
        mIvIcon.setColorFilter(ContextCompat.getColor(context, R.color.color_text_minor));
        addView(mIvIcon);

        mNormalTextColor = getResources().getColor(R.color.color_text_minor);
        mSelectedTextColor = getResources().getColor(R.color.color_tab_selected);
        mTvTitle = new TextView(context);
        mTvTitle.setTextColor(mNormalTextColor);
        mTvTitle.setTextSize(12);
        mTvTitle.getPaint().setFakeBoldText(true);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mTvTitle.setLayoutParams(params);
        addView(mTvTitle);
    }

    @Override
    public void setSelected(boolean selected) {
        mIvIcon.check(selected);
        mTvTitle.setTextColor(selected ? mSelectedTextColor : mNormalTextColor);
        super.setSelected(selected);
    }

    public void setTabPosition(int position) {
        mTabPosition = position;
        if (position == 0) {
            setSelected(true);
        }
    }

    public int getTabPosition() {
        return mTabPosition;
    }

    public void setNormalIcon(@DrawableRes int mNormalIcon) {
        this.mNormalIcon = mNormalIcon;
        mIvIcon.setNormalImage(mNormalIcon);
    }

    public void setSelectedIcon(@DrawableRes int mSelectedIcon) {
        this.mSelectedIcon = mSelectedIcon;
        mIvIcon.setCheckedImage(mSelectedIcon);
    }

    public void setTitle(String title) {
        this.mTvTitle.setText(title);
    }
}
