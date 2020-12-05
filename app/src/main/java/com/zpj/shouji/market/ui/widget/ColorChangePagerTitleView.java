package com.zpj.shouji.market.ui.widget;

import android.content.Context;

import com.zpj.rxbus.RxObserver;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.event.EventBus;

import io.reactivex.functions.Consumer;

public class ColorChangePagerTitleView extends ScaleTransitionPagerTitleView {

    private boolean isSelected;

    public ColorChangePagerTitleView(Context context) {
        super(context);
//        EventBus.getDefault().register(this);
        RxObserver.with(this, EventBus.KEY_COLOR_CHANGE_EVENT, Boolean.class)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isDark) throws Exception {
                        int color = getResources().getColor((AppConfig.isNightMode() || isDark) ? R.color.white : R.color.color_text_major);
                        setNormalColor(color);
                        if (!isSelected) {
                            setTextColor(mNormalColor);
                        }
                    }
                });
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
        RxObserver.unSubscribe(this);
//        EventBus.getDefault().unregister(this);
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
