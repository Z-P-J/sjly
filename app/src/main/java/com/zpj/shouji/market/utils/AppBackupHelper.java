package com.zpj.shouji.market.utils;

import android.os.Handler;
import android.os.Message;

import com.zpj.shouji.market.model.InstalledAppInfo;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AppBackupHelper {

    public interface AppBackupListener {
        void onAppBackupSuccess(int totalCount, int finishedCount, InstalledAppInfo appInfo);
        void onAppBackupFailed(int totalCount, int finishedCount, InstalledAppInfo appInfo);
    }

    private static AppBackupHelper appBackupHelper = null;

    private static final List<WeakReference<AppBackupListener>> listeners = new ArrayList<>();

    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            3, 3,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>()
    );

    private MyHandler handler = new MyHandler();

    private int totalCount = 0;
    private static final AtomicInteger finishedCount = new AtomicInteger(0);

    private AppBackupHelper() { }

    public synchronized static AppBackupHelper getInstance() {
        if (appBackupHelper == null) {
            appBackupHelper = new AppBackupHelper();
        }
        return appBackupHelper;
    }

    public AppBackupHelper addAppBackupListener(AppBackupListener listener) {
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
            if (appBackupHelper != null) {
                appBackupHelper.handleMessage(msg);
            }
        }

    };

}
