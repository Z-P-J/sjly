package com.zpj.shouji.market.ui.fragment.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.dialog.impl.ArrowMenuDialogFragment;
import com.zpj.fragmentation.dialog.model.OptionMenu;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.glide.GlideApp;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.model.XpkInfo;
import com.zpj.shouji.market.ui.fragment.base.RecyclerLayoutFragment;
import com.zpj.shouji.market.ui.fragment.dialog.RecyclerPartShadowDialogFragment;
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.shouji.market.utils.FileUtils;
import com.zpj.shouji.market.utils.ThemeUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipFile;

import io.haydar.filescanner.FileInfo;
import io.haydar.filescanner.FileScanner;

public class PackageManagerFragment extends RecyclerLayoutFragment<InstalledAppInfo> {

    private static final String TAG = "PackageFragment";

    private static final List<OptionMenu> optionMenus = new ArrayList<>();
    static {
        optionMenus.add(new OptionMenu("详细信息"));
        optionMenus.add(new OptionMenu("分享"));
        optionMenus.add(new OptionMenu("删除"));
        optionMenus.add(new OptionMenu("安装"));
    }

    private TextView sortTextView;
    private TextView infoTextView;

//    private long startTime;

    private int sortPosition = 0;

    private int lastProgress = 0;

    private boolean showToolbar = false;

