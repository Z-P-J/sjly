package com.zpj.sjly.fragment;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.zpj.sjly.DetailActivity;
import com.zpj.sjly.R;
import com.zpj.sjly.adapter.AppAdapter;
import com.zpj.sjly.listener.LoadMoreListener;
import com.zpj.sjly.utils.UIHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {

    private Handler handler;
    private View view;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private List<AppItem> appItemList = new ArrayList<>();
    private List<CoolApkItem> tempCoolApkItemList = new ArrayList<>();
    private AppAdapter appAdapter;
    private boolean isInit;
    private boolean isRefresh;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int totalPager = 100;
    private int currentPage = 0;
    private String result;
    private  String app_site;
    private TextView app_all;
    private TextView app_update;
    private TextView app_new;
    private TextView app_old;
    private int app_all_count = 0;
    private int app_update_count = 0;
    private int app_new_count = 0;
    private int app_old_count = 0;
    private int count = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        UIHelper.showDialogForLoading(getContext(),"正在加载。。。");
        getCoolApkHtml(1);

        view = inflater.inflate(R.layout.home_fragment,null);
        recyclerView = view.findViewById(R.id.coolapk_recyclerview);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //lazyLoadData();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("刷新","-------------------------------------------------------");
                                appItemList.clear();
                                isRefresh = true;
                                swipeRefreshLayout.setRefreshing(false);

                                //initRecyclerView();
                                getCoolApkHtml(1);

                            }
                        });

                    }
                }).start();
            }
        });
        //lazyLoadData();


        layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        appAdapter =new AppAdapter(appItemList);
        appAdapter.setItemClickListener(new AppAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                if ("game".equals(appItemList.get(position).getAppType())) {
                    intent.putExtra("app_site", "sjly:http://tt.shouji.com.cn/androidv3/game_show.jsp?id=" + appItemList.get(position).getAppId());
                } else {
                    intent.putExtra("app_site", "sjly:http://tt.shouji.com.cn/androidv3/soft_show.jsp?id=" + appItemList.get(position).getAppId());
                }

                getActivity().startActivity(intent);
//                Toast.makeText(getContext(), "点击", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(appAdapter);
        recyclerView.addOnScrollListener(new LoadMoreListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
//                if(isRefresh){
//                    currentPage = 1;
//                    initParams(20);
//                }
//                isRefresh = false;
//                if (currentPage<totalPager) {
//                    getCoolApkHtml(currentPage + 1);
//                    //loadMore(currentPage + 1);
//                }else{
//                    Toast.makeText(getContext(), "人家是有底线的。。。", Toast.LENGTH_SHORT).show();
//                }
                Toast.makeText(getContext(), "人家是有底线的。。。", Toast.LENGTH_SHORT).show();
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1){
                    appAdapter.notifyDataSetChanged();
                    UIHelper.HideDilog();
                }
            }
        };
        return view;
    }



    @Override
    public void lazyLoadData() {

    }

    private  void getCoolApkHtml(final int currentPage){

        new Thread(){
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("http://tt.shouji.com.cn/androidv3/app_list_xml.jsp?index=1&versioncode=187")
                            .userAgent("okhttp/3.0.1")
                            .header("Accept-Encoding", "gzip")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .get();

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
                }catch (Exception e) {
                    e.printStackTrace();
                }
                //Log.d("CoolApkHtml:", htmlContent);

                //getAppDetail(htmlContent);
                Message msg = new Message();
                //msg.obj = currentPage;
                msg.what = 1;
                handler.sendMessage(msg);
            }

        }.start();
    }

}
