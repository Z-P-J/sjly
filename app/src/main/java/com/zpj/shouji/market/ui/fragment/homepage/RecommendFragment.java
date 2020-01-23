package com.zpj.shouji.market.ui.fragment.homepage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.felix.atoast.library.AToast;
import com.gyf.immersionbar.ImmersionBar;
import com.sunfusheng.GroupRecyclerViewAdapter;
import com.sunfusheng.GroupViewHolder;
import com.sunfusheng.HeaderGroupRecyclerViewAdapter;
import com.sunfusheng.StickyHeaderDecoration;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.model.AppItem;
import com.zpj.shouji.market.model.SubjectItem;
import com.zpj.shouji.market.glide.blur.BlurTransformation;
import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.utils.HttpUtil;
import com.zpj.shouji.market.utils.ExecutorHelper;

import java.util.ArrayList;
import java.util.List;

public class RecommendFragment extends BaseFragment implements GroupRecyclerViewAdapter.OnItemClickListener<RecommendFragment.ItemWrapper> {

    private final List<List<ItemWrapper>> dataList = new ArrayList<>();
    private List<AppItem> bannerItemList = new ArrayList<>();
    private List<ItemWrapper> topList = new ArrayList<>();
    private List<ItemWrapper> updateList = new ArrayList<>();
    private List<ItemWrapper> appCollectionList = new ArrayList<>();
    private List<ItemWrapper> recommendAppList = new ArrayList<>();
    private List<ItemWrapper> recommendGameList = new ArrayList<>();
    private List<ItemWrapper> recommendSubjectList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MZBannerView<AppItem> mMZBanner;

    private RecommendAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recomment;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_view_recent_update);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> recyclerView.postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            initData();
//            nextUrl = DEFAULT_LIST_URL;
        }, 1000));

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.addItemDecoration(new StickyHeaderDecoration() {
            @Override
            protected boolean isStickHeader(int groupPosition) {
                return groupPosition != 0;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecommendAdapter(getContext(), dataList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
//        recyclerView.setItemViewCacheSize(50);
//        recyclerView.setDrawingCacheEnabled(true);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initData();
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
    public void onItemClick(GroupRecyclerViewAdapter adapter, GroupViewHolder holder, ItemWrapper data, int groupPosition, int childPosition) {
        if (groupPosition == 0) {
            return;
        }
        if (data.getAppItem() != null) {
            AppDetailFragment fragment = AppDetailFragment.newInstance(data.getAppItem());
            _mActivity.start(fragment);
        } else if (data.getCollectionItem() != null) {
            AToast.normal("TODO Collection");
            _mActivity.start(CollectionDetailFragment.newInstance(data.getCollectionItem()));
        }
    }

    private void initData() {
        dataList.clear();
        topList.clear();
        updateList.clear();
        bannerItemList.clear();
        appCollectionList.clear();
        recommendAppList.clear();
        recommendGameList.clear();
        recommendSubjectList.clear();
        topList.add(new ItemWrapper());
        updateList.add(new ItemWrapper("最近更新"));
        appCollectionList.add(new ItemWrapper("应用集推荐"));
        recommendAppList.add(new ItemWrapper("应用推荐"));
        recommendGameList.add(new ItemWrapper("游戏推荐"));
        recommendSubjectList.add(new ItemWrapper("专题推荐"));
        dataList.add(topList);
        dataList.add(updateList);
        dataList.add(appCollectionList);
        dataList.add(recommendAppList);
        dataList.add(recommendGameList);
        dataList.add(recommendSubjectList);
        adapter.notifyDataSetChanged();
        getRecentUpdates();
        getAppCollections();
        getRecommendApps();
        getRecommendGames();
        getRecommendSubjects();
    }

    private void getRecentUpdates() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/app_list_xml.jsp?index=1&versioncode=198");
                Elements elements = doc.select("item");
                int count = elements.size() > 9 ? 9 : elements.size();
                for (int i = 1; i < count; i++) {
                    updateList.add(new ItemWrapper(AppItem.create(elements.get(i))));
                }
                post(() -> adapter.updateGroup(1, updateList));
            } catch (Exception e) {
                e.printStackTrace();
                onError(e);
            }
        });
    }

    private void getAppCollections() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/yyj_tj_xml.jsp");
                Elements elements = doc.select("item");
                int count = elements.size() > 9 ? 9 : elements.size();
                for (int i = 1; i < count; i++) {
                    appCollectionList.add(new ItemWrapper(CollectionInfo.create(elements.get(i))));
                }
