package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.api.SearchApi;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.GuessAppInfo;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.utils.Callback;

import java.util.List;

public class GuessYouLikeRecommendCard extends RecommendCard<GuessAppInfo> {

    public GuessYouLikeRecommendCard(Context context) {
        this(context, null);
    }

    public GuessYouLikeRecommendCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuessYouLikeRecommendCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init();
        setTitle("猜你喜欢");
    }

//    protected void init() {
//        SearchApi.getGuessApi(obj -> {
//            list.clear();
//            list.addAll(obj);
//            recyclerView.notifyDataSetChanged();
//        });
//    }

    @Override
    public void loadData(Runnable runnable) {
        SearchApi.getGuessApi(obj -> {
            list.clear();
            list.addAll(obj);
            recyclerView.notifyDataSetChanged();
            if (runnable != null) {
                runnable.run();
            }
        });
    }

    @Override
    protected void buildRecyclerView(EasyRecyclerView<GuessAppInfo> recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        for (int i = 0; i < recyclerView.getRecyclerView().getItemDecorationCount(); i++) {
            recyclerView.getRecyclerView().removeItemDecorationAt(i);
        }
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<GuessAppInfo> list, int position, List<Object> payloads) {
        GuessAppInfo info = list.get(position);
        holder.setText(R.id.tv_title, info.getAppTitle());
        holder.setText(R.id.tv_info, info.getAppSize());
        holder.setText(R.id.tv_desc, info.getAppComment());
        Glide.with(context).load(info.getAppIcon()).into(holder.getImageView(R.id.iv_icon));
        holder.getView(R.id.tv_download).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AToast.normal("TODO Download");
            }
        });
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, GuessAppInfo data) {
        AppDetailFragment.start(data);
    }

    @Override
    public int getItemRes() {
        return R.layout.item_app_linear;
    }

    @Override
    public void onMoreClicked(View v) {

    }
}
