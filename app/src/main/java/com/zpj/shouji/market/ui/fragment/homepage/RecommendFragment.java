package com.zpj.shouji.market.ui.fragment.homepage;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.liaoinstan.springview.widget.SpringView;
import com.zpj.recyclerview.MultiData;
import com.zpj.recyclerview.MultiRecyclerViewWrapper;
import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;
import com.zpj.shouji.market.ui.fragment.base.StateFragment;
import com.zpj.shouji.market.ui.multidata.AppInfoMultiData;
import com.zpj.shouji.market.ui.multidata.CollectionMultiData;
import com.zpj.shouji.market.ui.multidata.GuessYouLikeMultiData;
import com.zpj.shouji.market.ui.multidata.SubjectMultiData;
import com.zpj.shouji.market.ui.widget.RecommendBanner;
import com.zpj.shouji.market.utils.EventBus;

import java.util.ArrayList;
import java.util.List;

public class RecommendFragment extends StateFragment {

    private static final String TAG = "RecommendFragment3";

    private RecyclerView recyclerView;

    private RecommendBanner mBanner;

    private int percent = 0;

    private boolean isLoaded = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.onMainActionEvent(this, new RxBus.SingleConsumer<Boolean>() {
            @Override
            public void onAccept(Boolean isShow) throws Exception {
                if (isSupportVisible() && mBanner != null) {
                    if (isShow) {
                        mBanner.onPause();
                    } else {
                        mBanner.onResume();
                    }
                }
            }
        });
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setFocusable(true);
//        recyclerView.setFocusableInTouchMode(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(-1)) {
                    percent = 0;
                    EventBus.sendScrollEvent(0);
                    if (mBanner != null) {
                        mBanner.onResume();
                    }
                } else {
                    if (percent != 1) {
                        EventBus.sendScrollEvent(1);
                        percent = 1;
                        if (mBanner != null) {
                            mBanner.onPause();
                        }
                    }
                }
            }
        });


        mBanner = new RecommendBanner(context);
        onRetry();
    }

    @Override
    protected void onRetry() {
        super.onRetry();
        mBanner.loadData(() -> {
            showContent();

            List<MultiData<?>> list = new ArrayList<>();

            list.add(new AppInfoMultiData("最近更新") {
                @Override
                public void onHeaderClick() {
                    ToolBarAppListFragment.startRecentUpdate();
                }

                @Override
                public PreloadApi getKey() {
                    return PreloadApi.HOME_RECENT;
                }
            });

            list.add(new CollectionMultiData());

            list.add(new AppInfoMultiData("应用推荐") {
                @Override
                public void onHeaderClick() {
                    ToolBarAppListFragment.startRecommendSoftList();
                }

                @Override
                public PreloadApi getKey() {
                    return PreloadApi.HOME_SOFT;
                }
            });

            list.add(new AppInfoMultiData("游戏推荐") {
                @Override
                public void onHeaderClick() {
                    ToolBarAppListFragment.startRecommendGameList();
                }

                @Override
                public PreloadApi getKey() {
                    return PreloadApi.HOME_GAME;
                }
            });

            list.add(new SubjectMultiData("专题推荐"));

            list.add(new GuessYouLikeMultiData("猜你喜欢"));

            MultiRecyclerViewWrapper.with(recyclerView)
                    .setData(list)
                    .setFooterView(LayoutInflater.from(context).inflate(R.layout.item_footer_home, null, false))
                    .setHeaderView(mBanner)
                    .build();
            isLoaded = true;
            EventBus.sendColorChangeEvent(true);

        }, throwable -> showError(throwable.getMessage()));
    }

    @Override
    public void onSupportVisible() {
        Log.d(TAG, "onSupportVisible");
        super.onSupportVisible();
        if (isLoaded) {
            if (recyclerView != null) {
                EventBus.sendScrollEvent(recyclerView.canScrollVertically(-1) ? 1 : 0);
            } else {
                EventBus.sendScrollEvent(0);
            }
        }

        if (mBanner != null) {
            mBanner.onResume();
        }
    }

    @Override
    public void onSupportInvisible() {
        Log.d(TAG, "onSupportInvisible");
        super.onSupportInvisible();
        if (mBanner != null) {
            mBanner.onPause();
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (mBanner != null) {
            mBanner.onResume();
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if (mBanner != null) {
            mBanner.onPause();
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        if (mBanner != null) {
            mBanner.onStop();
        }
    }

}
