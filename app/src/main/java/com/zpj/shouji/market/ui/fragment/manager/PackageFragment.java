package com.zpj.shouji.market.ui.fragment.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.zpj.popupmenuview.OptionMenu;
import com.zpj.popupmenuview.OptionMenuView;
import com.zpj.popupmenuview.PopupMenuView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.bean.InstalledAppInfo;
import com.zpj.shouji.market.bean.XpkInfo;
import com.zpj.shouji.market.ui.adapter.AppManagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.view.RecyclerPopup;
import com.zpj.shouji.market.utils.AppUpdateHelper;
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.shouji.market.utils.FileScanner;
import com.zpj.shouji.market.utils.FileUtils;
import com.zpj.shouji.market.utils.LoadApkTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipFile;

public class PackageFragment extends BaseFragment
        implements FileScanner.FileChecker,
        FileScanner.ScanListener,
        AppManagerAdapter.OnItemClickListener {

    private static final String TAG = "PackageFragment";

    private static final List<OptionMenu> optionMenus = new ArrayList<>();
    static {
        optionMenus.add(new OptionMenu("详细信息"));
        optionMenus.add(new OptionMenu("分享"));
        optionMenus.add(new OptionMenu("删除"));
        optionMenus.add(new OptionMenu("安装"));
    }

    private final List<InstalledAppInfo> appInfoList = new ArrayList<>();
    private AppManagerAdapter adapter;
    private RecyclerView recyclerView;

    private FileScanner fileScanner;

    private TextView sortTextView;
    private TextView infoTextView;

    private long startTime;

    private AsyncTask<Void, Void, List<String>> loadApkTask;

    private int sortPosition = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_package_manager;
    }

    @Override
    protected boolean supportSwipeBack() {
        return false;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        initFileScanner();
        initView(view);
        loadApk();
    }

    @Override
    public void onDestroy() {
        if (fileScanner != null) {
            fileScanner.cancel();
            fileScanner = null;
        }
        if (loadApkTask != null) {
            loadApkTask.cancel(true);
        }
        super.onDestroy();
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

    private void initFileScanner() {
        fileScanner = new FileScanner(this, this);
        fileScanner.setDirFilter(new FileScanner.DirFilter() {
            @Override
            public boolean accept(File dir) {
                String fileNameLowerCase = dir.getName().toLowerCase();

                String keyword = "tuniuapp";
                if (keyword.equalsIgnoreCase(fileNameLowerCase)) {
                    return false;
                }

                keyword = "cache";
                if (keyword.equalsIgnoreCase(fileNameLowerCase) || fileNameLowerCase.endsWith(keyword)) {
                    return false;
                }

                keyword = "log";
                if (keyword.equalsIgnoreCase(fileNameLowerCase) || fileNameLowerCase.endsWith(keyword)) {
                    return false;
                }

                keyword = "dump";
                return !keyword.equalsIgnoreCase(fileNameLowerCase) && !fileNameLowerCase.endsWith(keyword);
            }
        });
    }

    private void initView(View view) {

        sortTextView = view.findViewById(R.id.text_sort);
        sortTextView.setOnClickListener(v -> showSortPopWindow());

        infoTextView = view.findViewById(R.id.text_info);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setTag(false);
        adapter = new AppManagerAdapter(appInfoList);
        adapter.setItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadApk() {
        startTime = System.currentTimeMillis();
        loadApkTask = LoadApkTask.with(this)
                .setCallBack(new LoadApkTask.CallBack() {

                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onPostExecute(List<String> installedAppInfos) {
//                        Log.d(TAG, "installedAppInfos=" + installedAppInfos);
                        if (fileScanner != null) {
                            fileScanner.execute(installedAppInfos);
                        }
                    }

                })
                .execute();
    }

    private void showSortPopWindow() {

        RecyclerPopup.with(context)
                .addItems("按应用名称", "按占用空间", "按安装时间", "按更新时间", "按使用频率")
                .setSelectedItem(sortPosition)
                .setOnItemClickListener((view, title, position) -> {
                    sortPosition = position;
                    sortTextView.setText(title);
                    switch (position) {
                        case 0:
                            Collections.sort(appInfoList, new Comparator<InstalledAppInfo>() {
                                @Override
                                public int compare(InstalledAppInfo o1, InstalledAppInfo o2) {
                                    return o1.getName().compareTo(o2.getName());
                                }
                            });
                            break;
                        case 1:
                            Collections.sort(appInfoList, new Comparator<InstalledAppInfo>() {
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
                    adapter.notifyDataSetChanged();
                })
                .show(sortTextView);
    }




    // --------------------------------------FileScanner----------------------------------

    @Override
    public void onStarted() {
        Log.d(TAG, "onStarted");
    }

    @Override
    public void onFindFile(FileScanner.FileItem fileItem) {
        Log.d(TAG, "onFindFile");
        if (fileItem instanceof InstalledAppInfo) {
            appInfoList.add((InstalledAppInfo) fileItem);
            long currentTime = System.currentTimeMillis();
            infoTextView.setText("已发现" + appInfoList.size() + "个安装包，用时" +  ((currentTime - startTime) / 1000) + "秒");
            adapter.notifyItemInserted(appInfoList.size() - 1);
        }
    }

    @Override
    public void onUpdateProgress(int totalLength, int completedLength) {

    }

    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted");
    }

    @Override
    public void onCanceled() {
        Log.d(TAG, "onCanceled");
    }

    @Override
    public void onScanDir(File dir) {
        Log.d(TAG, "onScanDir dir=" + dir);
    }

    @Override
    public FileScanner.FileItem accept(File pathname) {
        Log.d(TAG, "accept pathname=" + pathname.getName());
        if (pathname.isFile()) {
            String subSuffix = FileUtils.subSuffix(pathname.getName());
            if (".apk".equalsIgnoreCase(subSuffix)) {
                return parseFromApk(getContext(), pathname);
            } else if (".xpk".equalsIgnoreCase(subSuffix)) {
                return parseFromXpk(pathname);
            }
        }
        return null;
    }

    @Override
    public void onFinished() {
        Log.d(TAG, "onFinished");
    }

    private InstalledAppInfo parseFromApk(Context context, File file) {
        if (context == null) {
            return null;
        }
        PackageInfo packageInfo;
        PackageManager manager = context.getPackageManager();
        try {
            packageInfo = manager.getPackageArchiveInfo(file.getPath(), PackageManager.GET_ACTIVITIES);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }

        if (packageInfo == null) {
            return null;
        }

        InstalledAppInfo appInfo = new InstalledAppInfo();
        appInfo.setApkFilePath(file.getPath());

        packageInfo.applicationInfo.sourceDir = file.getPath();
        packageInfo.applicationInfo.publicSourceDir = file.getPath();
        appInfo.setName(packageInfo.applicationInfo.loadLabel(manager).toString());
//        Log.d("parseFromApk", "name=" + appInfo.getName());
        appInfo.setPackageName(packageInfo.packageName);
        appInfo.setIdAndType(AppUpdateHelper.getInstance().getAppIdAndType(appInfo.getPackageName()));
        appInfo.setVersionName(packageInfo.versionName);
        appInfo.setVersionCode(packageInfo.versionCode);
        appInfo.setAppSize(file.length());
        appInfo.setFormattedAppSize(FileUtils.formatFileSize(file.length()));
        appInfo.setTempXPK(true);
        appInfo.setTempInstalled(false);
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
        appInfo.setId(AppUpdateHelper.getInstance().getAppIdAndType(appInfo.getPackageName()));
        appInfo.setVersionName(xpkInfo.getVersionName());
        appInfo.setVersionCode(xpkInfo.getVersionCode());
        appInfo.setAppSize(file.length());
        appInfo.setFormattedAppSize(FileUtils.formatFileSize(file.length()));
        appInfo.setTempXPK(true);
        appInfo.setTempInstalled(false);
        return appInfo;
    }



    // implement from AppManagerAdapter.OnItemClickListener

    @Override
    public void onItemClick(AppManagerAdapter.ViewHolder holder, int position, InstalledAppInfo updateInfo) {
//        AToast.normal("todo 详细信息");
        AppUtil.installApk(getActivity(), updateInfo.getApkFilePath());
    }

    @Override
    public void onMenuClicked(View view, InstalledAppInfo updateInfo) {
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
                        return true;
                    }
                }).show(view);
    }

    @Override
    public void onCheckBoxClicked(int allCount, int selectCount) {

    }

    @Override
    public void onEnterSelectMode() {

    }

    @Override
    public void onExitSelectMode() {

    }

}
