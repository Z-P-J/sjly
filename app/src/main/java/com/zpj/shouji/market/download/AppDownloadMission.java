package com.zpj.shouji.market.download;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.zpj.downloader.BaseMission;
import com.zpj.downloader.DownloadMission;
import com.zpj.downloader.constant.Error;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.http.core.IHttp;
import com.zpj.notification.ZNotify;
import com.zpj.rxbus.RxBus;
import com.zpj.rxlife.RxLife;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.installer.InstallMode;
import com.zpj.shouji.market.installer.ApkInstaller;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.activity.MainActivity;
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.toast.ZToast;
import com.zpj.utils.AppUtils;
import com.zpj.utils.Callback;
import com.zpj.utils.FileUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AppDownloadMission extends BaseMission<AppDownloadMission> {

    public interface AppMissionListener extends DownloadMission.MissionListener {

        void onInstalled();

        void onUninstalled();

    }

    private static final String TAG = AppDownloadMission.class.getSimpleName();

    private String appIcon;
    private String packageName;
    private String appId;
    private String appType;
    private String appName;
    private boolean isShareApp;

    private transient boolean isInstalled;
    private transient boolean isUpgrade;
    private transient String apkVersion;
    private transient String appVersion;

    public static AppDownloadMission create(String appId, String appName, String packageName, String appType) {
        return create(appId, appName, packageName, appType, false);
    }

    public static AppDownloadMission create(String appId, String appName, String packageName, String appType, boolean isShareApp) {
        AppDownloadMission mission = new AppDownloadMission();
        mission.isShareApp = isShareApp;
        mission.packageName = packageName;
        mission.appId = appId;
        mission.appType = appType;
        mission.appName = appName;
        mission.name = appName + "_" + appId + ".apk";
        mission.uuid = UUID.randomUUID().toString();
        mission.createTime = System.currentTimeMillis();
        mission.missionStatus = MissionStatus.INITING;
        mission.setCookie(UserManager.getInstance().getCookie());
        mission.setUserAgent(HttpApi.USER_AGENT);
        return mission;
    }

    @Override
    protected void initMission() {
        Log.d("AppDownloadMission", "initMission");
        if (TextUtils.isEmpty(url)) {
            String downloadUrl;
            if (isShareApp) {
                downloadUrl = String.format("http://tt.shouji.com.cn/wap/down/cmwap/share?id=%s&sjly=199", appId);
            } else {
                downloadUrl = String.format("http://tt.shouji.com.cn/wap/down/cmwap/package?package=%s&id=%s&sjly=199", packageName, appId);
            }
            Log.d("AppDownloadMission", "initMission downloadUrl=" + downloadUrl);
            HttpApi.getHtml(downloadUrl)
                    .onSuccess(data -> {
                        Log.d("AppDownloadMission", "data=" + data);
                        url = data.selectFirst("url").text();
                        if (url.endsWith(".zip") && name != null) {
                            name = name.replace(".apk", ".zip");
                        }
                        originUrl = url;
                        length = Long.parseLong(data.selectFirst("size").text());
                        Log.d("AppDownloadMission", "length=" + length);
                        AppDownloadMission.super.initMission();
                    })
                    .onError(new IHttp.OnErrorListener() {
                        @Override
                        public void onError(Throwable throwable) {
                            notifyError(new Error(throwable.getMessage()));
                        }
                    })
                    .subscribe();
        } else {
            super.initMission();
        }
    }

    @Override
    protected void onFinish() {
        if (errCode > 0) {
            return;
        }
        checkUpgrade();
        if (FileUtils.getFileType(name) == FileUtils.FileType.ARCHIVE) {
//            TODO 解压
            super.onFinish();
        } else {
            super.onFinish();
        }
        if (AppConfig.isInstallAfterDownloaded()) {
            install();
        }
    }

    @Override
    protected void onCreate() {
        isInstalled = AppUtils.isInstalled(getContext(), packageName);
        isUpgrade = checkUpgrade();
        RxBus.observe(this, packageName, String.class)
                .doOnNext(new RxBus.SingleConsumer<String>() {
                    @Override
                    public void onAccept(String action) throws Exception {
                        if (mListeners == null) {
                            return;
                        }
                        Log.d(TAG, "action=" + action);
                        switch (action) {
                            case Intent.ACTION_PACKAGE_ADDED:
                            case Intent.ACTION_PACKAGE_REPLACED:
                                isInstalled = true;
                                isUpgrade = checkUpgrade();
                                for (WeakReference<MissionListener> weakRef : mListeners) {
                                    MissionListener listener = weakRef.get();
                                    if (listener instanceof AppMissionListener) {
                                        ((AppMissionListener) listener).onInstalled();
                                    }
                                }
                                break;
                            case Intent.ACTION_PACKAGE_REMOVED:
                                isInstalled = false;
                                isUpgrade = false;
                                for (WeakReference<MissionListener> weakRef : mListeners) {
                                    MissionListener listener = weakRef.get();
                                    if (listener instanceof AppMissionListener) {
                                        ((AppMissionListener) listener).onUninstalled();
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                })
                .subscribe();
    }

    @Override
    protected void onDestroy() {
        RxBus.removeObservers(this);
    }

    public boolean isUpgrade() {
//        if (isInstalled()) {
//            String apkVersion = AppUtils.getApkVersionName(getContext(), getFilePath());
//            String appVersion = AppUtils.getAppVersionName(getContext(), getPackageName());
//            return AppUtil.isNewVersion(appVersion, apkVersion);
//        }
//        return false;
        return isUpgrade;
    }

    private boolean checkUpgrade() {
        if (isFinished() && isInstalled) {
            apkVersion = AppUtils.getApkVersionName(getContext(), getFilePath());
            appVersion = AppUtils.getAppVersionName(getContext(), getPackageName());
            return AppUtil.isNewVersion(appVersion, apkVersion);
        }
        return false;
    }

    public boolean isInstalled() {
//        boolean isInstalled;
//        PackageManager packageManager = getContext().getPackageManager();
//        try {
//            packageManager.getPackageInfo(packageName, 0);
//            isInstalled = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            isInstalled = false;
//        }
//        return isInstalled;
        if (isFinished() && isInstalled) {
            Log.d(TAG, "apkVersion=" + apkVersion + " appVersion=" + appVersion);
            return TextUtils.equals(apkVersion, appVersion);
        }
        return false;
//        return AppUtils.isApkInstalled(getContext(), packageName);
//        return AppUtils.isInstalled(getContext(), packageName);
    }

    public void install() {
        EventBus.getActivity(activity -> {
            if (isInstalled() && AppConfig.isCheckSignature()) {
                Observable.create(
                        (ObservableOnSubscribe<Boolean>) emitter -> {
                            String currentSign = AppUtils.getAppSignatureMD5(activity, getPackageName());
                            String apkSign = AppUtils.getApkSignatureMD5(activity, getFilePath());
                            emitter.onNext(TextUtils.equals(currentSign, apkSign));
                            emitter.onComplete();
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(flag -> {
                            if (flag) {
                                installApk(activity);
                            } else {
                                String versionName = AppUtils.getAppVersionName(activity, getPackageName());
                                new AlertDialogFragment()
                                        .setTitle(R.string.text_title_has_different_signature)
                                        .setContent(activity.getString(R.string.text_content_has_different_signature, getAppName(), versionName))
                                        .setPositiveButton("卸载", fragment -> {
                                            AppUtils.uninstallApk(activity, getPackageName());
                                        })
                                        .setNeutralButton("强制安装", fragment -> installApk(activity))
                                        .setNeutralButtonColor(activity.getResources().getColor(R.color.colorPrimary))
                                        .setPositionButtonnColor(activity.getResources().getColor(R.color.light_red_1))
                                        .show(activity);
                            }
                        })
                        .subscribe();
            } else {
                installApk(activity);
            }
        });
    }

    private void installApk(Activity activity) {
        InstallMode mode;
        if (AppConfig.isRootInstall() && AppConfig.isAccessibilityInstall()) {
            mode = InstallMode.AUTO;
        } else if (AppConfig.isRootInstall()) {
            mode = InstallMode.ROOT;
        } else if (AppConfig.isAccessibilityInstall()) {
            mode = InstallMode.ACCESSIBILITY;
        } else {
            mode = InstallMode.NORMAL;
        }
        ApkInstaller.with(activity)
                .setInstallMode(mode)
                .setInstallerListener(new ApkInstaller.InstallerListener() {
                    @Override
                    public void onStart() {
                        ZToast.normal("开始安装" + appName + "应用！");
                    }

                    @Override
                    public void onComplete() {
                        String currentVersion = AppUtils.getAppVersionName(activity, getPackageName());
                        String apkVersion = AppUtils.getApkVersionName(activity, getFilePath());
                        if (TextUtils.equals(apkVersion, currentVersion)) {
                            File file = getFile();
                            if (AppConfig.isAutoDeleteAfterInstalled() && file != null) {
                                file.delete();
                            }
                            ZToast.success(appName + "应用安装成功！");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        ZToast.error("安装失败！" + throwable.getMessage());
                    }

                    @Override
                    public void onNeed2OpenService() {
                        ZToast.warning(R.string.text_enable_accessibility_installation_service);
                    }

                    @Override
                    public void onNeedInstallPermission() {
                        ZToast.warning(R.string.text_grant_installation_permissions);
                    }
                })
                .install(getFile());
    }

    public AppDownloadMission setAppIcon(String appIcon) {
        this.appIcon = appIcon;
        return this;
    }

    public AppDownloadMission setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public AppDownloadMission setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public AppDownloadMission setAppType(String appType) {
        this.appType = appType;
        return this;
    }

    public AppDownloadMission setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public AppDownloadMission setShareApp(boolean shareApp) {
        isShareApp = shareApp;
        return this;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppType() {
        return appType;
    }

    public String getAppName() {
        return appName;
    }

}
