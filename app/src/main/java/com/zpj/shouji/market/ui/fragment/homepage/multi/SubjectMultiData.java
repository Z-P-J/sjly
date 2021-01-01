package com.zpj.shouji.market.ui.fragment.homepage.multi;

import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.MultiAdapter;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.model.SubjectInfo;
import com.zpj.shouji.market.ui.fragment.subject.SubjectDetailFragment;
import com.zpj.shouji.market.ui.fragment.subject.SubjectRecommendListFragment;
import com.zpj.shouji.market.utils.BeanUtils;

public class SubjectMultiData extends RecyclerMultiData<SubjectInfo> {

    public SubjectMultiData(String title) {
        super(title);
    }

    @Override
    public boolean loadData(final MultiAdapter adapter) {
        HttpPreLoader.getInstance()
                .setLoadListener(PreloadApi.HOME_SUBJECT, document -> {
                    Elements elements = document.select("item");
                    for (int i = 0; i < elements.size(); i++) {
                        list.add(BeanUtils.createBean(elements.get(i), SubjectInfo.class));
                    }
                    adapter.notifyDataSetChanged();
                });
        return false;
    }

    @Override
    public int getItemRes() {
        return R.layout.item_app_subject;
    }

    @Override
    public void buildRecyclerView(EasyRecyclerView<SubjectInfo> recyclerView) {
        recyclerView
                .setLayoutManager(new GridLayoutManager(recyclerView.getRecyclerView().getContext(), 2,
                        LinearLayoutManager.HORIZONTAL, false))
                .addItemDecoration(new Y_DividerItemDecoration(recyclerView.getRecyclerView().getContext()) {
                    @Override
                    public Y_Divider getDivider(int itemPosition) {
                        Y_DividerBuilder builder;
                        int color = Color.TRANSPARENT;
                        if (itemPosition == 0 || itemPosition == 1) {
                            builder = new Y_DividerBuilder()
                                    .setLeftSideLine(true, color, 12, 0, 0)
                                    .setRightSideLine(true, color, 4, 0, 0);
                        } else if (itemPosition == list.size() - 1 || itemPosition == list.size() - 2) {
                            builder = new Y_DividerBuilder()
                                    .setRightSideLine(true, color, 12, 0, 0)
                                    .setLeftSideLine(true, color, 4, 0, 0);
                        } else {
                            builder = new Y_DividerBuilder()
                                    .setLeftSideLine(true, color, 4, 0, 0)
                                    .setRightSideLine(true, color, 4, 0, 0);
                        }
                        return builder.setTopSideLine(true, color, 4, 0, 0)
                                .setBottomSideLine(true, color, 4, 0, 0)
                                .create();
                    }
                })
                .onBindViewHolder((holder, list, position, payloads) -> {

                    SubjectInfo info = list.get(position);
                    holder.setText(R.id.tv_title, info.getTitle());
                    holder.setText(R.id.tv_comment, info.getComment());
                    holder.setText(R.id.tv_m, info.getM());
                    ImageView ivIcon = holder.getImageView(R.id.iv_icon);
                    Glide.with(ivIcon)
                            .load(info.getIcon())
                            .apply(GlideRequestOptions.getDefaultIconOption())
                            .into(ivIcon);

                    holder.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SubjectDetailFragment.start(info);
                        }
                    });
                });
    }

    @Override
    public void onHeaderClick() {
        SubjectRecommendListFragment.start("http://tt.shouji.com.cn/androidv3/special_index_xml.jsp?jse=yes");
    }
}
