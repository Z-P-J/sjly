package com.zpj.market.ui.fragment.main.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zpj.market.R;
import com.zpj.market.bean.UserDownloadedAppInfo;
import com.zpj.market.ui.adapter.UserDownloadedAdapter;
import com.zpj.market.ui.adapter.loadmore.LoadMoreAdapter;
import com.zpj.market.ui.adapter.loadmore.LoadMoreWrapper;
import com.zpj.market.ui.fragment.AppDetailFragment;
import com.zpj.market.ui.fragment.base.BaseFragment;
import com.zpj.market.utils.ConnectUtil;
import com.zpj.market.utils.ExecutorHelper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SupportActivity;

public class UserDownloadedFragment extends BaseFragment {

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

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserDownloadedAdapter(appInfoList);
        adapter.setItemClickListener(new UserDownloadedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserDownloadedAdapter.ViewHolder holder, int position, UserDownloadedAppInfo item) {
                if (getActivity() instanceof SupportActivity) {
                    ((SupportActivity) getActivity()).start(AppDetailFragment.newInstance(item));
                }
            }
        });
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
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
}
