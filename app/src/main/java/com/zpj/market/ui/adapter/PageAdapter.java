package com.zpj.market.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by huangyong on 2018/1/31.
 */

public class PageAdapter extends FragmentPagerAdapter {
    private List<? extends Fragment> fragments;
    private String[] tabTitle;
    public PageAdapter(FragmentManager fm, List<? extends Fragment> fragments, String[] tabTiltle) {
        super(fm);
        this.fragments = fragments;
        this.tabTitle = tabTiltle;
    }

//    public PageAdapter(FragmentManager fm, List<BaseFragment> fragments, String[] tabTiltle) {
//        super(fm);
//        this.fragments = new ArrayList<>();
//        this.fragments.addAll(fragments);
//        this.tabTitle = tabTiltle;
//    }



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
        return tabTitle[position];
    }
}
