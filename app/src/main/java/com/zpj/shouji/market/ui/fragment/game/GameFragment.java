package com.zpj.shouji.market.ui.fragment.game;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.blur.BlurTransformation;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.article.ArticleInfo;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.ui.fragment.ArticleDetailFragment;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GameFragment extends BaseFragment
        implements GroupRecyclerViewAdapter.OnItemClickListener<GameFragment.ItemWrapper> {

    private static final String TAG = "GameFragment";

    private final List<List<ItemWrapper>> dataList = new ArrayList<>();
    private List<ItemWrapper> updateList = new ArrayList<>();
//    private List<ItemWrapper> appCollectionList = new ArrayList<>();
    private List<ItemWrapper> recommendGameList = new ArrayList<>();
    private List<ItemWrapper> hotGameList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private RecommendAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_recomment;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_view_recent_update);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> recyclerView.postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            initData();
        }, 1000));

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
//        recyclerView.addItemDecoration(new StickyHeaderDecoration() {
//            @Override
//            protected boolean isStickHeader(int groupPosition) {
//                return groupPosition != 0;
//            }
//        });
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecommendAdapter(context, dataList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initData();
    }

    @Override
    public void onSupportInvisible() {
        lightStatusBar();
    }

    @Override
    public void onSupportVisible() {
        darkStatusBar();
    }

    @Override
    public void onItemClick(GroupRecyclerViewAdapter<ItemWrapper> adapter, GroupViewHolder holder, ItemWrapper data, int groupPosition, int childPosition) {
        if (groupPosition == 0) {
            return;
        }
        if (data.getAppInfo() != null) {
            AppDetailFragment fragment = AppDetailFragment.newInstance(data.getAppInfo());
            _mActivity.start(fragment);
        } else if (data.getCollectionItem() != null) {
            AToast.normal("TODO Collection");
            _mActivity.start(CollectionDetailFragment.newInstance(data.getCollectionItem()));
        }
    }

    private void initData() {
        dataList.clear();



        // TODO 排行

        List<ItemWrapper> topList = new ArrayList<>();
        topList.add(new ItemWrapper());
        dataList.add(topList);

        updateList.clear();
        updateList.add(new ItemWrapper("最近更新"));
        dataList.add(updateList);

//        appCollectionList.clear();
//        appCollectionList.add(new ItemWrapper("应用集推荐"));
//        dataList.add(appCollectionList);

        recommendGameList.clear();
        recommendGameList.add(new ItemWrapper("游戏推荐"));
        dataList.add(recommendGameList);

        hotGameList.clear();
        hotGameList.add(new ItemWrapper("热门网游"));
        dataList.add(hotGameList);

        List<ItemWrapper> tutorialList = new ArrayList<>();
        tutorialList.add(new ItemWrapper("游戏快递"));
        tutorialList.add(new ItemWrapper());
        dataList.add(tutorialList);

        List<ItemWrapper> weeklyList = new ArrayList<>();
        weeklyList.add(new ItemWrapper("游戏评测"));
        weeklyList.add(new ItemWrapper());
        dataList.add(weeklyList);

        List<ItemWrapper> profileList = new ArrayList<>();
        profileList.add(new ItemWrapper("游戏攻略"));
        profileList.add(new ItemWrapper());
        dataList.add(profileList);

        List<ItemWrapper> list1 = new ArrayList<>();
        list1.add(new ItemWrapper("游戏新闻"));
        list1.add(new ItemWrapper());
        dataList.add(list1);

        List<ItemWrapper> list2 = new ArrayList<>();
        list2.add(new ItemWrapper("游戏周刊"));
        list2.add(new ItemWrapper());
        dataList.add(list2);

        List<ItemWrapper> list3 = new ArrayList<>();
        list3.add(new ItemWrapper("游戏公告"));
        list3.add(new ItemWrapper());
        dataList.add(list3);

        adapter.notifyDataSetChanged();
        getRecentUpdates();
//        getAppCollections();
        getRecommendGames();
        getHotGames();
    }

    private void getRecentUpdates() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sort=time&versioncode=198");
                Elements elements = doc.select("item");
                int count = elements.size() > 9 ? 9 : elements.size();
                for (int i = 1; i < count; i++) {
                    updateList.add(new ItemWrapper(AppInfo.create(elements.get(i))));
                }
                post(() -> adapter.updateGroup(1, updateList));
            } catch (Exception e) {
                e.printStackTrace();
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
                    recommendGameList.add(new ItemWrapper(AppInfo.create(elements.get(i))));
                }
                post(() -> adapter.updateGroup(2, recommendGameList));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

