package com.zpj.shouji.market.api;

import android.util.Log;

import com.felix.atoast.library.AToast;
import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.http.core.ObservableTask;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.constant.UpdateFlagAction;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.utils.OSUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public final class HttpApi {

    private static final String USER_AGENT = "okhttp/3.0.1";
    private static final String VERSION_CODE = "199";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String VALUE_ACCEPT_ENCODING = "gzip";

    private HttpApi() {

    }

    public static Connection openConnection(String url, Connection.Method method) {
        Connection connection = ZHttp.connect(url).method(method);
        connection.data("versioncode", VERSION_CODE);
        connection.data("version", "2.9.9.9.3");
        connection.data("sn", UserManager.getInstance().getSn());
        if (UserManager.getInstance().isLogin()) {
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
        return openConnection(url, Connection.Method.GET).toHtml();
    }

    public static ObservableTask<Document> post(String url) {
        return openConnection(url, Connection.Method.POST).toHtml();
    }

    public static ObservableTask<Runnable> with(Runnable runnable) {
        return new ObservableTask<>(Observable.create((ObservableOnSubscribe<Runnable>) emitter -> {
            runnable.run();
            emitter.onNext(runnable);
            emitter.onComplete();
        })).subscribeOn(Schedulers.io())
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

    public static ObservableTask<Document> getMemberInfoApi(String id) {
        String url = "http://tt.tljpxm.com/app/view_member_xml_v4.jsp?id=" + id;
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
                        AToast.success(info);
                    } else {
                        AToast.error(info);
                    }
                })
                .onError(throwable -> AToast.error(throwable.getMessage()))
                .subscribe();;
    }

    public static ObservableTask<Document> removeBlacklistApi(String id) {
        return blacklistApi(id, false);
    }

    public static void addCollectionApi(String id) {
        openConnection("http://tt.shouji.com.cn/app/user_review_fav_add.jsp", Connection.Method.GET)
                .data("id", id)
                .data("t", "discuss")
                .toHtml()
                .onSuccess(doc -> {
                    String info = doc.selectFirst("info").text();
                    if ("success".equals(doc.selectFirst("result").text())) {
                        AToast.success(info);
                    } else {
                        AToast.error(info);
                    }
                })
                .onError(throwable -> AToast.error(throwable.getMessage()))
                .subscribe();
    }

    public static void deleteCollectionApi(String id) {
        openConnection("http://tt.shouji.com.cn/app/user_review_fav_del.jsp", Connection.Method.GET)
                .data("id", id)
                .data("t", "discuss")
                .toHtml()
                .onSuccess(doc -> {
                    String info = doc.selectFirst("info").text();
                    if ("success".equals(doc.selectFirst("result").text())) {
                        AToast.success(info);
                    } else {
                        AToast.error(info);
                    }
                })
                .onError(throwable -> AToast.error(throwable.getMessage()))
                .subscribe();
    }

    public static void deleteThemeApi(String id) {
        openConnection("http://tt.shouji.com.cn/app/user_review_del_xml.jsp", Connection.Method.GET)
                .data("id", id)
                .data("t", "discuss")
                .toHtml()
                .onSuccess(doc -> {
                    String info = doc.selectFirst("info").text();
                    if ("success".equals(doc.selectFirst("result").text())) {
                        AToast.success(info);
                    } else {
                        AToast.error(info);
                    }
                })
                .onError(throwable -> AToast.error(throwable.getMessage()))
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
                        AToast.success(info);
                    } else {
                        AToast.error(info);
                    }
                })
                .onError(throwable -> AToast.error(throwable.getMessage()))
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
                        AToast.success(info);
                    } else {
                        AToast.error(info);
                    }
                })
                .onError(throwable -> AToast.error(throwable.getMessage()))
                .subscribe();
    }

    public static ObservableTask<Document> commentApi(String id, String content) {
        return openConnection("http://tt.tljpxm.com/app/comment_xml_v5.jsp", Connection.Method.POST)
                .data("replyid", id)
                .data("phone", "MI%205s")
                .data("content", content)
                .toHtml();
    }

    public static ObservableTask<Document> discussCommentApi(String replyId, String content) {
        return openConnection("http://tt.tljpxm.com/app/square_disscuss_text_post_xml.jsp", Connection.Method.POST)
                .data("replyid", replyId)
                .data("phone", "MI%205s")
                .data("content", content)
                .toHtml();
    }

    public static ObservableTask<Document> appCommentApi(String content, String appId, String appType, String appPackage) {
        return openConnection("http://tt.tljpxm.com/app/comment_xml_v5.jsp", Connection.Method.POST)
                .data("replyid", "0")
                .data("phone", "MI%205s")
                .data("content", content)
                .data("appid", appId)
                .data("apptype", appType)
                .data("package", appPackage)
                .toHtml();
    }

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

}
