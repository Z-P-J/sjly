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
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.InstalledAppInfo;
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

    public static ObservableTask<Document> backupListApi() {
        return HttpApi.get("http://tt.shouji.com.cn/appv3/user_app_beifendan_list.jsp");
    }

    public static String getBackupDetailApi(String id) {
        return "http://tt.shouji.com.cn/appv3/user_app_list.jsp?id=" + id;
    }

    public static void createBackup(String title, String content, List<InstalledAppInfo> list, IHttp.OnStreamWriteListener listener, Runnable successRunnable) {
        Log.d("createBackupFragment", "title=" + title + "  content=" + content);
        ShowLoadingEvent.post("创建云备份...");
        new ObservableTask<>(
                (ObservableOnSubscribe<List<Connection.KeyVal>>) emitter -> {
                    List<Connection.KeyVal> dataList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        InstalledAppInfo info = list.get(i);
                        dataList.add(HttpKeyVal.create("apn" + i, info.getName()));
                        dataList.add(HttpKeyVal.create("apa" + i, info.getPackageName()));
                        dataList.add(HttpKeyVal.create("apc" + i, ""));
                    }
                    for (int i = 0; i < list.size(); i++) {
                        InstalledAppInfo info = list.get(i);
                        File file = Glide.with(ContextUtils.getApplicationContext()).asFile().load(info).submit().get();
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
                                String.format("http://tt.shouji.com.cn/appv3/user_app_beifen_add.jsp?versioncode=%s&jsessionid=%s&bcount=%s",
                                        "199", UserManager.getInstance().getSessionId(), list.size()))
                                .data("phone", DeviceUtils.getModel())
                                .data("title", title)
                                .data("content", content)

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
                    Log.d("createBackupFragment", "data=" + data);
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
                    AToast.error("创建失败！" + throwable.getMessage());
                    HideLoadingEvent.postDelayed(250);
                })
                .subscribe();
    }

}
