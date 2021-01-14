package com.zpj.shouji.market.ui.widget.indicator;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zpj.shouji.market.R;

import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

public class BadgePagerTitle extends CommonPagerTitleView {

    private final TextView tvTitle;
    private final TextView tvBadge;

    private int mSelectedColor;
    protected int mNormalColor;

    public BadgePagerTitle(Context context) {
        super(context);

        mNormalColor = mSelectedColor = Color.WHITE;

        View view = LayoutInflater.from(context).inflate(R.layout.layout_pager_title_badge, null, false);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addView(view, params);

        tvTitle = view.findViewById(R.id.tv_title);
        tvBadge = view.findViewById(R.id.tv_badge);

        hideBadge();

//        FrameLayout flContainer = new FrameLayout(context);
//        tvTitle = new TextView(context);
//        tvTitle.setTextColor(Color.WHITE);
//        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.gravity = Gravity.CENTER;
//        flContainer.addView(tvTitle, params);
//
//        int min = ScreenUtils.dp2pxInt(context, 20);
//        int padding = ScreenUtils.dp2pxInt(context, 4);
//        TextView mTvUnreadCount = new TextView(context);
//        mTvUnreadCount.setBackgroundResource(R.drawable.bg_msg_bubble);
//        mTvUnreadCount.setText("12");
//        mTvUnreadCount.setMinWidth(min);
//        mTvUnreadCount.setTextSize(10);
//        mTvUnreadCount.setTextColor(Color.WHITE);
//        mTvUnreadCount.setPadding(padding, 0, padding, 0);
//        mTvUnreadCount.setGravity(Gravity.CENTER);
//        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.gravity = Gravity.END;
//        flContainer.addView(mTvUnreadCount, layoutParams);
//
//        addView(flContainer);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(leavePercent, mSelectedColor, mNormalColor);
        tvTitle.setTextColor(color);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(enterPercent, mNormalColor, mSelectedColor);
        tvTitle.setTextColor(color);
    }

    public void setTitle(CharSequence text) {
        tvTitle.setText(text);
    }

    public void hideBadge() {
        tvBadge.setVisibility(INVISIBLE);
    }

    public void setBadgeCount(int num) {
//        if (num <= 0) {
//            tvBadge.setVisibility(INVISIBLE);
//        } else {
//            tvBadge.setVisibility(VISIBLE);
//            tvBadge.setText(String.valueOf(num));
//        }
        setBadgeText(num <= 0 ? null : String.valueOf(num));
    }

    public void setBadgeText(String text) {
        if (TextUtils.isEmpty(text)) {
            tvBadge.setVisibility(INVISIBLE);
        } else {
            tvBadge.setVisibility(VISIBLE);
            tvBadge.setText(text);
        }
    }

    public void setNormalColor(int mNormalColor) {
        this.mNormalColor = mNormalColor;
    }

    public void setSelectedColor(int mSelectedColor) {
        this.mSelectedColor = mSelectedColor;
    }
}
