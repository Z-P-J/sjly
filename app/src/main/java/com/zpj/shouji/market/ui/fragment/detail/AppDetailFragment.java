package com.zpj.shouji.market.ui.fragment.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.felix.atoast.library.AToast;
import com.shehuan.niv.NiceImageView;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.event.BaseEvent;
import com.zpj.shouji.market.event.FabEvent;
import com.zpj.shouji.market.event.RefreshEvent;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.AppUpdateInfo;
import com.zpj.shouji.market.model.CollectionAppInfo;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.model.UserDownloadedAppInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.widget.popup.AppCommentPopup;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;
import per.goweii.burred.Blurred;

public class AppDetailFragment extends BaseFragment {

    private static final String TAG = "AppDetailFragment";

    private static final String[] TAB_TITLES = {"详情", "评论", "发现", "推荐"};

    private static final String SOFT_URL = "http://tt.shouji.com.cn/androidv3/soft_show.jsp?id=";
    private static final String GAME_URL = "http://tt.shouji.com.cn/androidv3/game_show.jsp?id=";
    private final static String KEY_ID = "app_id";
    private final static String KEY_TYPE = "app_type";

    private String url;
    private String id;
    private String type;

    private AppDetailInfo appDetailInfo;
    private StateLayout stateLayout;
    private AppBarLayout appBarLayout;
    private NiceImageView icon;
    private TextView title;
    private TextView shortInfo;
    private TextView shortIntroduce;
    private FloatingActionButton fabComment;


    public static void start(String type, String id) {
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        args.putString(KEY_TYPE, type);
        AppDetailFragment fragment = new AppDetailFragment();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    public static void start(AppInfo item) {
        start(item.getAppType(), item.getAppId());
    }

    public static void start(AppUpdateInfo item) {
        start(item.getAppType(), item.getId());
    }

    public static void start(InstalledAppInfo appInfo) {
        start(appInfo.getAppType(), appInfo.getId());
    }

    public static void start(UserDownloadedAppInfo info) {
        start(info.getAppType(), info.getId());
    }

    public static void start(CollectionAppInfo info) {
        start(info.getAppType(), info.getId());
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_detail;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            type = getArguments().getString(KEY_TYPE);
            id = getArguments().getString(KEY_ID);
            if ("game".equals(type)) {
                url = GAME_URL + id;
            } else {
                url = SOFT_URL + id;
            }
        } else {
            pop();
            return;
        }
        stateLayout = view.findViewById(R.id.state_layout);
        stateLayout.showLoadingView();
        appBarLayout = view.findViewById(R.id.appbar);
        icon = view.findViewById(R.id.iv_icon);
        title = view.findViewById(R.id.tv_title);
        shortInfo = view.findViewById(R.id.tv_info);
        shortIntroduce = view.findViewById(R.id.tv_detail);

        fabComment = view.findViewById(R.id.fab_comment);
        fabComment.hide();
        fabComment.setOnClickListener(v -> {
            AppCommentPopup.with(context, id, type, "").show();
        });


        ArrayList<Fragment> list = new ArrayList<>();
        AppInfoFragment infoFragment = findChildFragment(AppInfoFragment.class);
        if (infoFragment == null) {
            infoFragment = new AppInfoFragment();
        }

        AppCommentFragment commentFragment = findChildFragment(AppCommentFragment.class);
        if (commentFragment == null) {
            commentFragment = AppCommentFragment.newInstance(id, type);
        }

        AppThemeFragment exploreFragment = findChildFragment(AppThemeFragment.class);
        if (exploreFragment == null) {
            exploreFragment = AppThemeFragment.newInstance(id, type);
        }

        AppRecommendFragment recommendFragment = findChildFragment(AppRecommendFragment.class);
        if (recommendFragment == null) {
            recommendFragment = AppRecommendFragment.newInstance(id);
        }
        list.add(infoFragment);
        list.add(commentFragment);
        list.add(exploreFragment);
        list.add(recommendFragment);

        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 1) {
                    fabComment.show();
                } else {
                    fabComment.hide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(list.size());
        MagicIndicator magicIndicator = view.findViewById(R.id.magic_indicator);
        CommonNavigator navigator = new CommonNavigator(getContext());
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
        getAppInfo(url);
    }

