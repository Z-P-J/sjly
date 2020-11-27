package com.zpj.shouji.market.download;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.felix.atoast.library.AToast;
import com.zpj.downloader.config.MissionConfig;
import com.zpj.downloader.constant.Error;
import com.zpj.downloader.core.DownloadMission;
import com.zpj.downloader.util.FileUtil;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.installer.InstallMode;
import com.zpj.installer.ZApkInstaller;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.utils.AppUtils;
import com.zpj.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AppDownloadMission extends DownloadMission {

    private static final String TAG = AppDownloadMission.class.getSimpleName();

    private String appIcon;
    private String packageName;
    private String appId;
    private String appType;
    private String appName;
    private boolean isShareApp;

    public static AppDownloadMission create(String appId, String appName, String packageName, String appType, MissionConfig config) {
        return create(appId, appName, packageName, appType, config, false);
    }

    public static AppDownloadMission create(String appId, String appName, String packageName, String appType, MissionConfig config, boolean isShareApp) {
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
        mission.missionConfig = config;
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
            HttpApi.get(downloadUrl)
                    .onSuccess(data -> {
                        Log.d("AppDownloadMission", "data=" + data);
                        url = data.selectFirst("url").text();
                        if (url.endsWith(".zip") && name != null) {
                            name = name.replace(".apk", ".zip");
                        }
                        originUrl = url;
                        length = Long.parseLong(data.selectFirst("size").text());
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
        if (FileUtil.checkFileType(name) == FileUtil.FILE_TYPE.ARCHIVE) {
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
                        String currentSign = AppUtil.getAppSignatureMD5(getContext(), getPackageName());
                        String apkSign = AppUtil.getApkSignatureMD5(getContext(), getFilePath());
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
                                        AppUtil.uninstallApp(getContext(), getPackageName());
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
                            AToast.success("开始安装" + appName + "应用！");
                        }

                        @Override
                        public void onComplete() {
                            String currentVersion = AppUtils.getAppVersionName(getContext(), getPackageName());
                            String apkVersion = AppUtil.getApkVersionName(getContext(), getFilePath());
                            if (TextUtils.equals(apkVersion, currentVersion)) {
                                if (AppConfig.isAutoDeleteAfterInstalled()) {
                                    FileUtils.deleteFile(getFilePath());
                                }
                                AToast.success(appName + "应用安装成功！");
                            }
                        }

                        @Override
                        public void onNeed2OpenService() {
                            AToast.normal(R.string.text_enable_accessibility_installation_service);
                        }

                        @Override
                        public void onNeedInstallPermission() {
                            AToast.warning(R.string.text_grant_installation_permissions);
                        }
                    })
                    .install(getFile());

            Log.d("AppDownloadMission", "Thread");

        } else {
            openFile();
        }
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
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
