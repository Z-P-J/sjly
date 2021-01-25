package com.zpj.shouji.market.ui.multidata;

import android.view.View;
import android.widget.ImageView;

import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.ExpandableMultiData;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.ui.widget.DownloadButton;

import java.util.List;

public class AppUrlMultiData extends ExpandableMultiData<AppDetailInfo.AppUrlInfo> {

    private final AppDetailInfo detailInfo;
    private final String id;

    public AppUrlMultiData(AppDetailInfo detailInfo, AppDetailInfo.AppUrlInfo appUrlInfo) {
        super();
        this.detailInfo = detailInfo;
        this.id = appUrlInfo.getUrlAdress().substring(appUrlInfo.getUrlAdress().lastIndexOf("id=") + 3);
        this.list.add(appUrlInfo);
        setExpand(false);
//        this.isLoaded = true;
        this.hasMore = false;
    }

    @Override
    public int getHeaderLayoutId() {
        return R.layout.item_app_url_header;
    }

    @Override
    public int getChildViewType(int position) {
        return R.layout.item_app_url;
    }

    @Override
    public int getChildLayoutId(int viewType) {
        return R.layout.item_app_url;
    }

    @Override
    public void onBindHeader(EasyViewHolder holder, List<Object> payloads) {
        AppDetailInfo.AppUrlInfo appUrlInfo = list.get(0);
        holder.setText(R.id.tv_title, appUrlInfo.getUrlName());
        updateIcon(holder, false);
        holder.setOnItemClickListener(v -> {
            if (isExpand()) {
                collapse();
            } else {
                expand();
            }
            updateIcon(holder, true);
        });


        DownloadButton downloadButton = holder.getView(R.id.tv_download);
        downloadButton.bindApp(id, detailInfo.getName(),
                detailInfo.getPackageName(), detailInfo.getAppType(),
                detailInfo.getIconUrl(), appUrlInfo.getYunUrl());

    }

    private void updateIcon(EasyViewHolder holder, boolean anim) {
        ImageView ivArrow = holder.getView(R.id.iv_arrow);
        if (anim) {
            float rotation;
            if (isExpand()) {
                ivArrow.setRotation(180);
                rotation = 90;
            } else {
                ivArrow.setRotation(90);
                rotation = 180;
            }
            ivArrow.animate()
                    .rotation(rotation)
                    .setDuration(360)
                    .start();
        } else {
            ivArrow.setRotation(isExpand() ? 90 : 180);
        }
    }

    @Override
    public void onBindChild(EasyViewHolder holder, List<AppDetailInfo.AppUrlInfo> list, int position, List<Object> payloads) {
        AppDetailInfo.AppUrlInfo appUrlInfo = list.get(position);
        String content = "文件Md5:" + appUrlInfo.getMd5() + "\n";
        holder.setText(R.id.tv_content,content +  appUrlInfo.getMore());
    }

    @Override
    public boolean loadData() {
        return false;
    }

}
