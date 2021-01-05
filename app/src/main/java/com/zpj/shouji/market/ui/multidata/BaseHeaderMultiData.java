package com.zpj.shouji.market.ui.multidata;

import android.view.View;

import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.HeaderMultiData;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.widget.TitleHeaderLayout;

import java.util.List;

public abstract class BaseHeaderMultiData<T> extends HeaderMultiData<T>
        implements View.OnClickListener {

    protected final String title;

    public BaseHeaderMultiData(String title) {
        this.title = title;
    }

    @Override
    public int getHeaderLayoutId() {
        return R.layout.item_header_title;
    }

    @Override
    public void onBindHeader(EasyViewHolder holder, List<Object> payloads) {
        TitleHeaderLayout headerLayout = holder.getView(R.id.layout_title_header);
        headerLayout.setTitle(title);
        headerLayout.setOnMoreClickListener(showMoreButton() ? this : null);
//        holder.setText(R.id.tv_title, title);
//        holder.setVisible(R.id.tv_more, true);
//        holder.setOnClickListener(R.id.tv_more, this);
    }

    @Override
    public final void onClick(View v) {
        onHeaderClick();
    }

    protected boolean showMoreButton() {
        return true;
    }

    public void onHeaderClick() {

    }

}
