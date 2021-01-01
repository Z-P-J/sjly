package com.zpj.shouji.market.ui.fragment.collection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.model.CollectionInfo;

public class CollectionRecommendListFragment extends CollectionListFragment {

    public static void start() {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, "http://tt.shouji.com.cn/androidv3/yyj_tj_xml.jsp");
        CollectionRecommendListFragment fragment = new CollectionRecommendListFragment();
        fragment.setArguments(args);
        start(fragment);
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_with_toolbar;
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
