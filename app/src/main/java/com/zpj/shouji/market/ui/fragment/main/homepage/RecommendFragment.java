package com.zpj.shouji.market.ui.fragment.main.homepage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.mingle.widget.LoadingView;
import com.stx.xhb.xbanner.XBanner;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.bean.AppItem;
import com.zpj.shouji.market.ui.adapter.AppAdapter;
import com.zpj.shouji.market.ui.adapter.loadmore.LoadMoreAdapter;
import com.zpj.shouji.market.ui.adapter.loadmore.LoadMoreWrapper;
import com.zpj.shouji.market.ui.fragment.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.utils.ConnectUtil;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.TransportUtil;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SupportActivity;

public class RecommendFragment extends BaseFragment {

    private static final String DEFAULT_LIST_URL = "http://tt.shouji.com.cn/androidv3/app_list_xml.jsp?index=1&versioncode=187";

    private View view;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private LoadingView loadingView;
    private List<AppItem> appItemList = new ArrayList<>();
    private List<AppItem> recommendItemList = new ArrayList<>();
    private AppAdapter appAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private LoadMoreAdapter loadMoreAdapter;

    private String nextUrl = DEFAULT_LIST_URL;


//    AutoPlayRecyclerView banner;
//    BannerLayout banner;
//    private BannerAdapter bannerAdapter;
//    private CarouselLayoutManager carouselLayoutManager;

    private XBanner mXBanner;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recomment;
    }

    @Override
    protected boolean supportSwipeBack() {
        return false;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.coolapk_recyclerview);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        loadingView = view.findViewById(R.id.load_view);
        loadingView.setVisibility(View.VISIBLE);
        loadingView.setLoadingText("正在加载，请稍后……");

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        appItemList.clear();
                        appAdapter.notifyDataSetChanged();
                        nextUrl = DEFAULT_LIST_URL;
//                        getCoolApkHtml();
                    }
                }, 1000);
            }
        });

        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float progress = Math.abs(verticalOffset) * 1.0f / appBarLayout.getTotalScrollRange();
                if (progress >= 0.1) {
                    swipeRefreshLayout.setEnabled(false);
                } else {
                    swipeRefreshLayout.setEnabled(true);
                }
            }
        });

        //lazyLoadData();


        layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        appAdapter =new AppAdapter(appItemList);
        appAdapter.setItemClickListener(new AppAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AppAdapter.ViewHolder holder, int position, AppItem item) {
                TransportUtil.getInstance().setAppItem(item);
                TransportUtil.getInstance().setIconBitmap(holder.icon);
                if (getActivity() instanceof SupportActivity) {
                    ((SupportActivity) getActivity()).start(AppDetailFragment.newInstance(item));
                }
            }
        });
        loadMoreAdapter = LoadMoreWrapper.with(appAdapter)
                .setLoadMoreEnabled(true)
                .setListener(new LoadMoreAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(LoadMoreAdapter.Enabled enabled) {
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getRecentUpdateApps();
                            }
                        }, 1);
                    }
                })
                .into(recyclerView);




//        carouselLayoutManager = new CarouselLayoutManager(getContext(), Util.Dp2px(getContext(), 100));
//        carouselLayoutManager.setItemSpace(Util.Dp2px(getContext(), 80));
//        carouselLayoutManager.setMoveSpeed(0.3f);
//        banner = view.findViewById(R.id.recycler);
////        banner.setLayoutManager(carouselLayoutManager);
//        bannerAdapter = new BannerAdapter(getContext(), appItemList);
//        banner.setAdapter(bannerAdapter);

        mXBanner = view.findViewById(R.id.xbanner);
        mXBanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                AppItem item = (AppItem)model;
                Glide.with(view).load(item.getAppIcon()).into((ImageView) view);
            }
        });
        getRecommends();
    }

    private  void getRecentUpdateApps(){
        ExecutorHelper.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("getCoolApkHtml", "nextUrl=" + nextUrl);
                    Document doc = ConnectUtil.getDocument(nextUrl);

                    nextUrl = doc.select("nextUrl").get(0).text();
                    Elements elements = doc.select("item");
                    for (int i = 1; i < elements.size(); i++) {
                        Element item = elements.get(i);
                        AppItem appItem = new AppItem();
                        appItem.setAppIcon(item.select("icon").text());
                        appItem.setAppTitle(item.select("title").text());
                        appItem.setAppId(item.select("id").text());
                        appItem.setAppViewType(item.select("viewtype").text());
                        appItem.setAppType(item.select("apptype").text());
                        appItem.setAppPackage(item.select("package").text());
                        appItem.setAppArticleNum(item.select("articleNum").text());
                        appItem.setAppNum(item.select("appNum").text());
                        appItem.setAppMinSdk(item.select("msdk").text());
                        appItem.setAppSize(item.select("m").text());
                        appItem.setAppInfo(item.select("r").text());
                        appItem.setAppComment(item.select("comment").text());
                        appItemList.add(appItem);
                    }
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadingView.setVisibility(View.GONE);
                            appAdapter.notifyDataSetChanged();
                        }
                    }, 1);
                }catch (Exception e) {
                    e.printStackTrace();
                    loadMoreAdapter.setLoadFailed(true);
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            AToast.error("加载失败！");
                        }
                    });
                }
            }
        });
    }

    private void getRecommends() {
        ExecutorHelper.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = ConnectUtil.getDocument("http://tt.shouji.com.cn/androidv3/app_index_xml.jsp?index=1&versioncode=187");

                    Elements elements = doc.select("item");
                    for (int i = 1; i < elements.size(); i++) {
                        Element item = elements.get(i);
                        AppItem appItem = new AppItem();
                        appItem.setAppIcon(item.select("icon").text());
                        appItem.setAppTitle(item.select("title").text());
                        appItem.setAppId(item.select("id").text());
                        appItem.setAppViewType(item.select("viewtype").text());
                        appItem.setAppType(item.select("apptype").text());
                        appItem.setAppPackage(item.select("package").text());
                        appItem.setAppArticleNum(item.select("articleNum").text());
                        appItem.setAppNum(item.select("appNum").text());
                        appItem.setAppSize(item.select("m").text());
                        appItem.setAppInfo(item.select("r").text());
                        appItem.setAppComment(item.select("comment").text());
                        recommendItemList.add(appItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mXBanner.post(new Runnable() {
                    @Override
                    public void run() {
                        mXBanner.setData(recommendItemList, new ArrayList<>(appItemList.size()));
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mXBanner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        mXBanner.stopAutoPlay();
    }
}
