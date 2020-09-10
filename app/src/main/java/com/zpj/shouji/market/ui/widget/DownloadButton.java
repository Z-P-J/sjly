package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.zpj.downloader.config.MissionConfig;
import com.zpj.downloader.constant.Error;
import com.zpj.downloader.core.DownloadMission;
import com.zpj.shouji.market.manager.UserManager;

public class DownloadButton extends AppCompatTextView implements DownloadMission.MissionListener {

    private String appId;
    private String appType;
    private String appName;

    public DownloadButton(Context context) {
        this(context, null);
    }

    public DownloadButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(v -> {
            if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(appName) || TextUtils.isEmpty(appType)) {
                AToast.error("Please call the bindApp() method first!");
                return;
            }
            MissionConfig config = MissionConfig.with()
                    .setCookie(UserManager.getInstance().getCookie());

            DownloadMission mission = DownloadMission
                    .create(String.format("http://tt.shouji.com.cn/wap/down/%s?id=%s", appType, appId), appName, config);
            mission.start();
        });
    }

    public void bindApp(String appId, String appName, String appType) {
        this.appId = appId;
        this.appName = appName;
        this.appType = appType;
    }

    @Override
    public void onInit() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onWaiting() {

    }

    @Override
    public void onRetry() {

    }

    @Override
    public void onProgress(DownloadMission.UpdateInfo update) {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onError(Error e) {

    }

}
