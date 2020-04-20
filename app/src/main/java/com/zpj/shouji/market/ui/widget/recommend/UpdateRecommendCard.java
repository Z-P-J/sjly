package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.ui.fragment.ToolBarListFragment;

public class UpdateRecommendCard extends AppInfoRecommendCard {

    public UpdateRecommendCard(Context context) {
        this(context, null);
    }

    public UpdateRecommendCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UpdateRecommendCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMoreClicked(View v) {
        ToolBarListFragment.startRecentUpdate();
    }

    @Override
    public String getKey() {
        return HttpPreLoader.HOME_RECENT;
    }

}
