package com.zpj.shouji.market.api;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.GuessAppInfo;
import com.zpj.shouji.market.model.QuickAppInfo;
import com.zpj.utils.Callback;
import com.zpj.utils.ContextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchApi {

    private static final List<String> HOT_KEYWORD_LIST = new ArrayList<>();
    private static boolean flag;


    public static void getHotKeywordApi(Callback<List<String>> callback) {
        if (flag && !HOT_KEYWORD_LIST.isEmpty()) {
            callback.onCallback(HOT_KEYWORD_LIST);
            return;
        }
        returnDefaultTags(callback);
        HttpApi.getXml("http://tt.shouji.com.cn/app/user_app_search_rm_xml.jsp?searchKey=")
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
        HttpApi.getXml("http://tt.shouji.com.cn/appv3/user_app_search_rm_xml.jsp?searchKey=")
                .onSuccess(data -> {
                    List<GuessAppInfo> list = new ArrayList<>();
                    for (Element item : data.select("item").get(1).select("subitem")) {
                        list.add(GuessAppInfo.parse(item));
                    }
                    callback.onCallback(list);
                })
                .onError(throwable -> callback.onCallback(new ArrayList<>()))
                .subscribe();
    }

    public static void getQuickApi(String keyword, Callback<List<QuickAppInfo>> callback) {
        HttpApi.getHtml("http://tt.shouji.com.cn/androidv4/app_search_quick_xml.jsp?s=" + keyword)
                .onSuccess(data -> {
                    List<QuickAppInfo> list = new ArrayList<>();
                    for (Element item : data.select("item")) {
                        list.add(QuickAppInfo.parse(item));
                    }
                    callback.onCallback(list);
                })
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
