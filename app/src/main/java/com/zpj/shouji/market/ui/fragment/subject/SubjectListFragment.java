package com.zpj.shouji.market.ui.fragment.subject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.model.SubjectInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchResultFragment;
import com.zpj.shouji.market.utils.BeanUtils;

import java.util.List;

public class SubjectListFragment extends NextUrlFragment<SubjectInfo>
        implements SearchResultFragment.KeywordObserver {

    public static SubjectListFragment newInstance(String defaultUrl) {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, defaultUrl);
        SubjectListFragment fragment = new SubjectListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_subject;
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<SubjectInfo> recyclerLayout) {
        super.buildRecyclerLayout(recyclerLayout);
        recyclerLayout.addItemDecoration(new Y_DividerItemDecoration(context) {
            @Override
            public Y_Divider getDivider(int itemPosition) {
                Y_DividerBuilder builder = null;
                int color = Color.TRANSPARENT;
                if (itemPosition == 0) {
                    builder = new Y_DividerBuilder()
                            .setTopSideLine(true, color, 16, 0, 0)
                            .setBottomSideLine(true, color, 8, 0, 0);
                } else if (itemPosition == data.size() - 1) {
                    builder = new Y_DividerBuilder()
                            .setTopSideLine(true, color, 8, 0, 0)
                            .setBottomSideLine(true, color, 16, 0, 0);
                } else {
                    builder = new Y_DividerBuilder()
                            .setTopSideLine(true, color, 8, 0, 0)
                            .setBottomSideLine(true, color, 8, 0, 0);
                }
                return builder
                        .setLeftSideLine(true, color, 16, 0, 0)
                        .setRightSideLine(true, color, 16, 0, 0)
                        .create();
            }
        });
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<SubjectInfo> list, int position, List<Object> payloads) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        holder.getItemView().setLayoutParams(params);
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
    public void updateKeyword(String key) {
        defaultUrl = "/androidv3/app_search_xml.jsp?sdk=26&type=default&s=" + key;
        nextUrl = defaultUrl;
        onRefresh();
    }

    @Override
    public SubjectInfo createData(Element element) {
        return BeanUtils.createBean(element, SubjectInfo.class);
//        return SubjectInfo.create(element);
    }

}
