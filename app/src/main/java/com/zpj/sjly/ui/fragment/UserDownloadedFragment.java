package com.zpj.sjly.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpj.sjly.R;
import com.zpj.sjly.bean.UserDownloadedAppInfo;
import com.zpj.sjly.ui.adapter.UserDownloadedAdapter;
import com.zpj.sjly.ui.adapter.loadmore.LoadMoreAdapter;
import com.zpj.sjly.ui.adapter.loadmore.LoadMoreWrapper;
import com.zpj.sjly.ui.fragment.base.LazyLoadFragment;
import com.zpj.sjly.utils.ConnectUtil;
import com.zpj.sjly.utils.ExecutorHelper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class UserDownloadedFragment extends LazyLoadFragment {

    private static final String DEFAULT_URL = "http://tt.shouji.com.cn/app/view_member_down_xml_v2.jsp?versioncode=187&id=5636865";

    private RecyclerView recyclerView;
    private UserDownloadedAdapter adapter;
    private final List<UserDownloadedAppInfo> appInfoList = new ArrayList<>();
    private String nextUrl = DEFAULT_URL;

    private Runnable getDataRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Document doc = ConnectUtil.getDocument(nextUrl);
                nextUrl = doc.select("nextUrl").get(0).text();
                Elements items = doc.select("item");
                for (Element item : items) {
                    UserDownloadedAppInfo appInfo = new UserDownloadedAppInfo();
                    appInfo.setId(item.select("id").get(0).text());
                    appInfo.setTitle(item.select("title").get(0).text());
                    appInfo.setDownId(item.select("downid").get(0).text());
                    appInfo.setAppType(item.select("apptype").get(0).text());
                    appInfo.setPackageName(item.select("package").get(0).text());
                    appInfo.setAppSize(item.select("m").get(0).text());
                    appInfo.setDownloadTime(item.select("r").get(0).text());
                    appInfoList.add(appInfo);
                }
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Nullable
    @Override
    protected View onBuildView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, null, false);
        initView(view);
        return view;
    }

    @Override
    protected void lazyLoadData() {
        LoadMoreWrapper.with(adapter)
                .setLoadMoreEnabled(true)
                .setListener(new LoadMoreAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(LoadMoreAdapter.Enabled enabled) {
                        ExecutorHelper.submit(getDataRunnable);
                    }
                })
                .into(recyclerView);
    }

    @Override
    public boolean handleBackPressed() {
        return false;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserDownloadedAdapter(appInfoList);
    }

}
