package com.zpj.shouji.market.utils;

import android.util.Log;

import com.zpj.http.ZHttp;
import com.zpj.http.core.ObservableTask;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public final class HttpApi {

    private static final String USER_AGENT = "okhttp/3.0.1";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String VALUE_ACCEPT_ENCODING = "gzip";

    private HttpApi() {

    }

    public static ObservableTask<Document> connect(String url) {
        return ZHttp.get(url)
                .userAgent(USER_AGENT)
                .onRedirect(new IHttp.OnRedirectListener() {
                    @Override
                    public boolean onRedirect(String redirectUrl) {
                        Log.d("connect", "onRedirect redirectUrl=" + redirectUrl);
                        return true;
                    }
                })
                .cookie(UserManager.getCookie())
//                .header(HEADER_ACCEPT_ENCODING, VALUE_ACCEPT_ENCODING)
                .referer(url)
                .ignoreContentType(true)
                .toHtml();
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
        return connect("http://tt.shouji.com.cn/androidv3/app_index_xml.jsp?index=1&versioncode=198");
    }

    public static ObservableTask<Document> recentUpdateAppApi() {
        return connect("http://tt.shouji.com.cn/androidv3/app_list_xml.jsp?index=1&versioncode=198");
    }

    public static ObservableTask<Document> homeRecommendSoftApi() {
        return connect("http://tt.shouji.com.cn/androidv3/special_list_xml.jsp?id=-9998");
    }

    public static ObservableTask<Document> homeRecommendGameApi() {
        return connect("http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sdk=100&sort=day");
    }
    public static ObservableTask<Document> subjectApi() {
        return connect("http://tt.shouji.com.cn/androidv3/app_index_xml.jsp?index=1&versioncode=198");
    }


}
