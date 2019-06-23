package com.zpj.sjly;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zpj.sjly.adapter.ImgAdapter;
import com.zpj.sjly.model.AppItem;
import com.zpj.sjly.model.ImgItem;
import com.zpj.sjly.utils.TransportUtil;
import com.zpj.sjly.utils.ColorHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private String app_site;
    private String app_icon_site = "";
    private String app_name;
    private String app_info;
    private String yingyongjianjie = "";
    private String xinbantexing = "";
    private String xiangxixinxi = "";
    private String quanxianxinxi = "";
    private String apkDownloadUrl;
    private View rootView;
    private android.support.v7.widget.Toolbar toolbar;
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

    private Bitmap icon;
    private AppItem item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        app_site = getIntent().getStringExtra("app_site");
        if (app_site.startsWith("sjly:")) {
            app_site = app_site.substring(5);
            requstCode = 3;
//            Bundle bundle = getIntent().getExtras();
//            if (bundle != null) {
//                icon = bundle.getParcelable("bitmap_icon");
//                item = bundle.getParcelable("app_item");
//            }
            icon = TransportUtil.getInstance().getIconBitmap();
            item = TransportUtil.getInstance().getAppItem();
        } else if (app_site.startsWith("coolapk:")) {
            app_site = app_site.substring(8);
            requstCode = 1;
        } else if (app_site.startsWith("appchina:")){
            app_site = app_site.substring(9);
            requstCode = 2;
        }
        Toast.makeText(this, app_site, Toast.LENGTH_SHORT).show();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    if (item == null) {
                        collapsingToolbarLayout.setTitle(app_name);
                        app_info_view.setText(app_info);
                        Log.d("app_icon_site", app_icon_site);
                    }


