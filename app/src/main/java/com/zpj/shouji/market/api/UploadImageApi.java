package com.zpj.shouji.market.api;

import android.net.Uri;
import android.util.Log;

import com.yalantis.ucrop.CropEvent;
import com.zpj.http.ZHttp;
import com.zpj.http.core.IHttp;
import com.zpj.http.core.ObservableTask;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.toast.ZToast;

import java.io.File;
import java.io.FileInputStream;

public class UploadImageApi {

    public static ObservableTask<Document> uploadAvatarApi(Uri uri, IHttp.OnStreamWriteListener listener) throws Exception {
        return ZHttp.post(String.format("http://tt.shouji.com.cn/app/user_upload_avatar.jsp?jsessionid=%s&versioncode=%s", UserManager.getInstance().getSessionId(), HttpApi.VERSION_CODE))
                .validateTLSCertificates(false)
                .userAgent(HttpApi.USER_AGENT)
                .onRedirect(redirectUrl -> {
                    Log.d("connect", "onRedirect redirectUrl=" + redirectUrl);
                    return true;
                })
                .cookie(UserManager.getInstance().getCookie())
                .ignoreContentType(true)
                .data("image", "image.png", new FileInputStream(uri.getPath()), listener)
                .header("Charset", "UTF-8")
                .toXml();
    }

    public static ObservableTask<Document> uploadAvatarApi(Uri uri) throws Exception {
        return uploadAvatarApi(uri, new IHttp.OnStreamWriteListener() {
            @Override
            public void onBytesWritten(int bytesWritten) {

            }

            @Override
            public boolean shouldContinue() {
                return true;
            }
        });
    }

    public static ObservableTask<Document> uploadBackgroundApi(Uri uri, IHttp.OnStreamWriteListener listener) throws Exception {
        return ZHttp.post(
                String.format(
                        "http://tt.shouji.com.cn/app/user_upload_background.jsp?action=save&sn=%s&jsessionid=%s&versioncode=%s",
                        UserManager.getInstance().getSn(),
                        UserManager.getInstance().getSessionId(),
                        HttpApi.VERSION_CODE))
                .validateTLSCertificates(false)
                .userAgent(HttpApi.USER_AGENT)
                .onRedirect(redirectUrl -> {
                    Log.d("connect", "onRedirect redirectUrl=" + redirectUrl);
                    return true;
                })
                .cookie(UserManager.getInstance().getCookie())
                .ignoreContentType(true)
                .data("image", "image.png", new FileInputStream(uri.getPath()), listener)
                .header("Charset", "UTF-8")
                .toXml();
    }

    public static ObservableTask<Document> uploadBackgroundApi(Uri uri) throws Exception {
        return uploadBackgroundApi(uri, new IHttp.OnStreamWriteListener() {
            @Override
            public void onBytesWritten(int bytesWritten) {

            }

            @Override
            public boolean shouldContinue() {
                return true;
            }
        });
    }

    public static void uploadCropImage(CropEvent event) {
        if (event.isAvatar()) {
            EventBus.showLoading("上传头像...");
            try {
                uploadAvatarApi(event.getUri())
                        .onSuccess(doc -> {
                            Log.d("uploadAvatarApi", "data=" + doc);
                            String info = doc.selectFirst("info").text();
                            if ("success".equals(doc.selectFirst("result").text())) {
//                                ZToast.success(info);

                                UserManager.getInstance().getMemberInfo().setMemberAvatar(info);
                                UserManager.getInstance().saveUserInfo();
                                PictureUtil.saveAvatar(event.getUri(), new IHttp.OnSuccessListener<File>() {
                                    @Override
                                    public void onSuccess(File data) throws Exception {
                                        EventBus.sendImageUploadEvent(event);
                                    }
                                });
                            } else {
                                ZToast.error(info);
                            }
                        })
                        .onError(throwable -> {
                            ZToast.error("上传头像失败！" + throwable.getMessage());
                        })
                        .onComplete(() -> EventBus.hideLoading(500))
                        .subscribe();
            } catch (Exception e) {
                e.printStackTrace();
                ZToast.error("上传头像失败！" + e.getMessage());
                EventBus.hideLoading(500);
            }
        } else {
            EventBus.showLoading("上传背景...");
            try {
                uploadBackgroundApi(event.getUri())
                        .onSuccess(doc -> {
                            Log.d("uploadBackgroundApi", "data=" + doc);
                            String info = doc.selectFirst("info").text();
                            if ("success".equals(doc.selectFirst("result").text())) {
                                UserManager.getInstance().getMemberInfo().setMemberBackGround(info);
                                UserManager.getInstance().saveUserInfo();
                                PictureUtil.saveBackground(event.getUri(), data -> EventBus.sendImageUploadEvent(event));
                            } else {
                                ZToast.error(info);
                            }
                        })
                        .onError(throwable -> {
                            Log.d("uploadBackgroundApi", "throwable.msg=" + throwable.getMessage());
                            throwable.printStackTrace();
                            ZToast.error("上传背景失败！" + throwable.getMessage());
                        })
                        .onComplete(() -> EventBus.hideLoading(500))
                        .subscribe();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("uploadBackgroundApi", "e.msg=" + e.getMessage());
                ZToast.error("上传背景失败！" + e.getMessage());
                EventBus.hideLoading(500);
            }
        }
    }

}
