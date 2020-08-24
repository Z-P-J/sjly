package com.zpj.shouji.market.ui.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.SearchApi;
import com.zpj.shouji.market.database.SearchHistoryManager;
import com.zpj.shouji.market.model.SearchHistory;
import com.zpj.shouji.market.ui.widget.flowlayout.FlowLayout;
import com.zpj.shouji.market.utils.Callback;
import com.zpj.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SearchPanelFragment extends BaseFragment {

    private FlowLayout hotSearch;
    private FlowLayout searchHistory;
    private FlowLayout.OnItemClickListener onItemClickListener;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_panel;
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
        int dp8 = ScreenUtils.dp2pxInt(context, 8);
        hotSearch = view.findViewById(R.id.hot_search);
        hotSearch.setSpace(dp8);
        hotSearch.setOnItemClickListener(onItemClickListener);
        searchHistory = view.findViewById(R.id.search_history);
        searchHistory.setSpace(dp8);
        searchHistory.setOnItemClickListener(onItemClickListener);
        getHotSearch();
        getSearchHistory();
    }

    public void setOnItemClickListener(FlowLayout.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void getHotSearch() {
        SearchApi.getHotKeywordApi(obj -> hotSearch.setItems(obj));
    }

    private void getSearchHistory() {
        List<String> list = new ArrayList<>();
        for (SearchHistory history : SearchHistoryManager.getAllSearchHistory()) {
            list.add(history.getText());
        }
        searchHistory.addItems(list);
    }

    @Subscribe
    public void onSearchEvent(SearchFragment.SearchEvent event) {
        String keyword = event.keyword;
        SearchHistory history = SearchHistoryManager.getSearchHistoryByText(keyword);
        if (history == null) {
            history = new SearchHistory();
            history.setText(keyword);
        }
        history.setTime(System.currentTimeMillis());
        history.save();
        getSearchHistory();
    }
}
