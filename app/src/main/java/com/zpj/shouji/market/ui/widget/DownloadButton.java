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
import com.zpj.rxlife.RxLife;
import com.zpj.shouji.market.R;
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
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.toast.ZToast;
import com.zpj.utils.AppUtils;

import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DownloadButton extends AppCompatTextView
        implements AppDownloadMission.AppMissionListener, View.OnClickListener {

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
        RxLife.removeByTag(this);
        this.appId = appId;
        this.appName = appName;
        this.packageName = packageName;
        this.appType = appType;
        this.appIcon = appIcon;
        this.yunUrl = yunUrl;
        this.isShareApp = isShareApp;
        mission = null;

        setOnClickListener(this);

        ZDownloader.getAllMissions(AppDownloadMission.class, missions -> {
            for (AppDownloadMission mission : missions) {
                if (TextUtils.equals(appId, mission.getAppId()) && TextUtils.equals(packageName, mission.getPackageName())) {
                    DownloadButton.this.mission = mission;
                    post(new Runnable() {
                        @Override
                        public void run() {
                            onBindMission(mission);
                        }
                    });
                    break;
                }
            }
            if (mission == null) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        onDelete();
                    }
                });
            } else {
                DownloadButton.this.mission.addListener(DownloadButton.this);
            }
        });
//        init(appId, packageName);

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


    }

    protected void onBindMission(AppDownloadMission mission) {
        if (mission.isIniting()) {
            setText(R.string.text_preparing);
        } else if (mission.isRunning()) {
            if (mission.getProgress() < 10) {
                setText(mission.getProgressStr());
            } else {
                setText(String.format(Locale.US, "%.1f%%", mission.getProgress()));
            }
        } else if (mission.isFinished()) {
            if (mission.getFile().exists()) {
                if (mission.isUpgrade()) {
                    setText(R.string.text_upgrade);
                } else if (mission.isInstalled()) {
                    setText(R.string.text_open);
                } else {
                    setText(R.string.text_install);
                }
            } else {
                mission.delete();
//                onDelete();
            }
        } else if (mission.isWaiting()) {
            setText(R.string.text_waiting);
        } else {
            setText(R.string.text_continue);
        }
    }

//    private void init(final String appId, final String packageName) {
//        Observable.create(
//                (ObservableOnSubscribe<Integer>) emitter -> {
//                    ZDownloader.getAllMissions(AppDownloadMission.class, missions -> {
//                        for (AppDownloadMission mission : missions) {
//                            if (TextUtils.equals(appId, mission.getAppId()) && TextUtils.equals(packageName, mission.getPackageName())) {
//                                DownloadButton.this.mission = mission;
//                                if (mission.isIniting()) {
//                                    setText(R.string.text_preparing);
//                                } else if (mission.isRunning()) {
//                                    if (mission.getProgress() < 10) {
//                                        setText(mission.getProgressStr());
//                                    } else {
//                                        setText(String.format(Locale.US, "%.1f%%", mission.getProgress()));
//                                    }
//                                } else if (mission.isFinished()) {
//                                    if (mission.getFile().exists()) {
//                                        if (mission.isUpgrade()) {
//                                            setText(R.string.text_upgrade);
//                                        } else if (mission.isInstalled()) {
//                                            setText(R.string.text_open);
//                                        } else {
//                                            setText(R.string.text_install);
//                                        }
//                                    } else {
//                                        DownloadButton.this.mission.delete();
//                                        onDelete();
//                                    }
//                                } else if (mission.isWaiting()) {
//                                    setText(R.string.text_waiting);
//                                } else {
//                                    setText(R.string.text_continue);
//                                }
//                                break;
//                            }
//                        }
//                        if (mission == null) {
//                            onDelete();
//                        } else {
//                            DownloadButton.this.mission.addListener(DownloadButton.this);
//                        }
//                    });
//                    emitter.onComplete();
//                })
//                .subscribeOn(Schedulers.io())
//                .compose(RxLife.bindTag(this))
//                .subscribe();
//    }

    @Override
    public void onInit() {
        setText(R.string.text_preparing);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {
        setText(R.string.text_continue);
    }

    @Override
    public void onWaiting() {
        setText(R.string.text_waiting);
    }

    @Override
    public void onRetry() {
        setText(R.string.text_retrying);
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
        setText(R.string.text_install);
    }

    @Override
    public void onError(Error e) {
        setText(R.string.text_error);
    }

    @Override
    public void onDelete() {
        this.mission = null;
        if (TextUtils.isEmpty(defaultText)) {
            setText(R.string.text_download);
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
            ZDownloader.getAllMissions(AppDownloadMission.class, missions -> {
                for (AppDownloadMission mission : missions) {
                    if (TextUtils.equals(appId, mission.getAppId()) && TextUtils.equals(packageName, mission.getPackageName())) {
                        DownloadButton.this.mission = mission;
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
            if (mission.canPause()) {
                mission.pause();
            } else if (mission.canStart()){
                mission.start();
            } else if (mission.isFinished()) {
                if (mission.getFile().exists()) {
                    if (mission.isInstalled()) {
                        AppUtils.runApp(getContext(), mission.getPackageName());
                    } else {
                        mission.install();
                    }
                } else {
                    mission.restart();
                }
            }
        } else {

            mission = AppDownloadMission.create(appId, appName, packageName, appType, isShareApp);
            mission.setAppIcon(appIcon)
                    .addListener(this)
                    .start();
            ZToast.normal("开始下载" + appName);
        }
    }

    @Override
    public void onInstalled() {
        setText(R.string.text_open);
    }

    @Override
    public void onUninstalled() {
        setText(R.string.text_download);
    }
}
