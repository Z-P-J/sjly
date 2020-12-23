package com.zpj.shouji.market.ui.fragment.collection;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zpj.toast.ZToast;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.model.CollectionAppInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.widget.DownloadButton;
import com.zpj.shouji.market.ui.widget.emoji.EmojiExpandableTextView;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.utils.ScreenUtils;

import org.w3c.dom.Text;

import java.util.List;

public class CollectionAppListFragment extends NextUrlFragment<CollectionAppInfo> {

    public static CollectionAppListFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, "http://tt.shouji.com.cn/app/yyj_applist.jsp?t=discuss&parent=" + id);
        CollectionAppListFragment fragment = new CollectionAppListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_collection_list;
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<CollectionAppInfo> recyclerLayout) {
        recyclerLayout.onViewClick(R.id.tv_download, new IEasy.OnClickListener<CollectionAppInfo>() {
            @Override
            public void onClick(EasyViewHolder holder, View view, CollectionAppInfo data) {
                if (data.isApkExist()) {
                    // TODO
                    ZToast.success("TODO 开始下载");
                } else {
                    ZToast.warning("应用未收录");
                }
            }
        });
        recyclerLayout.getRecyclerView().addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
//                DownloadButton tvDownload = view.findViewById(R.id.tv_download);
//                tvDownload.onChildViewDetachedFromWindow();
            }
        });
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, CollectionAppInfo data) {
        if (data.isApkExist()) {
            AppDetailFragment.start(data);
        } else {
            ZToast.warning("应用未收录");
        }
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<CollectionAppInfo> list, int position, List<Object> payloads) {
        final CollectionAppInfo appItem = list.get(position);
        holder.setText(R.id.tv_title, appItem.getTitle());

        holder.getTextView(R.id.tv_info).setText(appItem.getPackageName());

        EmojiExpandableTextView tvDesc = holder.getView(R.id.tv_desc);
        tvDesc.setContent(appItem.getComment());

        DownloadButton tvDownload = holder.getView(R.id.tv_download);
        if (appItem.isApkExist()) {
            tvDownload.setText("下载");
            tvDownload.bindApp(appItem);
//            tvDownload.setTextColor(getResources().getColor(R.color.colorPrimary));
//            tvDownload.setBackgroundResource(R.drawable.bg_download_button);
        } else {
            tvDownload.setText("查看");
//            tvDownload.setText("未收录");
//            tvDownload.setTextColor(Color.GRAY);
//            tvDownload.setBackgroundColor(Color.WHITE);
        }

        Glide.with(context).load(appItem.getIcon()).into(holder.getImageView(R.id.iv_icon));
    }

    @Override
    public CollectionAppInfo createData(Element element) {
        if (!"app".equals(element.selectFirst("viewtype").text())) {
            return null;
        }
        return BeanUtils.createBean(element, CollectionAppInfo.class);
//        return CollectionAppInfo.from(element);
    }

}
