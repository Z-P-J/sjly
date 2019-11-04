package com.zpj.sjly.ui.fragment.main.homepage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.felix.atoast.library.AToast;
import com.qyh.qtablayoutlib.QTabLayout;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.zpj.sjly.R;
import com.zpj.sjly.ui.adapter.PageAdapter;
import com.zpj.sjly.ui.fragment.main.homepage.ExploreFragment;
import com.zpj.sjly.ui.fragment.main.homepage.RecommendFragment;
import com.zpj.sjly.ui.fragment.base.BaseFragment;
import com.zpj.sjly.ui.fragment.manager.AppManagerFragment;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;

public class HomeFragment extends BaseFragment {

    private static final String[] TAB_TITLES = {"推荐", "发现", "乐图"};

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected boolean supportSwipeBack() {
        return false;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        CommonTitleBar titleBar = view.findViewById(R.id.title_bar);
        View rightCustomView = titleBar.getRightCustomView();
        rightCustomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AToast.normal("rightCustomView");
            }
        });
        QTabLayout tabLayout = (QTabLayout) titleBar.getCenterCustomView();


        ArrayList<Fragment> list = new ArrayList<>();
        list.add(new RecommendFragment());
        list.add(ExploreFragment.newInstance("http://tt.shouji.com.cn/app/faxian.jsp?index=faxian&versioncode=187"));
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
                if (getActivity() instanceof SupportActivity) {
                    ((SupportActivity) getActivity()).start(new AppManagerFragment());
                }
            }
        });
    }
}
