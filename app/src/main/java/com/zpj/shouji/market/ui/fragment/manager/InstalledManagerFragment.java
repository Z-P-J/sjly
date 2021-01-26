package com.zpj.shouji.market.ui.fragment.manager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
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
import com.zpj.shouji.market.ui.fragment.dialog.RecyclerPartShadowDialogFragment;
import com.zpj.shouji.market.ui.widget.GradientButton;
import com.zpj.toast.ZToast;
import com.zpj.utils.AppUtils;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.checkbox.SmoothCheckBox;

import java.util.ArrayList;
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
    private TextView tvSort;
    private ProgressBar progressBar;
    private RelativeLayout bottomLayout;

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
        tvSort = findViewById(R.id.tv_sort);
        progressBar = findViewById(R.id.progress_bar);
        ExpandIconView expandIconView = findViewById(R.id.expand_icon);
        View.OnClickListener listener = v -> showFilterPopWindow(expandIconView);
        expandIconView.setOnClickListener(listener);
        tvSort.setOnClickListener(listener);

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
        if (idStr == null) {
            info = "未收录";
        } else {
            info = "已收录";
        }
        holder.setText(R.id.tv_info, appInfo.getVersionName() + " | " + appInfo.getFormattedAppSize() + " | " + info);

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
            tvSort.setText("用户应用");
            tvInfo.setText("共计：" + data.size());
            recyclerLayout.showContent();
            progressBar.setVisibility(View.GONE);
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

    private void showFilterPopWindow(ExpandIconView expandIconView) {
        expandIconView.switchState();
        new RecyclerPartShadowDialogFragment()
                .addItems("用户应用", "系统应用", "已备份", "已禁用", "已隐藏")
                .setSelectedItem(sortPosition)
                .setOnItemClickListener((view, title, position) -> {
                    sortPosition = position;
                    tvSort.setText(title);
                    data.clear();
                    switch (position) {
                        case 0:
                            data.addAll(USER_APP_LIST);
                            break;
                        case 1:
                            data.addAll(SYSTEM_APP_LIST);
                            break;
                        case 2:
                            data.addAll(BACKUP_APP_LIST);
                            break;
                        case 3:
                            data.addAll(FORBID_APP_LIST);
                            break;
                        case 4:
                            data.addAll(HIDDEN_APP_LIST);
                            break;
                        default:
                            break;
                    }
                    tvInfo.setText("共计：" + data.size() + " | 已选：0");
                    recyclerLayout.notifyDataSetChanged();
                })
                .setAttachView(tvSort)
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
                            ZToast.normal("TODO 详细信息");
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


//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, bottomLayoutHeight);
//        int height1 = recyclerLayout.getHeight();
//        int height = ((ViewGroup) recyclerLayout.getParent()).getMeasuredHeight() - recyclerLayout.getTop();
//        Log.d("enterSelectModeAnim", "height1=" + height1);
//        Log.d("enterSelectModeAnim", "height=" + height);
//        Log.d("enterSelectModeAnim", "bottomLayout.getHeight()=" + bottomLayout.getHeight());
//
//        valueAnimator.addUpdateListener(animation -> {
//            float value = (float) animation.getAnimatedValue();
//            Log.d("enterSelectModeAnim", "value=" + value);
////            ViewGroup.LayoutParams params = recyclerLayout.getLayoutParams();
////            params.height = (int) (height - value);
////            recyclerLayout.setLayoutParams(params);
//
//            params.bottomMargin = (int) value;
//            recyclerLayout.setLayoutParams(params);
//        });
//
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.setDuration(500);
//        animatorSet.playTogether(valueAnimator, translationY);
//        animatorSet.start();
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

//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(y, 0);
//        int height = recyclerLayout.getHeight();
//
//        valueAnimator.addUpdateListener(animation -> {
//            float value = (float) animation.getAnimatedValue();
//            Log.d("exitSelectModeAnim", "value=" + value);
////            ViewGroup.LayoutParams params = recyclerLayout.getLayoutParams();
////            params.height = (int) (height + value);
////            recyclerLayout.setLayoutParams(params);
//
//            params.bottomMargin = (int) value;
//            recyclerLayout.setLayoutParams(params);
//        });
//
//
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.setDuration(500);
//        animatorSet.playTogether(valueAnimator, translationY);
//        animatorSet.start();
    }

}
