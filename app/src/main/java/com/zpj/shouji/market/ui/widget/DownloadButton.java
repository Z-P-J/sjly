package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zpj.downloader.DownloadManager;
import com.zpj.downloader.DownloadMission;
import com.zpj.downloader.ZDownloader;
import com.zpj.downloader.constant.Error;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.AppUpdateInfo;
import com.zpj.shouji.market.model.CollectionAppInfo;
import com.zpj.shouji.market.model.GuessAppInfo;
import com.zpj.shouji.market.model.PickedGameInfo;
import com.zpj.shouji.market.model.QuickAppInfo;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.toast.ZToast;

import java.util.List;
import java.util.Locale;

public class DownloadButton extends AppCompatTextView
        implements DownloadMission.MissionListener, View.OnClickListener {

    private static final String TAG = "DownloadButton";

    private String appUrl;
    private String appId;
    private String appIcon;
    private String packageName;
    private String appType;
    private String appName;
    private String yunUrl;
    private boolean isShareApp;

    private CharSequence defaultText;

    private AppDownloadMission mission;

    public DownloadButton(Context context) {
        this(context, null);
    }

    public DownloadButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defaultText = getText();
    }

    @Override
    protected void onAttachedToWindow() {
        if (mission != null) {
            Log.d(TAG, "onAttachedToWindow");
            mission.addListener(this);
        }
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mission != null) {
            Log.d(TAG, "onDetachedFromWindow");
            mission.removeListener(this);
        }
        super.onDetachedFromWindow();
    }

    public void onChildViewDetachedFromWindow() {
        Log.d(TAG, "onChildViewDetachedFromWindow");
        if (mission != null) {
            mission.removeListener(this);
            this.mission = null;
        }
    }

    public void bindApp(AppInfo info) {
        bindApp(info.getAppId(), info.getAppTitle(), info.getAppPackage(), info.getAppType(), info.getAppIcon(), "");
    }

    public void bindApp(AppUpdateInfo info) {
        bindApp(info.getId(), info.getAppName(), info.getPackageName(), info.getAppType(), "", "");
    }

    public void bindApp(PickedGameInfo info) {
        bindApp(info.getAppId(), info.getAppName(), info.getPackageName(), info.getAppType(), info.getAppIcon(), "");
    }

    public void bindApp(CollectionAppInfo info) {
        bindApp(info.getId(), info.getTitle(), info.getPackageName(), info.getAppType(), info.getIcon(), "");
    }

    public void bindApp(QuickAppInfo info) {
        bindApp(info.getAppId(), info.getAppTitle(), info.getAppPackage(), info.getAppType(), "", info.getYunUrl());
    }

    public void bindApp(GuessAppInfo info) {
        bindApp(info.getAppId(), info.getAppTitle(), info.getAppPackage(), info.getAppType(), info.getAppIcon(), "");
    }

    public void bindApp(String appId, String appName, String packageName, String appType, String appIcon, String yunUrl) {
        bindApp(appId, appName, packageName, appType, appIcon, yunUrl, false);
    }

    public void bindApp(String appId, String appName, String packageName, String appType, String appIcon, String yunUrl, boolean isShareApp) {
        this.appId = appId;
        this.appName = appName;
        this.packageName = packageName;
        this.appType = appType;
        this.appIcon = appIcon;
        this.yunUrl = yunUrl;
        this.isShareApp = isShareApp;
        mission = null;

        ZDownloader.getAllMissions(AppDownloadMission.class, new DownloadManager.OnLoadMissionListener<AppDownloadMission>() {
            @Override
            public void onLoaded(List<AppDownloadMission> missions) {
                for (AppDownloadMission mission : missions) {
                    if (TextUtils.equals(appId, mission.getAppId()) && TextUtils.equals(packageName, mission.getPackageName())) {
                        DownloadButton.this.mission = mission;
                        if (mission.isIniting()) {
                            setText("初始中");
                        } else if (mission.isRunning()) {
                            if (mission.getProgress() < 10) {
                                setText(mission.getProgressStr());
                            } else {
                                setText(String.format(Locale.US, "%.1f%%", mission.getProgress()));
                            }
                        } else if (mission.isFinished()) {
                            if (mission.getFile().exists()) {
                                setText("安装");
                            } else {
                                DownloadButton.this.mission.delete();
                                onDelete();
                            }
                        } else if (mission.isWaiting()) {
                            setText("等待中");
                        } else {
                            setText("继续");
                        }
                        break;
                    }
                }
                if (mission == null) {
//            setText("下载");
                    onDelete();
                } else {
                    DownloadButton.this.mission.addListener(DownloadButton.this);
                }
            }
        });

//        for (AppDownloadMission mission : ZDownloader.getAllMissions(AppDownloadMission.class)) {
//            if (TextUtils.equals(appId, mission.getAppId()) && TextUtils.equals(packageName, mission.getPackageName())) {
//                this.mission = mission;
//                if (mission.isIniting()) {
//                    setText("初始中");
//                } else if (mission.isRunning()) {
////                    setText(mission.getProgressStr());
//                    if (mission.getProgress() < 10) {
//                        setText(mission.getProgressStr());
//                    } else {
//                        setText(String.format(Locale.US, "%.1f%%", mission.getProgress()));
//                    }
//                } else if (mission.isFinished()) {
//                    if (mission.getFile().exists()) {
//                        setText("安装");
//                    } else {
//                        this.mission.delete();
//                        onDelete();
//                    }
//                } else if (mission.isWaiting()) {
//                    setText("等待中");
//                } else {
//                    setText("继续");
//                }
//                break;
//            }
//        }
//        if (mission == null) {
////            setText("下载");
//            onDelete();
//        } else {
//            this.mission.addListener(this);
//        }

        setOnClickListener(this);
    }

    @Override
    public void onInit() {
        setText("初始中");
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {
        setText("继续");
    }

    @Override
    public void onWaiting() {
        setText("等待中");
    }

    @Override
    public void onRetry() {
        setText("重试中");
    }

    @Override
    public void onProgress(DownloadMission.ProgressInfo update) {
        if (update.getProgress() < 10) {
            setText(update.getProgressStr());
        } else {
            setText(String.format(Locale.US, "%.1f%%", update.getProgress()));
        }
    }

    @Override
    public void onFinish() {
        setText("安装");
    }

    @Override
    public void onError(Error e) {
        setText("出错了");
    }

    @Override
    public void onDelete() {
        this.mission = null;
        if (TextUtils.isEmpty(defaultText)) {
            setText("下载");
        } else {
            setText(defaultText);
        }
    }

    @Override
    public void onClear() {
        onDelete();
    }

    @Override
    public void onClick(View v) {
        if (!TextUtils.isEmpty(yunUrl)) {
            WebFragment.start(yunUrl);
            return;
        }
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(appName) || TextUtils.isEmpty(appType)) {
            ZToast.error("Please call the bindApp() method first!");
            return;
        }
        if (mission == null) {
//            for (AppDownloadMission mission : ZDownloader.getAllMissions(AppDownloadMission.class)) {
//                if (TextUtils.equals(appId, mission.getAppId()) && TextUtils.equals(packageName, mission.getPackageName())) {
//                    this.mission = mission;
//                    break;
//                }
//            }
            ZDownloader.getAllMissions(AppDownloadMission.class, new DownloadManager.OnLoadMissionListener<AppDownloadMission>() {
                @Override
                public void onLoaded(List<AppDownloadMission> missions) {
                    for (AppDownloadMission mission : missions) {
                        if (TextUtils.equals(appId, mission.getAppId()) && TextUtils.equals(packageName, mission.getPackageName())) {
                            DownloadButton.this.mission = mission;
                            break;
                        }
                    }
                }
            });
        }
        if (mission != null) {
            if (mission.canPause()) {
                mission.pause();
            } else if (mission.canStart()){
                mission.start();
            } else if (mission.isFinished()) {
                if (mission.getFile().exists()) {
                    mission.install();
                } else {
                    mission.restart();
                }
            }
        } else {

            mission = AppDownloadMission.create(appId, appName, packageName, appType, isShareApp);
            mission.setAppIcon(appIcon)
                    .setCookie(UserManager.getInstance().getCookie())
                    .setUserAgent(HttpApi.USER_AGENT)
                    .addListener(this)
                    .start();
//            mission.setAppIcon(appIcon);
//            mission.addListener(this);
//            mission.start();
            ZToast.normal("开始下载" + appName);
        }
    }
}
