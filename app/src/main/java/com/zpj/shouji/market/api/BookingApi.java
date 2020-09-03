package com.zpj.shouji.market.api;

import com.felix.atoast.library.AToast;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.BookingAppInfo;
import com.zpj.shouji.market.ui.fragment.login.LoginFragment;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.Callback;

import java.util.ArrayList;
import java.util.List;

public class BookingApi {

    public static final String LATEST_BOOKING_URL = "http://tt.shouji.com.cn/app/app_game_yuyue_list.jsp?sort=date&sdk=100";
    public static final String HOT_BOOKING_URL = "http://tt.shouji.com.cn/app/app_game_yuyue_list.jsp?sort=date&sdk=100";

    private BookingApi() {

    }
    public static void latestBookingApi(Callback<List<BookingAppInfo>> callback) {
        HttpApi.get(LATEST_BOOKING_URL)
                .onSuccess(doc -> {
                    List<BookingAppInfo> list = new ArrayList<>();
                    for (Element element : doc.select("item")) {
                        BookingAppInfo item = BeanUtils.createBean(element, BookingAppInfo.class);
                        if (item == null) {
                            continue;
                        }
                        list.add(item);
                    }
                    if (callback != null) {
                        callback.onCallback(list);
                    }
                })
                .onError(throwable -> {
                    if (callback != null) {
                        callback.onCallback(new ArrayList<>());
                    }
                })
                .subscribe();
    }


    public static void bookingApi(BookingAppInfo appInfo, Runnable successRunnable) {
        if (!UserManager.getInstance().isLogin()) {
            AToast.warning(R.string.text_msg_not_login);
            LoginFragment.start();
            return;
        }
        ShowLoadingEvent.post("预约中...");
        HttpApi.get("http://tt.shouji.com.cn/appv3/app_game_yuyue.jsp?id=" + appInfo.getAppId())
                .onSuccess(data -> {
                    String info = data.selectFirst("info").text();
                    if ("success".equals(data.selectFirst("result").text())) {
                        AToast.success(info);
                        appInfo.setBooking(false);
                        successRunnable.run();
                    } else {
                        AToast.error(info);
                        appInfo.setBooking(true);
                    }
                    HideLoadingEvent.postDelayed(250);
                })
                .onError(throwable -> {
                    AToast.error("预约失败！" + throwable.getMessage());
                    appInfo.setBooking(true);
                    HideLoadingEvent.postDelayed(250);
                })
                .subscribe();
    }

    public static void cancelBookingApi(BookingAppInfo appInfo, Runnable successRunnable) {
        ShowLoadingEvent.post("取消预约...");
        HttpApi.get("http://tt.shouji.com.cn/appv3/app_game_yuyue_del.jsp?id=" + appInfo.getAppId())
                .onSuccess(data -> {
                    if ("success".equals(data.selectFirst("result").text())) {
                        AToast.success("取消预约成功！");
                        appInfo.setBooking(true);
                        successRunnable.run();
                    } else {
                        appInfo.setBooking(false);
                        AToast.error("取消预约失败！");
                    }
                    HideLoadingEvent.postDelayed(250);
                })
                .onError(throwable -> {
                    AToast.error("取消预约失败！" + throwable.getMessage());
                    HideLoadingEvent.postDelayed(250);
                })
                .subscribe();
    }

    public static void autoDownloadApi(BookingAppInfo appInfo, Runnable successRunnable) {
        if (!appInfo.isAutoDownload()) {
            ShowLoadingEvent.post("开启自动下载...");
            HttpApi.get("http://tt.shouji.com.cn/appv3/app_game_auto_download.jsp?id=" + appInfo.getAppId())
                    .onSuccess(data -> {
                        if ("success".equals(data.selectFirst("result").text())) {
                            AToast.success("该应用上线后将在Wifi环境下自动下载");
                            appInfo.setAutoDownload(true);
                            successRunnable.run();
                        } else {
                            AToast.error("开启自动下载失败！");
                            appInfo.setAutoDownload(false);
                        }
                        HideLoadingEvent.postDelayed(250);
                    })
                    .onError(throwable -> {
                        AToast.error("开启自动下载失败！" + throwable.getMessage());
                        appInfo.setAutoDownload(false);
                        HideLoadingEvent.postDelayed(250);
                    })
                    .subscribe();
        }
    }

    public static void cancelAutoDownloadApi(BookingAppInfo appInfo, Runnable successRunnable) {
        if (appInfo.isAutoDownload()) {
            ShowLoadingEvent.post("开启自动下载...");
            HttpApi.get("http://tt.shouji.com.cn/appv3/app_game_auto_download_del.jsp?id=" + appInfo.getAppId())
                    .onSuccess(data -> {
                        if ("success".equals(data.selectFirst("result").text())) {
                            AToast.success("取消自动下载成功");
                            appInfo.setAutoDownload(false);
                            successRunnable.run();
                        } else {
                            AToast.error("取消自动下载失败！");
                        }
                        HideLoadingEvent.postDelayed(250);
                    })
                    .onError(throwable -> {
                        AToast.error("取消自动下载失败！" + throwable.getMessage());
                        HideLoadingEvent.postDelayed(250);
                    })
                    .subscribe();
        }
    }

}
