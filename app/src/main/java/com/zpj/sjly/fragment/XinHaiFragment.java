package com.zpj.sjly.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zpj.sjly.R;

import com.zpj.sjly.adapter.XinHaiAdapter;
import com.zpj.sjly.listener.LoadMoreListener;
import com.zpj.sjly.model.XinHaiItem;
import com.zpj.sjly.utils.UIHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class XinHaiFragment extends BaseFragment{

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<XinHaiItem> xinHaiItemList = new ArrayList<>();
    private XinHaiItem xinHaiItem;
    private LinearLayoutManager layoutManager;
    private XinHaiAdapter xinHaiAdapter;
    private Handler handler;
    private int totalPager = 39;
    private boolean isInit;


    private boolean isRefresh = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xinhai_fragment,null);
        isInit = true;
        recyclerView = (RecyclerView)view.findViewById(R.id.xinhai_recyclerview);
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

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1){
                    xinHaiAdapter.notifyDataSetChanged();
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
                    Document doc = Jsoup.connect("https://hrtsea.com/category/android/android-apps/page/" + currentPage)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .get();
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
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }).start();
    }
}
