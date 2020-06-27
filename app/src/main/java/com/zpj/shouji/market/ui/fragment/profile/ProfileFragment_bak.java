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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shehuan.niv.NiceImageView;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.behavior.AppBarLayoutOverScrollViewBehavior;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
import com.zpj.shouji.market.ui.widget.RoundProgressBar;
import com.zpj.utils.ScreenUtils;
import com.zpj.shouji.market.ui.widget.ZViewPager;

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

public class ProfileFragment_bak extends BaseFragment implements ThemeListFragment.Callback {

    private static final String USER_ID = "user_id";
    public static final String DEFAULT_URL = "http://tt.shouji.com.cn/app/view_member_xml_v4.jsp?versioncode=198&id=5636865";

    private static final String[] TAB_TITLES = {"我的动态", "我的收藏", "我的下载", "我的好友"};

    private ImageView mZoomIv;
    private Toolbar mToolBar;
    private AppBarLayout mAppBarLayout;
    private ViewGroup titleCenterLayout;
    private RoundProgressBar progressBar;
    private TextView mNicknameTextView, mSignatureTextView;
    private ImageView mSettingIv, mMsgIv;
    private NiceImageView mAvater;
    private MagicIndicator magicIndicator;
    private ZViewPager mViewPager;

    private final List<Fragment> fragments = new ArrayList<>();
    private ThemeListFragment exploreFragment;

    private String userId = "5636865";

    private int lastState = 1;

    public static ProfileFragment_bak newInstance(String userId, boolean shouldLazyLoad) {
        ProfileFragment_bak profileFragment = new ProfileFragment_bak();
//        profileFragment.setShouldLazyLoad(shouldLazyLoad);
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userId);
        profileFragment.setArguments(bundle);
        return profileFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initTab();
        initListener();
        initStatus();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
//        exploreFragment.loadData();
    }

    private void initView(View view) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString(USER_ID);
        } else {
//            exploreFragment = ExploreFragment.newInstance(DEFAULT_URL, false);
            throw new RuntimeException("bundle is null!");
        }
        mZoomIv = view.findViewById(R.id.uc_zoomiv);
        mToolBar = view.findViewById(R.id.toolbar);
        mNicknameTextView = view.findViewById(R.id.text_nickname);
        mSignatureTextView = view.findViewById(R.id.text_signature);
