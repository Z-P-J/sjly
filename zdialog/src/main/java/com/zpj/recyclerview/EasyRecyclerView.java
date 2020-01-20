package com.zpj.recyclerview;

import android.annotation.SuppressLint;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

import com.zpj.dialog.R;

import java.util.ArrayList;
import java.util.List;

public class EasyRecyclerView<T> {

    private final RecyclerView recyclerView;

    private RecyclerView.LayoutManager layoutManager;

    private EasyAdapter<T> easyAdapter;

    private List<T> list;

    private int itemRes = -1;

    private View headerView;
    private IEasy.OnBindHeaderListener onBindHeaderListener;
    private View footerView;

    private IEasy.OnBindViewHolderListener<T> onBindViewHolderListener;
    private IEasy.OnCreateViewHolderListener<T> onCreateViewHolder;
    private IEasy.OnLoadMoreListener onLoadMoreListener;

    private final SparseArray<IEasy.OnClickListener<T>> onClickListeners = new SparseArray<>();
    private IEasy.OnItemClickListener<T> onItemClickListener;
    private IEasy.OnItemLongClickListener<T> onItemLongClickListener;

    public EasyRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public EasyRecyclerView<T> setItemAnimator(RecyclerView.ItemAnimator animator) {
        recyclerView.setItemAnimator(animator);
        return this;
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

    @SuppressLint("ResourceType")
    public EasyRecyclerView<T> setHeaderView(@LayoutRes int layoutRes, IEasy.OnBindHeaderListener l) {
        if (layoutRes > 0 && l != null) {
            this.headerView = LayoutInflater.from(recyclerView.getContext()).inflate(layoutRes, null, false);
            onBindHeaderListener = l;
        }
        return this;
    }

    public EasyRecyclerView<T> setFooterView(View headerView) {
        this.footerView = headerView;
        return this;
    }

    public EasyRecyclerView<T> setFooterView(@LayoutRes int layoutRes, IEasy.OnCreateFooterListener callback) {
        this.footerView = LayoutInflater.from(recyclerView.getContext()).inflate(layoutRes, null, false);
        callback.onCreateFooterView(footerView);
        return this;
    }

    public EasyRecyclerView<T> onLoadMore(IEasy.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        return this;
    }

    public EasyRecyclerView<T> onBindViewHolder(IEasy.OnBindViewHolderListener<T> callback) {
        this.onBindViewHolderListener = callback;
        return this;
    }

    public EasyRecyclerView<T> onCreateViewHolder(IEasy.OnCreateViewHolderListener<T> callback) {
        this.onCreateViewHolder = callback;
        return this;
    }

    public EasyRecyclerView<T> addOnScrollListener(final RecyclerView.OnScrollListener onScrollListener) {
        recyclerView.addOnScrollListener(onScrollListener);
        return this;
    }

    public EasyRecyclerView<T> onViewClick(@IdRes int id, IEasy.OnClickListener<T> onClickListener) {
        onClickListeners.put(id, onClickListener);
        return this;
    }

    public EasyRecyclerView<T> onItemClick(IEasy.OnItemClickListener<T> listener) {
        this.onItemClickListener = listener;
        return this;
    }

    public EasyRecyclerView<T> onItemLongClick(IEasy.OnItemLongClickListener<T> listener) {
        this.onItemLongClickListener = listener;
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
        easyAdapter = new EasyAdapter<>(list, itemRes, onCreateViewHolder,
                onBindViewHolderListener, onItemClickListener,
                onItemLongClickListener, onClickListeners);
        if (headerView != null) {
            easyAdapter.setHeaderView(headerView);
            easyAdapter.setOnBindHeaderListener(onBindHeaderListener);
        }
        if (footerView != null) {
            easyAdapter.setFooterView(footerView);
        } else if (onLoadMoreListener != null) {
            footerView = LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.easy_base_footer, null, false);
            easyAdapter.setFooterView(footerView);
        }
        easyAdapter.setOnLoadMoreListener(onLoadMoreListener);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(easyAdapter);
    }

    public void notifyDataSetChanged() {
        if (easyAdapter == null) {
            return;
        }
        easyAdapter.notifyDataSetChanged();
    }

    public void notifyItemChanged(int position) {
        if (easyAdapter == null) {
            return;
        }
        easyAdapter.notifyItemChanged(position);
    }

    public void notifyItemChanged(int position, Object payload) {
        if (easyAdapter == null) {
            return;
        }
        easyAdapter.notifyItemChanged(position, payload);
    }

    public void notifyItemInserted(int position) {
        if (easyAdapter == null) {
            return;
        }
        easyAdapter.notifyItemInserted(position);
    }

    public void notifyItemRangeChanged(int start, int itemCount) {
        if (easyAdapter == null) {
            return;
        }
        easyAdapter.notifyItemRangeChanged(start, itemCount);
    }

    public void notifyItemRangeChanged(int start, int itemCount, Object payload) {
        if (easyAdapter == null) {
            return;
        }
        easyAdapter.notifyItemRangeChanged(start, itemCount, payload);
    }

    public void notifyItemRemoved(int position) {
        if (easyAdapter == null) {
            return;
        }
        easyAdapter.notifyItemRemoved(position);
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

    public List<T> getData() {
        return list;
    }
}
