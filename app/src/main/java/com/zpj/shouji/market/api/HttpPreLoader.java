package com.zpj.shouji.market.api;

import com.zpj.http.parser.html.nodes.Document;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.schedulers.Schedulers;

public class HttpPreLoader {

    public static final String HOME_BANNER = "home_banner";
    public static final String HOME_RECENT = "home_recent";
    public static final String HOME_COLLECTION = "home_collection";
    public static final String HOME_SOFT = "home_soft";
    public static final String HOME_GAME = "home_game";
    public static final String HOME_SUBJECT = "home_subject";

    private static HttpPreLoader LOADER;

    private final Map<String, Document> map = new HashMap<>();
    private final Map<String, WeakReference<OnLoadListener>> listeners = new HashMap<>();

    public static HttpPreLoader getInstance() {
        if (LOADER == null) {
            synchronized (HttpPreLoader.class) {
                if (LOADER == null) {
                    LOADER = new HttpPreLoader();
                }
            }
        }
        return LOADER;
    }

    private HttpPreLoader() {

    }

    public void load(String url) {
        load(url, url);
    }

    public void load(final String key, final String url) {
        HttpApi.connect(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .onSuccess(data -> {
                    synchronized (map) {
                        WeakReference<OnLoadListener> weakReference = listeners.remove(key);
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onLoad(data);
                        } else {
                            map.put(key, data);
                        }
                    }
                })
                .subscribe();
    }

    public void loadHomepage() {
        HttpPreLoader.getInstance().load(HOME_BANNER, "http://tt.tljpxm.com/androidv3/app_index_xml.jsp?index=1&versioncode=198");
        HttpPreLoader.getInstance().load(HOME_RECENT, "http://tt.shouji.com.cn/androidv3/app_list_xml.jsp?index=1&versioncode=198");
        HttpPreLoader.getInstance().load(HOME_COLLECTION, "http://tt.shouji.com.cn/androidv3/yyj_tj_xml.jsp");
        HttpPreLoader.getInstance().load(HOME_SOFT, "http://tt.shouji.com.cn/androidv3/special_list_xml.jsp?id=-9998");
        HttpPreLoader.getInstance().load(HOME_GAME, "http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sdk=100&sort=day");
        HttpPreLoader.getInstance().load(HOME_SUBJECT, "http://tt.shouji.com.cn/androidv3/special_index_xml.jsp?jse=yes");
    }

    public void setLoadListener(String key, OnLoadListener listener) {
        synchronized (map) {
            if (map.containsKey(key)) {
                listener.onLoad(map.remove(key));
            } else {
                listeners.put(key, new WeakReference<>(listener));
            }
        }
    }

    public boolean hasKey(String key) {
        synchronized (map) {
            return map.containsKey(key);
        }
    }

    public interface OnLoadListener {
        void onLoad(Document document);
    }

}
