package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;

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
    public void onMoreClicked(View v) {
        ToolBarAppListFragment.startRecommendGameList();
    }

    @Override
    public PreloadApi getKey() {
        return PreloadApi.HOME_GAME;
    }

}
