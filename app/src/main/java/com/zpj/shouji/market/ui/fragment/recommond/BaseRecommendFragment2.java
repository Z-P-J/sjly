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

import com.geek.banner.Banner;
import com.zpj.blur.ZBlurry;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.recyclerview.MultiData;
import com.zpj.recyclerview.MultiRecyclerViewWrapper;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.MainActionPopupEvent;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.manager.ManagerFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchFragment;
import com.zpj.shouji.market.ui.widget.recommend.AppBannerLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecommendFragment2 extends SkinFragment
        implements IEasy.OnBindHeaderListener {

    private static final String TAG = "BaseRecommendFragment2";

    private final List<AppInfo> bannerItemList = new ArrayList<>();

    protected Banner banner;

    private RecyclerView recyclerView;
    private MultiRecyclerViewWrapper wrapper;

    private ZBlurry blurred;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_recomment2;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        recyclerView = findViewById(R.id.recycler_view);
        wrapper = new MultiRecyclerViewWrapper(recyclerView);

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

        List<MultiData> multiDataList = new ArrayList<>();

        initMultiData(multiDataList);

        wrapper.setData(multiDataList)
                .setMaxSpan(4)
                .setFooterView(LayoutInflater.from(context).inflate(R.layout.item_footer_home, null, false))
                .setHeaderView(getHeaderLayoutId(), this)
                .build();
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
    }

    protected abstract int getHeaderLayoutId();

    @Override
    public final void onBindHeader(EasyViewHolder holder) {
        if (banner != null) {
            return;
        }
        banner = holder.getView(R.id.banner2);
        banner.setBannerLoader(new AppBannerLoader());

        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {
            @Override
            public void onBannerClick(int position) {
                AppDetailFragment.start(bannerItemList.get(position));
            }
        });
        initHeader(holder);
    }

    protected abstract void initHeader(EasyViewHolder holder);

    protected abstract void initMultiData(List<MultiData> list);

}
