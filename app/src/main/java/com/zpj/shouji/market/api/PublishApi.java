package com.zpj.shouji.market.api;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.Callback;
import com.zpj.utils.ContextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PublishApi {

    private static final List<String> publishTags = new ArrayList<>(0);
    private static boolean flag;

    private PublishApi() {

    }

    public static void getPublishTags(Callback<List<String>> callback) {
        if (flag && !publishTags.isEmpty()) {
            callback.onCallback(publishTags);
            return;
        }
        returnDefaultTags(callback);
        HttpApi.get("http://tt.shouji.com.cn/androidv3/publish_tag.jsp")
                .onSuccess(data -> {
                    Elements elements = data.select("item");
                    publishTags.clear();
                    for (Element item : elements) {
                        publishTags.add(item.selectFirst("title").text());
                    }
                    flag = true;
                    callback.onCallback(publishTags);
                })
                .onError(throwable -> {
                    returnDefaultTags(callback);
                })
                .subscribe();
    }

    private static void returnDefaultTags(Callback<List<String>> callback) {
        String[] tags = ContextUtils.getApplicationContext().getResources().getStringArray(R.array.default_publish_tags);
        publishTags.clear();
        publishTags.addAll(Arrays.asList(tags));
        callback.onCallback(publishTags);
    }

}
