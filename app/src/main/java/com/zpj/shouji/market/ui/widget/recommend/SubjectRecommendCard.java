package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.model.SubjectInfo;
import com.zpj.shouji.market.ui.fragment.subject.SubjectDetailFragment;
import com.zpj.shouji.market.ui.fragment.subject.SubjectRecommendListFragment;
import com.zpj.shouji.market.ui.fragment.ToolBarListFragment;
import com.zpj.shouji.market.utils.BeanUtils;

import java.util.List;

public class SubjectRecommendCard extends RecommendCard<SubjectInfo> {

    public SubjectRecommendCard(Context context) {
        this(context, null);
    }

    public SubjectRecommendCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubjectRecommendCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTitle("专题推荐");
    }

    @Override
    public void loadData(Runnable runnable) {
        HttpPreLoader.getInstance()
                .setLoadListener(PreloadApi.HOME_SUBJECT, document -> {
                    Elements elements = document.select("item");
                    for (int i = 0; i < elements.size(); i++) {
//                list.add(SubjectInfo.create(elements.get(i)));
                        list.add(BeanUtils.createBean(elements.get(i), SubjectInfo.class));
                    }
//            if (list.size() % 2 != 0) {
//                list.remove(list.size() - 1);
//            }
                    recyclerView.notifyDataSetChanged();
                    if (runnable != null) {
                        runnable.run();
                    }
                });
    }

    @Override
    protected void buildRecyclerView(EasyRecyclerView<SubjectInfo> recyclerView) {
        recyclerView.setLayoutManager(
                new GridLayoutManager(context, 1, // 2
                        LinearLayoutManager.HORIZONTAL, false));
        for (int i = 0; i < recyclerView.getRecyclerView().getItemDecorationCount(); i++) {
            recyclerView.getRecyclerView().removeItemDecorationAt(i);
        }
        recyclerView.addItemDecoration(new Y_DividerItemDecoration(context) {
            @Override
            public Y_Divider getDivider(int itemPosition) {
                Y_DividerBuilder builder;
                if (itemPosition == 0) {
                    builder = new Y_DividerBuilder()
                            .setLeftSideLine(true, Color.WHITE, 16, 0, 0)
                            .setRightSideLine(true, Color.WHITE, 8, 0, 0);
                } else if (itemPosition == list.size() - 1) {
                    builder = new Y_DividerBuilder()
                            .setRightSideLine(true, Color.WHITE, 16, 0, 0)
                            .setLeftSideLine(true, Color.WHITE, 8, 0, 0);
                } else {
                    builder = new Y_DividerBuilder()
                            .setLeftSideLine(true, Color.WHITE, 8, 0, 0)
                            .setRightSideLine(true, Color.WHITE, 8, 0, 0);
                }
                return builder.setTopSideLine(true, Color.WHITE, 8, 0, 0)
                        .setBottomSideLine(true, Color.WHITE, 8, 0, 0)
                        .create();
            }
        });
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<SubjectInfo> list, int position, List<Object> payloads) {
        SubjectInfo info = list.get(position);
        holder.setText(R.id.tv_title, info.getTitle());
        holder.setText(R.id.tv_comment, info.getComment());
        holder.setText(R.id.tv_m, info.getM());
        Glide.with(context).load(info.getIcon()).into(holder.getImageView(R.id.iv_icon));
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, SubjectInfo data) {
//        ToolBarListFragment.startSubjectDetail(data.getId());
        SubjectDetailFragment.start(data);
    }

    @Override
    public int getItemRes() {
        return R.layout.item_app_subject;
    }

    @Override
    public void onMoreClicked(View v) {
        SubjectRecommendListFragment.start("http://tt.shouji.com.cn/androidv3/special_index_xml.jsp?jse=yes");
    }
}
