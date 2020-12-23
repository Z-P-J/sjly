package com.zpj.shouji.market.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.zpj.toast.ZToast;
import com.zpj.http.core.ObservableTask;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.shouji.market.utils.FileUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AppInstalledManager extends BroadcastReceiver {

    private volatile static AppInstalledManager manager;

    private final List<InstalledAppInfo> installedAppInfoList = new ArrayList<>();
    private final AtomicBoolean isLoading = new AtomicBoolean(false);
    private final AtomicBoolean isLoaded = new AtomicBoolean(false);

    private final List<WeakReference<CallBack>> callbacks = new ArrayList<>();

    public static AppInstalledManager getInstance() {
        if (manager == null) {
            synchronized (AppInstalledManager.class) {
                if (manager == null) {
                    manager = new AppInstalledManager();
                }
            }
        }
        return manager;
    }

    private AppInstalledManager() {

    }

    private synchronized InstalledAppInfo onAppAdded(PackageManager manager, PackageInfo packageInfo) {
        for (InstalledAppInfo info : installedAppInfoList) {
            if (info.getPackageName().equals(packageInfo.packageName)) {
                return info;
            }
        }
        InstalledAppInfo installedAppInfo = new InstalledAppInfo();
        installedAppInfo.setName(packageInfo.applicationInfo.loadLabel(manager).toString());
        installedAppInfo.setPackageName(packageInfo.packageName);
        installedAppInfo.setSortName(installedAppInfo.getName());
        installedAppInfo.setIdAndType(AppUpdateManager.getInstance().getAppIdAndType(installedAppInfo.getPackageName()));
        installedAppInfo.setVersionName(packageInfo.versionName);
        installedAppInfo.setApkFilePath(packageInfo.applicationInfo.publicSourceDir);
        installedAppInfo.setFormattedAppSize(FileUtils.formatFileSize(new File(installedAppInfo.getApkFilePath()).length()));
        installedAppInfo.setVersionCode(packageInfo.versionCode);
        installedAppInfo.setTempXPK(false);
        installedAppInfo.setTempInstalled(true);
        installedAppInfo.setEnabled(packageInfo.applicationInfo.enabled);
        installedAppInfo.setBackuped(new File(AppUtil.getDefaultAppBackupFolder() + installedAppInfo.getName() + "_" + installedAppInfo.getVersionName() + ".apk").exists());
        installedAppInfo.setUserApp((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0);
        installedAppInfoList.add(installedAppInfo);
        return installedAppInfo;
    }

    private synchronized void onAppRemoved(String packageName) {
        for (InstalledAppInfo info : installedAppInfoList) {
            if (info.getPackageName().equals(packageName)) {
                installedAppInfoList.remove(info);
                return;
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //接收安装广播
        if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {
            String packageName = intent.getDataString();
            System.out.println();
            ZToast.warning("安装了:" + packageName + "包名的程序");
            new ObservableTask<>(emitter -> {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                getInstance().onAppAdded(packageManager, packageInfo);
            }).subscribe();
        }
        //接收卸载广播
        if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
            String packageName = intent.getDataString();
            ZToast.warning("卸载了:" + packageName + "包名的程序");
            new ObservableTask<>(emitter -> getInstance().onAppRemoved(packageName)).subscribe();
        }
    }

    public AppInstalledManager addListener(CallBack callBack) {
        callbacks.add(new WeakReference<>(callBack));
        return this;
    }

    public void removeListener(CallBack callBack) {
        for (WeakReference<CallBack> appBackupListener : callbacks) {
            if (appBackupListener.get() != null && appBackupListener.get() == callBack) {
                callbacks.remove(appBackupListener);
                break;
            }
        }
    }

    public void loadApps(Context context) {
        if (context == null) {
            return;
        }
        Observable.create(
                (ObservableOnSubscribe<InstalledAppInfo>) emitter -> {
                    if (isLoaded.get() && !isLoading.get() && !installedAppInfoList.isEmpty()) {
                        isLoading.set(true);
                        for (InstalledAppInfo appInfo : installedAppInfoList) {
                            emitter.onNext(appInfo);
                        }
                    } else {
                        isLoading.set(true);
                        PackageManager manager = context.getPackageManager();
                        List<PackageInfo> packageInfoList = manager.getInstalledPackages(0);
                        for (PackageInfo packageInfo : packageInfoList) {
                            emitter.onNext(onAppAdded(manager, packageInfo));
                        }
                        isLoaded.set(true);
                        isLoading.set(false);
                    }
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(installedAppInfo -> {

                    for (WeakReference<CallBack> callBackWeakReference : callbacks) {
                        CallBack callBack = callBackWeakReference.get();
                        if (callBack != null) {
                            if (installedAppInfo.isUserApp()) {
                                // 非系统应用
                                callBack.onGetUserApp(installedAppInfo);
                            } else {
                                // 系统应用
                                callBack.onGetSystemApp(installedAppInfo);
                            }

                            if (installedAppInfo.isBackuped()) {
                                // 已备份
                                callBack.onGetBackupApp(installedAppInfo);
                            }

                            if (!installedAppInfo.isEnabled()) {
                                // 已禁用
                                callBack.onGetForbidApp(installedAppInfo);
                            }

                        }
                    }

                    // TODO 获取隐藏应用


//                    if (installedAppInfo.isUserApp()) {
//                        // 非系统应用
//                        for (WeakReference<CallBack> callBackWeakReference : callbacks) {
//                            if (callBackWeakReference.get() != null) {
//                                callBackWeakReference.get().onGetUserApp(installedAppInfo);
//                            }
//                        }
//                    } else {
//                        // 系统应用
//                        for (WeakReference<CallBack> callBackWeakReference : callbacks) {
//                            if (callBackWeakReference.get() != null) {
//                                callBackWeakReference.get().onGetSystemApp(installedAppInfo);
//                            }
//                        }
//                    }
//                    if (installedAppInfo.isBackuped()) {
//                        // 已备份
//                        for (WeakReference<CallBack> callBackWeakReference : callbacks) {
//                            if (callBackWeakReference.get() != null) {
//                                callBackWeakReference.get().onGetBackupApp(installedAppInfo);
//                            }
//                        }
//                    }
//                    if (!installedAppInfo.isEnabled()) {
//                        // 已禁用
//                        for (WeakReference<CallBack> callBackWeakReference : callbacks) {
//                            if (callBackWeakReference.get() != null) {
//                                callBackWeakReference.get().onGetForbidApp(installedAppInfo);
//                            }
//                        }
//                    }

                })
                .doOnError(Throwable::printStackTrace)
                .doOnComplete(() -> {
                    for (WeakReference<CallBack> callBackWeakReference : callbacks) {
                        if (callBackWeakReference.get() != null) {
                            callBackWeakReference.get().onLoadAppFinished();
//                            callbacks.remove(callBackWeakReference);
                        }
                    }
                })
                .subscribe();
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
