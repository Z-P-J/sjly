package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.glide.transformations.blur.CropBlurTransformation;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionRecommendListFragment;
import com.zpj.shouji.market.ui.widget.CombineImageView;

import java.util.List;

public class CollectionRecommendCard extends RecommendCard<CollectionInfo> {

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
        ImageView imgBg = holder.getView(R.id.img_bg);

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
                .apply(
                        GlideRequestOptions.with()
                                .addTransformation(new CropBlurTransformation(25, 0.3f))
                                .centerCrop()
                                .roundedCorners(8)
                                .skipMemoryCache(true)
                                .get()
                )
                .into(imgBg);

        CombineImageView ivIcon = holder.getView(R.id.iv_icon);
        ivIcon.setUrls(info.getIcons());
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
