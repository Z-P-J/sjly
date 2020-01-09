package com.zpj.shouji.market.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.zpj.shouji.market.bean.InstalledAppInfo;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

public class LoadAppTask extends AsyncTask<Void, Void, List<InstalledAppInfo>> {

    private WeakReference<Fragment> fragmentWeakReference;

    private CallBack callBack;

    private LoadAppTask(Fragment fragment) {
        fragmentWeakReference = new WeakReference<>(fragment);
    }

    public static LoadAppTask with(Fragment fragment) {
        return new LoadAppTask(fragment);
    }

    public LoadAppTask setCallBack(CallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    @Override
    protected List<InstalledAppInfo> doInBackground(Void... voids) {
        if (fragmentWeakReference.get() == null || fragmentWeakReference.get().getContext() == null) {
            cancel(true);
        }
        if (callBack != null) {
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
                    callBack.onGetUserApp(installedAppInfo);
                } else {
                    // 系统应用
                    callBack.onGetSystemApp(installedAppInfo);
                }
                if (new File(AppUtil.getDefaultAppBackupFolder() + installedAppInfo.getName() + "_" + installedAppInfo.getVersionName() + ".apk").exists()) {
                    // 已备份
                    callBack.onGetBackupApp(installedAppInfo);
                }
                if (!packageInfo.applicationInfo.enabled) {
                    // 已禁用
                    callBack.onGetForbidApp(installedAppInfo);
                }
                // todo 已隐藏
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<InstalledAppInfo> installedAppInfos) {
        if (callBack != null) {
            callBack.onLoadAppFinished();
        }
    }

    public void onDestroy() {
        if (getStatus() != Status.FINISHED) {
            cancel(true);
        }
    }

    public interface CallBack {
        void onGetUserApp(InstalledAppInfo appInfo);
        void onGetSystemApp(InstalledAppInfo appInfo);
        void onGetBackupApp(InstalledAppInfo appInfo);
        void onGetForbidApp(InstalledAppInfo appInfo);
        void onGetHiddenApp(InstalledAppInfo appInfo);
        void onLoadAppFinished();
    }

}
