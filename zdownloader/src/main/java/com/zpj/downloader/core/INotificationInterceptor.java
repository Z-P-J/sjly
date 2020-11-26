package com.zpj.downloader.core;

import android.content.Context;

public interface INotificationInterceptor {

    void onProgress(Context context, DownloadMission mission, float progress, boolean isPause);

    void onFinished(Context context, DownloadMission mission);

    void onError(Context context, DownloadMission mission, int errCode);

    void onCancel(Context context, DownloadMission mission);

    void onCancelAll(Context context);

}
