package com.zpj.shouji.market.api;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.zpj.http.ZHttp;
import com.zpj.http.core.HttpObserver;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.toast.ZToast;

import java.io.File;
import java.io.FileInputStream;

public class UploadImageApi {

    public static HttpObserver<Document> uploadAvatarApi(File file) throws Exception {
        return ZHttp.post(String.format("http://tt.shouji.com.cn/app/user_upload_avatar.jsp?jsessionid=%s&versioncode=%s", UserManager.getInstance().getSessionId(), HttpApi.VERSION_CODE))
                .cookie(UserManager.getInstance().getCookie())
                .data("image", "image.png", new FileInputStream(file))
                .toHtml();
    }

    public static HttpObserver<Document> uploadBackgroundApi(File file) throws Exception {
        return ZHttp.post(
                String.format(
                        "http://tt.shouji.com.cn/app/user_upload_background.jsp?action=save&sn=%s&jsessionid=%s&versioncode=%s",
                        UserManager.getInstance().getSn(),
                        UserManager.getInstance().getSessionId(),
                        HttpApi.VERSION_CODE))
                .cookie(UserManager.getInstance().getCookie())
                .data("image", "image.png", new FileInputStream(file))
                .toHtml();
    }

    public static void uploadCropImage(File file, boolean isAvatar) {
        if (isAvatar) {
            EventBus.showLoading("上传头像...");
            try {
                uploadAvatarApi(file)
                        .onSuccess(doc -> {
                            Log.d("uploadAvatarApi", "data=" + doc);
                            String info = doc.selectFirst("info").text();
                            if ("success".equals(doc.selectFirst("result").text())) {
//                                ZToast.success(info);

                                UserManager.getInstance().getMemberInfo().setMemberAvatar(info);
                                UserManager.getInstance().saveUserInfo();
                                PictureUtil.saveAvatar(Uri.fromFile(file), new IHttp.OnSuccessListener<File>() {
                                    @Override
                                    public void onSuccess(File data) throws Exception {
                                        EventBus.sendImageUploadEvent(isAvatar);
                                    }
                                });
                            } else if (TextUtils.isEmpty(info)) {
                                ZToast.error(doc.selectFirst("title").text());
                            } else {
                                ZToast.error(info);
                            }
                            EventBus.hideLoading(500);
                        })
                        .onError(throwable -> {
                            throwable.printStackTrace();
                            ZToast.error("上传头像失败！" + throwable.getMessage());
                            EventBus.hideLoading(500);
                        })
//                        .onComplete(() -> EventBus.hideLoading(500))
                        .subscribe();
            } catch (Exception e) {
                e.printStackTrace();
                ZToast.error("上传头像失败！" + e.getMessage());
                EventBus.hideLoading(500);
            }
        } else {
            EventBus.showLoading("上传背景...");
            try {
                uploadBackgroundApi(file)
                        .onSuccess(doc -> {
                            Log.d("uploadBackgroundApi", "data=" + doc);
                            String info = doc.selectFirst("info").text();
                            if ("success".equals(doc.selectFirst("result").text())) {
                                UserManager.getInstance().getMemberInfo().setMemberBackGround(info);
                                UserManager.getInstance().saveUserInfo();
                                PictureUtil.saveBackground(Uri.fromFile(file), data -> EventBus.sendImageUploadEvent(isAvatar));
                            } else {
                                ZToast.error(info);
                            }
                            EventBus.hideLoading(500);
                        })
                        .onError(throwable -> {
                            Log.d("uploadBackgroundApi", "throwable.msg=" + throwable.getMessage());
                            throwable.printStackTrace();
                            EventBus.hideLoading(500);
                            ZToast.error("上传背景失败！" + throwable.getMessage());
                        })
//                        .onComplete(() -> EventBus.hideLoading(500))
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
