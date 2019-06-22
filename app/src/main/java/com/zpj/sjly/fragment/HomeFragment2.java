package com.zpj.sjly.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qyh.qtablayoutlib.QTabLayout;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.zpj.sjly.R;
import com.zpj.sjly.adapter.PageAdapter;

import java.util.ArrayList;

public class HomeFragment2 extends BaseFragment {

    private static final String[] TAB_TITLES = {"推荐", "发现", "乐图"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, null);
        CommonTitleBar titleBar = view.findViewById(R.id.title_bar);
        View rightCustomView = titleBar.getRightCustomView();
        rightCustomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "rightCustomView", Toast.LENGTH_SHORT).show();
            }
        });
        QTabLayout tabLayout = (QTabLayout) titleBar.getCenterCustomView();


        ArrayList<Fragment> list = new ArrayList<>();
        for (String s : TAB_TITLES) {
            if (list.isEmpty()) {
                list.add(new RecommendFragment());
            } else {
                list.add(new Fragment());
            }
            tabLayout.addTab(tabLayout.newTab().setText(s));
        }
        PageAdapter adapter = new PageAdapter(getChildFragmentManager(), list, TAB_TITLES);
        tabLayout.setTabMode(QTabLayout.MODE_FIXED);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapter);
        return view;
    }



    @Override
    public void lazyLoadData() {

    }

}
