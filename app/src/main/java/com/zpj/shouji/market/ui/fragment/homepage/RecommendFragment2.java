package com.zpj.shouji.market.ui.fragment.homepage;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shehuan.niv.NiceImageView;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZViewHolder;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.blur.BlurTransformation;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.model.GroupItem;
import com.zpj.shouji.market.model.SubjectInfo;
import com.zpj.shouji.market.ui.fragment.AppListFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.ui.widget.recommend.CollectionRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.GameRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.RecommendBanner;
import com.zpj.shouji.market.ui.widget.recommend.SoftRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.SubjectRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.UpdateRecommendCard;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import www.linwg.org.lib.LCardView;

public class RecommendFragment2 extends BaseFragment {

    private static final String TAG = "RecommendFragment";

    private final int[] RES_ICONS = {R.id.item_icon_1, R.id.item_icon_2, R.id.item_icon_3};

    private RecommendBanner mBanner;
    private UpdateRecommendCard updateRecommendCard;
    private CollectionRecommendCard collectionRecommendCard;
    private SoftRecommendCard softRecommendCard;
    private GameRecommendCard gameRecommendCard;
    private SubjectRecommendCard subjectRecommendCard;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recommend;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        LinearLayout llContainer = view.findViewById(R.id.ll_container);
        mBanner = view.findViewById(R.id.rb_banner);
        updateRecommendCard = view.findViewById(R.id.rc_update);
        collectionRecommendCard = view.findViewById(R.id.rc_collection);
        softRecommendCard = view.findViewById(R.id.rc_soft);
        gameRecommendCard = view.findViewById(R.id.rc_game);
        subjectRecommendCard = view.findViewById(R.id.rc_subject);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBanner != null) {
            mBanner.onResume();
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
