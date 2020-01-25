package com.zpj.shouji.market.ui.fragment.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.glide.blur.BlurTransformation;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;

public class AppRecommendFragment extends BaseFragment
        implements GroupRecyclerViewAdapter.OnItemClickListener<AppRecommendFragment.ItemWrapper> {

    private static final String KEY_ID = "key_id";
    private final List<List<ItemWrapper>> dataList = new ArrayList<>();

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

//        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
//        recyclerView.addItemDecoration(new StickyHeaderDecoration());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
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

    }

    private void initData() {
        dataList.clear();
        List<ItemWrapper> list0 = new ArrayList<>();
        list0.add(new ItemWrapper("相关应用集"));
        list0.add(new ItemWrapper());

        List<ItemWrapper> list1 = new ArrayList<>();
        list1.add(new ItemWrapper("相关应用"));
        list1.add(new ItemWrapper());

        dataList.add(list0);
        dataList.add(list1);
        adapter.notifyDataSetChanged();
    }

    public class ItemWrapper {

        private String title;

        ItemWrapper() {

        }

        ItemWrapper(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

    }

    public class RecommendAdapter extends HeaderGroupRecyclerViewAdapter<ItemWrapper> {

        private final int[] RES_ICONS = {R.id.item_icon_1, R.id.item_icon_2, R.id.item_icon_3};

        private List<CollectionInfo> appCollectionList = new ArrayList<>();
        private List<AppInfo> recommendAppList = new ArrayList<>();

        private EasyRecyclerView<CollectionInfo> collectionRecyclerView;
        private EasyRecyclerView<AppInfo> appRecyclerView;

        static final int TYPE_CHILD_COLLECTION = 332;
        static final int TYPE_CHILD_RECOMMEND = 333;

        RecommendAdapter(Context context, List<List<ItemWrapper>> groups) {
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
            return R.layout.layout_recycler_view;
        }

        @Override
        public int getFooterLayoutId(int viewType) {
            return 0;
        }

        @Override
        public void onBindHeaderViewHolder(GroupViewHolder holder, ItemWrapper item, int groupPosition) {
            holder.setText(R.id.tv_title, item.getTitle());
        }

        @Override
        public void onBindChildViewHolder(GroupViewHolder holder, ItemWrapper item, int groupPosition, int childPosition) {
            Object object = holder.itemView.getTag();
            if (object instanceof EasyRecyclerView) {
                ((EasyRecyclerView) object).notifyDataSetChanged();
                return;
            }
            int viewType = getChildItemViewType(groupPosition, childPosition);
            if (viewType == TYPE_CHILD_RECOMMEND) {
                getAppInfo(holder.itemView);
            } else if (viewType == TYPE_CHILD_COLLECTION) {
                getCollection(holder.itemView);
                getSimilar();
            }
        }

        private void getAppInfo(View itemView) {
            RecyclerView view = itemView.findViewById(R.id.recycler_view);
            appRecyclerView = new EasyRecyclerView<>(view);
            itemView.setTag(appRecyclerView);
            appRecyclerView.setData(recommendAppList)
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
        }

        private void getSimilar() {
            ExecutorHelper.submit(() -> {
                try {
                    Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/soft_yyj_similar.jsp?id=" + id);
                    Elements elements = doc.select("item");
                    for (Element element : elements) {
                        if ("yyj".equals(element.selectFirst("viewtype").text())) {
                            for (Element recognizeItem : element.selectFirst("recognizelist").select("recognize")) {
                                appCollectionList.add(CollectionInfo.buildSimilarCollection(recognizeItem));
                            }
                            post(() -> {
                                if (collectionRecyclerView != null) {
                                    collectionRecyclerView.notifyDataSetChanged();
                                }
                            });
                        } else {
                            recommendAppList.add(AppInfo.parse(element));
                        }
                    }
                    post(() -> {
                        if (appRecyclerView != null) {
                            appRecyclerView.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        private void getCollection(final View itemView) {
            RecyclerView view = itemView.findViewById(R.id.recycler_view);
            collectionRecyclerView = new EasyRecyclerView<>(view);
            itemView.setTag(collectionRecyclerView);
            collectionRecyclerView.setData(appCollectionList)
                    .setItemRes(R.layout.item_app_collection)
                    .setLayoutManager(new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false))
                    .onBindViewHolder((holder1, list1, position, payloads) -> {
                        CollectionInfo info = list1.get(position);
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
                                        .into(holder1.getImageView(R.id.img_bg));
                            }
                            Glide.with(context).load(info.getIcons().get(i)).into(holder1.getImageView(res));
                        }
                    })
                    .onItemClick((holder14, view12, data) -> _mActivity.start(CollectionDetailFragment.newInstance(data)))
                    .build();
        }

    }

}
