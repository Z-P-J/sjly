package com.zpj.shouji.market.download;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.zpj.downloader.core.DownloadMission;
import com.zpj.downloader.core.INotificationInterceptor;
import com.zpj.notification.ZNotify;
import com.zpj.shouji.market.constant.Actions;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.ui.activity.MainActivity;
import com.zpj.utils.ContextUtils;

public class DownloadNotificationInterceptor implements INotificationInterceptor {

    @Override
    public void onProgress(Context context, DownloadMission mission, float progress, boolean isPause) {
        Intent intent = new Intent(ContextUtils.getApplicationContext(), MainActivity.class);
        intent.putExtra(Actions.ACTION, Actions.ACTION_SHOW_DOWNLOAD);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ZNotify.with(context)
                .buildProgressNotify()
                .setProgressAndFormat(progress, false, "")
                .setContentTitle((isPause ? "已暂停：" : "") + mission.getTaskName())
                .setContentIntent(pendingIntent)
                .setId(mission.getNotifyId())
                .show();
    }

    @Override
    public void onFinished(Context context, DownloadMission mission) {
        PendingIntent pi;
        if (AppConfig.isInstallAfterDownloaded()) {
            Intent intent = new Intent(ContextUtils.getApplicationContext(), MainActivity.class);
            intent.putExtra(Actions.ACTION, Actions.ACTION_SHOW_DOWNLOAD);
            pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(mission.getFile()), "application/vnd.android.package-archive");
            pi = PendingIntent.getActivity(context, 0, intent, 0);
        }
        ZNotify.with(context)
                .buildNotify()
                .setContentTitle(mission.getTaskName())
                .setContentText("下载已完成")
                .setContentIntent(pi)
                .setId(mission.getNotifyId())
                .show();
    }

    @Override
    public void onError(Context context, DownloadMission mission, int errCode) {
        Intent intent = new Intent(ContextUtils.getApplicationContext(), MainActivity.class);
        intent.putExtra(Actions.ACTION, Actions.ACTION_SHOW_DOWNLOAD);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ZNotify.with(context)
                .buildNotify()
                .setContentTitle("下载出错" + errCode + ":" + mission.getTaskName())
                .setContentIntent(pendingIntent)
                .setId(mission.getNotifyId())
                .show();
    }

    @Override
    public void onCancel(Context context, DownloadMission mission) {
        ZNotify.cancel(mission.getNotifyId());
    }

    @Override
    public void onCancelAll(Context context) {
        ZNotify.cancelAll();
    }

}
