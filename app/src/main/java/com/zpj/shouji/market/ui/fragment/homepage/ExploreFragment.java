//package com.zpj.shouji.market.ui.fragment.homepage;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.LinearLayoutManager;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.View;
//
//import com.felix.atoast.library.AToast;
//import com.zpj.http.parser.html.nodes.Document;
//import com.zpj.http.parser.html.nodes.Element;
//import com.zpj.http.parser.html.select.Elements;
//import com.zpj.recyclerview.EasyAdapter;
//import com.zpj.recyclerview.EasyRecyclerLayout;
//import com.zpj.recyclerview.EasyViewHolder;
//import com.zpj.recyclerview.IEasy;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.model.ExploreItem;
//import com.zpj.shouji.market.ui.adapter.ExploreBinder;
//import com.zpj.shouji.market.ui.fragment.search.SearchResultFragment;
//import com.zpj.shouji.market.ui.widget.BottomListPopupMenu;
//import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
//import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
//import com.zpj.shouji.market.utils.ExecutorHelper;
//import com.zpj.shouji.market.utils.HttpUtil;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ExploreFragment extends BaseFragment
//        implements IEasy.OnLoadMoreListener,
//        SwipeRefreshLayout.OnRefreshListener, SearchResultFragment.KeywordObserver {
//
//    public interface Callback {
//        void onGetUserItem(Element element);
//    }
//
//    private static final String DEFAULT_URL = "http://tt.shouji.com.cn/app/faxian.jsp?index=faxian&versioncode=198";
//
//    private EasyRecyclerLayout<ExploreItem> recyclerLayout;
//
//    private final List<ExploreItem> exploreItems = new ArrayList<>();
//    private boolean enableSwipeRefresh = true;
//
//    private String defaultUrl = DEFAULT_URL;
//    private String nextUrl;
//
//    private Callback callback;
//
//
//    private final Runnable getDataRunnable = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                Log.d("getExploreData", "nextUrl=" + nextUrl);
//                Document doc = HttpUtil.getDocument(nextUrl);
//                Elements elements = doc.select("item");
//                if (nextUrl.equals(defaultUrl)) {
//                    Element userElement = elements.get(0);
//                    if (callback != null) {
//                        callback.onGetUserItem(userElement);
//                    }
//                }
//
//                nextUrl = doc.selectFirst("nextUrl").text();
//                Map<String, ExploreItem> map = new HashMap<>();
//                for (int i = 0; i < elements.size(); i++) {
//                    Element item = elements.get(i);
//
//                    String type = item.selectFirst("type").text();
//                    if ("tag".equals(type)) {
//                        continue;
//                    }
//
//                    ExploreItem exploreItem = new ExploreItem();
//                    String id = item.selectFirst("id").text();
//                    String parent = item.selectFirst("parent").text();
//                    if (id == null && parent == null) {
//                        continue;
//                    }
//                    Log.d("getExploreData", "id=" + id);
//                    Log.d("getExploreData", "parent=" + parent);
//                    exploreItem.setId(id);
//                    exploreItem.setParent(parent);
//                    exploreItem.setMemberId(item.selectFirst("memberid").text());
//
//                    exploreItem.setIcon(item.selectFirst("icon").text());
//                    exploreItem.setIconState(item.selectFirst("iconstate").text());
//                    exploreItem.setNickName(item.selectFirst("nickname").text());
//                    exploreItem.setTime(item.selectFirst("time").text());
//                    exploreItem.setContent(item.selectFirst("content").text());
//                    exploreItem.setPhone(item.selectFirst("phone").text());
//
//                    Elements pics = item.select("pics");
//                    Elements spics = item.select("spics");
//                    if (!pics.isEmpty()) {
//                        for (Element pic : item.selectFirst("pics").select("pic")) {
//                            exploreItem.addPic(pic.text());
////                                exploreItem.addSpic(pic.text().replace(".png", "_s.jpg"));
//                        }
//                    }
//                    if (!spics.isEmpty()) {
//                        for (Element spic : item.selectFirst("spics").select("spic")) {
//                            exploreItem.addSpic(spic.text());
//                        }
//                    }
//
//
//                    if ("theme".equals(type)) {
//
//                        // 分享应用
//                        Elements appNames = item.select("appname");
//                        if (!appNames.isEmpty()) {
//                            exploreItem.setAppName(appNames.get(0).text());
//                            exploreItem.setAppPackageName(item.selectFirst("apppackagename").text());
//                            exploreItem.setAppIcon(item.selectFirst("appicon").text());
//                            exploreItem.setApkExist("1".equals(item.selectFirst("isApkExist").text()));
//                            exploreItem.setAppUrl(item.selectFirst("appurl").text());
//                            exploreItem.setApkUrl(item.selectFirst("apkurl").text());
//                            exploreItem.setAppSize(item.selectFirst("apksize").text());
//                        }
//
//                        // 分享应用集
//                        for (Element sharePic : item.select("sharepics").select("sharepic")) {
//                            exploreItem.addSharePic(sharePic.text());
//                        }
//                        for (Element sharePn : item.select("sharepics").select("sharepn")) {
//                            exploreItem.addSharePn(sharePn.text());
//                        }
//
//                    } else if ("reply".equals(type)) {
//                        String toNickName = item.selectFirst("tonickname").text();
//                        exploreItem.setToNickName(toNickName);
//                    }
//
//                    Elements supportCountElements = item.select("supportcount");
//                    exploreItem.setSupportCount(supportCountElements.isEmpty() ? "0" : supportCountElements.get(0).text());
//                    Elements replayCountElements = item.select("replycount");
//                    exploreItem.setReplyCount(replayCountElements.isEmpty() ? "0" : replayCountElements.get(0).text());
//                    if (parent.equals(id)) {
//                        map.put(id, exploreItem);
//                        exploreItems.add(exploreItem);
//                    } else {
//                        ExploreItem parentItem = map.get(parent);
//                        if (parentItem != null) {
//                            parentItem.addChild(exploreItem);
//                        }
//                    }
//                }
//                post(() -> recyclerLayout.notifyDataSetChanged());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    public static ExploreFragment newInstance(String url) {
//        return newInstance(url, true);
//    }
//
//    public static ExploreFragment newInstance(String url, boolean shouldLazyLoad) {
//        ExploreFragment fragment = new ExploreFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("default_url", url);
//        fragment.setArguments(bundle);
//        return fragment;
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_explore;
//    }
//
//    @Override
//    protected boolean supportSwipeBack() {
//        return false;
//    }
//
//    @Override
//    protected void initView(View view, @Nullable Bundle savedInstanceState) {
//        if (getArguments() != null) {
//            defaultUrl = getArguments().getString("default_url");
//        }
//        nextUrl = defaultUrl;
//        recyclerLayout = view.findViewById(R.id.recycler_layout);
//    }
//
//    @Override
//    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
//        super.onLazyInitView(savedInstanceState);
//        recyclerLayout.setItemRes(R.layout.item_explore)
//                .setData(exploreItems)
//                .setLayoutManager(new LinearLayoutManager(getContext()))
//                .setEnableLoadMore(true)
//                .setEnableSwipeRefresh(enableSwipeRefresh)
//                .onLoadMore(this)
//                .setOnRefreshListener(this)
//                .onBindViewHolder(new ExploreBinder())
//                .onItemClick(new IEasy.OnItemClickListener<ExploreItem>() {
//                    @Override
//                    public void onClick(EasyViewHolder holder, View view, ExploreItem data, float x, float y) {
//                        AToast.normal("TODO click");
//                    }
//                })
//                .onItemLongClick(new IEasy.OnItemLongClickListener<ExploreItem>() {
//                    @Override
//                    public boolean onLongClick(EasyViewHolder holder, View view, ExploreItem data, float x, float y) {
//                        showMenu(data);
//                        return true;
//                    }
//                })
//                .onViewClick(R.id.item_icon, (holder, view, data) -> {
//                    _mActivity.start(ProfileFragment.newInstance(data.getMemberId(), false));
//                })
//                .build();
//    }
//
//    @Override
//    public void updateKeyword(String keyword) {
//        defaultUrl = "http://tt.shouji.com.cn/app/faxian.jsp?versioncode=198&s=" + keyword;
//        nextUrl = defaultUrl;
//        onRefresh();
////        if (isHidden()) {
////            getSupportDelegate().getVisibleDelegate().onDestroyView();
////        } else {
////            onRefresh();
////        }
//    }
//
//    @Override
//    public void onRefresh() {
//        nextUrl = defaultUrl;
//        exploreItems.clear();
//        recyclerLayout.notifyDataSetChanged();
//    }
//
//    @Override
//    public boolean onLoadMore(EasyAdapter.Enabled enabled, int currentPage) {
//        if (TextUtils.isEmpty(nextUrl)) {
////                            enabled.setLoadFailed(false);
//            AToast.normal("没有更多了！");
//            return false;
//        }
//        // 获取数据
//        ExecutorHelper.submit(getDataRunnable);
//        return true;
//    }
//
//
//    public void setCallback(Callback callback) {
//        this.callback = callback;
//    }
//
//    public void setEnableSwipeRefresh(boolean enableSwipeRefresh) {
//        this.enableSwipeRefresh = enableSwipeRefresh;
//        if (recyclerLayout != null) {
//            AToast.normal("setEnableSwipeRefresh");
//            recyclerLayout.setEnableSwipeRefresh(enableSwipeRefresh);
//        }
//    }
//
//    private void showMenu(ExploreItem data) {
//        BottomListPopupMenu.with(context)
//                .setMenu(R.menu.menu_tools)
//                .onItemClick(new BottomListPopupMenu.OnItemClickListener() {
//                    @Override
//                    public void onClick(BottomListPopupMenu menu, View view, MenuItem data) {
//                        switch (data.getItemId()) {
//                            case R.id.copy:
//
//                                break;
//                            case R.id.share:
//
//                                break;
//                            case R.id.collect:
//
//                                break;
//                            case R.id.delete:
//
//                                break;
//                            case R.id.report:
//
//                                break;
//                            case R.id.black_list:
//                                break;
//                        }
//                        menu.dismiss();
//                    }
//                })
//                .show();
//    }
//
//}
