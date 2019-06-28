package com.zpj.sjly.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zpj.sjly.R;
import com.zpj.sjly.ui.adapter.QianQianAdapter;
import com.zpj.sjly.ui.fragment.base.LazyLoadFragment;
import com.zpj.sjly.listener.LoadMoreListener;
import com.zpj.sjly.bean.QianQianItem;
import com.zpj.sjly.utils.UIHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class QianQianFragment extends LazyLoadFragment {

    private RecyclerView recyclerView;
    private List<QianQianItem> qianQianItemList = new ArrayList<>();
    private QianQianAdapter qianQianAdapter;
    private QianQianItem qianQianItem;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager layoutManager;
    private Handler handler;
    private static final int totalPager = 48;
    private boolean isRefresh = false;
    private boolean isInit;

    @Nullable
    @Override
    public View onBuildView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qianqian_fragment,null);
        isInit = true;
        recyclerView = (RecyclerView)view.findViewById(R.id.qianqian_recyclerview);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
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


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    qianQianAdapter.notifyDataSetChanged();
                    UIHelper.HideDilog();
                }
            }
        };
        return view;
    }

    @Override
    public void lazyLoadData() {
        if (isInit&&isVisible){
            UIHelper.showDialogForLoading(getContext(),"正在加载。。。");
            getAppInfo(1);
            isInit = false;
        }
    }

    private void getAppInfo(final int currentPage){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://www.myqqjd.com/android/page/" + currentPage)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .get();
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

                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);


            }
        }).start();
    }

    @Override
    public boolean handleBackPressed() {
        return false;
    }
}
