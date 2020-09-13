package com.zpj.shouji.market.download;

import android.text.TextUtils;
import android.util.Log;

import com.zpj.downloader.ZDownloader;
import com.zpj.downloader.config.MissionConfig;
import com.zpj.downloader.constant.Error;
import com.zpj.downloader.core.DownloadMission;
import com.zpj.downloader.util.FileUtil;
import com.zpj.http.ZHttp;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.AppConfig;

import java.util.UUID;

public class AppDownloadMission extends DownloadMission {

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
