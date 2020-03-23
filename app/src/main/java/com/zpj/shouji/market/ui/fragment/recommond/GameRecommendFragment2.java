package com.zpj.shouji.market.ui.fragment.recommond;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.GroupItem;
import com.zpj.shouji.market.ui.widget.recommend.GameRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.GameUpdateRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.TutorialRecommendCard;
import com.zpj.utils.ScreenUtils;

import java.util.List;

import www.linwg.org.lib.LCardView;

public class GameRecommendFragment2 extends BaseRecommendFragment2 {

    private static final String TAG = "GameRecommendFragment";

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        postDelayed(() -> {
            addCard(new GameUpdateRecommendCard(context));
            addCard(new GameRecommendCard(context));
            for (int i = 1; i < 7; i++) {
                addCard(new TutorialRecommendCard(context, "game", i));
            }
        }, 500);
    }
}
