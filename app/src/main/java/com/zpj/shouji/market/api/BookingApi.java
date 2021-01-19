package com.zpj.shouji.market.api;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.BookingAppInfo;
import com.zpj.shouji.market.ui.fragment.login.LoginFragment;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.toast.ZToast;
import com.zpj.utils.Callback;

import java.util.ArrayList;
import java.util.List;

public class BookingApi {

    public static final String LATEST_BOOKING_URL = "/app/app_game_yuyue_list.jsp?sort=date&sdk=100";
    public static final String HOT_BOOKING_URL = "/app/app_game_yuyue_list.jsp?sort=date&sdk=100";

    private BookingApi() {

    }
    public static void latestBookingApi(Callback<List<BookingAppInfo>> callback) {
        HttpApi.getXml(LATEST_BOOKING_URL)
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
            ZToast.warning(R.string.text_msg_not_login);
            LoginFragment.start();
            return;
        }
        EventBus.showLoading("预约中...");
        HttpApi.getXml("/appv3/app_game_yuyue.jsp?id=" + appInfo.getAppId())
                .onSuccess(data -> {
                    String info = data.selectFirst("info").text();
                    if ("success".equals(data.selectFirst("result").text())) {
                        ZToast.success(info);
                        appInfo.setBooking(false);
                        successRunnable.run();
                    } else {
                        ZToast.error(info);
                        appInfo.setBooking(true);
                    }
                    EventBus.hideLoading(250);
                })
                .onError(throwable -> {
                    ZToast.error("预约失败！" + throwable.getMessage());
                    appInfo.setBooking(true);
                    EventBus.hideLoading(250);
                })
                .subscribe();
    }

    public static void cancelBookingApi(BookingAppInfo appInfo, Runnable successRunnable) {
        EventBus.showLoading("取消预约...");
        HttpApi.getXml("/appv3/app_game_yuyue_del.jsp?id=" + appInfo.getAppId())
                .onSuccess(data -> {
                    if ("success".equals(data.selectFirst("result").text())) {
                        ZToast.success("取消预约成功！");
                        appInfo.setBooking(true);
                        successRunnable.run();
                    } else {
                        appInfo.setBooking(false);
                        ZToast.error("取消预约失败！");
                    }
                    EventBus.hideLoading(250);
                })
                .onError(throwable -> {
                    ZToast.error("取消预约失败！" + throwable.getMessage());
                    EventBus.hideLoading(250);
                })
                .subscribe();
    }

    public static void autoDownloadApi(BookingAppInfo appInfo, Runnable successRunnable) {
        if (!appInfo.isAutoDownload()) {
            EventBus.showLoading("开启自动下载...");
            HttpApi.getXml("/appv3/app_game_auto_download.jsp?id=" + appInfo.getAppId())
                    .onSuccess(data -> {
                        if ("success".equals(data.selectFirst("result").text())) {
                            ZToast.success("该应用上线后将在Wifi环境下自动下载");
                            appInfo.setAutoDownload(true);
                            successRunnable.run();
                        } else {
                            ZToast.error("开启自动下载失败！");
                            appInfo.setAutoDownload(false);
                        }
                        EventBus.hideLoading(250);
                    })
                    .onError(throwable -> {
                        ZToast.error("开启自动下载失败！" + throwable.getMessage());
                        appInfo.setAutoDownload(false);
                        EventBus.hideLoading(250);
                    })
                    .subscribe();
        }
    }

    public static void cancelAutoDownloadApi(BookingAppInfo appInfo, Runnable successRunnable) {
        if (appInfo.isAutoDownload()) {
            EventBus.showLoading("开启自动下载...");
            HttpApi.getXml("/appv3/app_game_auto_download_del.jsp?id=" + appInfo.getAppId())
                    .onSuccess(data -> {
                        if ("success".equals(data.selectFirst("result").text())) {
                            ZToast.success("取消自动下载成功");
                            appInfo.setAutoDownload(false);
                            successRunnable.run();
                        } else {
                            ZToast.error("取消自动下载失败！");
                        }
                        EventBus.hideLoading(250);
                    })
                    .onError(throwable -> {
                        ZToast.error("取消自动下载失败！" + throwable.getMessage());
                        EventBus.hideLoading(250);
                    })
                    .subscribe();
        }
    }

}
