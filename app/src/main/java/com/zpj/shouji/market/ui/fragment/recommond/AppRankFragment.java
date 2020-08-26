package com.zpj.shouji.market.ui.fragment.recommond;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ctetin.expandabletextviewlibrary.ExpandableTextView;
import com.ctetin.expandabletextviewlibrary.app.LinkType;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.glide.blur.CropBlurTransformation;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.ClassificationItem;
import com.zpj.shouji.market.ui.fragment.ToolBarListFragment;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.widget.RankHeaderLayout;

import java.util.ArrayList;
import java.util.List;

public class AppRankFragment extends NextUrlFragment<AppRankFragment.RankItem> {


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
        return R.layout.fragment_list_with_toolbar;
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
