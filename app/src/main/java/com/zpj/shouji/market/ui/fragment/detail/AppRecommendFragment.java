package com.zpj.shouji.market.ui.fragment.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.felix.atoast.library.AToast;
import com.sunfusheng.GroupRecyclerViewAdapter;
import com.sunfusheng.GroupViewHolder;
import com.sunfusheng.HeaderGroupRecyclerViewAdapter;
import com.sunfusheng.StickyHeaderDecoration;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.bean.AppCollectionItem;
import com.zpj.shouji.market.bean.AppItem;
import com.zpj.shouji.market.glide.blur.BlurTransformation;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;

public class AppRecommendFragment extends BaseFragment implements GroupRecyclerViewAdapter.OnItemClickListener<AppRecommendFragment.ItemWrapper> {

    private static final String KEY_ID = "key_id";
    private final List<List<ItemWrapper>> dataList = new ArrayList<>();
    private List<ItemWrapper> appCollectionList = new ArrayList<>();
    private List<ItemWrapper> recommendAppList = new ArrayList<>();

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecommendAdapter adapter;

    private String id;

    public static AppRecommendFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        AppRecommendFragment fragment = new AppRecommendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_detail_recomment;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            return;
        }
        id = getArguments().getString(KEY_ID, "");
        recyclerView = view.findViewById(R.id.recycler_view_recent_update);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> recyclerView.postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            initData();
        }, 1000));

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.addItemDecoration(new StickyHeaderDecoration());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecommendAdapter(getContext(), dataList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initData();
    }

    @Override
    public void onItemClick(GroupRecyclerViewAdapter<ItemWrapper> adapter, GroupViewHolder holder, ItemWrapper data, int groupPosition, int childPosition) {
        if (data.getAppItem() != null) {
            AppDetailFragment fragment = AppDetailFragment.newInstance(data.getAppItem());
            _mActivity.start(fragment);
        } else if (data.getCollectionItem() != null) {
            AToast.normal("TODO Collection");
        }
    }

    private void initData() {
        dataList.clear();
        appCollectionList.clear();
        recommendAppList.clear();
        appCollectionList.add(new ItemWrapper("相关应用集"));
        recommendAppList.add(new ItemWrapper("相关应用"));
        dataList.add(appCollectionList);
        dataList.add(recommendAppList);
        adapter.notifyDataSetChanged();
        getSimilar();
    }

    private void getSimilar() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/soft_yyj_similar.jsp?id=" + id);
                Elements elements = doc.select("item");
                for (Element element : elements) {
                    if (element.selectFirst("viewtype").text().equals("yyj")) {
                        for (Element recognizeItem : element.selectFirst("recognizelist").select("recognize")) {
                            appCollectionList.add(new ItemWrapper(AppCollectionItem.buildSimilarCollection(recognizeItem)));
                        }
                        post(() -> adapter.updateGroup(0, appCollectionList));
                    } else {
                        recommendAppList.add(new ItemWrapper(AppItem.create(element)));
                    }
                }
                post(() -> adapter.updateGroup(1, recommendAppList));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public class ItemWrapper {

        private AppItem appItem;
        private AppCollectionItem collectionItem;
        private String title;

        ItemWrapper() {

        }

        ItemWrapper(AppCollectionItem collectionItem) {
            this.collectionItem = collectionItem;
        }

        ItemWrapper(AppItem appItem) {
            this.appItem = appItem;
        }

        ItemWrapper(String title) {
            this.title = title;
        }

        public AppCollectionItem getCollectionItem() {
            return collectionItem;
        }

        public AppItem getAppItem() {
            return appItem;
        }

        public String getTitle() {
            return title;
        }

    }

    public class RecommendAdapter extends HeaderGroupRecyclerViewAdapter<ItemWrapper> {

        private final int[] RES_ICONS = {R.id.item_icon_1, R.id.item_icon_2, R.id.item_icon_3};

        public static final int TYPE_CHILD_COLLECTION = 332;
        public static final int TYPE_CHILD_RECOMMEND = 333;

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
        public int getChildItemViewType(int groupPosition, int childPosition) {
            if (groupPosition == 0) {
                return TYPE_CHILD_COLLECTION;
            } else if (groupPosition == 1) {
                return TYPE_CHILD_RECOMMEND;
            }
            return super.getChildItemViewType(groupPosition, childPosition);
        }

        @Override
        public int getHeaderLayoutId(int viewType) {
            return R.layout.item_recommend_header;
        }

        @Override
        public int getChildLayoutId(int viewType) {
            if (viewType == TYPE_CHILD_COLLECTION) {
                return R.layout.item_app_collection;
            }
            return R.layout.item_app_grid;
        }

        @Override
        public int getFooterLayoutId(int viewType) {
            return 0;
        }

        @Override
        public void onBindHeaderViewHolder(GroupViewHolder holder, ItemWrapper item, int groupPosition) {
            holder.setText(R.id.text_title, item.getTitle());
        }

        @Override
        public void onBindChildViewHolder(GroupViewHolder holder, ItemWrapper item, int groupPosition, int childPosition) {
            int viewType = getChildItemViewType(groupPosition, childPosition);
            if (viewType == TYPE_CHILD_RECOMMEND) {
                final AppItem appItem = item.getAppItem();
                if (appItem == null) {
                    return;
                }
                holder.setText(R.id.item_title, appItem.getAppTitle());
                holder.setText(R.id.item_info, appItem.getAppSize());
                Glide.with(context).load(appItem.getAppIcon()).into((ImageView) holder.get(R.id.item_icon));
            } else if (viewType == TYPE_CHILD_COLLECTION) {
                long time1 = System.currentTimeMillis();
                final AppCollectionItem appItem = item.getCollectionItem();
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
                                .apply(RequestOptions.bitmapTransform(new BlurTransformation(7, 10)))
                                .into((ImageView) holder.get(R.id.img_bg));
                    }
                    Glide.with(context).load(appItem.getIcons().get(i)).into((ImageView) holder.get(res));
                }
                Log.d("onBindChildViewHolder", "deltaTime=" + (System.currentTimeMillis() - time1));
            }
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
                        if (isGroupChild(0, position)) {
                            return 2;
                        }
                        int span = isChild(position)
                                ? 1 : gridManager.getSpanCount();
                        return span;
                    }
                });
            }
        }

    }

}
