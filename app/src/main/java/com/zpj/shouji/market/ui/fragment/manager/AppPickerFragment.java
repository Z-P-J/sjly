package com.zpj.shouji.market.ui.fragment.manager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.constant.UpdateFlagAction;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.glide.GlideApp;
import com.zpj.shouji.market.manager.AppInstalledManager;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.RecyclerLayoutFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.profile.MyDiscoverFragment;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
import com.zpj.shouji.market.ui.widget.popup.RecyclerPopup;
import com.zpj.shouji.market.utils.Callback;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppPickerFragment extends BaseFragment {

    //    private static final String[] TAB_TITLES = {"本地应用", "我的收藏", "搜索应用"};
    private static final String[] TAB_TITLES = {"本地应用"};

    protected ViewPager viewPager;
    private MagicIndicator magicIndicator;

    private LocalAppFragment localAppFragment;

    private boolean singleSelectMode;

    private final List<InstalledAppInfo> selectedList = new ArrayList<>();
    private Callback<List<InstalledAppInfo>> callback;

    public static void start(List<InstalledAppInfo> selectedList, Callback<List<InstalledAppInfo>> callback) {
        AppPickerFragment fragment = new AppPickerFragment();
        fragment.callback = callback;
        fragment.selectedList.clear();
        fragment.selectedList.addAll(selectedList);
        StartFragmentEvent.start(fragment);
    }

    public static void start(InstalledAppInfo info, Callback<List<InstalledAppInfo>> callback) {
        AppPickerFragment fragment = new AppPickerFragment();
        fragment.callback = callback;
        fragment.selectedList.clear();
        if (info != null) {
            fragment.selectedList.add(info);
        }
        fragment.singleSelectMode = true;
        StartFragmentEvent.start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_picker;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (callback == null) {
            pop();
            return;
        }
        setToolbarTitle("选择应用");
        viewPager = view.findViewById(R.id.view_pager);
        magicIndicator = view.findViewById(R.id.magic_indicator);
    }

    @Override
    public void toolbarRightTextView(@NonNull TextView view) {
        super.toolbarRightTextView(view);
        view.setOnClickListener(v -> pop());
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        List<Fragment> fragments = new ArrayList<>();
        localAppFragment = findChildFragment(LocalAppFragment.class);
        if (localAppFragment == null) {
            localAppFragment = new LocalAppFragment();
        }
        localAppFragment.maxCount = singleSelectMode ? 1 : Integer.MAX_VALUE;
        localAppFragment.setSelectApp(selectedList);

        fragments.add(localAppFragment);
        viewPager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES));
        viewPager.setOffscreenPageLimit(fragments.size());

        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES);
    }

    @Override
    public void onDestroy() {
        if (callback != null) {
            callback.onCallback(localAppFragment.getSelectApp());
        }
        super.onDestroy();
    }

    public static class LocalAppFragment extends BaseInstalledFragment {

        private final List<InstalledAppInfo> selectedList = new ArrayList<>();
        private int maxCount = Integer.MAX_VALUE;

        @Override
        public void onLoadAppFinished() {
            super.onLoadAppFinished();
            recyclerLayout.enterSelectMode();
        }

        @Override
        protected void buildRecyclerLayout(EasyRecyclerLayout<InstalledAppInfo> recyclerLayout) {
            super.buildRecyclerLayout(recyclerLayout);
//            recyclerLayout.setShowCheckBox(true);
            recyclerLayout.setMaxSelectCount(maxCount);
        }

        @Override
        public void onBindViewHolder(EasyViewHolder holder, List<InstalledAppInfo> list, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, list, position, payloads);
        }

        @Override
        public void onSelectChange(List<InstalledAppInfo> list, int position, boolean isChecked) {
            super.onSelectChange(list, position, isChecked);
//            AToast.normal("onSelectChange position=" + position);
        }

        @Override
        protected void initData(List<InstalledAppInfo> infoList) {
            addAllSelected();
            super.initData(infoList);
            recyclerLayout.clearSelectedPosition();
            for (int i = 0; i < data.size(); i++) {
                if (selectedList.contains(data.get(i))) {
                    recyclerLayout.addSelectedPosition(i);
                }
            }
        }

        public List<InstalledAppInfo> getSelectApp() {
            addAllSelected();
            return selectedList;
//            return recyclerLayout.getSelectedItem();
        }

        private void addAllSelected() {
            for (InstalledAppInfo info : recyclerLayout.getSelectedItem()) {
                if (!selectedList.contains(info)) {
                    selectedList.add(info);
                }
            }
        }

        public void setSelectApp(List<InstalledAppInfo> list) {
            this.selectedList.addAll(list);
        }

    }

}
