package com.zpj.shouji.market.ui.fragment.collection;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.immersionbar.ImmersionBar;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.ui.adapter.ZFragmentPagerAdapter;
import com.zpj.shouji.market.ui.fragment.discover.DiscoverListFragment;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.HttpUtil;
import com.zpj.utils.ScreenUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.io.IOException;
import java.util.ArrayList;

public class CollectionDetailFragment extends BaseFragment {

    private final String[] TAB_TITLES = {"应用", "评论"};

    private ImageView ivIcon;
    private ImageView ivAvatar;
    private TextView tvTitle;
    private TextView tvUserName;
    private TextView tvDesc;
    private TextView tvSupport;
    private TextView tvFavorite;
    private TextView tvView;
    private TextView tvDownload;

    private String backgroundUrl;
    private String time;
    private String userAvatarUrl;
    private boolean isFav;
    private boolean isLike;

    private CollectionInfo item;

    public static CollectionDetailFragment newInstance(CollectionInfo item) {
        Bundle args = new Bundle();
        CollectionDetailFragment fragment = new CollectionDetailFragment();
        fragment.setAppCollectionItem(item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_collection_detail;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (item == null) {
            pop();
            return;
        }
        ivIcon = view.findViewById(R.id.iv_icon);
        tvTitle = view.findViewById(R.id.tv_title);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvDesc = view.findViewById(R.id.tv_desc);
        tvSupport = view.findViewById(R.id.tv_support);
        tvFavorite = view.findViewById(R.id.tv_favorite);
        tvView = view.findViewById(R.id.tv_view);
        tvDownload = view.findViewById(R.id.tv_download);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        TAB_TITLES[0] = TAB_TITLES[0] + "(" + item.getSize() + ")";
        TAB_TITLES[1] = TAB_TITLES[1] + "(" + item.getReplyCount() + ")";

        getCollectionInfo();

        toolbar.setTitle(item.getTitle());
        tvTitle.setText(item.getTitle());
        tvUserName.setText(item.getNickName());
        tvDesc.setText(item.getComment());
        tvFavorite.setText(item.getFavCount() + "");
        tvSupport.setText(item.getSupportCount() + "");
        tvView.setText(item.getViewCount() + "");



        ArrayList<Fragment> list = new ArrayList<>();
        CollectionAppListFragment appListFragment = findChildFragment(CollectionAppListFragment.class);
        if (appListFragment == null) {
            appListFragment = CollectionAppListFragment.newInstance(item.getId());
        }
        DiscoverListFragment discoverListFragment = findChildFragment(DiscoverListFragment.class);
        if (discoverListFragment == null) {
            discoverListFragment = DiscoverListFragment.newInstance("http://tt.shouji.com.cn/app/yyj_comment.jsp?versioncode=198&t=discuss&parent=" + item.getId());
        }
        list.add(appListFragment);
        list.add(discoverListFragment);

        ZFragmentPagerAdapter adapter = new ZFragmentPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        MagicIndicator magicIndicator = view.findViewById(R.id.magic_indicator);
        CommonNavigator navigator = new CommonNavigator(context);
        navigator.setAdjustMode(true);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return TAB_TITLES.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleView.setNormalColor(Color.LTGRAY);
                titleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
                titleView.setTextSize(14);
                titleView.setText(TAB_TITLES[index]);
                titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index));
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setLineHeight(ScreenUtil.dp2px(context, 1));
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                return indicator;
            }
        });
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    @Override
    public void onVisible() {
        ImmersionBar.with(this).statusBarDarkFont(false).init();
    }

    private void setAppCollectionItem(CollectionInfo item) {
        this.item = item;
    }

    private void getCollectionInfo() {
        ExecutorHelper.submit(() -> {
            try {
                Log.d("getCollectionInfo", "start id=" + item.getId());
                Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/androidv3/yyj_info_xml.jsp?versioncode=198&reviewid=" + item.getId());
                Log.d("getCollectionInfo", "doc=" + doc.toString());
//                collectionInfo.collectionId = doc.selectFirst("yyjid").text();
                isFav = "1".equals(doc.selectFirst("isfav").text());
                isLike = "1".equals(doc.selectFirst("islike").text());
                backgroundUrl = doc.selectFirst("memberBackGround").text();
                time = doc.selectFirst("time").text();
                userAvatarUrl = doc.selectFirst("memberAvatar").text();
                post(() -> {
                    RequestOptions options = new RequestOptions().centerCrop().error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher);
                    Glide.with(context).load(backgroundUrl).apply(options).into(ivIcon);
                    Glide.with(context).load(userAvatarUrl).apply(options).into(ivAvatar);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
