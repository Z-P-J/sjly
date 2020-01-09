package com.zpj.shouji.market.ui.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.zpj.http.ZHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.market.R;
import com.zpj.shouji.market.ui.adapter.QianQianAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.listener.LoadMoreListener;
import com.zpj.shouji.market.bean.QianQianItem;
import com.zpj.shouji.market.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;

public class QianQianFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private List<QianQianItem> qianQianItemList = new ArrayList<>();
    private QianQianAdapter qianQianAdapter;
    private QianQianItem qianQianItem;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager layoutManager;
    private static final int totalPager = 48;
    private boolean isRefresh = false;

    private void getAppInfo(final int currentPage){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = ZHttp.connect("https://www.myqqjd.com/android/page/" + currentPage)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .toHtml();
                    //htmlContent = doc.body().text();
                    Elements elements = doc.select("article");
                    for (int i = 0; i < 10; i++) {
                        Log.d("app_site_-----",elements.get(i).select("a").get(0).attr("href"));
                        qianQianItem = new QianQianItem(elements.get(i).select("a").get(0).attr("href")
                                , elements.get(i).select("img").attr("src")
                                , elements.get(i).select("img").attr("alt")
                                , elements.get(i).select("div.archive-content").text()
                                , elements.get(i).select("a").get(1).text()
                                , elements.get(i).select("span.date").text() + " " + elements.get(i).select("span.views").text());
                        qianQianItemList.add(qianQianItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        qianQianAdapter.notifyDataSetChanged();
                        UIHelper.HideDilog();
                    }
                });
            }
        }).start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.qianqian_fragment;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.qianqian_recyclerview);
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
                        qianQianItemList.clear();
                        isRefresh = true;
                        swipeRefreshLayout.setRefreshing(false);
                        getAppInfo(1);
                    }
                }).start();
            }
        });

        layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        qianQianAdapter = new QianQianAdapter(qianQianItemList);
        recyclerView.setAdapter(qianQianAdapter);
        recyclerView.addOnScrollListener(new LoadMoreListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if (isRefresh) {
                    currentPage = 1;
                    initParams(10);
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
