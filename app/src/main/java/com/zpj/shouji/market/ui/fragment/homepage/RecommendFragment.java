package com.zpj.shouji.market.ui.fragment.homepage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.felix.atoast.library.AToast;
import com.sunfusheng.GroupRecyclerViewAdapter;
import com.sunfusheng.GroupViewHolder;
import com.sunfusheng.HeaderGroupRecyclerViewAdapter;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZViewHolder;
import com.zpj.http.parser.html.nodes.Document;
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
import com.zpj.shouji.market.model.SubjectInfo;
import com.zpj.shouji.market.ui.fragment.AppListFragment;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.fragment.base.RecyclerLayoutFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.HttpUtil;
import com.zpj.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import www.linwg.org.lib.LCardView;

public class RecommendFragment extends RecyclerLayoutFragment<GroupItem> {

    private static final String TAG = "RecommendFragment";

    private final int[] RES_ICONS = {R.id.item_icon_1, R.id.item_icon_2, R.id.item_icon_3};

    private MZBannerView<AppInfo> mMZBanner;

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_recommend_card;
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<GroupItem> recyclerLayout) {
        recyclerLayout.getEasyRecyclerView().getRecyclerView().setBackgroundColor(getResources().getColor(R.color.color_background));
        recyclerLayout.setHeaderView(R.layout.layout_recommend_header, new IEasy.OnBindHeaderListener() {
            private final List<AppInfo> bannerItemList = new ArrayList<>();
            private final BannerViewHolder bannerViewHolder = new BannerViewHolder();

            @Override
            public void onBindHeader(EasyViewHolder holder) {
                if (mMZBanner == null) {
                    mMZBanner = holder.getView(R.id.banner);
                    getBanners();
                } else {
                    mMZBanner.setPages(bannerItemList, BannerViewHolder::new);
                    mMZBanner.start();
                }
            }

            private void getBanners() {
                ExecutorHelper.submit(() -> {
                    try {
                        Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/app_index_xml.jsp?index=1&versioncode=198");
                        Elements elements = doc.select("item");
                        bannerItemList.clear();
                        for (Element element : elements) {
                            AppInfo info = AppInfo.parse(element);
                            if (info == null) {
                                continue;
                            }
                            bannerItemList.add(info);
                        }
                        post(() -> {
                            mMZBanner.setPages(bannerItemList, () -> bannerViewHolder);
                            mMZBanner.start();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        onError(e);
                    }
                });
            }
        })
                .onGetChildViewType(position -> position + 1);
    }

    @Override
    public void onRefresh() {
        mMZBanner = null;
        data.clear();
        recyclerLayout.notifyDataSetChanged();
        data.add(new GroupItem("最近更新"));
        data.add(new GroupItem("应用集推荐"));
        data.add(new GroupItem("应用推荐"));
        data.add(new GroupItem("游戏推荐"));
        data.add(new GroupItem("专题推荐"));
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
    public void onResume() {
        super.onResume();
        if (mMZBanner != null) {
            mMZBanner.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMZBanner != null) {
            mMZBanner.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMZBanner != null) {
            mMZBanner.pause();
        }
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, GroupItem data) {

    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, GroupItem data) {
        return false;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<GroupItem> list, int position, List<Object> payloads) {
        holder.getTextView(R.id.tv_title).setText(list.get(position).getTitle());
        if (holder.getItemView().getTag() instanceof EasyRecyclerView) {
            ((EasyRecyclerView) holder.getItemView().getTag()).notifyDataSetChanged();
            return;
        }
        RelativeLayout rlHeader = holder.getView(R.id.rl_header);
        RecyclerView view = holder.getView(R.id.recycler_view);
        LCardView cardView = holder.getView(R.id.card_view);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) cardView.getLayoutParams();
        LinearLayout.LayoutParams rlParams = (LinearLayout.LayoutParams) rlHeader.getLayoutParams();
        rlParams.setMarginStart(0);
        rlParams.setMarginEnd(0);
        int margin = ScreenUtil.dp2pxInt(context, 12);
        int padding = ScreenUtil.dp2pxInt(context, 8);
        view.setPadding(padding, padding, padding, padding);
        switch (holder.getViewType()) {
            case 1:
                params.setMargins(margin, margin, margin, margin / 2);
                cardView.setCardBackgroundColor(Color.WHITE);
                getAppInfo(holder, "http://tt.shouji.com.cn/androidv3/app_list_xml.jsp?index=1&versioncode=198");
                break;
            case 2:
                params.setMargins(0, margin / 2, 0, margin / 2);
                rlParams.setMarginStart(margin);
                rlParams.setMarginEnd(margin);
                cardView.setCardBackgroundColor(Color.TRANSPARENT);
                view.setPadding(0, 0, 0, 0);
                getCollection(holder);
                break;
            case 3:
                params.setMargins(margin, margin / 2, margin, margin / 2);
                cardView.setCardBackgroundColor(Color.WHITE);
                getAppInfo(holder, "http://tt.shouji.com.cn/androidv3/special_list_xml.jsp?id=-9998");
                break;
            case 4:
                params.setMargins(margin, margin / 2, margin, margin / 2);
                cardView.setCardBackgroundColor(Color.WHITE);
                getAppInfo(holder, "http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sdk=100&sort=day");
                break;
            case 5:
                params.setMargins(0, margin / 2, 0, 0);
                rlParams.setMarginStart(margin);
                rlParams.setMarginEnd(margin);
                cardView.setCardBackgroundColor(Color.TRANSPARENT);
                view.setPadding(0, 0, 0, 0);
                getSubjects(holder);
                break;
        }
        rlHeader.setLayoutParams(rlParams);
        cardView.setLayoutParams(params);
    }

    private void getAppInfo(EasyViewHolder holder, final String url) {
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
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument(url);
                Elements elements = doc.select("item");
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
                post(recyclerView::notifyDataSetChanged);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void getCollection(final EasyViewHolder holder) {
        EasyRecyclerView<CollectionInfo> recyclerView = new EasyRecyclerView<>(holder.getView(R.id.recycler_view));
        holder.getItemView().setTag(recyclerView);
        List<CollectionInfo> list = new ArrayList<>();
        int margin = ScreenUtil.dp2pxInt(context, 12);
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
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/yyj_tj_xml.jsp");
                Elements elements = doc.select("item");
                for (Element element : elements) {
                    list.add(CollectionInfo.create(element));
                }
                if (list.size() % 2 != 0) {
                    list.remove(list.size() - 1);
                }
                post(recyclerView::notifyDataSetChanged);
            } catch (Exception e) {
                e.printStackTrace();
                onError(e);
            }
        });
    }

    private void getSubjects(EasyViewHolder holder) {
        EasyRecyclerView<SubjectInfo> recyclerView = new EasyRecyclerView<>(holder.getView(R.id.recycler_view));
        holder.getItemView().setTag(recyclerView);
        List<SubjectInfo> list = new ArrayList<>();
        int margin = ScreenUtil.dp2pxInt(context, 12);
        recyclerView.setData(list)
                .setItemRes(R.layout.item_app_subject)
                .setLayoutManager(new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false))
                .onBindViewHolder((holder12, list12, position, payloads) -> {

                    LCardView cardView = holder.getView(R.id.card_view);
                    cardView.setShadowSize(0);
                    cardView.setShadowAlpha(0);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) cardView.getLayoutParams();
                    if (position % 2 == 0) {
                        if (position == 0) {
                            params.setMargins(margin, 0, margin / 2, margin / 2);
                        } else if (position == list12.size() - 2) {
                            params.setMargins(margin / 2, 0, margin, margin / 2);
                        } else {
                            params.setMargins(margin / 2, 0, margin / 2, margin / 2);
                        }
                    } else {
                        if (position == 1) {
                            params.setMargins(margin, margin / 2, margin / 2, 0);
                        } else if (position == list12.size() - 1) {
                            params.setMargins(margin / 2, margin / 2, margin, 0);
                        } else {
                            params.setMargins(margin / 2, margin / 2, margin / 2, 0);
                        }
                    }
                    cardView.setLayoutParams(params);

                    SubjectInfo info = list12.get(position);
                    holder12.setText(R.id.tv_title, info.getTitle());
                    holder12.setText(R.id.tv_comment, info.getComment());
                    holder12.setText(R.id.tv_m, info.getM());
                    Glide.with(context).load(info.getIcon()).into(holder12.getImageView(R.id.iv_icon));
                })
                .onItemClick((holder15, view13, data) -> _mActivity.start(AppListFragment.newInstance("http://tt.shouji.com.cn/androidv3/special_list_xml.jsp?id=" + data.getId())))
                .build();
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/special_index_xml.jsp?jse=yes");
                Elements elements = doc.select("item");
                for (int i = 0; i < elements.size(); i++) {
                    list.add(SubjectInfo.create(elements.get(i)));
                }
                if (list.size() % 2 != 0) {
                    list.remove(list.size() - 1);
                }
                post(recyclerView::notifyDataSetChanged);
            } catch (Exception e) {
                e.printStackTrace();
                onError(e);
            }
        });
    }

    private void onError(Exception e) {
        post(() -> AToast.error("加载失败！" + e.getMessage()));
    }

    private static class BannerViewHolder implements MZViewHolder<AppInfo> {
        private ImageView mImageView;

        @Override
        public View createView(Context context) {
            // 返回页面布局
            View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null, false);
            mImageView = view.findViewById(R.id.img_view);
            return view;
        }

        @Override
        public void onBind(Context context, int position, AppInfo item) {
            Glide.with(context).load(item.getAppIcon()).into(mImageView);
        }
    }

}
