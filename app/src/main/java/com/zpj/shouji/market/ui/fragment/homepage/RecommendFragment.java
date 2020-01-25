package com.zpj.shouji.market.ui.fragment.homepage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.felix.atoast.library.AToast;
import com.sunfusheng.GroupRecyclerViewAdapter;
import com.sunfusheng.GroupViewHolder;
import com.sunfusheng.HeaderGroupRecyclerViewAdapter;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZViewHolder;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.model.SubjectInfo;
import com.zpj.shouji.market.glide.blur.BlurTransformation;
import com.zpj.shouji.market.ui.fragment.AppListFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.utils.HttpUtil;
import com.zpj.shouji.market.utils.ExecutorHelper;

import java.util.ArrayList;
import java.util.List;

public class RecommendFragment extends BaseFragment
        implements GroupRecyclerViewAdapter.OnItemClickListener<RecommendFragment.ItemWrapper> {

    private static final String TAG = "RecommendFragment";

    private final List<List<ItemWrapper>> dataList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;
    private MZBannerView<AppInfo> mMZBanner;

    private RecommendAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recomment;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_recent_update);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> recyclerView.postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
//            initData();
            refresh();
        }, 1000));

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
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
//        if (groupPosition == 0) {
//            return;
//        }
    }

    private void initData() {
        dataList.clear();

        List<ItemWrapper> list0 = new ArrayList<>();
        list0.add(new ItemWrapper());
        dataList.add(list0);

        List<ItemWrapper> list1 = new ArrayList<>();
        list1.add(new ItemWrapper("最近更新"));
        list1.add(new ItemWrapper());
        dataList.add(list1);

        List<ItemWrapper> list2 = new ArrayList<>();
        list2.add(new ItemWrapper("应用集推荐"));
        list2.add(new ItemWrapper());
        dataList.add(list2);

        List<ItemWrapper> list3 = new ArrayList<>();
        list3.add(new ItemWrapper("应用推荐"));
        list3.add(new ItemWrapper());
        dataList.add(list3);

        List<ItemWrapper> list4 = new ArrayList<>();
        list4.add(new ItemWrapper("游戏推荐"));
        list4.add(new ItemWrapper());
        dataList.add(list4);

        List<ItemWrapper> list5 = new ArrayList<>();
        list5.add(new ItemWrapper("专题推荐"));
        list5.add(new ItemWrapper());
        dataList.add(list5);

        adapter.notifyDataSetChanged();
    }

    public void refresh() {
        for (int i = 0; i < dataList.size(); i++) {
            adapter.updateGroup(i, dataList.get(i));
        }
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

    class ItemWrapper {

        private String title;

        ItemWrapper() {

        }

        ItemWrapper(String title) {
            this.title = title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    private class RecommendAdapter extends HeaderGroupRecyclerViewAdapter<ItemWrapper> {

        private final int[] RES_ICONS = {R.id.item_icon_1, R.id.item_icon_2, R.id.item_icon_3};

        private final List<AppInfo> bannerItemList = new ArrayList<>();
        private final BannerViewHolder bannerViewHolder = new BannerViewHolder();

        static final int TYPE_TOP_HEADER = 11;
        static final int TYPE_SUB_HEADER = 22;
        static final int TYPE_CHILD_UPDATE = 331;
        static final int TYPE_CHILD_COLLECTION = 332;
        static final int TYPE_CHILD_RECOMMEND_APP = 333;
        static final int TYPE_CHILD_RECOMMEND_GAME = 33;
        static final int TYPE_CHILD_SUBJECT = 334;

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
            } else if (groupPosition == 3) {
                return TYPE_CHILD_RECOMMEND_APP;
            } else if (groupPosition == 4) {
                return TYPE_CHILD_RECOMMEND_GAME;
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
            return R.layout.layout_recycler_view;
        }

        @Override
        public int getFooterLayoutId(int viewType) {
            return 0;
        }

        @Override
        public void onBindHeaderViewHolder(GroupViewHolder holder, ItemWrapper item, int groupPosition) {
            int viewType = getHeaderItemViewType(groupPosition);
            if (viewType == TYPE_TOP_HEADER) {
                if (mMZBanner == null) {
                    mMZBanner = holder.get(R.id.banner);
                    getBanners();
                } else {
                    mMZBanner.setPages(bannerItemList, () -> bannerViewHolder);
                    mMZBanner.start();
                }
            } else if (viewType == TYPE_SUB_HEADER) {
                holder.setText(R.id.tv_title, item.getTitle());
            }
        }

        @Override
        public void onBindChildViewHolder(GroupViewHolder holder, ItemWrapper item, int groupPosition, int childPosition) {
            int viewType = getChildItemViewType(groupPosition, childPosition);
            Log.d(TAG, "groupPosition=" + groupPosition + " childPosition=" + childPosition + " viewType=" + viewType);

            RecyclerView view = holder.get(R.id.recycler_view);
            if (view.getTag() instanceof EasyRecyclerView) {
                ((EasyRecyclerView) view.getTag()).notifyDataSetChanged();
                return;
            }
            if (viewType == TYPE_CHILD_UPDATE) {
                getAppInfo(view, "http://tt.shouji.com.cn/androidv3/app_list_xml.jsp?index=1&versioncode=198");
            } else if (viewType == TYPE_CHILD_RECOMMEND_APP) {
                getAppInfo(view, "http://tt.shouji.com.cn/androidv3/special_list_xml.jsp?id=-9998");
            } else if (viewType == TYPE_CHILD_RECOMMEND_GAME) {
                getAppInfo(view, "http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sdk=100&sort=day");
            } else if (viewType == TYPE_CHILD_COLLECTION) {
                getCollection(view);
            } else if (viewType == TYPE_CHILD_SUBJECT) {
                getSubjects(view);
            }
        }

        @Override
        public void onBindFooterViewHolder(GroupViewHolder holder, ItemWrapper item, int groupPosition) {

        }

        private void onError(Exception e) {
            post(() -> AToast.error("加载失败！" + e.getMessage()));
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

        private void getAppInfo(RecyclerView view, final String url) {
            EasyRecyclerView<AppInfo> recyclerView = new EasyRecyclerView<>(view);
            view.setTag(recyclerView);
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

        private void getCollection(final RecyclerView view) {
            EasyRecyclerView<CollectionInfo> recyclerView = new EasyRecyclerView<>(view);
            view.setTag(recyclerView);
            List<CollectionInfo> list = new ArrayList<>();
            recyclerView.setData(list)
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
            ExecutorHelper.submit(() -> {
                try {
                    Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/yyj_tj_xml.jsp");
                    Elements elements = doc.select("item");
                    for (Element element : elements) {
                        list.add(CollectionInfo.create(element));
                    }
                    post(recyclerView::notifyDataSetChanged);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        private void getSubjects(RecyclerView view) {
            EasyRecyclerView<SubjectInfo> recyclerView = new EasyRecyclerView<>(view);
            view.setTag(recyclerView);
            List<SubjectInfo> list = new ArrayList<>();
            recyclerView.setData(list)
                    .setItemRes(R.layout.item_app_subject)
                    .setLayoutManager(new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false))
                    .onBindViewHolder((holder12, list12, position, payloads) -> {
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
                    post(recyclerView::notifyDataSetChanged);
                } catch (Exception e) {
                    e.printStackTrace();
                    onError(e);
                }
            });
        }

    }

}
