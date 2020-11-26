//package com.zpj.shouji.market.ui.fragment.recommond;
//
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//
//import com.zpj.recyclerview.MultiData;
//import com.zpj.recyclerview.MultiRecyclerViewWrapper;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.api.PreloadApi;
//import com.zpj.shouji.market.event.ColorChangeEvent;
//import com.zpj.shouji.market.event.MainActionPopupEvent;
//import com.zpj.shouji.market.event.ScrollChangeEvent;
//import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;
//import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
//import com.zpj.shouji.market.ui.fragment.homepage.multi.AppInfoMultiData;
//import com.zpj.shouji.market.ui.fragment.homepage.multi.CollectionMultiData;
//import com.zpj.shouji.market.ui.fragment.homepage.multi.GuessYouLikeMultiData;
//import com.zpj.shouji.market.ui.fragment.homepage.multi.SubjectMultiData;
//import com.zpj.shouji.market.ui.widget.recommend.RecommendBanner;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BaseRecommendFragment3 extends SkinFragment {
//
//    private static final String TAG = "RecommendFragment3";
//
//    private RecyclerView recyclerView;
//
//    private RecommendBanner mBanner;
//
//    private int percent = 0;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_recycler_view;
//    }
//
//    @Override
//    protected void initView(View view, @Nullable Bundle savedInstanceState) {
//        recyclerView = findViewById(R.id.recycler_view);
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (!recyclerView.canScrollVertically(-1)) {
//                    percent = 0;
//                    ScrollChangeEvent.post(0);
//                    if (mBanner != null) {
//                        mBanner.onResume();
//                    }
//                } else {
//                    if (percent != 1) {
//                        ScrollChangeEvent.post(1);
//                        percent = 1;
//                        if (mBanner != null) {
//                            mBanner.onPause();
//                        }
//                    }
//                }
//            }
//        });
//
//
//        MultiRecyclerViewWrapper wrapper = new MultiRecyclerViewWrapper(recyclerView);
//
//        mBanner = new RecommendBanner(context);
//        mBanner.loadData(new Runnable() {
//            @Override
//            public void run() {
////                wrapper.showContent();
//                ColorChangeEvent.post(true);
//            }
//        });
//
//        List<MultiData> list = new ArrayList<>();
//
//        list.add(new AppInfoMultiData("最近更新") {
//            @Override
//            public void onHeaderClick() {
//                ToolBarAppListFragment.startRecentUpdate();
//            }
//
//            @Override
//            public PreloadApi getKey() {
//                return PreloadApi.HOME_RECENT;
//            }
//        });
//
//        list.add(new CollectionMultiData());
//
//        list.add(new AppInfoMultiData("应用推荐") {
//            @Override
//            public void onHeaderClick() {
//                ToolBarAppListFragment.startRecommendSoftList();
//            }
//
//            @Override
//            public PreloadApi getKey() {
//                return PreloadApi.HOME_SOFT;
//            }
//        });
//
//
////        list.add(new TitleMultiData("游戏推荐", new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                ToolBarAppListFragment.startRecommendGameList();
////            }
////        }));
//
//        list.add(new AppInfoMultiData("游戏推荐") {
//            @Override
//            public void onHeaderClick() {
//                ToolBarAppListFragment.startRecommendGameList();
//            }
//
//            @Override
//            public PreloadApi getKey() {
//                return PreloadApi.HOME_GAME;
//            }
//        });
//
//
////        list.add(new TitleMultiData("专题推荐", new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                SubjectRecommendListFragment.start("http://tt.shouji.com.cn/androidv3/special_index_xml.jsp?jse=yes");
////            }
////        }));
//
//        list.add(new SubjectMultiData("专题推荐"));
//
////        list.add(new TitleMultiData("猜你喜欢", null));
//
//        list.add(new GuessYouLikeMultiData("猜你喜欢"));
//
//        wrapper.setData(list)
//                .setMaxSpan(4)
//                .setFooterView(LayoutInflater.from(context).inflate(R.layout.item_footer_home, null, false))
//                .setHeaderView(mBanner)
//                .build();
////        wrapper.showLoading();
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
//        Log.d(TAG, "onCreate");
//    }
//
//    @Override
//    public void onDestroy() {
//        Log.d(TAG, "onDestroy");
//        EventBus.getDefault().unregister(this);
//        super.onDestroy();
//    }
//
//    @Override
//    public void onSupportVisible() {
//        Log.d(TAG, "onSupportVisible");
//        super.onSupportVisible();
//        if (recyclerView != null) {
//            ScrollChangeEvent.post(recyclerView.canScrollVertically(-1) ? 1 : 0);
//        } else {
//            ScrollChangeEvent.post(0);
//        }
//        if (mBanner != null) {
//            mBanner.onResume();
//        }
//    }
//
//    @Override
//    public void onSupportInvisible() {
//        Log.d(TAG, "onSupportInvisible");
//        super.onSupportInvisible();
//        if (mBanner != null) {
//            mBanner.onPause();
//        }
//    }
//
//    @Override
//    protected void initStatusBar() {
//
//    }
//
//    @Override
//    public void onResume() {
//        Log.d(TAG, "onResume");
//        super.onResume();
//        if (mBanner != null) {
//            mBanner.onResume();
//        }
//    }
//
//    @Override
//    public void onPause() {
//        Log.d(TAG, "onPause");
//        super.onPause();
//        if (mBanner != null) {
//            mBanner.onPause();
//        }
//    }
//
//    @Override
//    public void onStop() {
//        Log.d(TAG, "onStop");
//        super.onStop();
//        if (mBanner != null) {
//            mBanner.onStop();
//        }
//    }
//
//    @Subscribe
//    public void onMainActionPopupEvent(MainActionPopupEvent event) {
//        if (isSupportVisible() && mBanner != null) {
//            if (event.isShow()) {
//                mBanner.onPause();
//            } else {
//                mBanner.onResume();
//            }
//        }
//    }
//
////    private void onError(Exception e) {
////        post(() -> AToast.error("加载失败！" + e.getMessage()));
////    }
////
////    public interface OnItemClickListener<T> {
////        void onItemClick(View v, T data);
////    }
//
//}
