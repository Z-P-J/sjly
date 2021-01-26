package com.zpj.shouji.market.ui.fragment.manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.zagum.expandicon.ExpandIconView;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.GlideApp;
import com.zpj.shouji.market.manager.AppInstalledManager;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.ui.fragment.base.RecyclerLayoutFragment;
import com.zpj.shouji.market.ui.fragment.dialog.RecyclerPartShadowDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class BaseInstalledFragment extends RecyclerLayoutFragment<InstalledAppInfo>
        implements AppInstalledManager.CallBack,
        IEasy.OnSelectChangeListener<InstalledAppInfo>,
        RecyclerPartShadowDialogFragment.OnItemClickListener{

    private static final List<InstalledAppInfo> USER_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> SYSTEM_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> BACKUP_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> FORBID_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> HIDDEN_APP_LIST = new ArrayList<>();

    private TextView tvInfo;
    private TextView tvSort;
    private ProgressBar progressBar;

    protected int sortPosition = 0;

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
        return R.layout.item_app_installed;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        tvInfo = findViewById(R.id.tv_info);
        tvInfo.setText("扫描中...");
        tvSort = findViewById(R.id.tv_sort);
        progressBar = findViewById(R.id.progress_bar);
        ExpandIconView expandIconView = findViewById(R.id.expand_icon);
        View.OnClickListener listener = v -> showFilterPopWindow(expandIconView);
        expandIconView.setOnClickListener(listener);
        tvSort.setOnClickListener(listener);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        loadInstallApps();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppInstalledManager.getInstance().loadApps();
    }

    @Override
    public void onDestroy() {
        AppInstalledManager.getInstance().onDestroy();
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
//                ZToast.success("应用卸载成功！");
//                loadInstallApps();
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                ZToast.normal("应用卸载取消！");
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
        return false;
    }

    @Override
    public void onSelectModeChange(boolean selectMode) {
        tvInfo.setText("共计：" + data.size() + " | 已选：" + recyclerLayout.getSelectedCount());
    }

    @Override
    public void onSelectChange(List<InstalledAppInfo> list, int position, boolean isChecked) {
        tvInfo.setText("共计：" + data.size() + " | 已选：" + recyclerLayout.getSelectedCount());
    }

    @Override
    public void onSelectAll() {
        tvInfo.setText("共计：" + data.size() + " | 已选：" + recyclerLayout.getSelectedCount());
    }

    @Override
    public void onUnSelectAll() {
        tvInfo.setText("共计：" + data.size() + " | 已选：0");
    }

    @Override
    public void onSelectOverMax(int maxSelectCount) {

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
        postOnEnterAnimationEnd(() -> {
            initData(USER_APP_LIST);
            tvSort.setText("用户应用");
            tvInfo.setText("共计：" + data.size() + " | 已选：0");
            recyclerLayout.showContent();
            progressBar.setVisibility(View.GONE);
        });
    }


    protected void loadInstallApps() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerLayout.showLoading();
        USER_APP_LIST.clear();
        SYSTEM_APP_LIST.clear();
        BACKUP_APP_LIST.clear();
        FORBID_APP_LIST.clear();
        HIDDEN_APP_LIST.clear();
        AppInstalledManager.getInstance().loadApps(this);
    }

    private void showFilterPopWindow(ExpandIconView expandIconView) {
        expandIconView.switchState();
        new RecyclerPartShadowDialogFragment()
                .addItems("用户应用", "系统应用", "已备份", "已禁用", "已隐藏")
                .setSelectedItem(sortPosition)
                .setOnItemClickListener(this)
                .setAttachView(tvSort)
                .setOnDismissListener(expandIconView::switchState)
                .show(context);
    }

    @Override
    public void onItemClick(View view, String title, int position) {
        if (sortPosition == position) {
            return;
        }
        sortPosition = position;
        tvSort.setText(title);
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
        tvInfo.setText("共计：" + data.size() + " | 已选：0");
        recyclerLayout.notifyDataSetChanged();
    }

    protected void initData(List<InstalledAppInfo> infoList) {
        data.clear();
        data.addAll(infoList);
    }

}
