//package com.zpj.shouji.market.ui.fragment.homepage;
//
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//
//import com.felix.atoast.library.AToast;
//import com.zpj.recyclerview.MultiData;
//import com.zpj.recyclerview.MultiRecyclerViewWrapper;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.api.PreloadApi;
//import com.zpj.shouji.market.event.ColorChangeEvent;
//import com.zpj.shouji.market.event.ScrollChangeEvent;
//import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;
//import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
//import com.zpj.shouji.market.ui.fragment.homepage.multi.AppInfoMultiData;
//import com.zpj.shouji.market.ui.fragment.homepage.multi.CollectionMultiData;
//import com.zpj.shouji.market.ui.fragment.homepage.multi.GuessYouLikeMultiData;
//import com.zpj.shouji.market.ui.fragment.homepage.multi.SubjectMultiData;
//import com.zpj.shouji.market.ui.widget.recommend.RecommendBanner;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class RecommendFragment4 extends SkinFragment {
//
//    private static final String TAG = "RecommendFragment4";
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
//                AToast.normal("onScrolled");
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
////        list.add(new TitleMultiData("最近更新", new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                ToolBarAppListFragment.startRecentUpdate();
////            }
////        }));
////
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
////
////        list.add(new TitleMultiData("应用集推荐", new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                CollectionRecommendListFragment.start();
////            }
////        }));
////
//        list.add(new CollectionMultiData());
////
////        list.add(new TitleMultiData("应用推荐", new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                ToolBarAppListFragment.startRecommendSoftList();
////            }
////        }));
////
//        list.add(new AppInfoMultiData("应用推荐") {
//
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
////
////
////        list.add(new TitleMultiData("游戏推荐", new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                ToolBarAppListFragment.startRecommendGameList();
////            }
////        }));
////
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
////
////
////        list.add(new TitleMultiData("专题推荐", new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                SubjectRecommendListFragment.start("http://tt.shouji.com.cn/androidv3/special_index_xml.jsp?jse=yes");
////            }
////        }));
////
//        list.add(new SubjectMultiData("专题推荐"));
////
////        list.add(new TitleMultiData("猜你喜欢", null));
////
//        list.add(new GuessYouLikeMultiData("猜你喜欢"));
//
//        wrapper.setData(list)
//                .setMaxSpan(4)
//                .setFooterView(LayoutInflater.from(context).inflate(R.layout.item_footer_home, null, false))
//                .setHeaderView(mBanner)
//                .build();
//    }
//
//    @Override
//    protected void initStatusBar() {
//
//    }
//
//}
