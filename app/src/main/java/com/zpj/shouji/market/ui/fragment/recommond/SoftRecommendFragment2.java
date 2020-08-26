package com.zpj.shouji.market.ui.fragment.recommond;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.ToolBarListFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionRecommendListFragment;
import com.zpj.shouji.market.ui.widget.recommend.CollectionRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.SoftRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.SoftUpdateRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.TutorialRecommendCard;

public class SoftRecommendFragment2 extends BaseRecommendFragment2 implements View.OnClickListener {

    private static final String TAG = "RecommendFragment";
    private static final String[] TITLES = {"软件新闻", "软件评测", "软件教程", "软件周刊"};

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.layout_header_recommend_soft;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        view.findViewById(R.id.tv_necessary).setOnClickListener(this);
        view.findViewById(R.id.tv_collection).setOnClickListener(this);
        view.findViewById(R.id.tv_rank).setOnClickListener(this);
        view.findViewById(R.id.tv_classification).setOnClickListener(this);
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_necessary:
                ToolBarListFragment.startNecessarySoftList();
                break;
            case R.id.tv_collection:
                CollectionRecommendListFragment.start();
                break;
            case R.id.tv_rank:
                AppRankFragment.startSoft();
                break;
            case R.id.tv_classification:
                AppClassificationFragment.startSoft();
                break;
        }
    }
}
