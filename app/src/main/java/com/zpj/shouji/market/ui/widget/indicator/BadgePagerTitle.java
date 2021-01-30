package com.zpj.shouji.market.ui.widget.indicator;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.zpj.shouji.market.R;

import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class BadgePagerTitle extends CommonPagerTitleView {

    private final TextView tvTitle;
    private final TextView tvBadge;

    private int mSelectedColor;
    protected int mNormalColor;

    private boolean mAutoCancelBadge = true;

    private boolean isAdjustMode;

    private Badge badge;

    public BadgePagerTitle(Context context) {
        super(context);

//        mNormalColor = mSelectedColor = Color.WHITE;
        setNormalColor(context.getResources().getColor(R.color.middle_gray_1));
        setSelectedColor(context.getResources().getColor(R.color.colorPrimary));

        View view = LayoutInflater.from(context).inflate(R.layout.layout_pager_title_badge, null, false);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addView(view, params);

        tvTitle = view.findViewById(R.id.tv_title);
        tvBadge = view.findViewById(R.id.tv_badge);

        hideBadge();

        badge = new QBadgeView(context)
                .setBadgeTextSize(12, true)
//                .setBadgeGravity(Gravity.CENTER | Gravity.TOP)
                .bindTarget(view);

    }

    @Override
    public void onSelected(int index, int totalCount) {
        super.onSelected(index, totalCount);
        if (mAutoCancelBadge) {
            hideBadge();
            badge.hide(true);
        }
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

    public void setAdjustMode(boolean adjustMode) {
        isAdjustMode = adjustMode;
    }

    public void setBadgeCount(int num) {
        setBadgeText(num <= 0 ? null : String.valueOf(num));
    }

    public void setBadgeText(String text) {
        if (isAdjustMode) {
            badge.setBadgeGravity(Gravity.CENTER | Gravity.TOP);
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    badge.setGravityOffset(tvTitle.getMeasuredWidth() / 2f, 0, false);
                    badge.setBadgeText(text);
                }
            });
        } else {
            badge.setBadgeGravity(Gravity.END | Gravity.TOP);
            badge.setBadgeText(text);
        }

//        if (TextUtils.isEmpty(text)) {
//            tvBadge.setVisibility(INVISIBLE);
//        } else {
//            tvBadge.setVisibility(VISIBLE);
//            tvBadge.setText(text);
//        }
    }

    public void setNormalColor(int mNormalColor) {
        this.mNormalColor = mNormalColor;
    }

    public void setSelectedColor(int mSelectedColor) {
        this.mSelectedColor = mSelectedColor;
    }

    public void setAutoCancelBadge(boolean mAutoCancelBadge) {
        this.mAutoCancelBadge = mAutoCancelBadge;
    }

    public boolean isAutoCancelBadge() {
        return mAutoCancelBadge;
    }
}
