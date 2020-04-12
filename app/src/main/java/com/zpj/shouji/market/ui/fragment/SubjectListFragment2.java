package com.zpj.shouji.market.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.SubjectInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchResultFragment;

import java.util.List;

public class SubjectListFragment2 extends AppListFragment {

//    public static SubjectListFragment2 newInstance(String defaultUrl) {
//        Bundle args = new Bundle();
//        args.putString(KEY_DEFAULT_URL, defaultUrl);
//        SubjectListFragment2 fragment = new SubjectListFragment2();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_subject_list;
    }

    public static SubjectListFragment2 newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(KEY_DEFAULT_URL, "http://tt.shouji.com.cn/androidv3/special_list_xml.jsp?id=" + id);
        SubjectListFragment2 fragment = new SubjectListFragment2();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        setToolbarTitle("专题详情");
    }
}
