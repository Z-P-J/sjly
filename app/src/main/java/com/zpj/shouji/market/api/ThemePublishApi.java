package com.zpj.shouji.market.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.zpj.http.ZHttp;
import com.zpj.http.core.HttpConfig;
import com.zpj.http.core.HttpKeyVal;
import com.zpj.http.core.HttpObserver;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.imagepicker.entity.Item;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.toast.ZToast;
import com.zpj.utils.AppUtils;
import com.zpj.utils.CipherUtils;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.DeviceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.ObservableOnSubscribe;

public class ThemePublishApi {

    private ThemePublishApi() {

    }

    public static void publishThemeApi(Context context, String content, String replyId, InstalledAppInfo appInfo, List<Item> imgList, String tags, boolean isPrivate, Runnable successRunnable, IHttp.OnStreamWriteListener listener) {
        Log.d("publishThemeApi", "content=" + content + " tag=" + tags);
        EventBus.showLoading("发布动态...");
        boolean compress = AppConfig.isCompressUploadImage();
        HttpObserver<Document> task;
        if (appInfo == null && imgList.isEmpty()) {
            task = getConnection(
                    String.format("/app/square_disscuss_text_post_xml.jsp?jsessionid=%s", UserManager.getInstance().getSessionId()),
                    replyId,
                    isPrivate,
                    tags)
                    .data("content", content)
                    .toXml();
        } else {
            task = new HttpObserver<>(
                    (ObservableOnSubscribe<List<IHttp.KeyVal>>) emitter -> {
                        List<IHttp.KeyVal> dataList = new ArrayList<>();
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
                            Log.d("publishThemeApi", "signature=" + AppUtils.getAppSignature(context, appInfo.getPackageName()));
                            dataList.add(HttpKeyVal.create("signature", AppUtils.getAppSignature(context, appInfo.getPackageName())));
                        }
                        dataList.add(HttpKeyVal.create("content", content));
                        if (appInfo != null) {
                            File file = Glide.with(ContextUtils.getApplicationContext()).asFile().load(appInfo).submit().get();
                            dataList.add(HttpKeyVal.create("md5", CipherUtils.md5(new FileInputStream(file))));
                            IHttp.KeyVal keyVal = HttpKeyVal.create("icon", "icon.png", new FileInputStream(file), listener);
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
                            File file = img.getFile(context);
                            if (compress && !file.getName().equalsIgnoreCase(".gif")) {
                                file = PictureUtil.compressImage(context, file);
                            }
                            IHttp.KeyVal keyVal = HttpKeyVal.create("image_" + i, "image_" + i + ".png", new FileInputStream(file), listener);
                            dataList.add(keyVal);
                        }
                        emitter.onNext(dataList);
                        emitter.onComplete();
                    })
                    .onNext(new HttpObserver.OnNextListener<List<IHttp.KeyVal>, Document>() {
                        @Override
                        public HttpObserver<Document> onNext(List<IHttp.KeyVal> data) throws Exception {
                            Log.d("publishThemeApi", "dataList=" + data);
                            return getConnection(String.format("/app/square_disscuss_post_xml_v6.jsp?versioncode=%s&jsessionid=%s",
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
                    ZToast.error("发布失败！" + throwable.getMessage());
                    EventBus.hideLoading(250);
                })
                .subscribe();
    }

    private static HttpConfig getConnection(String url, String replyId, boolean isPrivate, String tags) {
        return ZHttp.post(url)
                .data("tagurl", "http://tt.shouji.com.cn/app/faxian.jsp?index=faxian")
                .data("sn", UserManager.getInstance().getSn())
                .data("phone", DeviceUtils.getModel())
                .data("replyid", replyId)
                .data("gkbz", isPrivate ? "0" : "1")
                .data("tag", tags)
                .cookie(UserManager.getInstance().getCookie());
    }

}
