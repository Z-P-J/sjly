package com.zpj.shouji.market.ui.widget.indicator;

import android.content.Context;

import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.utils.EventBus;

public class ColorChangePagerTitleView extends ScaleTransitionPagerTitleView {

    private boolean isSelected;

    public ColorChangePagerTitleView(Context context) {
        super(context);
        RxBus.observe(this, EventBus.KEY_COLOR_CHANGE_EVENT, Boolean.class)
                .bindView(this)
                .doOnNext(new RxBus.SingleConsumer<Boolean>() {
                    @Override
                    public void onAccept(Boolean isDark) throws Exception {
                        int color = getResources().getColor((AppConfig.isNightMode() || isDark) ? R.color.white : R.color.color_text_major);
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
