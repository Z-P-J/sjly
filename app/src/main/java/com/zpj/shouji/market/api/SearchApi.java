package com.zpj.shouji.market.api;

import com.zpj.http.core.HttpObserver;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.GuessAppInfo;
import com.zpj.shouji.market.model.QuickAppInfo;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.utils.Callback;
import com.zpj.utils.ContextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.ObservableEmitter;

public class SearchApi {

    private static final List<String> HOT_KEYWORD_LIST = new ArrayList<>();
    private static boolean flag;


    public static void getHotKeywordApi(Callback<List<String>> callback) {
        if (flag && !HOT_KEYWORD_LIST.isEmpty()) {
            callback.onCallback(HOT_KEYWORD_LIST);
            return;
        }
        returnDefaultTags(callback);
        HttpApi.getXml("/app/user_app_search_rm_xml.jsp?searchKey=")
                .onSuccess(data -> {
                    HOT_KEYWORD_LIST.clear();
                    for (Element item : data.select("item")) {
                        HOT_KEYWORD_LIST.add(item.selectFirst("name").text());
                    }
                    flag = true;
                    callback.onCallback(HOT_KEYWORD_LIST);
                })
                .onError(throwable -> returnDefaultTags(callback))
                .subscribe();
    }

    public static void getGuessApi(Callback<List<GuessAppInfo>> callback) {
        HttpApi.getXml("/appv3/user_app_search_rm_xml.jsp?searchKey=")
                .flatMap((HttpObserver.OnFlatMapListener<Document, List<GuessAppInfo>>) (data, emitter) -> {
                    List<GuessAppInfo> list = new ArrayList<>();
                    for (Element item : data.select("item").get(1).select("subitem")) {
                        GuessAppInfo info = BeanUtils.createBean(item, GuessAppInfo.class);
                        info.init();
                        list.add(info);
                    }
                    emitter.onNext(list);
                    emitter.onComplete();
                })
                .onSuccess(callback::onCallback)
                .onError(throwable -> callback.onCallback(new ArrayList<>()))
                .subscribe();
    }

    public static void getQuickApi(String keyword, Callback<List<QuickAppInfo>> callback) {
        HttpApi.getHtml("/androidv4/app_search_quick_xml.jsp?s=" + keyword)
                .flatMap((HttpObserver.OnFlatMapListener<Document, List<QuickAppInfo>>) (data, emitter) -> {
                    List<QuickAppInfo> list = new ArrayList<>();
                    for (Element item : data.select("item")) {
                        QuickAppInfo info = BeanUtils.createBean(item, QuickAppInfo.class);
                        info.init();
                        list.add(info);
                    }
                    emitter.onNext(list);
                    emitter.onComplete();
                })
                .onSuccess(callback::onCallback)
                .onError(throwable -> callback.onCallback(new ArrayList<>()))
                .subscribe();
    }

    private static void returnDefaultTags(Callback<List<String>> callback) {
        String[] tags = ContextUtils.getApplicationContext().getResources().getStringArray(R.array.default_hot_keyword);
        HOT_KEYWORD_LIST.clear();
        HOT_KEYWORD_LIST.addAll(Arrays.asList(tags));
        callback.onCallback(HOT_KEYWORD_LIST);
    }

}
