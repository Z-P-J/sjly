package com.zpj.recyclerview;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.zpj.zdialog.R;

import java.util.ArrayList;
import java.util.List;

public class EasyRecyclerView<T> {

    private final RecyclerView recyclerView;

    private RecyclerView.LayoutManager layoutManager;

    private EasyAdapter<T> easyAdapter;

    private List<T> list;

    private int itemRes = -1;

    private View headerView;
    private View footerView;

    private IEasy.OnBindViewHolderCallback<T> callback;
    private IEasy.OnLoadMoreListener onLoadMoreListener;

    public EasyRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public EasyRecyclerView<T> setItemRes(int res) {
        this.itemRes = res;
        return this;
    }

    public EasyRecyclerView<T> setData(List<T> list) {
        this.list = list;
        return this;
    }

    public EasyRecyclerView<T> setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        return this;
    }

    public EasyRecyclerView<T> setHeaderView(View headerView) {
        this.headerView = headerView;
        return this;
    }

    public EasyRecyclerView<T> setHeaderView(@LayoutRes int layoutRes, IEasy.OnCreateHeaderCallback callback) {
        this.headerView = LayoutInflater.from(recyclerView.getContext()).inflate(layoutRes, null, false);
        callback.onCreateHeaderView(headerView);
        return this;
    }

    public EasyRecyclerView<T> setFooterView(View headerView) {
        this.footerView = headerView;
        return this;
    }

    public EasyRecyclerView<T> setFooterView(@LayoutRes int layoutRes, IEasy.OnCreateFooterCallback callback) {
        this.footerView = LayoutInflater.from(recyclerView.getContext()).inflate(layoutRes, null, false);
        callback.onCreateFooterView(footerView);
        return this;
    }

    public EasyRecyclerView<T> onLoadMore(IEasy.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        return this;
    }

    public EasyRecyclerView<T> onBindViewHolder(IEasy.OnBindViewHolderCallback<T> callback) {
        this.callback = callback;
        return this;
    }

    public void build() {
        if (itemRes <= 0) {
            throw new RuntimeException("You must set the itemRes!");
        }
        if (list == null) {
            list = new ArrayList<>(0);
        }
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(recyclerView.getContext());
        }
        easyAdapter = new EasyAdapter<T>(list, itemRes, callback);
        if (headerView != null) {
            easyAdapter.setHeaderView(headerView);
        }
        if (footerView != null) {
            easyAdapter.setFooterView(footerView);
        } else if (onLoadMoreListener != null) {
            footerView = LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.base_footer, null, false);
            easyAdapter.setFooterView(footerView);
        }
        easyAdapter.setOnLoadMoreListener(onLoadMoreListener);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(easyAdapter);
    }

    public void notifyDataSetChanged() {
        easyAdapter.notifyDataSetChanged();
    }

    public void notifyItemChanged(int position) {
        easyAdapter.notifyItemChanged(position);
    }

    public EasyAdapter<T> getAdapter() {
        return easyAdapter;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void post(Runnable runnable) {
        recyclerView.post(runnable);
    }
}
