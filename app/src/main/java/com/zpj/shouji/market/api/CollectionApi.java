package com.zpj.shouji.market.api;

import android.util.Log;

import com.bumptech.glide.Glide;
import com.zpj.http.ZHttp;
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
                        dataList.add(HttpKeyVal.create("apc" + i, ""));
                    }
                    dataList.add(HttpKeyVal.create("apcount", String.valueOf(list.size())));
                    for (int i = 0; i < list.size(); i++) {
                        InstalledAppInfo info = list.get(i);
                        File file = Glide.with(ContextUtils.getApplicationContext()).asFile().load(info).submit().get();
                        IHttp.KeyVal keyVal = HttpKeyVal.create("app" + i, "app" + i + ".png", new FileInputStream(file), listener);
                        dataList.add(keyVal);
                    }
                    emitter.onNext(dataList);
                    emitter.onComplete();
                })
                .onNext(new HttpObserver.OnNextListener<List<IHttp.KeyVal>, Document>() {
                    @Override
                    public HttpObserver<Document> onNext(List<IHttp.KeyVal> data) throws Exception {
                        return ZHttp.post(
                                String.format("/app/square_disscuss_post_xml_v6.jsp?versioncode=%s&jsessionid=%s",
                                        "199", UserManager.getInstance().getSessionId()))
                                .data("tagurl", "http://tt.shouji.com.cn/app/faxian.jsp?index=faxian")
                                .data("sn", UserManager.getInstance().getSn())
                                .data("phone", DeviceUtils.getModel())
                                .data("replyid", "0")
                                .data("gkbz", isPrivate ? "0" : "1")
                                .data("tag", tags)
                                .data("content", content)
                                .data("title", title)
                                .data(data)
                                .cookie(UserManager.getInstance().getCookie())
//                                .ignoreContentType(true)
                                .toXml();
                    }
                })
                .onSuccess(data -> {
                    Log.d("shareCollectionApi", "data=" + data);
                    String info = data.selectFirst("info").text();
                    if ("success".equals(data.selectFirst("result").text())) {
                        ZToast.success(info);
                        successRunnable.run();
                    } else {
                        ZToast.error(info);
                    }
                    EventBus.hideLoading(250);
                })
                .onError(throwable -> {
                    ZToast.error("分享失败！" + throwable.getMessage());
                    EventBus.hideLoading(250);
                })
                .subscribe();
    }

}
