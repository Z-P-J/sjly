package com.zpj.shouji.market.api;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.utils.Callback;

import java.util.ArrayList;
import java.util.List;

public class SearchApi {

    private static final List<String> HOT_KEYWORD_LIST = new ArrayList<>();
    private static boolean flag;


    public static void getHotKeywordApi(Callback<List<String>> callback) {
        if (flag && !HOT_KEYWORD_LIST.isEmpty()) {
            callback.onCallback(HOT_KEYWORD_LIST);
            return;
        }
        HttpApi.get("http://tt.shouji.com.cn/app/user_app_search_rm_xml.jsp?searchKey=")
                .onSuccess(data -> {
                    for (Element item : data.select("item")) {
                        HOT_KEYWORD_LIST.add(item.selectFirst("name").text());
                    }
                    callback.onCallback(HOT_KEYWORD_LIST);
                    flag = true;
                })
                .subscribe();
    }

}
