package com.zpj.shouji.market.ui.fragment.manager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zpj.progressbar.ZProgressBar;
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
import com.zpj.shouji.market.ui.widget.ExpandIcon;
import com.zpj.shouji.market.ui.widget.LetterSortSideBar;
import com.zpj.shouji.market.ui.widget.RoundedDrawableTextView;
import com.zpj.shouji.market.utils.PackageStateComparator;
import com.zpj.shouji.market.utils.PinyinComparator;
import com.zpj.toast.ZToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseInstalledFragment extends RecyclerLayoutFragment<InstalledAppInfo>
        implements AppInstalledManager.CallBack,
        IEasy.OnSelectChangeListener<InstalledAppInfo>,
        RecyclerPartShadowDialogFragment.OnItemClickListener{

    private final List<InstalledAppInfo> USER_APP_LIST = new ArrayList<>();
    private final List<InstalledAppInfo> SYSTEM_APP_LIST = new ArrayList<>();
    private final List<InstalledAppInfo> BACKUP_APP_LIST = new ArrayList<>();
    private final List<InstalledAppInfo> FORBID_APP_LIST = new ArrayList<>();
    private final List<InstalledAppInfo> HIDDEN_APP_LIST = new ArrayList<>();

    private TextView tvInfo;
    private TextView tvFilter;
    private ZProgressBar progressBar;

    private RelativeLayout headerLayout;

    private LetterSortSideBar sortSideBar;

    protected int filterPosition = 1;
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
        tvFilter = findViewById(R.id.tv_filter);
        progressBar = findViewById(R.id.progress_bar);
        ExpandIcon expandIconView = findViewById(R.id.expand_icon);
        View.OnClickListener listener = v -> showFilterPopWindow(expandIconView);
        expandIconView.setOnClickListener(listener);
        tvFilter.setOnClickListener(listener);

        ImageView ivSort = findViewById(R.id.iv_sort);
        ivSort.setOnClickListener(view1 -> showSortDialog(ivSort));

        headerLayout = findViewById(R.id.layout_header);

        TextView tvHint = findViewById(R.id.tv_hint);
        sortSideBar = findViewById(R.id.sortView);
        sortSideBar.setVisibility(View.GONE);
        sortSideBar.setIndexChangedListener(new LetterSortSideBar.OnIndexChangedListener() {
            @Override
            public void onSideBarScrollUpdateItem(String word) {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(word);

                int firstItemPosition = 0;
                int lastItemPosition = 0;
                RecyclerView.LayoutManager layoutManager = recyclerLayout.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取第一个可见view的位置
                    firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    lastItemPosition = linearManager.findLastVisibleItemPosition();
                }
                int delta = lastItemPosition - firstItemPosition;

                int index = -1;
                for (InstalledAppInfo info : data) {
                    if (info.getLetter().equals(word)) {
                        index = data.indexOf(info);
                        break;
                    }
                }
                if (index != -1) {
                    int pos = index + delta / 2;
                    if (pos > data.size()) {
                        pos = data.size() - 1;
                    }
                    recyclerLayout.getRecyclerView().scrollToPosition(pos);
                }
            }

            @Override
            public void onSideBarScrollEndHideText() {
                tvHint.setVisibility(View.GONE);
            }
        });

        recyclerLayout.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int mScrollState = -1;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mScrollState = newState;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mScrollState != -1) {
                    //第一个可见的位置
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    //判断是当前layoutManager是否为LinearLayoutManager
                    // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                    int firstItemPosition = 0;
                    int lastItemPosition = 0;
                    int position;
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                        //获取第一个可见view的位置
                        firstItemPosition = linearManager.findFirstVisibleItemPosition();
                        lastItemPosition = linearManager.findLastVisibleItemPosition();
                    }
                    if (lastItemPosition >= data.size() - 1) {
                        position = data.size() - 1;
                    } else {
                        position = firstItemPosition;
                    }

//                    sideBarLayout.OnItemScrollUpdateText(data.get(firstItemPosition).getLetter());
                    sortSideBar.onItemScrollUpdateText(data.get(position).getLetter());
                    if (mScrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        mScrollState = -1;
                    }
                }
            }
        });
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

