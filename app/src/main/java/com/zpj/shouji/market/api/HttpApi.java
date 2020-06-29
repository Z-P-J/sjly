package com.zpj.shouji.market.api;

import android.util.Log;

import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.http.core.ObservableTask;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.manager.UserManager;

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

    public static Connection openConnection(String url) {
        Connection connection = ZHttp.get(url);
        connection.data("versioncode", VERSION_CODE);
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

    public static ObservableTask<Document> connect(String url) {
        return openConnection(url).toHtml();
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
        return connect("http://tt.shouji.com.cn/androidv3/yyj_tj_xml.jsp");
    }

    public static ObservableTask<Document> bannerApi() {
        return connect("http://tt.tljpxm.com/androidv3/app_index_xml.jsp?index=1");
    }

    public static ObservableTask<Document> recentUpdateAppApi() {
        return connect("http://tt.shouji.com.cn/androidv3/app_list_xml.jsp?index=1");
    }

    public static ObservableTask<Document> homeRecommendSoftApi() {
        return connect("http://tt.shouji.com.cn/androidv3/special_list_xml.jsp?id=-9998");
    }

    public static ObservableTask<Document> homeRecommendGameApi() {
        return connect("http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sdk=100&sort=day");
    }

    public static ObservableTask<Document> subjectApi() {
        return connect("http://tt.tljpxm.com/androidv3/app_index_xml.jsp?index=1");
    }

    public static ObservableTask<Document> recentUpdateSoft() {
        return connect("http://tt.shouji.com.cn/androidv3/soft_index_xml.jsp?sort=time");
    }

    public static ObservableTask<Document> recentUpdateGame() {
        return connect("http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sort=time");
    }

    public static ObservableTask<Document> netGame() {
        return connect("http://tt.shouji.com.cn/androidv3/netgame.jsp");
    }

    public static String myCollectionAppsUrl() {
        return getCollectUrl("user_fav_index_xml_v2");
    }

    public static String myCollectionsUrl() {
        return getCollectUrl("user_yyj_fav_xml");
    }

    public static String myCollectionDiscoverUrl() {
        return getCollectUrl("user_review_fav_xml_v2") + "&t=discuss";
    }

    public static String myCollectionCommentUrl() {
        return getCollectUrl("user_review_fav_xml_v2") + "&t=reivew";
    }

    public static String myCollectionWallpaperUrl() {
        return getCollectUrl("user_letu_fav_xml_v2");
    }

    public static String myCollectionSubjectUrl() {
        return getCollectUrl("user_special_fav_index");
    }

    public static String myCollectionGameTutorialUrl() {
        return getCollectUrl("user_article_fav_list") + "&type=game";
    }

    public static String myCollectionSoftTutorialUrl() {
        return getCollectUrl("user_article_fav_list") + "&type=soft";
    }

    private static String getCollectUrl(String key) {
        return "http://tt.shouji.com.cn/app/" + key + ".jsp";
    }

    public static ObservableTask<Document> likeApi(String type, String id) {
        String url = "http://tt.shouji.com.cn/app/comment_flower_xml.jsp?t=" + type + "&id=" + id;
        return connect(url);
    }

    public static ObservableTask<Document> addFriendApi(String id) {
        String url = "http://tt.tljpxm.com/xml/addfriendprocess?memberid=" + id;
        return connect(url);
    }

    public static ObservableTask<Document> deleteFriendApi(String id) {
        String url = "http://tt.tljpxm.com/xml/deletefriendprocess?friendMemberID=" + id;
        return connect(url);
    }

    public static ObservableTask<Document> getMemberInfoApi(String id) {
        String url = "http://tt.tljpxm.com/app/view_member_xml_v4.jsp?id=" + id;
        return connect(url);
    }

    public static ObservableTask<Document> blacklistApi(String id, boolean isAdd) {
        String url = String.format("http://tt.tljpxm.com/app/user_blacklist_add.jsp?mid=%s&t=%s", id, isAdd ? "add" : "del");
        return connect(url);
    }

    public static ObservableTask<Document> addBlacklistApi(String id) {
//        String url = "http://tt.tljpxm.com/app/user_blacklist_add.jsp?t=add&mid=" + id;
//        return connect(url);
        return blacklistApi(id, true);
    }

    public static ObservableTask<Document> removeBlacklistApi(String id) {
        return blacklistApi(id, false);
    }


}
