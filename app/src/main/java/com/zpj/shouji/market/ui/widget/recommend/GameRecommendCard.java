package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;

import com.zpj.shouji.market.api.HttpPreLoader;

public class GameRecommendCard extends AppInfoRecommendCard {

    public GameRecommendCard(Context context) {
        this(context, null);
    }

    public GameRecommendCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameRecommendCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTitle("游戏推荐");
    }

    @Override
    public String getKey() {
        return HttpPreLoader.HOME_GAME;
    }

}
