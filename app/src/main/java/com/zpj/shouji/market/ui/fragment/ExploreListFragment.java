package com.zpj.shouji.market.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.ExploreItem;
import com.zpj.shouji.market.ui.adapter.ExploreBinder;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.fragment.base.LoadMoreFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchResultFragment;
import com.zpj.shouji.market.ui.widget.BottomListPopupMenu;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExploreListFragment extends LoadMoreFragment<ExploreItem>
        implements IEasy.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        SearchResultFragment.KeywordObserver {

    public interface Callback {
        void onGetUserItem(Element element);
    }

    private final ExploreBinder binder = new ExploreBinder();

    private boolean enableSwipeRefresh = true;


    private Callback callback;


    private final Runnable getDataRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d("getExploreData", "nextUrl=" + nextUrl);
                Document doc = HttpUtil.getDocument(nextUrl);
                Elements elements = doc.select("item");
                if (nextUrl.equals(defaultUrl)) {
                    Element userElement = elements.get(0);
                    if (callback != null) {
                        callback.onGetUserItem(userElement);
                    }
                }

                nextUrl = doc.selectFirst("nextUrl").text();
                Map<String, ExploreItem> map = new HashMap<>();
                for (int i = 0; i < elements.size(); i++) {
                    Element item = elements.get(i);

                    String type = item.selectFirst("type").text();
                    if ("tag".equals(type)) {
                        continue;
                    }

                    ExploreItem exploreItem = new ExploreItem();
                    String id = item.selectFirst("id").text();
                    String parent = item.selectFirst("parent").text();
                    if (id == null && parent == null) {
                        continue;
                    }
                    Log.d("getExploreData", "id=" + id);
                    Log.d("getExploreData", "parent=" + parent);
                    exploreItem.setId(id);
                    exploreItem.setParent(parent);
                    exploreItem.setMemberId(item.selectFirst("memberid").text());

                    exploreItem.setIcon(item.selectFirst("icon").text());
                    exploreItem.setIconState(item.selectFirst("iconstate").text());
                    exploreItem.setNickName(item.selectFirst("nickname").text());
                    exploreItem.setTime(item.selectFirst("time").text());
                    exploreItem.setContent(item.selectFirst("content").text());
                    exploreItem.setPhone(item.selectFirst("phone").text());

                    Elements pics = item.select("pics");
                    Elements spics = item.select("spics");
                    if (!pics.isEmpty()) {
                        for (Element pic : item.selectFirst("pics").select("pic")) {
                            exploreItem.addPic(pic.text());
//                                exploreItem.addSpic(pic.text().replace(".png", "_s.jpg"));
                        }
                    }
                    if (!spics.isEmpty()) {
                        for (Element spic : item.selectFirst("spics").select("spic")) {
                            exploreItem.addSpic(spic.text());
                        }
                    }


                    if ("theme".equals(type)) {

                        // 分享应用
                        Elements appNames = item.select("appname");
                        if (!appNames.isEmpty()) {
                            exploreItem.setAppName(appNames.get(0).text());
                            exploreItem.setAppPackageName(item.selectFirst("apppackagename").text());
                            exploreItem.setAppIcon(item.selectFirst("appicon").text());
                            exploreItem.setApkExist("1".equals(item.selectFirst("isApkExist").text()));
                            exploreItem.setAppUrl(item.selectFirst("appurl").text());
                            exploreItem.setApkUrl(item.selectFirst("apkurl").text());
                            exploreItem.setAppSize(item.selectFirst("apksize").text());
                        }

                        // 分享应用集
                        for (Element sharePic : item.select("sharepics").select("sharepic")) {
                            exploreItem.addSharePic(sharePic.text());
                        }
                        for (Element sharePn : item.select("sharepics").select("sharepn")) {
                            exploreItem.addSharePn(sharePn.text());
                        }

                    } else if ("reply".equals(type)) {
                        String toNickName = item.selectFirst("tonickname").text();
                        exploreItem.setToNickName(toNickName);
                    }

                    Elements supportCountElements = item.select("supportcount");
                    exploreItem.setSupportCount(supportCountElements.isEmpty() ? "0" : supportCountElements.get(0).text());
                    Elements replayCountElements = item.select("replycount");
                    exploreItem.setReplyCount(replayCountElements.isEmpty() ? "0" : replayCountElements.get(0).text());
                    if (parent.equals(id)) {
                        map.put(id, exploreItem);
                        data.add(exploreItem);
                    } else {
                        ExploreItem parentItem = map.get(parent);
                        if (parentItem != null) {
                            parentItem.addChild(exploreItem);
                        }
                    }
                }
                post(() -> recyclerLayout.notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static ExploreListFragment newInstance(String url) {
        return newInstance(url, true);
    }

    public static ExploreListFragment newInstance(String url, boolean shouldLazyLoad) {
        ExploreListFragment fragment = new ExploreListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEFAULT_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_explore;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<ExploreItem> list, int position, List<Object> payloads) {
        binder.onBindViewHolder(holder, list, position, payloads);
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<ExploreItem> recyclerLayout) {
        recyclerLayout.setEnableSwipeRefresh(enableSwipeRefresh)
                .onViewClick(R.id.item_icon, (holder, view, data) -> {
                    _mActivity.start(ProfileFragment.newInstance(data.getMemberId(), false));
                });
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, ExploreItem data, float x, float y) {
        AToast.normal("TODO click");
    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, ExploreItem data, float x, float y) {
        showMenu(data);
        return true;
    }

    @Override
    public void updateKeyword(String keyword) {
        defaultUrl = "http://tt.shouji.com.cn/app/faxian.jsp?versioncode=198&s=" + keyword;
        nextUrl = defaultUrl;
        onRefresh();
//        if (isHidden()) {
//            getSupportDelegate().getVisibleDelegate().onDestroyView();
//        } else {
//            onRefresh();
//        }
    }

    @Override
    protected void getData() {
        ExecutorHelper.submit(getDataRunnable);
    }

    @Override
    public ExploreItem createData(Element element) {
        return null;
    }


    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setEnableSwipeRefresh(boolean enableSwipeRefresh) {
        this.enableSwipeRefresh = enableSwipeRefresh;
        if (recyclerLayout != null) {
            AToast.normal("setEnableSwipeRefresh");
            recyclerLayout.setEnableSwipeRefresh(enableSwipeRefresh);
        }
    }

    private void showMenu(ExploreItem data) {
        BottomListPopupMenu.with(context)
                .setMenu(R.menu.menu_tools)
                .onItemClick((menu, view, data1) -> {
                    switch (data1.getItemId()) {
                        case R.id.copy:

                            break;
                        case R.id.share:

                            break;
                        case R.id.collect:

                            break;
                        case R.id.delete:

                            break;
                        case R.id.report:

                            break;
                        case R.id.black_list:
                            break;
                    }
                    menu.dismiss();
                })
                .show();
    }

}
