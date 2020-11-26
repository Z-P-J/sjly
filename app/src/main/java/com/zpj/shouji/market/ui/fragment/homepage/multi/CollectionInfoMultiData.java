//package com.zpj.shouji.market.ui.fragment.homepage.multi;
//
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.View;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
//import com.shehuan.niv.NiceImageView;
//import com.zpj.http.parser.html.nodes.Document;
//import com.zpj.http.parser.html.nodes.Element;
//import com.zpj.http.parser.html.select.Elements;
//import com.zpj.recyclerview.EasyViewHolder;
//import com.zpj.recyclerview.MultiAdapter;
//import com.zpj.recyclerview.MultiData;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.api.HttpApi;
//import com.zpj.shouji.market.api.HttpPreLoader;
//import com.zpj.shouji.market.api.PreloadApi;
//import com.zpj.shouji.market.glide.transformation.blur.CropBlurTransformation;
//import com.zpj.shouji.market.model.AppInfo;
//import com.zpj.shouji.market.model.CollectionInfo;
//import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
//import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
//import com.zpj.shouji.market.ui.widget.CombineImageView;
//import com.zpj.shouji.market.ui.widget.DownloadButton;
//
//import java.util.List;
//
//public class CollectionInfoMultiData extends MultiData<CollectionInfo> {
//
//    @Override
//    public int getSpanCount(int viewType) {
//        return 2;
//    }
//
//    @Override
//    public int getLayoutId(int viewType) {
//        return R.layout.item_app_collection;
//    }
//
//    @Override
//    public boolean loadData(RecyclerView recyclerView, MultiAdapter adapter) {
//        if (HttpPreLoader.getInstance().hasKey(PreloadApi.HOME_COLLECTION)) {
//            HttpPreLoader.getInstance()
//                    .setLoadListener(PreloadApi.HOME_COLLECTION, document -> onGetDoc(adapter, document));
//        } else {
//            HttpApi.collectionRecommend()
//                    .onSuccess(data -> onGetDoc(adapter, data))
//                    .subscribe();
//        }
//        return true;
//    }
//
//    @Override
//    public void onBindViewHolder(EasyViewHolder holder, List<CollectionInfo> list, int position, List<Object> payloads) {
//        CollectionInfo info = list.get(position);
//        NiceImageView imgBg = holder.getView(R.id.img_bg);
//
//        holder.setText(R.id.tv_title, info.getTitle());
//        holder.setText(R.id.tv_info, "共" + info.getAppSize() + "个应用");
//        holder.setText(R.id.tv_time, info.getTime());
//        holder.setText(R.id.tv_more_info, info.getViewCount() + "人气 · " + info.getSupportCount() + "赞 · " + info.getFavCount() + "收藏");
//        holder.setText(R.id.tv_creator, info.getNickName());
//
//        Glide.with(imgBg)
//                .load(info.getIcons().get(0))
//                .apply(RequestOptions.bitmapTransform(new CropBlurTransformation(25, 0.3f)))
////                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 8)))
//                .into(imgBg);
//
//        CombineImageView ivIcon = holder.getView(R.id.iv_icon);
//        ivIcon.setUrls(info.getIcons());
//
//        holder.setOnItemClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CollectionDetailFragment.start(info);
//            }
//        });
//    }
//
//    protected void onGetDoc(MultiAdapter adapter, Document document) {
//        Elements elements = document.select("item");
//        for (Element element : elements) {
//            list.add(CollectionInfo.create(element));
//        }
//        if (list.size() % 2 != 0) {
//            list.remove(list.size() - 1);
//        }
//        adapter.notifyDataSetChanged();
//    }
//
//}