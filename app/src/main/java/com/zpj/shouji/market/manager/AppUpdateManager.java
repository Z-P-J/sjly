package com.zpj.shouji.market.manager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.notification.ZNotify;
import com.zpj.rxlife.RxLife;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Actions;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.database.IgnoredUpdateManager;
import com.zpj.shouji.market.model.AppUpdateInfo;
import com.zpj.shouji.market.model.IgnoredUpdateInfo;
import com.zpj.shouji.market.ui.activity.MainActivity;
import com.zpj.utils.AppUtils;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.DeviceUtils;
import com.zpj.utils.NetUtils;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public final class AppUpdateManager {

    private static final String TAG = "AppUpdateManager";

    private static AppUpdateManager INSTANCE;
    //    private static final List<WeakReference<CheckUpdateListener>> LISTENERS = new ArrayList<>();
    private final List<CheckUpdateListener> LISTENERS = new ArrayList<>();
    private static final String CHECK_UPDATE_URL = "/appv3/checkAppVersionV14.jsp";

    private final CopyOnWriteArraySet<String> PACKAGE_SET = new CopyOnWriteArraySet<>();
    private final ConcurrentMap<String, String> INCLUDE_APP_MAP = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> APP_UPDATE_CONTENT_MAP = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<AppUpdateInfo> APP_UPDATE_INFO_LIST = new CopyOnWriteArrayList<>();

    //    private static final ConcurrentLinkedQueue<CheckUpdateRunnable> TASK_LIST = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<CheckUpdate> TASK_LIST = new ConcurrentLinkedQueue<>();

    private final AtomicBoolean checked = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger retryCount = new AtomicInteger(0);
    private Throwable throwable;

    private class CheckUpdate implements Runnable {
        private final Context context;
        private final String cookie;
        private final String sessionId;
        private final String packageId;
        private int retryCount = 3;

        CheckUpdate(Context context, String cookie, String sessionId, String packageId) {
            this.context = context;
            this.cookie = cookie;
            this.sessionId = sessionId;
            this.packageId = packageId;
        }

        @Override
        public void run() {
            synchronized (TASK_LIST) {
                if (!TASK_LIST.contains(this)) {
                    TASK_LIST.add(this);
                }
            }
            HttpApi.post(CHECK_UPDATE_URL)
                    .userAgent("Sjly(3.1)")
//                    .setCookie(cookie)
//                    .contentType("application/x-www-form-urlencoded")
                    .referer(CHECK_UPDATE_URL)
                    .data("setupid", "sjly3.1")
                    .data("skin", "0")
                    .data("sdk", String.valueOf(DeviceUtils.getBuildVersionSDK()))
                    .data("downmaxsize", "5")
                    .data("downnovlan", "0")
                    .data("silentinstall", "0")
                    .data("yundown", "1")
                    .data("yunuser", "14880389")
                    .data("marketversion", "3.1")
                    .data("net", NetUtils.isWiFi(ContextUtils.getApplicationContext()) ? "wifi" : "phone")
//                    .data("jsessionid", sessionId)
                    .data("phonesn", "86971802943762" + (Math.random() * 10))
                    .data("pname", "Xiaomi:capricorn")
                    .data("width", "1920")
                    .data("height", "1080")
                    .data("dpi", "480")
                    .data("version", DeviceUtils.getOSVersion())
                    .data("vpasspackage", "")
                    .data("packageid", packageId)
                    .toHtml()
                    .bindTag(TAG, false)
                    .onSuccess(doc -> {
                        Log.e("checkUpdate", "body=" + doc.toString());

                        Elements versionElements = doc.select("version");
                        for (Element versionElement : versionElements) {
                            String packageName = versionElement.select("vpackage").text();
                            APP_UPDATE_CONTENT_MAP.put(packageName, versionElement.select("vlog").text());
                            Log.e("checkUpdate", "versionElement=" + versionElement.text());
                        }

                        String updateInfos = doc.select("update").get(0).text();
                        Log.e("checkUpdate", "updateInfos=" + updateInfos);
                        String[] updateInfoArray = updateInfos.replaceAll("更新;", "更新,")
                                .split(",");
                        for (String updateInfo : updateInfoArray) {
//                            Log.e("checkUpdate", "updateInfo=" + updateInfo);
                            String packageName = updateInfo.substring(0, updateInfo.indexOf("|"));
                            PACKAGE_SET.add(packageName);
                            String[] infos = updateInfo.split("\\|");
                            String idStr = infos[1];
                            INCLUDE_APP_MAP.put(packageName, idStr);
                            if (!updateInfo.contains("||||||")) {
                                AppUpdateInfo appInfo = new AppUpdateInfo();
                                appInfo.setPackageName(infos[0]);
                                appInfo.setId(idStr.substring(7));
                                appInfo.setAppType(idStr.substring(0, 4));
                                appInfo.setDownloadUrl(infos[2]);
                                appInfo.setNewVersionName(infos[3]);
                                appInfo.setOldVersionName(AppUtils.getAppVersionName(context, appInfo.getPackageName()));
                                appInfo.setNewSize(infos[4]);
                                appInfo.setUpdateTime(infos[11]);
                                appInfo.setUpdateTimeInfo(infos[13]);
                                appInfo.setAppName(AppUtils.getAppName(context, appInfo.getPackageName()));
                                appInfo.setUpdateInfo(APP_UPDATE_CONTENT_MAP.get(appInfo.getPackageName()));
                                APP_UPDATE_INFO_LIST.add(appInfo);
//                                Log.e("checkUpdate", "updateInfo=" + appInfo);
                            }
                        }
                        onFinished(CheckUpdate.this);
                    })
                    .onError(throwable -> {
                        Log.e(TAG, "url=" + CHECK_UPDATE_URL, throwable);
                        if (retryCount > 0) {
                            Log.e(TAG, "timeout url=" + CHECK_UPDATE_URL);
                            retryCount--;
                            run();
                        }
                    })
                    .subscribe();
        }

    }

    private AppUpdateManager() {
    }

    public static AppUpdateManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AppUpdateManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppUpdateManager();
                }
            }
        }
        return INSTANCE;
    }

    public void onDestroy() {
        RxLife.removeByTag(TAG);
        INSTANCE = null;
    }

    public void checkUpdate(Context context) {
        retryCount.set(0);
        check(context);
    }

    private void check(Context context) {
        RxLife.removeByTag(TAG);
//        LISTENERS.clear();
        PACKAGE_SET.clear();
        INCLUDE_APP_MAP.clear();
        APP_UPDATE_CONTENT_MAP.clear();
        APP_UPDATE_INFO_LIST.clear();
        TASK_LIST.clear();
        throwable = null;
        checked.set(false);
        running.set(true);
        Observable.create(
                (ObservableOnSubscribe<CheckUpdate>) emitter -> {


                    StringBuilder packageid = new StringBuilder();
                    PackageManager manager = context.getPackageManager();
                    List<PackageInfo> packageInfoList = manager.getInstalledPackages(0);
                    String md5 = "";
                    int total = packageInfoList.size();
                    int count = 0;
                    for (PackageInfo packageInfo : packageInfoList) {
//                        Log.e("checkUpdate", "packagename=" + packageInfo.packageName);
//                        Log.e("checkUpdate", "appName=" + packageInfo.applicationInfo.loadLabel(manager).toString());
//                        Log.e("checkUpdate", "firstInstallTime=" + packageInfo.firstInstallTime);
//                        Log.e("checkUpdate", "lastUpdateTime=" + packageInfo.lastUpdateTime);
                        count++;
                        packageid.append(packageInfo.packageName)
                                .append("=").append(packageInfo.versionName)
                                .append("=").append(packageInfo.versionCode)
                                .append("=").append(packageInfo.applicationInfo.loadLabel(manager).toString())
                                .append("=").append(md5).append("=Yes")//.append(packageInfo.firstInstallTime)
                                .append(",");
                        if (count % 50 == 0 || count == total) {
                            if (total != count && total - count < 25) {
                                continue;
                            }
                            Log.e("checkUpdate", "packageid=" + packageid);
                            CheckUpdate checkUpdateRunnable = new CheckUpdate(context, "", "", packageid.toString());
                            emitter.onNext(checkUpdateRunnable);
                            packageid = new StringBuilder();
                        }
                    }
                    emitter.onComplete();
                })
                .compose(RxLife.bindTag(TAG))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CheckUpdate>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull CheckUpdate checkUpdate) {
                        synchronized (TASK_LIST) {
                            TASK_LIST.add(checkUpdate);
                        }
                        checkUpdate.run();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        AppUpdateManager.this.throwable = e;
                        AppUpdateManager.this.onError(e);
                        RxLife.removeByTag(TAG);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void notifyUpdate() {
        addCheckUpdateListener(new CheckUpdateListener() {
            @Override
            public void onCheckUpdateFinish(List<AppUpdateInfo> updateInfoList, List<IgnoredUpdateInfo> ignoredUpdateInfoList) {
                notifyUpdate(updateInfoList);
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    private void notifyUpdate(List<AppUpdateInfo> updateInfoList) {
        if (AppConfig.isShowUpdateNotification() && checked.get() && !running.get()) {
            StringBuilder content = new StringBuilder();
            for (int i = 0; i < updateInfoList.size(); i++) {
                AppUpdateInfo info = updateInfoList.get(i);
                content.append(info.getAppName());
                if (i > 10 || i == (updateInfoList.size() - 1)) {
                    break;
                }
                content.append("，");
            }
            Intent intent = new Intent(ContextUtils.getApplicationContext(), MainActivity.class);
            intent.putExtra(Actions.ACTION, Actions.ACTION_SHOW_UPDATE);
            PendingIntent pendingIntent = PendingIntent.getActivity(ContextUtils.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            ZNotify.with(ContextUtils.getApplicationContext())
                    .buildNotify()
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setBigIcon(R.mipmap.ic_launcher)
                    .setContentTitle(updateInfoList.size() + "个应用待更新")
                    .setContentText(content.toString())
                    .setContentIntent(pendingIntent)
                    .setId(hashCode())
                    .show();
        }
    }

    public void cancelNotifyUpdate() {
        if (!AppConfig.isShowUpdateNotification()) {
            ZNotify.cancel(hashCode());
        }
    }

    public boolean hasPackage(String packageName) {
        return PACKAGE_SET.contains(packageName);
    }

    public String getAppIdAndType(String packageName) {
        return INCLUDE_APP_MAP.get(packageName);
    }

    public boolean hasUpdate(String packageName) {
        return APP_UPDATE_CONTENT_MAP.containsKey(packageName);
    }

//    private boolean isLetterOrDigit(char c) {
//        return Character.isLetter(c) || Character.isDigit(c);
//    }

    private synchronized void onFinished(CheckUpdate checkUpdate) {
        synchronized (TASK_LIST) {
            TASK_LIST.remove(checkUpdate);
            Log.d(TAG, "onFinished TASK_LIST.size=" + TASK_LIST.size() + " TASK_LIST.isEmpty=" + TASK_LIST.isEmpty());
            if (TASK_LIST.size() == 0) {
                Log.d(TAG, "onFinished onCheckUpdateFinish TASK_LIST.isEmpty=" + TASK_LIST.isEmpty() + " TASK_LIST.size=" + TASK_LIST.size());
                Log.d(TAG, "onFinished-->load");
                load(new CheckUpdateListener() {
                    @Override
                    public void onCheckUpdateFinish(final List<AppUpdateInfo> updateInfoList, List<IgnoredUpdateInfo> ignoredUpdateInfoList) {
                        Log.d(TAG, "onFinished onCheckUpdateFinish update.size=" + updateInfoList.size()
                                + " ignore.size=" + ignoredUpdateInfoList.size());
                        synchronized (LISTENERS) {
                            checked.set(true);
                            running.set(false);
                            notifyUpdate(updateInfoList);
                            Log.d(TAG, "onFinished doOnComplete LISTENERS.size=" + LISTENERS.size());

                            for (CheckUpdateListener checkUpdateListener : LISTENERS) {
                                if (checkUpdateListener != null) {
                                    checkUpdateListener.onCheckUpdateFinish(updateInfoList, ignoredUpdateInfoList);
                                }
                            }
                            LISTENERS.clear();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

            }
        }

    }

    private void load(CheckUpdateListener listener) {
        Log.d(TAG, "load listener=" + listener);
        if (listener != null) {
            Observable.create(
                    emitter -> {
                        List<AppUpdateInfo> list = new ArrayList<>(APP_UPDATE_INFO_LIST);
                        List<IgnoredUpdateInfo> ignoredUpdateInfoList = IgnoredUpdateManager.getAllIgnoredUpdateApp();
                        Comparator<Object> comparator1 = Collator.getInstance(Locale.CHINA);
                        Collections.sort(list, (o1, o2) -> comparator1.compare(o1.getAppName(), o2.getAppName()));

                        if (!ignoredUpdateInfoList.isEmpty()) {
                            for (int i = list.size() - 1; i >= 0; i--) {
                                AppUpdateInfo info = list.get(i);
                                for (int j = ignoredUpdateInfoList.size() - 1; j >= 0; j--) {
                                    IgnoredUpdateInfo ignoredUpdateInfo = ignoredUpdateInfoList.get(j);
                                    if (TextUtils.equals(info.getPackageName(), ignoredUpdateInfo.getPackageName())) {
                                        ignoredUpdateInfo.setUpdateInfo(info);
                                        list.remove(i);
                                    }
//                                    else if (!AppUtils.isApkInstalled(ContextUtils.getApplicationContext(), ignoredUpdateInfo.getPackageName())) {
//                                        ignoredUpdateInfo.delete();
//                                        ignoredUpdateInfoList.remove(j);
//                                    }
                                }
//                                for (IgnoredUpdateInfo ignoredUpdateInfo : ignoredUpdateInfoList) {
//                                    if (TextUtils.equals(info.getPackageName(), ignoredUpdateInfo.getPackageName())) {
//                                        ignoredUpdateInfo.setUpdateInfo(info);
//                                        list.remove(i);
//                                    }
//                                }
                            }
                        }
                        Observable.empty()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnComplete(() -> {
                                    Log.d(TAG, "load onCheckUpdateFinish");
                                    listener.onCheckUpdateFinish(list, ignoredUpdateInfoList);
                                })
                                .subscribe();
                        emitter.onComplete();
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    }

    private void onError(Throwable e) {
        e.printStackTrace();
        synchronized (LISTENERS) {
            checked.set(false);
            running.set(false);
            for (CheckUpdateListener checkUpdateListener : LISTENERS) {
                if (checkUpdateListener != null) {
                    checkUpdateListener.onError(e);
                }
            }
        }
    }

    public void addCheckUpdateListener(CheckUpdateListener listener) {
        Log.d(TAG, "addCheckUpdateListener listener=" + listener);
        Log.d(TAG, "addCheckUpdateListener running=" + running.get());
        Log.d(TAG, "addCheckUpdateListener checked=" + checked.get());
        if (!running.get()) {
            synchronized (LISTENERS) {
                if (checked.get()) {
                    Log.d(TAG, "addCheckUpdateListener load");
                    Log.d(TAG, "addCheckUpdateListener-->load");
                    load(listener);
                } else {
                    Log.d(TAG, "addCheckUpdateListener add111");
                    LISTENERS.add(listener);
                    Log.d(TAG, "addCheckUpdateListener retryCount=" + retryCount.get());
                    if (retryCount.get() < 3) {
                        retryCount.addAndGet(1);
                        check(ContextUtils.getApplicationContext());
                    } else {
                        onError(throwable);
                    }
                }
            }
        } else {
            synchronized (LISTENERS) {
                if (running.get()) {
                    Log.d(TAG, "addCheckUpdateListener add222");
                    LISTENERS.add(listener);
                    return;
                }
            }
            addCheckUpdateListener(listener);
        }
    }

    public List<AppUpdateInfo> getUpdateAppList() {
        List<AppUpdateInfo> list = new ArrayList<>(APP_UPDATE_INFO_LIST);
//        Log.e("checkUpdate", "getUpdateAppList  size=" + list.size());
        return list;
    }

    public interface CheckUpdateListener {
        void onCheckUpdateFinish(List<AppUpdateInfo> updateInfoList, List<IgnoredUpdateInfo> ignoredUpdateInfoList);

        void onError(Throwable e);
    }

}
