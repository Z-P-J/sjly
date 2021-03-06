package com.zpj.shouji.market.api;

import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.zpj.http.ZHttp;
import com.zpj.http.core.HttpConfig;
import com.zpj.http.core.HttpKeyVal;
import com.zpj.http.core.HttpObserver;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.toast.ZToast;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.DeviceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableOnSubscribe;

public class CollectionApi {

    private CollectionApi() {

    }

    public static void shareCollectionApi(String title, String content, List<InstalledAppInfo> list, String tags, boolean isPrivate, Runnable successRunnable, IHttp.OnStreamWriteListener listener) {
        Log.d("shareCollectionApi", "title=" + title + "  content=" + content + " tag=" + tags);
        EventBus.showLoading("分享应用集...");
        new HttpObserver<>(
                (ObservableOnSubscribe<List<IHttp.KeyVal>>) emitter -> {
                    List<IHttp.KeyVal> dataList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        InstalledAppInfo info = list.get(i);
                        dataList.add(HttpKeyVal.create("apn" + i, info.getName()));
                        dataList.add(HttpKeyVal.create("apa" + i, info.getPackageName()));
                        dataList.add(HttpKeyVal.create("apc" + i, info.getName() + " " + info.getVersionName()));
                    }
                    dataList.add(HttpKeyVal.create("apcount", String.valueOf(list.size())));
                    for (int i = 0; i < list.size(); i++) {
                        InstalledAppInfo info = list.get(i);
                        Log.d("CollectionApi", "i=" + i + " installedAppInfo=" + info);
//                        Glide.with(ContextUtils.getApplicationContext()).downloadOnly().load(info).submit().get();
                        File file = Glide.with(ContextUtils.getApplicationContext()).downloadOnly().load(info).submit().get();
                        Log.d("CollectionApi", "i=" + i + " file=" + file);
                        IHttp.KeyVal keyVal = HttpKeyVal.create("app" + i, "app" + i + ".png", new FileInputStream(file), listener);
                        dataList.add(keyVal);
                    }
                    emitter.onNext(dataList);
                    emitter.onComplete();
                })
                .onNext(data -> {
                    HttpConfig config = ZHttp.post(
                            String.format("/appv3/square_disscuss_post_xml_v6.jsp?versioncode=%s&jsessionid=%s",
                                    "199", UserManager.getInstance().getSessionId()))
                            .data("tagurl", String.format("http://tt.shouji.com.cn/appv3/faxian.jsp?index=faxian&versioncode=%s&jsessionid=%s", "199", UserManager.getInstance().getSessionId()))
                            .data("sn", UserManager.getInstance().getSn())
                            .data("phone", DeviceUtils.getModel())
                            .data("replyid", "0")
                            .data("gkbz", isPrivate ? "0" : "1")
                            .data("content", content)
                            .data("title", title);
                    if (!TextUtils.isEmpty(tags)) {
                        config.data("tag", tags);
                    }
                    return config.data(data)
                            .cookie(UserManager.getInstance().getCookie())
                            .toXml();
                })
                .onSuccess(data -> {
                    Log.d("shareCollectionApi", "data=" + data);
                    String info = data.selectFirst("info").text();
                    if ("success".equals(data.selectFirst("result").text())) {
//                        ZToast.success(info);
//                        successRunnable.run();
                        EventBus.hideLoading(() -> {
                            ZToast.success(info);
                            if (successRunnable != null) {
                                successRunnable.run();
                            }
                        });
                    } else {
                        ZToast.error(info);
                        EventBus.hideLoading(250);
                    }
                })
                .onError(throwable -> {
                    ZToast.error("分享失败！" + throwable.getMessage());
                    EventBus.hideLoading(250);
                })
                .subscribe();
    }

}
