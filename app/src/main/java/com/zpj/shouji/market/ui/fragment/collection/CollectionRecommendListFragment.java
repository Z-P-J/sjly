package com.zpj.shouji.market.ui.fragment.collection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.model.CollectionInfo;

import org.greenrobot.eventbus.EventBus;

public class CollectionRecommendListFragment extends CollectionListFragment {

    public static void start(String defaultUrl) {
        Bundle args = new Bundle();
        args.putString(KEY_DEFAULT_URL, defaultUrl);
        CollectionRecommendListFragment fragment = new CollectionRecommendListFragment();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_toolbar_list;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        setToolbarTitle("应用集");
    }

    @Override
    public CollectionInfo createData(Element element) {
        return CollectionInfo.create(element);
    }

}