//                    Picasso.get().load(app_icon_site).into(app_icon);
                    if (icon == null) {
                        Glide.with(DetailActivity.this)
                                .asBitmap()
                                .load(app_icon_site)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        getColor(resource);
                                        app_icon.setImageBitmap(resource);
                                    }
                                });
                    }

                    yingyongjianjie_view.setText(yingyongjianjie.isEmpty() ? "无" : yingyongjianjie);
                    xinbantexing_view.setText(xinbantexing.isEmpty() ? "无" : xinbantexing);
                    xiangxixinxi_view.setText(xiangxixinxi.isEmpty() ? "无" : xiangxixinxi);
                    quanxianxinxi_view.setText(quanxianxinxi.isEmpty() ? "无" : quanxianxinxi);
                    imgAdapter.notifyDataSetChanged();
                }
            }
        };
        initView(requstCode);
    }


    private void initView(int requstCode){
        rootView = findViewById(R.id.root);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        app_icon = findViewById(R.id.app_icon);
        app_info_view = findViewById(R.id.app_info);
        yingyongjianjie_view = findViewById(R.id.yingyongjianjie);
        xinbantexing_view = findViewById(R.id.xinbantexing);
        xiangxixinxi_view = findViewById(R.id.xiangxixinxi);
        quanxianxinxi_view = findViewById(R.id.quanxianxinxi);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(app_site);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        imgAdapter = new ImgAdapter(imgItemList);
        recyclerView.setAdapter(imgAdapter);

        if (icon != null) {
            getColor(icon);
            app_icon.setImageBitmap(icon);
        }
        if (item != null) {
            collapsingToolbarLayout.setTitle(item.getAppTitle());
            app_info_view.setText(item.getAppSize() + " | " + item.getAppInfo());
        }

        switch (requstCode) {
            case 1:
                getCoolApkDetail(app_site);
                break;
            case 2:
                getAppChinaDetail(app_site);
                break;
            case 3:
                getSjlyDetail(app_site);
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DetailActivity.this, "下载=" + app_site, Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void getAppChinaDetail(final String app_site){
        Log.d("apppppp", app_site);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc  = Jsoup.connect(app_site)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .get();
                    Elements elements = doc.select("ul.app-screenshot-list").select("li");
                    for (Element element : elements){
                        imgItem = new ImgItem(element.select("img").attr("src"));
                        imgItemList.add(imgItem);
                    }
                    app_name = doc.select("h1.app-name").text();
                    app_icon_site = doc.select("div.msg").select("img.Content_Icon").attr("src");
                    app_info = doc.select("span.app-statistic").text();
                    elements = doc.select("div.detail-app-other-info").select("li");
                    for (Element element : elements) {
                        app_info = app_info + "/" + element.text();
                    }

                    xinbantexing = doc.select("div.main-info").select("p.art-content").get(1).text();
                    yingyongjianjie = doc.select("div.main-info").select("p.art-content").get(0).toString();
                    yingyongjianjie = yingyongjianjie.replace("<p class=\"art-content\">", "").replace("</p>", "");
                    yingyongjianjie = yingyongjianjie.replaceAll("<br>","\n");
                    while(yingyongjianjie.contains("</a>")) {
                        Document document = Jsoup.parse(yingyongjianjie);
                        yingyongjianjie = yingyongjianjie.substring(0, yingyongjianjie.indexOf("<a"))
                                + document.select("a").get(0).text() + yingyongjianjie.substring(yingyongjianjie.indexOf("</a>") + 4);
                    }
                    //yingyongjianjie.substring(23).substring(0, yingyongjianjie.indexOf("</p>"));

                    elements = doc.select("div.other-info").select("p.art-content");
                    for (Element element : elements){
                        if (xiangxixinxi.equals("")){
                            xiangxixinxi = xiangxixinxi + element.text();
                        }else {
                            xiangxixinxi = xiangxixinxi + "\n" + element.text();
                        }
                    }
                    elements = doc.select("ul.permissions-list").select("li");
                    for (Element element : elements) {
                        if (quanxianxinxi.equals("")) {
                            quanxianxinxi = quanxianxinxi + element.text();
                        }else {
                            quanxianxinxi = quanxianxinxi + "\n" + element.text();
                        }
                    }
                    apkDownloadUrl = doc.select("a.download_app").attr("onclick");
                    Log.d("apkDownloadUrl", apkDownloadUrl);




                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void getSjlyDetail(final String app_site){
        Log.d("apppppp", app_site);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc  = Jsoup.connect(app_site)
//                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .get();
                    Elements elements = doc.select("pics").select("pic");
                    for (Element element : elements){
                        imgItem = new ImgItem(element.text());
                        imgItemList.add(imgItem);
                    }
                    Log.d("getSjlyDetail", "imgItemList=" + imgItemList);
                    app_name = doc.select("name").get(0).text();
                    Log.d("getSjlyDetail", "app_name=" + app_name);
                    app_icon_site = doc.select("icon").get(0).text();
                    Log.d("getSjlyDetail", "app_icon_site=" + app_icon_site);
//                    Log.d("getSjlyDetail", "baseinfof=" + doc.select("baseinfof").text());
                    if (doc.select("baseinfof").hasText()) {
                        app_info = doc.select("baseinfof").get(0).text();
                    } else {
                        app_info = doc.select("lineinfo").get(0).text();
                    }
                    Log.d("getSjlyDetail", "app_info=" + app_info);

                    elements = doc.select("introduces").select("introduce");
                    for (Element introduce : elements) {
                        String introduceType = introduce.select("introducetype").get(0).text();
                        Log.d("getSjlyDetail", "introduceType=" + introduceType);
                        String introduceTitle = introduce.select("introducetitle").get(0).text();
                        Log.d("getSjlyDetail", "introduceTitle=" + introduceTitle);
                        if ("permission".equals(introduceType)) {
                            Elements permissions = introduce.select("permissions").select("permission");
                            for (Element permission : permissions) {
                                if (quanxianxinxi.equals("")) {
                                    quanxianxinxi = quanxianxinxi + permission.text();
                                }else {
                                    quanxianxinxi = quanxianxinxi + "\n" + permission.text();
                                }
                            }
                            Log.d("getSjlyDetail", "quanxianxinxi=" + quanxianxinxi);
                        } else if ("text".equals(introduceType)) {
                            if ("软件信息".equals(introduceTitle) || "游戏信息".equals(introduceTitle)) {
                                xiangxixinxi = introduce.select("introduceContent").get(0).text().replaceAll(" ", "\n");
                            } else if ("软件简介".equals(introduceTitle) || "游戏简介".equals(introduceTitle)) {
                                yingyongjianjie = introduce.select("introduceContent").get(0).text();
                                Log.d("getSjlyDetail", "yingyongjianjie=" + yingyongjianjie);
                            } else if ("更新内容".equals(introduceTitle)) {
                                xinbantexing = introduce.select("introduceContent").get(0).text();
                                Log.d("getSjlyDetail", "xinbantexing=" + xinbantexing);
                            }
                        }
                    }

//                    elements = doc.select("div.other-info").select("p.art-content");
//                    for (Element element : elements){
//                        if (xiangxixinxi.equals("")){
//                            xiangxixinxi = xiangxixinxi + element.text();
//                        }else {
//                            xiangxixinxi = xiangxixinxi + "\n" + element.text();
//                        }
//                    }

                    apkDownloadUrl = "http://tt.shouji.com.cn/wap/down/soft?id=" + doc.select("id").get(0).text();
                    Log.d("apkDownloadUrl", apkDownloadUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }).start();
    }

    public void getColor(Bitmap bitmap) {
        // Palette的部分
        Palette.Builder builder = Palette.from(bitmap);
        builder.generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                //获取到充满活力的这种色调
                Palette.Swatch vibrant = palette.getMutedSwatch();
                //根据调色板Palette获取到图片中的颜色设置到toolbar和tab中背景，标题等，使整个UI界面颜色统一
                if (rootView != null) {
                    if (vibrant != null) {
                        ValueAnimator colorAnim2 = ValueAnimator.ofArgb(Color.rgb(110, 110, 100), ColorHelper.colorBurn(vibrant.getRgb()));
                        colorAnim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                rootView.setBackgroundColor((Integer) animation.getAnimatedValue());
                                // toolbar.setBackgroundColor((Integer) animation.getAnimatedValue());
                                toolbar.setBackgroundColor((Integer) animation.getAnimatedValue());
                            }
                        });
                        colorAnim2.setDuration(300);
                        colorAnim2.setRepeatMode(ValueAnimator.RESTART);
                        colorAnim2.start();

                        if (Build.VERSION.SDK_INT >= 21) {
                            Window window = getWindow();
                            window.setStatusBarColor(ColorHelper.colorBurn(vibrant.getRgb()));
                            int barColor = ColorHelper.colorBurn(vibrant.getRgb());
                            window.setNavigationBarColor(barColor);
                        }
                    }
                }

            }
        });
    }
}
