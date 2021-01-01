package com.zpj.shouji.market.ui.fragment.homepage.multi;

import android.view.View;

import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.HeaderMultiData;
import com.zpj.shouji.market.R;

import java.util.List;

public abstract class BaseHeaderMultiData<T> extends HeaderMultiData<T>
        implements View.OnClickListener {

    protected final String title;

    public BaseHeaderMultiData(String title) {
        this.title = title;
    }

    @Override
    public int getHeaderSpanCount() {
        return 4;
    }

    @Override
    public int getHeaderLayoutId() {
        return R.layout.item_header_title;
    }

    @Override
    public int getChildSpanCount(int viewType) {
        return 4;
    }

//    @Override
//    public int getChildViewType(int position) {
//        return R.layout.layout_text;
//    }
//
//    @Override
//    public boolean hasChildViewType(int viewType) {
//        return viewType == R.layout.layout_text;
//    }
//
//    @Override
//    public int getChildLayoutId(int viewType) {
//        return R.layout.layout_text;
//    }

    @Override
    public void onBindHeader(EasyViewHolder holder, List<Object> payloads) {
        holder.setText(R.id.tv_title, title);
        holder.setVisible(R.id.tv_more, true);
        holder.setOnClickListener(R.id.tv_more, this);
    }

    @Override
    public final void onClick(View v) {
        onHeaderClick();
    }

    public abstract void onHeaderClick();

}
