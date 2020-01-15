package com.zpj.shouji.market.ui.fragment.main.homepage;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.felix.atoast.library.AToast;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.popupmenuview.CustomPopupMenuView;
import com.zpj.popupmenuview.OptionMenuView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.bean.ExploreItem;
import com.zpj.shouji.market.ui.adapter.ExploreAdapter;
import com.zpj.shouji.market.ui.adapter.loadmore.LoadMoreAdapter;
import com.zpj.shouji.market.ui.adapter.loadmore.LoadMoreWrapper;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.fragment.main.MainFragment;
import com.zpj.shouji.market.ui.fragment.main.user.UserFragment;
import com.zpj.shouji.market.utils.ConnectUtil;
import com.zpj.shouji.market.utils.ExecutorHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExploreFragment extends BaseFragment implements ExploreAdapter.OnItemClickListener {

    public interface Callback {
        void onGetUserItem(Element element);
    }

    private static final String DEFAULT_URL = "http://tt.shouji.com.cn/app/faxian.jsp?index=faxian&versioncode=198";

    private RecyclerView recyclerView;
    private List<ExploreItem> exploreItems = new ArrayList<>();
    private ExploreAdapter exploreAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean enableSwipeRefresh = true;

    private String defaultUrl = DEFAULT_URL;
    private String nextUrl;

    private Callback callback;


    private final Runnable getDataRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d("getExploreData", "nextUrl=" + nextUrl);
                Document doc = ConnectUtil.getDocument(nextUrl);
                Elements elements = doc.select("item");
                if (nextUrl.equals(defaultUrl)) {
                    Element userElement = elements.get(0);
                    if (callback != null) {
                        callback.onGetUserItem(userElement);
                    }
                }

                nextUrl = doc.selectFirst("nextUrl").text();
                Map<String, ExploreItem> map = new HashMap<>();
                for (int i = 1; i < elements.size(); i++) {
                    Element item = elements.get(i);
                    ExploreItem exploreItem = new ExploreItem();
                    String id = item.selectFirst("id").text();
                    String parent = item.selectFirst("parent").text();
                    if (id == null && parent == null) {
                        continue;
                    }
                    Log.d("getExploreData", "id=" + id);
                    Log.d("getExploreData", "parent=" + parent);
                    exploreItem.setId(id);
                    exploreItem.setParent(parent);
                    exploreItem.setMemberId(item.selectFirst("memberid").text());

                    exploreItem.setIcon(item.selectFirst("icon").text());
                    exploreItem.setIconState(item.selectFirst("iconstate").text());
                    exploreItem.setNickName(item.selectFirst("nickname").text());
                    exploreItem.setTime(item.selectFirst("time").text());
                    exploreItem.setContent(item.selectFirst("content").text());
                    exploreItem.setPhone(item.selectFirst("phone").text());

                    Elements pics = item.select("pics");
                    Elements spics = item.select("spics");
                    if (!pics.isEmpty()) {
                        for (Element pic : item.selectFirst("pics").select("pic")) {
                            exploreItem.addPic(pic.text());
//                                exploreItem.addSpic(pic.text().replace(".png", "_s.jpg"));
                        }
                    }
                    if (!spics.isEmpty()) {
                        for (Element spic : item.selectFirst("spics").select("spic")) {
                            exploreItem.addSpic(spic.text());
                        }
                    }

                    String type = item.selectFirst("type").text();
                    if ("theme".equals(type)) {

                        // 分享应用
                        Elements appNames = item.select("appname");
                        if (!appNames.isEmpty()) {
                            exploreItem.setAppName(appNames.get(0).text());
                            exploreItem.setAppPackageName(item.selectFirst("apppackagename").text());
                            exploreItem.setAppIcon(item.selectFirst("appicon").text());
                            exploreItem.setApkExist("1".equals(item.selectFirst("isApkExist").text()));
                            exploreItem.setAppUrl(item.selectFirst("appurl").text());
                            exploreItem.setApkUrl(item.selectFirst("apkurl").text());
                            exploreItem.setAppSize(item.selectFirst("apksize").text());
                        }

                        // 分享应用集
                        for (Element sharePic : item.select("sharepics").select("sharepic")) {
                            exploreItem.addSharePic(sharePic.text());
                        }
                        for (Element sharePn : item.select("sharepics").select("sharepn")) {
                            exploreItem.addSharePn(sharePn.text());
                        }

                    } else if ("reply".equals(type)) {
                        String toNickName = item.selectFirst("tonickname").text();
                        exploreItem.setToNickName(toNickName);
                    }

                    Elements supportCountElements = item.select("supportcount");
                    exploreItem.setSupportCount(supportCountElements.isEmpty() ? "0" : supportCountElements.get(0).text());
                    Elements replayCountElements = item.select("replycount");
                    exploreItem.setReplyCount(replayCountElements.isEmpty() ? "0" : replayCountElements.get(0).text());
                    if (parent.equals(id)) {
                        map.put(id, exploreItem);
                        exploreItems.add(exploreItem);
                    } else {
                        ExploreItem parentItem = map.get(parent);
                        if (parentItem != null) {
                            parentItem.addChild(exploreItem);
                        }
                    }
                }
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                            loadingView.setVisibility(View.GONE);
                        exploreAdapter.notifyDataSetChanged();
                    }
                }, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static ExploreFragment newInstance(String url) {
        return newInstance(url, true);
    }

    public static ExploreFragment newInstance(String url, boolean shouldLazyLoad) {
        ExploreFragment fragment = new ExploreFragment();
        Bundle bundle = new Bundle();
        bundle.putString("default_url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_explore;
    }

    @Override
    protected boolean supportSwipeBack() {
        return false;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            defaultUrl = getArguments().getString("default_url");
        }
        nextUrl = defaultUrl;
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setEnabled(enableSwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        exploreItems.clear();
                        exploreAdapter.notifyDataSetChanged();
                        nextUrl = DEFAULT_URL;
//                        getCoolApkHtml();
                    }
                }, 1000);
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        exploreAdapter = new ExploreAdapter(exploreItems);
        exploreAdapter.setItemClickListener(this);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        loadData();
    }

    @Override
    public void onItemClick(ExploreAdapter.ViewHolder holder, int position, ExploreItem item) {
        AToast.normal("onItemClick");
    }

    @Override
    public void onMenuClicked(View view, ExploreItem item) {
        showMenu(view, item);
    }

    @Override
    public void onIconClicked(View view, String userId) {
        if (defaultUrl.equals(DEFAULT_URL)) {
            findFragment(MainFragment.class).start(UserFragment.newInstance(userId, false));
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setEnableSwipeRefresh(boolean enableSwipeRefresh) {
        this.enableSwipeRefresh = enableSwipeRefresh;
        if (swipeRefreshLayout != null) {
            AToast.normal("setEnableSwipeRefresh");
            swipeRefreshLayout.setEnabled(enableSwipeRefresh);
        }
    }

    public void loadData() {
        if (exploreAdapter == null) {
            return;
        }
        LoadMoreWrapper.with(exploreAdapter)
                .setLoadMoreEnabled(true)
                .setListener(new LoadMoreAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(LoadMoreAdapter.Enabled enabled) {
                        if (TextUtils.isEmpty(nextUrl)) {
//                            enabled.setLoadFailed(false);
                            AToast.normal("没有更多了！");
                            return;
                        }
                        // 获取数据
                        ExecutorHelper.submit(getDataRunnable);
                    }
                })
                .into(recyclerView);
    }

    private void showMenu(View view, ExploreItem item) {
        CustomPopupMenuView.with(getContext(), R.layout.layout_menu)
                .setOrientation(LinearLayout.VERTICAL)
                .setBackgroundAlpha(getActivity(), 0.9f, 500)
                .setPopupViewBackgroundColor(Color.parseColor("#eeffffff"))
//                .setAnimationTranslationShow(CustomPopupMenuView.DIRECTION_X, 350, 100, 0)
//                .setAnimationTranslationShow(CustomPopupMenuView.DIRECTION_Y, 350, -100, 0)
//                .setAnimationAlphaShow(350, 0.0f, 1.0f)
//                .setAnimationAlphaDismiss(350, 1.0f, 0.0f)
                .initViews(
                        1,
                        (popupMenuView, itemView, position) -> {
                            FrameLayout toolsFrameLayout = itemView.findViewById(R.id.tools);
                            OptionMenuView tools = new OptionMenuView(getContext());
                            tools.setOrientation(LinearLayout.VERTICAL);
                            tools.inflate(R.menu.menu_tools, new MenuBuilder(itemView.getContext()));
                            tools.setOnOptionMenuClickListener((index, menu) -> {
                                Toast.makeText(getContext(), menu.getTitle(), Toast.LENGTH_SHORT).show();
                                popupMenuView.dismiss();
                                switch (index) {
                                    case 0:

                                        break;
                                    case 1:

                                        break;
                                    case 2:

                                        break;
                                    case 3:

                                        break;
                                    case 4:

                                        break;
                                    case 5:

                                        break;
                                    case 6:

                                        break;
                                    case 7:

                                        break;
                                    default:
                                        break;
                                }
                                return true;
                            });
                            toolsFrameLayout.addView(tools);
                        })
                .show(view);
    }
}
