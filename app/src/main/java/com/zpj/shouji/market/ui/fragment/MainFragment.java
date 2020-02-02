package com.zpj.shouji.market.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.fragment.game.GameFragment;
import com.zpj.shouji.market.ui.fragment.homepage.HomeFragment;
import com.zpj.shouji.market.ui.fragment.profile.MeFragment;
import com.zpj.shouji.market.ui.fragment.soft.SoftFragment;
import com.zpj.shouji.market.ui.view.AddLayout;
import com.zpj.shouji.market.ui.view.ZViewPager;
import com.zpj.shouji.market.ui.widget.navigation.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends BaseFragment
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private final List<BaseFragment> fragments = new ArrayList<>();
    private ZViewPager viewPager;
    private AddLayout addLayout;

    private int previousPosition = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected boolean supportSwipeBack() {
        return false;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        HomeFragment homeFragment = findChildFragment(HomeFragment.class);
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }

        SoftFragment softFragment = findChildFragment(SoftFragment.class);
        if (softFragment == null) {
            softFragment = new SoftFragment();
        }

        GameFragment game = findChildFragment(GameFragment.class);
        if (game == null) {
            game = new GameFragment();
        }

        MeFragment profileFragment = findChildFragment(MeFragment.class);
        if (profileFragment == null) {
            profileFragment = new MeFragment();
        }
        fragments.clear();
        fragments.add(homeFragment);
        fragments.add(softFragment);
        fragments.add(game);
        fragments.add(profileFragment);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab);

        BottomNavigationViewEx navigationView = view.findViewById(R.id.navigation_view);
        navigationView.enableItemShiftingMode(false);
        navigationView.enableShiftingMode(false);
        navigationView.enableAnimation(false);
        viewPager = view.findViewById(R.id.vp);
        viewPager.setCanScroll(false);
        viewPager.setOffscreenPageLimit(4);
        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), fragments, null);
        viewPager.setAdapter(adapter);
        navigationView.setOnNavigationItemSelectedListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                navigationView.setCurrentItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        addLayout = view.findViewById(R.id.layout_add);
        addLayout.initBlurView((ViewGroup) view);
        addLayout.bindButton(floatingActionButton);
        floatingActionButton.setOnClickListener(v -> {
            if (addLayout.isShow()) {
                addLayout.close();
                onSupportVisible();
            } else {
                addLayout.show();
                postDelay(this::darkStatusBar, 300);
            }
        });
    }

    @Override
    public boolean onBackPressedSupport() {
        if (addLayout.isShow()) {
            addLayout.close();
            onSupportVisible();
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onSupportVisible() {
        if (viewPager != null && !fragments.isEmpty()) {
            fragments.get(viewPager.getCurrentItem()).onSupportVisible();
        } else {
            darkStatusBar();
        }
    }

    @Override
    public void onSupportInvisible() {
        if (viewPager != null && !fragments.isEmpty()) {
            fragments.get(viewPager.getCurrentItem()).onSupportInvisible();
        } else {
            darkStatusBar();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int position = 0;
        switch (menuItem.getItemId()) {
            case R.id.i_homepage:
                position = 0;
                break;
            case R.id.i_app:
                position = 1;
                break;
            case R.id.i_game:
                position = 2;
                break;
            case R.id.i_me:
                position = 3;
                break;
            case R.id.i_empty: {
                return false;
            }
        }
        if(previousPosition != position) {
            viewPager.setCurrentItem(position, false);
            previousPosition = position;
        }

        return true;
    }
}