    private void getAppInfo(final String url) {
        Log.d("apppppp", url);
        HttpApi.get(url)
                .onSuccess(data -> {
                    if ("NoApp".equals(data.selectFirst("errorcode").text())) {
                        AToast.warning("应用不存在");
                        pop();
                        return;
                    }
                    AppDetailInfo info = AppDetailInfo.create(data);
                    appDetailInfo = info;

//                    Glide.with(context).load(appDetailInfo.getIconUrl()).into(icon);
//                    Glide.with(context)
//                            .asBitmap()
//                            .load(appDetailInfo.getIconUrl())
//                            .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 8)))
//                            .into(new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                    appBarLayout.setBackground(new BitmapDrawable(getResources(), resource));
//                                    getColor(resource);
//                                }
//                            });

                    Glide.with(context).asBitmap().load(appDetailInfo.getIconUrl()).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            icon.setImageBitmap(resource);
                            Log.d(TAG, "resource=" + resource);
                            Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
                                Bitmap bitmap = Blurred.with(resource)
//                                        .recycleOriginal(true)
                                        .percent(0.2f)
                                        .scale(0.3f)
                                        .blur();
                                emitter.onNext(bitmap);
                                Log.d(TAG, "bitmap=" + bitmap);
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

//                    Glide.with(context).load(appDetailInfo.getIconUrl()).into(icon);
                    title.setText(info.getName());
                    shortInfo.setText(info.getBaseInfo());
                    shortIntroduce.setText(info.getLineInfo());
                    stateLayout.showContentView();
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(info);
                        }
                    }, 50);
                })
                .onError(throwable -> stateLayout.showErrorView(throwable.getMessage()))
                .subscribe();
    }

    public void getColor(Bitmap bitmap) {
        // Palette的部分
        Palette.Builder builder = Palette.from(bitmap);
        builder.generate(palette -> {
            //获取到充满活力的这种色调
//                Palette.Swatch s = palette.getMutedSwatch();
            //获取图片中充满活力的色调
//                Palette.Swatch s = palette.getVibrantSwatch();
            Palette.Swatch s = palette.getDominantSwatch();//独特的一种
            Palette.Swatch s1 = palette.getVibrantSwatch();       //获取到充满活力的这种色调
            Palette.Swatch s2 = palette.getDarkVibrantSwatch();    //获取充满活力的黑
            Palette.Swatch s3 = palette.getLightVibrantSwatch();   //获取充满活力的亮
            Palette.Swatch s4 = palette.getMutedSwatch();           //获取柔和的色调
            Palette.Swatch s5 = palette.getDarkMutedSwatch();      //获取柔和的黑
            Palette.Swatch s6 = palette.getLightMutedSwatch();    //获取柔和的亮
            Log.d(TAG, "s=" + s);
            Log.d(TAG, "s1=" + s1);
            Log.d(TAG, "s2=" + s2);
            Log.d(TAG, "s3=" + s3);
            Log.d(TAG, "s4=" + s4);
            Log.d(TAG, "s5=" + s5);
            Log.d(TAG, "s6=" + s6);
            if (s != null) {
//                post(() -> {
//
//                });

                boolean isDark = ColorUtils.calculateLuminance(s.getRgb()) <= 0.5;
                ;
                if (isDark) {
                    lightStatusBar();
                } else {
                    darkStatusBar();
                }
//                AToast.success("isDark=" + isDark);
                int color = getResources().getColor(isDark ? R.color.white : R.color.color_text_major);
                title.setTextColor(color);
                shortInfo.setTextColor(color);
                shortIntroduce.setTextColor(color);
                toolbar.setLightStyle(isDark);
            }

//                //根据调色板Palette获取到图片中的颜色设置到toolbar和tab中背景，标题等，使整个UI界面颜色统一
//                if (rootView != null) {
//                    if (vibrant != null) {
//                        ValueAnimator colorAnim2 = ValueAnimator.ofArgb(Color.rgb(110, 110, 100), ColorHelper.colorBurn(vibrant.getRgb()));
//                        colorAnim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                            @Override
//                            public void onAnimationUpdate(ValueAnimator animation) {
//                                rootView.setBackgroundColor((Integer) animation.getAnimatedValue());
//                                // toolbar.setBackgroundColor((Integer) animation.getAnimatedValue());
//                            }
//                        });
//                        colorAnim2.setDuration(300);
//                        colorAnim2.setRepeatMode(ValueAnimator.RESTART);
//                        colorAnim2.start();
//
//                        if (Build.VERSION.SDK_INT >= 21) {
//                            Window window = getActivity().getWindow();
//                            window.setStatusBarColor(ColorHelper.colorBurn(vibrant.getRgb()));
//                            int barColor = ColorHelper.colorBurn(vibrant.getRgb());
//                            window.setNavigationBarColor(barColor);
//                        }
//                    }
//                }

        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFabEvent(FabEvent event) {
        if (event.isShow()) {
            fabComment.show();
        } else {
            fabComment.hide();
        }
    }


}
