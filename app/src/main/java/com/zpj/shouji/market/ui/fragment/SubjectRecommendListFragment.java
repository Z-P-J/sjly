package com.zpj.shouji.market.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.SubjectInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchResultFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class SubjectRecommendListFragment extends SubjectListFragment {

    public static SubjectRecommendListFragment newInstance(String defaultUrl) {
        Bundle args = new Bundle();
        args.putString(KEY_DEFAULT_URL, defaultUrl);
        SubjectRecommendListFragment fragment = new SubjectRecommendListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void start(String defaultUrl) {
        EventBus.getDefault().post(newInstance(defaultUrl));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_toolbar_list;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }
}
