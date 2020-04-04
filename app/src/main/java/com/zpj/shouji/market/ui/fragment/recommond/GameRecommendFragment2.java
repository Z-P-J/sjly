package com.zpj.shouji.market.ui.fragment.recommond;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zpj.shouji.market.ui.widget.recommend.GameRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.GameUpdateRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.NetGameRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.TutorialRecommendCard;

public class GameRecommendFragment2 extends BaseRecommendFragment2 {

    private static final String TAG = "GameRecommendFragment";
    private static final String[] TITLES = {"游戏快递", "游戏评测", "游戏攻略", "游戏新闻", "游戏周刊", "游戏公告"};

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

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
}
