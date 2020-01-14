package com.sunfusheng;

import android.content.Context;

import java.util.List;

/**
 * @author sunfusheng on 2018/2/1.
 */
abstract public class FooterGroupRecyclerViewAdapter<T> extends GroupRecyclerViewAdapter<T> {

    public FooterGroupRecyclerViewAdapter(Context context) {
        super(context);
    }

    public FooterGroupRecyclerViewAdapter(Context context, List<List<T>> items) {
        super(context, items);
    }

    public FooterGroupRecyclerViewAdapter(Context context, T[][] items) {
        super(context, items);
    }

    @Override
    public boolean showHeader() {
        return false;
    }

    @Override
    public boolean showFooter() {
        return true;
    }

    @Override
    public int getHeaderLayoutId(int viewType) {
        return 0;
    }

    @Override
    public void onBindHeaderViewHolder(GroupViewHolder holder, T item, int groupPosition) {

    }
}
