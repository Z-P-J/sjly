package com.zpj.market.ui.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.zpj.http.ZHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.market.R;

import com.zpj.market.ui.adapter.XinHaiAdapter;
import com.zpj.market.ui.fragment.base.BaseFragment;
import com.zpj.market.listener.LoadMoreListener;
import com.zpj.market.bean.XinHaiItem;
import com.zpj.market.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;

public class XinHaiFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<XinHaiItem> xinHaiItemList = new ArrayList<>();
    private XinHaiItem xinHaiItem;
    private LinearLayoutManager layoutManager;
    private XinHaiAdapter xinHaiAdapter;
    private int totalPager = 39;


    private boolean isRefresh = false;


    private void getAppInfo(final int currentPage){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = ZHttp.get("https://hrtsea.com/category/android/android-apps/page/" + currentPage)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .toHtml();
                    Elements elements = doc.select("article");
                    for (int i = 0; i < elements.size(); i++) {
                        xinHaiItem = new XinHaiItem();
                        xinHaiItem.setAppSite(elements.get(i).select("a").get(0).attr("href"));
                        xinHaiItem.setAppImgSite(elements.get(i).select("img").attr("data-src"));
                        xinHaiItem.setAppTitle(elements.get(i).select("img").attr("data-src"));
                        xinHaiItem.setAppDescription(elements.get(i).select("img").attr("data-src"));
                        xinHaiItem.setAppInfo(elements.get(i).select("img").attr("data-src"));
                        xinHaiItemList.add(xinHaiItem);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        xinHaiAdapter.notifyDataSetChanged();
                        UIHelper.HideDilog();
                    }
                });
            }
        }).start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.xinhai_fragment;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.xinhai_recyclerview);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //do something
                        xinHaiItemList.clear();
                        isRefresh = true;
                        swipeRefreshLayout.setRefreshing(false);
                        getAppInfo(1);
                    }
                }).start();
            }
        });

        layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        xinHaiAdapter = new XinHaiAdapter(xinHaiItemList);
        recyclerView.setAdapter(xinHaiAdapter);
        recyclerView.addOnScrollListener(new LoadMoreListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if (isRefresh) {
                    currentPage = 1;
                    initParams(11);
                    isRefresh = false;
                }
                if (currentPage<totalPager) {

                    getAppInfo(currentPage + 1);


                }else{
                    Toast.makeText(getContext(), "人家是有底线的。。。", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        UIHelper.showDialogForLoading(getContext(),"正在加载。。。");
        getAppInfo(1);
    }
}
