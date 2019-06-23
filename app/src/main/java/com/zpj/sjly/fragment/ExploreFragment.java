package com.zpj.sjly.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mingle.widget.LoadingView;
import com.zpj.sjly.R;
import com.zpj.sjly.adapter.ExploreAdapter;
import com.zpj.sjly.model.ExploreItem;
import com.zpj.sjly.utils.ConnectUtil;
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

    private Handler handler;
    private View view;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private LoadingView loadingView;
    private List<ExploreItem> exploreItems = new ArrayList<>();
    private ExploreAdapter exploreAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String nextUrl = DEFAULT_LIST_URL;

    private boolean isInit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.explore_fragment, null);
        initView(view);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    loadingView.setVisibility(View.GONE);
                    exploreAdapter.notifyDataSetChanged();
                }
            }
        };
        isInit = true;
        return view;
    }


    @Override
    public void lazyLoadData() {
        if (isInit && isVisible) {
            Toast.makeText(getContext(), "lazyLoadData", Toast.LENGTH_SHORT).show();
            LoadMoreWrapper.with(exploreAdapter)
                    .setLoadMoreEnabled(true)
                    .setListener(new LoadMoreAdapter.OnLoadMoreListener() {
                        @Override
                        public void onLoadMore(LoadMoreAdapter.Enabled enabled) {
                            recyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getExploreData();
                                }
                            }, 1);
                        }
                    })
                    .into(recyclerView);
            isInit = false;
        }

    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        loadingView = view.findViewById(R.id.load_view);
        loadingView.setVisibility(View.VISIBLE);
        loadingView.setLoadingText("正在加载，请稍后……");

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
        });
    }

    private void getExploreData() {
        new Thread() {
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
                            Elements appNames = item.select("appname");
                            if (!appNames.isEmpty()) {
                                exploreItem.setAppName(appNames.get(0).text());
                                exploreItem.setAppPackageName(item.select("apppackagename").get(0).text());
                                exploreItem.setAppIcon(item.select("appicon").get(0).text());
                                exploreItem.setApkExist("1".equals(item.select("isApkExist").get(0).text()));
                                exploreItem.setAppUrl(item.select("appurl").get(0).text());
                                exploreItem.setApkUrl(item.select("apkurl").get(0).text());
                                exploreItem.setAppSize(item.select("apksize").get(0).text());
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
                            loadingView.setVisibility(View.GONE);
                            exploreAdapter.notifyDataSetChanged();
                        }
                    }, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }.start();
    }

}
