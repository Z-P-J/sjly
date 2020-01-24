package com.zpj.shouji.market.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchResultFragment;

import java.util.List;

public class AppListFragment extends NextUrlFragment<AppInfo>
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
    public void onClick(EasyViewHolder holder, View view, AppInfo data, float x, float y) {
        _mActivity.start(AppDetailFragment.newInstance(data));
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<AppInfo> list, int position, List<Object> payloads) {
        final AppInfo appInfo = list.get(position);
        holder.getTextView(R.id.tv_title).setText(appInfo.getAppTitle());
        holder.getTextView(R.id.tv_info).setText(appInfo.getAppSize() + " | " + appInfo.getAppInfo());
        holder.getTextView(R.id.tv_desc).setText(appInfo.getAppComment());
        Glide.with(context).load(appInfo.getAppIcon()).into(holder.getImageView(R.id.iv_icon));
    }

    @Override
    public void updateKeyword(String key) {
        defaultUrl = "http://tt.shouji.com.cn/androidv3/app_search_xml.jsp?sdk=26&type=default&s=" + key;
        onRefresh();
    }

    @Override
    public AppInfo createData(Element element) {
        if ("app".equals(element.selectFirst("viewtype").text())) {
            return AppInfo.create(element);
        }
        return null;
    }

}
