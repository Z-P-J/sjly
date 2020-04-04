package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;

import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.model.AppInfo;

public class SoftUpdateRecommendCard extends AppInfoRecommendCard {

    public SoftUpdateRecommendCard(Context context) {
        this(context, null);
    }

    public SoftUpdateRecommendCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoftUpdateRecommendCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTitle("最近更新");
        HttpApi.recentUpdateSoft().onSuccess(document -> {
            Elements elements = document.select("item");
            for (Element element : elements) {
                AppInfo info = AppInfo.parse(element);
                if (info == null) {
                    continue;
                }
                list.add(info);
                if (list.size() == 8) {
                    break;
                }
            }
            recyclerView.notifyDataSetChanged();
        })
                .subscribe();
    }

    @Override
    public String getKey() {
        return null;
    }


}
