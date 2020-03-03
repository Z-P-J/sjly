package com.zpj.shouji.market.ui.fragment.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.utils.HttpApi;

public abstract class NextUrlFragment<T> extends RecyclerLayoutFragment<T> {

    protected static final String KEY_DEFAULT_URL = "default_url";
    protected String defaultUrl;
    protected String nextUrl;

    @Override
    protected void handleArguments(Bundle arguments) {
        defaultUrl = arguments.getString(KEY_DEFAULT_URL, "");
        nextUrl = defaultUrl;
    }

    @Override
    public void onRefresh() {
        nextUrl = defaultUrl;
        super.onRefresh();
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, T data) {

    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, T data) {
        return false;
    }

    @Override
    public boolean onLoadMore(EasyAdapter.Enabled enabled, int currentPage) {
        if (TextUtils.isEmpty(nextUrl)) {
            return false;
        }
//        if (data.isEmpty()) {
//            recyclerLayout.showLoading();
//        }
        getData();
        return true;
    }

    protected void getData() {
        HttpApi.connect(nextUrl)
                .onSuccess(doc -> {
                    nextUrl = doc.selectFirst("nextUrl").text();
                    for (Element element : doc.select("item")) {
                        T item = createData(element);
                        if (item == null) {
                            continue;
                        }
                        data.add(item);
                    }
                    recyclerLayout.notifyDataSetChanged();
                    if (data.isEmpty()) {
                        recyclerLayout.showEmpty();
                    }
                })
                .subscribe();
    }

    public abstract T createData(Element element);

}
