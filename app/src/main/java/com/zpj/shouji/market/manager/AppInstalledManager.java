package com.zpj.shouji.market.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.zpj.http.core.HttpObserver;
import com.zpj.rxlife.RxLife;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.toast.ZToast;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.FormatUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AppInstalledManager { //  extends BroadcastReceiver

    private static final String TAG = AppInstalledManager.class.getName();

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
        isLoaded.set(false);
        isLoading.set(false);
    }

    private synchronized InstalledAppInfo onAppAdded(PackageManager manager, PackageInfo packageInfo) {
//        for (InstalledAppInfo info : installedAppInfoList) {
//            if (info.getPackageName().equals(packageInfo.packageName)) {
//                return info;
//            }
//        }
        InstalledAppInfo installedAppInfo = new InstalledAppInfo();
        installedAppInfo.setName(packageInfo.applicationInfo.loadLabel(manager).toString());
        installedAppInfo.setPackageName(packageInfo.packageName);
        installedAppInfo.setSortName(installedAppInfo.getName());
        installedAppInfo.setIdAndType(AppUpdateManager.getInstance().getAppIdAndType(installedAppInfo.getPackageName()));
        installedAppInfo.setVersionName(packageInfo.versionName);
        installedAppInfo.setApkFilePath(packageInfo.applicationInfo.publicSourceDir);
        installedAppInfo.setFormattedAppSize(FormatUtils.formatSize(new File(installedAppInfo.getApkFilePath()).length()));
        installedAppInfo.setVersionCode(packageInfo.versionCode);
        installedAppInfo.setTempXPK(false);
        installedAppInfo.setTempInstalled(true);
        installedAppInfo.setEnabled(packageInfo.applicationInfo.enabled);
        installedAppInfo.setBackuped(new File(AppUtil.getDefaultAppBackupFolder() + installedAppInfo.getName() + "_" + installedAppInfo.getVersionName() + ".apk").exists());
        installedAppInfo.setUserApp((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0);
        installedAppInfo.setFirstInstallTime(packageInfo.firstInstallTime);
        installedAppInfo.setLastUpdateTime(packageInfo.lastUpdateTime);
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

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        //接收安装广播
//        if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {
//            String packageName = intent.getDataString();
//            System.out.println();
//            ZToast.warning("安装了:" + packageName + "包名的程序");
//            new HttpObserver<>(emitter -> {
//                PackageManager packageManager = context.getPackageManager();
//                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
//                getInstance().onAppAdded(packageManager, packageInfo);
//            }).subscribe();
//        }
//        //接收卸载广播
//        if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
//            String packageName = intent.getDataString();
//            ZToast.warning("卸载了:" + packageName + "包名的程序");
//            new HttpObserver<>(emitter -> getInstance().onAppRemoved(packageName)).subscribe();
//        }
//    }

    public AppInstalledManager addListener(CallBack callBack) {
        synchronized (callbacks) {
            callbacks.add(new WeakReference<>(callBack));
        }
        return this;
    }

    public void removeListener(CallBack callBack) {
        synchronized (callbacks) {
            for (WeakReference<CallBack> appBackupListener : callbacks) {
                if (appBackupListener.get() != null && appBackupListener.get() == callBack) {
                    callbacks.remove(appBackupListener);
                    break;
                }
            }
        }
    }

    public void onDestroy() {
        RxLife.removeByTag(TAG);
        manager = null;
        installedAppInfoList.clear();
        callbacks.clear();
        isLoaded.set(false);
        isLoading.set(false);
    }

    private void onNext(InstalledAppInfo installedAppInfo) {
        synchronized (callbacks) {
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
        }
    }

    private void onFinished() {
        synchronized (callbacks) {
            for (WeakReference<CallBack> callBackWeakReference : callbacks) {
                if (callBackWeakReference.get() != null) {
                    callBackWeakReference.get().onLoadAppFinished();
                }
            }
            callbacks.clear();
        }
    }

    public void loadApps(CallBack callBack) {
        if (callBack == null) {
            loadApps();
            return;
        }
        if (isLoaded.get() && !isLoading.get()) {
            for (InstalledAppInfo appInfo : installedAppInfoList) {
                onNext(appInfo);
            }
            onFinished();
        } else if (!isLoaded.get() && !isLoading.get()) {
            addListener(callBack);
            loadApps();
        } else {
            synchronized (callbacks) {
                if (isLoaded.get() && !isLoading.get()) {
                    loadApps(callBack);
                } else {
                    callbacks.add(new WeakReference<>(callBack));
                }
            }
        }

//        Observable.create(
//                (ObservableOnSubscribe<InstalledAppInfo>) emitter -> {
//                    if (isLoaded.get() && !isLoading.get() && !installedAppInfoList.isEmpty()) {
//                        isLoading.set(true);
//                        for (InstalledAppInfo appInfo : installedAppInfoList) {
//                            emitter.onNext(appInfo);
//                        }
//                        isLoading.set(false);
//                    } else {
//                        isLoading.set(true);
//                        PackageManager manager = context.getPackageManager();
//                        List<PackageInfo> packageInfoList = manager.getInstalledPackages(0);
//                        for (PackageInfo packageInfo : packageInfoList) {
//                            emitter.onNext(onAppAdded(manager, packageInfo));
//                        }
//                        isLoaded.set(true);
//                        isLoading.set(false);
//                    }
//                    emitter.onComplete();
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(RxLife.bindTag(TAG))
//                .doOnNext(this::onNext)
//                .doOnError(Throwable::printStackTrace)
//                .doOnComplete(this::onFinished)
//                .subscribe();
    }

    public void loadApps() {
        if (isLoaded.get() && !isLoading.get()) {
            return;
        }
        isLoaded.set(false);
        isLoading.set(true);
        RxLife.removeByTag(TAG);
        installedAppInfoList.clear();
        Observable.create(
                (ObservableOnSubscribe<List<InstalledAppInfo>>) emitter -> {
                    PackageManager manager = ContextUtils.getApplicationContext().getPackageManager();
                    List<PackageInfo> packageInfoList = manager.getInstalledPackages(0);
                    List<InstalledAppInfo> installedAppInfos = new ArrayList<>();
                    for (PackageInfo packageInfo : packageInfoList) {
                        installedAppInfos.add(onAppAdded(manager, packageInfo));
                    }
                    emitter.onNext(installedAppInfos);
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLife.bindTag(TAG))
                .doOnNext(new Consumer<List<InstalledAppInfo>>() {
                    @Override
                    public void accept(List<InstalledAppInfo> installedAppInfos) throws Exception {
                        installedAppInfoList.addAll(installedAppInfos);
                        isLoaded.set(true);
                        isLoading.set(false);
                        for (InstalledAppInfo info : installedAppInfos) {
                            onNext(info);
                        }
                        onFinished();
                    }
                })
                .doOnError(Throwable::printStackTrace)
//                    .doOnComplete(this::onFinished)
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
