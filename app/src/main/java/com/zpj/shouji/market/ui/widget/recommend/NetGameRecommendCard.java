package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.ToolBarListFragment;

public class NetGameRecommendCard extends AppInfoRecommendCard {

    public NetGameRecommendCard(Context context) {
        this(context, null);
    }

    public NetGameRecommendCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetGameRecommendCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTitle("热门网游");
        HttpApi.netGame().onSuccess(new IHttp.OnSuccessListener<Document>() {
            @Override
            public void onSuccess(Document document) throws Exception {
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
            }
        })
                .subscribe();
    }

    @Override
    public void onMoreClicked(View v) {
        ToolBarListFragment.startNetGameList();
    }

    @Override
    public PreloadApi getKey() {
        return null;
    }

}
