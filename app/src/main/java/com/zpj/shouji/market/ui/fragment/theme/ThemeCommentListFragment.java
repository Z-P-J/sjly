package com.zpj.shouji.market.ui.fragment.theme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.RefreshEvent;
import com.zpj.shouji.market.model.DiscoverInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

public class ThemeCommentListFragment extends ThemeListFragment {

    private String rootId;
//    private String contentType;

    public static ThemeCommentListFragment newInstance(String id, String type) {
        ThemeCommentListFragment fragment = new ThemeCommentListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Keys.ID, id);
        bundle.putString(Keys.TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void handleArguments(Bundle arguments) {
        rootId = arguments.getString(Keys.ID, "");
        String contentType = arguments.getString(Keys.TYPE, "");
        Log.d("ThemeCommentListFr", "rootId=" + rootId + " contentType=" + contentType);
        if (TextUtils.isEmpty(rootId) || TextUtils.isEmpty(contentType)) {
            pop();
            return;
        }
        defaultUrl = "http://tt.tljpxm.com/app/comment_topic.jsp?t=" + contentType + "&parent=" + rootId;
        nextUrl = defaultUrl;
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
            if (parent.equals(id) || parent.equals(rootId)) {
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
    public DiscoverInfo createData(Element element) {
        DiscoverInfo info = DiscoverInfo.from(element);
        Log.d("ThemeCommentListFr", "info=" + info);
        Log.d("ThemeCommentListFr", "rootId=" + rootId);
        if (info != null && rootId.equals(info.getId())) {
            return null;
        }
        return info;
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, DiscoverInfo data) {
        ThemeDetailFragment.start(data);
    }

    @Override
    public boolean showComment() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        if (isSupportVisible()) {
            onRefresh();
        }
    }
}
