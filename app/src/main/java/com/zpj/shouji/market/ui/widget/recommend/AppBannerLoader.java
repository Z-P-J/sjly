package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.geek.banner.loader.BannerEntry;
import com.geek.banner.loader.BannerLoader;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.widget.DownloadButton;

public class AppBannerLoader implements BannerLoader<AppInfo, View> {

    @Override
    public void loadView(Context context, BannerEntry entry, int position, View itemView) {
        ImageView mImageView = itemView.findViewById(R.id.img_view);
        ImageView ivIcon = itemView.findViewById(R.id.iv_icon);
        TextView tvTitle = itemView.findViewById(R.id.tv_title);
        TextView tvInfo = itemView.findViewById(R.id.tv_info);
        DownloadButton downloadButton = itemView.findViewById(R.id.tv_download);


        AppInfo appInfo = (AppInfo) entry.getBannerPath();
        Glide.with(context).load(appInfo.getAppIcon()).into(mImageView);
        Glide.with(context).load(appInfo.getAppIcon())
                .apply(
                        GlideRequestOptions.with()
                                .centerCrop()
                                .roundedCorners(4)
                                .get()
                )
                .into(ivIcon);
        tvTitle.setText(appInfo.getAppTitle());
        tvInfo.setText(appInfo.getAppSize());
        downloadButton.bindApp(appInfo);
    }

    @Override
    public View createView(Context context, int position) {
        return LayoutInflater.from(context).inflate(R.layout.item_banner_recommend, null, false);
    }

}
