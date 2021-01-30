package com.zpj.shouji.market.manager;

import android.os.Handler;
import android.os.Message;

import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.utils.AppUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AppBackupManager {

    public interface AppBackupListener {
        void onAppBackupSuccess(int totalCount, int finishedCount, InstalledAppInfo appInfo);
        void onAppBackupFailed(int totalCount, int finishedCount, InstalledAppInfo appInfo);
    }

    private static AppBackupManager appBackupManager = null;

    private final List<WeakReference<AppBackupListener>> listeners = new ArrayList<>();

    private final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            3, 3,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>()
    );

    private final MyHandler handler = new MyHandler();

    private int totalCount = 0;
    private final AtomicInteger finishedCount = new AtomicInteger(0);

    private AppBackupManager() { }

    public synchronized static AppBackupManager getInstance() {
        if (appBackupManager == null) {
            synchronized (AppBackupManager.class) {
                appBackupManager = new AppBackupManager();
            }
        }
        return appBackupManager;
    }

    public void onDestroy() {
        appBackupManager = null;
    }

    public AppBackupManager addAppBackupListener(AppBackupListener listener) {
        listeners.add(new WeakReference<>(listener));
        return this;
    }

    public void removeAppBackupListener(AppBackupListener listener) {
        for (WeakReference<AppBackupListener> appBackupListener : listeners) {
            if (appBackupListener.get() != null && appBackupListener.get() == listener) {
                listeners.remove(appBackupListener);
                break;
            }
        }
    }

    public void startBackup(List<InstalledAppInfo> appInfoList, List<Integer> selectSet) {
        totalCount = selectSet.size();
        finishedCount.set(0);
        for (int position : selectSet) {
            InstalledAppInfo appInfo = appInfoList.get(position);
            EXECUTOR_SERVICE.submit(() -> {
                Message msg = new Message();
                msg.obj = appInfo;
                try {
                    AppUtil.backupApp(appInfo);
                    msg.what = 1;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            });
        }
    }

    public void startBackup(List<InstalledAppInfo> selectedAppList) {
        totalCount = selectedAppList.size();
        finishedCount.set(0);
        for (InstalledAppInfo appInfo : selectedAppList) {
            EXECUTOR_SERVICE.submit(() -> {
                Message msg = new Message();
                msg.obj = appInfo;
                try {
                    AppUtil.backupApp(appInfo);
                    msg.what = 1;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            });
        }
    }

    private void onSuccess(int finishedCount, InstalledAppInfo appInfo) {
        for (WeakReference<AppBackupListener> listener : listeners) {
            if (listener.get() != null) {
                listener.get().onAppBackupSuccess(totalCount, finishedCount, appInfo);
            }
        }
    }

    private void onFailed(int finishedCount, InstalledAppInfo appInfo) {
        for (WeakReference<AppBackupListener> listener : listeners) {
            if (listener.get() != null) {
                listener.get().onAppBackupFailed(totalCount, finishedCount, appInfo);
            }
        }
    }

    private void handleMessage(Message msg) {
        InstalledAppInfo appInfo = (InstalledAppInfo) msg.obj;
        if (msg.what == 1) {
            onSuccess(finishedCount.incrementAndGet(), appInfo);
        } else if (msg.what == -1) {
            onFailed(finishedCount.incrementAndGet(), appInfo);
        }
    }

    private static class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (appBackupManager != null) {
                appBackupManager.handleMessage(msg);
            }
        }

    };

}
