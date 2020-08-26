package com.zpj.shouji.market.ui.fragment.recommond;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.manager.AppManagerFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchFragment;
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
        if (getHeaderLayoutId() > 0) {
            View header = LayoutInflater.from(context).inflate(getHeaderLayoutId(), null, false);
            llContainer.addView(header);
        }
    }

    @Override
    public void toolbarRightCustomView(@NonNull View view) {
        view.findViewById(R.id.btn_manage).setOnClickListener(v -> AppManagerFragment.start());
        view.findViewById(R.id.btn_search).setOnClickListener(v -> SearchFragment.start());
    }

    protected abstract int getHeaderLayoutId();

    protected void addCard(RecommendCard recommendCard) {
        llContainer.addView(recommendCard);
    }

}
