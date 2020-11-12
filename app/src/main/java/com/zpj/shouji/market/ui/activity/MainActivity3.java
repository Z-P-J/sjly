//package com.zpj.shouji.market.ui.activity;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.StrictMode;
//import android.provider.Settings;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v4.app.ActivityCompat;
//import android.util.Log;
//
//import com.felix.atoast.library.AToast;
//import com.lxj.xpermission.PermissionConstants;
//import com.lxj.xpermission.XPermission;
//import com.yalantis.ucrop.CropEvent;
//import com.zpj.downloader.util.permission.PermissionUtil;
//import com.zpj.fragmentation.BaseFragment;
//import com.zpj.fragmentation.SupportActivity;
//import com.zpj.fragmentation.SupportFragment;
//import com.zpj.fragmentation.anim.DefaultHorizontalAnimator;
//import com.zpj.fragmentation.anim.FragmentAnimator;
//import com.zpj.http.core.IHttp;
//import com.zpj.popup.ZPopup;
//import com.zpj.popup.impl.LoadingPopup;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.api.HttpApi;
//import com.zpj.shouji.market.api.HttpPreLoader;
//import com.zpj.shouji.market.event.HideLoadingEvent;
//import com.zpj.shouji.market.event.IconUploadSuccessEvent;
//import com.zpj.shouji.market.event.MainActionPopupEvent;
//import com.zpj.shouji.market.event.ShowLoadingEvent;
//import com.zpj.shouji.market.event.StartFragmentEvent;
//import com.zpj.shouji.market.event.StatusBarEvent;
//import com.zpj.shouji.market.manager.UserManager;
//import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
//import com.zpj.shouji.market.ui.fragment.homepage.HomeFragment;
//import com.zpj.shouji.market.ui.fragment.profile.MyFragment;
//import com.zpj.shouji.market.ui.fragment.recommond.GameRecommendFragment2;
//import com.zpj.shouji.market.ui.fragment.recommond.SoftRecommendFragment2;
//import com.zpj.shouji.market.ui.widget.navigation.BottomBar;
//import com.zpj.shouji.market.ui.widget.navigation.BottomBarTab;
//import com.zpj.shouji.market.ui.widget.ZViewPager;
//import com.zpj.shouji.market.ui.widget.popup.MainActionPopup;
//import com.zpj.shouji.market.utils.AppUtil;
//import com.zpj.shouji.market.utils.BrightnessUtils;
//import com.zpj.shouji.market.utils.PictureUtil;
//import com.zpj.utils.StatusBarUtils;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.Observable;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.schedulers.Schedulers;
//
//public class MainActivity3 extends SupportActivity {
//
//
//    public static final int FIRST = 0;
//    public static final int SECOND = 1;
//    public static final int THIRD = 2;
//    public static final int FOURTH = 3;
//
//    private SupportFragment[] mFragments = new SupportFragment[4];
//
//    private BottomBar mBottomBar;
//
//    private long firstTime = 0;
//
//    private LoadingPopup loadingPopup;
//
////    private RelativeLayout rlSplash;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        long start = System.currentTimeMillis();
//
//        EventBus.getDefault().register(this);
//
//        setContentView(R.layout.activity_main3);
//
////        rlSplash = findViewById(R.id.rl_splash);
////        rlSplash.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////
////            }
////        });
////        rlSplash.setOnTouchListener((v, event) -> true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//            StrictMode.setVmPolicy(builder.build());
//        }
//
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//        BrightnessUtils.setBrightness(this);
//
//        Log.d("MainActivity", "temptime=" + (System.currentTimeMillis() - start));
//
////        if (!AppConfig.isShowSplash()) {
////            init(false);
////        }
//
////        init(false);
//
//
//    }
//
//    @Override
//    public void onEnterAnimationComplete() {
//        super.onEnterAnimationComplete();
////        if (AppConfig.isShowSplash()) {
////            init(true);
////        }
//        init(false);
//    }
//
//    private void init(boolean isShowSplash) {
//
//        long start = System.currentTimeMillis();
//        if (isShowSplash) {
//            Observable.timer(2000, TimeUnit.MILLISECONDS)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .doOnComplete(() -> {
//                        showRequestPermissionPopup();
////                        rlSplash.setVisibility(View.GONE);
//                    })
//                    .subscribe();
//        } else {
//            showRequestPermissionPopup();
////            rlSplash.setVisibility(View.GONE);
//        }
//
//
//
//
//        UserManager.getInstance().init();
//
//        HttpPreLoader.getInstance().loadHomepage();
//
////        AppUpdateManager.getInstance().checkUpdate(MainActivity2.this);
////
////        AppInstalledManager.getInstance().loadApps(this);
//
//        long temp1 = System.currentTimeMillis();
//        Log.d("MainActivity", "duration111=" + (temp1 - start));
//
//        Context context = this;
//
//        SupportFragment firstFragment = findFragment(HomeFragment.class);
//        if (firstFragment == null) {
//            mFragments[FIRST] = new HomeFragment();
//            mFragments[SECOND] = new SoftRecommendFragment2();
//            mFragments[THIRD] = new GameRecommendFragment2();
//            mFragments[FOURTH] = new MyFragment();
//
//            loadMultipleRootFragment(R.id.fl_container, FIRST,
//                    mFragments[FIRST],
//                    mFragments[SECOND],
//                    mFragments[THIRD],
//                    mFragments[FOURTH]);
//        } else {
//            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题
//
//            // 这里我们需要拿到mFragments的引用
//            mFragments[FIRST] = firstFragment;
//            mFragments[SECOND] = findFragment(SoftRecommendFragment2.class);
//            mFragments[THIRD] = findFragment(GameRecommendFragment2.class);
//            mFragments[FOURTH] = findFragment(MyFragment.class);
//        }
//
//
//
//        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
//
//        mBottomBar = findViewById(R.id.bottom_bar);
//
//        BottomBarTab emptyTab = new BottomBarTab(context);
//        mBottomBar.addItem(BottomBarTab.build(context, "主页", R.drawable.ic_home_normal, R.drawable.ic_home_checked))
//                .addItem(BottomBarTab.build(context, "应用", R.drawable.ic_software_normal, R.drawable.ic_software_checked))
//                .addItem(emptyTab)
//                .addItem(BottomBarTab.build(context, "游戏", R.drawable.ic_game_normal, R.drawable.ic_game_checked))
//                .addItem(BottomBarTab.build(context, "我的", R.drawable.ic_me_normal, R.drawable.ic_me_checked));
//
//        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(int position, int prePosition) {
//                if (position == 2) {
//                    floatingActionButton.performClick();
//                    return;
//                }
//                if (position > 2) {
//                    position -= 1;
//                }
//                if (prePosition > 2) {
//                    prePosition -= 1;
//                }
//                showHideFragment(mFragments[position], mFragments[prePosition]);
//            }
//
//            @Override
//            public void onTabUnselected(int position) {
//
//            }
//
//            @Override
//            public void onTabReselected(int position) {
//            }
//        });
//
//
//        mBottomBar.setCurrentItem(0);
//
//        Log.d("MainActivity", "duration111=" + (System.currentTimeMillis() - temp1));
//    }
//
//    @Override
//    protected void onDestroy() {
//        EventBus.getDefault().unregister(this);
//        HttpPreLoader.getInstance().onDestroy();
//        super.onDestroy();
//    }
//
//    @Override
//    public void onBackPressedSupport() {
//        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
//            pop();
//        } else if (System.currentTimeMillis() - firstTime > 2000) {
//            AToast.warning("再次点击退出！");
//            firstTime = System.currentTimeMillis();
//        } else {
////            finish();
//            ActivityCompat.finishAfterTransition(this);
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == AppUtil.UNINSTALL_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                AToast.success("应用卸载成功！");
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                AToast.normal("应用卸载取消！");
//            }
//        }
//    }
//
//    @Override
//    public FragmentAnimator onCreateFragmentAnimator() {
//        return new DefaultHorizontalAnimator();
//    }
//
//    private void showRequestPermissionPopup() {
//        if (PermissionUtil.checkStoragePermissions(getApplicationContext())) {
//            requestPermission();
//        } else {
//            ZPopup.alert(MainActivity3.this)
//                    .setTitle("权限申请")
//                    .setContent("本软件需要读写手机存储的权限用于文件的下载与查看，是否申请该权限？")
//                    .setConfirmButton("去申请", popup -> requestPermission())
//                    .setCancelButton("拒绝", popup -> ActivityCompat.finishAfterTransition(MainActivity3.this))
//                    .show();
//        }
//    }
//
//    private void requestPermission() {
//        XPermission.create(getApplicationContext(), PermissionConstants.STORAGE)
//                .callback(new XPermission.SimpleCallback() {
//                    @Override
//                    public void onGranted() {
//                    }
//
//                    @Override
//                    public void onDenied() {
//                        showRequestPermissionPopup();
//                    }
//                }).request();
//    }
//
////    @Subscribe
////    public void onGetMainActivityEvent(GetMainActivityEvent event) {
////        if (event.getCallback() != null) {
////            event.getCallback().onCallback(this);
////        }
////    }
//
//    @Subscribe
//    public void startFragment(SupportFragment fragment) {
//        start(fragment);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onStartFragmentEvent(StartFragmentEvent event) {
//        start(event.getFragment());
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onShowLoadingEvent(ShowLoadingEvent event) {
//        if (loadingPopup != null) {
//            if (event.isUpdate()) {
//                loadingPopup.setTitle(event.getText());
//                return;
//            }
//            loadingPopup.dismiss();
//        }
//        loadingPopup = null;
//        loadingPopup = ZPopup.loading(MainActivity3.this)
//                .setTitle(event.getText())
//                .show();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onHideLoadingEvent(HideLoadingEvent event) {
//        if (loadingPopup != null) {
//            loadingPopup.setOnDismissListener(event.getOnDismissListener());
//            loadingPopup.dismiss();
//            loadingPopup = null;
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onStatusBarEvent(StatusBarEvent event) {
//        if (event.isLightMode()) {
//            StatusBarUtils.setLightMode(getWindow());
//        } else {
//            StatusBarUtils.setDarkMode(getWindow());
//        }
//    }
//
//    @Subscribe
//    public void onCropEvent(CropEvent event) {
////        AToast.success("path=" + event.getUri().getPath());
//        if (event.isAvatar()) {
//            ShowLoadingEvent.post("上传头像...");
//            try {
//                HttpApi.uploadAvatarApi(event.getUri())
//                        .onSuccess(doc -> {
//                            Log.d("uploadAvatarApi", "data=" + doc);
//                            String info = doc.selectFirst("info").text();
//                            if ("success".equals(doc.selectFirst("result").text())) {
////                                AToast.success(info);
//
//                                UserManager.getInstance().getMemberInfo().setMemberAvatar(info);
//                                UserManager.getInstance().saveUserInfo();
//                                PictureUtil.saveAvatar(event.getUri(), new IHttp.OnSuccessListener<File>() {
//                                    @Override
//                                    public void onSuccess(File data) throws Exception {
//                                        IconUploadSuccessEvent.post(event);
//                                    }
//                                });
//                            } else {
//                                AToast.error(info);
//                            }
//                            HideLoadingEvent.postDelayed(500);
//                        })
//                        .onError(throwable -> {
//                            AToast.error("上传头像失败！" + throwable.getMessage());
//                            HideLoadingEvent.postDelayed(500);
//                        })
//                        .subscribe();
//            } catch (Exception e) {
//                e.printStackTrace();
//                AToast.error("上传头像失败！" + e.getMessage());
//                HideLoadingEvent.postDelayed(500);
//            }
//        } else {
//            ShowLoadingEvent.post("上传背景...");
//            try {
//                HttpApi.uploadBackgroundApi(event.getUri())
//                        .onSuccess(doc -> {
//                            Log.d("uploadBackgroundApi", "data=" + doc);
//                            String info = doc.selectFirst("info").text();
//                            if ("success".equals(doc.selectFirst("result").text())) {
//                                UserManager.getInstance().getMemberInfo().setMemberBackGround(info);
//                                UserManager.getInstance().saveUserInfo();
//                                PictureUtil.saveBackground(event.getUri(), data -> IconUploadSuccessEvent.post(event));
//                            } else {
//                                AToast.error(info);
//                            }
//                            HideLoadingEvent.postDelayed(500);
//                        })
//                        .onError(throwable -> {
//                            Log.d("uploadBackgroundApi", "throwable.msg=" + throwable.getMessage());
//                            throwable.printStackTrace();
//                            AToast.error("上传背景失败！" + throwable.getMessage());
//                            HideLoadingEvent.postDelayed(500);
//                        })
//                        .subscribe();
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.d("uploadBackgroundApi", "e.msg=" + e.getMessage());
//                AToast.error("上传背景失败！" + e.getMessage());
//                HideLoadingEvent.postDelayed(500);
//            }
//        }
//    }
//
//    private float getSystemBrightness(){
//        int screenBrightness=255;
//        try{
//            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        return (float) screenBrightness / 255 * 100;
//    }
//
//
//}
