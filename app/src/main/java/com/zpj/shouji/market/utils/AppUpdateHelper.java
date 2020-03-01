package com.zpj.shouji.market.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.http.core.ObservableTask;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.model.AppUpdateInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

public final class AppUpdateHelper {

    private static final AppUpdateHelper INSTANCE = new AppUpdateHelper();
    private static final List<WeakReference<CheckUpdateListener>> LISTENERS = new ArrayList<>();
    private static final String CHECK_UPDATE_URL = "http://tt.shouji.com.cn/app/checkAppVersionV14.jsp";

    private static final CopyOnWriteArraySet<String> PACKAGE_SET = new CopyOnWriteArraySet<>();
    private static final ConcurrentMap<String, String> INCLUDE_APP_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, String> APP_UPDATE_CONTENT_MAP = new ConcurrentHashMap<>();
    private static final CopyOnWriteArrayList<AppUpdateInfo> APP_UPDATE_INFO_LIST = new CopyOnWriteArrayList<>();

    //    private static final ConcurrentLinkedQueue<CheckUpdateRunnable> TASK_LIST = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<CheckUpdate> TASK_LIST = new ConcurrentLinkedQueue<>();

    private final AtomicBoolean checked = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Throwable throwable;

    private class CheckUpdate implements Runnable {
        private final Context context;
        private final String cookie;
        private final String sessionId;
        private final String packageId;

        CheckUpdate(Context context, String cookie, String sessionId, String packageId) {
            this.context = context;
            this.cookie = cookie;
            this.sessionId = sessionId;
            this.packageId = packageId;
        }

