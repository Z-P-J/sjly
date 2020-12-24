package com.zpj.shouji.market.ui.fragment.homepage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;

public class DiscoverFragment extends ThemeListFragment {

    private static final String DEFAULT_URL = "http://tt.tljpxm.com/app/faxian.jsp?index=faxian";

    private int percent = 0;

    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Keys.DEFAULT_URL, DEFAULT_URL);
        fragment.setArguments(bundle);
        return fragment;
    }

//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_discover;
//    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<DiscoverInfo> recyclerLayout) {
        super.buildRecyclerLayout(recyclerLayout);
        recyclerLayout.setHeaderView(R.layout.item_discover_header, new IEasy.OnBindHeaderListener() {
            @Override
            public void onBindHeader(EasyViewHolder holder) {

            }
        });
        recyclerLayout.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerLayout.getRecyclerView().canScrollVertically(-1)) {
                    percent = 0;
                    EventBus.sendScrollEvent(0);
                } else {
                    if (percent != 1) {
                        EventBus.sendScrollEvent(1);
                        percent = 1;
                    }
                }
            }
        });
    }

    @Override
    protected View getFooterView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.item_footer_home, null, false);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
//        ScrollChangeEvent.post(1);
    }
//
//    @Override
//    public void onSupportInvisible() {
//
//    }

}
