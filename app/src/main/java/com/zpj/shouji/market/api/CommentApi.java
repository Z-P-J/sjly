package com.zpj.shouji.market.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.zpj.toast.ZToast;
import com.nanchen.compresshelper.CompressHelper;
import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.http.core.HttpKeyVal;
import com.zpj.http.core.IHttp;
import com.zpj.http.core.ObservableTask;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.matisse.entity.Item;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.utils.FileUtils;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.utils.AppUtils;
import com.zpj.utils.CipherUtils;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.DeviceUtils;
import com.zpj.utils.OSUtils;
import com.zpj.utils.ScreenUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.ObservableOnSubscribe;

public class CommentApi {

    public static ObservableTask<Document> discussCommentApi(String replyId, String content) {
        return HttpApi.openConnection("http://tt.shouji.com.cn/app/square_disscuss_text_post_xml.jsp", Connection.Method.POST)
                .data("replyid", replyId)
                .data("phone", DeviceUtils.getModel())
                .data("content", content)
                .toHtml();
    }

    public static void discussCommentWithFileApi(Context context, String replyId, String content, List<Item> imgList, Runnable successRunnable, IHttp.OnStreamWriteListener listener) {
        ShowLoadingEvent.post("评论中...");
        new ObservableTask<>(
                (ObservableOnSubscribe<List<Connection.KeyVal>>) emitter -> {
                    List<Connection.KeyVal> dataList = new ArrayList<>();
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
                    public ObservableTask<Document> onNext(List<Connection.KeyVal> dataList) throws Exception {
                        return ZHttp.post(
                                String.format("http://tt.shouji.com.cn/app/square_disscuss_post_xml_v6.jsp?versioncode=%s&jsessionid=%s",
                                        "199", UserManager.getInstance().getSessionId()))
                                .data("sn", UserManager.getInstance().getSn())
                                .data("phone", DeviceUtils.getModel())
                                .data("replyid", replyId)
                                .data("content", content)
                                .data(dataList)
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
                    String info = data.selectFirst("info").text();
                    if ("success".equals(data.selectFirst("result").text())) {
                        ZToast.success(info);
                        successRunnable.run();
                    } else {
                        ZToast.error(info);
                    }
                    HideLoadingEvent.postDelayed(250);
                })
                .onError(throwable -> {
                    ZToast.error("评论失败！" + throwable.getMessage());
                    HideLoadingEvent.postDelayed(250);
                })
                .subscribe();
    }

//    public static ObservableTask<Document> appCommentApi(String content, String replyId, String appId, String appType, String appPackage) {
//        return HttpApi.openConnection("http://tt.shouji.com.cn/app/comment_xml_v5.jsp", Connection.Method.POST)
//                .data("replyid", replyId)
//                .data("phone", DeviceUtils.getModel())
//                .data("content", content)
//                .data("appid", appId)
//                .data("apptype", appType)
//                .data("package", appPackage)
//                .toHtml();
//    }

    public static void appCommentWithFileApi(Context context, String content, String replyId, String appId, String appType, String appPackage, List<Item> imgList, Runnable successRunnable, IHttp.OnStreamWriteListener listener) {
//        ShowLoadingEvent.post("评论中...");
//        ObservableTask<Document> task;
//        if (imgList == null || imgList.isEmpty()) {
//            task = HttpApi.openConnection("http://tt.shouji.com.cn/app/comment_xml_v5.jsp", Connection.Method.POST)
//                    .data("replyid", replyId)
//                    .data("phone", DeviceUtils.getModel())
//                    .data("content", content)
//                    .data("appid", appId)
//                    .data("apptype", appType)
//                    .data("package", appPackage)
//                    .toHtml();
//        } else {
//            task = new ObservableTask<>(
//                    (ObservableOnSubscribe<List<Connection.KeyVal>>) emitter -> {
//                        List<Connection.KeyVal> dataList = new ArrayList<>();
//                        for (int i = 0; i < imgList.size(); i++) {
//                            Item img = imgList.get(i);
//                            Connection.KeyVal keyVal = HttpKeyVal.create("image_" + i, "image_" + i + ".png", new FileInputStream(img.getFile(context)), listener);
//                            dataList.add(keyVal);
//                        }
//                        emitter.onNext(dataList);
//                        emitter.onComplete();
//                    })
//                    .onNext(new ObservableTask.OnNextListener<List<Connection.KeyVal>, Document>() {
//                        @Override
//                        public ObservableTask<Document> onNext(List<Connection.KeyVal> dataList) throws Exception {
//                            return ZHttp.post(
//                                    String.format("http://tt.shouji.com.cn/app/comment_xml_v5_file.jsp?versioncode=%s&jsessionid=%s",
//                                            "199", UserManager.getInstance().getSessionId()))
//                                    .data("sn", UserManager.getInstance().getSn())
//                                    .data("phone", DeviceUtils.getModel())
//                                    .data("replyid", replyId)
//                                    .data("apptype", appType)
//                                    .data("appid", appId)
//                                    .data("package", appPackage)
//                                    .data("content", content)
//
//                                    .data(dataList)
//                                    .validateTLSCertificates(false)
//                                    .userAgent(HttpApi.USER_AGENT)
//                                    .onRedirect(redirectUrl -> {
//                                        Log.d("connect", "onRedirect redirectUrl=" + redirectUrl);
//                                        return true;
//                                    })
//                                    .cookie(UserManager.getInstance().getCookie())
//                                    .ignoreContentType(true)
//                                    .toXml();
//                        }
//                    });
//        }
//        task
//                .onSuccess(data -> {
//                    String info = data.selectFirst("info").text();
//                    if ("success".equals(data.selectFirst("result").text())) {
//                        ZToast.success(info);
//                        successRunnable.run();
//                    } else {
//                        ZToast.error(info);
//                    }
//                    HideLoadingEvent.postDelayed(250);
//                })
//                .onError(throwable -> {
//                    ZToast.error("评论失败！" + throwable.getMessage());
//                    HideLoadingEvent.postDelayed(250);
//                })
//                .subscribe();

        appCommentWithFileApi(context, "评论中...", content, replyId, appId, appType, appPackage, imgList, successRunnable, listener);
    }

    public static void feedbackApi(Context context, String content, String replyId, String appId, String appType, String appPackage, List<Item> imgList, Runnable successRunnable, IHttp.OnStreamWriteListener listener) {
        appCommentWithFileApi(context, "反馈中...", content, replyId, appId, appType, appPackage, imgList, successRunnable, listener);
    }

    private static void appCommentWithFileApi(Context context, String msg, String content, String replyId, String appId, String appType, String appPackage, List<Item> imgList, Runnable successRunnable, IHttp.OnStreamWriteListener listener) {
        ShowLoadingEvent.post(msg);
        ObservableTask<Document> task;
        boolean compress = AppConfig.isCompressUploadImage();
        if (imgList == null || imgList.isEmpty()) {
            task = HttpApi.openConnection("http://tt.shouji.com.cn/app/comment_xml_v5.jsp", Connection.Method.POST)
                    .data("replyid", replyId)
                    .data("phone", DeviceUtils.getModel())
                    .data("content", content)
                    .data("appid", appId)
                    .data("apptype", appType)
                    .data("package", appPackage)
                    .toHtml();
        } else {
            task = new ObservableTask<>(
                    (ObservableOnSubscribe<List<Connection.KeyVal>>) emitter -> {
                        List<Connection.KeyVal> dataList = new ArrayList<>();
                        for (int i = 0; i < imgList.size(); i++) {
                            Item img = imgList.get(i);
                            File file = img.getFile(context);
                            if (compress && !file.getName().equalsIgnoreCase(".gif")) {
                                file = PictureUtil.compressImage(context, file);
                            }
                            Connection.KeyVal keyVal = HttpKeyVal.create("image_" + i, "image_" + i + ".png", new FileInputStream(file), listener);
                            dataList.add(keyVal);
                        }
                        emitter.onNext(dataList);
                        emitter.onComplete();
                    })
                    .onNext(new ObservableTask.OnNextListener<List<Connection.KeyVal>, Document>() {
                        @Override
                        public ObservableTask<Document> onNext(List<Connection.KeyVal> dataList) throws Exception {
                            return ZHttp.post(
                                    String.format("http://tt.shouji.com.cn/app/comment_xml_v5_file.jsp?versioncode=%s&jsessionid=%s",
                                            "199", UserManager.getInstance().getSessionId()))
                                    .data("sn", UserManager.getInstance().getSn())
                                    .data("phone", DeviceUtils.getModel())
                                    .data("replyid", replyId)
                                    .data("apptype", appType)
                                    .data("appid", appId)
                                    .data("package", appPackage)
                                    .data("content", content)

                                    .data(dataList)
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
                    });
        }
        task
                .onSuccess(data -> {
                    String info = data.selectFirst("info").text();
                    if ("success".equals(data.selectFirst("result").text())) {
                        ZToast.success(info);
                        successRunnable.run();
                    } else {
                        ZToast.error(info);
                    }
                    HideLoadingEvent.postDelayed(250);
                })
                .onError(throwable -> {
                    ZToast.error("评论失败！" + throwable.getMessage());
                    HideLoadingEvent.postDelayed(250);
                })
                .subscribe();
    }


    public static void discussCommentWithFileApi(Context context, String content, String replyId, InstalledAppInfo appInfo, List<Item> imgList, String tags, boolean isPrivate, Runnable successRunnable, IHttp.OnStreamWriteListener listener) {
        Log.d("publishThemeApi", "content=" + content + " tag=" + tags);
        ShowLoadingEvent.post("评论中...");
        ObservableTask<Document> task;
        if (appInfo == null && imgList.isEmpty()) {
            task = getConnection(
                    String.format("http://tt.shouji.com.cn/app/square_disscuss_text_post_xml.jsp?jsessionid=%s", UserManager.getInstance().getSessionId()),
                    replyId,
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
//                        dataList.add(HttpKeyVal.create("sn", UserManager.getInstance().getSn()));
//                        dataList.add(HttpKeyVal.create("phone", "MI 5s"));
//                        dataList.add(HttpKeyVal.create("replyid", replyId));
                        dataList.add(HttpKeyVal.create("content", content));
                        if (appInfo != null) {
                            File file = Glide.with(ContextUtils.getApplicationContext()).asFile().load(appInfo).submit().get();
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
                                    "199", UserManager.getInstance().getSessionId()), replyId, isPrivate, tags)
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
                        ZToast.success(info);
                        successRunnable.run();
                    } else {
                        ZToast.error(info);
                    }
                    HideLoadingEvent.postDelayed(250);
                })
                .onError(throwable -> {
                    ZToast.error("出错了！" + throwable.getMessage());
                    HideLoadingEvent.postDelayed(250);
                })
                .subscribe();
    }

    private static Connection getConnection(String url, String replyId, boolean isPrivate, String tags) {
        return ZHttp.post(url)
                .data("tagurl", "http://tt.shouji.com.cn/app/faxian.jsp?index=faxian")
                .data("sn", UserManager.getInstance().getSn())
                .data("phone", DeviceUtils.getModel())
                .data("replyid", replyId)
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
