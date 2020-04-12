package com.zpj.shouji.market.ui.fragment.homepage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.widget.recommend.CollectionRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.GameRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.RecommendBanner;
import com.zpj.shouji.market.ui.widget.recommend.SoftRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.SubjectRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.UpdateRecommendCard;

public class RecommendFragment2 extends BaseFragment {

    private static final String TAG = "RecommendFragment";

//    private final int[] RES_ICONS = {R.id.item_icon_1, R.id.item_icon_2, R.id.item_icon_3};

    private RecommendBanner mBanner;
//    private UpdateRecommendCard updateRecommendCard;
//    private CollectionRecommendCard collectionRecommendCard;
//    private SoftRecommendCard softRecommendCard;
//    private GameRecommendCard gameRecommendCard;
//    private SubjectRecommendCard subjectRecommendCard;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recommend;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
//        LinearLayout llContainer = view.findViewById(R.id.ll_container);
        mBanner = view.findViewById(R.id.rb_banner);
//        updateRecommendCard = view.findViewById(R.id.rc_update);
//        collectionRecommendCard = view.findViewById(R.id.rc_collection);
//        softRecommendCard = view.findViewById(R.id.rc_soft);
//        gameRecommendCard = view.findViewById(R.id.rc_game);
//        subjectRecommendCard = view.findViewById(R.id.rc_subject);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (mBanner != null) {
            mBanner.onResume();
//            postDelayed(() -> mBanner.onResume(), 500);
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (mBanner != null) {
            mBanner.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBanner != null) {
            mBanner.onResume();
//            postDelayed(() -> mBanner.onResume(), 500);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBanner != null) {
            mBanner.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBanner != null) {
            mBanner.onStop();
        }
    }

}
