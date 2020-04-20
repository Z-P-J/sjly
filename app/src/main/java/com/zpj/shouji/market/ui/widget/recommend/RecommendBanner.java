package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZViewHolder;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.SubjectRecommendListFragment;
import com.zpj.shouji.market.ui.fragment.ToolBarListFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionRecommendListFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class RecommendBanner extends LinearLayout implements View.OnClickListener {

    private final List<AppInfo> bannerItemList = new ArrayList<>();
    private final BannerViewHolder bannerViewHolder = new BannerViewHolder();

    private final MZBannerView<AppInfo> mMZBanner;

    public RecommendBanner(Context context) {
        this(context, null);
    }

    public RecommendBanner(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecommendBanner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_recommend_header, this, true);
        mMZBanner = findViewById(R.id.banner);
        mMZBanner.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int i) {
                EventBus.getDefault().post(AppDetailFragment.newInstance(bannerItemList.get(i)));
            }
        });

        HttpPreLoader.getInstance().setLoadListener(HttpPreLoader.HOME_BANNER, document -> {
            Elements elements = document.select("item");
            bannerItemList.clear();
            for (Element element : elements) {
                AppInfo info = AppInfo.parse(element);
                if (info == null) {
                    continue;
                }
                bannerItemList.add(info);
            }
            mMZBanner.setPages(bannerItemList, () -> bannerViewHolder);

            mMZBanner.start();
        });

        findViewById(R.id.tv_common_app).setOnClickListener(this);
        findViewById(R.id.tv_recent_download).setOnClickListener(this);
        findViewById(R.id.tv_subjects).setOnClickListener(this);
        findViewById(R.id.tv_collections).setOnClickListener(this);
    }

    public void onResume() {
        if (mMZBanner != null) {
            mMZBanner.start();
        }
    }

    public void onPause() {
        if (mMZBanner != null) {
            mMZBanner.pause();
        }
    }

    public void onStop() {
        if (mMZBanner != null) {
            mMZBanner.pause();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_app:
                ToolBarListFragment.startRecommendSoftList();
                break;
            case R.id.tv_recent_download:
                ToolBarListFragment.startRecentDownload();
                break;
            case R.id.tv_subjects:
                SubjectRecommendListFragment.start("http://tt.shouji.com.cn/androidv3/special_index_xml.jsp?jse=yes");
                break;
            case R.id.tv_collections:
                CollectionRecommendListFragment.start("http://tt.shouji.com.cn/androidv3/yyj_tj_xml.jsp");
                break;
        }
    }

    private static class BannerViewHolder implements MZViewHolder<AppInfo> {
        private ImageView mImageView;

        @Override
        public View createView(Context context) {
            // 返回页面布局
            View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null, false);
            mImageView = view.findViewById(R.id.img_view);
            return view;
        }

        @Override
        public void onBind(Context context, int position, AppInfo item) {
            Glide.with(context).load(item.getAppIcon()).into(mImageView);
        }
    }

}
