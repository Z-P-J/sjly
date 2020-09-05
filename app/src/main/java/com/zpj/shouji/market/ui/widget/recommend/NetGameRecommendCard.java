package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;

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

    }

    @Override
    public void loadData(Runnable runnable) {
        HttpApi.netGame()
                .onSuccess(document -> {
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
                    if (runnable != null) {
                        runnable.run();
                    }
                })
                .subscribe();
    }

    @Override
    public void onMoreClicked(View v) {
        ToolBarAppListFragment.startNetGameList();
    }

    @Override
    public PreloadApi getKey() {
        return null;
    }

}
