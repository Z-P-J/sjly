package com.zpj.shouji.market.ui.fragment.detail;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.zpj.fragmentation.dialog.impl.AttachListDialogFragment;
import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.BookingApi;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.AppUpdateInfo;
import com.zpj.shouji.market.model.BookingAppInfo;
import com.zpj.shouji.market.model.CollectionAppInfo;
import com.zpj.shouji.market.model.GuessAppInfo;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.model.QuickAppInfo;
import com.zpj.shouji.market.model.UserDownloadedAppInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.fragment.base.StateSwipeBackFragment;
import com.zpj.shouji.market.ui.fragment.dialog.AppCommentDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.AppUrlCenterListDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.CommentDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.ShareDialogFragment;
import com.zpj.shouji.market.ui.fragment.login.LoginFragment;
import com.zpj.shouji.market.ui.fragment.manager.ManagerFragment;
import com.zpj.shouji.market.ui.widget.AppDetailLayout;
import com.zpj.shouji.market.ui.widget.indicator.SubTitlePagerTitle;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.toast.ZToast;
import com.zpj.utils.Callback;
import com.zxy.skin.sdk.SkinEngine;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;

public class AppDetailFragment extends StateSwipeBackFragment
        implements View.OnClickListener {

    private static final String TAG = "AppDetailFragment";

    private static final String BOOKING = "key_booking";
    private static final String AUTO_DOWNLOAD = "key_auto_download";

    private static final String[] TAB_TITLES = {"详情", "评论", "发现", "推荐"};

    private String id;
    private String type;
    private boolean isBooking;
    private boolean isAutoDownload;

//    private StateLayout stateLayout;
    private FloatingActionButton fabComment;

    private ImageButton btnShare;
    private ImageButton btnCollect;
    //    private LikeButton btnCollect;
    private ImageButton btnMenu;

    private AppDetailLayout appDetailLayout;
    private ViewPager viewPager;
    private MagicIndicator magicIndicator;

    private CommentDialogFragment commentDialogFragment;

    private AppDetailInfo info;


    public static void start(String type, String id) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, id);
        args.putString(Keys.TYPE, type);
        AppDetailFragment fragment = new AppDetailFragment();
        fragment.setArguments(args);
        start(fragment);
    }

    public static void start(BookingAppInfo appInfo) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, appInfo.getAppId());
        args.putString(Keys.TYPE, appInfo.getAppType());
        args.putBoolean(BOOKING, appInfo.isBooking());
        args.putBoolean(AUTO_DOWNLOAD, appInfo.isAutoDownload());
        AppDetailFragment fragment = new AppDetailFragment();
        fragment.setArguments(args);
        start(fragment);
    }

    public static void start(AppInfo item) {
        start(item.getAppType(), item.getAppId());
    }

    public static void start(GuessAppInfo item) {
        start(item.getAppType(), item.getAppId());
    }

    public static void start(QuickAppInfo item) {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.onFabEvent(this, new RxBus.SingleConsumer<Boolean>() {
            @Override
            public void onAccept(Boolean show) throws Exception {
                if (show) {
                    fabComment.show();
                } else {
                    fabComment.hide();
                }
            }
        });
    }

    @Override
    protected void initStatusBar() {
        if (isLazyInit() || AppConfig.isNightMode()) {
            lightStatusBar();
        } else {
            darkStatusBar();
        }
    }

    @Override
    public void onDestroy() {
        commentDialogFragment = null;
        EventBus.removeGetAppInfoEvent();
        super.onDestroy();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            type = getArguments().getString(Keys.TYPE);
            id = getArguments().getString(Keys.ID);
            isBooking = getArguments().getBoolean(BOOKING);
            isAutoDownload = getArguments().getBoolean(AUTO_DOWNLOAD);
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

        fabComment = view.findViewById(R.id.fab_comment);
        fabComment.setImageResource(R.drawable.ic_file_download_white_24dp);
        fabComment.setOnClickListener(this);

        appDetailLayout = view.findViewById(R.id.layout_app_detail);
        appDetailLayout.bindToolbar(toolbar);


        viewPager = appDetailLayout.getViewPager();
        magicIndicator = appDetailLayout.getMagicIndicator();

        getAppInfo();

    }

    @Override
    protected void onRetry() {
        super.onRetry();
        getAppInfo();
    }

    private void initViewPager(AppDetailInfo info) {
        ArrayList<Fragment> list = new ArrayList<>();
//        AppDetailInfoFragment infoFragment = findChildFragment(AppDetailInfoFragment.class);
//        if (infoFragment == null) {
//            infoFragment = new AppDetailInfoFragment();
//        }
        AppDetailContentFragment infoFragment = findChildFragment(AppDetailContentFragment.class);
        if (infoFragment == null) {
            infoFragment = new AppDetailContentFragment();
        }

        AppDetailCommentFragment commentFragment = findChildFragment(AppDetailCommentFragment.class);
        if (commentFragment == null) {
            commentFragment = AppDetailCommentFragment.newInstance(info);
        }

        AppDetailThemeFragment exploreFragment = findChildFragment(AppDetailThemeFragment.class);
        if (exploreFragment == null) {
            exploreFragment = AppDetailThemeFragment.newInstance(id, type);
        }

        AppDetailRecommendFragment recommendFragment = findChildFragment(AppDetailRecommendFragment.class);
        if (recommendFragment == null) {
            recommendFragment = AppDetailRecommendFragment.newInstance(id, type);
        }
        list.add(infoFragment);
        list.add(commentFragment);
        list.add(exploreFragment);
        list.add(recommendFragment);

        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);
        viewPager.setOffscreenPageLimit(list.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    if (fabComment.getVisibility() != View.VISIBLE) {
                        fabComment.setImageResource(R.drawable.ic_file_download_white_24dp);
                        fabComment.show();
                    } else {
                        fabComment.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                            @Override
                            public void onHidden(FloatingActionButton fab) {
                                super.onHidden(fab);
                                fabComment.setImageResource(R.drawable.ic_file_download_white_24dp);
                                fabComment.show();
                            }
                        });
                    }
                } else if (i == 1) {
                    if (fabComment.getVisibility() != View.VISIBLE) {
                        fabComment.setImageResource(R.drawable.ic_comment_white_24dp);
                        fabComment.show();
                    } else {
                        fabComment.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                            @Override
                            public void onHidden(FloatingActionButton fab) {
                                super.onHidden(fab);
                                fabComment.setImageResource(R.drawable.ic_comment_white_24dp);
                                fabComment.show();
                            }
                        });
                    }
                } else {
                    fabComment.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                        @Override
                        public void onHidden(FloatingActionButton fab) {
                            super.onHidden(fab);
                            fabComment.setImageDrawable(null);
                        }
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setAdapter(adapter);

        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES, true);

        MagicIndicatorHelper.builder(context)
                .setMagicIndicator(magicIndicator)
                .setViewPager(viewPager)
                .setTabTitles(TAB_TITLES)
                .setAdjustMode(true)
                .setOnGetTitleViewListener((context12, index) -> {

                    SubTitlePagerTitle subTitlePagerTitle = new SubTitlePagerTitle(context12);
                    subTitlePagerTitle.setNormalColor(SkinEngine.getColor(context12, R.attr.textColorMajor));
                    subTitlePagerTitle.setSelectedColor(context12.getResources().getColor(R.color.colorPrimary));
                    subTitlePagerTitle.setTitle(TAB_TITLES[index]);
                    subTitlePagerTitle.setOnClickListener(view1 -> viewPager.setCurrentItem(index));
                    if (index == 1) {
                        subTitlePagerTitle.setSubCount(info.getReviewCount());
                    } else if (index == 2) {
                        subTitlePagerTitle.setSubCount(info.getDiscoverCount());
                    } else if (index == 3) {
                        subTitlePagerTitle.setSubText("20+");
                    }
                    return subTitlePagerTitle;

//                    BadgePagerTitle badgePagerTitle = new BadgePagerTitle(context);
//                    badgePagerTitle.setNormalColor(SkinEngine.getColor(context12, R.attr.textColorMajor));
//                    badgePagerTitle.setSelectedColor(context12.getResources().getColor(R.color.colorPrimary));
//                    badgePagerTitle.setTitle(TAB_TITLES[index]);
//                    badgePagerTitle.setOnClickListener(view1 -> viewPager.setCurrentItem(index));
//                    if (index == 1 || index == 2) {
//                        String title = TAB_TITLES[index];
//                        RxBus.observeSticky(title, title, Integer.class)
//                                .bindToLife(AppDetailFragment.this)
//                                .doOnNext(new RxBus.SingleConsumer<Integer>() {
//                                    @Override
//                                    public void onAccept(Integer integer) throws Exception {
//                                        badgePagerTitle.setBadgeCount(integer);
//                                        RxBus.removeStickyEvent(title);
//                                    }
//                                })
//                                .subscribe();
//                    } else if (index == 3) {
//                        badgePagerTitle.setBadgeText("20+");
//                    }
//                    return badgePagerTitle;

                })
                .build();

    }

    private void getAppInfo() {
        HttpApi.appInfoApi(type, id)
                .onSuccess(data -> {
                    postOnEnterAnimationEnd(() -> {
                        Log.d("getAppInfo", "data=" + data);
                        if ("NoApp".equals(data.selectFirst("errorcode").text())) {
                            ZToast.warning("应用不存在");
                            pop();
                            return;
                        }
                        info = AppDetailInfo.create(data);
//                        RxBus.postSticky(TAB_TITLES[1], info.getReviewCount());
//                        RxBus.postSticky(TAB_TITLES[2], info.getDiscoverCount());
                        Log.d("getAppInfo", "info=" + info);
                        appDetailLayout.loadInfo(info);
                        int color = Color.WHITE;
                        toolbar.setLightStyle(true);
                        btnMenu.setColorFilter(color);
                        btnShare.setColorFilter(color);


                        if (info.isFavState()) {
                            btnCollect.setImageResource(R.drawable.ic_star_black_24dp);
                            btnCollect.setColorFilter(Color.RED);
                        } else {
                            btnCollect.setImageResource(R.drawable.ic_star_border_black_24dp);
                            btnCollect.setColorFilter(color);
                        }
                        for (String img : info.getImgUrlList()) {
                            Glide.with(context).load(img).preload();
                        }

                        initViewPager(info);
                        EventBus.sendGetAppInfoEvent(info);
                        showContent();
                        lightStatusBar();
                    });
                })
                .onError(throwable -> {
//                    stateLayout.showErrorView(throwable.getMessage());
                    showError(throwable.getMessage());
                })
                .subscribe();
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onFabEvent(FabEvent event) {
//        if (event.isShow()) {
//            fabComment.show();
//        } else {
//            fabComment.hide();
//        }
//    }

    @Override
    public void onClick(View v) {
        if (v == btnMenu) {
            showMenu();
        } else if (v == fabComment) {
            onFabClicked();
        } else if (v == btnShare) {
            new ShareDialogFragment()
                    .setShareContent(getString(R.string.text_app_share_content, info.getName(), info.getId()))
                    .show(context);
        } else if (v == btnCollect) {
            if (!UserManager.getInstance().isLogin()) {
                ZToast.warning(R.string.text_msg_not_login);
                LoginFragment.start();
                return;
            }
            Callback<Boolean> callback = result -> {
                if (result) {
                    btnCollect.setImageResource(R.drawable.ic_star_black_24dp);
                    btnCollect.setColorFilter(Color.RED);
                } else {
                    btnCollect.setImageResource(R.drawable.ic_star_border_black_24dp);
                    btnCollect.setColorFilter(Color.WHITE);
                }
                info.setFavState(result);
            };
            if (info.isFavState()) {
                HttpApi.cancelAppFavoriteApi(info.getId(), info.getAppType(), callback);
            } else {
                HttpApi.appFavoriteApi(info.getId(), info.getAppType(), callback);
            }
        }
    }

    private void showMenu() {
        new AttachListDialogFragment<String>()
                .addItems("下载管理", "复制包名", "浏览器中打开")
                .addItemIf(UserManager.getInstance().isLogin(), "添加到应用集")
                .setOnSelectListener((fragment, position, title) -> {
                    switch (position) {
                        case 0:
                            ManagerFragment.start();
                            break;
                        case 1:
                            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            cm.setPrimaryClip(ClipData.newPlainText(null, info.getPackageName()));
                            ZToast.success("已复制到粘贴板");
                            break;
                        case 2:
                            WebFragment.appPage(type, id);
                            break;
                        case 3:
                            ZToast.normal("TODO " + title);
                            break;
                    }
                    fragment.dismiss();
                })
                .setAttachView(btnMenu)
                .show(this);
    }

    private void onFabClicked() {
        if (viewPager.getCurrentItem() == 0) {
            if (TextUtils.isEmpty(info.getPackageName())) {
                if (isBooking) {
                    BookingAppInfo appInfo = new BookingAppInfo();
                    appInfo.setAppId(id);
                    BookingApi.bookingApi(appInfo, new Runnable() {
                        @Override
                        public void run() {
                            isBooking = false;
                        }
                    });
                } else {
                    ZToast.warning("已预约，应用上架后将及时通知您");
                }
            } else {
                new AppUrlCenterListDialogFragment()
                        .setAnchorView(fabComment)
                        .setAppDetailInfo(info)
                        .show(context);
            }
        } else {
            if (!UserManager.getInstance().isLogin()) {
                ZToast.warning(R.string.text_msg_not_login);
                LoginFragment.start();
                return;
            }
            fabComment.hide();
            setSwipeBackEnable(false);

            if (commentDialogFragment == null) {
                commentDialogFragment = AppCommentDialogFragment.with(context, id, type, "", () -> commentDialogFragment = null);
                commentDialogFragment.setOnDismissListener(() -> {
                    setSwipeBackEnable(true);
                    fabComment.show();
                    EventBus.sendRefreshEvent();
                });
            }
            commentDialogFragment.show(context);
        }
    }


}
