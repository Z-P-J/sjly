package com.zpj.shouji.market.ui.fragment.collection;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zpj.shouji.market.event.RefreshEvent;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CollectionCommentFragment extends ThemeListFragment {

    public static CollectionCommentFragment newInstance(String id) {
        CollectionCommentFragment fragment = new CollectionCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEFAULT_URL, "http://tt.shouji.com.cn/app/yyj_comment.jsp?t=discuss&parent=" + id);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        if (isSupportVisible()) {
            onRefresh();
        }
    }

}
