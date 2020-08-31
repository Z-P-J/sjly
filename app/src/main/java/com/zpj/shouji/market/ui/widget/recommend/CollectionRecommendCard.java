package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lwkandroid.widget.ninegridview.NineGridBean;
import com.lwkandroid.widget.ninegridview.NineGridView;
import com.othershe.combinebitmap.CombineBitmap;
import com.othershe.combinebitmap.layout.DingLayoutManager;
import com.othershe.combinebitmap.layout.WechatLayoutManager;
import com.shehuan.niv.NiceImageView;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.glide.blur.CropBlurTransformation;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.ui.adapter.DiscoverBinder;
import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionRecommendListFragment;
import com.zpj.shouji.market.ui.widget.CombineImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionRecommendCard extends RecommendCard<CollectionInfo> {

    private final int[] RES_ICONS = {R.id.item_icon_1, R.id.item_icon_2, R.id.item_icon_3};

    public CollectionRecommendCard(Context context) {
        this(context, null);
    }

    public CollectionRecommendCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollectionRecommendCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTitle("应用集推荐");
//        init();
    }

    @Override
    public void loadData(Runnable runnable) {
        if (HttpPreLoader.getInstance().hasKey(PreloadApi.HOME_COLLECTION)) {
            HttpPreLoader.getInstance()
                    .setLoadListener(PreloadApi.HOME_COLLECTION, document -> onGetDoc(document, runnable));
        } else {
            HttpApi.collectionRecommend()
                    .onSuccess(data -> onGetDoc(data, runnable))
                    .subscribe();
        }
    }

//    protected void init() {
//        if (HttpPreLoader.getInstance().hasKey(PreloadApi.HOME_COLLECTION)) {
//            HttpPreLoader.getInstance().setLoadListener(PreloadApi.HOME_COLLECTION, this::onGetDoc);
//        } else {
//            HttpApi.collectionRecommend()
//                    .onSuccess(this::onGetDoc)
//                    .subscribe();
//        }
//    }



    private void onGetDoc(Document document, Runnable runnable) {
        Log.d("CollectionRecommendCard", "onGetDoc document=" + document);
        Elements elements = document.select("item");
        for (Element element : elements) {
            list.add(CollectionInfo.create(element));
        }
        if (list.size() % 2 != 0) {
            list.remove(list.size() - 1);
        }
        recyclerView.notifyDataSetChanged();
        if (runnable != null) {
            runnable.run();
        }
    }

    @Override
    protected void buildRecyclerView(EasyRecyclerView<CollectionInfo> recyclerView) {
        recyclerView.setLayoutManager(
                new GridLayoutManager(context, 2,
                        LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<CollectionInfo> list, int position, List<Object> payloads) {
        CollectionInfo info = list.get(position);
        NiceImageView imgBg = holder.getView(R.id.img_bg);

        holder.setText(R.id.tv_title, info.getTitle());
        holder.setText(R.id.tv_info, "共" + info.getAppSize() + "个应用");
        holder.setText(R.id.tv_time, info.getTime());
        holder.setText(R.id.tv_more_info, info.getViewCount() + "人气 · " + info.getSupportCount() + "赞 · " + info.getFavCount() + "收藏");
        holder.setText(R.id.tv_creator, info.getNickName());

//        NineGridView gridImageView = holder.getView(R.id.grid_image_view);
//        gridImageView.setImageLoader(DiscoverBinder.getImageLoader());
//        List<NineGridBean> gridList = new ArrayList<>();
//        for (String url : info.getIcons()) {
//            gridList.add(new NineGridBean(url));
//            if (gridList.size() >= 4) {
//                break;
//            }
//        }
//        gridImageView.setDataList(gridList);

        Glide.with(context)
                .load(info.getIcons().get(0))
                .apply(RequestOptions.bitmapTransform(new CropBlurTransformation(25, 0.3f)))
//                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 8)))
                .into(imgBg);

        CombineImageView ivIcon = holder.getView(R.id.iv_icon);
        ivIcon.setUrls(info.getIcons());

//        String[] urls = new String[Math.min(info.getIcons().size(), 4)];
//
//        for (int i = 0; i < urls.length; i++) {
//            urls[i] = info.getIcons().get(i);
//        }
//
//        CombineBitmap.init(context)
//                .setLayoutManager(new DingLayoutManager()) // 必选， 设置图片的组合形式，支持WechatLayoutManager、DingLayoutManager
//                .setSize(48) // 必选，组合后Bitmap的尺寸，单位dp
//                .setGap(2) // 单个图片之间的距离，单位dp，默认0dp
//                .setGapColor(getResources().getColor(R.color.color_background_gray)) // 单个图片间距的颜色，默认白色
////                .setPlaceholder() // 单个图片加载失败的默认显示图片
//                .setUrls(urls) // 要加载的图片url数组
//                .setImageView(holder.getImageView(R.id.iv_icon)) // 直接设置要显示图片的ImageView
//                // 设置“子图片”的点击事件，需使用setImageView()，index和图片资源数组的索引对应
//                .build();

    }

    @Override
    public void onClick(EasyViewHolder holder, View view, CollectionInfo data) {
        CollectionDetailFragment.start(data);
    }

    @Override
    public int getItemRes() {
        return R.layout.item_app_collection;
    }

    @Override
    public void onMoreClicked(View v) {
        CollectionRecommendListFragment.start();
    }
}
