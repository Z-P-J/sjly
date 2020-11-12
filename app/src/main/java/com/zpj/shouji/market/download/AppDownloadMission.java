package com.zpj.shouji.market.download;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.felix.atoast.library.AToast;
import com.zpj.downloader.config.MissionConfig;
import com.zpj.downloader.constant.Error;
import com.zpj.downloader.core.DownloadMission;
import com.zpj.downloader.util.FileUtil;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.installer.InstallMode;
import com.zpj.installer.ZApkInstaller;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.utils.AppUtils;
import com.zpj.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

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

//    public static AppDownloadMission create(String appUrl, String appId, String appName, String packageName, String appType, MissionConfig config) {
//        AppDownloadMission mission = new AppDownloadMission();
//        mission.url = appUrl;
//        mission.originUrl = appUrl;
//        mission.packageName = packageName;
//        mission.appId = appId;
//        mission.appType = appType;
//        mission.appName = appName;
//        mission.name = appName + "_" + appId + ".apk";
//        mission.uuid = UUID.randomUUID().toString();
//        mission.createTime = System.currentTimeMillis();
//        mission.missionStatus = MissionStatus.INITING;
//        mission.missionConfig = config;
//        return mission;
//    }



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
                    .onSuccess(new IHttp.OnSuccessListener<Document>() {
                        @Override
                        public void onSuccess(Document data) throws Exception {
                            Log.d("AppDownloadMission", "data=" + data);
                            url = data.selectFirst("url").text();
                            if (url.endsWith(".zip") && name != null) {
                                name = name.replace(".apk", ".zip");
                            }
                            originUrl = url;
                            length = Long.parseLong(data.selectFirst("size").text());
                            AppDownloadMission.super.initMission();
                        }
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
        if (AppConfig.isAccessibilityInstall() || AppConfig.isRootInstall()) {
            Log.d("AppDownloadMission", "install---dir=" + Environment.getExternalStorageDirectory().getAbsolutePath());
//            AutoInstaller installer = new AutoInstaller.Builder(getContext())
//                    .setMode(AppConfig.isRootInstall() ? AutoInstaller.MODE.ROOT_ONLY : AutoInstaller.MODE.AUTO_ONLY)
//                    .setOnStateChangedListener(new AutoInstaller.OnStateChangedListener() {
//                        @Override
//                        public void onStart() {
//                            AToast.success("开始安装" + appName + "应用！");
//                        }
//
//                        @Override
//                        public void onComplete() {
//                            if (AppUtils.isInstalled(getContext(), getPackageName())) {
//                                if (AppConfig.isAutoDeleteAfterInstalled()) {
//                                    FileUtils.deleteFile(getFilePath());
//                                }
//                                AToast.success(appName + "应用安装成功！");
//                            }
//                        }
//
//                        @Override
//                        public void onNeed2OpenService() {
//                            AToast.normal(R.string.text_enable_accessibility_installation_service);
//                        }
//
//                        @Override
//                        public void needPermission() {
//                            AToast.warning(R.string.text_grant_installation_permissions);
//                        }
//                    })
//                    .build();
//            Log.d("AppDownloadMission", "installer=" + installer);
//            installer.install(getFile());




//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    installUseRoot(getFile());
//
//                }
//            }).start();


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
                            if (AppUtils.isInstalled(getContext(), getPackageName())) {
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

    private boolean installUseRoot(File file) {
        Log.d(TAG, "installUseRoot");
        if (file == null)
            throw new IllegalArgumentException("Please check apk file!");
        boolean result = false;
        Process process = null;
        OutputStream outputStream = null;
        BufferedReader errorStream = null;
        try {
            process = Runtime.getRuntime().exec("su");
            outputStream = process.getOutputStream();

//            String command = "pm install -r " + file.getAbsolutePath() + "\n";
            String command = "cat " + file.getAbsolutePath() + " | pm install -S "+ file.length() + "\n";
            Log.d(TAG, "command=" + command);
            outputStream.write(command.getBytes());
            outputStream.flush();
            outputStream.write("exit\n".getBytes());
            outputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder msg = new StringBuilder();
            String line;
            while ((line = errorStream.readLine()) != null) {
                msg.append(line);
            }
            Log.d(TAG, "install msg is " + msg);
            if (!msg.toString().toLowerCase().contains("failure")) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
            result = false;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                outputStream = null;
                errorStream = null;
                process.destroy();
            }
        }
        return result;
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
