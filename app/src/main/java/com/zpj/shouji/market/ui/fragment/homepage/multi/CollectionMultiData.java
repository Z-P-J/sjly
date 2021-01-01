package com.zpj.shouji.market.ui.fragment.homepage.multi;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.MultiAdapter;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.glide.transformations.RoundedTransformation;
import com.zpj.shouji.market.glide.transformations.blur.CropBlurTransformation;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionRecommendListFragment;
import com.zpj.shouji.market.ui.widget.CombineImageView;

public class CollectionMultiData extends RecyclerMultiData<CollectionInfo> {

    public CollectionMultiData() {
        super("应用集推荐");
    }

    public CollectionMultiData(String title) {
        super(title);
    }

    @Override
    public boolean loadData(final MultiAdapter adapter) {
        if (HttpPreLoader.getInstance().hasKey(PreloadApi.HOME_COLLECTION)) {
            HttpPreLoader.getInstance()
                    .setLoadListener(PreloadApi.HOME_COLLECTION, document -> onGetDoc(adapter, document));
        } else {
            HttpApi.collectionRecommend()
                    .onSuccess(data -> onGetDoc(adapter, data))
                    .subscribe();
        }
        return false;
    }

    private void onGetDoc(MultiAdapter adapter, Document document) {
        Log.d("CollectionRecommendCard", "onGetDoc document=" + document);
        Elements elements = document.select("item");
        for (Element element : elements) {
            list.add(CollectionInfo.create(element));
        }
        if (list.size() % 2 != 0) {
            list.remove(list.size() - 1);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public int getItemRes() {
        return R.layout.item_app_collection;
    }

    @Override
    public void buildRecyclerView(EasyRecyclerView<CollectionInfo> recyclerView) {
        Context context = recyclerView.getRecyclerView().getContext();
//        recyclerView.getRecyclerView().setNestedScrollingEnabled(true);
        recyclerView
                .setLayoutManager(new GridLayoutManager(context, 2,
                        LinearLayoutManager.HORIZONTAL, false))
                .addItemDecoration(new Y_DividerItemDecoration(context) {
                    @Override
                    public Y_Divider getDivider(int itemPosition) {
                        Y_DividerBuilder builder;
                        int color = Color.TRANSPARENT;
                        if (itemPosition == 0 || itemPosition == 1) {
                            builder = new Y_DividerBuilder()
                                    .setLeftSideLine(true, color, 12, 0, 0)
                                    .setRightSideLine(true, color, 4, 0, 0);
                        } else if (itemPosition == list.size() - 1 || itemPosition == list.size() - 2) {
                            builder = new Y_DividerBuilder()
                                    .setRightSideLine(true, color, 12, 0, 0)
                                    .setLeftSideLine(true, color, 4, 0, 0);
                        } else {
                            builder = new Y_DividerBuilder()
                                    .setLeftSideLine(true, color, 4, 0, 0)
                                    .setRightSideLine(true, color, 4, 0, 0);
                        }
                        return builder.setTopSideLine(true, color, 4, 0, 0)
                                .setBottomSideLine(true, color, 4, 0, 0)
                                .create();
                    }
                })
                .onBindViewHolder((holder, list, position, payloads) -> {

                    CollectionInfo info = list.get(position);
                    ImageView imgBg = holder.getView(R.id.img_bg);

                    holder.setText(R.id.tv_title, info.getTitle());
                    holder.setText(R.id.tv_info, "共" + info.getAppSize() + "个应用");
                    holder.setText(R.id.tv_time, info.getTime());
                    holder.setText(R.id.tv_more_info, info.getViewCount() + "人气 · " + info.getSupportCount() + "赞 · " + info.getFavCount() + "收藏");
                    holder.setText(R.id.tv_creator, info.getNickName());
                    Glide.with(context)
                            .load(info.getIcons().get(0))
                            .apply(GlideRequestOptions.with()
                                    .addTransformation(new CropBlurTransformation(25, 0.3f))
                                    .centerCrop()
//                                    .roundedCorners(8)
                                    .addTransformation(new RoundedTransformation(8, 2, Color.WHITE))
                                    .get()
                                    .error(R.drawable.bg_button_round_gray))
                            .into(imgBg);

                    CombineImageView ivIcon = holder.getView(R.id.iv_icon);
                    ivIcon.setUrls(info.getIcons());

                    holder.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CollectionDetailFragment.start(info);
                        }
                    });
                });
    }

    @Override
    public void onHeaderClick() {
        CollectionRecommendListFragment.start();
    }
}
