package com.zpj.shouji.market.ui.fragment.manager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.zagum.expandicon.ExpandIconView;
import com.zpj.fragmentation.dialog.impl.ArrowMenuDialogFragment;
import com.zpj.notification.ZNotify;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.glide.GlideApp;
import com.zpj.shouji.market.manager.AppBackupManager;
import com.zpj.shouji.market.manager.AppInstalledManager;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.ui.fragment.base.RecyclerLayoutFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.dialog.PackageDetailDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.RecyclerPartShadowDialogFragment;
import com.zpj.shouji.market.ui.widget.GradientButton;
import com.zpj.shouji.market.ui.widget.LetterSortSideBar;
import com.zpj.shouji.market.ui.widget.RoundedDrawableTextView;
import com.zpj.shouji.market.utils.PackageStateComparator;
import com.zpj.shouji.market.utils.PinyinComparator;
import com.zpj.toast.ZToast;
import com.zpj.utils.AppUtils;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.checkbox.SmoothCheckBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InstalledManagerFragment extends RecyclerLayoutFragment<InstalledAppInfo>
        implements AppInstalledManager.CallBack,
        AppBackupManager.AppBackupListener {

    private final List<InstalledAppInfo> USER_APP_LIST = new ArrayList<>();
    private final List<InstalledAppInfo> SYSTEM_APP_LIST = new ArrayList<>();
    private final List<InstalledAppInfo> BACKUP_APP_LIST = new ArrayList<>();
    private final List<InstalledAppInfo> FORBID_APP_LIST = new ArrayList<>();
    private final List<InstalledAppInfo> HIDDEN_APP_LIST = new ArrayList<>();

    private SmoothCheckBox checkBox;

    private TextView tvInfo;
    private TextView tvFilter;
    private ProgressBar progressBar;
    private RelativeLayout headerLayout;
    private RelativeLayout bottomLayout;

    private LetterSortSideBar sortSideBar;

    private int filterPosition = 1;
    private int sortPosition = 0;

    public static InstalledManagerFragment newInstance(boolean showToolbar) {
        Bundle args = new Bundle();
        args.putBoolean(Keys.SHOW_TOOLBAR, showToolbar);
        InstalledManagerFragment fragment = new InstalledManagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void start(boolean showToolbar) {
        start(InstalledManagerFragment.newInstance(showToolbar));
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
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppInstalledManager.getInstance().loadApps();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        boolean showToolbar = getArguments() != null && getArguments().getBoolean(Keys.SHOW_TOOLBAR, false);
        if (showToolbar) {
            toolbar.setVisibility(View.VISIBLE);
//            findViewById(R.id.shadow_view).setVisibility(View.VISIBLE);
            setToolbarTitle("应用管理");
        } else {
            setSwipeBackEnable(false);
        }

        tvInfo = findViewById(R.id.tv_info);
        tvInfo.setText("扫描中...");
        tvFilter = findViewById(R.id.tv_filter);
        progressBar = findViewById(R.id.progress_bar);
        ExpandIconView expandIconView = findViewById(R.id.expand_icon);
        View.OnClickListener listener = v -> showFilterPopWindow(expandIconView);
        expandIconView.setOnClickListener(listener);
        tvFilter.setOnClickListener(listener);

        ImageView ivSort = findViewById(R.id.iv_sort);
        ivSort.setOnClickListener(view1 -> showSortDialog(ivSort));


        headerLayout = findViewById(R.id.layout_header);
        bottomLayout = findViewById(R.id.layout_bottom);

        GradientButton btnUninstall = findViewById(R.id.btn_uninstall);
        btnUninstall.setOnClickListener(v -> {
            ZToast.normal(recyclerLayout.getSelectedPositionList().toString());
            for (InstalledAppInfo info : recyclerLayout.getSelectedItem()) {
                AppUtils.uninstallApk(_mActivity, info.getPackageName());
            }
        });
        GradientButton btnBackup = findViewById(R.id.btn_backup);
        btnBackup.setOnClickListener(v -> {
            ZToast.normal(recyclerLayout.getSelectedPositionList().toString());
            AppBackupManager.getInstance()
                    .addAppBackupListener(this)
                    .startBackup(recyclerLayout.getSelectedItem());
        });

        checkBox = findViewById(R.id.checkbox);
        checkBox.setChecked(false);
        checkBox.setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                recyclerLayout.unSelectAll();
            } else {
                recyclerLayout.selectAll();
            }
        });

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
    public void onDestroy() {
        AppInstalledManager.getInstance().onDestroy();
        AppBackupManager.getInstance().removeAppBackupListener(this);
        super.onDestroy();
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<InstalledAppInfo> recyclerLayout) {
        recyclerLayout.setEnableSwipeRefresh(false)
                .setEnableSelection(true)
                .setOnSelectChangeListener(new IEasy.OnSelectChangeListener<InstalledAppInfo>() {
                    @Override
                    public void onSelectModeChange(boolean selectMode) {
                        if (selectMode) {
                            enterSelectModeAnim();
                        } else {
                            exitSelectModeAnim();
                        }
                    }

                    @Override
                    public void onSelectChange(List<InstalledAppInfo> list, int position, boolean isChecked) {
                        tvInfo.setText("共计：" + data.size() + " | 已选：" + recyclerLayout.getSelectedCount());
                    }

                    @Override
                    public void onSelectAll() {
                        checkBox.setChecked(true, true);
                        tvInfo.setText("共计：" + data.size() + " | 已选：" + recyclerLayout.getSelectedCount());
                    }

                    @Override
                    public void onUnSelectAll() {
                        checkBox.setChecked(false, true);
                        tvInfo.setText("共计：" + data.size() + " | 已选：0");
                    }

                    @Override
                    public void onSelectOverMax(int maxSelectCount) {
                        ZToast.warning("最多只能选择" + maxSelectCount + "项");
                    }
                });
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, InstalledAppInfo data) {
        if (TextUtils.isEmpty(data.getId()) || TextUtils.isEmpty(data.getAppType())) {
            PackageDetailDialogFragment.with(data).show(context);
            return;
        }
        AppDetailFragment.start(data);
    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, InstalledAppInfo data) {
        if (!recyclerLayout.isSelectMode()) {
            recyclerLayout.addSelectedPosition(holder.getAdapterPosition());
            recyclerLayout.enterSelectMode();
            enterSelectModeAnim();
            return true;
        }
        return false;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<InstalledAppInfo> list, int position, List<Object> payloads) {
        InstalledAppInfo appInfo = list.get(position);
        GlideApp.with(context).load(appInfo).into(holder.getImageView(R.id.iv_icon));

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
//        holder.setVisible(R.id.tv_state, idStr == null);

        holder.setOnClickListener(R.id.layout_right, view -> onMenuClicked(view, appInfo));
    }

    @Override
    public boolean onLoadMore(EasyAdapter.Enabled enabled, int currentPage) {
        return false;
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
            data.clear();
            data.addAll(USER_APP_LIST);
            tvFilter.setText("用户应用");
            tvInfo.setText("共计：" + data.size());
            progressBar.setVisibility(View.GONE);
            sort();
        });
    }

    @Override
    public boolean onBackPressedSupport() {
        if (recyclerLayout.isSelectMode()) {
            recyclerLayout.exitSelectMode();
            exitSelectModeAnim();
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onAppBackupSuccess(int totalCount, int finishedCount, InstalledAppInfo appInfo) {
        if (totalCount == finishedCount) {
            ZNotify.with(context)
                    .buildNotify()
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(totalCount + "个应用备份完成！")
                    .setId(hashCode())
                    .show();
        } else {
            ZNotify.with(context)
                    .buildProgressNotify()
                    .setProgress(totalCount, finishedCount, false)
                    .setContentTitle("备份中..." + appInfo.getName() + "备份成功！")
                    .setContentText(totalCount + "/" + finishedCount)
                    .setId(hashCode())
                    .show();
        }
    }

    @Override
    public void onAppBackupFailed(int totalCount, int finishedCount, InstalledAppInfo appInfo) {
        ZToast.error(appInfo.getName() + "备份失败！");
        ZNotify.with(context)
                .buildNotify()
                .setContentTitle(getString(R.string.app_name))
                .setContentText(appInfo.getName() + "备份失败！")
                .setId(appInfo.hashCode())
                .show();
    }


    private void loadInstallApps() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerLayout.showLoading();
        USER_APP_LIST.clear();
        SYSTEM_APP_LIST.clear();
        BACKUP_APP_LIST.clear();
        FORBID_APP_LIST.clear();
        HIDDEN_APP_LIST.clear();
        AppInstalledManager.getInstance().loadApps(this);
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

    private void showFilterPopWindow(ExpandIconView expandIconView) {
        expandIconView.switchState();
        new RecyclerPartShadowDialogFragment()
                .addItems("全部应用", "用户应用", "系统应用", "已备份", "已禁用", "已隐藏")
                .setSelectedItem(filterPosition)
                .setOnItemClickListener((view, title, position) -> {
                    filterPosition = position;
                    tvFilter.setText(title);
                    data.clear();
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
                    tvInfo.setText("共计：" + data.size());
                    sort();
                    if (recyclerLayout.isSelectMode()) {
                        recyclerLayout.exitSelectMode();
                        exitSelectModeAnim();
                    }
                })
                .setAttachView(headerLayout)
                .setOnDismissListener(expandIconView::switchState)
                .show(context);
    }

    public void onMenuClicked(View view, InstalledAppInfo appInfo) {
        new ArrowMenuDialogFragment()
                .setOptionMenus(R.array.app_actions)
                .setOrientation(LinearLayout.HORIZONTAL)
                .setOnItemClickListener((position, menu) -> {
                    switch (position) {
                        case 0:
                            PackageDetailDialogFragment.with(appInfo).show(context);
                            break;
                        case 1:
                            ZToast.normal(appInfo.getApkFilePath());
                            AppUtils.shareApk(context, appInfo.getApkFilePath());
                            break;
                        case 2:
                            AppUtils.uninstallApk(_mActivity, appInfo.getPackageName());
                            break;
                        case 3:
                            AppUtils.runApp(context, appInfo.getPackageName());
                            break;
                        default:
                            ZToast.warning("未知操作！");
                            break;
                    }
                })
                .setAttachView(view)
                .show(context);
    }

    private void enterSelectModeAnim() {
        tvInfo.setText("共计：" + data.size() + " | 已选：" + recyclerLayout.getSelectedCount());
        if (bottomLayout.getVisibility() == View.VISIBLE)
            return;
        bottomLayout.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) recyclerLayout.getLayoutParams();

        int bottomLayoutHeight = bottomLayout.getHeight() == 0 ? ScreenUtils.dp2pxInt(context, 48) : bottomLayout.getHeight();
        ObjectAnimator translationY = ObjectAnimator.ofFloat(bottomLayout, "translationY", bottomLayoutHeight, 0);
        translationY.setInterpolator(new DecelerateInterpolator());
        translationY.addUpdateListener(valueAnimator -> {
            float value = (float) valueAnimator.getAnimatedValue();
            params.bottomMargin = bottomLayoutHeight - (int) value;
            recyclerLayout.setLayoutParams(params);
        });
        translationY.setDuration(500);
        translationY.start();
    }

    private void exitSelectModeAnim() {
        tvInfo.setText("共计：" + data.size());
        if (bottomLayout.getVisibility() != View.VISIBLE)
            return;

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) recyclerLayout.getLayoutParams();

        float y = ((ViewGroup) bottomLayout.getParent()).getMeasuredHeight() - bottomLayout.getTop();
        ObjectAnimator translationY = ObjectAnimator.ofFloat(bottomLayout, "translationY", 0, y);
        translationY.setInterpolator(new DecelerateInterpolator());
        translationY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bottomLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        translationY.addUpdateListener(valueAnimator -> {
            float value = (float) valueAnimator.getAnimatedValue();
            params.bottomMargin = (int) (y - value);
            recyclerLayout.setLayoutParams(params);
        });
        translationY.setDuration(500);
        translationY.start();
    }

}
