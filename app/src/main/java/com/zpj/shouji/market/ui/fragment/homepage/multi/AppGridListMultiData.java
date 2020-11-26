package com.zpj.shouji.market.ui.fragment.homepage.multi;

import com.zpj.recyclerview.MultiAdapter;
import com.zpj.recyclerview.MultiData;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.model.AppInfo;

import java.lang.reflect.Field;
import java.util.List;

public class AppGridListMultiData extends AppInfoMultiData {

    private final List<AppInfo> appInfoList;

    public AppGridListMultiData(String title, List<AppInfo> appInfoList) {
        super(title);
        this.appInfoList = appInfoList;
        list.addAll(appInfoList);
        try {
            hasMore = false;
            Field isLoaded = MultiData.class.getDeclaredField("isLoaded");
            isLoaded.setAccessible(true);
            isLoaded.set(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean loadData(MultiAdapter adapter) {
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
