package com.zpj.shouji.market.api;

import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class HttpPreLoader {

    private static HttpPreLoader LOADER;

    private final Map<PreloadApi, Document> map = new HashMap<>();
    private final Map<PreloadApi, WeakReference<OnLoadListener>> listeners = new HashMap<>();
    private final List<PreloadApi> preloadApiList = new ArrayList<>();

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

    public void onDestroy() {
        this.map.clear();
        this.listeners.clear();
        this.preloadApiList.clear();
        LOADER = null;
    }

//    public void load(String url) {
//        load(url, url);
//    }

    public void load(final PreloadApi key) {
        preloadApiList.add(key);
        HttpApi.get(key.getUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onSuccess(data -> {
                    synchronized (map) {
                        WeakReference<OnLoadListener> weakReference = listeners.remove(key);
                        if (weakReference != null && weakReference.get() != null) {
//                            Observable.empty()
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .doOnComplete(new Action() {
//                                        @Override
//                                        public void run() throws Exception {
//                                            weakReference.get().onLoad(data);
//                                        }
//                                    })
//                                    .subscribe();
                            weakReference.get().onLoad(data);
                        } else {
                            map.put(key, data);
                        }
                        preloadApiList.remove(key);
                    }
                })
                .onError(new IHttp.OnErrorListener() {
                    @Override
                    public void onError(Throwable throwable) {
                        preloadApiList.remove(key);
                    }
                })
                .subscribe();
    }

    public void loadHomepage() {
        HttpPreLoader.getInstance().load(PreloadApi.HOME_BANNER);
//        HttpPreLoader.getInstance().load(PreloadApi.HOME_RECENT);
//        HttpPreLoader.getInstance().load(PreloadApi.HOME_COLLECTION);
//        HttpPreLoader.getInstance().load(PreloadApi.HOME_SOFT);
//        HttpPreLoader.getInstance().load(PreloadApi.HOME_GAME);
//        HttpPreLoader.getInstance().load(PreloadApi.HOME_SUBJECT);
    }

    public void setLoadListener(PreloadApi key, OnLoadListener listener) {
        synchronized (map) {
            if (map.containsKey(key)) {
                listener.onLoad(map.remove(key));
            } else {
                listeners.put(key, new WeakReference<>(listener));
                if (!preloadApiList.contains(key)) {
                    HttpPreLoader.getInstance().load(key);
                }
            }
        }
    }

    public boolean hasKey(PreloadApi key) {
        synchronized (map) {
            return map.containsKey(key);
        }
    }

    public interface OnLoadListener {
        void onLoad(Document document);
    }

}
