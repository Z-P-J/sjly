package com.zpj.shouji.market.ui.fragment.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.IEasy;
import com.zpj.recyclerview.IEasy.OnBindViewHolderListener;
import com.zpj.shouji.market.R;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerLayoutFragment<T> extends BaseFragment
        implements OnBindViewHolderListener<T>,
        IEasy.OnItemClickListener<T>,
        IEasy.OnItemLongClickListener<T>,
        IEasy.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener {

    protected final List<T> data = new ArrayList<>();
    protected EasyRecyclerLayout<T> recyclerLayout;

    @Override
    protected final int getLayoutId() {
        return R.layout.fragment_recycler_layout;
    }

    @Override
    protected final void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            handleArguments(getArguments());
        }
        recyclerLayout = view.findViewById(R.id.recycler_layout);
    }

    @Override
    public final void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        buildRecyclerLayout(context);
    }

    @Override
    public void onRefresh() {
        data.clear();
        recyclerLayout.notifyDataSetChanged();
    }

    protected void handleArguments(Bundle arguments) {

    }

    protected void buildRecyclerLayout(Context context) {
        recyclerLayout.setData(data)
                .setItemRes(getItemLayoutId())
                .setLayoutManager(getLayoutManager(context))
                .setEnableLoadMore(true)
                .setEnableSwipeRefresh(true)
                .setOnRefreshListener(this)
                .onBindViewHolder(this)
                .onItemClick(this)
                .onItemLongClick(this)
                .onLoadMore(this)
                .build();
    }

    protected RecyclerView.LayoutManager getLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @LayoutRes
    protected abstract int getItemLayoutId();

}
