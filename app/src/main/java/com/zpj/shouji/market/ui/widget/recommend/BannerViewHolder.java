package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhouwei.mzbanner.holder.MZViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppInfo;

public class BannerViewHolder implements MZViewHolder<AppInfo> {
    private ImageView mImageView;
    private ImageView ivIcon;

    private TextView tvTitle;
    private TextView tvInfo;

    @Override
    public View createView(Context context) {
        // 返回页面布局
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner_recommend, null, false);
        mImageView = view.findViewById(R.id.img_view);
        ivIcon = view.findViewById(R.id.iv_icon);
        tvTitle = view.findViewById(R.id.tv_title);
        tvInfo = view.findViewById(R.id.tv_info);
        return view;
    }

    @Override
    public void onBind(Context context, int position, AppInfo item) {
        Glide.with(context).load(item.getAppIcon()).into(mImageView);
        Glide.with(context).load(item.getAppIcon()).into(ivIcon);
        tvTitle.setText(item.getAppTitle());
        tvInfo.setText(item.getAppSize());
    }
}
