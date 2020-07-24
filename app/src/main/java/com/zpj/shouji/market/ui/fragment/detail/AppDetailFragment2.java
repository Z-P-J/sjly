package com.zpj.shouji.market.ui.fragment.detail;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.felix.atoast.library.AToast;
import com.shehuan.niv.NiceImageView;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.popup.ZPopup;
import com.zpj.popup.impl.AttachListPopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.FabEvent;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.AppUpdateInfo;
import com.zpj.shouji.market.model.CollectionAppInfo;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.model.UserDownloadedAppInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.fragment.manager.AppManagerFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.widget.JudgeNestedScrollView;
import com.zpj.shouji.market.ui.widget.popup.AppCommentPopup;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.utils.ScreenUtils;
import com.zpj.utils.StatusBarUtils;
import com.zpj.widget.statelayout.StateLayout;
import com.zpj.widget.tinted.TintedImageButton;
import com.zpj.widget.tinted.TintedImageView;

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
import per.goweii.burred.Blurred;
import top.defaults.drawabletoolbox.DrawableBuilder;

public class AppDetailFragment2 extends BaseFragment
        implements View.OnClickListener,
        NestedScrollView.OnScrollChangeListener {

    private static final String TAG = "AppDetailFragment";

    private static final String[] TAB_TITLES = {"详情", "评论", "发现", "推荐"};

    private static final String SOFT_URL = "http://tt.shouji.com.cn/androidv3/soft_show.jsp?id=";
    private static final String GAME_URL = "http://tt.shouji.com.cn/androidv3/game_show.jsp?id=";
    private static final String URL = "http://tt.shouji.com.cn/androidv3/%s_show.jsp?id=%s";

    private String id;
    private String type;

    private AppDetailInfo appDetailInfo;
    private StateLayout stateLayout;
    private ImageView ivHeader;
    private NiceImageView icon;
    private TextView title;
    private TextView tvVersion;
    private TextView tvSize;
    private TextView shortInfo;
    private TextView shortIntroduce;
    private FloatingActionButton fabComment;

    private ViewPager viewPager;
    private MagicIndicator magicIndicator;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar mToolBar;
    private JudgeNestedScrollView scrollView;
    private View buttonBarLayout;
    private NiceImageView ivToolbarAvater;
    private TextView tvToolbarName;

    private TintedImageButton btnShare;
    private TintedImageButton btnCollect;
    private TintedImageButton btnMenu;

    int toolBarPositionY = 0;
    private int mOffset = 0;
    private int mScrollY = 0;

    private AppDetailInfo info;


    public static void start(String type, String id) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, id);
        args.putString(Keys.TYPE, type);
        AppDetailFragment2 fragment = new AppDetailFragment2();
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
        return R.layout.fragment_app_detail2;
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
            type = getArguments().getString(Keys.TYPE);
            id = getArguments().getString(Keys.ID);
        } else {
            pop();
            return;
        }

        btnShare = toolbar.getRightCustomView().findViewById(R.id.btn_share);
        btnCollect = toolbar.getRightCustomView().findViewById(R.id.btn_collect);
        btnMenu = toolbar.getRightCustomView().findViewById(R.id.btn_menu);

        btnShare.setOnClickListener(this);
        btnCollect.setOnClickListener(this);
        btnMenu.setOnClickListener(this);

        stateLayout = view.findViewById(R.id.state_layout);
        ivHeader = view.findViewById(R.id.iv_header);
        icon = view.findViewById(R.id.iv_icon);
        title = view.findViewById(R.id.tv_title);
        tvVersion = view.findViewById(R.id.tv_version);
        tvSize = view.findViewById(R.id.tv_size);
        shortInfo = view.findViewById(R.id.tv_info);
        shortIntroduce = view.findViewById(R.id.tv_detail);

        fabComment = view.findViewById(R.id.fab_comment);
//        fabComment.hide();
        fabComment.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() == 0) {
                AToast.normal("TODO Download");
            } else {
                AppCommentPopup.with(context, id, type, "").show();
            }
        });

        collapsingToolbarLayout = view.findViewById(R.id.collapse);
        mToolBar = view.findViewById(R.id.layout_toolbar);
        scrollView = view.findViewById(R.id.scroll_view);
        buttonBarLayout = toolbar.getCenterCustomView();
        ivToolbarAvater = view.findViewById(R.id.toolbar_avatar);
        tvToolbarName = view.findViewById(R.id.toolbar_name);


