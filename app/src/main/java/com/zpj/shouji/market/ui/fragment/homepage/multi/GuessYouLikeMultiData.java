package com.zpj.shouji.market.ui.fragment.homepage.multi;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.MultiAdapter;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.SearchApi;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.model.GuessAppInfo;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.widget.DownloadButton;

import java.util.List;

public class GuessYouLikeMultiData extends BaseHeaderMultiData<GuessAppInfo> {

    public GuessYouLikeMultiData(String title) {
        super(title);
    }

    @Override
    public boolean loadData(MultiAdapter adapter) {
        SearchApi.getGuessApi(obj -> {
            list.clear();
            list.addAll(obj);
            adapter.notifyDataSetChanged();
        });
        return false;
    }

    @Override
    public int getChildSpanCount(int viewType) {
        return 4;
    }

    @Override
    public int getChildViewType(int position) {
        return R.layout.item_app_linear;
    }

    @Override
    public boolean hasChildViewType(int viewType) {
        return viewType == R.layout.item_app_linear;
    }

    @Override
    public int getChildLayoutId(int viewType) {
        return R.layout.item_app_linear;
    }

    @Override
    public void onBindChild(EasyViewHolder holder, List<GuessAppInfo> list, int position, List<Object> payloads) {
        GuessAppInfo info = list.get(position);
        holder.setText(R.id.tv_title, info.getAppTitle());
        holder.setText(R.id.tv_info, info.getAppSize());
        holder.setText(R.id.tv_desc, info.getAppComment());
        ImageView ivIcon = holder.getImageView(R.id.iv_icon);
        Glide.with(ivIcon)
                .load(info.getAppIcon())
                .apply(GlideRequestOptions.getDefaultIconOption())
                .into(ivIcon);
        DownloadButton downloadButton = holder.getView(R.id.tv_download);
        downloadButton.bindApp(info);

        holder.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDetailFragment.start(info);
            }
        });
    }

    @Override
    public void onBindHeader(EasyViewHolder holder, List<Object> payloads) {
        super.onBindHeader(holder, payloads);
        holder.setVisible(R.id.tv_more, false);
    }

    @Override
    public void onHeaderClick() {

    }

}
