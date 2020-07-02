package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.model.article.ArticleInfo;
import com.zpj.shouji.market.ui.fragment.ArticleDetailFragment;

import java.util.List;
import java.util.Locale;

public class TutorialRecommendCard extends RecommendCard<ArticleInfo> {

//    private final int index;
    private final String type;

    public TutorialRecommendCard(Context context) {
        this(context, "soft", 0);
    }

    public TutorialRecommendCard(Context context, String type, int index) {
        this(context, null, type, index);
    }

    public TutorialRecommendCard(Context context, AttributeSet attrs, String type, int index) {
        this(context, attrs, 0, type, index);
    }

    public TutorialRecommendCard(Context context, AttributeSet attrs, int defStyleAttr, String type, int index) {
        super(context, attrs, defStyleAttr);
        this.type = type;
//        this.index = index;
        HttpApi.get(String.format(Locale.CHINA, "https://%s.shouji.com.cn/newslist/list_%d_1.html", type, index))
                .onSuccess(data -> {
                    Elements elements = data.selectFirst("ul.news_list").select("li");
                    list.clear();
                    for (Element element : elements) {
                        list.add(ArticleInfo.from(element));
                    }
                    recyclerView.notifyDataSetChanged();
                })
//                .onError(this)
                .subscribe();
    }


    @Override
    protected void buildRecyclerView(EasyRecyclerView<ArticleInfo> recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<ArticleInfo> list, int position, List<Object> payloads) {
        ArticleInfo info = list.get(position);
//                        Log.d("onBindViewHolder", "position=" + position + " ArticleInfo=" + info);
        Glide.with(context).load(info.getImage()).into(holder.getImageView(R.id.iv_image));
        holder.getTextView(R.id.tv_title).setText(info.getTitle());
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, ArticleInfo data) {
        ArticleDetailFragment.start("https://" + type + ".shouji.com.cn" + data.getUrl());
    }

    @Override
    public int getItemRes() {
        return R.layout.item_tutorial;
    }

    @Override
    public void onMoreClicked(View v) {
        AToast.normal("TODO");
    }
}