//        ArrayList<Fragment> list = new ArrayList<>();
//        AppInfoFragment infoFragment = findChildFragment(AppInfoFragment.class);
//        if (infoFragment == null) {
//            infoFragment = new AppInfoFragment();
//        }
//
//        AppCommentFragment commentFragment = findChildFragment(AppCommentFragment.class);
//        if (commentFragment == null) {
//            commentFragment = AppCommentFragment.newInstance(id, type);
//        }
//
//        AppThemeFragment exploreFragment = findChildFragment(AppThemeFragment.class);
//        if (exploreFragment == null) {
//            exploreFragment = AppThemeFragment.newInstance(id, type);
//        }
//
//        AppRecommendFragment recommendFragment = findChildFragment(AppRecommendFragment.class);
//        if (recommendFragment == null) {
//            recommendFragment = AppRecommendFragment.newInstance(id);
//        }
//        list.add(infoFragment);
//        list.add(commentFragment);
//        list.add(exploreFragment);
//        list.add(recommendFragment);
//
//        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);
        viewPager = view.findViewById(R.id.view_pager);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int i, float v, int i1) {
//
//            }
//
//            @Override
//            public void onPageSelected(int i) {
//                if (i == 1) {
//                    fabComment.show();
//                } else {
//                    fabComment.hide();
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {
//
//            }
//        });
//        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(list.size());
        magicIndicator = view.findViewById(R.id.magic_indicator);

//        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES, true);


        mToolBar.post(this::dealWithViewPager);

        buttonBarLayout.setAlpha(0);
        mToolBar.setBackgroundColor(0);

        postDelayed(() -> stateLayout.showLoadingView(), 5);

        getAppInfo();


//        CommonNavigator navigator = new CommonNavigator(getContext());
//        navigator.setAdjustMode(true);
//        navigator.setAdapter(new CommonNavigatorAdapter() {
//            @Override
//            public int getCount() {
//                return TAB_TITLES.length;
//            }
//
//            @Override
//            public IPagerTitleView getTitleView(Context context, int index) {
//                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
//                titleView.setNormalColor(Color.GRAY);
//                titleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
//                titleView.setTextSize(14);
//                titleView.setText(TAB_TITLES[index]);
//                titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index));
//                return titleView;
//            }
//
//            @Override
//            public IPagerIndicator getIndicator(Context context) {
//                LinePagerIndicator indicator = new LinePagerIndicator(context);
//                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
//                indicator.setLineHeight(ScreenUtils.dp2px(context, 4f));
//                indicator.setLineWidth(ScreenUtils.dp2px(context, 12f));
//                indicator.setRoundRadius(ScreenUtils.dp2px(context, 4f));
//                indicator.setColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimary));
//                return indicator;
//            }
//        });
//        magicIndicator.setNavigator(navigator);
//        ViewPagerHelper.bind(magicIndicator, viewPager);


    }

    private void initViewPager() {
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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    fabComment.setImageResource(R.drawable.ic_file_download_white_24dp);
                    fabComment.show();
                } else if (i == 1) {
                    fabComment.setImageResource(R.drawable.ic_comment_white_24dp);
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

        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES, true);
    }

    private void dealWithViewPager() {
        toolBarPositionY = mToolBar.getHeight();
        ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        params.height = ScreenUtils.getScreenHeight(context) - toolBarPositionY - magicIndicator.getHeight() + 1; // + 1
        Log.d(TAG, "toolBarPositionY=" + toolBarPositionY + " magicIndicator.getHeight=" + magicIndicator.getHeight() + " params.height=" + params.height);
        viewPager.setLayoutParams(params);
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);

    }

    private void getAppInfo() {
        String url = String.format(URL, type, id);
        Log.d("apppppp", url);
        HttpApi.get(url)
                .onSuccess(data -> {
                    Log.d("getAppInfo", "data=" + data);
                    if ("NoApp".equals(data.selectFirst("errorcode").text())) {
                        AToast.warning("应用不存在");
                        pop();
                        return;
                    }
                    postOnEnterAnimationEnd(() -> {
                        initViewPager();
                        info = AppDetailInfo.create(data);
                        Log.d("getAppInfo", "info=" + info);
                        appDetailInfo = info;

                        Glide.with(context).asBitmap().load(appDetailInfo.getIconUrl()).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                icon.setImageBitmap(resource);
                                ivToolbarAvater.setImageBitmap(resource);
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
//                                            ivHeader.setBackground(new BitmapDrawable(getResources(), bitmap));
                                            ivHeader.setImageBitmap(bitmap);
                                            getColor(bitmap);

                                        })
                                        .subscribe();
                            }
                        });

                        title.setText(info.getName());
                        tvVersion.setText(info.getVersion());
                        tvSize.setText(info.getSize());
                        tvToolbarName.setText(info.getName());
                        shortInfo.setText(info.getLanguage() + " | " + info.getFee()
                                + " | " + info.getAds() + " | " + info.getFirmware());
                        shortIntroduce.setText(info.getLineInfo());
                        postDelayed(() -> EventBus.getDefault().post(info), 50);
                    });
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
                    tvVersion.setBackground(new DrawableBuilder()
                            .rectangle()
                            .rounded()
                            .strokeColor(getResources().getColor(R.color.colorPrimary))
                            .solidColor(getResources().getColor(R.color.colorPrimary))
                            .build());
                    tvSize.setBackground(new DrawableBuilder()
                            .rectangle()
                            .rounded()
                            .strokeColor(getResources().getColor(R.color.light_blue1))
                            .solidColor(getResources().getColor(R.color.light_blue1))
                            .build());
