package com.zpj.shouji.market.download;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.zpj.downloader.BaseMission;
import com.zpj.downloader.constant.Error;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.http.core.IHttp;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.installer.InstallMode;
import com.zpj.shouji.market.installer.ZApkInstaller;
import com.zpj.toast.ZToast;
import com.zpj.utils.AppUtils;
import com.zpj.utils.FileUtils;

import java.io.File;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AppDownloadMission extends BaseMission<AppDownloadMission> {

    private static final String TAG = AppDownloadMission.class.getSimpleName();

    private String appIcon;
    private String packageName;
    private String appId;
    private String appType;
    private String appName;
    private boolean isShareApp;

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

    public void install() {

        if (AppUtils.isInstalled(getContext(), getPackageName()) && AppConfig.isCheckSignature()) {
            Observable.create(
                    (ObservableOnSubscribe<Boolean>) emitter -> {
                        String currentSign = AppUtils.getAppSignatureMD5(getContext(), getPackageName());
                        String apkSign = AppUtils.getApkSignatureMD5(getContext(), getFilePath());
                        emitter.onNext(TextUtils.equals(currentSign, apkSign));
                        emitter.onComplete();
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(flag -> {
                        if (flag) {
                            installApk();
                        } else {
                            String versionName = AppUtils.getAppVersionName(getContext(), getPackageName());
                            new AlertDialogFragment()
                                    .setTitle(R.string.text_title_has_different_signature)
                                    .setContent(getContext().getString(R.string.text_content_has_different_signature, getAppName(), versionName))
                                    .setPositiveButton("卸载", fragment -> {
                                        AppUtils.uninstallApk(getContext(), getPackageName());
                                    })
                                    .setNeutralButton("强制安装", fragment -> installApk())
                                    .setNeutralButtonColor(getContext().getResources().getColor(R.color.colorPrimary))
                                    .setPositionButtonnColor(getContext().getResources().getColor(R.color.light_red_1))
                                    .show(getContext());
                        }
                    })
                    .subscribe();
        } else {
            installApk();
        }
    }

    private void installApk() {
        if (AppConfig.isAccessibilityInstall() || AppConfig.isRootInstall()) {
            Log.d("AppDownloadMission", "install---dir=" + Environment.getExternalStorageDirectory().getAbsolutePath());
            InstallMode mode = InstallMode.AUTO;
            if (AppConfig.isRootInstall() && AppConfig.isAccessibilityInstall()) {
                mode = InstallMode.AUTO;
            } else if (AppConfig.isRootInstall()) {
                mode = InstallMode.ROOT;
            } else if (AppConfig.isAccessibilityInstall()) {
                mode = InstallMode.ACCESSIBILITY;
            }
            ZApkInstaller.with(getContext())
                    .setInstallMode(mode)
                    .setInstallerListener(new ZApkInstaller.InstallerListener() {
                        @Override
                        public void onStart() {
                            ZToast.success("开始安装" + appName + "应用！");
                        }

                        @Override
                        public void onComplete() {
                            String currentVersion = AppUtils.getAppVersionName(getContext(), getPackageName());
                            String apkVersion = AppUtils.getApkVersionName(getContext(), getFilePath());
                            if (TextUtils.equals(apkVersion, currentVersion)) {
                                File file = getFile();
                                if (AppConfig.isAutoDeleteAfterInstalled() && file != null) {
                                    file.delete();
                                }
                                ZToast.success(appName + "应用安装成功！");
                            }
                        }

                        @Override
                        public void onNeed2OpenService() {
                            ZToast.normal(R.string.text_enable_accessibility_installation_service);
                        }

                        @Override
                        public void onNeedInstallPermission() {
                            ZToast.warning(R.string.text_grant_installation_permissions);
                        }
                    })
                    .install(getFile());

            Log.d("AppDownloadMission", "Thread");

        } else {
            openFile();
        }
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
