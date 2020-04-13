package com.zpj.shouji.market.ui.fragment.recommond;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.widget.recommend.RecommendCard;

public abstract class BaseRecommendFragment2 extends BaseFragment {

    private static final String TAG = "BaseRecommendFragment2";

    private LinearLayout llContainer;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_recomment_2;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        llContainer = view.findViewById(R.id.ll_container);
    }

    protected void addCard(RecommendCard recommendCard) {
        llContainer.addView(recommendCard);
    }

}
