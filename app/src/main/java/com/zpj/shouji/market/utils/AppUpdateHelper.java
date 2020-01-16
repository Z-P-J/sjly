package com.zpj.shouji.market.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.bean.AppUpdateInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AppUpdateHelper {

    private static AppUpdateHelper appUpdateHelper = null;

    private static final String URL = "http://tt.shouji.com.cn/app/checkAppVersionV14.jsp";

    private static final Set<String> SET = new HashSet<>();
    private static final Map<String, String> MAP = new HashMap<>();
    private static final Map<String, String> APP_UPDATE_CONTENT_MAP = new HashMap<>();
    private static final List<AppUpdateInfo> APP_UPDATE_INFO_LIST = new ArrayList<>();

    private static boolean checked = false;
    private static boolean running = false;

    private static final List<WeakReference<CheckUpdateListener>> listeners = new ArrayList<>();

    private AppUpdateHelper() { }

    public synchronized static AppUpdateHelper getInstance() {
        if (appUpdateHelper == null) {
            appUpdateHelper = new AppUpdateHelper();
        }
        return appUpdateHelper;
    }

    public void checkUpdate(Context context) {
        checked = false;
        running = true;
        ExecutorHelper.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection.Response response = ZHttp.get("http://tt.shouji.com.cn/app/update.jsp")
                            .userAgent("Sjly(2.0..9.9)")
                            .execute();
                    String setCookie = response.header("Set-Cookie");
                    UserManager.setCookie(setCookie);
                    Log.d("checkUpdate", "setCookie=" + setCookie);
                    String jsessionId = setCookie.substring(setCookie.indexOf("="), setCookie.indexOf(";"));

                    StringBuilder packageid = new StringBuilder();
                    PackageManager manager = context.getPackageManager();
                    List<PackageInfo> packageInfoList = manager.getInstalledPackages(0);
                    for (PackageInfo packageInfo : packageInfoList) {
                        Log.d("checkUpdate", "packagename=" + packageInfo.packageName);
                        Log.d("checkUpdate", "appName=" + packageInfo.applicationInfo.loadLabel(manager).toString());
                        Log.d("checkUpdate", "firstInstallTime=" + packageInfo.firstInstallTime);
                        Log.d("checkUpdate", "lastUpdateTime=" + packageInfo.lastUpdateTime);
                        String md5 = "";
                        packageid.append(packageInfo.packageName)
                                .append("=").append(packageInfo.versionName)
                                .append("=").append(packageInfo.versionCode)
                                .append("=").append(packageInfo.applicationInfo.loadLabel(manager).toString())
                                .append("=").append(md5).append("=Yes")//.append(packageInfo.firstInstallTime)
                                .append(",");
                    }
                    Log.d("checkUpdate", "packageid=" + packageid);

                    Document doc = ZHttp.get(URL)
                            .userAgent("Sjly(2.0..9.9)")
                            .header("Cookie", setCookie)
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .header("referer", URL)
                            .data("setupid", "sjly2.0.9.9")
                            .data("skin", "0")
                            .data("sdk", "26")
                            .data("downmaxsize", "5")
                            .data("downnovlan", "0")
                            .data("silentinstall", "0")
                            .data("yundown", "1")
                            .data("yunuser", "14880389")
                            .data("marketversion", "2.0.9.9")
                            .data("net", "phone")
                            .data("jsessionid", jsessionId)
                            .data("phonesn", "86971802943762" + (Math.random() * 10))
                            .data("pname", "Xiaomi:capricorn")
                            .data("width", "1920")
                            .data("height", "1080")
                            .data("dpi", "480")
                            .data("version", "8.0.0")
                            .data("vpasspackage", "")
                            .data("packageid", packageid.toString())
//                            .data("packageid", "com.maimemo.android.momo=3.6.31=475=墨墨背单词,cn.wps.moffice_eng=9.9.0.244459=163=WPS Office,com.xfx.surfvpn=1.6.1=47=Surf VPN,com.eg.android.AlipayGphone=10.1.65.6567=144=支付宝==yes=3249560371,cn.bingoogolapple.badgeview.demo=1.1.6=116=BGABadgeViewDemo==yes=644081428,com.qianxun.browser=65.0.3325.230=10=千寻浏览器==yes=1921022368,com.fooview.android.fooview=1.0.1.1=125=FV悬浮球==yes=1680024518,com.netease.cloudmusic=6.2.2=144=网易云音乐==yes=3955544393,cn.bingoogolapple.photopicker.demo=1.2.6=126=BGAPhotoPickerDemo==yes=644081428,com.baidu.netdisk=9.6.63=898=百度网盘==yes=3938900617,com.qianxun.downloader=1.0.15=115=千寻下载==yes=3283140403,")
                            .ignoreContentType(true)
                            .toHtml();
                    Log.d("checkUpdate", doc.toString());

                    Elements versionElements = doc.select("version");
                    for (Element versionElement : versionElements) {
                        String packageName = versionElement.select("vpackage").text();
                        APP_UPDATE_CONTENT_MAP.put(packageName, versionElement.select("vlog").text());
                        Log.d("checkUpdate", "versionElement=" + versionElement.text());
                    }

                    String updateInfos = doc.select("update").get(0).text();
                    Log.d("checkUpdate", "updateInfos=" + updateInfos);
                    String[] updateInfoArray = updateInfos.replaceAll("更新;", "更新,")
//                            .replaceAll("||||||;", "||||||,")
                            .split(",");
                    for (String updateInfo : updateInfoArray) {
                        Log.d("checkUpdate", "updateInfo=" + updateInfo);
                        String packageName = updateInfo.substring(0, updateInfo.indexOf("|"));
                        SET.add(packageName);
                        String[] infos = updateInfo
//                                    .replaceAll("\\||\\||", "|")
//                                    .replaceAll("\\||", "|")
//                                    .replaceAll("|Old|", "|")
                                .split("\\|");
                        String idStr = infos[1];
                        MAP.put(packageName, idStr);
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
                            Log.d("checkUpdate", "updateInfo=" + appInfo);
                        }
                    }
                    Log.d("checkUpdate", "size111111111=" + APP_UPDATE_INFO_LIST.size());
                    checked = true;
                    running = false;
                    onFinished();
                } catch (Exception e) {
                    e.printStackTrace();
                    running = false;
                    onError(e);
                }
            }
        });
    }

    private void handleMessage(Message msg) {

    }

    public boolean hasPackage(String packageName) {
        return SET.contains(packageName);
    }

    public String getAppIdAndType(String packageName) {
        return MAP.get(packageName);
    }

    private void onFinished() {
        for (WeakReference<CheckUpdateListener> checkUpdateListener : listeners) {
            if (checkUpdateListener.get() != null) {
                Log.d("checkUpdate", "size22222222222=" + APP_UPDATE_INFO_LIST.size());
                checkUpdateListener.get().onCheckUpdateFinish(APP_UPDATE_INFO_LIST);
            }
        }
    }

    private void onError(Exception e) {
        for (WeakReference<CheckUpdateListener> checkUpdateListener : listeners) {
            if (checkUpdateListener.get() != null) {
                checkUpdateListener.get().onError(e);
            }
        }
    }

    public void addCheckUpdateListener(CheckUpdateListener listener) {
        listeners.add(new WeakReference<>(listener));
        if (!running) {
            if (checked) {
                onFinished();
            } else {
                onError(null);
            }
        }
    }

    public List<AppUpdateInfo> getUpdateAppList() {
        Log.d("checkUpdate", "getUpdateAppList  size=" + APP_UPDATE_INFO_LIST.size());
        return APP_UPDATE_INFO_LIST;
    }

    public interface CheckUpdateListener {
        void onCheckUpdateFinish(List<AppUpdateInfo> updateInfoList);
        void onError(Exception e);
    }

    private static final class MyHandler extends Handler {
        WeakReference<AppUpdateHelper> reference;

        MyHandler(AppUpdateHelper appUpdateHelper) {
            reference = new WeakReference<>(appUpdateHelper);
        }

        @Override
        public void handleMessage(Message msg) {
            reference.get().handleMessage(msg);
        }
    }

}
