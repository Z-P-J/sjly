package com.zpj.shouji.market.ui.fragment.recommond;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.widget.DownloadButton;
import com.zpj.shouji.market.ui.widget.RankHeaderLayout;
import com.zpj.shouji.market.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

public class AppRankFragment extends NextUrlFragment<AppRankFragment.RankItem> {

    private View shadowBottomView;

    private boolean flag = false;

    public static void startSoft() {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, "http://tt.shouji.com.cn/androidv3/soft_index_xml.jsp?sort=day");
        args.putString(Keys.TITLE, "软件排行");
        AppRankFragment fragment = new AppRankFragment();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    public static void startGame() {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, "http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sort=day");
        args.putString(Keys.TITLE, "游戏排行");
        AppRankFragment fragment = new AppRankFragment();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    public static class RankItem {
        private AppInfo rank1;
        private AppInfo rank2;
        private AppInfo rank3;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_rank;
    }

    @Override
    protected int getItemLayoutId() {
        return 0;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        if (getArguments() != null) {
            setToolbarTitle(getArguments().getString(Keys.TITLE, "Title"));
        }
        shadowBottomView = view.findViewById(R.id.shadow_bottom_view);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        ThemeUtils.initStatusBar(this);
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<RankItem> recyclerLayout) {
        super.buildRecyclerLayout(recyclerLayout);
        recyclerLayout
                .onGetChildViewType(new IEasy.OnGetChildViewTypeListener<RankItem>() {
                    @Override
                    public int onGetViewType(List<RankItem> list, int position) {
                        if (position == 0) {
                            return 0;
                        }
                        return 1;
                    }
                })
//                .addOnScrollListener(new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                        super.onScrolled(recyclerView, dx, dy);
//                        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                        if (layoutManager != null) {
//                            int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
//                            if (firstCompletelyVisibleItemPosition == 0) {
//                                toolbar.setBackgroundColor(Color.TRANSPARENT);
//                            } else {
//                                toolbar.setBackgroundColor(Color.parseColor("#aaffffff"));
//                            }
//                        }
//                    }
//                })
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                        if (newState == RecyclerView.SCROLL_STATE_IDLE) { //当前状态为停止滑动
//                            if (!recyclerLayout.getRecyclerView().canScrollVertically(-1)) {
//                                toolbar.setBackgroundColor(Color.TRANSPARENT);
//                                toolbar.setLightStyle(true);
//
//                            } else {
//                                toolbar.setBackgroundColor(Color.parseColor("#aaffffff"));
//                                toolbar.setLightStyle(false);
//                            }
//                        }

                        if (!recyclerLayout.getRecyclerView().canScrollVertically(-1)) {
                            toolbar.setBackgroundColor(Color.TRANSPARENT);
                            toolbar.setLightStyle(true);
                            shadowBottomView.setVisibility(View.GONE);
                        } else {
                            toolbar.setBackgroundColor(Color.parseColor("#aaffffff"));
                            toolbar.setLightStyle(false);
                            shadowBottomView.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .onGetChildLayoutId(viewType -> {
                    if (viewType == 0) {
                        return R.layout.item_rank_header;
                    } else if (viewType == 1) {
                        return R.layout.item_app_linear;
                    }
                    return 0;
                });
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        flag = false;
    }

    @Override
    public boolean onLoadMore(EasyAdapter.Enabled enabled, int currentPage) {
        if (TextUtils.isEmpty(nextUrl)) {
            return false;
        }
//        if (data.isEmpty()) {
//            recyclerLayout.showLoading();
//        }
//        getData();

        if (data.isEmpty() && !refresh) {
            if (flag) {
                return false;
            }
            flag = true;
            recyclerLayout.showLoading();
            postOnEnterAnimationEnd(this::getData);
        } else {
            getData();
        }
//        refresh = false;
        return true;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<RankItem> list, int position, List<Object> payloads) {
        RankItem item = list.get(position);
        if (holder.getViewType() == 0) {
            RankHeaderLayout rank1 = holder.getView(R.id.rank_1);
            RankHeaderLayout rank2 = holder.getView(R.id.rank_2);
            RankHeaderLayout rank3 = holder.getView(R.id.rank_3);
            rank1.loadApp(item.rank1);
            rank2.loadApp(item.rank2);
            rank3.loadApp(item.rank3);
        } else {
            final AppInfo appInfo = item.rank1;
            holder.getTextView(R.id.tv_title).setText(appInfo.getAppTitle());
            holder.getTextView(R.id.tv_info).setText(appInfo.getAppSize());
            holder.getTextView(R.id.tv_desc).setText(appInfo.getAppComment());
            Glide.with(context).load(appInfo.getAppIcon()).into(holder.getImageView(R.id.iv_icon));
            DownloadButton downloadButton = holder.getView(R.id.tv_download);
            downloadButton.bindApp(appInfo);
        }
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, RankItem data) {
        if (holder.getViewType() == 0) {
            return;
        }
        AppDetailFragment.start(data.rank1);
    }

    protected void onGetDocument(Document doc) throws Exception {
        Elements items = doc.select("item");
        items.remove(0);
        List<AppInfo> appInfoList = new ArrayList<>();

        for (Element element : items) {
            AppInfo item = AppInfo.parse(element);
            if (item == null) {
                continue;
            }
            appInfoList.add(item);
        }

        if (data.isEmpty() && appInfoList.size() > 3) {
            RankItem item = new RankItem();
            item.rank1 = appInfoList.remove(0);
            item.rank2 = appInfoList.remove(0);
            item.rank3 = appInfoList.remove(0);
            data.add(item);
        }

        for (AppInfo info : appInfoList) {
            RankItem rankItem = new RankItem();
            rankItem.rank1 = info;
            data.add(rankItem);
        }


    }

    @Override
    public RankItem createData(Element element) {
        return null;
    }

}
