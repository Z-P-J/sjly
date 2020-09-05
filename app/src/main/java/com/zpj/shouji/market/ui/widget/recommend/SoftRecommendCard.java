package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;

public class SoftRecommendCard extends AppInfoRecommendCard {

    public SoftRecommendCard(Context context) {
        this(context, null);
    }

    public SoftRecommendCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoftRecommendCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTitle("应用推荐");
    }

    @Override
    public void onMoreClicked(View v) {
        ToolBarAppListFragment.startRecommendSoftList();
    }

    @Override
    public PreloadApi getKey() {
        return PreloadApi.HOME_SOFT;
    }

}
