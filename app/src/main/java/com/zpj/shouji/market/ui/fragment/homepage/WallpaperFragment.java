package com.zpj.shouji.market.ui.fragment.homepage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.github.zagum.expandicon.ExpandIconView;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.WallpaperApi;
import com.zpj.shouji.market.model.WallpaperTag;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.wallpaper.WallpaperListFragment;
import com.zpj.shouji.market.ui.widget.flowlayout.FlowLayout;
import com.zpj.shouji.market.ui.widget.popup.WallpaperTagPopup;
import com.zpj.shouji.market.utils.Callback;
import com.zpj.utils.ScreenUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WallpaperFragment extends BaseFragment implements View.OnClickListener {

    private final AtomicBoolean isInitTags = new AtomicBoolean(false);

    private final List<WallpaperListFragment> fragments = new ArrayList<>(0);
    private final List<WallpaperTag> wallpaperTags = new ArrayList<>(0);

    private ViewPager viewPager;
    private MagicIndicator magicIndicator;
    private ExpandIconView expandIconView;
    private WallpaperTagPopup wallpaperTagPopup;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wallpaper;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        isInitTags.set(false);
        viewPager = view.findViewById(R.id.vp);
        magicIndicator = view.findViewById(R.id.magic_indicator);
        expandIconView = view.findViewById(R.id.iv_expand);
        expandIconView.setOnClickListener(this);
        initWallpaperTags();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (!isInitTags.get()) {
            initWallpaperTags();
        }
    }

    @Override
    public void onSupportVisible() {

    }

    @Override
    public void onSupportInvisible() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_expand) {
            if (wallpaperTagPopup == null) {
                expandIconView.switchState();
                wallpaperTagPopup = WallpaperTagPopup.with(context)
                        .setLabels(wallpaperTags)
                        .setSelectedPosition(viewPager.getCurrentItem())
                        .setOnItemClickListener(new FlowLayout.OnItemClickListener() {
                            @Override
                            public void onClick(int index, View v, String text) {
                                viewPager.setCurrentItem(index);
                            }
                        })
                        .setOnDismissListener(() -> {
                            wallpaperTagPopup = null;
                            expandIconView.switchState();
                        })
                        .show(v);
            } else {
                wallpaperTagPopup.dismiss();
                wallpaperTagPopup = null;
            }
        }
    }

    private void initWallpaperTags() {
//        HttpApi.get("http://tt.shouji.com.cn/app/bizhi_tags.jsp")
//                .onSuccess(data -> {
//                    Elements elements = data.select("item");
//                    wallpaperTags.clear();
//                    for (Element item : elements) {
//                        wallpaperTags.add(WallpaperTag.create(item));
//                    }
//                    initMagicIndicator();
//                })
//                .onError(throwable -> {
//                    String[] tags = getResources().getStringArray(R.array.default_wallpaper_tags);
//                    for (int i = 0; i < tags.length; i++) {
//                        wallpaperTags.add(WallpaperTag.create(Integer.toString(i + 1), tags[i]));
//                    }
//                })
//                .subscribe();
        WallpaperApi.getWallpaperTags(tags -> {
            wallpaperTags.clear();
            wallpaperTags.addAll(tags);
            initMagicIndicator();
        });
    }

    private void initMagicIndicator() {
        isInitTags.set(true);
        fragments.clear();
        for (WallpaperTag tag : wallpaperTags) {
            fragments.add(WallpaperListFragment.newInstance(tag));
        }
        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), fragments, null);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.size());
        CommonNavigator navigator = new CommonNavigator(getContext());
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return wallpaperTags.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleView.setNormalColor(getResources().getColor(R.color.color_text_normal));
                titleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
                titleView.setTextSize(14);
                titleView.setText(wallpaperTags.get(index).getName());
                titleView.setOnClickListener(view -> viewPager.setCurrentItem(index));
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(ScreenUtils.dp2px(context, 4f));
                indicator.setLineWidth(ScreenUtils.dp2px(context, 12f));
                indicator.setRoundRadius(ScreenUtils.dp2px(context, 4f));
                indicator.setColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimary));
                return indicator;
            }
        });
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }
}
