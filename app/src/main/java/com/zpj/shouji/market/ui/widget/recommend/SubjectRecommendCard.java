package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.model.SubjectInfo;
import com.zpj.shouji.market.ui.fragment.SubjectListFragment;
import com.zpj.shouji.market.ui.fragment.SubjectRecommendListFragment;
import com.zpj.shouji.market.ui.fragment.ToolBarListFragment;

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

        HttpPreLoader.getInstance().setLoadListener(HttpPreLoader.HOME_SUBJECT, document -> {
            Elements elements = document.select("item");
            for (int i = 0; i < elements.size(); i++) {
                list.add(SubjectInfo.create(elements.get(i)));
            }
            if (list.size() % 2 != 0) {
                list.remove(list.size() - 1);
            }
            recyclerView.notifyDataSetChanged();
        });
    }

    @Override
    protected void buildRecyclerView(EasyRecyclerView<SubjectInfo> recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<SubjectInfo> list, int position, List<Object> payloads) {
//        LCardView cardView = holder.getView(R.id.card_view);
//        cardView.setShadowSize(0);
//        cardView.setShadowAlpha(0);

//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) cardView.getLayoutParams();
//        if (position % 2 == 0) {
//            if (position == 0) {
//                params.setMargins(margin, 0, margin / 2, margin / 2);
//            } else if (position == list12.size() - 2) {
//                params.setMargins(margin / 2, 0, margin, margin / 2);
//            } else {
//                params.setMargins(margin / 2, 0, margin / 2, margin / 2);
//            }
//        } else {
//            if (position == 1) {
//                params.setMargins(margin, margin / 2, margin / 2, 0);
//            } else if (position == list12.size() - 1) {
//                params.setMargins(margin / 2, margin / 2, margin, 0);
//            } else {
//                params.setMargins(margin / 2, margin / 2, margin / 2, 0);
//            }
//        }
//        cardView.setLayoutParams(params);

        SubjectInfo info = list.get(position);
        holder.setText(R.id.tv_title, info.getTitle());
        holder.setText(R.id.tv_comment, info.getComment());
        holder.setText(R.id.tv_m, info.getM());
        Glide.with(context).load(info.getIcon()).into(holder.getImageView(R.id.iv_icon));
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, SubjectInfo data) {
        ToolBarListFragment.startSubjectDetail(data.getId());
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
