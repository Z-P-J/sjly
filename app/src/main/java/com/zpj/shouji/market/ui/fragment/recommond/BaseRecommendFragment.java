package com.zpj.shouji.market.ui.fragment.recommond;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.felix.atoast.library.AToast;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.blur.BlurTransformation;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.model.GroupItem;
import com.zpj.shouji.market.model.article.ArticleInfo;
import com.zpj.shouji.market.ui.fragment.ArticleDetailFragment;
import com.zpj.shouji.market.ui.fragment.base.RecyclerLayoutFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.utils.HttpApi;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import www.linwg.org.lib.LCardView;

public abstract class BaseRecommendFragment extends RecyclerLayoutFragment<GroupItem>
        implements IHttp.OnErrorListener {

    private static final String TAG = "GameRecommendFragment";

    private static final int[] RES_ICONS = {R.id.item_icon_1, R.id.item_icon_2, R.id.item_icon_3};

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_recomment;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_recommend_card;
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<GroupItem> recyclerLayout) {
        recyclerLayout.getEasyRecyclerView().getRecyclerView().setBackgroundColor(getResources().getColor(R.color.color_background));
        recyclerLayout.setHeaderView(R.layout.layout_app_header, new IEasy.OnBindHeaderListener() {
            @Override
            public void onBindHeader(EasyViewHolder holder) {

            }

        })
                .onGetChildViewType(position -> position + 1);
    }

    @Override
    public void onRefresh() {
        data.clear();
        data.add(new GroupItem("最近更新"));
        data.add(new GroupItem("游戏推荐"));
        data.add(new GroupItem("热门网游"));
        // TODO 排行
        data.add(new GroupItem("游戏快递"));
        data.add(new GroupItem("游戏评测"));
        data.add(new GroupItem("游戏攻略"));
        data.add(new GroupItem("游戏新闻"));
        data.add(new GroupItem("游戏周刊"));
        data.add(new GroupItem("游戏公告"));
        recyclerLayout.notifyDataSetChanged();
    }

    @Override
    public boolean onLoadMore(EasyAdapter.Enabled enabled, int currentPage) {
        if (data.isEmpty()) {
            onRefresh();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, GroupItem data) {

    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, GroupItem data) {
        return false;
    }

    protected void getAppInfo(EasyViewHolder holder, final String url) {
        EasyRecyclerView<AppInfo> recyclerView = new EasyRecyclerView<>(holder.getView(R.id.recycler_view));
        holder.getItemView().setTag(recyclerView);
        List<AppInfo> list = new ArrayList<>();
        recyclerView.setData(list)
                .setItemRes(R.layout.item_app_grid)
                .setLayoutManager(new GridLayoutManager(context, 4))
                .onBindViewHolder((holder1, list1, position, payloads) -> {
                    AppInfo info = list1.get(position);
                    holder1.getTextView(R.id.item_title).setText(info.getAppTitle());
                    holder1.getTextView(R.id.item_info).setText(info.getAppSize());
                    Glide.with(context).load(info.getAppIcon()).into(holder1.getImageView(R.id.item_icon));
                })
                .onItemClick((holder13, view1, data) -> _mActivity.start(AppDetailFragment.newInstance(data)))
                .build();
        HttpApi.connect(url)
                .onSuccess(data -> {
                    Elements elements = data.select("item");
                    for (Element element : elements) {
                        AppInfo info = AppInfo.parse(element);
                        if (info == null) {
                            continue;
                        }
                        list.add(info);
                        if (list.size() == 8) {
                            break;
                        }
                    }
                    recyclerView.notifyDataSetChanged();
                })
                .onError(this)
                .subscribe();
    }

    protected void getCollection(final EasyViewHolder holder) {
        EasyRecyclerView<CollectionInfo> recyclerView = new EasyRecyclerView<>(holder.getView(R.id.recycler_view));
        holder.getItemView().setTag(recyclerView);
        List<CollectionInfo> list = new ArrayList<>();
        int margin = ScreenUtils.dp2pxInt(context, 12);
        recyclerView.setData(list)
                .setItemRes(R.layout.item_app_collection)
                .setLayoutManager(new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false))
                .onBindViewHolder((holder1, list1, position, payloads) -> {
                    CollectionInfo info = list1.get(position);
                    LCardView cardView = holder.getView(R.id.card_view);
                    cardView.setShadowSize(0);
                    cardView.setShadowAlpha(0);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) cardView.getLayoutParams();
                    if (position % 2 == 0) {
                        if (position == 0) {
                            params.setMargins(margin, 0, margin / 2, margin / 2);
                        } else if (position == list1.size() - 2) {
                            params.setMargins(margin / 2, 0, margin, margin / 2);
                        } else {
                            params.setMargins(margin / 2, 0, margin / 2, margin / 2);
                        }
                    } else {
                        if (position == 1) {
                            params.setMargins(margin, margin / 2, margin / 2, 0);
                        } else if (position == list1.size() - 1) {
                            params.setMargins(margin / 2, margin / 2, margin, 0);
                        } else {
                            params.setMargins(margin / 2, margin / 2, margin / 2, 0);
                        }
                    }
                    cardView.setLayoutParams(params);

                    holder1.getTextView(R.id.item_title).setText(info.getTitle());
                    holder1.setText(R.id.tv_view_count, info.getViewCount() + "");
                    holder1.setText(R.id.tv_favorite_count, info.getFavCount() + "");
                    holder1.setText(R.id.tv_support_count, info.getSupportCount() + "");
                    for (int i = 0; i < RES_ICONS.length; i++) {
                        int res = RES_ICONS[i];
                        if (i == 0) {
                            Glide.with(context)
                                    .load(info.getIcons().get(0))
                                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(context, 7)))
                                    .into(new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            holder1.getView(R.id.img_bg).setBackground(resource);
                                        }
                                    });
                        }
                        Glide.with(context).load(info.getIcons().get(i)).into(holder1.getImageView(res));
                    }
                })
                .onItemClick((holder14, view12, data) -> _mActivity.start(CollectionDetailFragment.newInstance(data)))
                .build();

        HttpApi.collectionRecommond()
                .onSuccess(data -> {
                    Elements elements = data.select("item");
                    for (Element element : elements) {
                        list.add(CollectionInfo.create(element));
                    }
                    if (list.size() % 2 != 0) {
                        list.remove(list.size() - 1);
                    }
                    recyclerView.notifyDataSetChanged();
                })
                .onError(this)
                .subscribe();
    }

    protected void getTutorial(final EasyViewHolder holder, final String type, final int index) {
        EasyRecyclerView<ArticleInfo> recyclerView = new EasyRecyclerView<>(holder.getView(R.id.recycler_view));
        holder.getItemView().setTag(recyclerView);
        final List<ArticleInfo> articleInfoList = new ArrayList<>();
        recyclerView.setData(articleInfoList)
                .setItemRes(R.layout.item_tutorial)
                .setLayoutManager(new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false))
                .onBindViewHolder((holder1, list, position, payloads) -> {
                    ArticleInfo info = list.get(position);
//                        Log.d("onBindViewHolder", "position=" + position + " ArticleInfo=" + info);
                    Glide.with(context).load(info.getImage()).into(holder1.getImageView(R.id.iv_image));
                    holder1.getTextView(R.id.tv_title).setText(info.getTitle());
                })
                .onItemClick((holder12, view1, data) -> _mActivity.start(ArticleDetailFragment.newInstance("https://" + type + ".shouji.com.cn" + data.getUrl())))
                .build();

        HttpApi.connect(String.format(Locale.CHINA, "https://%s.shouji.com.cn/newslist/list_%d_1.html", type, index))
                .onSuccess(data -> {
                    Elements elements = data.selectFirst("ul.news_list").select("li");
                    articleInfoList.clear();
                    for (Element element : elements) {
                        articleInfoList.add(ArticleInfo.from(element));
                    }
                    recyclerView.notifyDataSetChanged();
                })
                .onError(this)
                .subscribe();
    }

    @Override
    public void onError(Throwable e) {
        post(() -> AToast.error("加载失败！" + e.getMessage()));
    }

}
