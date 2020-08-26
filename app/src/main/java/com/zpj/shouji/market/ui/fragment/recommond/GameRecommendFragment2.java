package com.zpj.shouji.market.ui.fragment.recommond;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.ToolBarListFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionRecommendListFragment;
import com.zpj.shouji.market.ui.widget.recommend.GameRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.GameUpdateRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.NetGameRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.TutorialRecommendCard;

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
