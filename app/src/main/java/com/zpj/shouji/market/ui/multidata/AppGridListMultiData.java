package com.zpj.shouji.market.ui.multidata;

import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.model.AppInfo;

import java.util.List;

public class AppGridListMultiData extends AppInfoMultiData {

    public AppGridListMultiData(String title, List<AppInfo> appInfoList) {
        super(title);
        list.addAll(appInfoList);
        hasMore = false;
//        isLoaded = true;
    }

    @Override
    public boolean loadData() {
//        list.addAll(appInfoList);
//        adapter.notifyDataSetChanged();
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
