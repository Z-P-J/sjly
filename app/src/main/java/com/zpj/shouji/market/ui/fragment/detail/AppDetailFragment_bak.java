//package com.zpj.shouji.market.ui.fragment.detail;
//
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
//import android.util.Log;
//import android.view.View;
//
//import com.bumptech.glide.Glide;
//import com.felix.atoast.library.AToast;
//import com.github.clans.fab.FloatingActionButton;
//import com.zpj.fragmentation.BaseFragment;
//import com.zpj.popup.ZPopup;
//import com.zpj.popup.interfaces.OnDismissListener;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.api.HttpApi;
//import com.zpj.shouji.market.constant.Keys;
//import com.zpj.shouji.market.event.FabEvent;
//import com.zpj.shouji.market.event.StartFragmentEvent;
//import com.zpj.shouji.market.manager.UserManager;
//import com.zpj.shouji.market.model.AppDetailInfo;
//import com.zpj.shouji.market.model.AppInfo;
//import com.zpj.shouji.market.model.AppUpdateInfo;
//import com.zpj.shouji.market.model.CollectionAppInfo;
//import com.zpj.shouji.market.model.InstalledAppInfo;
//import com.zpj.shouji.market.model.UserDownloadedAppInfo;
//import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
//import com.zpj.shouji.market.ui.fragment.WebFragment;
//import com.zpj.shouji.market.ui.fragment.manager.AppManagerFragment;
//import com.zpj.shouji.market.ui.widget.AppDetailLayout;
//import com.zpj.shouji.market.ui.widget.popup.AppCommentPopup;
//import com.zpj.shouji.market.utils.MagicIndicatorHelper;
//import com.zpj.widget.statelayout.StateLayout;
//import com.zpj.widget.tinted.TintedImageButton;
//
//import net.lucode.hackware.magicindicator.MagicIndicator;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.ArrayList;
//
//public class AppDetailFragment_bak extends BaseFragment
//        implements View.OnClickListener {
//
//    private static final String TAG = "AppDetailFragment";
//
//    private static final String[] TAB_TITLES = {"详情", "评论", "发现", "推荐"};
//
//    private String id;
//    private String type;
//
//    private StateLayout stateLayout;
//    private FloatingActionButton fabComment;
//
//    private TintedImageButton btnShare;
//    private TintedImageButton btnCollect;
//    private TintedImageButton btnMenu;
//
//    private AppDetailLayout appDetailLayout;
//    private ViewPager viewPager;
//    private MagicIndicator magicIndicator;
//
//    private AppDetailInfo info;
//
//
//    public static void start(String type, String id) {
//        Bundle args = new Bundle();
//        args.putString(Keys.ID, id);
//        args.putString(Keys.TYPE, type);
//        AppDetailFragment_bak fragment = new AppDetailFragment_bak();
//        fragment.setArguments(args);
//        StartFragmentEvent.start(fragment);
//    }
//
//    public static void start(AppInfo item) {
//        start(item.getAppType(), item.getAppId());
//    }
//
//    public static void start(AppUpdateInfo item) {
//        start(item.getAppType(), item.getId());
//    }
//
//    public static void start(InstalledAppInfo appInfo) {
//        start(appInfo.getAppType(), appInfo.getId());
//    }
//
//    public static void start(UserDownloadedAppInfo info) {
//        start(info.getAppType(), info.getId());
//    }
//
//    public static void start(CollectionAppInfo info) {
//        start(info.getAppType(), info.getId());
//    }
//
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_app_detail;
//    }
//
//    @Override
//    protected boolean supportSwipeBack() {
//        return true;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Override
//    protected void initView(View view, @Nullable Bundle savedInstanceState) {
//        if (getArguments() != null) {
//            type = getArguments().getString(Keys.TYPE);
//            id = getArguments().getString(Keys.ID);
//        } else {
//            pop();
//            return;
//        }
//
//        btnShare = toolbar.getRightCustomView().findViewById(R.id.btn_share);
//        btnCollect = toolbar.getRightCustomView().findViewById(R.id.btn_collect);
//        btnMenu = toolbar.getRightCustomView().findViewById(R.id.btn_menu);
//
//        btnShare.setOnClickListener(this);
//        btnCollect.setOnClickListener(this);
//        btnMenu.setOnClickListener(this);
//
//        stateLayout = view.findViewById(R.id.state_layout);
//
//
//        fabComment = view.findViewById(R.id.fab_comment);
//        fabComment.setOnClickListener(v -> {
//            if (viewPager.getCurrentItem() == 0) {
//                AToast.normal("TODO Download");
//            } else {
//                fabComment.hide(true);
//                AppCommentPopup.with(context, id, type, "")
//                        .setDecorView(view.findViewById(R.id.fl_container))
//                        .setOnDismissListener(new OnDismissListener() {
//                            @Override
//                            public void onDismiss() {
//                                fabComment.show(true);
//                            }
//                        })
//                        .show();
//            }
//        });
//
//        appDetailLayout = view.findViewById(R.id.layout_app_detail);
//        appDetailLayout.bindToolbar(toolbar);
//
//
//        viewPager = appDetailLayout.getViewPager();
//        magicIndicator = appDetailLayout.getMagicIndicator();
//
//        stateLayout.showLoadingView();
//        getAppInfo();
//
//    }
//
//    private void initViewPager() {
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
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int i, float v, int i1) {
//
//            }
//
//            @Override
//            public void onPageSelected(int i) {
//                if (i == 0) {
//                    fabComment.setImageResource(R.drawable.ic_file_download_white_24dp);
//                    if (fabComment.isHidden()) {
//                        fabComment.show(true);
//                    }
//                } else if (i == 1) {
//                    fabComment.setImageResource(R.drawable.ic_comment_white_24dp);
//                    if (fabComment.isHidden()) {
//                        fabComment.show(true);
//                    }
//                } else {
//                    if (!fabComment.isHidden()) {
//                        fabComment.hide(true);
//                    }
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
//
//        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES, true);
//    }
//
//    @Override
//    public void onEnterAnimationEnd(Bundle savedInstanceState) {
//        super.onEnterAnimationEnd(savedInstanceState);
//
//    }
//
//    private void getAppInfo() {
//        HttpApi.appInfoApi(type, id)
//                .onSuccess(data -> {
//                    Log.d("getAppInfo", "data=" + data);
//                    if ("NoApp".equals(data.selectFirst("errorcode").text())) {
//                        AToast.warning("应用不存在");
//                        pop();
//                        return;
//                    }
//                    info = AppDetailInfo.create(data);
//                    Log.d("getAppInfo", "info=" + info);
//                    appDetailLayout.loadInfo(info);
//                    int color = Color.WHITE;
//                    toolbar.setLightStyle(true);
//                    btnMenu.setTint(color);
//                    btnCollect.setTint(color);
//                    btnShare.setTint(color);
//                    for (String img : info.getImgUrlList()) {
//                        Glide.with(context).load(img).preload();
//                    }
//                    postOnEnterAnimationEnd(() -> {
//                        initViewPager();
//                        postDelayed(() -> EventBus.getDefault().post(info), 50);
//                        lightStatusBar();
//                        stateLayout.showContentView();
//                        fabComment.show(true);
//                    });
//                })
//                .onError(throwable -> stateLayout.showErrorView(throwable.getMessage()))
//                .subscribe();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onFabEvent(FabEvent event) {
//        if (event.isShow()) {
//            if (fabComment.isHidden()) {
//                fabComment.show(true);
//            }
//        } else {
//            if (!fabComment.isHidden()) {
//                fabComment.hide(true);
//            }
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v == btnMenu) {
//            ZPopup.attachList(context)
//                    .addItems("下载管理", "复制包名", "浏览器中打开")
//                    .addItemIf(UserManager.getInstance().isLogin(), "添加到应用集")
//                    .setOnSelectListener((position, title) -> {
//                        AToast.normal(title);
//                        switch (position) {
//                            case 0:
//                                AppManagerFragment.start();
//                                break;
//                            case 1:
//                                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//                                cm.setPrimaryClip(ClipData.newPlainText(null, info.getPackageName()));
//                                AToast.success("已复制到粘贴板");
//                                break;
//                            case 2:
//                                WebFragment.appPage(type, id);
//                                AToast.normal("TODO 分享主页");
//                                break;
//                            case 3:
//
//                                break;
//                            case 4:
//                                break;
//                        }
//                    })
//                    .show(v);
//        }
//    }
//
//
//}
