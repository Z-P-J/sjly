package com.zpj.shouji.market.utils;

import android.content.Context;
import android.graphics.Color;
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
        CommonNavigator navigator = new CommonNavigator(context);
        navigator.setAdjustMode(adjustMode);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return tabTitles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleView.setNormalColor(Color.GRAY); // context.getResources().getColor(R.color.color_text_normal)
                titleView.setSelectedColor(context.getResources().getColor(R.color.colorPrimary));
                titleView.setTextSize(14);
                titleView.setText(tabTitles[index]);
                titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index, true));
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
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
