package com.zpj.shouji.market.ui.fragment.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.SubjectItem;
import com.zpj.shouji.market.ui.fragment.SubjectListFragment;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.HttpUtil;

import java.io.IOException;
import java.util.List;

public abstract class LoadMoreFragment<T> extends RecyclerLayoutFragment<T> {

    protected static final String KEY_DEFAULT_URL = "default_url";
    protected String defaultUrl;
    protected String nextUrl;

    @Override
    protected void handleArguments(Bundle arguments) {
        defaultUrl = arguments.getString(KEY_DEFAULT_URL, "");
        nextUrl = defaultUrl;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_linear;
    }

    @Override
    public void onRefresh() {
        nextUrl = defaultUrl;
        super.onRefresh();
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, T data, float x, float y) {

    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, T data, float x, float y) {
        return false;
    }

    @Override
    public boolean onLoadMore(EasyAdapter.Enabled enabled, int currentPage) {
        if (TextUtils.isEmpty(nextUrl)) {
            return false;
        }
        if (data.isEmpty()) {
            recyclerLayout.showLoading();
        }
        getData();
        return true;
    }

    protected void getData() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument(nextUrl);
                nextUrl = doc.selectFirst("nextUrl").text();
                for (Element element : doc.select("item")) {
                    T item = createData(element);
                    if (item == null) {
                        continue;
                    }
                    data.add(item);
                }
                post(() -> recyclerLayout.notifyDataSetChanged());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public abstract T createData(Element element);

}
