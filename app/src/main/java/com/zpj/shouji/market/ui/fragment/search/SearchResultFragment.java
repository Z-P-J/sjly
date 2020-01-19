package com.zpj.shouji.market.ui.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.database.SearchHistoryManager;
import com.zpj.shouji.market.model.SearchHistory;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SearchResultFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

    }

    @Subscribe
    public void onSearchEvent(SearchFragment.SearchEvent event) {
        AToast.success(event.keyword);
    }

}
