package com.zpj.shouji.market.ui.fragment.theme;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.model.DiscoverInfo;

public class ThemeCommentListFragment extends ThemeListFragment {

    protected static final String KEY_ID= "key_id";

    private String rootId;

    public static ThemeCommentListFragment newInstance(String id) {
        ThemeCommentListFragment fragment = new ThemeCommentListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void handleArguments(Bundle arguments) {
        rootId = arguments.getString(KEY_ID, "");
        if (TextUtils.isEmpty(rootId)) {
            pop();
            return;
        }
        defaultUrl = "http://tt.shouji.com.cn/app/comment_topic.jsp?versioncode=198&t=discuss&parent=" + rootId;
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
    public void onClick(EasyViewHolder holder, View view, DiscoverInfo data, float x, float y) {

    }

    @Override
    public boolean showComment() {
        return true;
    }
}
