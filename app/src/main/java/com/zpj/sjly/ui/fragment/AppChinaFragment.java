package com.zpj.sjly.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.zpj.sjly.ui.activity.DetailActivity;
import com.zpj.sjly.R;
import com.zpj.sjly.ui.adapter.AppChinaAdapter;
import com.zpj.sjly.ui.fragment.base.LazyLoadFragment;
import com.zpj.sjly.listener.LoadMoreListener;
import com.zpj.sjly.bean.AppChinaItem;
import com.zpj.sjly.utils.UIHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AppChinaFragment extends LazyLoadFragment {

    private AppChinaItem appChinaItem;
    private List<AppChinaItem> appChinaItemList = new ArrayList<>();
    private AppChinaAdapter appChinaAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager layoutManager;
    private Handler handler;
    private boolean isRefresh = false;
    private int totalPager = 34;
    private String result;
    private boolean isInit;
    private Button appButton;
    private Button gameButton;
    private boolean isChangeType;

    private static final String APP_URL = "http://www.appchina.com/category/30/";
    private static final String GAME_URL = "http://www.appchina.com/category/40/";
    private String app_url = "http://www.appchina.com/category/30/";

    @Nullable
    @Override
    protected View onBuildView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isInit = true;
        //lazyLoadData();
        View view = inflater.inflate(R.layout.appchina_fragment,null);
        recyclerView = (RecyclerView)view.findViewById(R.id.appchina_recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(2000);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appChinaItemList.clear();
                        isRefresh = true;
                        swipeRefreshLayout.setRefreshing(false);
                        getAppChinaApk(app_url, 1);
                    }
                });
            }
        });

        layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        appChinaAdapter = new AppChinaAdapter(appChinaItemList);
        appChinaAdapter.setItemClickListener(new AppChinaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("cessssssssssssss","---------------------");
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("app_site","appchina:http://www.appchina.com" + appChinaItemList.get(position).getApp_url());
                getActivity().startActivity(intent);
            }
        });
        recyclerView.setAdapter(appChinaAdapter);
        recyclerView.addOnScrollListener(new LoadMoreListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if(isRefresh||isChangeType){
                    currentPage = 1;
                    initParams(30);
                }
                isRefresh = false;
                isChangeType = false;
                if (currentPage<totalPager) {
                    //getCoolApkHtml(currentPage + 1);
                    getAppChinaApk(app_url, currentPage + 1);
                }else{
                    Toast.makeText(getContext(), "人家是有底线的。。。", Toast.LENGTH_SHORT).show();
                }
            }
        });

        appButton = (Button)view.findViewById(R.id.app_button);
        appButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app_url.equals(APP_URL)){
                    return;
                }
                UIHelper.showDialogForLoading(getContext(),"正在加载应用中。。。");
                app_url = APP_URL;
                appChinaItemList.clear();
                appChinaAdapter.notifyDataSetChanged();
                isChangeType = true;
                getAppChinaApk(app_url, 1);
            }
        });

        gameButton = (Button)view.findViewById(R.id.game_button);
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app_url.equals(GAME_URL)) {
                    return;
                }
                UIHelper.showDialogForLoading(getContext(),"正在加载游戏中。。。");
                app_url = GAME_URL;
                appChinaItemList.clear();
                appChinaAdapter.notifyDataSetChanged();
                isChangeType = true;
                getAppChinaApk(app_url, 1);
            }
        });



        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1){
                    appChinaAdapter.notifyDataSetChanged();
                    UIHelper.HideDilog();
                } else if (msg.what == 2) {
                    String string = (String) msg.obj;
                    Log.d("string11111",string);
                    String[] arr = string.split("\\.");
                    int i = Integer.parseInt(arr[0]);
                    appChinaItemList.get(i).setApp_result(arr[1]);
                    appChinaAdapter.notifyDataSetChanged();
                }
            }
        };
        return view;
    }

    @Override
    public void lazyLoadData() {
        if (isInit&&isVisible){
            UIHelper.showDialogForLoading(getContext(),"正在加载。。。");
            getAppChinaApk(app_url, 1);
            isInit = false;
        }
    }

    private void getAppChinaApk(final String url, final int currentPage){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String app_type = "";
                if (url.contains("30"))
                    app_type = "应用";
                else if (url.contains("40"))
                    app_type = "游戏";
                try {
                    Document doc = Jsoup.connect(url + currentPage + "_1_1_3_0_0_0.html")
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .get();
                    Elements elements = doc.select("ul.app-list").select("li");
                    for (int i = 30 * currentPage; i < 30 * (currentPage + 1); i++){
                        int m = i - 30 * currentPage;
                        appChinaItem = new AppChinaItem(elements.get(m).select("a").get(0).attr("href"),
                                elements.get(m).select("img").attr("src"),
                                elements.get(m).select("h1.app-name").text(),
                                elements.get(m).select("span.download-count").text(),
                                elements.get(m).select("span.update-date").text(),
                                elements.get(m).select("div.app-intro").select("span").text(), app_type, "检测中");
                        appChinaItemList.add(appChinaItem);
                        compareVersion(elements.get(m).select("a").get(0).attr("href"),i - 30);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);


            }
        }).start();
    }

    private void compareVersion(final String app_site,final int i){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String id = "";
                String url = "http://www.appchina.com" + app_site;
                final String packageName;
                packageName = app_site.substring(5);
                Document doc;
                Elements elements;
                try {
                    doc = Jsoup.connect(url)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true).get();

                    elements = doc.select("div.other-info").select("p");
                    String versionName2 = elements.get(3).text().substring(3);
                    Log.d("versionName2",versionName2);

                    doc = Jsoup.connect("http://tt.shouji.com.cn/androidv3/app_search_quick_xml.jsp?s=" + packageName)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .get();
                    elements = doc.select("package");
                    Log.d("elementSize","" + elements.size());

                    if(elements.size() > 0) {
                        int count = 0;
                        for (Element element : elements){
                            Log.d("count",""+count);
                            id = doc.select("id").get(count).text();
                            if (element.text().equals(packageName)){
                                doc = Jsoup.connect("http://tt.shouji.com.cn/androidv3/soft_show.jsp?id=" + id)
                                        .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                                        .ignoreHttpErrors(true)
                                        .ignoreContentType(true)
                                        .get();
                                if (doc.select("versionname").size() == 0) {
                                    doc = Jsoup.connect("http://tt.shouji.com.cn/androidv3/game_show.jsp?id=" + id)
                                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                                            .ignoreHttpErrors(true)
                                            .ignoreContentType(true)
                                            .get();
                                }

                                Log.d("sjlyversionname",doc.select("versionname").get(count).text());
                                String versionName1 = doc.select("versionname").get(count).text();
                                if (compareVersion(versionName1,versionName2)) {
                                    result = "已收录";
                                    Log.d("result",i+"\t"+count+"\t"+packageName+"已收录"+"\t"+versionName1+"\t"+versionName2);
                                } else {
                                    result = "待更新";
                                    Log.d("result",i+"\t"+count+"\t"+packageName+"待更新"+"\t"+versionName1+"\t"+versionName2);
                                }
                                break;
                            } else {
                                result = "未收录";
                                Log.d("result",i+"\t"+count+"\t"+packageName+"未收录"+"\t无\t"+versionName2);
                            }
                            count++;
                        }
                    } else {
                        result = "未收录";
                        Log.d("result",i+"\t"+packageName+"未收录"+"\t无\t"+versionName2);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = 2;
                msg.obj = i + "." + result;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private boolean compareVersion(String version1, String version2){
        if (version1.startsWith("v"))
            version1 = version1.substring(1);
        if (version2.startsWith("v"))
            version2 = version2.substring(1);
        if (version1.equals(version2)){
            return true;
        }

        if (version1.matches(".*[a-zA-Z]+.*") || version2.matches(".*[a-zA-Z]+.*")) {
            return false;
        }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");
        int index = 0;
        int minLen = Math.min(version1Array.length,version2Array.length);
        int diff = 0;
        while (index < minLen && (diff = Integer.parseInt(version1Array[index])- Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0){
            for (int i = index; i < version1Array.length; i++){
                if (Integer.parseInt(version1Array[1]) > 0)
                    return true;
            }
            for (int i = index; i < version2Array.length; i++){
                if (Integer.parseInt(version2Array[1]) > 0)
                    return false;
            }
            return true;
        }else {
            return diff > 0;
        }
    }

    @Override
    public boolean handleBackPressed() {
        return false;
    }
}
