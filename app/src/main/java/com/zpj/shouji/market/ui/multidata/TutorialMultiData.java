package com.zpj.shouji.market.ui.multidata;

import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.MultiAdapter;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.model.article.ArticleInfo;
import com.zpj.shouji.market.ui.fragment.ArticleDetailFragment;
import com.zpj.toast.ZToast;

import java.util.Locale;

public class TutorialMultiData extends RecyclerMultiData<ArticleInfo> {

    private final int index;
    private final String type;

    public TutorialMultiData(String title, String type, int index) {
        super(title);
        this.type = type;
        this.index = index;
    }

    @Override
    public int getItemRes() {
        return R.layout.item_tutorial;
    }

    @Override
    public void buildRecyclerView(EasyRecyclerView<ArticleInfo> recyclerView) {
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
                    ArticleInfo info = list.get(position);
                    ImageView ivImage = holder.getImageView(R.id.iv_image);
                    Glide.with(ivImage).load(info.getImage()).into(ivImage);
                    holder.setText(R.id.tv_title, info.getTitle());

                    holder.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArticleDetailFragment.start("https://" + type + ".shouji.com.cn" + info.getUrl());
                        }
                    });
                });
    }

    @Override
    public void onHeaderClick() {
        ZToast.normal("TODO");
    }

    @Override
    public boolean loadData(MultiAdapter adapter) {
        HttpApi.getXml(String.format(Locale.CHINA, "https://%s.shouji.com.cn/newslist/list_%d_1.html", type, index))
                .onSuccess(data -> {
                    Elements elements = data.selectFirst("ul.news_list").select("li");
                    list.clear();
//                    adapter.getItemCount();
                    for (Element element : elements) {
                        list.add(ArticleInfo.from(element));
                    }
                    adapter.notifyDataSetChanged();
                })
                .subscribe();
        return false;
    }

}
