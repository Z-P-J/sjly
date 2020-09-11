package com.zpj.downloader.core;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.zpj.downloader.constant.Error;

public class DownloadService extends Service implements DownloadMission.MissionListener {

    private static final String TAG = DownloadService.class.getSimpleName();

    private DMBinder mBinder;
    private DownloadManager mManager;
    private Notification mNotification;
    private Handler mHandler;
    private long mLastTimeStamp = System.currentTimeMillis();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        mBinder = new DMBinder();
        if (mManager == null) {
            //下载地址
//			String path = Settings.DEFAULT_PATH;
//			File file = new File(path);
//			if (!file.exists()) {
//				file.mkdirs();
//			}
            mManager = DownloadManagerImpl.getInstance();
            Log.d(TAG, "mManager == null");
//			Log.d(TAG, "Download directory: " + path);
        }

        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
//		i.setClass(this, Main2Activity.class);
        mNotification = new Notification.Builder(this)
                .setContentIntent(PendingIntent.getActivity(this, 0, i, 0))
                .setContentTitle("下载中")
                .setContentText("内容")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .build();

        HandlerThread thread = new HandlerThread("ServiceMessenger");
        thread.start();

        mHandler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    int runningCount = 0;

                    for (int i = 0; i < mManager.getCount(); i++) {
                        if (mManager.getMission(i).isRunning()) {
                            runningCount++;
                        }
                    }

                    updateState(runningCount);
                }
            }
        };

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Starting");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "Destroying");
        mManager.pauseAllMissions();
//		for (int i = 0; i < mManager.getCount(); i++) {
//			mManager.pauseMission(i);
//		}

        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
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
        postUpdateMessage();
    }

    @Override
    public void onFinish() {
        postUpdateMessage();
    }

    @Override
    public void onError(Error errCode) {
        postUpdateMessage();
    }

    @Override
    public void onDelete() {

    }

    @Override
    public void onClear() {

    }

    private void postUpdateMessage() {
        mHandler.sendEmptyMessage(0);
    }

    private void updateState(int runningCount) {
        if (runningCount == 0) {
            stopForeground(true);
        } else {
//			startForeground(1000, null);
            startForeground(1000, mNotification);
        }
    }


    // Wrapper of DownloadManager
    public class DMBinder extends Binder {
        public DownloadManager getDownloadManager() {
            return mManager;
        }

        public void onMissionAdded(DownloadMission mission) {
            mission.addListener(DownloadService.this);
            mission.setErrCode(-1);
            postUpdateMessage();
        }

        public void onMissionRemoved(DownloadMission mission) {
            mission.removeListener(DownloadService.this);
            postUpdateMessage();
        }

    }

}
