package com.zpj.shouji.market.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchResultFragment;
import com.zpj.shouji.market.ui.widget.DownloadButton;

import java.util.List;

public class AppListFragment extends NextUrlFragment<AppInfo> {

//    private boolean updateKeyword = false;

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_linear;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
//        if (updateKeyword) {
//            updateKeyword = false;
//            onRefresh();
//        }
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, AppInfo data) {
        AppDetailFragment.start(data);
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<AppInfo> list, int position, List<Object> payloads) {
        final AppInfo appInfo = list.get(position);
        holder.getTextView(R.id.tv_title).setText(appInfo.getAppTitle());
        holder.getTextView(R.id.tv_info).setText(appInfo.getAppSize() + " | " + appInfo.getAppInfo());
        holder.getTextView(R.id.tv_desc).setText(appInfo.getAppComment());
        Glide.with(context).load(appInfo.getAppIcon()).into(holder.getImageView(R.id.iv_icon));

        DownloadButton downloadButton = holder.getView(R.id.tv_download);
        downloadButton.bindApp(appInfo);
    }

    @Override
    public AppInfo createData(Element element) {
        if ("app".equals(element.selectFirst("viewtype").text())) {
            return AppInfo.parse(element);
        }
        return null;
    }

//    @Override
//    public void onSuccess(Document doc) throws Exception {
//        Log.d("getData", "doc=" + doc);
//        nextUrl = doc.selectFirst("nextUrl").text();
//        if (refresh) {
//            data.clear();
//        }
//        onGetDocument(doc);
//
//        recyclerLayout.notifyDataSetChanged();
//        refresh = false;
//
//    }

}
