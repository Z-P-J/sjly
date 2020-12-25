package com.zpj.downloader;

import android.content.Context;

/**
 * 通知拦截器接口
 * @author Z-P-J
 */
public interface INotificationInterceptor {

    void onProgress(Context context, BaseMission<?> mission, float progress, boolean isPause);

    void onFinished(Context context, BaseMission<?> mission);

    void onError(Context context, BaseMission<?> mission, int errCode);

    void onCancel(Context context, BaseMission<?> mission);

    void onCancelAll(Context context);

}
