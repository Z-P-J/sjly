package com.zpj.shouji.market.ui.fragment.recommond;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.manager.AppManagerFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchFragment;
import com.zpj.shouji.market.ui.widget.recommend.BannerViewHolder;
import com.zpj.shouji.market.ui.widget.recommend.RecommendBanner;
import com.zpj.shouji.market.ui.widget.recommend.RecommendCard;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecommendFragment2 extends BaseFragment {

    private static final String TAG = "BaseRecommendFragment2";

    private final List<AppInfo> bannerItemList = new ArrayList<>();

    protected MZBannerView<AppInfo> mMZBanner;

    private LinearLayout llContainer;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_recomment_2;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        llContainer = view.findViewById(R.id.ll_container);

        mMZBanner = view.findViewById(R.id.banner);
        mMZBanner.setDelayedTime(5 * 1000);
        mMZBanner.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int i) {
                AppDetailFragment.start(bannerItemList.get(i));
            }
        });
        ViewGroup.LayoutParams params = mMZBanner.getLayoutParams();
        int screenWidth = ScreenUtils.getScreenWidth(context);

//        params.height = (int) ((float) screenWidth * screenWidth / ScreenUtils.getScreenHeight(context));
        params.height = (int) ((float) screenWidth / 2f);

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

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (mMZBanner != null) {
            mMZBanner.start();
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (mMZBanner != null) {
            mMZBanner.pause();
        }
    }

    protected void initData(List<AppInfo> list) {
        bannerItemList.clear();
        bannerItemList.addAll(list);
        mMZBanner.setPages(bannerItemList, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
        mMZBanner.start();
    }

    protected abstract int getHeaderLayoutId();

    protected void addCard(RecommendCard recommendCard) {
        llContainer.addView(recommendCard);
    }

}
