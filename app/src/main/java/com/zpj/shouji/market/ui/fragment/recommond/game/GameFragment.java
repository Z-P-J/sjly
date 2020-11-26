//package com.zpj.shouji.market.ui.fragment.recommond.game;
//
//import android.content.Context;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.View;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
//import com.bumptech.glide.request.target.SimpleTarget;
//import com.bumptech.glide.request.transition.Transition;
//import com.sunfusheng.GroupRecyclerViewAdapter;
//import com.sunfusheng.GroupViewHolder;
//import com.sunfusheng.HeaderGroupRecyclerViewAdapter;
//import com.zpj.http.parser.html.nodes.Element;
//import com.zpj.http.parser.html.select.Elements;
//import com.zpj.recyclerview.EasyRecyclerView;
//import com.zpj.recyclerview.EasyViewHolder;
//import com.zpj.recyclerview.IEasy;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.glide.transformation.blur.BlurTransformation;
//import com.zpj.shouji.market.model.AppInfo;
//import com.zpj.shouji.market.model.GroupItem;
//import com.zpj.shouji.market.model.article.ArticleInfo;
//import com.zpj.shouji.market.model.CollectionInfo;
//import com.zpj.shouji.market.ui.fragment.ArticleDetailFragment;
//import com.zpj.fragmentation.BaseFragment;
//import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
//import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
//import com.zpj.shouji.market.api.HttpApi;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//public class GameFragment extends BaseFragment
//        implements GroupRecyclerViewAdapter.OnItemClickListener<GroupItem> {
//
//    private static final String TAG = "GameFragment";
//
//    private final List<List<GroupItem>> dataList = new ArrayList<>();
//
//    private Adapter adapter;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_app_recomment;
//    }
//
//    @Override
//    protected void initView(View view, @Nullable Bundle savedInstanceState) {
//        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_recent_update);
//        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
//        swipeRefreshLayout.setOnRefreshListener(() -> recyclerView.postDelayed(() -> {
//            swipeRefreshLayout.setRefreshing(false);
//            initData();
//        }, 1000));
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        adapter = new Adapter(context, dataList);
//        adapter.setOnItemClickListener(this);
//        recyclerView.setAdapter(adapter);
//    }
//
//    @Override
//    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
//        super.onLazyInitView(savedInstanceState);
//        initData();
//    }
//
//    @Override
//    public void onSupportInvisible() {
//        lightStatusBar();
//    }
//
//    @Override
//    public void onSupportVisible() {
//        darkStatusBar();
//    }
//
//    @Override
//    public void onItemClick(GroupRecyclerViewAdapter<GroupItem> adapter, GroupViewHolder holder, GroupItem data, int groupPosition, int childPosition) {
//        if (groupPosition == 0) {
//            return;
//        }
//    }
//
//    private void initData() {
//        dataList.clear();
//
//        List<GroupItem> topList = new ArrayList<>();
//        topList.add(new GroupItem());
//        dataList.add(topList);
//
//        List<GroupItem> list1 = new ArrayList<>();
//        list1.add(new GroupItem("最近更新"));
//        list1.add(new GroupItem());
//        dataList.add(list1);
//
//        List<GroupItem> list2 = new ArrayList<>();
//        list2.add(new GroupItem("游戏推荐"));
//        list2.add(new GroupItem());
//        dataList.add(list2);
//
//        List<GroupItem> list3 = new ArrayList<>();
//        list3.add(new GroupItem("热门网游"));
//        list3.add(new GroupItem());
//        dataList.add(list3);
//
//        List<GroupItem> list4 = new ArrayList<>();
//        list4.add(new GroupItem("游戏快递"));
//        list4.add(new GroupItem());
//        dataList.add(list4);
//
//        List<GroupItem> list5 = new ArrayList<>();
//        list5.add(new GroupItem("游戏评测"));
//        list5.add(new GroupItem());
//        dataList.add(list5);
//
//        List<GroupItem> list6 = new ArrayList<>();
//        list6.add(new GroupItem("游戏攻略"));
//        list6.add(new GroupItem());
//        dataList.add(list6);
//
//        List<GroupItem> list7 = new ArrayList<>();
//        list7.add(new GroupItem("游戏新闻"));
//        list7.add(new GroupItem());
//        dataList.add(list7);
//
//        List<GroupItem> list8 = new ArrayList<>();
//        list8.add(new GroupItem("游戏周刊"));
//        list8.add(new GroupItem());
//        dataList.add(list8);
//
//        List<GroupItem> list9 = new ArrayList<>();
//        list9.add(new GroupItem("游戏公告"));
//        list9.add(new GroupItem());
//        dataList.add(list9);
//
//        adapter.notifyDataSetChanged();
//    }
//
//    private class Adapter extends HeaderGroupRecyclerViewAdapter<GroupItem> {
//
//        private final int[] RES_ICONS = {R.id.item_icon_1, R.id.item_icon_2, R.id.item_icon_3};
//
//        static final int TYPE_TOP_HEADER = 11;
//        static final int TYPE_SUB_HEADER = 22;
//        static final int TYPE_CHILD_UPDATE = 331;
//        static final int TYPE_CHILD_COLLECTION = 332;
//        static final int TYPE_CHILD_RECOMMEND = 333;
//        static final int TYPE_CHILD_HOT = 334;
//
//        private static final int TYPE_CHILD_EXPRESS = 30;
//        private static final int TYPE_CHILD_PROFILE = 31;
//        private static final int TYPE_CHILD_GUIDE = 32;
//        private static final int TYPE_CHILD_NEWS = 33;
//        private static final int TYPE_CHILD_ARTICLE = 34;
//        private static final int TYPE_CHILD_WEEKLY = 35;
//
//        Adapter(Context context, List<List<GroupItem>> groups) {
//            super(context, groups);
//        }
//
//        @Override
//        public boolean showHeader() {
//            return true;
//        }
//
//        @Override
//        public boolean showFooter() {
//            return false;
//        }
//
//        @Override
//        public int getHeaderItemViewType(int groupPosition) {
//            if (groupPosition == 0) {
//                return TYPE_TOP_HEADER;
//            }
//            return TYPE_SUB_HEADER;
//        }
//
//        @Override
//        public int getChildItemViewType(int groupPosition, int childPosition) {
//            if (groupPosition == 1) {
//                return TYPE_CHILD_UPDATE;
//            } else if (groupPosition == 2) {
//                return TYPE_CHILD_RECOMMEND;
//            } else if (groupPosition == 3) {
//                return TYPE_CHILD_HOT;
//            } else if (groupPosition == 4) {
//                return TYPE_CHILD_EXPRESS;
//            } else if (groupPosition == 5) {
//                return TYPE_CHILD_PROFILE;
//            } else if (groupPosition == 6) {
//                return TYPE_CHILD_GUIDE;
//            } else if (groupPosition == 7) {
//                return TYPE_CHILD_NEWS;
//            } else if (groupPosition == 8) {
//                return TYPE_CHILD_ARTICLE;
//            } else if (groupPosition == 9) {
//                return TYPE_CHILD_WEEKLY;
//            }
//            return super.getChildItemViewType(groupPosition, childPosition);
//        }
//
//        @Override
//        public int getHeaderLayoutId(int viewType) {
//            if (viewType == TYPE_TOP_HEADER) {
//                return R.layout.layout_app_header;
//            } else {
//                return R.layout.item_recommend_header;
//            }
//        }
//
//        @Override
//        public int getChildLayoutId(int viewType) {
//            return R.layout.layout_recycler_card_view;
//        }
//
//        @Override
//        public int getFooterLayoutId(int viewType) {
//            return 0;
//        }
//
//        @Override
//        public void onBindHeaderViewHolder(GroupViewHolder holder, GroupItem item, int groupPosition) {
//            int viewType = getHeaderItemViewType(groupPosition);
//            if (viewType == TYPE_TOP_HEADER) {
//                holder.getString(R.id.cv_item1).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
//                holder.getString(R.id.cv_item2).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
//            } else if (viewType == TYPE_SUB_HEADER) {
//                holder.setText(R.id.tv_title, item.getTitle());
//            }
//        }
//
//        @Override
//        public void onBindChildViewHolder(GroupViewHolder holder, GroupItem item, int groupPosition, int childPosition) {
//            RecyclerView view = holder.getString(R.id.recycler_view);
//            if (view == null) {
//                return;
//            }
//            Object object = view.getTag();
//            if (object instanceof EasyRecyclerView) {
//                ((EasyRecyclerView) object).notifyDataSetChanged();
//                return;
//            }
//            int viewType = getChildItemViewType(groupPosition, childPosition);
//            if (viewType == TYPE_CHILD_UPDATE) {
//                getAppInfo(view, "http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sort=time&versioncode=198");
//            } else if (viewType == TYPE_CHILD_RECOMMEND) {
//                getAppInfo(view, "http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sdk=100&sort=day");
//            } else if (viewType == TYPE_CHILD_HOT) {
//                getAppInfo(view, "http://tt.shouji.com.cn/androidv3/netgame.jsp");
//            } else if (viewType == TYPE_CHILD_COLLECTION) {
//                getCollection(view);
//            } else if (viewType == TYPE_CHILD_EXPRESS
//                    || viewType == TYPE_CHILD_PROFILE
//                    || viewType == TYPE_CHILD_GUIDE
//                    || viewType == TYPE_CHILD_NEWS
//                    || viewType == TYPE_CHILD_ARTICLE
//                    || viewType == TYPE_CHILD_WEEKLY) {
//                getTutorial(holder, String.format(Locale.CHINA, "https://game.shouji.com.cn/newslist/list_%d_1.html", groupPosition - 3));
//            }
//        }
//
//        @Override
//        public void onBindFooterViewHolder(GroupViewHolder holder, GroupItem item, int groupPosition) {
//
//        }
//
//        private void getAppInfo(RecyclerView view, final String url) {
//            EasyRecyclerView<AppInfo> recyclerView = new EasyRecyclerView<>(view);
//            view.setTag(recyclerView);
//            List<AppInfo> list = new ArrayList<>();
//            recyclerView.setData(list)
//                    .setItemRes(R.layout.item_app_grid)
//                    .setLayoutManager(new GridLayoutManager(context, 4))
//                    .onBindViewHolder((holder1, list1, position, payloads) -> {
//                        AppInfo info = list1.getString(position);
//                        holder1.getTextView(R.id.item_title).setText(info.getAppTitle());
//                        holder1.getTextView(R.id.item_info).setText(info.getAppSize());
//                        Glide.with(context).load(info.getAppIcon()).into(holder1.getImageView(R.id.item_icon));
//                    })
//                    .onItemClick((holder13, view1, data) -> _mActivity.start(AppDetailFragment.newInstance(data)))
//                    .build();
//            HttpApi.connect(url)
//                    .onSuccess(data -> {
//                        Elements elements = data.select("item");
//                        for (Element element : elements) {
//                            AppInfo info = AppInfo.parse(element);
//                            if (info == null) {
//                                continue;
//                            }
//                            list.add(info);
//                            if (list.size() == 8) {
//                                break;
//                            }
//                        }
//                        recyclerView.notifyDataSetChanged();
//                    })
//                    .subscribe();
//        }
//
//        private void getCollection(final RecyclerView view) {
//            EasyRecyclerView<CollectionInfo> recyclerView = new EasyRecyclerView<>(view);
//            view.setTag(recyclerView);
//            List<CollectionInfo> list = new ArrayList<>();
//            recyclerView.setData(list)
//                    .setItemRes(R.layout.item_app_collection)
//                    .setLayoutManager(new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false))
//                    .onBindViewHolder((holder1, list1, position, payloads) -> {
//                        CollectionInfo info = list1.getString(position);
//                        holder1.getTextView(R.id.item_title).setText(info.getTitle());
//                        holder1.setText(R.id.tv_view_count, info.getViewCount() + "");
//                        holder1.setText(R.id.tv_favorite_count, info.getFavCount() + "");
//                        holder1.setText(R.id.tv_support_count, info.getSupportCount() + "");
//                        for (int i = 0; i < RES_ICONS.length; i++) {
//                            int res = RES_ICONS[i];
//                            if (i == 0) {
//                                Glide.with(context)
//                                        .load(info.getIcons().getString(0))
//                                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(context, 7)))
//                                        .into(new SimpleTarget<Drawable>() {
//                                            @Override
//                                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                                                holder1.getView(R.id.img_bg).setBackground(resource);
//                                            }
//                                        });
//                            }
//                            Glide.with(context).load(info.getIcons().getString(i)).into(holder1.getImageView(res));
//                        }
//                    })
//                    .onItemClick((holder14, view12, data) -> _mActivity.start(CollectionDetailFragment.newInstance(data)))
//                    .build();
//            HttpApi.connect("http://tt.shouji.com.cn/androidv3/yyj_tj_xml.jsp")
//                    .onSuccess(data -> {
//                        Elements elements = data.select("item");
//                        for (Element element : elements) {
//                            list.add(CollectionInfo.create(element));
//                        }
//                        recyclerView.notifyDataSetChanged();
//                    })
//                    .subscribe();
//        }
//
//        private void getTutorial(final GroupViewHolder holder, final String url) {
//            RecyclerView view = holder.getString(R.id.recycler_view);
//            if (view == null) {
//                return;
//            }
//            Object object = view.getTag();
//            if (object instanceof EasyRecyclerView) {
//                ((EasyRecyclerView) object).notifyDataSetChanged();
//            } else {
//                final EasyRecyclerView<ArticleInfo> recyclerView = new EasyRecyclerView<>(view);
//                view.setTag(recyclerView);
//                final List<ArticleInfo> articleInfoList = new ArrayList<>();
//                recyclerView.setData(articleInfoList)
//                        .setItemRes(R.layout.item_tutorial)
//                        .setLayoutManager(new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false))
//                        .onBindViewHolder((holder1, list, position, payloads) -> {
//                            ArticleInfo info = list.getString(position);
//                            Log.d("onBindViewHolder", "position=" + position + " ArticleInfo=" + info);
//                            Glide.with(context).load(info.getImage()).into(holder1.getImageView(R.id.iv_image));
//                            holder1.getTextView(R.id.tv_title).setText(info.getTitle());
//                        })
//                        .onItemClick(new IEasy.OnItemClickListener<ArticleInfo>() {
//                            @Override
//                            public void onClick(EasyViewHolder holder, View view, ArticleInfo data) {
//                                _mActivity.start(ArticleDetailFragment.newInstance("https://game.shouji.com.cn" + data.getUrl()));
//                            }
//                        })
//                        .build();
//                HttpApi.connect(url)
//                        .onSuccess(data -> {
//                            Elements elements = data.selectFirst("ul.news_list").select("li");
//                            articleInfoList.clear();
//                            for (Element element : elements) {
//                                articleInfoList.add(ArticleInfo.from(element));
//                            }
//                            Log.d("getTutorial", "articleInfoList.size=" + articleInfoList.size() + "  recyclerView=" + recyclerView);
//                            recyclerView.notifyDataSetChanged();
//                        })
//                        .subscribe();
//            }
//        }
//
//    }
//}