//                recyclerView.postDelayed(() -> adapter.updateGroup(2, appCollectionList), 2);
                post(() -> adapter.updateGroup(2, appCollectionList));
            } catch (Exception e) {
                e.printStackTrace();
                onError(e);
            }
        });
    }

    private void getRecommendApps() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/special_list_xml.jsp?id=-9998");
                Elements elements = doc.select("item");
                int count = elements.size() > 8 ? 8 : elements.size();
                for (int i = 0; i < count; i++) {
                    recommendAppList.add(new ItemWrapper(AppItem.create(elements.get(i))));
                }
                post(() -> adapter.updateGroup(3, recommendAppList));
            } catch (Exception e) {
                e.printStackTrace();
                onError(e);
            }
        });
    }

    private void getRecommendGames() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sdk=100&sort=day");
                Elements elements = doc.select("item");
                int count = elements.size() > 9 ? 9 : elements.size();
                for (int i = 1; i < count; i++) {
                    recommendGameList.add(new ItemWrapper(AppItem.create(elements.get(i))));
                }
//                recyclerView.postDelayed(() -> adapter.updateGroup(4, recommendGameList), 4);
                post(() -> adapter.updateGroup(4, recommendGameList));
            } catch (Exception e) {
                e.printStackTrace();
                onError(e);
            }
        });
    }

    private void getRecommendSubjects() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/special_index_xml.jsp?jse=yes");
                Elements elements = doc.select("item");
                int count = elements.size() > 8 ? 8 : elements.size();
                for (int i = 0; i < count; i++) {
                    recommendSubjectList.add(new ItemWrapper(SubjectItem.create(elements.get(i))));
                }
