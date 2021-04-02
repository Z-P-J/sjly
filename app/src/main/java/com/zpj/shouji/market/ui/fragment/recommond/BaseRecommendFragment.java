package com.zpj.shouji.market.ui.fragment.recommond;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.geek.banner.Banner;
import com.zpj.blur.ZBlurry;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.recyclerview.MultiData;
import com.zpj.recyclerview.MultiRecyclerViewWrapper;
import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.adapter.FooterViewHolder;
import com.zpj.shouji.market.ui.fragment.base.RecyclerLayoutFragment;
import com.zpj.shouji.market.ui.fragment.base.StateFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.manager.ManagerFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchFragment;
import com.zpj.shouji.market.ui.widget.RecommendBanner;
import com.zpj.shouji.market.utils.EventBus;
import com.zxy.skin.sdk.SkinEngine;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecommendFragment extends StateFragment
        implements IEasy.OnBindHeaderListener {

    private static final String TAG = "BaseRecommendFragment2";

    private final List<AppInfo> bannerItemList = new ArrayList<>();

    private View header;
    protected Banner banner;

    private RecyclerView recyclerView;

    private ZBlurry blurred;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_recomment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.onMainActionEvent(this, new RxBus.SingleConsumer<Boolean>() {
            @Override
            public void onAccept(Boolean isShow) throws Exception {
                if (isSupportVisible() && banner != null) {
                    if (isShow) {
                        banner.stopAutoPlay();
                    } else {
                        banner.startAutoPlay();
                    }
                }
            }
        });
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        recyclerView = findViewById(R.id.recycler_view);
        View shadowView = findViewById(R.id.shadow_view);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.canScrollVertically(-1)) {
                    shadowView.setAlpha(1f);
                } else {
                    shadowView.setAlpha(0f);
                }
            }
        });

        blurred = ZBlurry.with(recyclerView)
                .scale(0.1f)
                .radius(20)
//                .maxFps(40)
                .blur(toolbar, new ZBlurry.Callback() {
                    @Override
                    public void down(Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(bitmap);
                        drawable.setAlpha(recyclerView.canScrollVertically(-1) ? 255 : 0);
                        toolbar.setBackground(drawable, true);
                    }
                });
        blurred.pauseBlur();

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

        header = LayoutInflater.from(context).inflate(getHeaderLayoutId(), null, false);
        banner = header.findViewById(R.id.banner2);
        banner.setBannerLoader(new RecommendBanner.AppBannerLoader());

        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {
            @Override
            public void onBannerClick(int position) {
                AppDetailFragment.start(bannerItemList.get(position));
            }
        });
        initHeader(new EasyViewHolder(header));

        toolbar.setLeftButtonTint(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void toolbarRightCustomView(@NonNull View view) {
        ImageButton btnManage = view.findViewById(R.id.btn_manage);
        ImageButton btnSearch = view.findViewById(R.id.btn_search);
        SkinEngine.setTint(btnManage, R.attr.textColorMajor);
        SkinEngine.setTint(btnSearch, R.attr.textColorMajor);
        btnManage.setOnClickListener(v -> ManagerFragment.start());
        btnSearch.setOnClickListener(v -> SearchFragment.start());
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
//        if (blurred != null) {
//            blurred.startBlur();
//        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (blurred != null) {
                    blurred.startBlur();
                }
                if (banner != null) {
                    banner.startAutoPlay();
                }
            }
        }, 360);
//        if (banner != null) {
//            banner.startAutoPlay();
//        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (blurred != null) {
            blurred.pauseBlur();
        }
        if (banner != null) {
            banner.stopAutoPlay();
        }
    }

    @Override
    protected void onRetry() {
        super.onRetry();
        new EasyViewHolder(header);
    }

    //    @Subscribe
//    public void onMainActionPopupEvent(MainActionPopupEvent event) {
//        if (isSupportVisible() && banner != null) {
//            if (event.isShow()) {
//                banner.stopAutoPlay();
//            } else {
//                banner.startAutoPlay();
//            }
//        }
//    }

    protected void initData(List<AppInfo> list) {
        bannerItemList.clear();
        bannerItemList.addAll(list);
        banner.loadImagePaths(bannerItemList);
        banner.startAutoPlay();

        List<MultiData<?>> multiDataList = new ArrayList<>();

        initMultiData(multiDataList);

        MultiRecyclerViewWrapper.with(recyclerView)
                .setData(multiDataList)
                .setFooterViewBinder(new FooterViewHolder(true))
                .setHeaderView(header)
                .build();
        showContent();
    }

    protected abstract int getHeaderLayoutId();

    @Override
    public final void onBindHeader(EasyViewHolder holder) {
        if (banner != null) {
            return;
        }
        banner = holder.getView(R.id.banner2);
        banner.setBannerLoader(new RecommendBanner.AppBannerLoader());

        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {
            @Override
            public void onBannerClick(int position) {
                AppDetailFragment.start(bannerItemList.get(position));
            }
        });
        initHeader(holder);
    }

    protected abstract void initHeader(EasyViewHolder holder);

    protected abstract void initMultiData(List<MultiData<?>> list);

}
