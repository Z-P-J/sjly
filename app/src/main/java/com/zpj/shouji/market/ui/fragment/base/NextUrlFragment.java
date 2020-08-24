package com.zpj.shouji.market.ui.fragment.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;

public abstract class NextUrlFragment<T> extends RecyclerLayoutFragment<T>
        implements IHttp.OnSuccessListener<Document>, IHttp.OnErrorListener {

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
//        super.onRefresh();
//        data.clear();
//        recyclerLayout.notifyDataSetChanged();
        if (data.isEmpty()) {
            refresh = false;
            recyclerLayout.showContent();
        } else {
            refresh = true;
            getData();
        }
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
//        refresh = false;
        return true;
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        flag = false;
    }

    @Override
    public void onSuccess(Document doc) throws Exception {
        Log.d("getData", "doc=" + doc);
        nextUrl = doc.selectFirst("nextUrl").text();
        if (refresh) {
            data.clear();
        }
        int start = data.size();
        onGetDocument(doc);
//        int end = data.size() == 0 ? 0 : data.size() - 1;
        int end = data.size();

        Log.d("getData", "start=" + start + " end=" + end + " count=" + (end - start));
        Log.d("getData", "getHeaderView=" + recyclerLayout.getAdapter().getHeaderView());
        Log.d("getData", "getFooterView=" + recyclerLayout.getAdapter().getFooterView());
        if (start == 0) {
            recyclerLayout.notifyDataSetChanged();
        } else {
            if (start < end) {
//            recyclerLayout.notifyItemRangeChanged(start, end);
                int count = end - start;
                if (recyclerLayout.getAdapter().getHeaderView() != null) {
                    start += 1;
                }
                Log.d("getData222222222222", "start=" + start + " count=" + count);
                recyclerLayout.notifyItemRangeChanged(start, count);
            }
        }
        refresh = false;
        if (data.size() == 0) {
            recyclerLayout.showEmpty();
        } else {
            recyclerLayout.showContent();
        }
//        recyclerLayout.notifyDataSetChanged();
    }

    protected void onGetDocument(Document doc) throws Exception {
        for (Element element : doc.select("item")) {
            T item = createData(element);
            if (item == null) {
                continue;
            }
            data.add(item);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        recyclerLayout.showErrorView(throwable.getMessage());
    }

    protected void getData() {
        Log.d("NextUrlFragment", "getData nextUrl=" + nextUrl);
        HttpApi.get(nextUrl)
                .onSuccess(this)
                .onError(this)
                .subscribe();
    }

    public abstract T createData(Element element);

}
