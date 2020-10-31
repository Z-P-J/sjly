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

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.dialog.impl.AttachListDialogFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.BookingApi;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.FabEvent;
import com.zpj.shouji.market.event.StartFragmentEvent;
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
import com.zpj.shouji.market.ui.fragment.dialog.AppCommentDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.AppUrlCenterListDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.CommentDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.ShareDialogFragment;
import com.zpj.shouji.market.ui.fragment.login.LoginFragment;
import com.zpj.shouji.market.ui.fragment.manager.ManagerFragment;
import com.zpj.shouji.market.ui.widget.AppDetailLayout;
import com.zpj.shouji.market.utils.Callback;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.widget.statelayout.StateLayout;
import com.zpj.widget.tinted.TintedImageButton;

import net.lucode.hackware.magicindicator.MagicIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class AppDetailFragment extends BaseFragment
        implements View.OnClickListener {

    private static final String TAG = "AppDetailFragment";

    private static final String BOOKING = "key_booking";
    private static final String AUTO_DOWNLOAD = "key_auto_download";

    private static final String[] TAB_TITLES = {"详情", "评论", "发现", "推荐"};

    private String id;
    private String type;
    private boolean isBooking;
    private boolean isAutoDownload;

    private StateLayout stateLayout;
    private FloatingActionButton fabComment;

    private TintedImageButton btnShare;
    private TintedImageButton btnCollect;
    //    private LikeButton btnCollect;
    private TintedImageButton btnMenu;

    private AppDetailLayout appDetailLayout;
    private ViewPager viewPager;
    private MagicIndicator magicIndicator;

    //    private CommentPopup commentPopup;
    private CommentDialogFragment commentDialogFragment;

    private AppDetailInfo info;


    public static void start(String type, String id) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, id);
        args.putString(Keys.TYPE, type);
        AppDetailFragment fragment = new AppDetailFragment();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    public static void start(BookingAppInfo appInfo) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, appInfo.getAppId());
        args.putString(Keys.TYPE, appInfo.getAppType());
        args.putBoolean(BOOKING, appInfo.isBooking());
        args.putBoolean(AUTO_DOWNLOAD, appInfo.isAutoDownload());
        AppDetailFragment fragment = new AppDetailFragment();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
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
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (isLazyInit()) {
            lightStatusBar();
        } else {
            darkStatusBar();
        }
    }

//    @Override
//    public void onSupportInvisible() {
//        super.onSupportInvisible();
//        postOnSupportVisible(this::lightStatusBar);
//    }

    @Override
    public void onDestroy() {
        commentDialogFragment = null;
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        commentPopup = null;
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

        stateLayout = view.findViewById(R.id.state_layout);


        fabComment = view.findViewById(R.id.fab_comment);
//        fabComment.show(true);
        fabComment.setImageResource(R.drawable.ic_file_download_white_24dp);
        fabComment.setOnClickListener(this);

        appDetailLayout = view.findViewById(R.id.layout_app_detail);
        appDetailLayout.bindToolbar(toolbar);


        viewPager = appDetailLayout.getViewPager();
        magicIndicator = appDetailLayout.getMagicIndicator();

        stateLayout.showLoadingView();
        getAppInfo();

    }

    private void initViewPager() {
        ArrayList<Fragment> list = new ArrayList<>();
        AppDetailInfoFragment infoFragment = findChildFragment(AppDetailInfoFragment.class);
        if (infoFragment == null) {
            infoFragment = new AppDetailInfoFragment();
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
        viewPager.setOffscreenPageLimit(list.size());

        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES, true);
    }

    private void getAppInfo() {
        HttpApi.appInfoApi(type, id)
                .onSuccess(data -> {
                    postOnEnterAnimationEnd(() -> {
                        Log.d("getAppInfo", "data=" + data);
                        if ("NoApp".equals(data.selectFirst("errorcode").text())) {
                            AToast.warning("应用不存在");
                            pop();
                            return;
                        }
                        info = AppDetailInfo.create(data);
                        Log.d("getAppInfo", "info=" + info);
                        appDetailLayout.loadInfo(info);
                        int color = Color.WHITE;
                        toolbar.setLightStyle(true);
                        btnMenu.setTint(color);
                        btnShare.setTint(color);


                        if (info.isFavState()) {
                            btnCollect.setImageResource(R.drawable.ic_star_black_24dp);
                            btnCollect.setTint(Color.RED);
                        } else {
                            btnCollect.setImageResource(R.drawable.ic_star_border_black_24dp);
                            btnCollect.setTint(color);
                        }
                        for (String img : info.getImgUrlList()) {
                            Glide.with(context).load(img).preload();
                        }


                        initViewPager();
                        postDelayed(() -> EventBus.getDefault().post(info), 50);
                        stateLayout.showContentView();
                        lightStatusBar();
                    });
                })
                .onError(throwable -> stateLayout.showErrorView(throwable.getMessage()))
                .subscribe();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFabEvent(FabEvent event) {
        if (event.isShow()) {
            fabComment.show();
        } else {
            fabComment.hide();
        }
    }

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
//            SharePopup.with(getContext())
//                    .setShareContent(getContext().getString(R.string.text_app_share_content, info.getName(), info.getId()))
//                    .show();
        } else if (v == btnCollect) {
            if (!UserManager.getInstance().isLogin()) {
                AToast.warning(R.string.text_msg_not_login);
                LoginFragment.start();
                return;
            }
            Callback<Boolean> callback = result -> {
                if (result) {
                    btnCollect.setImageResource(R.drawable.ic_star_black_24dp);
                    btnCollect.setTint(Color.RED);
                } else {
                    btnCollect.setImageResource(R.drawable.ic_star_border_black_24dp);
                    btnCollect.setTint(Color.WHITE);
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
//                            fragment.dismissWithStart(new ManagerFragment());
                            ManagerFragment.start();
                            break;
                        case 1:
                            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            cm.setPrimaryClip(ClipData.newPlainText(null, info.getPackageName()));
                            AToast.success("已复制到粘贴板");
                            break;
                        case 2:
                            WebFragment.appPage(type, id);
                            break;
                        case 3:
                            AToast.normal("TODO " + title);
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
                    AToast.warning("已预约，应用上架后将及时通知您");
                }
            } else {
                new AppUrlCenterListDialogFragment()
                        .setAnchorView(fabComment)
                        .setAppDetailInfo(info)
                        .show(context);
//                AppUrlCenterListPopup.with(context)
//                        .setAppDetailInfo(info)
//                        .show();
            }
        } else {
            if (!UserManager.getInstance().isLogin()) {
                AToast.warning(R.string.text_msg_not_login);
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
                });
            }
            commentDialogFragment.show(context);

//            if (commentPopup == null) {
//                commentPopup = AppCommentPopup.with(context, id, type, "", () -> commentPopup = null)
//                        .setDecorView(findViewById(R.id.fl_container))
//                        .setOnDismissListener(() -> {
//                            setSwipeBackEnable(true);
//                            fabComment.show();
//                        })
//                        .show();
//            }
//            commentPopup.show();
        }
    }


}
