package com.zpj.shouji.market.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppItem;
import com.zpj.shouji.market.ui.fragment.base.LoadMoreFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchResultFragment;

import java.util.List;

public class AppListFragment extends LoadMoreFragment<AppItem>
        implements SearchResultFragment.KeywordObserver {

    public static AppListFragment newInstance(String defaultUrl) {
        Bundle args = new Bundle();
        args.putString(KEY_DEFAULT_URL, defaultUrl);
        AppListFragment fragment = new AppListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_linear;
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, AppItem data, float x, float y) {
        _mActivity.start(AppDetailFragment.newInstance(data));
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<AppItem> list, int position, List<Object> payloads) {
        final AppItem appItem = list.get(position);
        holder.getTextView(R.id.tv_title).setText(appItem.getAppTitle());
        holder.getTextView(R.id.tv_info).setText(appItem.getAppSize() + " | " + appItem.getAppInfo());
        holder.getTextView(R.id.tv_desc).setText(appItem.getAppComment());
        Glide.with(context).load(appItem.getAppIcon()).into(holder.getImageView(R.id.iv_icon));
    }

    @Override
    public void updateKeyword(String key) {
        defaultUrl = "http://tt.shouji.com.cn/androidv3/app_search_xml.jsp?sdk=26&type=default&s=" + key;
        onRefresh();
    }

    @Override
    public AppItem createData(Element element) {
        if ("app".equals(element.selectFirst("viewtype").text())) {
            return AppItem.create(element);
        }
        return null;
    }

}
