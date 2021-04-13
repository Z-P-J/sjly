package com.zpj.shouji.market.ui.widget.indicator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.zpj.shouji.market.R;
import com.zpj.skin.SkinEngine;

import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class SubTitlePagerTitle extends CommonPagerTitleView {

    private final TextView tvTitle;
    private final TextView tvSubTitle;

    private int mSelectedColor;
    protected int mNormalColor;

    private int mSubSelectedColor;
    protected int mSubNormalColor;

    private Drawable subTitleBackground;

    public SubTitlePagerTitle(Context context) {
        super(context);

        mNormalColor = mSelectedColor = Color.WHITE;

        mSubNormalColor = SkinEngine.getColor(context, R.attr.textColorMinor);
//        mSubSelectedColor = SkinEngine.getColor(context, R.attr.textColorMajor);
        mSubSelectedColor = Color.WHITE;

        View view = LayoutInflater.from(context).inflate(R.layout.layout_pager_title_sub_title, null, false);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addView(view, params);

        tvTitle = view.findViewById(R.id.tv_title);
        tvSubTitle = view.findViewById(R.id.tv_sub_title);

        hideSubTitle();

        subTitleBackground = context.getResources().getDrawable(R.drawable.bg_msg_bubble);

    }

    @Override
    public void onSelected(int index, int totalCount) {
        super.onSelected(index, totalCount);
//        tvSubTitle.setBackgroundResource(R.drawable.bg_msg_bubble);
//        tvSubTitle.setBackground(subTitleBackground);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        super.onDeselected(index, totalCount);
//        tvSubTitle.setBackground(null);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(leavePercent, mSelectedColor, mNormalColor);
        tvTitle.setTextColor(color);
        int subColor = ArgbEvaluatorHolder.eval(leavePercent, mSubSelectedColor, mSubNormalColor);
        tvSubTitle.setTextColor(subColor);
        subTitleBackground.setAlpha((int) ((1 - leavePercent) * 255));
        tvSubTitle.setBackground(subTitleBackground);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(enterPercent, mNormalColor, mSelectedColor);
        tvTitle.setTextColor(color);
        int subColor = ArgbEvaluatorHolder.eval(enterPercent, mSubNormalColor, mSubSelectedColor);
        tvSubTitle.setTextColor(subColor);

        subTitleBackground.setAlpha((int) (enterPercent * 255));
        tvSubTitle.setBackground(subTitleBackground);
    }

    public void setTitle(CharSequence text) {
        tvTitle.setText(text);
    }

    public void hideSubTitle() {
        tvSubTitle.setVisibility(INVISIBLE);
    }

    public void setSubCount(int num) {
        setSubText(num < 0 ? null : String.valueOf(num));
    }

    public void setSubText(String text) {
        if (TextUtils.isEmpty(text)) {
            tvSubTitle.setVisibility(GONE);
        } else {
            tvSubTitle.setVisibility(VISIBLE);
            tvSubTitle.setText(text);
        }
    }

    public void setNormalColor(int mNormalColor) {
        this.mNormalColor = mNormalColor;
    }

    public void setSelectedColor(int mSelectedColor) {
        this.mSelectedColor = mSelectedColor;
    }

    public void setSubNormalColor(int mSubNormalColor) {
        this.mSubNormalColor = mSubNormalColor;
    }

    public void setSubSelectedColor(int mSubSelectedColor) {
        this.mSubSelectedColor = mSubSelectedColor;
    }
}
