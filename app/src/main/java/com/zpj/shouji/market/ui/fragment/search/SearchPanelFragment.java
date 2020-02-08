package com.zpj.shouji.market.ui.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.kongzue.stacklabelview.StackLabel;
import com.kongzue.stacklabelview.interfaces.OnLabelClickListener;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.database.SearchHistoryManager;
import com.zpj.shouji.market.model.SearchHistory;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.HttpApi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchPanelFragment extends BaseFragment {

    private StackLabel hotSearch;
    private StackLabel searchHistory;
    private OnLabelClickListener onLabelClickListener;

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
        hotSearch = view.findViewById(R.id.hot_search);
        hotSearch.setOnLabelClickListener(onLabelClickListener);
        searchHistory = view.findViewById(R.id.search_history);
        searchHistory.setOnLabelClickListener(onLabelClickListener);
        getHotSearch();
        getSearchHistory();
    }

    public void setOnLabelClickListener(OnLabelClickListener onLabelClickListener) {
        this.onLabelClickListener = onLabelClickListener;
    }

    private void getHotSearch() {
        HttpApi.connect("http://tt.shouji.com.cn/app/user_app_search_rm_xml.jsp?searchKey=")
                .onSuccess(data -> {
                    List<String> list = new ArrayList<>();
                    for (Element item : data.select("item")) {
                        list.add(item.selectFirst("name").text());
                    }
                    hotSearch.setLabels(list);
                })
                .subscribe();
    }

    private void getSearchHistory() {
        List<String> list = new ArrayList<>();
        for (SearchHistory history : SearchHistoryManager.getAllSearchHistory()) {
            list.add(history.getText());
        }
        searchHistory.setLabels(list);
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
