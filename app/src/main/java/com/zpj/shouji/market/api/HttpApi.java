package com.zpj.shouji.market.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.zpj.http.ZHttp;
import com.zpj.http.core.HttpConfig;
import com.zpj.http.core.HttpKeyVal;
import com.zpj.http.core.HttpObserver;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.constant.UpdateFlagAction;
import com.zpj.shouji.market.imagepicker.entity.Item;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.toast.ZToast;
import com.zpj.utils.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public final class HttpApi {

    public static final String USER_AGENT = "okhttp/3.0.1";
    public static final String VERSION_CODE = "210";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String VALUE_ACCEPT_ENCODING = "gzip";

    private HttpApi() {

    }

    public static HttpConfig openConnection(String url, IHttp.Method method) {
        HttpConfig connection = ZHttp.connect(url).method(method);
        if (!url.contains("versioncode=")) {
            connection.data("versioncode", VERSION_CODE);
        }
        if (!url.contains("version=")) {
            connection.data("version", "3.1");
        }
        if (!url.contains("sn=")) {
            connection.data("sn", UserManager.getInstance().getSn());
        }
        if (UserManager.getInstance().isLogin() && !url.contains("jsessionid=")) {
            connection.data("jsessionid", UserManager.getInstance().getSessionId());
        }
        connection.onRedirect(new IHttp.OnRedirectListener() {
                    @Override
                    public boolean onRedirect(int redirectCount, String redirectUrl) {
                        Log.d("connect", "onRedirect redirectUrl=" + redirectUrl);
                        return true;
                    }
                })
                .cookie(UserManager.getInstance().getCookie())
//                .header(HEADER_ACCEPT_ENCODING, VALUE_ACCEPT_ENCODING)
                .referer(url)
                .ignoreContentType(true);
        return connection;
    }

    public static HttpConfig get(String url) {
        return openConnection(url, IHttp.Method.GET);
    }

    public static HttpConfig post(String url) {
        return openConnection(url, IHttp.Method.POST);
    }

    public static HttpObserver<Document> getXml(String url) {
        return get(url).toXml();
    }

    public static HttpObserver<Document> getHtml(String url) {
        return get(url).toHtml();
    }

    public static HttpObserver<Document> postXml(String url) {
        return post(url).toXml();
    }

    public static HttpObserver<Runnable> with(Runnable runnable) {
        return new HttpObserver<>(
                Observable.create((ObservableOnSubscribe<Runnable>) emitter -> {
                    runnable.run();
                    emitter.onNext(runnable);
                    emitter.onComplete();
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static HttpObserver<Document> collectionRecommend() {
        return getXml("/androidv3/yyj_tj_xml.jsp");
    }

    public static HttpObserver<Document> bannerApi() {
//        http://tt.tljpxm.com
        return getXml("/androidv3/app_index_xml.jsp?index=1");
    }

    public static HttpObserver<Document> recentUpdateAppApi() {
        return getXml("/androidv3/app_list_xml.jsp?index=1");
    }

    public static HttpObserver<Document> homeRecommendSoftApi() {
        return getXml("/androidv3/special_list_xml.jsp?id=-9998");
    }

    public static HttpObserver<Document> homeRecommendGameApi() {
        return getXml("/androidv3/game_index_xml.jsp?sdk=100&sort=day");
    }

    public static HttpObserver<Document> subjectApi() {
//        http://tt.tljpxm.com
        return getXml("/androidv3/app_index_xml.jsp?index=1");
    }

    public static HttpObserver<Document> recentUpdateSoft() {
        return getXml("/androidv3/soft_index_xml.jsp?sort=time");
    }

    public static HttpObserver<Document> recentUpdateGame() {
        return getXml("/androidv3/game_index_xml.jsp?sort=time");
    }

    public static HttpObserver<Document> netGame() {
        return getXml("/androidv3/netgame.jsp");
    }

    public static String myCollectionAppsUrl(String id) {
        return getCollectUrl("view_member_fav_xml_v2") + "?id=" + id;
//        return getCollectUrl("user_fav_index_xml_v2");
    }

    public static String myCollectionsUrl(String id) {
        return getCollectUrl("user_yyj_fav_xml") + "?userid=" + id;
    }

    public static String myCollectionDiscoverUrl(String id) {
        return getCollectUrl("view_member_content_fav_v2") + "?t=discuss&id=" + id;
//        return getCollectUrl("user_review_fav_xml_v2") + "?t=discuss";
    }

    public static String myCollectionCommentUrl(String id) {
        return getCollectUrl("view_member_content_fav_v2") + "?t=reivew&id=" + id;
//        return getCollectUrl("user_review_fav_xml_v2") + "?t=reivew";
    }

    public static String myCollectionWallpaperUrl(String id) {
        return getCollectUrl("user_letu_fav_xml_v2") + "?member=" + id;
    }

    public static String myCollectionSubjectUrl(String id) {
        return getCollectUrl("view_member_fav_speical_xml") + "?id=" + id;
//        return getCollectUrl("user_special_fav_index");
    }

    public static String myCollectionGameTutorialUrl(String id) {
        return getCollectUrl("user_article_fav_list") + "?type=game&userid=" + id;
    }

    public static String myCollectionSoftTutorialUrl(String id) {
        return getCollectUrl("user_article_fav_list") + "?type=soft&userid=" + id;
    }

    private static String getCollectUrl(String key) {
        return "/app/" + key + ".jsp";
    }

    public static HttpObserver<Document> likeApi(String type, String id) {
        String url = "/app/comment_flower_xml.jsp?t=" + type + "&id=" + id;
        return getXml(url);
    }

    public static HttpObserver<Document> addFriendApi(String id) {
//        http://tt.tljpxm.com
        String url = "/xml/addfriendprocess?memberid=" + id;
        return getXml(url);
    }

    public static HttpObserver<Document> deleteFriendApi(String id) {
//        http://tt.tljpxm.com
        String url = "/xml/deletefriendprocess?friendMemberID=" + id;
        return getXml(url);
    }

    public static HttpObserver<Document> getMemberInfoByIdApi(String id) {
        String url = String.format("/app/view_member_xml_v4.jsp?id=%s", id);
        if (id.equals(UserManager.getInstance().getUserId())) {
            url += "&myself=yes";
        }
        Log.d("getMemberInfoByIdApi", "url=" + url);
        return getXml(url);
    }

    public static HttpObserver<Document> getMemberInfoByNameApi(String name) {
        String url = String.format("/app/view_member_xml_v4.jsp?mm=%s", name);
        Log.d("getMemberInfoByNameApi", "url=" + url);
        return getXml(url);
    }

    public static HttpObserver<Document> blacklistApi(String id, boolean isAdd) {
//        http://tt.tljpxm.com
        String url = String.format("/app/user_blacklist_add.jsp?mid=%s&t=%s", id, isAdd ? "add" : "del");
        return getXml(url);
    }

    public static void addBlacklistApi(String id) {
        blacklistApi(id, true)
                .onSuccess(data -> {
                    String info = data.selectFirst("info").text();
                    if ("success".equals(data.selectFirst("result").text())) {
                        ZToast.success(info);
                    } else {
                        ZToast.error(info);
                    }
                })
                .onError(throwable -> ZToast.error(throwable.getMessage()))
                .subscribe();
        ;
    }

    public static HttpObserver<Document> removeBlacklistApi(String id) {
        return blacklistApi(id, false);
    }

    public static void addCollectionApi(String id, Runnable runnable) {
        getXml(String.format("/app/user_review_fav_add.jsp?t=discuss&id=%s", id))
                .onSuccess(doc -> {
                    String info = doc.selectFirst("info").text();
                    if ("success".equals(doc.selectFirst("result").text())) {
                        ZToast.success(info);
                        if (runnable != null) {
                            runnable.run();
                        }
                    } else {
                        ZToast.error(info);
                    }
                })
                .onError(throwable -> ZToast.error(throwable.getMessage()))
                .subscribe();
    }

    public static HttpObserver<Document> addCollectionApi(String id, String type) {
        return getXml(String.format("/app/user_review_fav_add.jsp?t=%s&id=%s", type, id));
    }

    public static void deleteCollectionApi(String id) {
        get("/app/user_review_fav_del.jsp")
                .data("id", id)
                .data("t", "discuss")
                .toXml()
                .onSuccess(doc -> {
                    String info = doc.selectFirst("info").text();
                    if ("success".equals(doc.selectFirst("result").text())) {
                        ZToast.success(info);
                        EventBus.sendRefreshEvent();
                    } else {
                        ZToast.error(info);
                    }
                })
                .onError(throwable -> ZToast.error(throwable.getMessage()))
                .subscribe();
    }

    public static HttpObserver<Document> deleteCollectionApi(String id, String type) {
        return getXml(String.format("/app/user_review_fav_del.jsp?t=%s&id=%s", type, id));
    }

    public static void deleteThemeApi(String id, String type) {
        get("/app/user_review_del_xml.jsp")
                .data("id", id)
                .data("t", type)
                .toXml()
                .onSuccess(doc -> {
                    String info = doc.selectFirst("info").text();
                    if ("success".equals(doc.selectFirst("result").text())) {
                        ZToast.success(info);
                        EventBus.sendRefreshEvent();
                    } else {
                        ZToast.error(info);
                    }
                })
                .onError(throwable -> ZToast.error(throwable.getMessage()))
                .subscribe();
    }

    public static void privateThemeApi(String id) {
        get("/app/user_review_pass_xml.jsp")
                .data("id", id)
                .data("t", "discuss")
                .toXml()
                .onSuccess(doc -> {
                    String info = doc.selectFirst("info").text();
                    if ("success".equals(doc.selectFirst("result").text())) {
                        ZToast.success(info);
                        EventBus.sendRefreshEvent();
                    } else {
                        ZToast.error(info);
                    }
                })
                .onError(throwable -> ZToast.error(throwable.getMessage()))
                .subscribe();
    }

    public static void publicThemeApi(String id) {
        get("/app/user_review_public_xml.jsp")
                .data("id", id)
                .data("t", "discuss")
                .toXml()
                .onSuccess(doc -> {
                    String info = doc.selectFirst("info").text();
                    if ("success".equals(doc.selectFirst("result").text())) {
                        ZToast.success(info);
                        EventBus.sendRefreshEvent();
                    } else {
                        ZToast.error(info);
                    }
                })
                .onError(throwable -> ZToast.error(throwable.getMessage()))
                .subscribe();
    }

//    public static HttpObserver<Document> commentApi(String id, String content) {
//        return openConnection("http://tt.tljpxm.com/app/comment_xml_v5.jsp", Connection.Method.POST)
//                .data("replyid", id)
//                .data("phone", "MI%205s")
//                .data("content", content)
//                .toHtml();
//    }

    public static HttpObserver<Document> discussCommentApi(String replyId, String content) {
//        http://tt.tljpxm.com
        return post("/app/square_disscuss_text_post_xml.jsp")
                .data("replyid", replyId)
                .data("phone", "MI%205s")
                .data("content", content)
                .toXml();
    }

//    public static HttpObserver<Document> appCommentApi(String content, String appId, String appType, String appPackage) {
//        return openConnection("http://tt.tljpxm.com/app/comment_xml_v5.jsp", Connection.Method.POST)
//                .data("replyid", "0")
//                .data("phone", "MI%205s")
//                .data("content", content)
//                .data("appid", appId)
//                .data("apptype", appType)
//                .data("package", appPackage)
//                .toHtml();
//    }

    public static HttpObserver<Document> rsyncMessageApi() {
//        http://tt.tljpxm.com
        return post("/app/rsyncMessageV3.jsp")
                .data("from", "refresh")
                .toXml();
    }

    public static void updateFlagApi(UpdateFlagAction action) {
        if (action == UpdateFlagAction.COMMENT
                && UserManager.getInstance().getMessageInfo().getMessageCount() < 1) {
            return;
        }
        if (action == UpdateFlagAction.GOOD
                && UserManager.getInstance().getMessageInfo().getLikeCount() < 1) {
            return;
        }
        if (action == UpdateFlagAction.AT
                && UserManager.getInstance().getMessageInfo().getAiteCount() < 1) {
            return;
        }
        if (action == UpdateFlagAction.DISCOVER
                && UserManager.getInstance().getMessageInfo().getDiscoverCount() < 1) {
            return;
        }
        if (action == UpdateFlagAction.PRIVATE
                && UserManager.getInstance().getMessageInfo().getPrivateLetterCount() < 1) {
            return;
        }
        if (action == UpdateFlagAction.FAN
                && UserManager.getInstance().getMessageInfo().getFanCount() < 1) {
            return;
        }
//        http://tt.tljpxm.com
        get("/app/updateFlag.jsp")
                .data("action", action.getAction())
                .toXml()
                .onSuccess(data -> {
                    Log.d("HttpApi", "updateFlagApi data=" + data);
                    UserManager.getInstance().rsyncMessage(true);
                })
                .subscribe();
    }

    public static HttpObserver<Document> deletePrivateLetterApi(String id) {
//        http://tt.tljpxm.com
        return get("/app/user_message_del_xml.jsp")
                .data("message", id)
                .toXml();
    }

    public static void sendPrivateLetterApi(Context context, String id, String content, List<Item> imgList, Runnable successRunnable, IHttp.OnStreamWriteListener listener) {
        EventBus.showLoading("发送中...");
        boolean compress = AppConfig.isCompressUploadImage();
        HttpObserver<Document> task;
        if (imgList == null || imgList.isEmpty()) {
//            http://tt.tljpxm.com
            task = get("/app/user_message_add_text_xml.jsp")
                    .data("mmid", id)
                    .data("content", content)
                    .toXml();
        } else {
            task = new HttpObserver<>(
                    (ObservableOnSubscribe<List<IHttp.KeyVal>>) emitter -> {
                        List<IHttp.KeyVal> dataList = new ArrayList<>();
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
                        public HttpObserver<Document> onNext(List<IHttp.KeyVal> dataList) throws Exception {
                            return ZHttp.post(
                                    String.format("/app/user_message_add_post_xml.jsp?versioncode=%s&jsessionid=%s",
                                            "199", UserManager.getInstance().getSessionId()))
                                    .data("mmid", id)
                                    .data("content", content)
                                    .data(dataList)
                                    .cookie(UserManager.getInstance().getCookie())
//                                    .ignoreContentType(true)
                                    .toXml();
                        }
                    });
        }

        task
                .onSuccess(data -> {
                    String info = data.selectFirst("info").text();
                    if ("success".equals(data.selectFirst("result").text())) {
                        ZToast.success("发送成功");
                        successRunnable.run();
                    } else {
                        ZToast.error(info);
                    }
                    EventBus.hideLoading(250);
                })
                .onError(throwable -> {
                    ZToast.error("发送失败！" + throwable.getMessage());
                    EventBus.hideLoading(250);
                })
                .subscribe();
    }

    public static HttpObserver<Document> appInfoApi(String type, String id) {
        return getXml(String.format("/androidv4/%s_show.jsp?id=%s", type, id));
    }

    public static HttpObserver<Document> nicknameApi(String nickname) {
        return getXml(String.format("/app/user_nickname_xml.jsp?NickName=%s", nickname));
    }

    public static HttpObserver<Document> emailApi(String email, String password) {
        return getXml(String.format("/app/user_email_xml.jsp?MemberEmail=%s&p=%s", email, encodePassword(password)));
    }

    public static HttpObserver<Document> passwordApi(String oldPassword, String newPassword) {
        String url = "/app/user_password_xml.jsp?p=";
        url += encodePassword(oldPassword.trim());
        url += ("&np=" + encodePassword(newPassword.trim()));
        Log.d("passwordApi", "url=" + url);
        return getXml(url);
    }

    public static HttpObserver<Document> addFavCollectionApi(String id, String type) {
        return getXml(String.format("/app/user_yyj_fav_add.jsp?id=%s&t=%s", id, type));
    }

    public static HttpObserver<Document> delFavCollectionApi(String id, String type) {
        return getXml(String.format("/app/user_yyj_fav_del.jsp?id=%s&t=%s", id, type));
    }

    public static HttpObserver<Document> getShareInfoApi(String id) {
        return getXml(String.format("/app/getShareInfo.jsp?id=%s", id));
    }

    public static HttpObserver<Document> getSupportUserListApi(String contentType, String id) {
        return getXml(String.format("/app/flower_show_xml_v2.jsp?type=%s&id=%s", contentType, id));
    }

    public static HttpObserver<Document> deleteBackgroundApi() {
        return getXml("/app/user_upload_background.jsp?action=delete");
    }

    public static HttpObserver<Document> reportApi(String id, String type, String reason) {
        return getXml(String.format("/app/jubao.jsp?id=%s&t=%s&reason=%s", id, type, reason));
    }

    public static HttpObserver<Document> appRatingApi(String id, String value, String type, String packageName, String versionName) {
        String url = String.format("/appv3/score_post_xml_v2.jsp?id=%s&value=%s&type=%s&packagename=%s&versionname=%s", id, value, type, packageName, versionName);
        return getXml(url);
    }

    public static HttpObserver<Document> findDetailMemberInfoApi(String appId, String type, String memberId) {
        String url = String.format("/appv3/findDetailMemberInfo.jsp?id=%s&t=%s&memberid=%s", appId, "soft".equals(type) ? "1" : "2", memberId);
        return getXml(url);
    }

    public static void appFavoriteApi(String appId, String type, Callback<Boolean> callback) {
        String url = String.format("/appv3/user_fav_add_xml_v2.jsp?id=%s&t=%s", appId, "soft".equals(type) ? "1" : "2");
        Log.d("appFavoriteApi", "url=" + url);
        getHtml(url)
                .onSuccess(data -> {
                    Log.d("appFavoriteApi", "data=" + data);
                    if ("success".equals(data.selectFirst("result").text())) {
                        ZToast.success("收藏成功！");
                        if (callback != null) {
                            callback.onCallback(true);
                        }
                    } else {
                        ZToast.error("收藏失败！");
                        if (callback != null) {
                            callback.onCallback(false);
                        }
                    }
                })
                .onError(throwable -> {
                    ZToast.error("收藏失败！" + throwable.getMessage());
                    if (callback != null) {
                        callback.onCallback(false);
                    }
                })
                .subscribe();
    }

    public static void cancelAppFavoriteApi(String appId, String type, Callback<Boolean> callback) {
        String url = String.format("/appv3/user_fav_delete_member_xml_v2.jsp?id=%s&t=1&apptype=%s", appId, type);
        getXml(url)
                .onSuccess(data -> {
                    if ("success".equals(data.selectFirst("result").text())) {
                        ZToast.success("取消收藏成功！");
                        if (callback != null) {
                            callback.onCallback(false);
                        }
                    } else {
                        ZToast.error("取消收藏失败！");
                        if (callback != null) {
                            callback.onCallback(true);
                        }
                    }
                })
                .onError(throwable -> {
                    ZToast.error("取消收藏失败！" + throwable.getMessage());
                    if (callback != null) {
                        callback.onCallback(true);
                    }
                })
                .subscribe();
    }


    private static String encodePassword(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(string.getBytes(StandardCharsets.UTF_8));
            return android.util.Base64.encodeToString(md5.digest(), Base64.DEFAULT).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
