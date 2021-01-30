package com.zpj.shouji.market.ui.fragment.theme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.adapter.DiscoverBinder;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
import com.zpj.shouji.market.ui.fragment.dialog.ThemeMoreDialogFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.toast.ZToast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThemeListFragment extends NextUrlFragment<DiscoverInfo>
        implements IEasy.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener {

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
        bundle.putString(Keys.DEFAULT_URL, url);
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
        return R.layout.item_theme2;
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
        if (data.getSpics().isEmpty() && !data.getSharePics().isEmpty()) {
            CollectionInfo info = new CollectionInfo();
            info.setId(data.getId());
            info.setTitle(data.getShareTitle());
            info.setComment(data.getContent());
            info.setNickName(data.getNickName());
            info.setMemberId(data.getMemberId());
            info.setContentType(data.getContentType());
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
        new ThemeMoreDialogFragment()
                .setDiscoverInfo(data)
                .show(context);
        return true;
    }

    @Override
    public void onGetDocument(Document doc) throws Exception {
        Log.d("ThemeListFragment", "data=" + doc);
        Elements elements = doc.select("item");
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
    }

    @Override
    public void onError(Throwable throwable) {
        Log.d("ThemeListFragment", "showError");
        super.onError(throwable);
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
            ZToast.normal("setEnableSwipeRefresh");
            recyclerLayout.setEnableSwipeRefresh(enableSwipeRefresh);
        }
    }

}
