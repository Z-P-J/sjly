package com.zpj.shouji.market.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class ZFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<? extends Fragment> fragments;
    private String[] tabTitle;

    public ZFragmentPagerAdapter(FragmentManager fm, List<? extends Fragment> fragments, String[] tabTiltle) {
        super(fm);
        this.fragments = fragments;
        this.tabTitle = tabTiltle;
    }

    @Override
    public Fragment getItem(int position) {
        return  fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle == null ? "" : tabTitle[position];
    }

}
