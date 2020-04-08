package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;

import com.zpj.shouji.market.api.HttpPreLoader;

public class SimilarAppCard extends AppInfoRecommendCard {

    public SimilarAppCard(Context context) {
        this(context, null);
    }

    public SimilarAppCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimilarAppCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        setTitle("相似应用");
    }

    @Override
    public String getKey() {
        return null;
    }

}
