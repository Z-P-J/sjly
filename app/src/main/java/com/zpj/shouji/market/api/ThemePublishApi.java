package com.zpj.shouji.market.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.http.core.HttpKeyVal;
import com.zpj.http.core.IHttp;
import com.zpj.http.core.ObservableTask;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.matisse.entity.Item;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.utils.AppUtils;
import com.zpj.utils.CipherUtils;
import com.zpj.utils.ContextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.ObservableOnSubscribe;

public class ThemePublishApi {

    private ThemePublishApi() {

    }

    public static void publishThemeApi(Context context, String content, InstalledAppInfo appInfo, List<Item> imgList, String tags, boolean isPrivate, Runnable successRunnable, IHttp.OnStreamWriteListener listener) {
        Log.d("publishThemeApi", "content=" + content + " tag=" + tags);
        ShowLoadingEvent.post("发布动态...");
//        IHttp.OnSuccessListener<Document> onSuccessListener = new IHttp.OnSuccessListener<Document>() {
//            @Override
//            public void onSuccess(Document data) throws Exception {
//                Log.d("publishThemeApi", "data=" + data);
//                String info = data.selectFirst("info").text();
//                if ("success".equals(data.selectFirst("result").text())) {
//                    AToast.success(info);
//                    successRunnable.run();
//                } else {
//                    AToast.error(info);
//                }
//                HideLoadingEvent.postDelayed(250);
//            }
//        };
//
//        IHttp.OnErrorListener onErrorListener = new IHttp.OnErrorListener() {
//            @Override
//            public void onError(Throwable throwable) {
//                AToast.error("发布失败！" + throwable.getMessage());
//                HideLoadingEvent.postDelayed(250);
//            }
//        };

        ObservableTask<Document> task;
        if (appInfo == null && imgList.isEmpty()) {
            task = getConnection(
                    String.format("http://tt.shouji.com.cn/app/square_disscuss_text_post_xml.jsp?jsessionid=%s", UserManager.getInstance().getSessionId()),
                    isPrivate,
                    tags)
                    .data("content", content)
                    .toXml();
        } else {
            task = new ObservableTask<>(
                    (ObservableOnSubscribe<List<Connection.KeyVal>>) emitter -> {
                        List<Connection.KeyVal> dataList = new ArrayList<>();
                        if (appInfo != null) {
                            dataList.add(HttpKeyVal.create("appname", appInfo.getName()));
                            dataList.add(HttpKeyVal.create("package", appInfo.getPackageName()));
                            dataList.add(HttpKeyVal.create("version", appInfo.getVersionName()));
                            dataList.add(HttpKeyVal.create("versioncode", String.valueOf(appInfo.getVersionCode())));
                            String[] appPermissions = AppUtils.getAppPermissions(context, appInfo.getPackageName());
                            Log.d("publishThemeApi", "appPermissions=" + Arrays.toString(appPermissions));
                            String permissions = "";
                            if (appPermissions != null) {
                                for (String permission : appPermissions) {
                                    if (!TextUtils.isEmpty(permissions)) {
                                        permissions += ';';
                                    }
                                    permissions += permission;
                                }
                            }
                            Log.d("publishThemeApi", "permissions=" + permissions);
                            dataList.add(HttpKeyVal.create("permission", permissions));
                            Log.d("publishThemeApi", "signature=" + AppUtils.getAppSign(context, appInfo.getPackageName()));
                            dataList.add(HttpKeyVal.create("signature", AppUtils.getAppSign(context, appInfo.getPackageName())));
                        }
                        dataList.add(HttpKeyVal.create("content", content));
                        if (appInfo != null) {
                            File file = Glide.with(ContextUtils.getApplicationContext()).downloadOnly().load(appInfo).submit().get();
                            dataList.add(HttpKeyVal.create("md5", CipherUtils.md5(new FileInputStream(file))));
                            Connection.KeyVal keyVal = HttpKeyVal.create("icon", "icon.png", new FileInputStream(file), listener);
                            dataList.add(keyVal);

                            dataList.add(
                                    HttpKeyVal.create(
                                            "apkfile",
                                            "webApk",
                                            new FileInputStream(new File(appInfo.getApkFilePath())),
                                            listener
                                    )
                            );
                        }
                        for (int i = 0; i < imgList.size(); i++) {
                            Item img = imgList.get(i);
                            Connection.KeyVal keyVal = HttpKeyVal.create("image_" + i, "image_" + i + ".png", new FileInputStream(img.getFile(context)), listener);
                            dataList.add(keyVal);
                        }
                        emitter.onNext(dataList);
                        emitter.onComplete();
                    })
                    .onNext(new ObservableTask.OnNextListener<List<Connection.KeyVal>, Document>() {
                        @Override
                        public ObservableTask<Document> onNext(List<Connection.KeyVal> data) throws Exception {
                            Log.d("publishThemeApi", "dataList=" + data);
                            return getConnection(String.format("http://tt.shouji.com.cn/app/square_disscuss_post_xml_v6.jsp?versioncode=%s&jsessionid=%s",
                                    "199", UserManager.getInstance().getSessionId()), isPrivate, tags)
                                    .data(data)
                                    .toXml();
                        }
                    });
        }

        task
                .onSuccess(data -> {
                    Log.d("publishThemeApi", "data=" + data);
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
                    AToast.error("发布失败！" + throwable.getMessage());
                    HideLoadingEvent.postDelayed(250);
                })
                .subscribe();

//        new ObservableTask<>(
//                (ObservableOnSubscribe<List<Connection.KeyVal>>) emitter -> {
//                    List<Connection.KeyVal> dataList = new ArrayList<>();
//                    if (appInfo != null) {
//                        dataList.add(HttpKeyVal.create("appname", appInfo.getName()));
//                        dataList.add(HttpKeyVal.create("package", appInfo.getPackageName()));
//                        dataList.add(HttpKeyVal.create("version", appInfo.getVersionName()));
//                        dataList.add(HttpKeyVal.create("versioncode", String.valueOf(appInfo.getVersionCode())));
//                        String[] appPermissions = AppUtils.getAppPermissions(context, appInfo.getPackageName());
//                        String permissions = "";
//                        if (appPermissions != null) {
//                            for (String permission : appPermissions) {
//                                if (!TextUtils.isEmpty(permissions)) {
//                                    permissions += ';';
//                                }
//                                permissions += permission;
//                            }
//                        }
//
//                        dataList.add(HttpKeyVal.create("permission", permissions));
//                        dataList.add(HttpKeyVal.create("signature", AppUtils.getAppSign(context, appInfo.getPackageName())));
//                    }
//                    dataList.add(HttpKeyVal.create("content", content));
//                    if (appInfo != null) {
//                        File file = Glide.with(ContextUtils.getApplicationContext()).downloadOnly().load(appInfo).submit().get();
//                        dataList.add(HttpKeyVal.create("md5", CipherUtils.md5(new FileInputStream(file))));
//                        Connection.KeyVal keyVal = HttpKeyVal.create("icon", "icon.png", new FileInputStream(file), listener);
//                        dataList.add(keyVal);
//
//                        dataList.add(
//                                HttpKeyVal.create(
//                                        "apkfile",
//                                        "webApk",
//                                        new FileInputStream(new File(appInfo.getApkFilePath())),
//                                        listener
//                                )
//                        );
//                    }
//                    for (int i = 0; i < imgList.size(); i++) {
//                        Item img = imgList.get(i);
//                        Connection.KeyVal keyVal = HttpKeyVal.create("image_" + i, "image_" + i + ".png", new FileInputStream(img.getFile(context)), listener);
//                        dataList.add(keyVal);
//                    }
//                    emitter.onNext(dataList);
//                    emitter.onComplete();
//                })
//                .onNext(new ObservableTask.OnNextListener<List<Connection.KeyVal>, Document>() {
//                    @Override
//                    public ObservableTask<Document> onNext(List<Connection.KeyVal> data) throws Exception {
//                        return ZHttp.post(
//                                String.format("http://tt.shouji.com.cn/app/square_disscuss_post_xml_v6.jsp?versioncode=%s&jsessionid=%s",
//                                        "199", UserManager.getInstance().getSessionId()))
//                                .data("tagurl", "http://tt.shouji.com.cn/app/faxian.jsp?index=faxian")
//                                .data("sn", UserManager.getInstance().getSn())
//                                .data("phone", "MI 5s")
//                                .data("replyid", "0")
//                                .data("gkbz", isPrivate ? "0" : "1")
//                                .data("tag", tags)
//                                .data(data)
//                                .validateTLSCertificates(false)
//                                .userAgent(HttpApi.USER_AGENT)
//                                .onRedirect(redirectUrl -> {
//                                    Log.d("connect", "onRedirect redirectUrl=" + redirectUrl);
//                                    return true;
//                                })
//                                .cookie(UserManager.getInstance().getCookie())
//                                .ignoreContentType(true)
//                                .toXml();
//                    }
//                })
//                .onSuccess(data -> {
//                    Log.d("publishThemeApi", "data=" + data);
//                    String info = data.selectFirst("info").text();
//                    if ("success".equals(data.selectFirst("result").text())) {
//                        AToast.success(info);
//                        successRunnable.run();
//                    } else {
//                        AToast.error(info);
//                    }
//                    HideLoadingEvent.postDelayed(250);
//                })
//                .onError(throwable -> {
//                    AToast.error("发布失败！" + throwable.getMessage());
//                    HideLoadingEvent.postDelayed(250);
//                })
//                .subscribe();
    }

    private static Connection getConnection(String url, boolean isPrivate, String tags) {
        return ZHttp.post(url)
                .data("tagurl", "http://tt.shouji.com.cn/app/faxian.jsp?index=faxian")
                .data("sn", UserManager.getInstance().getSn())
                .data("phone", "MI 5s")
                .data("replyid", "0")
                .data("gkbz", isPrivate ? "0" : "1")
                .data("tag", tags)
                .validateTLSCertificates(false)
                .userAgent(HttpApi.USER_AGENT)
                .onRedirect(redirectUrl -> {
                    Log.d("connect", "onRedirect redirectUrl=" + redirectUrl);
                    return true;
                })
                .cookie(UserManager.getInstance().getCookie())
                .ignoreContentType(true);
    }

}
