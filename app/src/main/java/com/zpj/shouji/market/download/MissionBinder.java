package com.zpj.shouji.market.download;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zpj.downloader.BaseMission;
import com.zpj.downloader.ZDownloader;
import com.zpj.downloader.constant.Error;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.widget.DownloadButton2;
import com.zpj.toast.ZToast;
import com.zpj.utils.AppUtils;

public abstract class MissionBinder implements AppDownloadMission.AppMissionListener {

    private AppDownloadMission mission;
    private AppDownloadMission.AppMissionListener listener;



    public interface AppDownloadListener extends AppDownloadMission.AppMissionListener {
        void onBindMission(AppDownloadMission mission);
    }

    @Override
    public void onInstalled() {
        if (listener != null) {
            listener.onInstalled();
        }
    }

    @Override
    public void onUninstalled() {
        if (listener != null) {
            listener.onUninstalled();
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
                    if (TextUtils.equals(getAppId(), mission.getAppId()) && TextUtils.equals(getPackageName(), mission.getPackageName())) {
                        MissionBinder.this.mission = mission;
                        break;
                    }
                }
                onClick();
            });
            return;
        }
        if (mission != null) {
            if (mission.canPause()) {
                mission.pause();
            } else if (mission.canStart()) {
                mission.start();
            } else if (mission.isFinished()) {
                if (mission.getFile().exists()) {
                    if (mission.isInstalled()) {
                        AppUtils.runApp(mission.getPackageName());
                    } else {
                        mission.install();
                    }
                } else {
                    mission.restart();
                }
            }
        } else {

            mission = AppDownloadMission.create(getAppId(), getAppName(), getPackageName(), getAppType(), isShareApp());
            mission.setAppIcon(getAppIcon())
                    .addListener(this)
                    .start();
            ZToast.normal("开始下载" + getAppName());
        }
    }

    public void bind(@NonNull AppDownloadListener listener) {
        this.listener = listener;
        if (mission == null) {
            ZDownloader.getAllMissions(AppDownloadMission.class, missions -> {
                for (AppDownloadMission mission : missions) {
                    if (TextUtils.equals(getAppId(), mission.getAppId()) && TextUtils.equals(getPackageName(), mission.getPackageName())) {
                        MissionBinder.this.mission = mission;
                        if (listener != null) {
                            listener.onBindMission(mission);
                        }
                        break;
                    }
                }
                if (mission == null) {
                    onDelete();
                } else {
                    mission.addListener(MissionBinder.this);
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
