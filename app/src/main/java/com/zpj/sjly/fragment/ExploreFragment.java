package com.zpj.sjly.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zpj.popupmenuview.CustomPopupMenuView;
import com.zpj.popupmenuview.OptionMenuView;
import com.zpj.sjly.R;
import com.zpj.sjly.adapter.ExploreAdapter;
import com.zpj.sjly.bean.ExploreItem;
import com.zpj.sjly.utils.ConnectUtil;
import com.zpj.sjly.utils.ExecutorHelper;
import com.zpj.sjly.view.recyclerview.LoadMoreAdapter;
import com.zpj.sjly.view.recyclerview.LoadMoreWrapper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExploreFragment extends BaseFragment {

    private static final String DEFAULT_LIST_URL = "http://tt.shouji.com.cn/app/faxian.jsp?index=faxian&versioncode=187";

    private View view;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
//    private LoadingView loadingView;
    private List<ExploreItem> exploreItems = new ArrayList<>();
    private ExploreAdapter exploreAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String nextUrl = DEFAULT_LIST_URL;

    private boolean isInit;


    private final Runnable getDataRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d("getExploreData", "nextUrl=" + nextUrl);
                Document doc = ConnectUtil.getDocument(nextUrl);

                nextUrl = doc.select("nextUrl").get(0).text();
                Elements elements = doc.select("item");
                Map<String, ExploreItem> map = new HashMap<>();
                for (int i = 1; i < elements.size(); i++) {
                    Element item = elements.get(i);
                    ExploreItem exploreItem = new ExploreItem();
                    String id = item.select("id").get(0).text();
                    String parent = item.select("parent").get(0).text();
                    exploreItem.setId(id);
                    exploreItem.setId(parent);

                    exploreItem.setIcon(item.select("icon").get(0).text());
                    exploreItem.setIconState(item.select("iconstate").get(0).text());
                    exploreItem.setNickName(item.select("nickname").get(0).text());
                    exploreItem.setTime(item.select("time").get(0).text());
                    exploreItem.setContent(item.select("content").get(0).text());
                    exploreItem.setPhone(item.select("phone").get(0).text());

                    Elements pics = item.select("pics");
                    Elements spics = item.select("spics");
                    if (!pics.isEmpty()) {
                        for (Element pic : item.select("pics").get(0).select("pic")) {
                            exploreItem.addPic(pic.text());
//                                exploreItem.addSpic(pic.text().replace(".png", "_s.jpg"));
                        }
                    }
                    if (!spics.isEmpty()) {
                        for (Element spic : item.select("spics").get(0).select("spic")) {
                            exploreItem.addSpic(spic.text());
                        }
                    }

                    String type = item.select("type").get(0).text();
                    if ("theme".equals(type)) {

                        // 分享应用
                        Elements appNames = item.select("appname");
                        if (!appNames.isEmpty()) {
                            exploreItem.setAppName(appNames.get(0).text());
                            exploreItem.setAppPackageName(item.select("apppackagename").get(0).text());
                            exploreItem.setAppIcon(item.select("appicon").get(0).text());
//                                Elements isApkExistElements = item.select("isApkExist");
//                                if (isApkExistElements.isEmpty()) {
//                                    exploreItem.setApkExist(false);
//                                } else {
//                                    exploreItem.setApkExist("1".equals(isApkExistElements.get(0).text()));
//                                }
                            exploreItem.setApkExist("1".equals(item.select("isApkExist").get(0).text()));

                            exploreItem.setAppUrl(item.select("appurl").get(0).text());
//                                Elements apkUrlElements = item.select("apkurl");
//                                exploreItem.setApkUrl(apkUrlElements.isEmpty() ? null : item.select("apkurl").get(0).text());
//                                Elements apkSizeElements = item.select("apksize");
//                                exploreItem.setAppSize(apkSizeElements.isEmpty() ? null : apkSizeElements.get(0).text());
                            exploreItem.setApkUrl(item.select("apkurl").get(0).text());
                            exploreItem.setAppSize(item.select("apksize").get(0).text());
                        }

                        // 分享应用集
                        for (Element sharePic : item.select("sharepics").select("sharepic")) {
                            exploreItem.addSharePic(sharePic.text());
                        }
                        for (Element sharePn : item.select("sharepics").select("sharepn")) {
                            exploreItem.addSharePn(sharePn.text());
                        }

                    } else if ("reply".equals(type)) {
                        String toNickName = item.select("tonickname").get(0).text();
                        exploreItem.setToNickName(toNickName);
//                            if (!TextUtils.isEmpty(toNickName)) {
//
//                            }
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
//                    Message msg = new Message();
//                    msg.what = 1;
//                    handler.sendMessage(msg);
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

    @Nullable
    @Override
    protected View onBuildView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.explore_fragment, null);
        initView(view);
        isInit = true;
        return view;
    }


    @Override
    protected void lazyLoadData() {
//        if (isInit && isVisible) {
//
//            isInit = false;
//        }
        Toast.makeText(getContext(), "lazyLoadData", Toast.LENGTH_SHORT).show();
        LoadMoreWrapper.with(exploreAdapter)
                .setLoadMoreEnabled(true)
                .setListener(new LoadMoreAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(LoadMoreAdapter.Enabled enabled) {
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 获取数据
                                ExecutorHelper.submit(getDataRunnable);
                            }
                        }, 1);
                    }
                })
                .into(recyclerView);
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

//        loadingView = view.findViewById(R.id.load_view);
//        loadingView.setVisibility(View.VISIBLE);
//        loadingView.setLoadingText("正在加载，请稍后……");

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        exploreItems.clear();
                        exploreAdapter.notifyDataSetChanged();
                        nextUrl = DEFAULT_LIST_URL;
//                        getCoolApkHtml();
                    }
                }, 1000);
            }
        });

        //lazyLoadData();


        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        exploreAdapter = new ExploreAdapter(exploreItems);
        exploreAdapter.setItemClickListener(new ExploreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ExploreAdapter.ViewHolder holder, int position, ExploreItem item) {
                Toast.makeText(getContext(), "onItemClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMenuClicked(View view, ExploreItem item) {
                showMenu(view, item);
            }
        });
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