        @Override
        public void run() {
            HttpApi.openConnection(CHECK_UPDATE_URL)
                    .userAgent("Sjly(2.9.9.9.3)")
                    .header("Cookie", cookie)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("referer", CHECK_UPDATE_URL)
                    .data("setupid", "sjly2.9.9.9.3")
                    .data("skin", "0")
                    .data("sdk", "26")
                    .data("downmaxsize", "5")
                    .data("downnovlan", "0")
                    .data("silentinstall", "0")
                    .data("yundown", "1")
                    .data("yunuser", "14880389")
                    .data("marketversion", "2.9.9.9.3")
                    .data("net", "phone")
                    .data("jsessionid", sessionId)
                    .data("phonesn", "86971802943762" + (Math.random() * 10))
                    .data("pname", "Xiaomi:capricorn")
                    .data("width", "1920")
                    .data("height", "1080")
                    .data("dpi", "480")
                    .data("version", "8.0.0")
                    .data("vpasspackage", "")
                    .data("packageid", packageId)
//                            .data("packageid", "com.maimemo.android.momo=3.6.31=475=墨墨背单词,cn.wps.moffice_eng=9.9.0.244459=163=WPS Office,com.xfx.surfvpn=1.6.1=47=Surf VPN,com.eg.android.AlipayGphone=10.1.65.6567=144=支付宝==yes=3249560371,cn.bingoogolapple.badgeview.demo=1.1.6=116=BGABadgeViewDemo==yes=644081428,com.qianxun.browser=65.0.3325.230=10=千寻浏览器==yes=1921022368,com.fooview.android.fooview=1.0.1.1=125=FV悬浮球==yes=1680024518,com.netease.cloudmusic=6.2.2=144=网易云音乐==yes=3955544393,cn.bingoogolapple.photopicker.demo=1.2.6=126=BGAPhotoPickerDemo==yes=644081428,com.baidu.netdisk=9.6.63=898=百度网盘==yes=3938900617,com.qianxun.downloader=1.0.15=115=千寻下载==yes=3283140403,")
                    .ignoreContentType(true)
                    .toHtml()
                    .onSuccess(doc -> {
                        Log.e("checkUpdate", doc.toString());

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
                                appInfo.setOldVersionName(AppUtil.getVersionName(context, appInfo.getPackageName()));
                                appInfo.setNewSize(infos[4]);
                                appInfo.setUpdateTime(infos[11]);
                                appInfo.setUpdateTimeInfo(infos[13]);
                                appInfo.setAppName(AppUtil.getAppName(context, appInfo.getPackageName()));
                                appInfo.setUpdateInfo(APP_UPDATE_CONTENT_MAP.get(appInfo.getPackageName()));
                                APP_UPDATE_INFO_LIST.add(appInfo);
//                                Log.e("checkUpdate", "updateInfo=" + appInfo);
                            }
                        }
                    })
                    .onComplete(() -> {
                        TASK_LIST.remove(CheckUpdate.this);
                        onFinished();
                    })
                    .subscribe();
        }

    }

    private AppUpdateHelper() {
    }

    public static AppUpdateHelper getInstance() {
        return INSTANCE;
    }

    public void checkUpdate(Context context) {
        LISTENERS.clear();
        PACKAGE_SET.clear();
        INCLUDE_APP_MAP.clear();
        APP_UPDATE_CONTENT_MAP.clear();
        APP_UPDATE_INFO_LIST.clear();
        TASK_LIST.clear();
        throwable = null;
        checked.set(false);
        running.set(true);
        HttpApi.openConnection("http://tt.shouji.com.cn/app/update.jsp")
                .userAgent("Sjly(2.9.9.9.3)")
                .execute()
                .flatMap((ObservableTask.OnFlatMapListener<Connection.Response, CheckUpdate>) (response, emitter) -> {
                    String setCookie = response.header("Set-Cookie");
//                    UserManager.setCookie(setCookie);
                    Log.e("checkUpdate", "setCookie=" + setCookie);
                    String jsessionId = setCookie.substring(setCookie.indexOf("="), setCookie.indexOf(";"));

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
                            CheckUpdate checkUpdateRunnable = new CheckUpdate(context, setCookie, jsessionId, packageid.toString());
                            emitter.onNext(checkUpdateRunnable);
                            packageid = new StringBuilder();
                        }
                    }
                })
                .onError(new IHttp.OnErrorListener() {
                    @Override
                    public void onError(Throwable throwable) {
                        AppUpdateHelper.this.throwable = throwable;
                        AppUpdateHelper.this.onError(throwable);
                    }
                })
                .onSuccess(checkUpdate -> {
                    TASK_LIST.add(checkUpdate);
                    checkUpdate.run();
                })
                .subscribe();

    }

    public boolean hasPackage(String packageName) {
        return PACKAGE_SET.contains(packageName);
    }

    public String getAppIdAndType(String packageName) {
        return INCLUDE_APP_MAP.get(packageName);
    }

    private synchronized void onFinished() {
        if (TASK_LIST.isEmpty()) {
            checked.set(true);
            running.set(false);
            List<AppUpdateInfo> list = new ArrayList<>(APP_UPDATE_INFO_LIST);
            for (WeakReference<CheckUpdateListener> checkUpdateListener : LISTENERS) {
                if (checkUpdateListener.get() != null) {
                    Log.e("checkUpdate", "size22222222222=" + list.size());
                    checkUpdateListener.get().onCheckUpdateFinish(list);
                }
            }
        }
    }

    private void onError(Throwable e) {
        checked.set(false);
        running.set(false);
        for (WeakReference<CheckUpdateListener> checkUpdateListener : LISTENERS) {
            if (checkUpdateListener.get() != null) {
                checkUpdateListener.get().onError(e);
            }
        }
    }

    public void addCheckUpdateListener(CheckUpdateListener listener) {
        LISTENERS.add(new WeakReference<>(listener));
        if (!running.get()) {
            if (checked.get()) {
                onFinished();
            } else {
                onError(throwable);
            }
        }
    }

    public List<AppUpdateInfo> getUpdateAppList() {
        List<AppUpdateInfo> list = new ArrayList<>(APP_UPDATE_INFO_LIST);
//        Log.e("checkUpdate", "getUpdateAppList  size=" + list.size());
        return list;
    }

    public interface CheckUpdateListener {
        void onCheckUpdateFinish(List<AppUpdateInfo> updateInfoList);

        void onError(Throwable e);
    }

}
