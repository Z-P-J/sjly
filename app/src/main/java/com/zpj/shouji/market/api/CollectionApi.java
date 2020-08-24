package com.zpj.shouji.market.api;

import android.util.Log;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.http.core.HttpKeyVal;
import com.zpj.http.core.IHttp;
import com.zpj.http.core.ObservableTask;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.model.WallpaperTag;
import com.zpj.shouji.market.utils.Callback;
import com.zpj.utils.ContextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class CollectionApi {

    private CollectionApi() {

    }

    public static void shareCollectionApi(String title, String content, List<InstalledAppInfo> list, String tags, boolean isPrivate, Runnable successRunnable, IHttp.OnStreamWriteListener listener) {
        Log.d("shareCollectionApi", "title=" + title + "  content=" + content + " tag=" + tags);
        ShowLoadingEvent.post("分享应用集...");
        new ObservableTask<>(
                (ObservableOnSubscribe<List<Connection.KeyVal>>) emitter -> {
                    List<Connection.KeyVal> dataList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        InstalledAppInfo info = list.get(i);
                        dataList.add(HttpKeyVal.create("apn" + i, info.getName()));
                        dataList.add(HttpKeyVal.create("apa" + i, info.getPackageName()));
                        dataList.add(HttpKeyVal.create("apc" + i, ""));
                    }
                    dataList.add(HttpKeyVal.create("apcount", String.valueOf(list.size())));
                    for (int i = 0; i < list.size(); i++) {
                        InstalledAppInfo info = list.get(i);
                        File file = Glide.with(ContextUtils.getApplicationContext()).downloadOnly().load(info).submit().get();
                        Connection.KeyVal keyVal = HttpKeyVal.create("app" + i, "app" + i + ".png", new FileInputStream(file), listener);
                        dataList.add(keyVal);
                    }
                    emitter.onNext(dataList);
                    emitter.onComplete();
                })
                .onNext(new ObservableTask.OnNextListener<List<Connection.KeyVal>, Document>() {
                    @Override
                    public ObservableTask<Document> onNext(List<Connection.KeyVal> data) throws Exception {
                        return ZHttp.post(
                                String.format("http://tt.shouji.com.cn/app/square_disscuss_post_xml_v6.jsp?versioncode=%s&jsessionid=%s",
                                        "199", UserManager.getInstance().getSessionId()))
                                .data("tagurl", "http://tt.shouji.com.cn/app/faxian.jsp?index=faxian")
                                .data("sn", UserManager.getInstance().getSn())
                                .data("phone", "MI 5s")
                                .data("replyid", "0")
                                .data("gkbz", isPrivate ? "0" : "1")
                                .data("tag", tags)
                                .data("content", content)
                                .data("title", title)
                                .data(data)
                                .validateTLSCertificates(false)
                                .userAgent(HttpApi.USER_AGENT)
                                .onRedirect(redirectUrl -> {
                                    Log.d("connect", "onRedirect redirectUrl=" + redirectUrl);
                                    return true;
                                })
                                .cookie(UserManager.getInstance().getCookie())
                                .ignoreContentType(true)
                                .toXml();
                    }
                })
                .onSuccess(data -> {
                    Log.d("shareCollectionApi", "data=" + data);
                    String info = data.selectFirst("info").text();
                    if ("success".equals(data.selectFirst("result").text())) {
                        AToast.success(info);
                        successRunnable.run();
                    } else {
                        AToast.error(info);
                    }
                    HideLoadingEvent.postDelayed(250);
                })
                .onError(throwable -> {
                    AToast.error("分享失败！" + throwable.getMessage());
                    HideLoadingEvent.postDelayed(250);
                })
                .subscribe();
    }

}
