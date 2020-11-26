package com.zpj.shouji.market.ui.fragment.homepage.multi;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.recyclerview.MultiAdapter;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.model.SubjectInfo;
import com.zpj.shouji.market.ui.fragment.subject.SubjectDetailFragment;
import com.zpj.shouji.market.ui.fragment.subject.SubjectRecommendListFragment;
import com.zpj.shouji.market.utils.BeanUtils;

import java.util.List;

public abstract class RecyclerMultiData<T> extends BaseHeaderMultiData<T> {

    protected EasyRecyclerView<T> recyclerView;

    public RecyclerMultiData(String title) {
        super(title);
    }

    @Override
    public int getChildCount() {
        return 1;
    }

    @Override
    public int getChildViewType(int position) {
        return hashCode();
    }

    @Override
    public boolean hasChildViewType(int viewType) {
        return viewType == hashCode();
    }

    @Override
    public int getChildLayoutId(int viewType) {
        return R.layout.layout_recycler_view;
    }

    @Override
    public void onBindChild(EasyViewHolder holder, List<T> list, int position, List<Object> payloads) {
        if (recyclerView == null) {
            RecyclerView view = holder.getView(R.id.recycler_view);
            view.setNestedScrollingEnabled(false);
            recyclerView = new EasyRecyclerView<>(view);
            recyclerView.setData(list)
                    .setItemRes(getItemRes())
                    .setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
            buildRecyclerView(recyclerView);
            recyclerView.build();
            recyclerView.showContent();
        }
    }

    public abstract int getItemRes();

    public abstract void buildRecyclerView(EasyRecyclerView<T> recyclerView);


}
