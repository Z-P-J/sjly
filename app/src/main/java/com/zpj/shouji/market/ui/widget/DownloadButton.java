package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zpj.downloader.DownloadMission;
import com.zpj.downloader.constant.Error;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.shouji.market.download.MissionBinder;

import java.util.Locale;

public class DownloadButton extends AppCompatTextView
        implements MissionBinder.AppDownloadListener, View.OnClickListener {

    private static final String TAG = "DownloadButton";

    private MissionBinder binder;

//    private String appUrl;
//    private String appId;
//    private String appIcon;
//    private String packageName;
//    private String appType;
//    private String appName;
//    private String yunUrl;
//    private boolean isShareApp;

    private CharSequence defaultText;

//    private AppDownloadMission mission;

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
        if (binder != null) {
            Log.d(TAG, "onAttachedToWindow");
            binder.bind(this);
        }
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (binder != null) {
            Log.d(TAG, "onDetachedFromWindow");
            binder.unbind();
        }
        super.onDetachedFromWindow();
    }

    public void bindApp(MissionBinder binder) {
        this.binder = binder;
        binder.bind(this);
        setOnClickListener(this);
    }

//    public void bindApp(AppUpdateInfo info) {
//        bindApp(info.getId(), info.getAppName(), info.getPackageName(), info.getAppType(), "", "");
//    }
//
//    public void bindApp(PickedGameInfo info) {
//        bindApp(info.getAppId(), info.getAppName(), info.getPackageName(), info.getAppType(), info.getAppIcon(), "");
//    }
//
//    public void bindApp(CollectionAppInfo info) {
//        bindApp(info.getId(), info.getTitle(), info.getPackageName(), info.getAppType(), info.getIcon(), "");
//    }
//
//    public void bindApp(QuickAppInfo info) {
//        bindApp(info.getAppId(), info.getAppTitle(), info.getAppPackage(), info.getAppType(), "", info.getYunUrl());
//    }
//
//    public void bindApp(GuessAppInfo info) {
//        bindApp(info.getAppId(), info.getAppTitle(), info.getAppPackage(), info.getAppType(), info.getAppIcon(), "");
//    }

    @Override
    public void onBindMission(AppDownloadMission mission) {
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
//            setText(update.getProgressStr());
            setText(String.format(Locale.US, "%.1f%%", update.getProgress()));
        } else {
//            setText(String.format(Locale.US, "%.1f%%", update.getProgress()));
            setText((int) update.getProgress() + "%");
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
        binder.onClick();
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