//                recyclerView.postDelayed(() -> adapter.updateGroup(5, recommendSubjectList), 5);
                post(() -> adapter.updateGroup(5, recommendSubjectList));
            } catch (Exception e) {
                e.printStackTrace();
                onError(e);
            }
        });
    }

    private void getBanners() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/app_index_xml.jsp?index=1&versioncode=198");
                Elements elements = doc.select("item");
                bannerItemList.clear();
                for (int i = 1; i < elements.size(); i++) {
                    bannerItemList.add(AppItem.create(elements.get(i)));
                }
                post(() -> {
                    mMZBanner.setPages(bannerItemList, (MZHolderCreator<BannerViewHolder>) BannerViewHolder::new);
                    mMZBanner.start();
                });
            } catch (Exception e) {
                e.printStackTrace();
                onError(e);
            }
        });
    }

    private void onError(Exception e) {
        post(() -> AToast.error("加载失败！" + e.getMessage()));
    }

    private static class BannerViewHolder implements MZViewHolder<AppItem> {
        private ImageView mImageView;

        @Override
        public View createView(Context context) {
            // 返回页面布局
            View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null, false);
            mImageView = view.findViewById(R.id.img_view);
            return view;
        }

        @Override
        public void onBind(Context context, int position, AppItem item) {
            Glide.with(context).load(item.getAppIcon()).into(mImageView);
        }
    }

    class ItemWrapper {

        private AppItem appItem;
        private CollectionInfo collectionItem;
        private SubjectItem subjectItem;
        private String title;
        public Drawable icon;

        ItemWrapper() {

        }

        ItemWrapper(SubjectItem subjectItem) {
            this.subjectItem = subjectItem;
        }

        ItemWrapper(CollectionInfo collectionItem) {
            this.collectionItem = collectionItem;
        }

        ItemWrapper(AppItem appItem) {
            this.appItem = appItem;
        }

        ItemWrapper(String title) {
            this.title = title;
        }

        public CollectionInfo getCollectionItem() {
            return collectionItem;
        }

        public AppItem getAppItem() {
            return appItem;
        }

        public SubjectItem getSubjectItem() {
            return subjectItem;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public Drawable getIcon() {
            return icon;
        }
    }

    private class RecommendAdapter extends HeaderGroupRecyclerViewAdapter<ItemWrapper> {

        private final int[] RES_ICONS = {R.id.item_icon_1, R.id.item_icon_2, R.id.item_icon_3};

        public static final int TYPE_TOP_HEADER = 11;
        public static final int TYPE_SUB_HEADER = 22;
        public static final int TYPE_CHILD_UPDATE = 331;
        public static final int TYPE_CHILD_COLLECTION = 332;
        public static final int TYPE_CHILD_RECOMMEND = 333;
        public static final int TYPE_CHILD_SUBJECT = 334;

        public RecommendAdapter(Context context, List<List<ItemWrapper>> groups) {
            super(context, groups);
        }

        @Override
        public boolean showHeader() {
            return true;
        }

        @Override
        public boolean showFooter() {
            return false;
        }

        @Override
        public int getHeaderItemViewType(int groupPosition) {
            if (groupPosition == 0) {
                return TYPE_TOP_HEADER;
            }
            return TYPE_SUB_HEADER;
        }

        @Override
        public int getChildItemViewType(int groupPosition, int childPosition) {
            if (groupPosition == 1) {
                return TYPE_CHILD_UPDATE;
            } else if (groupPosition == 2) {
                return TYPE_CHILD_COLLECTION;
            } else if (groupPosition == 3 || groupPosition == 4) {
                return TYPE_CHILD_RECOMMEND;
            } else if (groupPosition == 5) {
                return TYPE_CHILD_SUBJECT;
            }
            return super.getChildItemViewType(groupPosition, childPosition);
        }

        @Override
        public int getHeaderLayoutId(int viewType) {
            if (viewType == TYPE_TOP_HEADER) {
                return R.layout.layout_recommend_header;
            } else {
                return R.layout.item_recommend_header;
            }
        }

        @Override
        public int getChildLayoutId(int viewType) {
            if (viewType == TYPE_CHILD_COLLECTION) {
                return R.layout.item_app_collection;
            } else if (viewType == TYPE_CHILD_SUBJECT) {
                return R.layout.item_app_subject;
            }
            return R.layout.item_app_grid;
        }

        @Override
        public int getFooterLayoutId(int viewType) {
            return 0;
        }

        @Override
        public void onBindHeaderViewHolder(GroupViewHolder holder, ItemWrapper item, int groupPosition) {
            int viewType = getHeaderItemViewType(groupPosition);
            if (viewType == TYPE_TOP_HEADER) {
                mMZBanner = holder.get(R.id.banner);
                if (bannerItemList.isEmpty()) {
                    getBanners();
                } else {
                    mMZBanner.setPages(bannerItemList, (MZHolderCreator<BannerViewHolder>) BannerViewHolder::new);
                    mMZBanner.start();
                }
            } else if (viewType == TYPE_SUB_HEADER) {
                holder.setText(R.id.tv_title, item.getTitle());
            }
        }

        @Override
        public void onBindChildViewHolder(GroupViewHolder holder, ItemWrapper item, int groupPosition, int childPosition) {
            int viewType = getChildItemViewType(groupPosition, childPosition);
            Log.d("onBindChildViewHolder", "groupPosition=" + groupPosition + " childPosition=" + childPosition + " viewType=" + viewType);
            if (viewType == TYPE_CHILD_UPDATE || viewType == TYPE_CHILD_RECOMMEND) {
                final AppItem appItem = item.getAppItem();
                if (appItem == null) {
                    return;
                }
                holder.setText(R.id.item_title, appItem.getAppTitle());
                holder.setText(R.id.item_info, appItem.getAppSize());
                Log.d("onBindChildViewHolder", "holder.get(R.id.item_icon)=" + holder.get(R.id.item_icon));
                Glide.with(context).load(appItem.getAppIcon()).into((ImageView) holder.get(R.id.item_icon));
            } else if (viewType == TYPE_CHILD_COLLECTION) {
                long time1 = System.currentTimeMillis();
                final CollectionInfo appItem = item.getCollectionItem();
                if (appItem == null) {
                    return;
                }
                holder.setText(R.id.item_title, appItem.getTitle());
                holder.setText(R.id.tv_view_count, appItem.getViewCount() + "");
                holder.setText(R.id.tv_favorite_count, appItem.getFavCount() + "");
                holder.setText(R.id.tv_support_count, appItem.getSupportCount() + "");
                for (int i = 0; i < RES_ICONS.length; i++) {
                    int res = RES_ICONS[i];
                    if (i == 0) {
                        Glide.with(context)
                                .load(appItem.getIcons().get(0))
                                .apply(RequestOptions.bitmapTransform(new BlurTransformation(context, 7, 10)))
                                .into((ImageView) holder.get(R.id.img_bg));
                    }
                    Glide.with(context).load(appItem.getIcons().get(i)).into((ImageView) holder.get(res));
                }
                Log.d("onBindChildViewHolder", "deltaTime=" + (System.currentTimeMillis() - time1));
            } else if (viewType == TYPE_CHILD_SUBJECT) {
                final SubjectItem appItem = item.getSubjectItem();
                if (appItem == null) {
                    return;
                }
                holder.setText(R.id.tv_title, appItem.getTitle());
                holder.setText(R.id.tv_comment, appItem.getComment());
                holder.setText(R.id.tv_m, appItem.getM());
                Glide.with(context).load(appItem.getIcon()).into((ImageView) holder.get(R.id.iv_icon));
            }
        }

        @Override
        public void onBindFooterViewHolder(GroupViewHolder holder, ItemWrapper item, int groupPosition) {

        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (isGroupChild(2, position)) {
                            return 2;
                        }
                        if (isGroupChild(5, position)) {
                            return gridManager.getSpanCount();
                        }
                        int span = isChild(position)
                                ? 1 : gridManager.getSpanCount();
//                        Log.d("RecommendAdapter", "onAttachedToRecyclerView position=" + position + " span=" + span);
                        return span;
                    }
                });
            }
        }

    }

}
