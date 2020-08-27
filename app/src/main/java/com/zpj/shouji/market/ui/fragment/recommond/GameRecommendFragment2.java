package com.zpj.shouji.market.ui.fragment.recommond;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;
import com.zpj.http.ZHttp;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.ToolBarListFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionRecommendListFragment;
import com.zpj.shouji.market.ui.widget.recommend.GameRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.GameUpdateRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.NetGameRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.RecommendBanner;
import com.zpj.shouji.market.ui.widget.recommend.TutorialRecommendCard;

import java.util.ArrayList;
import java.util.List;

public class GameRecommendFragment2 extends BaseRecommendFragment2 implements View.OnClickListener {

    private static final String TAG = "GameRecommendFragment";
    private static final String[] TITLES = {"游戏快递", "游戏评测", "游戏攻略", "游戏新闻", "游戏周刊", "游戏公告"};

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.layout_header_recommend_game;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        view.findViewById(R.id.tv_booking).setOnClickListener(this);
        view.findViewById(R.id.tv_handpick).setOnClickListener(this);
        view.findViewById(R.id.tv_rank).setOnClickListener(this);
        view.findViewById(R.id.tv_classification).setOnClickListener(this);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

        ZHttp.get("https://game.shouji.com.cn/")
                .toHtml()
                .onSuccess(data -> {
                    List<AppInfo> list = new ArrayList<>();
                    Elements recommends = data.selectFirst("body > div:nth-child(5) > div.boutique.fl > ul").select("li");
                    for (Element recommend : recommends) {
                        Element a = recommend.selectFirst("a");
                        AppInfo info = new AppInfo();
                        info.setAppId(a.attr("href")
                                .replace("/game/", "")
                                .replace(".html", ""));
                        info.setAppIcon(a.selectFirst("img").attr("src"));
                        info.setAppTitle(a.attr("title"));
                        info.setAppType("game");
                        info.setAppSize(a.selectFirst("p").text().replace("大小：", ""));
                        list.add(info);
                    }
                    initData(list);
                })
                .onError(throwable -> {
                    throwable.printStackTrace();
                    AToast.error("出错了！" + throwable.getMessage());
                })
                .subscribe();

        postDelayed(() -> {
            // TODO 排行
            addCard(new GameUpdateRecommendCard(context));
            addCard(new GameRecommendCard(context));
            addCard(new NetGameRecommendCard(context));
            for (int i = 0; i < TITLES.length; i++) {
                TutorialRecommendCard card = new TutorialRecommendCard(context, "game", i + 1);
                card.setTitle(TITLES[i]);
                addCard(card);
            }
        }, 500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_booking:
                AToast.normal("TODO");
                break;
            case R.id.tv_handpick:
                AToast.normal("TODO");
                break;
            case R.id.tv_rank:
                AppRankFragment.startGame();
                break;
            case R.id.tv_classification:
                AppClassificationFragment.startGame();
                break;
        }
    }

}
