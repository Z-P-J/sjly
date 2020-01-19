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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.zpj.recyclerview.loadmore.LoadMoreAdapter;
import com.zpj.recyclerview.loadmore.LoadMoreWrapper;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.ui.adapter.AppManagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.view.GradientButton;
import com.zpj.shouji.market.ui.view.RecyclerPopup;
import com.zpj.shouji.market.utils.AppBackupHelper;
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.shouji.market.utils.LoadAppTask;
import com.zpj.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import cn.refactor.library.SmoothCheckBox;
import me.yokeyword.fragmentation.SupportActivity;

public class InstalledFragment extends BaseFragment implements AppManagerAdapter.OnItemClickListener,
        LoadAppTask.CallBack,
        AppBackupHelper.AppBackupListener {

    private static final List<OptionMenu> optionMenus = new ArrayList<>();
    static {
//        optionMenus.add(new OptionMenu("忽略更新"));
        optionMenus.add(new OptionMenu("详细信息"));
        optionMenus.add(new OptionMenu("分享"));
        optionMenus.add(new OptionMenu("卸载"));
        optionMenus.add(new OptionMenu("打开"));
    }

    private LoadAppTask loadAppTask;

    private final List<InstalledAppInfo> installedAppInfos = new ArrayList<>();
    private static final List<InstalledAppInfo> USER_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> SYSTEM_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> BACKUP_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> FORBID_APP_LIST = new ArrayList<>();
    private static final List<InstalledAppInfo> HIDDEN_APP_LIST = new ArrayList<>();
    private AppManagerAdapter adapter;
    private RecyclerView recyclerView;
    private SmoothCheckBox checkBox;

    private TextView infoTextView;
    private TextView titleTextView;
    private RelativeLayout bottomLayout;
    private GradientButton uninstallBtn;
    private GradientButton backupBtn;

    private int sortPosition = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_installed;
    }

    @Override
    protected boolean supportSwipeBack() {
        return false;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        infoTextView = view.findViewById(R.id.text_info);
        infoTextView.setText("扫描中...");
        titleTextView = view.findViewById(R.id.text_title);
        titleTextView.setOnClickListener(v -> showFilterPopWindow());

        bottomLayout = view.findViewById(R.id.layout_bottom);

        uninstallBtn = view.findViewById(R.id.btn_uninstall);
        uninstallBtn.setOnClickListener(v -> {
            AToast.normal(adapter.getSelectedSet().toString());
            for (int position : adapter.getSelectedSet()) {
                AppUtil.uninstallApp(_mActivity, installedAppInfos.get(position).getPackageName());
            }
        });
        backupBtn = view.findViewById(R.id.btn_backup);
        backupBtn.setOnClickListener(v -> {
            AToast.normal(adapter.getSelectedSet().toString());
            AppBackupHelper.getInstance()
                    .addAppBackupListener(this)
                    .startBackup(installedAppInfos, adapter.getSelectedSet());
        });

        checkBox = view.findViewById(R.id.checkbox);
        checkBox.setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                adapter.unSelectAll();
            } else {
                adapter.selectAll();
            }
        });

        adapter = new AppManagerAdapter(installedAppInfos);
        adapter.setItemClickListener(this);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        LoadMoreWrapper.with(adapter)
                .setLoadMoreEnabled(false)
                .setListener(new LoadMoreAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(LoadMoreAdapter.Enabled enabled) {
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 获取数据
                                loadInstallApps();
                            }
                        }, 1);
                    }
                })
                .into(recyclerView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppUtil.UNINSTALL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                AToast.success("应用卸载成功！");
                loadInstallApps();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                AToast.normal("应用卸载取消！");
            }
        }
    }


    private void loadInstallApps() {
        USER_APP_LIST.clear();
        SYSTEM_APP_LIST.clear();
        BACKUP_APP_LIST.clear();
        FORBID_APP_LIST.clear();
        HIDDEN_APP_LIST.clear();
        loadAppTask = LoadAppTask.with(this)
                .setCallBack(this);
        loadAppTask.execute();
    }

    private void showFilterPopWindow() {
        RecyclerPopup.with(context)
                .addItems("用户应用", "系统应用", "已备份", "已禁用", "已隐藏")
                .setSelectedItem(sortPosition)
                .setOnItemClickListener((view, title, position) -> {
                    sortPosition = position;
                    titleTextView.setText(title);
                    installedAppInfos.clear();
                    switch (position) {
                        case 0:
                            installedAppInfos.addAll(USER_APP_LIST);
                            break;
                        case 1:
                            installedAppInfos.addAll(SYSTEM_APP_LIST);
                            break;
                        case 2:
                            installedAppInfos.addAll(BACKUP_APP_LIST);
                            break;
                        case 3:
                            installedAppInfos.addAll(FORBID_APP_LIST);
                            break;
                        case 4:
                            installedAppInfos.addAll(HIDDEN_APP_LIST);
                            break;
                        default:
                            break;
                    }
                    infoTextView.setText("共计：" + installedAppInfos.size() + " | 已选：0");
                    adapter.notifyDataSetChanged();
                })
                .show(titleTextView);
    }

    @Override
    public void onItemClick(AppManagerAdapter.ViewHolder holder, int position, InstalledAppInfo updateInfo) {
        if (TextUtils.isEmpty(updateInfo.getId()) || TextUtils.isEmpty(updateInfo.getAppType())) {
            return;
        }
//        findFragment(MainFragment.class).start(AppDetailFragment.newInstance(updateInfo));
        if (getActivity() instanceof SupportActivity) {
            ((SupportActivity) getActivity()).start(AppDetailFragment.newInstance(updateInfo));
        }
    }

    @Override
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
                                AppUtil.shareApk(getContext(), appInfo.getApkFilePath());
                                break;
                            case 2:
                                AppUtil.uninstallApp(getActivity(), appInfo.getPackageName());
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

    @Override
    public void onCheckBoxClicked(int allCount, int selectCount) {
        boolean isSelectAll = selectCount == allCount;
//        installedInfo.setText("总计：" + allCount);
        infoTextView.setText("共计：" + installedAppInfos.size() + " | 已选：" + selectCount);
        if (checkBox.isChecked() == isSelectAll) {
            return;
        }
        checkBox.setChecked(isSelectAll, true);
    }

    @Override
    public void onEnterSelectMode() {
        enterSelectModeAnim();
    }

    @Override
    public void onExitSelectMode() {
        exitSelectModeAnim();
    }

    @Override
    public void onDestroy() {
        AppBackupHelper.getInstance().removeAppBackupListener(this);
        if (loadAppTask != null) {
            loadAppTask.onDestroy();
        }
        super.onDestroy();
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
        installedAppInfos.clear();
        installedAppInfos.addAll(USER_APP_LIST);
        titleTextView.setText("用户应用");
        infoTextView.setText("共计：" + installedAppInfos.size() + " | 已选：0");
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onBackPressedSupport() {
        if (adapter.isSelectMode()) {
            adapter.exitSelectMode();
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onAppBackupSuccess(int totalCount, int finishedCount, InstalledAppInfo appInfo) {
        if (totalCount == finishedCount) {
            NotifyUtil.with(getContext())
                    .buildNotify()
                    .setContentTitle("手机乐园S")
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
                .setContentTitle("手机乐园S")
                .setContentText(appInfo.getName() + "备份失败！")
                .setId(appInfo.hashCode())
                .show();
    }

    private void enterSelectModeAnim() {
        AToast.normal("enterSelectModeAnim");
        if (bottomLayout.getVisibility() == View.VISIBLE)
            return;
        bottomLayout.setVisibility(View.VISIBLE);

        int bottomLayoutHeight = bottomLayout.getHeight() == 0 ? ScreenUtil.dp2pxInt(context, 48) : bottomLayout.getHeight();
        ObjectAnimator translationY = ObjectAnimator.ofFloat(bottomLayout, "translationY", bottomLayoutHeight, 0);
        translationY.setInterpolator(new DecelerateInterpolator());


        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, bottomLayoutHeight);
        int height1 = recyclerView.getHeight();
        int height = ((ViewGroup) recyclerView.getParent()).getMeasuredHeight() - recyclerView.getTop();
        Log.d("enterSelectModeAnim", "height1=" + height1);
        Log.d("enterSelectModeAnim", "height=" + height);
        Log.d("enterSelectModeAnim", "bottomLayout.getHeight()=" + bottomLayout.getHeight());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue();
                Log.d("enterSelectModeAnim", "value=" + value);
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = (int) (height - value);
                recyclerView.setLayoutParams(params);
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
        int height = recyclerView.getHeight();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue();
                Log.d("exitSelectModeAnim", "value=" + value);
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = (int) (height + value);
                recyclerView.setLayoutParams(params);
            }
        });


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(valueAnimator, translationY);
        animatorSet.start();

//        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
//        mHiddenAction.setDuration(500);
//        bottomLayout.clearAnimation();
//        bottomLayout.setAnimation(mHiddenAction);
//        mHiddenAction.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                bottomLayout.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
    }
}
