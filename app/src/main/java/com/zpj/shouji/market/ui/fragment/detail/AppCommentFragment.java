package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.FabEvent;
import com.zpj.shouji.market.event.RefreshEvent;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AppCommentFragment extends ThemeListFragment {

    public static AppCommentFragment newInstance(String id, String type) {
        AppCommentFragment fragment = new AppCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Keys.DEFAULT_URL, "http://tt.shouji.com.cn/app/comment_index_xml_v5.jsp?type=" + type + "&id=" + id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        recyclerLayout.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int mScrollThreshold = 0;
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isSignificantDelta = Math.abs(dy) > mScrollThreshold;
                if (isSignificantDelta) {
                    FabEvent.post(dy < 0);
                }
            }
        });
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
    public void onSupportInvisible() {
        getSupportDelegate().onSupportInvisible();
    }

    @Override
    public void onSupportVisible() {
        getSupportDelegate().onSupportVisible();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        if (isSupportVisible()) {
            onRefresh();
        }
    }


}
