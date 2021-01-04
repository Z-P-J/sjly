package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.MultiAdapter;
import com.zpj.recyclerview.MultiData;
import com.zpj.recyclerview.MultiRecyclerViewWrapper;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.shouji.market.ui.multidata.AppInfoMultiData;
import com.zpj.shouji.market.ui.multidata.CollectionMultiData;
import com.zpj.utils.Callback;

import java.util.ArrayList;
import java.util.List;

public class AppDetailRecommendFragment extends SkinFragment {

    private final List<CollectionInfo> collectionInfoList = new ArrayList<>();
    private final List<AppInfo> appInfoList = new ArrayList<>();

    private MultiRecyclerViewWrapper wrapper;

    private String id;
    private String type;

    public static AppDetailRecommendFragment newInstance(String id, String type) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, id);
        args.putString(Keys.TYPE, type);
        AppDetailRecommendFragment fragment = new AppDetailRecommendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            pop();
            return;
        }
        id = getArguments().getString(Keys.ID, "");
        type = getArguments().getString(Keys.TYPE, "soft");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        wrapper = new MultiRecyclerViewWrapper(recyclerView);
    }

    @Override
    protected void initStatusBar() {

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
//        getSimilar();
        List<MultiData<?>> multiDataList = new ArrayList<>();

        SimilarAppMultiData similarAppMultiData = new SimilarAppMultiData();
        multiDataList.add(new SimilarCollectionMultiData(type, id, new Callback<List<AppInfo>>() {
            @Override
            public void onCallback(List<AppInfo> list) {
                similarAppMultiData.setData(list);
            }
        }));

        multiDataList.add(similarAppMultiData);

        wrapper.setData(multiDataList).build();
    }

    private void getSimilar() {
//        HttpApi.get("http://tt.shouji.com.cn/androidv3/" + type + "_yyj_similar.jsp?id=" + id)
//                .onSuccess(data -> {
//                    Elements elements = data.select("item");
//                    for (Element element : elements) {
//                        if ("yyj".equals(element.selectFirst("viewtype").text())) {
//                            for (Element recognizeItem : element.selectFirst("recognizelist").select("recognize")) {
//                                collectionInfoList.add(CollectionInfo.buildSimilarCollection(recognizeItem));
//                            }
//                        } else {
//                            appInfoList.add(AppInfo.parse(element));
//                        }
//                    }
//
//                    List<MultiData> multiDataList = new ArrayList<>();
//
//                    multiDataList.add(new SimilarCollectionMultiData(collectionInfoList));
//
//                    multiDataList.add(new AppGridListMultiData("相似应用", appInfoList));
//
//                    wrapper.setData(multiDataList)
//                            .setMaxSpan(4)
//                            .build();
//
//                })
//                .onError(new IHttp.OnErrorListener() {
//                    @Override
//                    public void onError(Throwable throwable) {
////                        stateLayout.showErrorView(throwable.getMessage());
//                    }
//                })
//                .subscribe();
    }

//    private static class SimilarCollectionMultiData extends CollectionMultiData {
//
//        private final List<CollectionInfo> collectionInfoList;
//
//        public SimilarCollectionMultiData(List<CollectionInfo> collectionInfoList) {
//            super("相关应用集");
//            this.collectionInfoList = collectionInfoList;
//
//        }
//
//        @Override
//        public boolean loadData(MultiAdapter adapter) {
//            list.addAll(collectionInfoList);
//            adapter.notifyDataSetChanged();
//            return false;
//        }
//    }

    private static class SimilarCollectionMultiData extends CollectionMultiData {

        private final String url;
        private final Callback<List<AppInfo>> callback;

        public SimilarCollectionMultiData(String type, String id, Callback<List<AppInfo>> callback) {
            super("相关应用集");
            this.url = "http://tt.shouji.com.cn/androidv3/" + type + "_yyj_similar.jsp?id=" + id;
            this.callback = callback;
        }

        @Override
        public boolean loadData(MultiAdapter adapter) {
            List<AppInfo> appInfoList = new ArrayList<>();
            HttpApi.getXml(url)
                    .onSuccess(data -> {
                        Elements elements = data.select("item");
                        for (Element element : elements) {
                            if ("yyj".equals(element.selectFirst("viewtype").text())) {
                                for (Element recognizeItem : element.selectFirst("recognizelist").select("recognize")) {
                                    list.add(CollectionInfo.buildSimilarCollection(recognizeItem));
                                }
                            } else {
                                appInfoList.add(AppInfo.parse(element));
                            }
                        }
                        if (callback != null) {
                            callback.onCallback(appInfoList);
                        }
                        adapter.notifyDataSetChanged();
                    })
                    .subscribe();
            return false;
        }
    }

    public static class SimilarAppMultiData extends AppInfoMultiData {

        public SimilarAppMultiData() {
            super("相似应用");
        }

        public void setData(List<AppInfo> appInfoList) {
            this.list.addAll(appInfoList);
            hasMore = false;
            isLoaded = true;
        }

        @Override
        public boolean loadData(MultiAdapter adapter) {
            return false;
        }

        @Override
        public PreloadApi getKey() {
            return null;
        }

        @Override
        public void onHeaderClick() {

        }

    }

}
