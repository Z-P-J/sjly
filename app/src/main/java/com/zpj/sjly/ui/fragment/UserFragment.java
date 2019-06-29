package com.zpj.sjly.ui.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.maning.imagebrowserlibrary.utils.StatusBarUtil;
import com.qyh.qtablayoutlib.QTabLayout;
import com.zpj.sjly.R;
import com.zpj.sjly.ui.adapter.PageAdapter;
import com.zpj.sjly.ui.behavior.AppBarLayoutOverScrollViewBehavior;
import com.zpj.sjly.ui.widget.CircleImageView;
import com.zpj.sjly.ui.widget.NoScrollViewPager;
import com.zpj.sjly.ui.widget.RoundProgressBar;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {

    private static final String[] TAB_TITLES = {"动态", "收藏", "下载", "粉丝"};

    private ImageView mZoomIv;
    private Toolbar mToolBar;
    private ViewGroup titleContainer;
    private AppBarLayout mAppBarLayout;
    private ViewGroup titleCenterLayout;
    private RoundProgressBar progressBar;
    private ImageView mSettingIv, mMsgIv;
    private CircleImageView mAvater;
    private QTabLayout mTablayout;
    private NoScrollViewPager mViewPager;

    private List<Fragment> fragments = new ArrayList<>();
    private int lastState = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, null, false);
        initView(view);
        initTab();
        initListener();
        initStatus();
        return view;
    }
    
    private void initView(View view) {
        mZoomIv = view.findViewById(R.id.uc_zoomiv);
        mToolBar = view.findViewById(R.id.toolbar);
        titleContainer = view.findViewById(R.id.title_layout);
        mAppBarLayout = view.findViewById(R.id.appbar_layout);
        titleCenterLayout = view.findViewById(R.id.title_center_layout);
        progressBar = view.findViewById(R.id.uc_progressbar);
        mSettingIv = view.findViewById(R.id.uc_setting_iv);
        mMsgIv = view.findViewById(R.id.uc_msg_iv);
        mAvater = view.findViewById(R.id.uc_avater);
        mTablayout = view.findViewById(R.id.tab_title);
        mViewPager = view.findViewById(R.id.uc_viewpager);
    }

    private void initTab() {
        fragments.add(new Fragment());
        fragments.add(new Fragment());
        fragments.add(new Fragment());
        fragments.add(new Fragment());
        for (String s : TAB_TITLES) {
            mTablayout.addTab(mTablayout.newTab().setText(s));
        }
        PageAdapter adapter = new PageAdapter(getChildFragmentManager(), fragments, TAB_TITLES);
        mTablayout.setTabMode(QTabLayout.MODE_FIXED);
        mTablayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(4);
    }

    private void initListener() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percent = Float.valueOf(Math.abs(verticalOffset)) / Float.valueOf(appBarLayout.getTotalScrollRange());
                if (titleCenterLayout != null && mAvater != null && mSettingIv != null && mMsgIv != null) {
                    titleCenterLayout.setAlpha(percent);
                    StatusBarUtil.setTranslucentForImageView(getActivity(), (int) (255f * percent), null);
                    if (percent == 0) {
                        groupChange(1f, 1);
                    } else if (percent == 1) {
                        if (mAvater.getVisibility() != View.GONE) {
                            mAvater.setVisibility(View.GONE);
                        }
                        groupChange(1f, 2);
                    } else {
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
                mViewPager.setNoScroll(false);
                break;
            case 2://完全关闭 显示黑色
                mMsgIv.setImageResource(R.drawable.ic_expand_more_black_24dp);
                mSettingIv.setImageResource(R.drawable.ic_settings_applications_black_24dp);
                mViewPager.setNoScroll(false);
                break;
            case 0://介于两种临界值之间 显示黑色
                if (lastState != 0) {
                    mMsgIv.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    mSettingIv.setImageResource(R.drawable.ic_settings_applications_black_24dp);
                }
                mViewPager.setNoScroll(true);
                break;
        }
    }

    private void initStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4以下不支持状态栏变色
            //注意了，这里使用了第三方库 StatusBarUtil，目的是改变状态栏的alpha
            StatusBarUtil.setTransparentForImageView(getActivity(), null);
            //这里是重设我们的title布局的topMargin，StatusBarUtil提供了重设的方法，但是我们这里有两个布局
            //TODO 关于为什么不把Toolbar和@layout/layout_uc_head_title放到一起，是因为需要Toolbar来占位，防止AppBarLayout折叠时将title顶出视野范围
            int statusBarHeight = getStatusBarHeight(getContext());
            CollapsingToolbarLayout.LayoutParams lp1 = (CollapsingToolbarLayout.LayoutParams) titleContainer.getLayoutParams();
            lp1.topMargin = statusBarHeight;
            titleContainer.setLayoutParams(lp1);
            CollapsingToolbarLayout.LayoutParams lp2 = (CollapsingToolbarLayout.LayoutParams) mToolBar.getLayoutParams();
            lp2.topMargin = statusBarHeight;
            mToolBar.setLayoutParams(lp2);
        }
    }

    private int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
    
}
