package com.zpj.shouji.market.ui.fragment.profile;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.felix.atoast.library.AToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.shehuan.niv.NiceImageView;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.popup.ZPopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.behavior.AppBarLayoutOverScrollViewBehavior;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
import com.zpj.shouji.market.ui.widget.JudgeNestedScrollView;
import com.zpj.shouji.market.ui.widget.RoundProgressBar;
import com.zpj.shouji.market.ui.widget.ZViewPager;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.statelayout.StateLayout;

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

public class ProfileFragment extends BaseFragment implements ThemeListFragment.Callback, View.OnClickListener {

    private static final String USER_ID = "user_id";
    public static final String DEFAULT_URL = "http://tt.shouji.com.cn/app/view_member_xml_v4.jsp?versioncode=198&id=5636865";

    private static final String[] TAB_TITLES = {"我的动态", "我的收藏", "我的下载", "我的好友"};

    private StateLayout stateLayout;
    private ImageView ivBack;
    private ImageView ivMenu;
    private ImageView ivHeader;
    private NiceImageView ivAvater;
    private NiceImageView ivToolbarAvater;
    private TextView tvName;
    private TextView tvToolbarName;
    private SmartRefreshLayout refreshLayout;
    private Toolbar mToolBar;
    private ViewPager mViewPager;
    private JudgeNestedScrollView scrollView;
    private ButtonBarLayout buttonBarLayout;
    private MagicIndicator magicIndicator;
    private MagicIndicator magicIndicatorTitle;
    int toolBarPositionY = 0;
    private int mOffset = 0;
    private int mScrollY = 0;

    private final List<Fragment> fragments = new ArrayList<>();
    private ThemeListFragment exploreFragment;

    private String userId = "5636865";

    private int lastState = 1;

    public static ProfileFragment newInstance(String userId, boolean shouldLazyLoad) {
        ProfileFragment profileFragment = new ProfileFragment();
//        profileFragment.setShouldLazyLoad(shouldLazyLoad);
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userId);
        profileFragment.setArguments(bundle);
        return profileFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile2;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString(USER_ID);
        } else {
//            exploreFragment = ExploreFragment.newInstance(DEFAULT_URL, false);
            throw new RuntimeException("bundle is null!");
        }
        stateLayout = view.findViewById(R.id.state_layout);
        ivBack = view.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        ivMenu = view.findViewById(R.id.iv_menu);
        ivMenu.setOnClickListener(this);
        ivHeader = view.findViewById(R.id.iv_header);
        ivAvater = view.findViewById(R.id.iv_avatar);
        ivToolbarAvater = view.findViewById(R.id.toolbar_avatar);
        tvName = view.findViewById(R.id.tv_name);
        tvToolbarName = view.findViewById(R.id.toolbar_name);
        refreshLayout = view.findViewById(R.id.layout_refresh);
        mToolBar = view.findViewById(R.id.layout_toolbar);

        ViewGroup.LayoutParams lp = mToolBar.getLayoutParams();
        if (lp != null && lp.height > 0) {
            lp.height += ScreenUtils.getStatusBarHeight(context);
        }
        mToolBar.setPadding(mToolBar.getPaddingLeft(), mToolBar.getPaddingTop() + ScreenUtils.getStatusBarHeight(context),
                mToolBar.getPaddingRight(), mToolBar.getPaddingBottom());

        mViewPager = view.findViewById(R.id.view_pager);
        scrollView = view.findViewById(R.id.scroll_view);
        buttonBarLayout = view.findViewById(R.id.layout_button_bar);
        magicIndicator = view.findViewById(R.id.magic_indicator);
        magicIndicatorTitle = view.findViewById(R.id.magic_indicator_title);

        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderPulling(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset / 2;
                ivHeader.setTranslationY(mOffset - mScrollY);
                mToolBar.setAlpha(1 - Math.min(percent, 1));
            }

            @Override
            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset / 2;
                ivHeader.setTranslationY(mOffset - mScrollY);
                mToolBar.setAlpha(1 - Math.min(percent, 1));
            }
        });

        mToolBar.post(this::dealWithViewPager);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            int lastScrollY = 0;
            int h = DensityUtil.dp2px(100);
            int color = ContextCompat.getColor(context, R.color.colorPrimary) & 0x00ffffff;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d("onScrollChange", "scrollX=" + scrollX + " scrollY=" + scrollY + " oldScrollX=" + oldScrollX + " oldScrollY=" + oldScrollY);
                int[] location = new int[2];
                magicIndicator.getLocationOnScreen(location);
                int yPosition = location[1];
                if (yPosition < toolBarPositionY) {
//                    magicIndicatorTitle.setVisibility(View.VISIBLE);
                    scrollView.setNeedScroll(false);
                } else {
//                    magicIndicatorTitle.setVisibility(View.GONE);
                    scrollView.setNeedScroll(true);

                }

                if (lastScrollY < h) {
                    scrollY = Math.min(h, scrollY);
                    mScrollY = Math.min(scrollY, h);
                    buttonBarLayout.setAlpha(1f * mScrollY / h);
                    mToolBar.setBackgroundColor(((255 * mScrollY / h) << 24) | color);
                    ivHeader.setTranslationY(mOffset - mScrollY);
                }
                if (scrollY == 0) {
                    ivBack.setImageResource(R.drawable.ic_back);
                    ivMenu.setImageResource(R.drawable.ic_more_vert_grey_24dp);
                } else {
                    ivBack.setImageResource(R.drawable.ic_back);
                    ivMenu.setImageResource(R.drawable.ic_more_vert_grey_24dp);
                }

                lastScrollY = scrollY;
            }
        });
        buttonBarLayout.setAlpha(0);
        mToolBar.setBackgroundColor(0);



        initViewPager();

        initMagicIndicator(magicIndicator);
        initMagicIndicator(magicIndicatorTitle);

        postDelayed(() -> stateLayout.showLoadingView(), 50);
    }

