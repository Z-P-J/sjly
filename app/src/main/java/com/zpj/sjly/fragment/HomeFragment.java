package com.zpj.sjly.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.qyh.qtablayoutlib.QTabLayout;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.zpj.sjly.AppManagerActivity;
import com.zpj.sjly.R;
import com.zpj.sjly.adapter.PageAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {

    private static final String[] TAB_TITLES = {"推荐", "发现", "乐图"};

    @Nullable
    @Override
    public View onBuildView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, null);
        initView(view);
        return view;
    }



    @Override
    public void lazyLoadData() {

    }

    private void initView(View view) {
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
        list.add(new RecommendFragment());
        list.add(new ExploreFragment());
        list.add(new Fragment());
        for (String s : TAB_TITLES) {
            tabLayout.addTab(tabLayout.newTab().setText(s));
        }

        tabLayout.addOnTabSelectedListener(new QTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabs(List<QTabLayout.Tab> tabs) {
//                for (QTabLayout.Tab tab : tabs) {
//                    View customView = tab.getCustomView();
//                    if (customView instanceof ColorChangeView) {
//                        ((ColorChangeView)(customView)).setTextSize(Util.Dp2px(getContext(), 16));
//                    }
//                }
            }

            @Override
            public void onTabSelected(QTabLayout.Tab tab) {
                Log.d("OnTabSelectedListener", "onTabSelected");
//                View view = tab.getCustomView();
//                if (view instanceof ColorChangeView) {
//                    ((ColorChangeView)(view)).setTextSize(Util.Dp2px(getContext(), 18));
//                }
            }

            @Override
            public void onTabUnselected(QTabLayout.Tab tab) {
                Log.d("OnTabSelectedListener", "onTabUnselected");
//                View view = tab.getCustomView();
//                if (view instanceof ColorChangeView) {
//                    ((ColorChangeView)(view)).setTextSize(Util.Dp2px(getContext(), 16));
//                }
            }

            @Override
            public void onTabReselected(QTabLayout.Tab tab) {

            }
        });
        PageAdapter adapter = new PageAdapter(getChildFragmentManager(), list, TAB_TITLES);
        tabLayout.setTabMode(QTabLayout.MODE_SCROLLABLE);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapter);

        ImageView manageBtn = titleBar.getRightCustomView().findViewById(R.id.btn_manage);
        manageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AppManagerActivity.class);
                getContext().startActivity(intent);
            }
        });
    }

}
