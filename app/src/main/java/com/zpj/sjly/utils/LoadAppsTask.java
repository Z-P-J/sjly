package com.zpj.sjly.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.zpj.sjly.bean.InstalledAppInfo;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LoadAppsTask extends AsyncTask<Void, Void, List<InstalledAppInfo>> {

    private WeakReference<Fragment> fragmentWeakReference;

    private static final List<InstalledAppInfo> userAppList = new ArrayList<>();
    private static final List<InstalledAppInfo> systemAppList = new ArrayList<>();
    private static final List<InstalledAppInfo> hiddenAppList = new ArrayList<>();
    private static final List<InstalledAppInfo> forbidAppList = new ArrayList<>();

    private CallBack callBack;

    private LoadAppsTask(Fragment fragment) {
        fragmentWeakReference = new WeakReference<>(fragment);
        userAppList.clear();
        systemAppList.clear();
        hiddenAppList.clear();
        forbidAppList.clear();
    }

    public static LoadAppsTask with(Fragment fragment) {
        return new LoadAppsTask(fragment);
    }

    public LoadAppsTask setCallBack(CallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    @Override
    protected List<InstalledAppInfo> doInBackground(Void... voids) {
        List<InstalledAppInfo> installedAppInfoList = new ArrayList<>();
        if (fragmentWeakReference.get() == null || fragmentWeakReference.get().getContext() == null) {
            cancel(true);
        }
        PackageManager manager = fragmentWeakReference.get().getContext().getPackageManager();
        List<PackageInfo> packageInfoList = manager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfoList) {
            InstalledAppInfo installedAppInfo = new InstalledAppInfo();
            installedAppInfo.setName(packageInfo.applicationInfo.loadLabel(manager).toString());
            installedAppInfo.setPackageName(packageInfo.packageName);
            installedAppInfo.setSortName(installedAppInfo.getName());
            installedAppInfo.setIdAndType(AppUpdateHelper.getInstance().getAppIdAndType(installedAppInfo.getPackageName()));
            installedAppInfo.setVersionName(packageInfo.versionName);
            installedAppInfo.setApkFilePath(packageInfo.applicationInfo.publicSourceDir);
            installedAppInfo.setFormattedAppSize(FileUtils.formatFileSize(new File(installedAppInfo.getApkFilePath()).length()));
            installedAppInfo.setVersionCode(packageInfo.versionCode);
            installedAppInfo.setTempXPK(false);
            installedAppInfo.setTempInstalled(true);

            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 非系统应用
                userAppList.add(installedAppInfo);
            } else {
                // 系统应用
                systemAppList.add(installedAppInfo);
            }
//            installedAppInfoList.add(installedAppInfo);
        }
//        Collections.sort(installedAppInfoList, new Comparator<InstalledAppInfo>() {
//            @Override
//            public int compare(InstalledAppInfo o1, InstalledAppInfo o2) {
//                return o1.getName().compareTo(o2.getName());
//            }
//        });
        return installedAppInfoList;
    }

    @Override
    protected void onPostExecute(List<InstalledAppInfo> installedAppInfos) {
        if (callBack != null) {
            callBack.onPostExecute(userAppList, systemAppList);
        }
    }

    public void onDestroy() {
        if (getStatus() != Status.FINISHED) {
            cancel(true);
        }
    }

    public interface CallBack {
        void onPostExecute(List<InstalledAppInfo> userAppList, List<InstalledAppInfo> systemAppList);
    }

}
