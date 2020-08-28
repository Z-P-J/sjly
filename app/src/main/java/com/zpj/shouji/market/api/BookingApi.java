package com.zpj.shouji.market.api;

import com.felix.atoast.library.AToast;
import com.zpj.http.core.IHttp;
import com.zpj.http.core.ObservableTask;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.model.BookingAppInfo;

import java.lang.annotation.Documented;

public class BookingApi {

    public static void autoDownloadApi(BookingAppInfo appInfo) {
        if (!appInfo.isAutoDownload()) {
            HttpApi.get("http://tt.shouji.com.cn/appv3/app_game_auto_download.jsp?id=" + appInfo.getAppId())
                    .onSuccess(data -> {
                        if ("success".equals(data.selectFirst("result").text())) {
                            AToast.success("该应用上线后将在Wifi环境下自动下载");
                            appInfo.setAutoDownload(true);
                        } else {
                            AToast.error("开启自动下载失败！");
                            appInfo.setAutoDownload(false);
                        }
                    })
                    .onError(throwable -> {
                        AToast.error("开启自动下载失败！" + throwable.getMessage());
                        appInfo.setAutoDownload(false);
                    })
                    .subscribe();
        }
    }

    public static void cancelAutoDownloadApi(BookingAppInfo appInfo) {
        if (appInfo.isAutoDownload()) {
            HttpApi.get("http://tt.shouji.com.cn/appv3/app_game_auto_download_del.jsp?id=" + appInfo.getAppId())
                    .onSuccess(data -> {
                        if ("success".equals(data.selectFirst("result").text())) {
                            AToast.success("取消自动下载成功");
                            appInfo.setAutoDownload(false);
                        } else {
                            AToast.error("取消自动下载失败！");
                        }
                    })
                    .onError(throwable -> {
                        AToast.error("取消自动下载失败！" + throwable.getMessage());
                    })
                    .subscribe();
        }
    }

    public static void cancelBookingApi(BookingAppInfo appInfo, Runnable successRunnable) {
        if (!appInfo.isAutoDownload()) {
            HttpApi.get("http://tt.shouji.com.cn/appv3/app_game_yuyue_del.jsp?id=" + appInfo.getAppId())
                    .onSuccess(data -> {
                        if ("success".equals(data.selectFirst("result").text())) {
                            AToast.success("取消预约成功！");
                            successRunnable.run();
                        } else {
                            AToast.error("取消预约失败！");
                        }
                    })
                    .onError(throwable -> {
                        AToast.error("取消预约失败！" + throwable.getMessage());
                    })
                    .subscribe();
        }
    }

}
