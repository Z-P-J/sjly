package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.text.TextPaint;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

public class ScaleTransitionPagerTitleView extends ColorTransitionPagerTitleView {

    private static final float MIN_SCALE = 0.7f;
    private static final float MAX_SCALE = 1.3f;

    public ScaleTransitionPagerTitleView(Context context) {
        super(context);
        TextPaint paint = getPaint();
        paint.setFakeBoldText(true);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);
//        setScaleX(MIN_SCALE + (1.0f - MIN_SCALE) * enterPercent);
//        setScaleY(MIN_SCALE + (1.0f - MIN_SCALE) * enterPercent);
        setScaleX(1.0f + (MAX_SCALE - 1.0f) * enterPercent);
        setScaleY(1.0f + (MAX_SCALE - 1.0f) * enterPercent);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);
//        setScaleX(1.0f + (MIN_SCALE - 1.0f) * leavePercent);
//        setScaleY(1.0f + (MIN_SCALE - 1.0f) * leavePercent);
        setScaleX(MAX_SCALE - (MAX_SCALE - 1.0f) * leavePercent);
        setScaleY(MAX_SCALE - (MAX_SCALE - 1.0f) * leavePercent);
    }
}
