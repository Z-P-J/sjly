package com.zpj.shouji.market.ui.fragment.theme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.event.RefreshEvent;
import com.zpj.shouji.market.model.DiscoverInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ThemeCommentListFragment extends ThemeListFragment {

    protected static final String KEY_ID= "key_id";
    protected static final String KEY_TYPE= "key_type";

    private String rootId;
//    private String contentType;

    public static ThemeCommentListFragment newInstance(String id, String type) {
        ThemeCommentListFragment fragment = new ThemeCommentListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID, id);
        bundle.putString(KEY_TYPE, type);
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
        rootId = arguments.getString(KEY_ID, "");
        String contentType = arguments.getString(KEY_TYPE, "");
        if (TextUtils.isEmpty(rootId) || TextUtils.isEmpty(contentType)) {
            pop();
            return;
        }
        defaultUrl = "http://tt.tljpxm.com/app/comment_topic.jsp?t=" + contentType + "&parent=" + rootId;
        nextUrl = defaultUrl;
    }

    @Override
    public DiscoverInfo createData(Element element) {
        DiscoverInfo info = DiscoverInfo.from(element);
        if (info != null && rootId.equals(info.getId())) {
            return null;
        }
        return info;
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, DiscoverInfo data) {

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
