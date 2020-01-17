//package com.zpj.sjly.ui.fragment.main;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.zpj.sjly.ui.activity.DetailActivity;
//import com.zpj.sjly.R;
//import com.zpj.sjly.ui.adapter.CoolApkAdapter;
//import com.zpj.sjly.ui.fragment.base.BaseFragment;
//import com.zpj.sjly.ui.fragment.base.LazyLoadFragment;
//import com.zpj.sjly.listener.LoadMoreListener;
//import com.zpj.sjly.bean.CoolApkItem;
//import com.zpj.sjly.utils.UIHelper;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CoolApkFragment extends BaseFragment {
//
//    private Handler handler;
//    private View view;
//    private LinearLayoutManager layoutManager;
//    private RecyclerView recyclerView;
//    private List<CoolApkItem> coolApkItemList = new ArrayList<>();
//    private List<CoolApkItem> tempCoolApkItemList = new ArrayList<>();
//    private CoolApkAdapter coolApkAdapter;
//    private CoolApkItem coolApkItem;
//    private SharedPreferences sharedPreferences;
//    private SharedPreferences.Editor editor;
//    private boolean isInit;
//    private boolean isRefresh;
//    private SwipeRefreshLayout swipeRefreshLayout;
//    private static final int totalPager = 100;
//    private int currentPage = 0;
//    private String result;
//    private  String app_site;
//    private TextView app_all;
//    private TextView app_update;
//    private TextView app_new;
//    private TextView app_old;
//    private int app_all_count = 0;
//    private int app_update_count = 0;
//    private int app_new_count = 0;
//    private int app_old_count = 0;
//    private int count = 0;
//
//
//    private  void getCoolApkHtml(final int currentPage){
//
//        new Thread(){
//            String htmlContent = "";
//            @Override
//            public void run() {
//                try {
//                    Document doc = Jsoup.connect("https://www.coolapk.com/apk/update?p="+currentPage)
//                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
//                            .ignoreHttpErrors(true)
//                            .ignoreContentType(true)
//                            .get();
//                    htmlContent = doc.body().toString();
//
//                    Elements elements = doc.select("div.app_list_left").select("a");
//                    for (int i = 20 * currentPage; i < 20 * (currentPage + 1); i++) {
//                        int m = i - 20 * currentPage;
//                        int j = elements.get(m).select("p.list_app_info").text().indexOf("M");
//                        String str = elements.get(m).select("p.list_app_info").text().substring(0,j+1);
//                        app_site = elements.get(m).attr("href");
//                        //Log.d("href:",elements.get(m).attr("href"));
//                        //Log.d("app_img_site_"+i,elements.get(m).select("img").attr("src"));
//                        compareVersion(app_site,i - 20);
//                        //Log.d("appppp", str);
//
//                        coolApkItem = new CoolApkItem(elements.get(m).attr("href"),
//                                elements.get(m).select("img").attr("src")
//                                , elements.get(m).select("p.list_app_title").text()
//                                , str
//                                , elements.get(m).select("span.list_app_count").text()
//                                , elements.get(m).select("p.list_app_description").text()
//                                , "检测中");
//                        coolApkItemList.add(coolApkItem);
//
//                    }
//                }catch (Exception e) {
//                    e.printStackTrace();
//                }
//                //Log.d("CoolApkHtml:", htmlContent);
//
//                //getAppDetail(htmlContent);
//                Message msg = new Message();
//                //msg.obj = currentPage;
//                msg.what = 1;
//                handler.sendMessage(msg);
//            }
//
//        }.start();
//    }
//
//    private void compareVersion(final String app_site,final int i){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String id = "";
//                String url = "https://www.coolapk.com" + app_site;
//                final String packageName;
//                if (app_site.startsWith("/apk/")) {
//                    packageName = app_site.substring(5);
//                }else {
//                    packageName = app_site.substring(6);
//                }
//                Document doc;
//                Elements elements;
//                try {
//                    doc = Jsoup.connect(url)
//                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
//                            .ignoreHttpErrors(true)
//                            .ignoreContentType(true).get();
//
//                    elements = doc.select("span.list_app_info");
//                    String versionName2 = elements.text();
//                    //Log.d("version",elements.text());
//
//                    doc = Jsoup.connect("http://tt.shouji.com.cn/androidv3/app_search_quick_xml.jsp?s=" + packageName)
//                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
//                            .ignoreHttpErrors(true)
//                            .ignoreContentType(true)
//                            .get();
//                    elements = doc.select("package");
//                    Log.d("elementSize","" + elements.size());
//
//                    /*
//                    if (elements.size() > 0) {
//                        Log.d("sjlypackageName",packageName + "\t" + elements.get(0).text());
//                        id = doc.select("id").get(0).text();
//                        Log.d("ididididid11111111",id);
//                    } else {
//                        Log.d("sjlypackageName",packageName + "\t无");
//                        Log.d("ididididid11111111","无");
//                    }
//                    */
//
//                    if(elements.size() > 0) {
//                        int count = 0;
//                        for (Element element : elements){
//                            Log.d("count",""+count);
//                            id = doc.select("id").get(count).text();
//                            if (element.text().equals(packageName)){
//                                doc = Jsoup.connect("http://tt.shouji.com.cn/androidv3/soft_show.jsp?id=" + id)
//                                        .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
//                                        .ignoreHttpErrors(true)
//                                        .ignoreContentType(true)
//                                        .get();
//                                if (doc.select("versionname").size() == 0) {
//                                    doc = Jsoup.connect("http://tt.shouji.com.cn/androidv3/game_show.jsp?id=" + id)
//                                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
//                                            .ignoreHttpErrors(true)
//                                            .ignoreContentType(true)
//                                            .get();
//                                }
//
//                                Log.d("sjlyversionname",doc.select("versionname").get(count).text());
//                                String versionName1 = doc.select("versionname").get(count).text();
//                                if (compareVersion(versionName1,versionName2)) {
//                                    result = "已收录";
//                                    Log.d("result",i+"\t"+count+"\t"+packageName+"已收录"+"\t"+versionName1+"\t"+versionName2);
//                                } else {
//                                    result = "待更新";
//                                    Log.d("result",i+"\t"+count+"\t"+packageName+"待更新"+"\t"+versionName1+"\t"+versionName2);
//                                }
//                                break;
//                            } else {
//                                result = "未收录";
//                                Log.d("result",i+"\t"+count+"\t"+packageName+"未收录"+"\t无\t"+versionName2);
//                            }
//                            count++;
//                        }
//                    } else {
//                        result = "未收录";
//                        Log.d("result",i+"\t"+packageName+"未收录"+"\t无\t"+versionName2);
//                    }
//                }catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                Message msg = new Message();
//                msg.what = 2;
//                msg.obj = i + "." + result;
//                handler.sendMessage(msg);
//
//
//
//                /*
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        coolApkItemList.get(i).setApp_result(result);
//                        Log.d("iiiiiii","" + i);
//                        coolApkAdapter.notifyDataSetChanged();
//                    }
//                });
//
//                */
//            }
//        }).start();
//    }
//
//    private boolean compareVersion(String version1, String version2){
//        if (version1.startsWith("v"))
//            version1 = version1.substring(1);
//        if (version2.startsWith("v"))
//            version2 = version2.substring(1);
//        if (version1.equals(version2)){
//            return true;
//        }
//
//        if (version1.matches(".*[a-zA-Z]+.*") || version2.matches(".*[a-zA-Z]+.*")) {
//            return false;
//        }
//        String[] version1Array = version1.split("\\.");
//        String[] version2Array = version2.split("\\.");
//        int index = 0;
//        int minLen = Math.min(version1Array.length,version2Array.length);
//        int diff = 0;
//        while (index < minLen && (diff = Integer.parseInt(version1Array[index])- Integer.parseInt(version2Array[index])) == 0) {
//            index++;
//        }
//        if (diff == 0){
//            for (int i = index; i < version1Array.length; i++){
//                if (Integer.parseInt(version1Array[1]) > 0)
//                    return true;
//            }
//            for (int i = index; i < version2Array.length; i++){
//                if (Integer.parseInt(version2Array[1]) > 0)
//                    return false;
//            }
//            return true;
//        }else {
//            return diff > 0;
//        }
//    }
//
//    private void CountApp(){
//        app_all_count = 0;
//        app_update_count = 0;
//        app_new_count = 0;
//        app_old_count = 0;
//        app_all_count = coolApkItemList.size();
//        for (int i = 0; i < app_all_count; i++) {
//            Log.d("",coolApkItemList.get(i).getApp_result());
//            if (coolApkItemList.get(i).getApp_result().equals("待更新"))
//                app_update_count++;
//            else if (coolApkItemList.get(i).getApp_result().equals("未收录"))
//                app_new_count++;
//            else  if (coolApkItemList.get(i).getApp_result().equals("已收录"))
//                app_old_count++;
//        }
//        app_all.setText("已加载("+app_all_count+")");
//        app_update.setText("待更新("+app_update_count+")");
//        app_new.setText("未收录("+app_new_count+")");
//        app_old.setText("已收录("+app_old_count+")");
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.coolapk_fragment;
//    }
//
//    @Override
//    protected void initView(View view, @Nullable Bundle savedInstanceState) {
//        UIHelper.showDialogForLoading(getContext(),"正在加载。。。");
//        getCoolApkHtml(1);
//
//        this.view = view;
//
//        recyclerView = view.findViewById(R.id.coolapk_recyclerview);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
//        swipeRefreshLayout.setOnRefreshListener(() -> {
//            //lazyLoadData();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(2000);
//                    }catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d("刷新","-------------------------------------------------------");
//                            coolApkItemList.clear();
//                            isRefresh = true;
//                            swipeRefreshLayout.setRefreshing(false);
//
//                            //initRecyclerView();
//                            getCoolApkHtml(1);
//
//                        }
//                    });
//
//                }
//            }).start();
//        });
//        //lazyLoadData();
//
//        app_all = view.findViewById(R.id.app_all);
//        app_all.onViewClick(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        app_update = view.findViewById(R.id.app_update);
//        app_update.onViewClick(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        app_new = view.findViewById(R.id.app_new);
//        app_new.onViewClick(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        app_old = view.findViewById(R.id.app_old);
//        app_old.onViewClick(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//
//        layoutManager=new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(layoutManager);
//        coolApkAdapter=new CoolApkAdapter(coolApkItemList);
//        coolApkAdapter.setItemClickListener(new CoolApkAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                Intent intent = new Intent(getActivity(), DetailActivity.class);
//                intent.putExtra("app_site", "coolapk:https://www.coolapk.com" + coolApkItemList.get(position).getApp_site());
//                getActivity().startActivity(intent);
//            }
//        });
//        recyclerView.setAdapter(coolApkAdapter);
//        recyclerView.addOnScrollListener(new LoadMoreListener(layoutManager) {
//            @Override
//            public void onLoadMore(int currentPage) {
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
//            }
//        });
//
//        handler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                if (msg.what == 1){
//                    //int currentPage = (int)msg.obj;
//                    //Log.d("curentpage",""+currentPage);
//                    /*
//                    for (int i = 20 * currentPage; i < 20 * (currentPage + 1); i++) {
//                        //Log.d("测试：",sharedPreferences.getString("app_img_site_" + i, ""));
//                        coolApkItem = new CoolApkItem(sharedPreferences.getString("app_site_" + i, ""),
//                                sharedPreferences.getString("app_img_site_" + i, ""),
//                                sharedPreferences.getString("app_title_" + i, ""),
//                                sharedPreferences.getString("app_info_" + i, ""),
//                                sharedPreferences.getString("app_count_" + i, ""),
//                                sharedPreferences.getString("app_description_" + i, ""),sharedPreferences.getString("app_result_"+i,""));
//                        coolApkItemList.add(coolApkItem);
//                    }
//                    */
//                    tempCoolApkItemList = coolApkItemList;
//
//                    coolApkAdapter.notifyDataSetChanged();
//                    UIHelper.HideDilog();
//
//                } else if (msg.what == 2) {
//                    String string = (String) msg.obj;
//                    Log.d("string11111",string);
//                    String[] arr = string.split("\\.");
//                    int i = Integer.parseInt(arr[0]);
//                    coolApkItemList.get(i).setApp_result(arr[1]);
//                    tempCoolApkItemList = coolApkItemList;
//                    Log.d("iiiiiii","" + i);
//                    count++;
//                    Log.d("countcount",""+count);
//                    if (count % 20 == 0) {
//                        CountApp();
//                    }
//
//                    coolApkAdapter.notifyDataSetChanged();
//                }
//            }
//        };
//    }
//}
