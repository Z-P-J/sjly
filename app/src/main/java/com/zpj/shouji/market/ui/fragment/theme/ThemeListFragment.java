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
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.adapter.DiscoverBinder;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchResultFragment;
import com.zpj.shouji.market.api.HttpApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThemeListFragment extends NextUrlFragment<DiscoverInfo>
        implements Runnable,
        IEasy.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        SearchResultFragment.KeywordObserver {

//    public interface Callback {
//        void onGetUserItem(Element element);
//        void onError(Throwable throwable);
//    }

    private DiscoverBinder binder;

    private boolean enableSwipeRefresh = true;


//    private Callback callback;

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
        Log.d("ThemeListFragment", "nextUrl=" + nextUrl);
        HttpApi.get(nextUrl)
                .onSuccess(doc -> {
                    Elements elements = doc.select("item");
//                    if (nextUrl.equals(defaultUrl)) {
//                        Element userElement = elements.get(0);
//                        if (callback != null) {
//                            callback.onGetUserItem(userElement);
//                        }
//                    }

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
                .onError(throwable -> {
                    Log.d("ThemeListFragment", "showError");
                    recyclerLayout.showErrorView(throwable.getMessage());
//                    if (callback != null) {
//                        callback.onError(throwable);
//                    }
                })
                .subscribe();
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<DiscoverInfo> list, int position, List<Object> payloads) {
        binder.onBindViewHolder(holder, list, position, payloads);
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<DiscoverInfo> recyclerLayout) {
        IEasy.OnClickListener<DiscoverInfo> listener = new IEasy.OnClickListener<DiscoverInfo>() {
            @Override
            public void onClick(EasyViewHolder holder, View view, DiscoverInfo data) {
                ProfileFragment.start(data.getMemberId(), false);
            }
        };
        recyclerLayout.setEnableSwipeRefresh(enableSwipeRefresh)
                .onViewClick(R.id.item_icon, listener)
                .onViewClick(R.id.user_name, listener);
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, DiscoverInfo data) {
        AToast.normal("TODO click");
        if (data.getSpics().isEmpty() && !data.getSharePics().isEmpty()) {
            CollectionInfo info = new CollectionInfo();
            info.setId(data.getId());
            info.setTitle(data.getShareTitle());
            info.setComment(data.getContent());
            info.setNickName(data.getNickName());
//            info.setFavCount(0);
//            info.setSupportCount(0);
//            info.setViewCount(0);
//            for (String url : data.getSharePics()) {
//                info.addIcon(url);
//            }
            CollectionDetailFragment.start(info);
        } else {
            ThemeDetailFragment.start(data, false);
        }
    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, DiscoverInfo data) {
        DiscoverBinder.showMenu(context, data);
        ;
        return true;
    }

    @Override
    public void updateKeyword(String keyword) {
        defaultUrl = "http://tt.shouji.com.cn/app/faxian.jsp?s=" + keyword;
        nextUrl = defaultUrl;
        if (isLazyInit) {
            onRefresh();
        }
    }

    @Override
    protected void getData() {
//        ExecutorHelper.submit(this);
        Log.d("ThemeListFragment", "getData");
        run();
    }

    @Override
    public DiscoverInfo createData(Element element) {
        return DiscoverInfo.from(element);
    }

    public boolean showComment() {
        return true;
    }


//    public void setCallback(Callback callback) {
//        this.callback = callback;
//    }

    public void setEnableSwipeRefresh(boolean enableSwipeRefresh) {
        this.enableSwipeRefresh = enableSwipeRefresh;
        if (recyclerLayout != null) {
            AToast.normal("setEnableSwipeRefresh");
            recyclerLayout.setEnableSwipeRefresh(enableSwipeRefresh);
        }
    }

}
