package com.zpj.shouji.market.api;

import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.Callback;
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
        HttpApi.get("http://tt.shouji.com.cn/app/user_app_search_rm_xml.jsp?searchKey=")
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

    private static void returnDefaultTags(Callback<List<String>> callback) {
        String[] tags = ContextUtils.getApplicationContext().getResources().getStringArray(R.array.default_hot_keyword);
        HOT_KEYWORD_LIST.clear();
        HOT_KEYWORD_LIST.addAll(Arrays.asList(tags));
        callback.onCallback(HOT_KEYWORD_LIST);
    }

}
