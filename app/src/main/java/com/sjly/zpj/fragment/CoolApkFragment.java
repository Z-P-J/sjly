package com.sjly.zpj.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sjly.zpj.MainActivity;
import com.sjly.zpj.R;
import com.sjly.zpj.adapter.CoolApkAdapter;
import com.sjly.zpj.listener.LoadMoreListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class CoolApkFragment extends BaseFragment {

    private Handler handler;
    private View view;
    private RecyclerView recyclerView;
    private List<CoolApkItem> coolApkItemList = new ArrayList<>();;
    private CoolApkAdapter coolApkAdapter;
    private CoolApkItem coolApkItem;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isInit;
    private boolean isRefresh;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int totalPager = 5;
    private int tempCurrentPage = 0;
    private String result;
    private  String app_site;
    private int count;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = sharedPreferences.edit();
        getCoolApkHtml(1);
        view = inflater.inflate(R.layout.coolapk_fragment,null);
        recyclerView = (RecyclerView)view.findViewById(R.id.coolapk_recyclerview);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        isInit=true;

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
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
                                coolApkItemList.clear();
                                getCoolApkHtml(1);
                                //lazyLoadData();

                                coolApkAdapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                                //Log.d("size:",""+coolApkItemList.size());
                                isRefresh = true;
                            }
                        });
                    }
                }).start();
            }
        });
        //lazyLoadData();



        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        coolApkAdapter=new CoolApkAdapter(coolApkItemList);
        recyclerView.setAdapter(coolApkAdapter);
        recyclerView.addOnScrollListener(new LoadMoreListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                //tempCurrentPage = currentPage
                if(isRefresh){
                    tempCurrentPage = currentPage - 1;
                }
                isRefresh = false;
                currentPage -= tempCurrentPage;
                //当前的页数 < 总页数
                if (currentPage<totalPager) {
                    //去加载更多的数据
                    //getCoolApkHtml(currentPage);
                    loadMore(currentPage + 1);
                    Log.d("currentPage",""+currentPage);
                }else{
                    Toast.makeText(getContext(), "大哥没有更多的数据", Toast.LENGTH_SHORT).show();
                }
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1){
                    //int currentPage = (int)msg.obj;
                    //Log.d("curentpage",""+currentPage);
                    /*
                    for (int i = 20 * currentPage; i < 20 * (currentPage + 1); i++) {
                        //Log.d("测试：",sharedPreferences.getString("app_img_site_" + i, ""));
                        coolApkItem = new CoolApkItem(sharedPreferences.getString("app_site_" + i, ""),
                                sharedPreferences.getString("app_img_site_" + i, ""),
                                sharedPreferences.getString("app_title_" + i, ""),
                                sharedPreferences.getString("app_info_" + i, ""),
                                sharedPreferences.getString("app_count_" + i, ""),
                                sharedPreferences.getString("app_description_" + i, ""),sharedPreferences.getString("app_result_"+i,""));
                        coolApkItemList.add(coolApkItem);
                    }
                    */

                    coolApkAdapter.notifyDataSetChanged();


                } else if (msg.what == 2) {
                    String string = (String) msg.obj;
                    Log.d("string11111",string);
                    String[] arr = string.split("\\.");
                    int i = Integer.parseInt(arr[0]);
                    coolApkItemList.get(i).setApp_result(arr[1]);
                    Log.d("iiiiiii","" + i);
                    coolApkAdapter.notifyDataSetChanged();
                }
            }
        };









        //coolApkAdapter.notifyDataSetChanged();




        /*
        handler.post(new Runnable() {
            @Override
            public void run() {
                coolApkItemList.get(msg.what).setApp_result((String) msg.obj);
                Log.d("cscscscsc2","ceshi");
                coolApkAdapter.notifyDataSetChanged();
            }
        });
        */
        return view;
    }

    @Override
    public void lazyLoadData() {
        coolApkItemList.clear();
            //coolApkItemList = new ArrayList<>();
            //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            //int m = 1;
            for (int i = 1; i <= 20; i++){
                coolApkItem = new CoolApkItem(sharedPreferences.getString("app_site_"+i,""),
                        sharedPreferences.getString("app_img_site_"+i,""),
                        sharedPreferences.getString("app_title_"+i,""),
                        sharedPreferences.getString("app_info_"+i,""),
                        sharedPreferences.getString("app_count_"+i,""),
                        sharedPreferences.getString("app_description_"+i,""),sharedPreferences.getString("app_result_"+i,""));
                coolApkItemList.add(coolApkItem);
            }


    }

    private  void getCoolApkHtml(final int currentPage){

        new Thread(){
            String htmlContent = "";
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://www.coolapk.com/apk/update?p="+currentPage)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .get();
                    htmlContent = doc.body().toString();

                    Elements elements = doc.select("div.app_list_left").select("a");
                    for (int i = 20 * currentPage; i < 20 * (currentPage + 1); i++) {
                        int m = i - 20 * currentPage;
                        int j = elements.get(m).select("p.list_app_info").text().indexOf("M");
                        String str = elements.get(m).select("p.list_app_info").text().substring(0,j+1);
                        //editor.putString("app_site_"+i, elements.get(m).attr("href"));
                        app_site = elements.get(m).attr("href");
                        //Log.d("href:",elements.get(m).attr("href"));
                        //editor.putString("app_img_site_"+i, elements.get(m).select("img").attr("src"));
                        //Log.d("app_img_site_"+i,elements.get(m).select("img").attr("src"));
                        //editor.putString("app_title_"+i, elements.get(m).select("p.list_app_title").text());
                        //editor.putString("app_info_"+i, str);
                        //editor.putString("app_count_"+i, elements.get(m).select("span.list_app_count").text());
                        //editor.putString("app_description_"+i, elements.get(m).select("p.list_app_description").text());
                        compareVersion(app_site,i - 20);
                        //editor.putString("app_result_"+i, compareVersion(elements.get(m).attr("href")));
                        //Log.d("appppp", str);

                        coolApkItem = new CoolApkItem(elements.get(m).attr("href"),
                                elements.get(m).select("img").attr("src")
                                , elements.get(m).select("p.list_app_title").text()
                                , str
                                , elements.get(m).select("span.list_app_count").text()
                                , elements.get(m).select("p.list_app_description").text()
                                , "");
                        coolApkItemList.add(coolApkItem);

                    }
                    //editor.apply();

                    //Toast.makeText(MainActivity.this, elements.get(0).text(), Toast.LENGTH_SHORT).show();


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

        //coolApkItemList.clear();

        /*

        for (int i = 20 * currentPage; i < 20 * (currentPage + 1); i++) {
            //Log.d("测试：",sharedPreferences.getString("app_img_site_" + i, ""));
            coolApkItem = new CoolApkItem(sharedPreferences.getString("app_site_" + i, ""),
                    sharedPreferences.getString("app_img_site_" + i, ""),
                    sharedPreferences.getString("app_title_" + i, ""),
                    sharedPreferences.getString("app_info_" + i, ""),
                    sharedPreferences.getString("app_count_" + i, ""),
                    sharedPreferences.getString("app_description_" + i, ""),sharedPreferences.getString("app_result_"+i,""));
            coolApkItemList.add(coolApkItem);
        }
        */







    }

    private void loadMore(int currentPage){
        Log.d("currentPage",""+currentPage);
        getCoolApkHtml(currentPage);
        coolApkAdapter.notifyDataSetChanged();
        Log.d("size:",""+coolApkItemList.size());
    }

    private void compareVersion(final String app_site,final int i){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String id = "";
                String url = "https://www.coolapk.com" + app_site;
                final String packageName;
                if (app_site.startsWith("/apk/")) {
                    packageName = app_site.substring(5);
                }else {
                    packageName = app_site.substring(6);
                }
                Document doc;
                Elements elements;
                try {
                    doc = Jsoup.connect(url)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true).get();

                    elements = doc.select("span.list_app_info");
                    String versionName2 = elements.text();
                    //Log.d("version",elements.text());

                    doc = Jsoup.connect("http://tt.shouji.com.cn/androidv3/app_search_quick_xml.jsp?s=" + packageName)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .get();
                    elements = doc.select("package");
                    Log.d("elementSize","" + elements.size());

                    /*
                    if (elements.size() > 0) {
                        Log.d("sjlypackageName",packageName + "\t" + elements.get(0).text());
                        id = doc.select("id").get(0).text();
                        Log.d("ididididid11111111",id);
                    } else {
                        Log.d("sjlypackageName",packageName + "\t无");
                        Log.d("ididididid11111111","无");
                    }
                    */

                    if(elements.size() > 0) {
                        int count = 0;
                        for (Element element : elements){

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
                                    Log.d("result",i+"\t"+packageName+"已收录"+"\t"+versionName1+"\t"+versionName2);
                                } else {
                                    result = "待更新";
                                    Log.d("result",i+"\t"+packageName+"待更新"+"\t"+versionName1+"\t"+versionName2);
                                }
                            } else {
                                result = "未收录";
                                Log.d("result",i+"\t"+packageName+"未收录"+"\t无\t"+versionName2);
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



                /*
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        coolApkItemList.get(i).setApp_result(result);
                        Log.d("iiiiiii","" + i);
                        coolApkAdapter.notifyDataSetChanged();
                    }
                });

                */
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

}
