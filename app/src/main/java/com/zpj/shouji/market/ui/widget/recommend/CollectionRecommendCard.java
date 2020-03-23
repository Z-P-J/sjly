package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.zpj.shouji.market.glide.blur.BlurTransformation2;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;

import org.greenrobot.eventbus.EventBus;

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

        if (HttpPreLoader.getInstance().hasKey(HttpPreLoader.HOME_COLLECTION)) {
            HttpPreLoader.getInstance().setLoadListener(HttpPreLoader.HOME_COLLECTION, this::onGetDoc);
        } else {
            HttpApi.collectionRecommend()
                    .onSuccess(this::onGetDoc)
                    .subscribe();
        }
    }

    private void onGetDoc(Document document) {
        Elements elements = document.select("item");
        for (Element element : elements) {
            list.add(CollectionInfo.create(element));
        }
        if (list.size() % 2 != 0) {
            list.remove(list.size() - 1);
        }
        recyclerView.notifyDataSetChanged();
    }

    @Override
    protected void buildRecyclerView(EasyRecyclerView<CollectionInfo> recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<CollectionInfo> list, int position, List<Object> payloads) {
        CollectionInfo info = list.get(position);
        NiceImageView imgBg = holder.getView(R.id.img_bg);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.getItemView().getLayoutParams();
//        if (position % 2 == 0) {
//            if (position == 0) {
//                params.setMargins(margin, 0, margin / 2, margin / 2);
//            } else if (position == list1.size() - 2) {
//                params.setMargins(margin / 2, 0, margin, margin / 2);
//            } else {
//                params.setMargins(margin / 2, 0, margin / 2, margin / 2);
//            }
//        } else {
//            if (position == 1) {
//                params.setMargins(margin, margin / 2, margin / 2, 0);
//            } else if (position == list1.size() - 1) {
//                params.setMargins(margin / 2, margin / 2, margin, 0);
//            } else {
//                params.setMargins(margin / 2, margin / 2, margin / 2, 0);
//            }
//        }

        holder.getTextView(R.id.item_title).setText(info.getTitle());
        holder.setText(R.id.tv_view_count, info.getViewCount() + "");
        holder.setText(R.id.tv_favorite_count, info.getFavCount() + "");
        holder.setText(R.id.tv_support_count, info.getSupportCount() + "");
        for (int i = 0; i < RES_ICONS.length; i++) {
            int res = RES_ICONS[i];
            if (i == 0) {
                Glide.with(context)
                        .load(info.getIcons().get(0))
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation2(0.1f, 1 / 4f)))
                        .into(imgBg);
            }
            Glide.with(context).load(info.getIcons().get(i)).into(holder.getImageView(res));
        }
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, CollectionInfo data) {
        EventBus.getDefault().post(CollectionDetailFragment.newInstance(data));
    }

    @Override
    public int getItemRes() {
        return R.layout.item_app_collection;
    }
}
