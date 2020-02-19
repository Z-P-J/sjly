package com.sunfusheng;

import android.content.Context;

import java.util.List;

/**
 * @author sunfusheng on 2018/2/1.
 */
abstract public class HeaderGroupRecyclerViewAdapter<T> extends GroupRecyclerViewAdapter<T> {

    public HeaderGroupRecyclerViewAdapter(Context context) {
        super(context);
    }

    public HeaderGroupRecyclerViewAdapter(Context context, List<List<T>> items) {
        super(context, items);
    }

    public HeaderGroupRecyclerViewAdapter(Context context, T[][] items) {
        super(context, items);
    }

    @Override
    public boolean showHeader() {
        return true;
    }

    @Override
    public boolean showFooter() {
        return false;
    }

    @Override
    public int getFooterLayoutId(int viewType) {
        return 0;
    }

    @Override
    public void onBindFooterViewHolder(GroupViewHolder holder, T item, int groupPosition) {

    }
}
