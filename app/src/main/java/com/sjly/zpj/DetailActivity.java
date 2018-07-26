package com.sjly.zpj;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sjly.zpj.adapter.ImgAdapter;
import com.sjly.zpj.fragment.ImgItem;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private String app_site;
    private String app_icon_site;
    private String app_name;
    private String app_info;
    private String yingyongjianjie = "";
    private String xinbantexing;
    private String xiangxixinxi;
    private String quanxianxinxi;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView app_icon;
    private TextView app_info_view;
    private TextView yingyongjianjie_view;
    private TextView xinbantexing_view;
    private TextView xiangxixinxi_view;
    private TextView quanxianxinxi_view;
    private FloatingActionButton floatingActionButton;

    private int requstCode;
    private RecyclerView recyclerView;
    private ImgItem imgItem;
    private Handler handler;
    private ImgAdapter imgAdapter;
    private List<ImgItem> imgItemList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        app_site = getIntent().getStringExtra("app_site");
        if (app_site.startsWith("coolapk:")) {
            app_site = app_site.substring(8);
            requstCode = 1;
        } else if (app_site.startsWith("qianqian:")) {
            app_site = app_site.substring(9);
            requstCode = 2;
        } else if (app_site.startsWith("xinhai:")) {
            app_site = app_site.substring(7);
            requstCode = 3;
        }
        Toast.makeText(this, app_site, Toast.LENGTH_SHORT).show();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    collapsingToolbarLayout.setTitle(app_name);
                    Log.d("app_icon_site",app_icon_site);

                    Picasso.get().load(app_icon_site).into(app_icon);
                    app_info_view.setText(app_info);
                    yingyongjianjie_view.setText(yingyongjianjie);
                    xinbantexing_view.setText(xinbantexing);
                    xiangxixinxi_view.setText(xiangxixinxi);
                    quanxianxinxi_view.setText(quanxianxinxi);
                    imgAdapter.notifyDataSetChanged();
                }
            }
        };
        initView(requstCode);
    }


    private void initView(int requstCode){

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        app_icon = (ImageView)findViewById(R.id.app_icon);
        app_info_view = (TextView)findViewById(R.id.app_info);
        yingyongjianjie_view = (TextView)findViewById(R.id.yingyongjianjie);
        xinbantexing_view = (TextView)findViewById(R.id.xinbantexing);
        xiangxixinxi_view = (TextView)findViewById(R.id.xiangxixinxi);
        quanxianxinxi_view = (TextView)findViewById(R.id.quanxianxinxi);

        floatingActionButton = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(app_site);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        imgAdapter = new ImgAdapter(imgItemList);
        recyclerView.setAdapter(imgAdapter);

        switch (requstCode) {
            case 1:
                getCoolApkDetail(app_site);
                break;
            case 2:
                getQianQianDetail(app_site);
                break;
            case 3:
                getXinHaiDetail(app_site);
                break;
        }

    }

    private void getCoolApkDetail(final String app_site){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(app_site)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .get();
                    Elements elements = doc.select("div.ex-screenshot-thumb-carousel").select("img");
                    for (Element element : elements) {
                        imgItem = new ImgItem(element.attr("src"));
                        imgItemList.add(imgItem);
                    }
                    app_name = doc.select("p.detail_app_title").text();
                    app_icon_site = doc.select("div.apk_topbar").select("img").get(0).attr("src");
                    app_info = doc.select("p.apk_topba_message").text();

                    /*
                    for (Element element : doc.select("div.apk_left_title_info").get(0).select("p")) {
                        if (yingyongjianjie.equals("")) {
                            yingyongjianjie = element.text();
                        } else {
                            yingyongjianjie = yingyongjianjie + "\n\n" + element.text();
                        }
                    }
                    */

                    //yingyongjianjie = doc.select("div.apk_left_title_info").get(0).text();
                    if (doc.select("p.apk_left_title_nav").get(0).text().equals("酷安点评")) {
                        xinbantexing = doc.select("p.apk_left_title_info").get(1).toString();
                        xiangxixinxi = doc.select("p.apk_left_title_info").get(2).toString();
                    }else {
                        xinbantexing = doc.select("p.apk_left_title_info").get(0).toString();
                        xiangxixinxi = doc.select("p.apk_left_title_info").get(1).toString();
                    }

                    xinbantexing = xinbantexing
                            .replaceAll("<br>","\n")
                            .replace("<p class=\"apk_left_title_info\">","")
                            .replace("</p>","")
                            .replace("&nbsp;","");

                    xiangxixinxi = xiangxixinxi
                            .replaceAll("<br>","\n")
                            .replace("<p class=\"apk_left_title_info\">","")
                            .replace("</p>","")
                            .trim();

                    quanxianxinxi = doc.select("div.apk_left_title_info").get(1).toString()
                            .replaceAll("<br>","\n")
                            .replace("<div class=\"apk_left_title_info\">","")
                            .replace("</div>","")
                            .trim();

                    Log.d("cccccccc",doc.select("div.apk_left_title_info").get(0).toString());



                    if (doc.select("div.apk_left_title_info").get(0).toString().replace("</div>","").replace("</p>","").contains("</p>")) {
                        for (Element element : doc.select("div.apk_left_title_info").get(0).select("p")) {
                            if (yingyongjianjie.equals("")) {
                                yingyongjianjie = element.text();
                            } else {
                                yingyongjianjie = yingyongjianjie + "\n\n" + element.text();
                            }
                        }
                        Log.d("ccccccc","1");
                    }else {
                        yingyongjianjie = doc.select("div.apk_left_title_info").get(0).toString()
                                .replaceAll("<br>","\n")
                                .replace("<div class=\"apk_left_title_info\">","")
                                .replace("</div>","")
                                .replace("<p>","")
                                .replace("</p>","")
                                .trim();
                        Log.d("ccccccc","2");
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

    private void getQianQianDetail(String app_site){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getXinHaiDetail(String app_site){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
