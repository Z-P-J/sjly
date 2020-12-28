package com.zpj.shouji.market.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.http.core.HttpKeyVal;
import com.zpj.http.core.IHttp;
import com.zpj.http.core.ObservableTask;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.matisse.entity.Item;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.constant.UpdateFlagAction;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.utils.Callback;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.toast.ZToast;

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

    public static Connection openConnection(String url, Connection.Method method) {
        Connection connection = ZHttp.connect(url).method(method);
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
        connection.validateTLSCertificates(false)
                .userAgent(USER_AGENT)
                .onRedirect(new IHttp.OnRedirectListener() {
                    @Override
                    public boolean onRedirect(String redirectUrl) {
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

    public static ObservableTask<Document> get(String url) {
        return openConnection(url, Connection.Method.GET).toXml();
    }

    public static ObservableTask<Document> post(String url) {
        return openConnection(url, Connection.Method.POST).toXml();
    }

    public static ObservableTask<Runnable> with(Runnable runnable) {
        return new ObservableTask<>(
                Observable.create((ObservableOnSubscribe<Runnable>) emitter -> {
                    runnable.run();
                    emitter.onNext(runnable);
                    emitter.onComplete();
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static ObservableTask<Document> collectionRecommend() {
        return get("http://tt.shouji.com.cn/androidv3/yyj_tj_xml.jsp");
    }

    public static ObservableTask<Document> bannerApi() {
        return get("http://tt.tljpxm.com/androidv3/app_index_xml.jsp?index=1");
    }

    public static ObservableTask<Document> recentUpdateAppApi() {
        return get("http://tt.shouji.com.cn/androidv3/app_list_xml.jsp?index=1");
    }

    public static ObservableTask<Document> homeRecommendSoftApi() {
        return get("http://tt.shouji.com.cn/androidv3/special_list_xml.jsp?id=-9998");
    }

    public static ObservableTask<Document> homeRecommendGameApi() {
        return get("http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sdk=100&sort=day");
    }

    public static ObservableTask<Document> subjectApi() {
        return get("http://tt.tljpxm.com/androidv3/app_index_xml.jsp?index=1");
    }

    public static ObservableTask<Document> recentUpdateSoft() {
        return get("http://tt.shouji.com.cn/androidv3/soft_index_xml.jsp?sort=time");
    }

    public static ObservableTask<Document> recentUpdateGame() {
        return get("http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sort=time");
    }

    public static ObservableTask<Document> netGame() {
        return get("http://tt.shouji.com.cn/androidv3/netgame.jsp");
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
        return "http://tt.shouji.com.cn/app/" + key + ".jsp";
    }

    public static ObservableTask<Document> likeApi(String type, String id) {
        String url = "http://tt.shouji.com.cn/app/comment_flower_xml.jsp?t=" + type + "&id=" + id;
        return get(url);
    }

    public static ObservableTask<Document> addFriendApi(String id) {
        String url = "http://tt.tljpxm.com/xml/addfriendprocess?memberid=" + id;
        return get(url);
    }

    public static ObservableTask<Document> deleteFriendApi(String id) {
        String url = "http://tt.tljpxm.com/xml/deletefriendprocess?friendMemberID=" + id;
        return get(url);
    }

    public static ObservableTask<Document> getMemberInfoByIdApi(String id) {
        String url = String.format("http://tt.shouji.com.cn/app/view_member_xml_v4.jsp?id=%s", id);
        if (id.equals(UserManager.getInstance().getUserId())) {
            url += "&myself=yes";
        }
        Log.d("getMemberInfoByIdApi", "url=" + url);
        return get(url);
    }

    public static ObservableTask<Document> getMemberInfoByNameApi(String name) {
        String url = String.format("http://tt.shouji.com.cn/app/view_member_xml_v4.jsp?mm=%s", name);
        Log.d("getMemberInfoByNameApi", "url=" + url);
        return get(url);
    }

    public static ObservableTask<Document> blacklistApi(String id, boolean isAdd) {
        String url = String.format("http://tt.tljpxm.com/app/user_blacklist_add.jsp?mid=%s&t=%s", id, isAdd ? "add" : "del");
        return get(url);
    }

    public static void addBlacklistApi(String id) {
//        String url = "http://tt.tljpxm.com/app/user_blacklist_add.jsp?t=add&mid=" + id;
//        return connect(url);
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

    public static ObservableTask<Document> removeBlacklistApi(String id) {
        return blacklistApi(id, false);
    }

    public static void addCollectionApi(String id, Runnable runnable) {
        get(String.format("http://tt.shouji.com.cn/app/user_review_fav_add.jsp?t=discuss&id=%s", id))
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

    public static ObservableTask<Document> addCollectionApi(String id, String type) {
        return get(String.format("http://tt.shouji.com.cn/app/user_review_fav_add.jsp?t=%s&id=%s", type, id));
    }

    public static void deleteCollectionApi(String id) {
        openConnection("http://tt.shouji.com.cn/app/user_review_fav_del.jsp", Connection.Method.GET)
                .data("id", id)
                .data("t", "discuss")
                .toHtml()
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

    public static ObservableTask<Document> deleteCollectionApi(String id, String type) {
        return get(String.format("http://tt.shouji.com.cn/app/user_review_fav_del.jsp?t=%s&id=%s", type, id));
    }

    public static void deleteThemeApi(String id, String type) {
        openConnection("http://tt.shouji.com.cn/app/user_review_del_xml.jsp", Connection.Method.GET)
                .data("id", id)
                .data("t", type)
                .toHtml()
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
        openConnection("http://tt.shouji.com.cn/app/user_review_pass_xml.jsp", Connection.Method.GET)
                .data("id", id)
                .data("t", "discuss")
                .toHtml()
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
        openConnection("http://tt.shouji.com.cn/app/user_review_public_xml.jsp", Connection.Method.GET)
                .data("id", id)
                .data("t", "discuss")
                .toHtml()
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

//    public static ObservableTask<Document> commentApi(String id, String content) {
//        return openConnection("http://tt.tljpxm.com/app/comment_xml_v5.jsp", Connection.Method.POST)
//                .data("replyid", id)
//                .data("phone", "MI%205s")
//                .data("content", content)
//                .toHtml();
//    }

    public static ObservableTask<Document> discussCommentApi(String replyId, String content) {
        return openConnection("http://tt.tljpxm.com/app/square_disscuss_text_post_xml.jsp", Connection.Method.POST)
                .data("replyid", replyId)
                .data("phone", "MI%205s")
                .data("content", content)
                .toHtml();
    }

//    public static ObservableTask<Document> appCommentApi(String content, String appId, String appType, String appPackage) {
//        return openConnection("http://tt.tljpxm.com/app/comment_xml_v5.jsp", Connection.Method.POST)
//                .data("replyid", "0")
//                .data("phone", "MI%205s")
//                .data("content", content)
//                .data("appid", appId)
//                .data("apptype", appType)
//                .data("package", appPackage)
//                .toHtml();
//    }

    public static ObservableTask<Document> rsyncMessageApi() {
        return openConnection("http://tt.tljpxm.com/app/rsyncMessageV3.jsp", Connection.Method.POST)
                .data("from", "refresh")
                .toHtml();
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
        openConnection("http://tt.tljpxm.com/app/updateFlag.jsp", Connection.Method.GET)
                .data("action", action.getAction())
                .toHtml()
                .onSuccess(data -> {
                    Log.d("HttpApi", "updateFlagApi data=" + data);
                    UserManager.getInstance().rsyncMessage(true);
                })
                .subscribe();
    }

    public static ObservableTask<Document> deletePrivateLetterApi(String id) {
        return openConnection("http://tt.tljpxm.com/app/user_message_del_xml.jsp", Connection.Method.GET)
                .data("message", id)
                .toHtml();
    }

    public static void sendPrivateLetterApi(Context context, String id, String content, List<Item> imgList, Runnable successRunnable, IHttp.OnStreamWriteListener listener) {
        EventBus.showLoading("发送中...");
        boolean compress = AppConfig.isCompressUploadImage();
        ObservableTask<Document> task;
        if (imgList == null || imgList.isEmpty()) {
            task = openConnection("http://tt.tljpxm.com/app/user_message_add_text_xml.jsp", Connection.Method.GET)
                    .data("mmid", id)
                    .data("content", content)
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
                                    String.format("http://tt.shouji.com.cn/app/user_message_add_post_xml.jsp?versioncode=%s&jsessionid=%s",
                                            "199", UserManager.getInstance().getSessionId()))
                                    .data("mmid", id)
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
                    EventBus.hideLoading(250);
                })
                .onError(throwable -> {
                    ZToast.error("发送失败！" + throwable.getMessage());
                    EventBus.hideLoading(250);
                })
                .onComplete(() -> EventBus.hideLoading(250))
                .subscribe();
    }

    public static ObservableTask<Document> appInfoApi(String type, String id) {
        return get(String.format("http://tt.shouji.com.cn/androidv4/%s_show.jsp?id=%s", type, id));
    }

    public static ObservableTask<Document> nicknameApi(String nickname) {
        return get(String.format("http://tt.shouji.com.cn/app/user_nickname_xml.jsp?NickName=%s", nickname));
    }

    public static ObservableTask<Document> emailApi(String email, String password) {
        return get(String.format("http://tt.shouji.com.cn/app/user_email_xml.jsp?MemberEmail=%s&p=%s", email, encodePassword(password)));
    }

    public static ObservableTask<Document> passwordApi(String oldPassword, String newPassword) {
        String url = "http://tt.shouji.com.cn/app/user_password_xml.jsp?p=";
        url += encodePassword(oldPassword.trim());
        url += ("&np=" + encodePassword(newPassword.trim()));
        Log.d("passwordApi", "url=" + url);
        return get(url);
    }

    public static ObservableTask<Document> addFavCollectionApi(String id, String type) {
        return get(String.format("http://tt.shouji.com.cn/app/user_yyj_fav_add.jsp?id=%s&t=%s", id, type));
    }

    public static ObservableTask<Document> delFavCollectionApi(String id, String type) {
        return get(String.format("http://tt.shouji.com.cn/app/user_yyj_fav_del.jsp?id=%s&t=%s", id, type));
    }

    public static ObservableTask<Document> getShareInfoApi(String id) {
        return get(String.format("http://tt.shouji.com.cn/app/getShareInfo.jsp?id=%s", id));
    }

    public static ObservableTask<Document> getSupportUserListApi(String id) {
        return get(String.format("http://tt.shouji.com.cn/app/flower_show_xml_v2.jsp?type=discuss&id=%s", id));
    }

    public static ObservableTask<Document> deleteBackgroundApi() {
        return get("http://tt.shouji.com.cn/app/user_upload_background.jsp?action=delete");
    }

    public static ObservableTask<Document> reportApi(String id, String type, String reason) {
        return get(String.format("http://tt.shouji.com.cn/app/jubao.jsp?id=%s&t=%s&reason=%s", id, type, reason));
    }

    public static ObservableTask<Document> appRatingApi(String id, String value, String type, String packageName, String versionName) {
        String url = String.format("http://tt.shouji.com.cn/appv3/score_post_xml_v2.jsp?id=%s&value=%s&type=%s&packagename=%s&versionname=%s", id, value, type, packageName, versionName);
        return get(url);
    }

    public static ObservableTask<Document> findDetailMemberInfoApi(String appId, String type, String memberId) {
        String url = String.format("http://tt.shouji.com.cn/appv3/findDetailMemberInfo.jsp?id=%s&t=%s&memberid=%s", appId, "soft".equals(type) ? "1" : "2", memberId);
        return get(url);
    }

    public static void appFavoriteApi(String appId, String type, Callback<Boolean> callback) {
        String url = String.format("http://tt.shouji.com.cn/appv3/user_fav_add_xml_v2.jsp?id=%s&t=%s", appId, "soft".equals(type) ? "1" : "2");
        Log.d("appFavoriteApi", "url=" + url);
        get(url)
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
        String url = String.format("http://tt.shouji.com.cn/appv3/user_fav_delete_member_xml_v2.jsp?id=%s&t=1&apptype=%s", appId, type);
        get(url)
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
