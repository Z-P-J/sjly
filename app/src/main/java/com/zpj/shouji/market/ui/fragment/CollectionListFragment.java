package com.zpj.shouji.market.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppCollectionItem;
import com.zpj.shouji.market.ui.fragment.base.LoadMoreFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchResultFragment;

import java.util.List;

public class CollectionListFragment extends LoadMoreFragment<AppCollectionItem>
        implements SearchResultFragment.KeywordObserver {

    private static final int[] ICON_RES = {R.id.iv_icon_1, R.id.iv_icon_2, R.id.iv_icon_3};

    public static CollectionListFragment newInstance(String defaultUrl) {
        Bundle args = new Bundle();
        args.putString(KEY_DEFAULT_URL, defaultUrl);
        CollectionListFragment fragment = new CollectionListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_collection_linear;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<AppCollectionItem> list, int position, List<Object> payloads) {
        AppCollectionItem item = list.get(position);
        holder.getTextView(R.id.tv_title).setText(item.getTitle());
        holder.getTextView(R.id.tv_info).setText("应用：" + item.getSize() + " | 收藏：" + item.getFavCount() + " | 查看：" + item.getViewCount());
        holder.getTextView(R.id.tv_desc).setText(item.getComment());
        for (int i = 0; i < 3; i++) {
            if (i > item.getIcons().size() - 1) {
                holder.getView(ICON_RES[i]).setVisibility(View.GONE);
                continue;
            }
            String icon = item.getIcons().get(i);
            holder.getView(ICON_RES[i]).setVisibility(View.VISIBLE);
            Glide.with(context).load(icon).into(holder.getImageView(ICON_RES[i]));
        }
    }

    @Override
    public void updateKeyword(String keyword) {
        defaultUrl = "http://tt.shouji.com.cn/androidv3/yyj_view_phb_xml.jsp?title=" + keyword;
        nextUrl = defaultUrl;
        onRefresh();
    }

    @Override
    public AppCollectionItem createData(Element element) {
        if ("yyj".equals(element.selectFirst("contenttype").text())) {
            return AppCollectionItem.create(element);
        }
        return null;
    }
}
