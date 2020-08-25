//package com.zpj.shouji.market.ui.widget.recommend;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.View;
//
//import com.zpj.http.parser.html.nodes.Element;
//import com.zpj.http.parser.html.select.Elements;
//import com.zpj.shouji.market.api.HttpApi;
//import com.zpj.shouji.market.model.AppInfo;
//import com.zpj.shouji.market.ui.fragment.ToolBarListFragment;
//
//public class AppUpdateRecommendCard extends AppInfoRecommendCard {
//
//    public AppUpdateRecommendCard(Context context) {
//        this(context, null);
//    }
//
//    public AppUpdateRecommendCard(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public AppUpdateRecommendCard(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        setTitle("最近更新");
//        HttpApi.recentUpdateAppApi()
//                .onSuccess(document -> {
//                    Elements elements = document.select("item");
//                    for (Element element : elements) {
//                        AppInfo info = AppInfo.parse(element);
//                        if (info == null) {
//                            continue;
//                        }
//                        list.add(info);
//                        if (list.size() == 8) {
//                            break;
//                        }
//                    }
//                    recyclerView.notifyDataSetChanged();
//                })
//                .subscribe();
//    }
//
//    @Override
//    public void onMoreClicked(View v) {
//        ToolBarListFragment.startRecentUpdate();
//    }
//
//    @Override
//    public String getKey() {
//        return null;
//    }
//
//
//}
