package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.ui.widget.recommend.SimilarAppCard;
import com.zpj.shouji.market.ui.widget.recommend.SimilarCollectionCard;
import com.zpj.widget.statelayout.StateLayout;

import java.util.ArrayList;
import java.util.List;

public class AppDetailRecommendFragment extends BaseFragment {

    private final List<CollectionInfo> collectionInfoList = new ArrayList<>();
    private final List<AppInfo> appInfoList = new ArrayList<>();

    private StateLayout stateLayout;
    private SimilarCollectionCard similarCollectionCard;
    private SimilarAppCard similarAppCard;

    private String id;

    private boolean flag;

    public static AppDetailRecommendFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, id);
        AppDetailRecommendFragment fragment = new AppDetailRecommendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_detail_recommend;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            return;
        }
        id = getArguments().getString(Keys.ID, "");

        stateLayout = findViewById(R.id.state_layout);
        stateLayout.showLoadingView();
        similarCollectionCard = findViewById(R.id.card_similar_collection);
        similarAppCard = findViewById(R.id.card_similar_app);

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        getSimilar();
    }

    private void getSimilar() {
        HttpApi.get("http://tt.shouji.com.cn/androidv3/soft_yyj_similar.jsp?id=" + id)
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
                    similarCollectionCard.setData(collectionInfoList);
                    similarAppCard.setData(appInfoList);
                    stateLayout.showContentView();
                })
                .onError(new IHttp.OnErrorListener() {
                    @Override
                    public void onError(Throwable throwable) {
                        stateLayout.showErrorView(throwable.getMessage());
                    }
                })
                .subscribe();
    }

}
