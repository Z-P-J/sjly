package com.zpj.shouji.market.ui.fragment.collection;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
import com.zpj.shouji.market.utils.EventBus;

public class CollectionCommentFragment extends ThemeListFragment {

    public static CollectionCommentFragment newInstance(String id) {
        CollectionCommentFragment fragment = new CollectionCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Keys.DEFAULT_URL, "/app/yyj_comment.jsp?t=discuss&parent=" + id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.onRefreshEvent(this, s -> {
            if (isSupportVisible()) {
                onRefresh();
            }
        });
    }

}
