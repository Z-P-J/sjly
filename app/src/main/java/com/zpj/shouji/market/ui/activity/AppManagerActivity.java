//package com.zpj.sjly.ui.activity;
//
//import android.support.v4.view.ViewPager;
//import android.os.Bundle;
//import android.view.View;
//
//import com.qyh.qtablayoutlib.QTabLayout;
//import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
//import com.zpj.sjly.R;
//import com.zpj.sjly.ui.adapter.PageAdapter;
//import com.zpj.sjly.ui.fragment.manager.DownloadFragment;
//import com.zpj.sjly.ui.fragment.manager.PackageFragment;
//import com.zpj.sjly.ui.fragment.manager.UpdateFragment;
//import com.zpj.sjly.ui.fragment.manager.InstalledFragment;
//import com.zpj.sjly.ui.fragment.base.BaseFragment;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import me.yokeyword.fragmentation.SupportActivity;
//
//public class AppManagerActivity extends SupportActivity implements QTabLayout.OnTabSelectedListener {
//
//    private static final String[] TAB_TITLES = {"下载管理", "更新", "已安装", "安装包"};
//
//    private List<BaseFragment> fragments = new ArrayList<>();
//
//    private UpdateFragment updateFragment = new UpdateFragment();
//    private InstalledFragment installedFragment = new InstalledFragment();
//    private PackageFragment packageFragment = new PackageFragment();
//
//    private QTabLayout tabLayout;
//    private CommonTitleBar titleBar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_app_manager);
//        initView();
//    }
//
//    private void initView() {
//        titleBar = findViewById(R.id.title_bar);
//        View leftImageButton = titleBar.getLeftImageButton();
//        leftImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        tabLayout = findViewById(R.id.tab_title);
//
//        tabLayout.addOnTabSelectedListener(this);
//
////        ArrayList<Fragment> list = new ArrayList<>();
//        fragments.add(new DownloadFragment());
//        fragments.add(updateFragment);
//        fragments.add(installedFragment);
//        fragments.add(packageFragment);
//        for (String s : TAB_TITLES) {
//            tabLayout.addTab(tabLayout.newTab().setText(s));
//        }
//        PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), fragments, TAB_TITLES);
//        tabLayout.setTabMode(QTabLayout.MODE_FIXED);
//        ViewPager viewPager = findViewById(R.id.view_pager);
//        tabLayout.setupWithViewPager(viewPager);
//        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(4);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    public void onTabs(List<QTabLayout.Tab> tabs) {
//
//    }
//
//    @Override
//    public void onTabSelected(QTabLayout.Tab tab) {
//        switch (tab.getPosition()) {
//            case 0:
//                titleBar.getRightImageButton().setImageResource(R.drawable.ic_settings_white_24dp);
//                break;
//            case 1:
//                titleBar.getRightImageButton().setImageResource(R.drawable.ic_search_white_24dp);
//                break;
//            case 2:
//                titleBar.getRightImageButton().setImageResource(R.drawable.ic_search_white_24dp);
//                break;
//            case 3:
//                titleBar.getRightImageButton().setImageResource(R.drawable.ic_search_white_24dp);
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void onTabUnselected(QTabLayout.Tab tab) {
//
//    }
//
//    @Override
//    public void onTabReselected(QTabLayout.Tab tab) {
//
//    }
//}
