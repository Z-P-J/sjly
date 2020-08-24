package com.zpj.shouji.market.ui.fragment.manager;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.zpj.downloader.util.notification.NotifyUtil;
import com.zpj.popupmenuview.OptionMenu;
import com.zpj.popupmenuview.OptionMenuView;
import com.zpj.popupmenuview.PopupMenuView;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.GlideApp;
import com.zpj.shouji.market.manager.AppBackupManager;
import com.zpj.shouji.market.manager.AppInstalledManager;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.ui.fragment.base.RecyclerLayoutFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.widget.GradientButton;
import com.zpj.shouji.market.ui.widget.popup.RecyclerPopup;
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.checkbox.SmoothCheckBox;

import java.util.ArrayList;
import java.util.List;

public class BaseInstalledFragment extends RecyclerLayoutFragment<InstalledAppInfo>
        implements AppInstalledManager.CallBack,
        IEasy.OnSelectChangeListener<InstalledAppInfo>,
        RecyclerPopup.OnItemClickListener {

    private static final List<InstalledAppInfo> USER_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> SYSTEM_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> BACKUP_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> FORBID_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> HIDDEN_APP_LIST = new ArrayList<>();

    private TextView infoTextView;
    private TextView titleTextView;

    protected int sortPosition = 0;

    private boolean isLoading = false;

    public static BaseInstalledFragment newInstance() {

        Bundle args = new Bundle();
        BaseInstalledFragment fragment = new BaseInstalledFragment();
        fragment.setArguments(args);
        return fragment;
    }

   @Override
    protected int getLayoutId() {
        return R.layout.fragment_installed;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.layout_installed_app;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        infoTextView = view.findViewById(R.id.text_info);
        infoTextView.setText("扫描中...");
        titleTextView = view.findViewById(R.id.text_title);
        titleTextView.setOnClickListener(v -> showFilterPopWindow());
    }

    @Override
    public void onDestroy() {
        AppInstalledManager.getInstance().removeListener(this);
        super.onDestroy();
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<InstalledAppInfo> recyclerLayout) {
        recyclerLayout.setEnableSwipeRefresh(false)
                .setEnableSelection(true)
                .setOnSelectChangeListener(this);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == AppUtil.UNINSTALL_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                AToast.success("应用卸载成功！");
//                loadInstallApps();
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                AToast.normal("应用卸载取消！");
//            }
//        }
//    }

    @Override
    public void onClick(EasyViewHolder holder, View view, InstalledAppInfo data) {

    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, InstalledAppInfo data) {
        return false;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<InstalledAppInfo> list, int position, List<Object> payloads) {
        InstalledAppInfo appInfo = list.get(position);
        Log.d("onBindViewHolder", "name=" + appInfo.getName());
        Log.d("onBindViewHolder", "size=" + appInfo.getFileLength());


        GlideApp.with(context).load(appInfo).into(holder.getImageView(R.id.iv_icon));

        holder.getTextView(R.id.tv_name).setText(appInfo.getName());
        String idStr = AppUpdateManager.getInstance().getAppIdAndType(appInfo.getPackageName());
        String info;
        if (idStr == null) {
            info = "未收录";
        } else {
            info = "已收录";
        }
        holder.getTextView(R.id.tv_info).setText(appInfo.getVersionName() + " | " + appInfo.getFormattedAppSize() + " | " + info);

        holder.setVisible(R.id.layout_right, false);
    }

    @Override
    public boolean onLoadMore(EasyAdapter.Enabled enabled, int currentPage) {
        if (isLoading) {
            return false;
        }
        isLoading = true;
        loadInstallApps();
        return true;
    }

    @Override
    public void onSelectModeChange(boolean selectMode) {
        infoTextView.setText("共计：" + data.size() + " | 已选：" + recyclerLayout.getSelectedCount());
    }

    @Override
    public void onSelectChange(List<InstalledAppInfo> list, int position, boolean isChecked) {
        infoTextView.setText("共计：" + data.size() + " | 已选：" + recyclerLayout.getSelectedCount());
    }

    @Override
    public void onSelectAll() {
        infoTextView.setText("共计：" + data.size() + " | 已选：" + recyclerLayout.getSelectedCount());
    }

    @Override
    public void onUnSelectAll() {
        infoTextView.setText("共计：" + data.size() + " | 已选：0");
    }

    @Override
    public void onGetUserApp(InstalledAppInfo appInfo) {
        USER_APP_LIST.add(appInfo);
    }

    @Override
    public void onGetSystemApp(InstalledAppInfo appInfo) {
        SYSTEM_APP_LIST.add(appInfo);
    }

    @Override
    public void onGetBackupApp(InstalledAppInfo appInfo) {
        BACKUP_APP_LIST.add(appInfo);
    }

    @Override
    public void onGetForbidApp(InstalledAppInfo appInfo) {
        FORBID_APP_LIST.add(appInfo);
    }

    @Override
    public void onGetHiddenApp(InstalledAppInfo appInfo) {
        HIDDEN_APP_LIST.add(appInfo);
    }

    @Override
    public void onLoadAppFinished() {
        initData(USER_APP_LIST);
        titleTextView.setText("用户应用");
        infoTextView.setText("共计：" + data.size() + " | 已选：0");
        recyclerLayout.notifyDataSetChanged();
    }


    protected void loadInstallApps() {
        USER_APP_LIST.clear();
        SYSTEM_APP_LIST.clear();
        BACKUP_APP_LIST.clear();
        FORBID_APP_LIST.clear();
        HIDDEN_APP_LIST.clear();
        AppInstalledManager.getInstance()
                .addListener(this)
                .loadApps(context);
    }

    private void showFilterPopWindow() {
        RecyclerPopup.with(context)
                .addItems("用户应用", "系统应用", "已备份", "已禁用", "已隐藏")
                .setSelectedItem(sortPosition)
                .setOnItemClickListener(this)
                .show(titleTextView);
    }

    @Override
    public void onItemClick(View view, String title, int position) {
        if (sortPosition == position) {
            return;
        }
        sortPosition = position;
        titleTextView.setText(title);
        switch (position) {
            case 0:
                initData(USER_APP_LIST);
                break;
            case 1:
                initData(SYSTEM_APP_LIST);
                break;
            case 2:
                initData(BACKUP_APP_LIST);
                break;
            case 3:
                initData(FORBID_APP_LIST);
                break;
            case 4:
                initData(HIDDEN_APP_LIST);
                break;
            default:
                break;
        }
        infoTextView.setText("共计：" + data.size() + " | 已选：0");
        recyclerLayout.notifyDataSetChanged();
    }

    protected void initData(List<InstalledAppInfo> infoList) {
        data.clear();
        data.addAll(infoList);
    }

}
