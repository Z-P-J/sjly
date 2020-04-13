package com.zpj.shouji.market.ui.fragment.recommond;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zpj.shouji.market.ui.widget.recommend.CollectionRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.SoftRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.SoftUpdateRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.TutorialRecommendCard;

public class SoftRecommendFragment2 extends BaseRecommendFragment2 {

    private static final String TAG = "RecommendFragment";
    private static final String[] TITLES = {"软件新闻", "软件评测", "软件教程", "软件周刊"};

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        postDelayed(() -> {
            // TODO 排行
            addCard(new SoftUpdateRecommendCard(context));
            addCard(new CollectionRecommendCard(context));
            SoftRecommendCard softRecommendCard = new SoftRecommendCard(context);
            softRecommendCard.setTitle("常用应用");
            addCard(softRecommendCard);
            for (int i = 0; i < TITLES.length; i++) {
                TutorialRecommendCard card = new TutorialRecommendCard(context, "soft", i + 1);
                card.setTitle(TITLES[i]);
                addCard(card);
            }
        }, 500);
    }

}
