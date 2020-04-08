package com.zpj.shouji.market.ui.fragment.detail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sunfusheng.GroupRecyclerViewAdapter;
import com.sunfusheng.GroupViewHolder;
import com.sunfusheng.HeaderGroupRecyclerViewAdapter;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.glide.blur.BlurTransformation2;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.model.GroupItem;
import com.zpj.shouji.market.ui.fragment.collection.CollectionDetailFragment;
import com.zpj.shouji.market.ui.widget.recommend.SimilarAppCard;
import com.zpj.shouji.market.ui.widget.recommend.SimilarCollectionCard;

import java.util.ArrayList;
import java.util.List;

public class AppRecommendFragment extends BaseFragment {

    private static final String KEY_ID = "key_id";
    private final List<Object> datas = new ArrayList<>();
    private List<CollectionInfo> collectionInfoList = new ArrayList<>();
    private List<AppInfo> appInfoList = new ArrayList<>();

    private EasyRecyclerLayout<Object> recyclerLayout;
    private SimilarCollectionCard similarCollectionCard;
    private SimilarAppCard similarAppCard;

    private String id;

    public static AppRecommendFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        AppRecommendFragment fragment = new AppRecommendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_layout;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            return;
        }
        id = getArguments().getString(KEY_ID, "");
        recyclerLayout = view.findViewById(R.id.recycler_layout);
        recyclerLayout.setData(datas)
                .setOnRefreshListener(() -> {
                    collectionInfoList.clear();
                    appInfoList.clear();
                    recyclerLayout.notifyDataSetChanged();
                })
                .onGetChildViewType(new IEasy.OnGetChildViewTypeListener() {
                    @Override
                    public int onGetViewType(int position) {
                        return position;
                    }
                })
                .onCreateViewHolder(new IEasy.OnCreateViewHolderListener<Object>() {
                    @Override
                    public View onCreateViewHolder(ViewGroup parent, int layoutRes, int viewType) {
                        if (viewType == 0) {
                            return similarCollectionCard;
                        } else {
                            return similarAppCard;
                        }
                    }
                })
                .onBindViewHolder(new IEasy.OnBindViewHolderListener<Object>() {
                    @Override
                    public void onBindViewHolder(EasyViewHolder holder, List<Object> list, int position, List<Object> payloads) {
                        if (appInfoList.isEmpty() && collectionInfoList.isEmpty()) {
                            getSimilar();
                        } else if (position == 1) {
                            similarAppCard.setData(appInfoList);
                        } else if (position == 0) {
                            similarCollectionCard.setData(collectionInfoList);
                        }
                    }
                })
                .build();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        similarCollectionCard = new SimilarCollectionCard(context);
        similarAppCard = new SimilarAppCard(context);
        datas.add(new Object());
        datas.add(new Object());
        recyclerLayout.notifyDataSetChanged();
    }

    private void getSimilar() {
        HttpApi.connect("http://tt.shouji.com.cn/androidv3/soft_yyj_similar.jsp?id=" + id)
                .onSuccess(data -> {
                    Elements elements = data.select("item");
                    for (Element element : elements) {
                        if ("yyj".equals(element.selectFirst("viewtype").text())) {
                            for (Element recognizeItem : element.selectFirst("recognizelist").select("recognize")) {
                                collectionInfoList.add(CollectionInfo.buildSimilarCollection(recognizeItem));
                            }
                        } else {
                            appInfoList.add(AppInfo.parse(element));
                        }
                    }
                    recyclerLayout.notifyDataSetChanged();
                })
                .onError(new IHttp.OnErrorListener() {
                    @Override
                    public void onError(Throwable throwable) {
                        recyclerLayout.showErrorView(throwable.getMessage());
                    }
                })
                .subscribe();
    }

}
