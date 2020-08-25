package com.zpj.shouji.market.ui.fragment.manager;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
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

public class InstalledFragment extends RecyclerLayoutFragment<InstalledAppInfo>
        implements AppInstalledManager.CallBack,
        AppBackupManager.AppBackupListener {

    private static final List<OptionMenu> optionMenus = new ArrayList<>();

    static {
//        optionMenus.add(new OptionMenu("忽略更新"));
        optionMenus.add(new OptionMenu("详细信息"));
        optionMenus.add(new OptionMenu("分享"));
        optionMenus.add(new OptionMenu("卸载"));
        optionMenus.add(new OptionMenu("打开"));
    }

    private static final List<InstalledAppInfo> USER_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> SYSTEM_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> BACKUP_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> FORBID_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> HIDDEN_APP_LIST = new ArrayList<>();

    private SmoothCheckBox checkBox;

    private TextView infoTextView;
    private TextView titleTextView;
    private RelativeLayout bottomLayout;
    private GradientButton uninstallBtn;
    private GradientButton backupBtn;

    private int sortPosition = 0;

    private boolean isLoading = false;

    public static InstalledFragment newInstance() {

        Bundle args = new Bundle();
        InstalledFragment fragment = new InstalledFragment();
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

        bottomLayout = view.findViewById(R.id.layout_bottom);

        uninstallBtn = view.findViewById(R.id.btn_uninstall);
        uninstallBtn.setOnClickListener(v -> {
            AToast.normal(recyclerLayout.getSelectedPositionList().toString());
            for (InstalledAppInfo info : recyclerLayout.getSelectedItem()) {
                AppUtil.uninstallApp(_mActivity, info.getPackageName());
            }
        });
        backupBtn = view.findViewById(R.id.btn_backup);
        backupBtn.setOnClickListener(v -> {
            AToast.normal(recyclerLayout.getSelectedPositionList().toString());
            AppBackupManager.getInstance()
                    .addAppBackupListener(this)
                    .startBackup(recyclerLayout.getSelectedItem());
        });

        checkBox = view.findViewById(R.id.checkbox);
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
    public void onDestroy() {
        AppBackupManager.getInstance().removeAppBackupListener(this);
        AppInstalledManager.getInstance().removeListener(this);
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
                        infoTextView.setText("共计：" + data.size() + " | 已选：" + recyclerLayout.getSelectedCount());
                    }

                    @Override
                    public void onSelectChange(List<InstalledAppInfo> list, int position, boolean isChecked) {
                        infoTextView.setText("共计：" + data.size() + " | 已选：" + recyclerLayout.getSelectedCount());
                    }

                    @Override
                    public void onSelectAll() {
                        checkBox.setChecked(true, true);
                        infoTextView.setText("共计：" + data.size() + " | 已选：" + recyclerLayout.getSelectedCount());
                    }

                    @Override
                    public void onUnSelectAll() {
                        checkBox.setChecked(false, true);
                        infoTextView.setText("共计：" + data.size() + " | 已选：0");
                    }

                    @Override
                    public void onSelectOverMax(int maxSelectCount) {
                        AToast.warning("最多只能选择" + maxSelectCount + "项");
                    }
                });
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

        holder.getView(R.id.layout_right).setOnClickListener(v -> {
            onMenuClicked(v, appInfo);
        });
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
        data.clear();
        data.addAll(USER_APP_LIST);
        titleTextView.setText("用户应用");
        infoTextView.setText("共计：" + data.size() + " | 已选：0");
        recyclerLayout.notifyDataSetChanged();
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
            NotifyUtil.with(getContext())
                    .buildNotify()
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(totalCount + "个应用备份完成！")
                    .setId(hashCode())
                    .show();
        } else {
            NotifyUtil.with(getContext())
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
        AToast.error(appInfo.getName() + "备份失败！");
        NotifyUtil.with(getContext())
                .buildNotify()
                .setContentTitle(getString(R.string.app_name))
                .setContentText(appInfo.getName() + "备份失败！")
                .setId(appInfo.hashCode())
                .show();
    }


    private void loadInstallApps() {
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
                .setOnItemClickListener((view, title, position) -> {
                    sortPosition = position;
                    titleTextView.setText(title);
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
                    infoTextView.setText("共计：" + data.size() + " | 已选：0");
                    recyclerLayout.notifyDataSetChanged();
                })
                .show(titleTextView);
    }

    public void onMenuClicked(View view, InstalledAppInfo appInfo) {
        PopupMenuView popupMenuView = new PopupMenuView(getContext());
        popupMenuView.setOrientation(LinearLayout.HORIZONTAL)
                .setMenuItems(optionMenus)
                .setBackgroundAlpha(getActivity(), 0.9f, 500)
                .setBackgroundColor(Color.WHITE)
                .setOnMenuClickListener(new OptionMenuView.OnOptionMenuClickListener() {
                    @Override
                    public boolean onOptionMenuClick(int position, OptionMenu menu) {
                        popupMenuView.dismiss();
                        switch (position) {
                            case 0:
                                AToast.normal("详细信息");
                                break;
                            case 1:
                                AToast.normal(appInfo.getApkFilePath());
                                AppUtil.shareApk(context, appInfo.getApkFilePath());
                                break;
                            case 2:
                                AppUtil.uninstallApp(_mActivity, appInfo.getPackageName());
                                break;
                            case 3:
                                AppUtil.openApp(getContext(), appInfo.getPackageName());
                                break;
                            default:
                                AToast.warning("未知操作！");
                                break;
                        }
                        return true;
                    }
                }).show(view);
    }

    private void enterSelectModeAnim() {
        AToast.normal("enterSelectModeAnim");
        if (bottomLayout.getVisibility() == View.VISIBLE)
            return;
        bottomLayout.setVisibility(View.VISIBLE);

        int bottomLayoutHeight = bottomLayout.getHeight() == 0 ? ScreenUtils.dp2pxInt(context, 48) : bottomLayout.getHeight();
        ObjectAnimator translationY = ObjectAnimator.ofFloat(bottomLayout, "translationY", bottomLayoutHeight, 0);
        translationY.setInterpolator(new DecelerateInterpolator());


        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, bottomLayoutHeight);
        int height1 = recyclerLayout.getHeight();
        int height = ((ViewGroup) recyclerLayout.getParent()).getMeasuredHeight() - recyclerLayout.getTop();
        Log.d("enterSelectModeAnim", "height1=" + height1);
        Log.d("enterSelectModeAnim", "height=" + height);
        Log.d("enterSelectModeAnim", "bottomLayout.getHeight()=" + bottomLayout.getHeight());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                Log.d("enterSelectModeAnim", "value=" + value);
                ViewGroup.LayoutParams params = recyclerLayout.getLayoutParams();
                params.height = (int) (height - value);
                recyclerLayout.setLayoutParams(params);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(valueAnimator, translationY);
        animatorSet.start();
    }

    private void exitSelectModeAnim() {
        if (bottomLayout.getVisibility() != View.VISIBLE)
            return;

        float y = ((ViewGroup) bottomLayout.getParent()).getMeasuredHeight() - bottomLayout.getTop();
        ObjectAnimator translationY = ObjectAnimator.ofFloat(bottomLayout, "translationY", 0, y);
        translationY.setInterpolator(new DecelerateInterpolator());
        translationY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                AToast.normal("onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                AToast.normal("onAnimationEnd");
                bottomLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                AToast.normal("onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, y);
        int height = recyclerLayout.getHeight();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                Log.d("exitSelectModeAnim", "value=" + value);
                ViewGroup.LayoutParams params = recyclerLayout.getLayoutParams();
                params.height = (int) (height + value);
                recyclerLayout.setLayoutParams(params);
            }
        });


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(valueAnimator, translationY);
        animatorSet.start();
    }

}