//    @Override
//    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
//        super.onLazyInitView(savedInstanceState);
////        exploreFragment.loadData();
//    }

    private void initViewPager() {
        exploreFragment = findChildFragment(ThemeListFragment.class);
        if (exploreFragment == null) {
            exploreFragment = ThemeListFragment.newInstance("http://tt.shouji.com.cn/app/view_member_xml_v4.jsp?versioncode=198&id=" + userId, true);
        }
        exploreFragment.setCallback(this);
        exploreFragment.setEnableSwipeRefresh(false);
        fragments.add(exploreFragment);
        fragments.add(new Fragment());
        UserDownloadedFragment userDownloadedFragment = findChildFragment(UserDownloadedFragment.class);
        if (userDownloadedFragment == null) {
            userDownloadedFragment = UserDownloadedFragment.newInstance(userId);
        }
        fragments.add(userDownloadedFragment);
        fragments.add(new Fragment());
        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(4);
    }

    private void initMagicIndicator(MagicIndicator magicIndicator) {

        CommonNavigator navigator = new CommonNavigator(getContext());
        navigator.setAdjustMode(true);
        navigator.setScrollPivotX(0.65f);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return TAB_TITLES.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleView.setNormalColor(getResources().getColor(R.color.color_text_major));
                titleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
                titleView.setTextSize(14);
                titleView.setText(TAB_TITLES[index]);
                titleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(ScreenUtils.dp2px(context, 4f));
                indicator.setLineWidth(ScreenUtils.dp2px(context, 12f));
                indicator.setRoundRadius(ScreenUtils.dp2px(context, 4f));
                int color = getResources().getColor(R.color.colorPrimary);
                indicator.setColors(color, color);
                return indicator;
            }
        });
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

//    private void initStatus() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4以下不支持状态栏变色
//            //注意了，这里使用了第三方库 StatusBarUtil，目的是改变状态栏的alpha
////            StatusBarUtil.setTransparentForImageView(getActivity(), null);
//            //这里是重设我们的title布局的topMargin，StatusBarUtil提供了重设的方法，但是我们这里有两个布局
//            //TODO 关于为什么不把Toolbar和@layout/layout_uc_head_title放到一起，是因为需要Toolbar来占位，防止AppBarLayout折叠时将title顶出视野范围
//            int statusBarHeight = ScreenUtils.getStatusBarHeight(getContext());
////            CollapsingToolbarLayout.LayoutParams lp1 = (CollapsingToolbarLayout.LayoutParams) titleContainer.getLayoutParams();
////            lp1.topMargin = statusBarHeight;
////            titleContainer.setLayoutParams(lp1);
//            CollapsingToolbarLayout.LayoutParams lp2 = (CollapsingToolbarLayout.LayoutParams) mToolBar.getLayoutParams();
//            lp2.topMargin = statusBarHeight;
//            mToolBar.setLayoutParams(lp2);
//        }
//    }

    private void dealWithViewPager() {
        toolBarPositionY = mToolBar.getHeight();
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        params.height = ScreenUtils.getScreenHeight(context) - toolBarPositionY - magicIndicator.getHeight() + 1;
        mViewPager.setLayoutParams(params);
    }

    @Override
    public void onGetUserItem(Element element) {
        ivHeader.post(() -> {
            String memberBackground = element.selectFirst("memberbackground").text();
            if (!TextUtils.isEmpty(memberBackground)) {
                Glide.with(context).load(memberBackground)
                        .apply(new RequestOptions()
                                .error(R.drawable.bg_member_default)
                                .placeholder(R.drawable.bg_member_default)
                        )
                        .into(ivHeader);
            }

        });
        ivAvater.post(() -> {
            String url = element.selectFirst("memberavatar").text();
            RequestOptions options = new RequestOptions()
                    .error(R.drawable.ic_user_head)
                    .placeholder(R.drawable.ic_user_head);
            Glide.with(context)
                    .load(url)
                    .apply(options)
                    .into(ivAvater);
            Glide.with(context)
                    .load(url)
                    .apply(options)
                    .into(ivToolbarAvater);
        });
        tvName.post(() -> {
            String nickName = element.selectFirst("nickname").text();
            tvName.setText(nickName);
            tvToolbarName.setText(nickName);
        });
        postDelayed(() -> stateLayout.post(() -> stateLayout.showContentView()), 500);
//        mSignatureTextView.post(() -> mSignatureTextView.setText(element.selectFirst("membersignature").text()));
    }

    @Override
    public void onError(Throwable throwable) {
        stateLayout.showErrorView(throwable.getMessage());
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            pop();
        } else if (v == ivMenu){
            ZPopup.attachList(context)
                    .addItem("加入黑名单")
                    .addItem("分享主页")
                    .addItem("举报Ta")
                    .setOnSelectListener((position, title) -> {
                        switch (position) {
                            case 0:
                            case 1:
                            case 2:
                                AToast.warning("TODO");
                                break;
                        }
                    })
                    .show(ivMenu);
        }
    }
}