    public static PackageManagerFragment newInstance(boolean showToolbar) {
        Bundle args = new Bundle();
        args.putBoolean(Keys.SHOW_TOOLBAR, showToolbar);
        PackageManagerFragment fragment = new PackageManagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void start(boolean showToolbar) {
        StartFragmentEvent.start(PackageManagerFragment.newInstance(showToolbar));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_package_manager;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.layout_installed_app;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initStatusBar() {
        if (showToolbar) {
            ThemeUtils.initStatusBar(this);
        }
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        showToolbar = getArguments() != null && getArguments().getBoolean(Keys.SHOW_TOOLBAR, false);
        if (showToolbar) {
            toolbar.setVisibility(View.VISIBLE);
//            findViewById(R.id.shadow_view).setVisibility(View.VISIBLE);
            setToolbarTitle("安装包管理");
        } else {
            setSwipeBackEnable(false);
        }
        sortTextView = view.findViewById(R.id.text_sort);
        sortTextView.setOnClickListener(v -> showSortPopWindow());
        infoTextView = view.findViewById(R.id.text_info);
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<InstalledAppInfo> recyclerLayout) {
        super.buildRecyclerLayout(recyclerLayout);
        recyclerLayout.setEnableSwipeRefresh(false).setEnableSelection(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onBackPressedSupport() {
        if (recyclerLayout.isSelectMode()) {
            recyclerLayout.exitSelectMode();
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppUtil.INSTALL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                AToast.success("安装成功！");
            } else {
                AToast.error("安装失败！");
            }
        }
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, InstalledAppInfo data) {
//        AToast.normal("todo 详细信息");
        AppUtil.installApk(context, data.getApkFilePath());
    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, InstalledAppInfo data) {
        return false;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<InstalledAppInfo> list, int position, List<Object> payloads) {
        InstalledAppInfo appInfo = list.get(position);

        holder.setText(R.id.tv_name, appInfo.getName());
        holder.getView(R.id.layout_right).setOnClickListener(v -> {
            onMenuClicked(v, appInfo);
        });
        if (appInfo.isDamaged()) {
            holder.setText(R.id.tv_info, appInfo.getFormattedAppSize() + " | 已损坏");
            holder.getImageView(R.id.iv_icon).setImageResource(R.drawable.wechat_icon_apk);
        } else {
            Log.d("onBindViewHolder", "name=" + appInfo.getName());
            Log.d("onBindViewHolder", "size=" + appInfo.getFileLength());

            GlideApp.with(context).load(appInfo).into(holder.getImageView(R.id.iv_icon));

            String idStr = AppUpdateManager.getInstance().getAppIdAndType(appInfo.getPackageName());
            String info;
            if (idStr == null) {
                info = "未收录";
            } else {
                info = "已收录";
            }
            holder.setText(R.id.tv_info, appInfo.getVersionName() + " | " + appInfo.getFormattedAppSize() + " | " + info);
        }
    }

    @Override
    public boolean onLoadMore(EasyAdapter.Enabled enabled, int currentPage) {
        if (data.isEmpty()) {
            postOnEnterAnimationEnd(new Runnable() {
                @Override
                public void run() {
                    loadApk();
                }
            });
            return true;
        }
        return false;
    }



    private void loadApk() {
//        startTime = System.currentTimeMillis();

        FileScanner.getInstance(context).clear();
        FileScanner.getInstance(context)
                .setType(".apk")
                .start(new FileScanner.ScannerListener() {
                    @Override
                    public void onScanBegin() {
                        Log.d(TAG, "onScanBegin");
                        post(() -> infoTextView.setText("扫描准备中..."));
                    }

                    @Override
                    public void onScanEnd() {
                        Log.d(TAG, "onScanEnd");
                        post(() -> infoTextView.setText("共" + data.size() + "个安装包"));
                    }

                    @Override
                    public void onScanning(String paramString, int progress) {
                        if (progress != lastProgress) {
                            lastProgress = progress;
                            post(() -> infoTextView.setText("已发现" + data.size() + "个安装包，已扫描" + progress + "%"));
                        }
                    }

                    @Override
                    public void onScanningFiles(FileInfo info, int type) {
                        Log.d(TAG, "onScanningFiles");
                        InstalledAppInfo appInfo = parseFromApk(context, new File(info.getFilePath()));
                        if (appInfo != null) {
                            synchronized (data) {
                                post(() -> {
                                    data.add(appInfo);
                                    if (data.size() < 10) {
                                        recyclerLayout.notifyDataSetChanged();
                                    } else {
                                        recyclerLayout.notifyItemInserted(data.size() - 1);
                                    }
                                    recyclerLayout.notifyItemRangeChanged(data.size() - 1, 1);
//                                    recyclerLayout.notifyItemInserted(data.size() - 1);
                                });
                            }
                        }
                    }
                });
    }

    private void showSortPopWindow() {
        new RecyclerPartShadowDialogFragment()
                .addItems("按应用名称", "按占用空间", "按安装时间", "按更新时间", "按使用频率")
                .setSelectedItem(sortPosition)
                .setOnItemClickListener((view, title, position) -> {
                    sortPosition = position;
                    sortTextView.setText(title);
                    switch (position) {
                        case 0:
                            Collections.sort(data, new Comparator<InstalledAppInfo>() {
                                @Override
                                public int compare(InstalledAppInfo o1, InstalledAppInfo o2) {
                                    return o1.getName().compareTo(o2.getName());
                                }
                            });
                            break;
                        case 1:
                            Collections.sort(data, new Comparator<InstalledAppInfo>() {
                                @Override
                                public int compare(InstalledAppInfo o1, InstalledAppInfo o2) {
                                    return (int) (o1.getAppSize() - o2.getAppSize());
                                }
                            });
                            break;
                        case 2:
//                                Collections.sort(appInfoList, new Comparator<InstalledAppInfo>() {
//                                    @Override
//                                    public int compare(InstalledAppInfo o1, InstalledAppInfo o2) {
//                                        return o1.getInstallTime().compareTo(o2.getInstallTime());
//                                    }
//                                });
                            break;
                        case 3:
//                                Collections.sort(appInfoList, new Comparator<InstalledAppInfo>() {
//                                    @Override
//                                    public int compare(InstalledAppInfo o1, InstalledAppInfo o2) {
//                                        return o1.getRecentUpdateTime().compareTo(o2.getRecentUpdateTime());
//                                    }
//                                });
                            break;
                        case 4:
//                                Collections.sort(appInfoList, new Comparator<InstalledAppInfo>() {
//                                    @Override
//                                    public int compare(InstalledAppInfo o1, InstalledAppInfo o2) {
//                                        return o1.getName().compareTo(o2.getName());
//                                    }
//                                });
                            break;
                        default:
                            break;
                    }
                    recyclerLayout.notifyDataSetChanged();
                })
                .setAttachView(sortTextView)
                .show(context);
    }

    private InstalledAppInfo parseFromApk(Context context, File file) {
        if (context == null) {
            return null;
        }
        PackageInfo packageInfo = null;
        PackageManager manager = context.getPackageManager();
        try {
            packageInfo = manager.getPackageArchiveInfo(file.getPath(), PackageManager.GET_ACTIVITIES);
        } catch (Throwable e) {
            e.printStackTrace();
//            return null;
        }

        InstalledAppInfo appInfo = new InstalledAppInfo();
        appInfo.setApkFilePath(file.getPath());
        appInfo.setAppSize(file.length());
        appInfo.setFormattedAppSize(FileUtils.formatFileSize(file.length()));
        appInfo.setTempXPK(true);
        appInfo.setTempInstalled(false);
        if (packageInfo == null) {
            appInfo.setDamaged(true);
            appInfo.setName(file.getName());
            return appInfo;
        }



        packageInfo.applicationInfo.sourceDir = file.getPath();
        packageInfo.applicationInfo.publicSourceDir = file.getPath();
        appInfo.setName(packageInfo.applicationInfo.loadLabel(manager).toString());
//        Log.d("parseFromApk", "name=" + appInfo.getName());
        appInfo.setPackageName(packageInfo.packageName);
        appInfo.setIdAndType(AppUpdateManager.getInstance().getAppIdAndType(appInfo.getPackageName()));
        appInfo.setVersionName(packageInfo.versionName);
        appInfo.setVersionCode(packageInfo.versionCode);
        return appInfo;
    }

    private InstalledAppInfo parseFromXpk(File file) {
        XpkInfo xpkInfo;
        try {
            xpkInfo = XpkInfo.getXPKManifestDom(new ZipFile(file));
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            return null;
        }
        InstalledAppInfo appInfo = new InstalledAppInfo();
        appInfo.setApkFilePath(file.getPath());
        appInfo.setName(xpkInfo.getAppName());
        appInfo.setPackageName(xpkInfo.getPackageName());
        appInfo.setId(AppUpdateManager.getInstance().getAppIdAndType(appInfo.getPackageName()));
        appInfo.setVersionName(xpkInfo.getVersionName());
        appInfo.setVersionCode(xpkInfo.getVersionCode());
        appInfo.setAppSize(file.length());
        appInfo.setFormattedAppSize(FileUtils.formatFileSize(file.length()));
        appInfo.setTempXPK(true);
        appInfo.setTempInstalled(false);
        return appInfo;
    }

    public void onMenuClicked(View view, InstalledAppInfo updateInfo) {
        new ArrowMenuDialogFragment()
                .setOptionMenus(optionMenus)
                .setOrientation(LinearLayout.HORIZONTAL)
                .setOnItemClickListener((position, menu) -> {
                    switch (position) {
                        case 0:
                            AToast.normal("详细信息");
                            break;
                        case 1:
                            AToast.normal(updateInfo.getApkFilePath());
                            AppUtil.shareApk(getContext(), updateInfo.getApkFilePath());
                            break;
                        case 2:
                            AppUtil.deleteApk(updateInfo.getApkFilePath());
                            break;
                        case 3:
                            AppUtil.installApk(getActivity(), updateInfo.getApkFilePath());
                            break;
                        default:
                            AToast.warning("未知操作！");
                            break;
                    }
                })
                .setAttachView(view)
                .show(context);
    }
}
