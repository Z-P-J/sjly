package com.sjly.zpj.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class LoadMoreListener extends RecyclerView.OnScrollListener {

    //声明一个LinearLayoutManager
    private LinearLayoutManager mLinearLayoutManager;

    //当前页从0开始
    private int currentPage = 0;
    //已经加载出来的Item的数量
    private int totalItemCount;

    //主要用来存储上一个totalItemCount
    private int preivousTotal = 0;

    //屏幕上可见的item数量
    private int visibleItemCount;

    //屏幕上可见的Item中的第一个
    private int fistVisibleItem;

    //是否正在上拉数据
    private boolean loading=true;

    public LoadMoreListener(LinearLayoutManager linearLayoutManager) {
        mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        //屏幕可见的条目数量
        visibleItemCount = recyclerView.getChildCount();
        //已经加载出来的条目数量
        totalItemCount = mLinearLayoutManager.getItemCount();
        //屏幕第一个条目可见对应的位置
        fistVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        Log.d("preivou",""+preivousTotal);

        //如果是正在加载数据
        if (loading){
            if (totalItemCount>preivousTotal){
                //说明数据已经加载结束
                loading = false;
                preivousTotal = totalItemCount;
                Log.d("preivousTotal",currentPage+"\t"+preivousTotal);
            }
        }
        if (!loading && totalItemCount - visibleItemCount <= fistVisibleItem){
            currentPage++;
            onLoadMore(currentPage);
            loading = true;
        }
    }

    public void initParams(){
        this.currentPage = 1;
        preivousTotal = 20;
    }

    public abstract void onLoadMore(int currentPage);
}
