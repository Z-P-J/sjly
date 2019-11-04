package com.zpj.recyclerview;

import android.view.View;

import java.util.List;

public interface IEasy {

//    interface OnCreateViewHolderCallback<T> {
//        EasyViewHolder onCreateViewHolder(List<T> list, View itemView, int position);
//    }


    interface OnBindViewHolderCallback<T>{
        void onBindViewHolder(EasyViewHolder holder, List<T> list, int position);
    }

    interface OnCreateHeaderCallback{
        void onCreateHeaderView(View view);
    }

    interface OnCreateFooterCallback{
        void onCreateFooterView(View view);
    }

    interface OnLoadMoreListener {
        void onLoadMore(EasyAdapter.Enabled enabled);
    }
}