//                    shortIntroduce.setBackgroundResource(R.drawable.bg_round_black_alpha);
                } else {
                    darkStatusBar();
                    tvVersion.setBackground(new DrawableBuilder()
                            .rectangle()
                            .rounded()
                            .solidColor(getResources().getColor(R.color.teal_2))
                            .build());
                    tvSize.setBackground(new DrawableBuilder()
                            .rectangle()
                            .rounded()
                            .solidColor(getResources().getColor(R.color.blue_gray))
                            .build());
//                    shortIntroduce.setBackgroundResource(R.drawable.bg_round_white_alpha);
                }
//                AToast.success("isDark=" + isDark);
                int color = getResources().getColor(isDark ? R.color.white : R.color.color_text_major);
                title.setTextColor(color);
                tvVersion.setTextColor(color);
                tvSize.setTextColor(color);
                tvToolbarName.setTextColor(color);
                shortInfo.setTextColor(color);
                shortIntroduce.setTextColor(color);
                toolbar.setLightStyle(isDark);
                btnMenu.setTint(color);
                btnCollect.setTint(color);
                btnShare.setTint(color);
            }
            stateLayout.showContentView();
            scrollView.setOnScrollChangeListener(AppDetailFragment2.this);
            scrollView.setOnNeedScrollListener(new JudgeNestedScrollView.OnNeedScrollListener() {
                @Override
                public boolean needScroll() {
                    int[] location = new int[2];
                    magicIndicator.getLocationOnScreen(location);
                    int yPosition = location[1];
                    return yPosition > toolBarPositionY;
                }
            });
//            postDelayed(() -> EventBus.getDefault().post(info), 50);
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


    private int lastScrollY = 0;
    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        Log.d("onScrollChange", "scrollX=" + scrollX + " scrollY=" + scrollY + " oldScrollX=" + oldScrollX + " oldScrollY=" + oldScrollY);
        int[] location = new int[2];
        magicIndicator.getLocationOnScreen(location);
        int yPosition = location[1];
        Log.d("onScrollChange", "yPosition=" + yPosition + " getStatusBarHeight=" + StatusBarUtils.getStatusBarHeight(context) + " location[1]=" + location[1] + " toolBarPositionY=" + toolBarPositionY);
//        if (yPosition <= toolBarPositionY) {
//            scrollView.setNeedScroll(false);
//        } else {
//            scrollView.setNeedScroll(true);
//        }

        int h = ScreenUtils.dp2pxInt(context, 100);
        if (lastScrollY < h) {
            scrollY = Math.min(h, scrollY);
            mScrollY = Math.min(scrollY, h);
            float alpha = 1f * mScrollY / h;
            buttonBarLayout.setAlpha(alpha);
            collapsingToolbarLayout.setAlpha(1 - alpha);
//            mToolBar.setBackgroundColor(((255 * mScrollY / h) << 24) | (ContextCompat.getColor(context, R.color.colorPrimary) & 0x00ffffff));
//            ivHeader.setTranslationY(mOffset - mScrollY);
        }
        lastScrollY = scrollY;
    }

    @Override
    public void onClick(View v) {
        if (v == btnMenu) {
            AttachListPopup<String> popup = ZPopup.attachList(context)
                    .addItems("下载管理", "复制包名", "浏览器中打开");
            if (UserManager.getInstance().isLogin()) {
                popup.addItem("添加到应用集");
            }
            popup.setOnSelectListener((position, title) -> {
                AToast.normal(title);
                switch (position) {
                    case 0:
                        AppManagerFragment.start();
                        break;
                    case 1:
                        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setPrimaryClip(ClipData.newPlainText(null, info.getPackageName()));
                        AToast.success("已复制到粘贴板");
                        break;
                    case 2:
                        WebFragment.appPage(type, id);
                        AToast.normal("TODO 分享主页");
                        break;
                    case 3:

                        break;
                    case 4:
                        break;
                }
            })
                    .show(v);
        }
    }
}