//        toolbar.setAlpha(0);
//        titleContainer = view.findViewById(R.id.title_layout);
        mAppBarLayout = view.findViewById(R.id.appbar_layout);
        titleCenterLayout = view.findViewById(R.id.title_center_layout);
        progressBar = view.findViewById(R.id.uc_progressbar);
        mSettingIv = view.findViewById(R.id.uc_setting_iv);
        mMsgIv = view.findViewById(R.id.uc_msg_iv);
        mAvater = view.findViewById(R.id.uc_avater);
        magicIndicator = view.findViewById(R.id.magic_indicator);
        mViewPager = view.findViewById(R.id.uc_viewpager);
    }

    private void initTab() {
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
        CommonNavigator navigator = new CommonNavigator(getContext());
        navigator.setAdjustMode(true);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return TAB_TITLES.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleView.setNormalColor(getResources().getColor(R.color.color_text_normal));
                titleView.setSelectedColor(getResources().getColor(R.color.color_text_major));
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
                int color = getResources().getColor(R.color.color_text_major);
                indicator.setColors(color, color);
                return indicator;
            }
        });
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    private static int color = Color.parseColor("#80333333");
    private void initListener() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percent = (float) Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
                if (titleCenterLayout != null && mAvater != null && mSettingIv != null && mMsgIv != null) {
                    titleCenterLayout.setAlpha(percent);
                    toolbar.setAlpha(0.8f * percent);
//                    StatusBarUtil.setTranslucentForImageView(getActivity(), (int) (255f * percent), null);
                    if (percent == 0) {
                        toolbar.setBackgroundColor(Color.TRANSPARENT);
                        toolbar.setStatusBarColor(Color.TRANSPARENT);
                        groupChange(1f, 1);
                    } else if (percent == 1) {
                        if (mAvater.getVisibility() != View.GONE) {
                            mAvater.setVisibility(View.GONE);
                        }
                        groupChange(1f, 2);
                    } else {
                        toolbar.setBackgroundColor(Color.WHITE);
                        toolbar.setStatusBarColor(Color.WHITE);
                        if (mAvater.getVisibility() != View.VISIBLE) {
                            mAvater.setVisibility(View.VISIBLE);
                        }
                        groupChange(percent, 0);
                    }

                }
            }
        });
        AppBarLayoutOverScrollViewBehavior myAppBarLayoutBehavoir = (AppBarLayoutOverScrollViewBehavior)
                ((CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams()).getBehavior();
        myAppBarLayoutBehavoir.setOnProgressChangeListener(new AppBarLayoutOverScrollViewBehavior.onProgressChangeListener() {
            @Override
            public void onProgressChange(float progress, boolean isRelease) {
                progressBar.setProgress((int) (progress * 360));
                if (progress == 1 && !progressBar.isSpinning && isRelease) {
                    // 刷新viewpager里的fragment
                }
                if (mMsgIv != null) {
                    if (progress == 0 && !progressBar.isSpinning) {
                        mMsgIv.setVisibility(View.VISIBLE);
                    } else if (progress > 0 && mSettingIv.getVisibility() == View.VISIBLE) {
                        mMsgIv.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    public void groupChange(float alpha, int state) {
        lastState = state;

        mSettingIv.setAlpha(alpha);
        mMsgIv.setAlpha(alpha);

        switch (state) {
            case 1://完全展开 显示白色
                mMsgIv.setImageResource(R.drawable.ic_expand_more_black_24dp);
                mSettingIv.setImageResource(R.drawable.ic_settings_white_24dp);
                mViewPager.setCanScroll(true);
                break;
            case 2://完全关闭 显示黑色
                mMsgIv.setImageResource(R.drawable.ic_expand_more_black_24dp);
                mSettingIv.setImageResource(R.drawable.ic_settings_applications_black_24dp);
                mViewPager.setCanScroll(true);
                break;
            case 0://介于两种临界值之间 显示黑色
                if (lastState != 0) {
                    mMsgIv.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    mSettingIv.setImageResource(R.drawable.ic_settings_applications_black_24dp);
                }
                mViewPager.setCanScroll(false);
                break;
        }
    }

    private void initStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4以下不支持状态栏变色
            //注意了，这里使用了第三方库 StatusBarUtil，目的是改变状态栏的alpha
//            StatusBarUtil.setTransparentForImageView(getActivity(), null);
            //这里是重设我们的title布局的topMargin，StatusBarUtil提供了重设的方法，但是我们这里有两个布局
            //TODO 关于为什么不把Toolbar和@layout/layout_uc_head_title放到一起，是因为需要Toolbar来占位，防止AppBarLayout折叠时将title顶出视野范围
            int statusBarHeight = ScreenUtils.getStatusBarHeight(getContext());
//            CollapsingToolbarLayout.LayoutParams lp1 = (CollapsingToolbarLayout.LayoutParams) titleContainer.getLayoutParams();
//            lp1.topMargin = statusBarHeight;
//            titleContainer.setLayoutParams(lp1);
            CollapsingToolbarLayout.LayoutParams lp2 = (CollapsingToolbarLayout.LayoutParams) mToolBar.getLayoutParams();
            lp2.topMargin = statusBarHeight;
            mToolBar.setLayoutParams(lp2);
        }
    }

    @Override
    public void onGetUserItem(Element element) {
        mZoomIv.post(() -> {
            mZoomIv.setTag(null);
            String memberBackground = element.selectFirst("memberbackground").text();
            if (!TextUtils.isEmpty(memberBackground)) {
                Glide.with(context).load(memberBackground)
                        .apply(new RequestOptions()
                                .error(R.drawable.bg_member_default)
                                .placeholder(R.drawable.bg_member_default)
                        )
                        .into(mZoomIv);
            }
            mZoomIv.setTag("overScroll");
        });
        mAvater.post(() -> {
            Glide.with(mAvater)
                    .load(element.selectFirst("memberavatar").text())
                    .apply(new RequestOptions()
                            .error(R.drawable.ic_user_head)
                            .placeholder(R.drawable.ic_user_head)
                    )
                    .into(mAvater);
        });
        mNicknameTextView.post(() -> mNicknameTextView.setText(element.selectFirst("nickname").text()));
        mSignatureTextView.post(() -> mSignatureTextView.setText(element.selectFirst("membersignature").text()));
    }

    @Override
    public void onError(Throwable throwable) {

    }
}
