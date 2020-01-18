package com.zpj.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public interface IEasy {

    interface OnItemClickCallback {
        boolean shouldIgnoreClick(View view);
    }

    interface OnClickListener<T> {
        void onClick(EasyViewHolder holder, View view, T data);
    }

    interface OnItemClickListener<T> {
        void onClick(EasyViewHolder holder, View view, T data, float x, float y);
    }

    interface OnItemLongClickListener<T> {
        boolean onLongClick(EasyViewHolder holder, View view, T data, float x, float y);
    }


    interface OnBindViewHolderListener<T>{
        void onBindViewHolder(EasyViewHolder holder, List<T> list, int position, List<Object> payloads);
    }

    interface OnCreateViewHolderListener<T>{
        void onCreateViewHolder(ViewGroup parent, View itemView, int viewType);
    }

    interface OnBindHeaderListener {
        void onBindHeader(EasyViewHolder holder);
    }

    interface OnCreateFooterListener {
        void onCreateFooterView(View view);
    }

    interface OnLoadMoreListener {
        boolean onLoadMore(EasyAdapter.Enabled enabled, int currentPage);
    }
}
