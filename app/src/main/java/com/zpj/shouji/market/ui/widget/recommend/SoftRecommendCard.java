package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;

import com.zpj.shouji.market.api.HttpPreLoader;

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
    public String getKey() {
        return HttpPreLoader.HOME_SOFT;
    }

}