//        holder.getTextView(R.id.tv_name).setText(appInfo.getName());
//        String idStr = AppUpdateManager.getInstance().getAppIdAndType(appInfo.getPackageName());
//        String info;
//        if (idStr == null) {
//            info = "未收录";
//        } else {
//            info = "已收录";
//        }
//        holder.getTextView(R.id.tv_info).setText(appInfo.getVersionName() + " | " + appInfo.getFormattedAppSize() + " | " + info);

        holder.setText(R.id.tv_name, appInfo.getName());
        String idStr = AppUpdateManager.getInstance().getAppIdAndType(appInfo.getPackageName());
        String info;
        boolean hasUpdate = AppUpdateManager.getInstance().hasUpdate(appInfo.getPackageName());
        if (idStr == null) {
            info = "未收录";
        } else {
            info = hasUpdate ? "可更新" : "已收录";
        }
//        holder.setText(R.id.tv_info, appInfo.getVersionName() + " | " + appInfo.getFormattedAppSize() + " | " + info);

        holder.setText(R.id.tv_version, appInfo.getVersionName());
        holder.setText(R.id.tv_size, appInfo.getFormattedAppSize());
        RoundedDrawableTextView tvState = holder.getView(R.id.tv_state);
        tvState.setText(info);
        tvState.setTintColor(context.getResources().getColor(idStr == null ? R.color.pink_fc4f74 : (hasUpdate ? R.color.yellow_1 : R.color.colorPrimary)));

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
            tvFilter.setText("用户应用");
            tvInfo.setText("共计：" + data.size() + " | 已选：0");
            recyclerLayout.showContent();
            progressBar.setVisibility(View.GONE);
            sort();
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

    private void showFilterPopWindow(ExpandIcon expandIconView) {
        expandIconView.switchState();
        new RecyclerPartShadowDialogFragment()
                .addItems("全部应用", "用户应用", "系统应用", "已备份", "已禁用", "已隐藏")
                .setSelectedItem(filterPosition)
                .setOnItemClickListener(this)
                .setAttachView(headerLayout)
                .setOnDismissListener(expandIconView::switchState)
                .show(context);
    }

    @Override
    public void onItemClick(View view, String title, int position) {
        if (filterPosition == position) {
            return;
        }
        filterPosition = position;
        tvFilter.setText(title);
        switch (position) {
            case 0:
                data.addAll(USER_APP_LIST);
                data.addAll(SYSTEM_APP_LIST);
                break;
            case 1:
                data.addAll(USER_APP_LIST);
                break;
            case 2:
                data.addAll(SYSTEM_APP_LIST);
                break;
            case 3:
                data.addAll(BACKUP_APP_LIST);
                break;
            case 4:
                data.addAll(FORBID_APP_LIST);
                break;
            case 5:
                data.addAll(HIDDEN_APP_LIST);
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

    private void showSortDialog(ImageView ivSort) {
        ivSort.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
        new RecyclerPartShadowDialogFragment()
                .addItems("按应用名称", "按应用大小", "按安装时间", "按更新时间", "按应用状态", "按使用频率")
                .setSelectedItem(sortPosition)
                .setOnItemClickListener((view, title, position) -> {
                    sortPosition = position;
                    sort();
                })
                .setAttachView(headerLayout)
                .setOnDismissListener(ivSort::clearColorFilter)
                .show(context);
    }

    private void sort() {
        sortSideBar.setVisibility(sortPosition == 0 ? View.VISIBLE : View.GONE);
        switch (sortPosition) {
            case 0:
                Collections.sort(data, new PinyinComparator());
                break;
            case 1:
                Collections.sort(data, (o1, o2) -> Long.compare(o1.getAppSize(), o2.getAppSize()));
                break;
            case 2:
                Collections.sort(data, (o1, o2) -> Long.compare(o2.getFirstInstallTime(), o1.getFirstInstallTime()));
                break;
            case 3:
                Collections.sort(data, (o1, o2) -> Long.compare(o2.getLastUpdateTime(), o1.getLastUpdateTime()));
                break;
            case 4:
                Collections.sort(data, new PackageStateComparator());
                break;
            case 5:
                ZToast.warning("TODO 按使用频率排序");
                break;
            default:
                break;
        }
        recyclerLayout.notifyDataSetChanged();
    }

}
