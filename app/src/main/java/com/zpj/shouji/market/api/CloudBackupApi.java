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

public final class CloudBackupApi {

    private CloudBackupApi() {
        throw new IllegalAccessError();
    }

    public static HttpObserver<Document> backupListApi() {
        return HttpApi.getXml("http://tt.shouji.com.cn/appv3/user_app_beifendan_list.jsp");
    }

    public static String getBackupDetailApi(String id) {
        return "http://tt.shouji.com.cn/appv3/user_app_list.jsp?id=" + id;
    }

    public static void createBackup(String title, String content, List<InstalledAppInfo> list, IHttp.OnStreamWriteListener listener, Runnable successRunnable) {
        Log.d("createBackupFragment", "title=" + title + "  content=" + content);
        EventBus.showLoading("创建云备份...");
        new HttpObserver<>(
                (ObservableOnSubscribe<List<IHttp.KeyVal>>) emitter -> {
                    List<IHttp.KeyVal> dataList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        InstalledAppInfo info = list.get(i);
                        dataList.add(HttpKeyVal.create("apn" + i, info.getName()));
                        dataList.add(HttpKeyVal.create("apa" + i, info.getPackageName()));
                        dataList.add(HttpKeyVal.create("apc" + i, ""));
                    }
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
                                String.format("http://tt.shouji.com.cn/appv3/user_app_beifen_add.jsp?versioncode=%s&jsessionid=%s&bcount=%s",
                                        "199", UserManager.getInstance().getSessionId(), list.size()))
                                .data("phone", DeviceUtils.getModel())
                                .data("title", title)
                                .data("content", content)

                                .data(data)
                                .setCookie(UserManager.getInstance().getCookie())
//                                .ignoreContentType(true)
                                .toXml();
                    }
                })
                .onSuccess(data -> {
                    Log.d("createBackupFragment", "data=" + data);
                    String info = data.selectFirst("info").text();
                    if ("success".equals(data.selectFirst("result").text())) {
                        ZToast.success(info);
                        successRunnable.run();
                    } else {
                        ZToast.error(info);
                    }
                })
                .onError(throwable -> {
                    ZToast.error("创建失败！" + throwable.getMessage());
                })
                .onComplete(() -> EventBus.hideLoading(250))
                .subscribe();
    }

}
