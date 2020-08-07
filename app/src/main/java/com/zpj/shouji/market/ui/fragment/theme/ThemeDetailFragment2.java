//package com.zpj.shouji.market.ui.fragment.theme;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
//import android.view.View;
//
//import com.felix.atoast.library.AToast;
//import com.zpj.fragmentation.BaseFragment;
//import com.zpj.recyclerview.EasyViewHolder;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.constant.Keys;
//import com.zpj.shouji.market.event.StartFragmentEvent;
//import com.zpj.shouji.market.model.DiscoverInfo;
//import com.zpj.shouji.market.ui.adapter.DiscoverBinder;
//import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
//import com.zpj.shouji.market.ui.widget.ThemeDetailLayout;
//import com.zpj.shouji.market.ui.widget.popup.AppCommentPopup;
//import com.zpj.shouji.market.ui.widget.popup.CommentPopup;
//import com.zpj.shouji.market.ui.widget.popup.ThemeMorePopupMenu;
//import com.zpj.shouji.market.utils.MagicIndicatorHelper;
//import com.zpj.utils.ScreenUtils;
//
//import net.lucode.hackware.magicindicator.MagicIndicator;
//import net.lucode.hackware.magicindicator.ViewPagerHelper;
//import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
//import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
//import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
//import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
//import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
//import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ThemeDetailFragment2 extends BaseFragment {
//
//    private final String[] TAB_TITLES = {"评论", "赞"};
//
////    private List<Fragment> fragments = new ArrayList<>();
//    private SupportUserListFragment supportUserListFragment;
//    private ThemeDetailLayout themeDetailLayout;
//
//    private CommentPopup commentPopup;
//
//    private DiscoverInfo item;
//
//    public static void start(DiscoverInfo item, boolean showCommentPopup) {
//        Bundle args = new Bundle();
//        args.putBoolean(Keys.SHOW_TOOLBAR, showCommentPopup);
//        ThemeDetailFragment2 fragment = new ThemeDetailFragment2();
//        fragment.setDiscoverInfo(item);
//        fragment.setArguments(args);
//        StartFragmentEvent.start(fragment);
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_theme_detail;
//    }
//
//    @Override
//    protected boolean supportSwipeBack() {
//        return true;
//    }
//
//    @Override
//    protected void initView(View view, @Nullable Bundle savedInstanceState) {
////        EasyViewHolder holder = new EasyViewHolder(view.findViewById(R.id.layout_discover));
////        DiscoverBinder binder = new DiscoverBinder(false);
////        List<DiscoverInfo> discoverInfoList = new ArrayList<>();
////        discoverInfoList.add(item);
////        binder.onBindViewHolder(holder, discoverInfoList, 0, new ArrayList<>(0));
////        holder.setOnItemLongClickListener(v -> {
////            ThemeMorePopupMenu.with(context)
////                    .setDiscoverInfo(item)
////                    .show();
////            return true;
////        });
//
//        themeDetailLayout = view.findViewById(R.id.layout_theme_detail);
//        themeDetailLayout.bindToolbar(toolbar);
//
//
//        View fabComment = view.findViewById(R.id.fab_comment);
//        fabComment.setOnClickListener(v -> {
//            commentPopup = AppCommentPopup.with(context, item.getId(), item.getContentType(), "").show();
//        });
//
//
//
////        CommonNavigator navigator = new CommonNavigator(context);
////        navigator.setAdjustMode(true);
////        navigator.setAdapter(new CommonNavigatorAdapter() {
////            @Override
////            public int getCount() {
////                return TAB_TITLES.length;
////            }
////
////            @Override
////            public IPagerTitleView getTitleView(Context context, int index) {
////                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
////                titleView.setNormalColor(Color.GRAY);
////                titleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
////                titleView.setTextSize(14);
////                titleView.setText(TAB_TITLES[index]);
////                titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index));
////                return titleView;
////            }
////
////            @Override
////            public IPagerIndicator getIndicator(Context context) {
////                LinePagerIndicator indicator = new LinePagerIndicator(context);
////                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
////                indicator.setLineHeight(ScreenUtils.dp2px(context, 4f));
////                indicator.setLineWidth(ScreenUtils.dp2px(context, 12f));
////                indicator.setRoundRadius(ScreenUtils.dp2px(context, 4f));
////                indicator.setColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimary));
////                return indicator;
////            }
////        });
////        magicIndicator.setNavigator(navigator);
////        ViewPagerHelper.bind(magicIndicator, viewPager);
//
//    }
//
//    @Override
//    public void onEnterAnimationEnd(Bundle savedInstanceState) {
//        super.onEnterAnimationEnd(savedInstanceState);
//
//        themeDetailLayout.loadInfo(item);
//
//        ArrayList<Fragment> list = new ArrayList<>();
//
//        ThemeCommentListFragment discoverListFragment = findChildFragment(ThemeCommentListFragment.class);
//        if (discoverListFragment == null) {
//            discoverListFragment = ThemeCommentListFragment.newInstance(item.getId(), item.getContentType());
//        }
//        supportUserListFragment = findChildFragment(SupportUserListFragment.class);
//        if (supportUserListFragment == null) {
//            supportUserListFragment = new SupportUserListFragment();
//        }
//        list.add(discoverListFragment);
//        list.add(supportUserListFragment);
//
//        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);
//        ViewPager viewPager = themeDetailLayout.getViewPager();
//        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(list.size());
//        MagicIndicator magicIndicator = themeDetailLayout.getMagicIndicator();
//
//        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES, true);
//
//
//        supportUserListFragment.setData(item.getSupportUserInfoList());
//        if (getArguments() != null) {
//            if (getArguments().getBoolean(Keys.SHOW_TOOLBAR, false)) {
//                commentPopup = CommentPopup.with(context, item.getId(), item.getContentType()).show();
//            }
//        }
//    }
//
//    @Override
//    public void onSupportInvisible() {
//        super.onSupportInvisible();
//        if (commentPopup != null) {
//            commentPopup.hide();
//        }
//    }
//
//    @Override
//    public void onSupportVisible() {
//        super.onSupportVisible();
//        if (commentPopup != null) {
//            commentPopup.show();
//        }
//    }
//
//    void setDiscoverInfo(DiscoverInfo discoverInfo) {
//        this.item = discoverInfo;
//    }
//
//}
