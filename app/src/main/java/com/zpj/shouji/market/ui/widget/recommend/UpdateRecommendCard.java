package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;

public class UpdateRecommendCard extends AppInfoRecommendCard {

    public UpdateRecommendCard(Context context) {
        this(context, null);
    }

    public UpdateRecommendCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UpdateRecommendCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTitle("最近更新");
    }

//    @Override
//    public void loadData(Runnable runnable) {
//        HttpApi.get(PreloadApi.HOME_RECENT.getUrl())
//                .onSuccess(document -> onGetDoc(document, runnable))
//                .subscribe();
//    }

    @Override
    public void onMoreClicked(View v) {
        ToolBarAppListFragment.startRecentUpdate();
    }

    @Override
    public PreloadApi getKey() {
        return PreloadApi.HOME_RECENT;
//        return null;
    }

}
