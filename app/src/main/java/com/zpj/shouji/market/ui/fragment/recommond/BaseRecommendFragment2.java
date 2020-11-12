package com.zpj.shouji.market.ui.fragment.recommond;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geek.banner.Banner;
import com.zpj.blur.ZBlurry;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.MainActionPopupEvent;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.manager.ManagerFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchFragment;
import com.zpj.shouji.market.ui.widget.SmartNestedScrollView;
import com.zpj.shouji.market.ui.widget.recommend.AppBannerLoader;
import com.zpj.shouji.market.ui.widget.recommend.RecommendCard;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.statelayout.StateLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class BaseRecommendFragment2 extends SkinFragment
        implements SmartNestedScrollView.ISmartScrollChangedListener {

    private static final String TAG = "BaseRecommendFragment2";

    protected final Queue<RecommendCard> recommendCardList = new LinkedList<>();

    private final List<AppInfo> bannerItemList = new ArrayList<>();

    protected StateLayout stateLayout;
    protected SmartNestedScrollView scrollView;

    protected View loadingFooter;

    protected Banner banner;

    protected LinearLayout llContainer;

    private ZBlurry blurred;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_recomment;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {


        llContainer = view.findViewById(R.id.ll_container);

        stateLayout = view.findViewById(R.id.state_layout);
        stateLayout.showLoadingView();

        scrollView = findViewById(R.id.scroll_view);
        scrollView.setScanScrollChangedListener(this);

        blurred = ZBlurry.with(stateLayout)
                .scale(0.1f)
                .radius(20)
//                .maxFps(40)
                .blur(toolbar, new ZBlurry.Callback() {
                    @Override
                    public void down(Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(bitmap);
                        drawable.setAlpha(scrollView.isScrolledToTop() ? 0 : 255);
                        toolbar.setBackground(drawable, true);
                    }
                });
        blurred.pauseBlur();

        loadingFooter = LayoutInflater.from(context).inflate(R.layout.item_footer_home, null, false);

        banner = view.findViewById(R.id.banner2);
        banner.setBannerLoader(new AppBannerLoader());

        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {
            @Override
            public void onBannerClick(int position) {
                AppDetailFragment.start(bannerItemList.get(position));
            }
        });

        ViewGroup.LayoutParams params = banner.getLayoutParams();
        int screenWidth = ScreenUtils.getScreenWidth(context);

        params.height = (int) ((float) screenWidth / 2f);

        if (getHeaderLayoutId() > 0) {
            View header = LayoutInflater.from(context).inflate(getHeaderLayoutId(), null, false);
            llContainer.addView(header);
        }
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
    }

    @Override
    public void toolbarRightCustomView(@NonNull View view) {
        view.findViewById(R.id.btn_manage).setOnClickListener(v -> ManagerFragment.start());
        view.findViewById(R.id.btn_search).setOnClickListener(v -> SearchFragment.start());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (blurred != null) {
            blurred.startBlur();
        }
        if (banner != null) {
            banner.startAutoPlay();
        }
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
    public void onScrolledToBottom() {
        if (recommendCardList.size() >= 2) {
            RecommendCard recommendCard = recommendCardList.remove();
            RecommendCard recommendCard2 = recommendCardList.remove();
            recommendCard2.loadData(null);
            recommendCard.loadData(() -> {
                addCard(recommendCard, false);
                addCard(recommendCard2, recommendCardList.isEmpty());
            });
        } else if (recommendCardList.size() == 1) {
            RecommendCard recommendCard = recommendCardList.remove();
            recommendCard.loadData(() -> addCard(recommendCard, true));
        }
    }

    @Override
    public void onScrolledToTop() {

    }

    @Subscribe
    public void onMainActionPopupEvent(MainActionPopupEvent event) {
        if (isSupportVisible() && banner != null) {
            if (event.isShow()) {
                banner.stopAutoPlay();
            } else {
                banner.startAutoPlay();
            }
        }
    }

    protected void initData(List<AppInfo> list) {
        bannerItemList.clear();
        bannerItemList.addAll(list);
        banner.loadImagePaths(bannerItemList);
        banner.startAutoPlay();
        stateLayout.showContentView();
    }

    protected abstract int getHeaderLayoutId();

    protected void addCard(RecommendCard recommendCard) {
        llContainer.removeView(loadingFooter);
        llContainer.addView(recommendCard);

//        loadingFooter.findViewById(R.id.ll_container_progress).setVisibility(View.GONE);
//        TextView tvMsg = loadingFooter.findViewById(R.id.tv_msg);
//        tvMsg.setVisibility(View.VISIBLE);

        llContainer.addView(loadingFooter);
    }

    protected void addCard(RecommendCard recommendCard, boolean hasNoMore) {
        llContainer.removeView(loadingFooter);
        llContainer.addView(recommendCard);

        if (hasNoMore) {
            loadingFooter.findViewById(R.id.ll_container_progress).setVisibility(View.GONE);
            TextView tvMsg = loadingFooter.findViewById(R.id.tv_msg);
            tvMsg.setVisibility(View.VISIBLE);
        }

        llContainer.addView(loadingFooter);
    }

}
