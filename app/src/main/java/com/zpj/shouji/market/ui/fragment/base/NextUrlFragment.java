package com.zpj.shouji.market.ui.fragment.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;

public abstract class NextUrlFragment<T> extends RecyclerLayoutFragment<T> {

    protected String defaultUrl;
    protected String nextUrl;
    private boolean flag = false;
    protected boolean refresh;

    @Override
    protected void handleArguments(Bundle arguments) {
        defaultUrl = arguments.getString(Keys.DEFAULT_URL, "");
        nextUrl = defaultUrl;
    }

    @Override
    public void onRefresh() {
        nextUrl = defaultUrl;
        refresh = true;
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
//        getData();

        if (data.isEmpty() && !refresh) {
            if (flag) {
                return false;
            }
            flag = true;
            postOnEnterAnimationEnd(this::getData);
        } else {
            getData();
        }
        refresh = false;
        return true;
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        flag = false;
    }

    protected void getData() {
        HttpApi.get(nextUrl)
                .onSuccess(doc -> {
                    Log.d("getData", "doc=" + doc);
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
