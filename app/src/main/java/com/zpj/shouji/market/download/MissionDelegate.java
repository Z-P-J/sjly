package com.zpj.shouji.market.download;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zpj.downloader.BaseMission;
import com.zpj.downloader.ZDownloader;
import com.zpj.downloader.constant.Error;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.model.AppUpdateInfo;
import com.zpj.shouji.market.model.IgnoredUpdateInfo;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.toast.ZToast;
import com.zpj.utils.AppUtils;
import com.zpj.utils.ContextUtils;

import java.util.List;

public abstract class MissionDelegate implements AppDownloadMission.AppMissionListener {

    private AppDownloadMission mission;
    private AppDownloadMission.AppMissionListener listener;

    private boolean isInit = true;

    private boolean isInstalled = false;
    private boolean isUpgrade = false;



    public interface AppDownloadListener extends AppDownloadMission.AppMissionListener {
        void onBindMission(AppDownloadMission mission);
    }

    @Override
    public void onInstalled() {
        isInstalled = true;
        initUpgrade();
        if (listener != null) {
            listener.onInstalled();
        }
    }

    @Override
    public void onUninstalled() {
        isInstalled = false;
        isUpgrade = false;
        if (listener != null) {
            listener.onUninstalled();
        }
    }

    @Override
    public void onUpgrade() {
        if (listener != null) {
            listener.onUpgrade();
        }
    }

    @Override
    public void onInit() {
        if (listener != null) {
            listener.onInit();
        }
    }

    @Override
    public void onStart() {
        if (listener != null) {
            listener.onStart();
        }
    }

    @Override
    public void onPause() {
        if (listener != null) {
            listener.onPause();
        }
    }

    @Override
    public void onWaiting() {
        if (listener != null) {
            listener.onWaiting();
        }
    }

    @Override
    public void onRetry() {
        if (listener != null) {
            listener.onRetry();
        }
    }

    @Override
    public void onProgress(BaseMission.ProgressInfo update) {
        if (listener != null) {
            listener.onProgress(update);
        }
    }

    @Override
    public void onFinish() {
        if (listener != null) {
            listener.onFinish();
        }
    }

    @Override
    public void onError(Error e) {
        if (listener != null) {
            listener.onError(e);
        }
    }

    @Override
    public void onDelete() {
        if (listener != null) {
            listener.onDelete();
        }
    }

    @Override
    public void onClear() {
        if (listener != null) {
            listener.onClear();
        }
    }

    public abstract String getYunUrl();
    public abstract String getAppId();
    public abstract String getAppName();
    public abstract String getAppType();
    public abstract String getPackageName();
    public abstract String getAppIcon();
    public abstract boolean isShareApp();

    public void setInit(boolean init) {
        isInit = init;
    }

    public void setInstalled(boolean installed) {
        isInstalled = installed;
    }

    public void setUpgrade(boolean upgrade) {
        isUpgrade = upgrade;
    }

    public void onClick() {
        if (!TextUtils.isEmpty(getYunUrl())) {
            WebFragment.start(getYunUrl());
            return;
        }
        if (TextUtils.isEmpty(getAppId()) || TextUtils.isEmpty(getAppName()) || TextUtils.isEmpty(getAppType())) {
            ZToast.error("Please call the bindApp() method first!");
            return;
        }
        if (mission == null) {
            ZDownloader.getAllMissions(AppDownloadMission.class, missions -> {
                for (AppDownloadMission mission : missions) {
                    if (TextUtils.equals(getAppId(), mission.getAppId())
                            && TextUtils.equals(getPackageName(), mission.getPackageName())) {
                        MissionDelegate.this.mission = mission;
                        break;
                    }
                }
                action();
            });
            return;
        }
        action();
    }

    private void action() {

        if (mission != null) {
            if (mission.isFinished()) {
                if (mission.isInstalled()) {
                    AppUtils.runApp(mission.getPackageName());
                } else if (mission.getFile().exists()) {
                    mission.install();
                } else {
                    mission.restart();
                }
//                if (mission.getFile().exists()) {
//                    if (mission.isInstalled()) {
//                        AppUtils.runApp(mission.getPackageName());
//                    } else {
//                        mission.install();
//                    }
//                } else {
//                    mission.restart();
//                }
            } else if (mission.canPause()) {
                mission.pause();
            } else if (mission.canStart()) {
                mission.start();
            } else {
                mission.restart();
            }
        } else {

            if (isInstalled && !isUpgrade) {
                AppUtils.runApp(getPackageName());
                return;
            }

            mission = AppDownloadMission.create(getAppId(), getAppName(), getPackageName(), getAppType(), isShareApp());
            mission.setAppIcon(getAppIcon())
                    .addListener(this)
                    .start();
            ZToast.normal("开始下载" + getAppName());
        }
    }

    public void init() {
        if (isInit) {
            Context context = ContextUtils.getApplicationContext();
            isInit = false;
            isInstalled = AppUtils.isInstalled(context, getPackageName());
            if (isInstalled) {
                initUpgrade();
            }
        }
    }

    private void initUpgrade() {
        isUpgrade = false;
        AppUpdateManager.getInstance().addCheckUpdateListener(new AppUpdateManager.CheckUpdateListener() {
            @Override
            public void onCheckUpdateFinish(List<AppUpdateInfo> updateInfoList, List<IgnoredUpdateInfo> ignoredUpdateInfoList) {
                for (AppUpdateInfo info : updateInfoList) {
                    if (TextUtils.equals(info.getPackageName(), getPackageName())) {
                        isUpgrade = true;
                        onUpgrade();
                        break;
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    public void bind(@NonNull AppDownloadListener listener) {
        this.listener = listener;
        if (mission == null) {
            ZDownloader.getAllMissions(AppDownloadMission.class, missions -> {
                for (AppDownloadMission mission : missions) {
                    if (TextUtils.equals(getAppId(), mission.getAppId())
                            && TextUtils.equals(getPackageName(), mission.getPackageName())) {
                        MissionDelegate.this.mission = mission;
                        if (listener != null) {
                            listener.onBindMission(mission);
                        }
                        break;
                    }
                }
                if (mission == null) {
                    if (isUpgrade) {
                        onUpgrade();
                    } else if (isInstalled) {
//                        onInstalled();
                        if (listener != null) {
                            listener.onInstalled();
                        }
                    } else {
                        onDelete();
                    }

                } else {
                    mission.addListener(MissionDelegate.this);
                }
            });
        } else {
            mission.addListener(this);
            listener.onBindMission(mission);
        }
    }

    public void unbind() {
        this.listener = null;
        if (mission != null) {
            mission.removeListener(this);
        }
    }



}
