//package com.zpj.shouji.market.ui.widget;
//
//import android.content.Context;
//
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.constant.AppConfig;
//import com.zpj.shouji.market.event.ColorChangeEvent;
//import com.zpj.shouji.market.event.SkinChangeEvent;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//
//public class SkinChangePagerTitleView extends ScaleTransitionPagerTitleView {
//
//    private boolean isSelected;
//
//    public SkinChangePagerTitleView(Context context) {
//        super(context);
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onSelected(int index, int totalCount) {
//        super.onSelected(index, totalCount);
//        isSelected = true;
//    }
//
//    @Override
//    public void onDeselected(int index, int totalCount) {
//        super.onDeselected(index, totalCount);
//        isSelected = false;
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Subscribe
//    public void onSkinChangeEvent(SkinChangeEvent event) {
//        int color = getResources().getColor(AppConfig.isNightMode() ? R.color.white : R.color.color_text_major);
//        setNormalColor(color);
//        if (!isSelected) {
//            setTextColor(mNormalColor);
//        }
//    }
//
//
//}
