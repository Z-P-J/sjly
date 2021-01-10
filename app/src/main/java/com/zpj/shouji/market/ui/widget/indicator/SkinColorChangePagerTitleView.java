package com.zpj.shouji.market.ui.widget.indicator;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.utils.EventBus;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

public class SkinColorChangePagerTitleView extends ColorTransitionPagerTitleView {

    private boolean isSelected;

    public SkinColorChangePagerTitleView(Context context) {
        super(context);
        int normalTextColor = ContextCompat.getColor(context, R.color.color_text_normal);
        setNormalColor(normalTextColor);
        setSelectedColor(ContextCompat.getColor(context, R.color.colorPrimary));
        RxBus.observe(this, EventBus.KEY_COLOR_CHANGE_EVENT, Boolean.class)
                .bindView(this)
                .doOnNext(new RxBus.SingleConsumer<Boolean>() {
                    @Override
                    public void onAccept(Boolean isDark) throws Exception {
                        int color = (AppConfig.isNightMode() || isDark) ? Color.WHITE : normalTextColor;
                        setNormalColor(color);
                        if (!isSelected) {
                            setTextColor(mNormalColor);
                        }
                    }
                })
                .subscribe();
    }

    @Override
    public void onSelected(int index, int totalCount) {
        super.onSelected(index, totalCount);
        isSelected = true;
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        super.onDeselected(index, totalCount);
        isSelected = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

//    @Subscribe
//    public void onColorChangeEvent(ColorChangeEvent event) {
//        int color = getResources().getColor((AppConfig.isNightMode() || event.isDark()) ? R.color.white : R.color.color_text_major);
//        setNormalColor(color);
//        if (!isSelected) {
//            setTextColor(mNormalColor);
//        }
//    }


}
