package com.zpj.shouji.market.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;

import com.zpj.shouji.market.R;
import com.zpj.utils.ScreenUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

public class MagicIndicatorHelper {

    public static void bindViewPager(Context context, MagicIndicator magicIndicator, ViewPager viewPager, String[] tabTitles) {
        bindViewPager(context, magicIndicator, viewPager, tabTitles, false);
    }

    public static void bindViewPager(Context context, MagicIndicator magicIndicator, ViewPager viewPager, String[] tabTitles, boolean adjustMode) {
//        CommonNavigator navigator = new CommonNavigator(context);
//        navigator.setAdjustMode(adjustMode);
//        navigator.setAdapter(new CommonNavigatorAdapter() {
//            @Override
//            public int getCount() {
//                return tabTitles.length;
//            }
//
//            @Override
//            public IPagerTitleView getTitleView(Context context, int index) {
//                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
//                titleView.setNormalColor(context.getResources().getColor(R.color.color_text_normal)); // context.getResources().getColor(R.color.color_text_normal)
//                titleView.setSelectedColor(context.getResources().getColor(R.color.colorPrimary));
//                titleView.setTextSize(14);
//                titleView.setText(tabTitles[index]);
//                titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index, true));
//                return titleView;
//            }
//
//            @Override
//            public IPagerIndicator getIndicator(Context context) {
//                LinePagerIndicator indicator = new LinePagerIndicator(context);
//                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
//                indicator.setLineHeight(ScreenUtils.dp2px(context, 4f));
//                indicator.setLineWidth(ScreenUtils.dp2px(context, 12f));
//                indicator.setRoundRadius(ScreenUtils.dp2px(context, 4f));
//                indicator.setColors(context.getResources().getColor(R.color.colorPrimary), context.getResources().getColor(R.color.colorPrimary));
//                return indicator;
//            }
//        });
//        magicIndicator.setNavigator(navigator);
//        ViewPagerHelper.bind(magicIndicator, viewPager);
        builder(context)
                .setMagicIndicator(magicIndicator)
                .setViewPager(viewPager)
                .setTabTitles(tabTitles)
                .setAdjustMode(adjustMode)
                .setOnGetTitleViewListener((context12, index) -> {
                    ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context12);
                    titleView.setNormalColor(context12.getResources().getColor(R.color.color_text_normal)); // context.getResources().getColor(R.color.color_text_normal)
                    titleView.setSelectedColor(context12.getResources().getColor(R.color.colorPrimary));
                    titleView.setTextSize(14);
                    titleView.setText(tabTitles[index]);
                    titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index, true));
                    return titleView;
                })
                .setOnGetIndicatorListener(context1 -> {
                    LinePagerIndicator indicator = new LinePagerIndicator(context1);
                    indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                    indicator.setLineHeight(ScreenUtils.dp2px(context1, 4f));
                    indicator.setLineWidth(ScreenUtils.dp2px(context1, 12f));
                    indicator.setRoundRadius(ScreenUtils.dp2px(context1, 4f));
                    indicator.setColors(context1.getResources().getColor(R.color.colorPrimary), context1.getResources().getColor(R.color.colorPrimary));
                    return indicator;
                })
                .build();
    }

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    public interface OnGetTitleViewListener {
        IPagerTitleView getTitleView(Context context, int index);
    }

    public interface OnGetIndicatorListener {
        IPagerIndicator getIndicator(Context context);
    }

    public static class Builder {

        private Context context;
        private MagicIndicator magicIndicator;
        private ViewPager viewPager;
        private String[] tabTitles;
        private boolean adjustMode;
        private OnGetTitleViewListener onGetTitleViewListener;
        private OnGetIndicatorListener onGetIndicatorListener;

        private Builder(Context context) {
            this.context = context;
        }

        public Builder setMagicIndicator(MagicIndicator magicIndicator) {
            this.magicIndicator = magicIndicator;
            return this;
        }

        public Builder setViewPager(ViewPager viewPager) {
            this.viewPager = viewPager;
            return this;
        }

        public Builder setTabTitles(String[] tabTitles) {
            this.tabTitles = tabTitles;
            return this;
        }

        public Builder setAdjustMode(boolean adjustMode) {
            this.adjustMode = adjustMode;
            return this;
        }

        public Builder setOnGetTitleViewListener(OnGetTitleViewListener onGetTitleViewListener) {
            this.onGetTitleViewListener = onGetTitleViewListener;
            return this;
        }

        public Builder setOnGetIndicatorListener(OnGetIndicatorListener onGetIndicatorListener) {
            this.onGetIndicatorListener = onGetIndicatorListener;
            return this;
        }

        public void build() {
            CommonNavigator navigator = new CommonNavigator(context);
            navigator.setAdjustMode(adjustMode);
            navigator.setAdapter(new CommonNavigatorAdapter() {
                @Override
                public int getCount() {
                    return tabTitles.length;
                }

                @Override
                public IPagerTitleView getTitleView(Context context, int index) {
                    if (onGetTitleViewListener != null) {
                        return onGetTitleViewListener.getTitleView(context, index);
                    }
                    ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                    titleView.setNormalColor(context.getResources().getColor(R.color.color_text_normal)); // context.getResources().getColor(R.color.color_text_normal)
                    titleView.setSelectedColor(context.getResources().getColor(R.color.colorPrimary));
                    titleView.setTextSize(14);
                    titleView.setText(tabTitles[index]);
                    titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index, true));
                    return titleView;
                }

                @Override
                public IPagerIndicator getIndicator(Context context) {
                    if (onGetIndicatorListener != null) {
                        return onGetIndicatorListener.getIndicator(context);
                    }
                    LinePagerIndicator indicator = new LinePagerIndicator(context);
                    indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                    indicator.setLineHeight(ScreenUtils.dp2px(context, 4f));
                    indicator.setLineWidth(ScreenUtils.dp2px(context, 12f));
                    indicator.setRoundRadius(ScreenUtils.dp2px(context, 4f));
                    indicator.setColors(context.getResources().getColor(R.color.colorPrimary), context.getResources().getColor(R.color.colorPrimary));
                    return indicator;
                }
            });
            magicIndicator.setNavigator(navigator);
            ViewPagerHelper.bind(magicIndicator, viewPager);
        }

    }

}
