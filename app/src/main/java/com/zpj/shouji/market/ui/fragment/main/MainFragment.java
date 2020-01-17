package com.zpj.shouji.market.ui.fragment.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.adapter.ZFragmentPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.fragment.main.homepage.HomeFragment;
import com.zpj.shouji.market.ui.fragment.main.user.UserFragment;
import com.zpj.shouji.market.ui.view.AddLayout;
import com.zpj.shouji.market.ui.view.KickBackAnimator;
import com.zpj.shouji.market.ui.view.ZViewPager;
import com.zpj.shouji.market.utils.BlurBuilder;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends BaseFragment {

    private AddLayout addLayout;

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

        QianQianFragment qianQianFragment = findChildFragment(QianQianFragment.class);
        if (qianQianFragment == null) {
            qianQianFragment = new QianQianFragment();
        }

        XinHaiFragment xinHaiFragment = findChildFragment(XinHaiFragment.class);
        if (xinHaiFragment == null) {
            xinHaiFragment = new XinHaiFragment();
        }

        UserFragment userFragment = findChildFragment(UserFragment.class);
        if (userFragment == null) {
            userFragment = UserFragment.newInstance("5636865", true);
        }

        final List<Fragment> fragments = new ArrayList<>();
        fragments.add(homeFragment);
        fragments.add(qianQianFragment);
        fragments.add(xinHaiFragment);
        fragments.add(userFragment);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab);

        BottomNavigationViewEx navigationView = view.findViewById(R.id.navigation_view);
        navigationView.enableItemShiftingMode(false);
        navigationView.enableShiftingMode(false);
        navigationView.enableAnimation(false);
        ZViewPager viewPager = view.findViewById(R.id.vp);
        viewPager.setCanScroll(false);
        viewPager.setOffscreenPageLimit(10);
        ZFragmentPagerAdapter adapter = new ZFragmentPagerAdapter(getChildFragmentManager(), fragments, null);
        viewPager.setAdapter(adapter);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            private int previousPosition = -1;

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
        });

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
        addLayout.bindButton(floatingActionButton);
    }

    @Override
    public boolean onBackPressedSupport() {
        if (addLayout.isShow()) {
            addLayout.close();
            return true;
        }
        return super.onBackPressedSupport();
    }

}
