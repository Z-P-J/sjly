package com.zpj.shouji.market.ui.fragment.theme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.adapter.DiscoverBinder;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchResultFragment;
import com.zpj.shouji.market.utils.HttpApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThemeListFragment extends NextUrlFragment<DiscoverInfo>
        implements Runnable,
        IEasy.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        SearchResultFragment.KeywordObserver {

    public interface Callback {
        void onGetUserItem(Element element);
    }

    private DiscoverBinder binder;

    private boolean enableSwipeRefresh = true;


    private Callback callback;

    public static ThemeListFragment newInstance(String url) {
        return newInstance(url, true);
    }

    public static ThemeListFragment newInstance(String url, boolean shouldLazyLoad) {
        ThemeListFragment fragment = new ThemeListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEFAULT_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = new DiscoverBinder(showComment());
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_theme;
    }

    @Override
    public void run() {
        Log.d("getExploreData", "nextUrl=" + nextUrl);
        HttpApi.connect(nextUrl)
                .onSuccess(doc -> {
                    Elements elements = doc.select("item");
                    if (nextUrl.equals(defaultUrl)) {
                        Element userElement = elements.get(0);
                        if (callback != null) {
                            callback.onGetUserItem(userElement);
                        }
                    }

                    nextUrl = doc.selectFirst("nextUrl").text();
                    Map<String, DiscoverInfo> map = new HashMap<>();
                    for (Element element : elements) {
                        DiscoverInfo info = createData(element);
                        if (info == null) {
                            continue;
                        }
                        String parent = info.getParent();
                        String id = info.getId();
                        if (parent.equals(id)) {
                            map.put(id, info);
                            data.add(info);
                        } else {
                            DiscoverInfo parentItem = map.get(parent);
                            if (parentItem != null) {
                                parentItem.addChild(info);
                            }
                        }
                    }
                    recyclerLayout.notifyDataSetChanged();
                    if (data.isEmpty()) {
                        recyclerLayout.showEmpty();
                    }
                })
                .subscribe();
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<DiscoverInfo> list, int position, List<Object> payloads) {
        binder.onBindViewHolder(holder, list, position, payloads);
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<DiscoverInfo> recyclerLayout) {
        recyclerLayout.setEnableSwipeRefresh(enableSwipeRefresh)
                .onViewClick(R.id.item_icon, (holder, view, data) -> {
                    _mActivity.start(ProfileFragment.newInstance(data.getMemberId(), false));
                });
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, DiscoverInfo data) {
        AToast.normal("TODO click");
        _mActivity.start(ThemeDetailFragment.newInstance(data));
    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, DiscoverInfo data) {
        DiscoverBinder.showMenu(context, data);;
        return true;
    }

    @Override
    public void updateKeyword(String keyword) {
        defaultUrl = "http://tt.shouji.com.cn/app/faxian.jsp?versioncode=198&s=" + keyword;
        nextUrl = defaultUrl;
        onRefresh();
    }

    @Override
    protected void getData() {
//        ExecutorHelper.submit(this);
        run();
    }

    @Override
    public DiscoverInfo createData(Element element) {
        return DiscoverInfo.from(element);
    }

    public boolean showComment() {
        return true;
    }


    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setEnableSwipeRefresh(boolean enableSwipeRefresh) {
        this.enableSwipeRefresh = enableSwipeRefresh;
        if (recyclerLayout != null) {
            AToast.normal("setEnableSwipeRefresh");
            recyclerLayout.setEnableSwipeRefresh(enableSwipeRefresh);
        }
    }

}
