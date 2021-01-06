package com.zpj.shouji.market.ui.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.SearchApi;
import com.zpj.shouji.market.database.SearchHistoryManager;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.model.GuessAppInfo;
import com.zpj.shouji.market.model.QuickAppInfo;
import com.zpj.shouji.market.model.SearchHistory;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.widget.DownloadButton;
import com.zpj.shouji.market.ui.widget.flowlayout.FlowLayout;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchPanelFragment extends SkinFragment {

    private final List<GuessAppInfo> appInfoList = new ArrayList<>();
    private final List<QuickAppInfo> quickAppInfoList = new ArrayList<>();

    private LinearLayout llRecommend;
    private LinearLayout llQuick;

    private EasyRecyclerView<GuessAppInfo> rvGuess;
    private EasyRecyclerView<QuickAppInfo> rvQuick;

    private FlowLayout hotSearch;
    private RelativeLayout rlHistoryBar;
    private FlowLayout searchHistory;
    private FlowLayout.OnItemClickListener onItemClickListener;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_panel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.onSearchEvent(this, new RxBus.SingleConsumer<String>() {
            @Override
            public void onAccept(String keyword) throws Exception {
                SearchHistory history = SearchHistoryManager.getSearchHistoryByText(keyword);
                if (history == null) {
                    history = new SearchHistory();
                    history.setText(keyword);
                }
                history.setTime(System.currentTimeMillis());
                history.save();
                searchHistory.remove(history.getText());
                searchHistory.addItem(0, history.getText());
                searchHistory.setVisibility(searchHistory.count() == 0 ? View.GONE : View.VISIBLE);
                rlHistoryBar.setVisibility(searchHistory.count() == 0 ? View.GONE : View.VISIBLE);
            }
        });
        EventBus.onKeywordChangeEvent(this, new RxBus.SingleConsumer<String>() {
            @Override
            public void onAccept(String keyword) throws Exception {
                if (TextUtils.isEmpty(keyword)) {
                    llRecommend.setVisibility(View.VISIBLE);
                    llQuick.setVisibility(View.GONE);
                } else {
                    SearchApi.getQuickApi(keyword, list -> {
                        quickAppInfoList.clear();
                        if (list.isEmpty()) {
                            llRecommend.setVisibility(View.VISIBLE);
                            llQuick.setVisibility(View.GONE);
                        } else {
                            llRecommend.setVisibility(View.GONE);
                            llQuick.setVisibility(View.VISIBLE);
                            quickAppInfoList.addAll(list);
                        }
                        rvQuick.notifyDataSetChanged();
                    });
                }
            }
        });
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        int dp8 = ScreenUtils.dp2pxInt(context, 8);

        llRecommend = findViewById(R.id.ll_recommend);
        llQuick = findViewById(R.id.ll_quick);

        hotSearch = view.findViewById(R.id.hot_search);
        hotSearch.setSpace(dp8);
        hotSearch.setOnItemClickListener(onItemClickListener);
        rlHistoryBar = findViewById(R.id.rl_history_bar);
        searchHistory = view.findViewById(R.id.search_history);
        searchHistory.setSpace(dp8);
        searchHistory.setOnItemClickListener(onItemClickListener);
        TextView tvClearHistory = findViewById(R.id.tv_clear_history);
        tvClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialogFragment()
                        .setTitle("清空记录？")
                        .setContent("您将清空您的历史搜索记录，确认清空？")
                        .setPositiveButton(fragment -> {
                            SearchHistoryManager.deleteAllLocalSearchHistory();
                            searchHistory.clear();
                            searchHistory.setVisibility(View.GONE);
                            rlHistoryBar.setVisibility(searchHistory.count() == 0 ? View.GONE : View.VISIBLE);
                        })
                        .show(context);
            }
        });

        rvGuess = new EasyRecyclerView<>(findViewById(R.id.rv_guess));
        rvGuess.setData(appInfoList)
                .setItemRes(R.layout.item_app_grid)
                .setLayoutManager(new GridLayoutManager(context, 5))
                .onBindViewHolder((holder, list, position, payloads) -> {
                    GuessAppInfo info = list.get(position);
                    holder.getTextView(R.id.item_title).setText(info.getAppTitle());
                    holder.getTextView(R.id.item_info).setText(info.getAppSize());
                    ImageView ivIcon = holder.getImageView(R.id.item_icon);
                    Glide.with(ivIcon)
                            .load(info.getAppIcon())
                            .apply(GlideRequestOptions.getDefaultIconOption())
                            .into(ivIcon);
                    DownloadButton downloadButton = holder.getView(R.id.tv_download);
                    downloadButton.bindApp(info);
                })
                .onItemClick((holder, view1, data) -> AppDetailFragment.start(data))
                .build();

        rvQuick = new EasyRecyclerView<>(findViewById(R.id.rv_quick));
        rvQuick.setData(quickAppInfoList)
                .setItemRes(R.layout.item_app_linear)
                .onBindViewHolder((holder, list, position, payloads) -> {
                    QuickAppInfo info = list.get(position);
//                    holder.setVisible(R.id.iv_icon, false);
                    ImageView ivIcon = holder.getView(R.id.iv_icon);
                    ViewGroup.LayoutParams params = ivIcon.getLayoutParams();
                    int size = ScreenUtils.dp2pxInt(context, 36);
                    params.height = size;
                    params.width = size;
                    ivIcon.setImageResource(R.drawable.ic_apk);
                    holder.setVisible(R.id.tv_info, false);
                    ivIcon.setColorFilter(getResources().getColor(R.color.colorPrimary));

                    TextView tvTitle = holder.getView(R.id.tv_title);
                    tvTitle.setMaxLines(1);
                    tvTitle.setTextSize(14);
                    tvTitle.setText(info.getAppTitle());
                    holder.setText(R.id.tv_desc, info.getAppPackage());

                    DownloadButton downloadButton = holder.getView(R.id.tv_download);
                    downloadButton.bindApp(info);

                })
                .onItemClick((holder, view1, data) -> AppDetailFragment.start(data))
                .build();

    }

    public void init() {
        getGuessApp();
        getHotSearch();
        getSearchHistory();
    }

    public void setOnItemClickListener(FlowLayout.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void getHotSearch() {
        SearchApi.getHotKeywordApi(obj -> {
            postOnEnterAnimationEnd(new Runnable() {
                @Override
                public void run() {
                    hotSearch.setItems(obj);
                }
            });
        });
    }

    private void getGuessApp() {
        SearchApi.getGuessApi(list -> {
//            appInfoList.addAll(list);
//            rvGuess.notifyDataSetChanged();
            postOnEnterAnimationEnd(new Runnable() {
                @Override
                public void run() {
                    appInfoList.addAll(list);
                    rvGuess.notifyDataSetChanged();
                }
            });
        });
    }

    private void getSearchHistory() {
        postOnEnterAnimationEnd(() -> {
            List<String> list = new ArrayList<>();
            for (SearchHistory history : SearchHistoryManager.getAllSearchHistory()) {
                list.add(history.getText());
            }
            searchHistory.setItems(list);
            searchHistory.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
            rlHistoryBar.setVisibility(searchHistory.count() == 0 ? View.GONE : View.VISIBLE);
        });
    }

}
