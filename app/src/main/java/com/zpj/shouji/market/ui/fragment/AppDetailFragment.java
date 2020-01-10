package com.zpj.shouji.market.ui.fragment;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.felix.atoast.library.AToast;
import com.zpj.http.ZHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.bean.AppItem;
import com.zpj.shouji.market.bean.AppUpdateInfo;
import com.zpj.shouji.market.bean.ImgItem;
import com.zpj.shouji.market.bean.InstalledAppInfo;
import com.zpj.shouji.market.bean.UserDownloadedAppInfo;
import com.zpj.shouji.market.ui.adapter.ImgAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.utils.ColorHelper;
import com.zpj.shouji.market.utils.TransportUtil;

import java.util.ArrayList;
import java.util.List;

public class AppDetailFragment extends BaseFragment {

    private final static String KEY = "app_site";

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
    private ImgAdapter imgAdapter;
    private List<ImgItem> imgItemList = new ArrayList<>();

    private Bitmap icon;
    private AppItem item;

    public static AppDetailFragment newInstance(String site) {
        Bundle args = new Bundle();
        args.putString(KEY, site);
        AppDetailFragment fragment = new AppDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static AppDetailFragment newInstance(String type, String id) {
        String site;
        if ("game".equals(type)) {
            site = "sjly:http://tt.shouji.com.cn/androidv3/game_show.jsp?id=" + id;
        } else {
            site = "sjly:http://tt.shouji.com.cn/androidv3/soft_show.jsp?id=" + id;
        }
        return newInstance(site);
    }

    public static AppDetailFragment newInstance(AppItem item) {
        return newInstance(item.getAppType(), item.getAppId());
    }

    public static AppDetailFragment newInstance(AppUpdateInfo item) {
        return newInstance(item.getAppType(), item.getId());
    }

    public static AppDetailFragment newInstance(InstalledAppInfo appInfo) {
        return newInstance(appInfo.getAppType(), appInfo.getId());
    }

    public static AppDetailFragment newInstance(UserDownloadedAppInfo info) {
        return newInstance(info.getAppType(), info.getId());
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        app_site = getArguments().getString(KEY);
        app_site = app_site.substring(5);
        requstCode = 3;
        icon = TransportUtil.getInstance().getIconBitmap();
        item = TransportUtil.getInstance().getAppItem();

        rootView = view.findViewById(R.id.root);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);

        app_icon = view.findViewById(R.id.app_icon);
        app_info_view = view.findViewById(R.id.app_info);
        yingyongjianjie_view = view.findViewById(R.id.yingyongjianjie);
        xinbantexing_view = view.findViewById(R.id.xinbantexing);
        xiangxixinxi_view = view.findViewById(R.id.xiangxixinxi);
        quanxianxinxi_view = view.findViewById(R.id.quanxianxinxi);

        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(app_site);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        recyclerView = view.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
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

        getSjlyDetail(app_site);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AToast.normal("下载=" + app_site);
            }
        });
    }

    private void getSjlyDetail(final String app_site){
        Log.d("apppppp", app_site);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc  = ZHttp.connect(app_site)
//                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .toHtml();
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
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {

                        imgAdapter.notifyDataSetChanged();
                        if (item == null) {
                            collapsingToolbarLayout.setTitle(app_name);
                            app_info_view.setText(app_info);
                            Log.d("app_icon_site", app_icon_site);
                        }


//                    Picasso.get().load(app_icon_site).into(app_icon);
                        if (icon == null) {
                            Glide.with(getContext())
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
                    }
                });
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
                            }
                        });
                        colorAnim2.setDuration(300);
                        colorAnim2.setRepeatMode(ValueAnimator.RESTART);
                        colorAnim2.start();

                        if (Build.VERSION.SDK_INT >= 21) {
                            Window window = getActivity().getWindow();
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
