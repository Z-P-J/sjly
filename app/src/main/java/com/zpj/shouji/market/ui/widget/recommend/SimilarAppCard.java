package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.api.PreloadApi;

public class SimilarAppCard extends AppInfoRecommendCard {

    public SimilarAppCard(Context context) {
        this(context, null);
    }

    public SimilarAppCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimilarAppCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTitle("相似应用");
    }

    @Override
    public void onMoreClicked(View v) {
        AToast.normal("TODO");
    }

    @Override
    public PreloadApi getKey() {
        return null;
    }

}