//    private void getAppCollections() {
//        ExecutorHelper.submit(() -> {
//            try {
//                Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/yyj_tj_xml.jsp");
//                Elements elements = doc.select("item");
//                int count = elements.size() > 9 ? 9 : elements.size();
//                for (int i = 1; i < count; i++) {
//                    appCollectionList.add(new ItemWrapper(CollectionInfo.create(elements.get(i))));
//                }
//                post(() -> adapter.updateGroup(3, appCollectionList));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }

    private void getHotGames() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/netgame.jsp");
                Elements elements = doc.select("item");
                int count = elements.size() > 9 ? 9 : elements.size();
                for (int i = 1; i < count; i++) {
                    recommendGameList.add(new ItemWrapper(AppInfo.create(elements.get(i))));
                }
                post(() -> adapter.updateGroup(3, recommendGameList));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    class ItemWrapper {

        private AppInfo appInfo;
        private CollectionInfo collectionItem;
        private String title;
        public Drawable icon;

        ItemWrapper() {

        }

        ItemWrapper(CollectionInfo collectionItem) {
            this.collectionItem = collectionItem;
        }

        ItemWrapper(AppInfo appInfo) {
            this.appInfo = appInfo;
        }

        ItemWrapper(String title) {
            this.title = title;
        }

        public CollectionInfo getCollectionItem() {
            return collectionItem;
        }

        public AppInfo getAppInfo() {
            return appInfo;
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

//        private static final int TYPE_CHILD_EXPRESS = 30;
//        private static final int TYPE_CHILD_PROFILE = 31;
//        private static final int TYPE_CHILD_GUIDE = 32;
//        private static final int TYPE_CHILD_NEWS = 33;
        private static final int TYPE_CHILD_ARTICLE = 34;
//        private static final int TYPE_CHILD_WEEKLY = 35;

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
            } else if (groupPosition == 2 || groupPosition == 3) {
                return TYPE_CHILD_RECOMMEND;
            } else if (groupPosition >= 4 && groupPosition <= 9) {
                return TYPE_CHILD_ARTICLE;
            }
            return super.getChildItemViewType(groupPosition, childPosition);
        }

        @Override
        public int getHeaderLayoutId(int viewType) {
            if (viewType == TYPE_TOP_HEADER) {
                return R.layout.layout_app_header;
            } else {
                return R.layout.item_recommend_header;
            }
        }

        @Override
        public int getChildLayoutId(int viewType) {
            if (viewType == TYPE_CHILD_COLLECTION) {
                return R.layout.item_app_collection;
            } else if (viewType == TYPE_CHILD_ARTICLE) {
                return R.layout.layout_recycler_view;
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
                holder.get(R.id.cv_item1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                holder.get(R.id.cv_item2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else if (viewType == TYPE_SUB_HEADER) {
                holder.setText(R.id.tv_title, item.getTitle());
            }
        }

        @Override
        public void onBindChildViewHolder(GroupViewHolder holder, ItemWrapper item, int groupPosition, int childPosition) {
            int viewType = getChildItemViewType(groupPosition, childPosition);
            if (viewType == TYPE_CHILD_UPDATE || viewType == TYPE_CHILD_RECOMMEND) {
                final AppInfo appInfo = item.getAppInfo();
                if (appInfo == null) {
                    return;
                }
//                if (holder.get(R.id.item_icon) == null) {
//                    return;
//                }
                Log.d(TAG, "holder.get(R.id.item_icon)=" + holder.get(R.id.item_icon));
                Log.d(TAG, "groupPosition=" + groupPosition + " childPosition=" + childPosition + " viewType=" + viewType);
                holder.setText(R.id.item_title, appInfo.getAppTitle());
                holder.setText(R.id.item_info, appInfo.getAppSize());
                Glide.with(context).load(appInfo.getAppIcon()).into((ImageView) holder.get(R.id.item_icon));
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
            } else if (viewType == TYPE_CHILD_ARTICLE) {
                getTutorial(holder, String.format(Locale.CHINA, "https://game.shouji.com.cn/newslist/list_%d_1.html", groupPosition - 3));
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
                        for (int i = 4; i < 10; i++) {
                            if (isGroupChild(i, position)) {
                                return gridManager.getSpanCount();
                            }
                        }
                        return isChild(position) ? 1 : gridManager.getSpanCount();
                    }
                });
            }
        }

        private void getTutorial(final GroupViewHolder holder, final String url) {
            RecyclerView view = holder.get(R.id.recycler_view);
            if (view == null) {
                return;
            }
            Object object = view.getTag();
            if (object instanceof EasyRecyclerView) {
                ((EasyRecyclerView) object).notifyDataSetChanged();
            } else {
                final EasyRecyclerView<ArticleInfo> recyclerView = new EasyRecyclerView<>(view);
                view.setTag(recyclerView);
                final List<ArticleInfo> articleInfoList = new ArrayList<>();
                recyclerView.setData(articleInfoList)
                        .setItemRes(R.layout.item_tutorial)
                        .setLayoutManager(new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false))
                        .onBindViewHolder((holder1, list, position, payloads) -> {
                            ArticleInfo info = list.get(position);
                            Log.d("onBindViewHolder", "position=" + position + " ArticleInfo=" + info);
                            Glide.with(context).load(info.getImage()).into(holder1.getImageView(R.id.iv_image));
                            holder1.getTextView(R.id.tv_title).setText(info.getTitle());
                        })
                        .onItemClick(new IEasy.OnItemClickListener<ArticleInfo>() {
                            @Override
                            public void onClick(EasyViewHolder holder, View view, ArticleInfo data, float x, float y) {
                                _mActivity.start(ArticleDetailFragment.newInstance("https://game.shouji.com.cn" + data.getUrl()));
                            }
                        })
                        .build();
                ExecutorHelper.submit(() -> {
                    try {
                        Document doc = HttpUtil.getDocument(url);
                        Elements elements = doc.selectFirst("ul.news_list").select("li");
                        articleInfoList.clear();
                        for (Element element : elements) {
                            articleInfoList.add(ArticleInfo.from(element));
                        }
                        post(() -> {
                            Log.d("getTutorial", "articleInfoList.size=" + articleInfoList.size() + "  recyclerView=" + recyclerView);
                            recyclerView.notifyDataSetChanged();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }

    }
}
