package com.zpj.shouji.market.ui.fragment.recommond;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.GroupItem;
import com.zpj.shouji.market.ui.widget.recommend.CollectionRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.GameRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.SoftRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.SoftUpdateRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.TutorialRecommendCard;
import com.zpj.utils.ScreenUtils;

import java.util.List;

import www.linwg.org.lib.LCardView;

public class SoftRecommendFragment2 extends BaseRecommendFragment2 {

    private static final String TAG = "RecommendFragment";

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        postDelayed(() -> {
            addCard(new SoftUpdateRecommendCard(context));
            addCard(new CollectionRecommendCard(context));
            addCard(new SoftRecommendCard(context));
            for (int i = 1; i < 5; i++) {
                addCard(new TutorialRecommendCard(context, "soft", i));
            }
        }, 500);
    }

}
