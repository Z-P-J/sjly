package com.zpj.shouji.market.ui.fragment.collection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.statelayout.StateLayout;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import per.goweii.burred.Blurred;

public class CollectionDetailFragment extends BaseFragment {

    private final String[] TAB_TITLES = {"应用", "评论"};

    private StateLayout stateLayout;
    private AppBarLayout appBarLayout;
    private ImageView ivIcon;
    private ImageView ivAvatar;
    private TextView tvTitle;
    private TextView tvUserName;
    private TextView tvDesc;
    private TextView tvSupport;
    private TextView tvFavorite;
    private TextView tvView;
    private TextView tvDownload;
    private ViewPager viewPager;
    private MagicIndicator magicIndicator;

    private String backgroundUrl;
    private String time;
    private String userAvatarUrl;
    private boolean isFav;
    private boolean isLike;

    private CollectionInfo item;

    public static void start(CollectionInfo item) {
        Bundle args = new Bundle();
        CollectionDetailFragment fragment = new CollectionDetailFragment();
        fragment.setAppCollectionItem(item);
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
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

        stateLayout = view.findViewById(R.id.state_layout);
        stateLayout.showLoadingView();

        appBarLayout = view.findViewById(R.id.appbar);
        ivIcon = view.findViewById(R.id.iv_icon);
        tvTitle = view.findViewById(R.id.tv_title);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvDesc = view.findViewById(R.id.tv_desc);
        tvSupport = view.findViewById(R.id.tv_support);
        tvFavorite = view.findViewById(R.id.tv_favorite);
        tvView = view.findViewById(R.id.tv_view);
        tvDownload = view.findViewById(R.id.tv_download);
//        Toolbar toolbar = view.findViewById(R.id.toolbar);
//        toolbar.setTitle(item.getTitle());

        viewPager = view.findViewById(R.id.view_pager);
        magicIndicator = view.findViewById(R.id.magic_indicator);

        //        TAB_TITLES[0] = TAB_TITLES[0] + "(" + item.getSize() + ")";
//        TAB_TITLES[1] = TAB_TITLES[1] + "(" + item.getReplyCount() + ")";

        setToolbarTitle(item.getTitle());

        tvTitle.setText(item.getTitle());
        tvUserName.setText(item.getNickName());
        tvDesc.setText(item.getComment());
//        tvFavorite.setText(item.getFavCount() + "");
//        tvSupport.setText(item.getSupportCount() + "");
//        tvView.setText(item.getViewCount() + "");


        ArrayList<Fragment> list = new ArrayList<>();
        CollectionAppListFragment appListFragment = findChildFragment(CollectionAppListFragment.class);
        if (appListFragment == null) {
            appListFragment = CollectionAppListFragment.newInstance(item.getId());
        }
        ThemeListFragment themeListFragment = findChildFragment(ThemeListFragment.class);
        if (themeListFragment == null) {
            themeListFragment = ThemeListFragment.newInstance("http://tt.shouji.com.cn/app/yyj_comment.jsp?t=discuss&parent=" + item.getId());
        }
        list.add(appListFragment);
        list.add(themeListFragment);

        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(list.size());
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
                titleView.setNormalColor(Color.GRAY);
                titleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
                titleView.setTextSize(14);
                titleView.setText(TAB_TITLES[index]);
                titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index));
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(ScreenUtils.dp2px(context, 4f));
                indicator.setLineWidth(ScreenUtils.dp2px(context, 12f));
                indicator.setRoundRadius(ScreenUtils.dp2px(context, 4f));
                indicator.setColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimary));
                return indicator;
            }
        });
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        getCollectionInfo();
    }

    private void setAppCollectionItem(CollectionInfo item) {
        this.item = item;
    }

    private void getCollectionInfo() {
        Log.d("getCollectionInfo", "start id=" + item.getId());
        HttpApi.connect("http://tt.shouji.com.cn/androidv3/yyj_info_xml.jsp?reviewid=" + item.getId())
                .onSuccess(doc -> {
                    Log.d("getCollectionInfo", "doc=" + doc.toString());
//                collectionInfo.collectionId = doc.selectFirst("yyjid").text();
                    isFav = "1".equals(doc.selectFirst("isfav").text());
                    isLike = "1".equals(doc.selectFirst("islike").text());
                    backgroundUrl = doc.selectFirst("memberBackGround").text();
                    time = doc.selectFirst("time").text();
                    userAvatarUrl = doc.selectFirst("memberAvatar").text();
                    tvFavorite.setText(doc.selectFirst("favcount").text());
                    tvSupport.setText(doc.selectFirst("supportcount").text());
                    tvView.setText(doc.selectFirst("viewcount").text());
                    RequestOptions options = new RequestOptions().centerCrop().error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher);
//                    Glide.with(context).load(backgroundUrl).apply(options).into(ivIcon);
                    Glide.with(context).load(userAvatarUrl).apply(options).into(ivAvatar);
                    Glide.with(context).asBitmap().load(backgroundUrl).apply(options).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ivIcon.setImageBitmap(resource);
                            Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
                                Bitmap bitmap = Blurred.with(resource)
                                        .percent(0.1f)
                                        .scale(0.1f)
                                        .blur();
                                emitter.onNext(bitmap);
                                emitter.onComplete();
                            })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnNext(bitmap -> {
                                        appBarLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
                                        getColor(bitmap);
                                    })
                                    .subscribe();
                        }
                    });
                    stateLayout.showContentView();
                })
                .onError(throwable -> stateLayout.showErrorView(throwable.getMessage()))
                .subscribe();
    }

    public void getColor(Bitmap bitmap) {
        Palette.from(bitmap)
                .generate(palette -> {
                    if (palette != null) {
                        Palette.Swatch s = palette.getDominantSwatch();//独特的一种
                        if (s != null) {
                            post(() -> {
                                boolean isDark = ColorUtils.calculateLuminance(s.getRgb()) <= 0.5;
                                if (isDark) {
                                    lightStatusBar();
                                } else {
                                    darkStatusBar();
                                }
                                int color = getResources().getColor(isDark ? R.color.white : R.color.color_text_major);
                                tvTitle.setTextColor(color);
                                tvUserName.setTextColor(color);
                                tvDesc.setTextColor(color);
                                tvFavorite.setTextColor(color);
                                tvSupport.setTextColor(color);
                                tvView.setTextColor(color);
                                toolbar.setLightStyle(isDark);
                            });

                        }
                    }
                });
    }

}
