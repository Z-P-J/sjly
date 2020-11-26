package com.zpj.shouji.market.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;
import com.yalantis.ucrop.CropEvent;
import com.zpj.downloader.ZDownloader;
import com.zpj.downloader.config.DownloaderConfig;
import com.zpj.downloader.config.ThreadPoolConfig;
import com.zpj.fragmentation.SupportActivity;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.fragmentation.anim.DefaultHorizontalAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.fragmentation.dialog.impl.LoadingDialogFragment;
import com.zpj.http.core.IHttp;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.constant.Actions;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.shouji.market.download.DownloadNotificationInterceptor;
import com.zpj.shouji.market.event.GetMainActivityEvent;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.IconUploadSuccessEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.event.StatusBarEvent;
import com.zpj.shouji.market.manager.AppInstalledManager;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.fragment.MainFragment;
import com.zpj.shouji.market.ui.fragment.manager.DownloadManagerFragment;
import com.zpj.shouji.market.ui.fragment.manager.ManagerFragment;
import com.zpj.shouji.market.ui.fragment.manager.UpdateManagerFragment;
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.shouji.market.utils.BrightnessUtils;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.utils.StatusBarUtils;
import com.zxy.skin.sdk.SkinLayoutInflater;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

public class MainActivity extends SupportActivity {

    private long firstTime = 0;

    private SkinLayoutInflater mLayoutInflater;

    private MainFragment mainFragment;

//    private OpeningStartAnimation openingStartAnimation3;
//    private LoadingPopup loadingPopup;
    private LoadingDialogFragment loadingDialogFragment;

    private FrameLayout flContainer;
//    private RelativeLayout rlSplash;

    @NonNull
    @Override
    public final LayoutInflater getLayoutInflater() {
        if (mLayoutInflater == null) {
            mLayoutInflater = new SkinLayoutInflater(this);
        }
        return mLayoutInflater;
    }

    @Override
    public final Object getSystemService(@NonNull String name) {
        if (Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
            return getLayoutInflater();
        }
        return super.getSystemService(name);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    protected void onDestroy() {
        loadingDialogFragment = null;
        ZDownloader.onDestroy();
        EventBus.getDefault().unregister(this);
        HttpPreLoader.getInstance().onDestroy();
        mLayoutInflater.destory();
        super.onDestroy();
//        System.exit(0);
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AToast.normal(intent.getStringExtra(Actions.ACTION));
        if (intent.hasExtra(Actions.ACTION)) {
            switch (intent.getStringExtra(Actions.ACTION)) {
                case Actions.ACTION_SHOW_UPDATE:
                    if (getTopFragment() instanceof ManagerFragment) {
                        ((ManagerFragment) getTopFragment()).showUpdateFragment();
                    } else if (!(getTopFragment() instanceof UpdateManagerFragment)) {
                        UpdateManagerFragment.start(true);
                    }
                    break;
                case Actions.ACTION_SHOW_DOWNLOAD:
                    if (getTopFragment() instanceof ManagerFragment) {
                        ((ManagerFragment) getTopFragment()).showDownloadFragment();
                    } else if (!(getTopFragment() instanceof DownloadManagerFragment)) {
                        DownloadManagerFragment.start(true);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(AppConfig.isNightMode() ? R.style.NightTheme : R.style.DayTheme);
        long start = System.currentTimeMillis();

        super.onCreate(savedInstanceState);

        getLayoutInflater();
        mLayoutInflater.applyCurrentSkin();
        // AppCompatActivity 需要设置
        AppCompatDelegate delegate = this.getDelegate();
        if (delegate instanceof LayoutInflater.Factory2) {
            mLayoutInflater.setFactory2((LayoutInflater.Factory2) delegate);
        }

        Log.d("MainActivity", "duration000-1=" + (System.currentTimeMillis() - start));

        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "duration000-2=" + (System.currentTimeMillis() - start));


        EventBus.getDefault().register(this);
        Log.d("MainActivity", "duration000-3=" + (System.currentTimeMillis() - start));

        flContainer = findViewById(R.id.fl_container);
        flContainer.setOnTouchListener((v, event) -> true);
        Log.d("MainActivity", "duration000-4=" + (System.currentTimeMillis() - start));

        BrightnessUtils.setBrightness(this);

        StatusBarUtils.transparentStatusBar(getWindow());

        ZDownloader.init(
                DownloaderConfig.with(MainActivity.this)
                        .setUserAgent("Sjly(3.0)")
                        .setNotificationIntercepter(new DownloadNotificationInterceptor())
                        .setConcurrentMissionCount(AppConfig.getMaxDownloadConcurrentCount())
                        .setEnableNotification(AppConfig.isShowDownloadNotification())
                        .setThreadPoolConfig(
                                ThreadPoolConfig.build()
                                        .setCorePoolSize(AppConfig.getMaxDownloadThreadCount())
                        )
                        .setDownloadPath(AppConfig.getDownloadPath()),
                AppDownloadMission.class
        );

        mainFragment = findFragment(MainFragment.class);
        if (mainFragment == null) {
            mainFragment = new MainFragment();
            loadRootFragment(R.id.fl_container, mainFragment);
        }

        init();

    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
    }

    private void init() {

//        long temp1 = System.currentTimeMillis();
//
//        Log.d("MainActivity", "duration111=" + (System.currentTimeMillis() - temp1));

        UserManager.getInstance().init();

        HttpPreLoader.getInstance().loadHomepage();

        AppUpdateManager.getInstance().checkUpdate(MainActivity.this);

        AppInstalledManager.getInstance().loadApps(this);

        showRequestPermissionPopup();


    }

    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else if (System.currentTimeMillis() - firstTime > 2000) {
            AToast.warning("再次点击退出！");
            firstTime = System.currentTimeMillis();
        } else {
//            finish();
            ZDownloader.pauseAll();
//            ZDownloader.onDestroy();
            ActivityCompat.finishAfterTransition(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppUtil.UNINSTALL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                AToast.success("应用卸载成功！");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                AToast.normal("应用卸载取消！");
            }
        }
    }

//    @Override
//    public FragmentAnimator onCreateFragmentAnimator() {
//        return new MyFragmentAnimator();
//    }

    private void showRequestPermissionPopup() {
        if (hasStoragePermissions(getApplicationContext())) {
            requestPermission();
        } else {
            new AlertDialogFragment()
                    .setTitle("权限申请")
                    .setContent("本软件需要读写手机存储的权限用于文件的下载与查看，是否申请该权限？")
                    .setPositiveButton("去申请", popup -> requestPermission())
                    .setNegativeButton("拒绝", popup -> ActivityCompat.finishAfterTransition(MainActivity.this))
                    .show(this);
        }
    }

    private boolean hasStoragePermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        XPermission.create(getApplicationContext(), PermissionConstants.STORAGE)
                .callback(new XPermission.SimpleCallback() {
                    @Override
                    public void onGranted() {

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                        flContainer.setOnTouchListener(null);

                        mainFragment.animatedToShow();



//                        if (AppConfig.isShowSplash()) {
//                            Observable.timer(1500, TimeUnit.MILLISECONDS)
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .doOnComplete(() -> {
////                                        rlSplash.setVisibility(View.GONE);
//                                        flContainer.animate()
//                                                .setDuration(500)
//                                                .alpha(1)
//                                                .start();
//                                        flContainer.setOnTouchListener(null);
//                                    })
//                                    .subscribe();
//                        } else {
//                            flContainer.setOnTouchListener(null);
//                            flContainer.animate()
//                                    .setDuration(1000)
//                                    .alpha(1)
//                                    .start();
//                        }





                    }

                    @Override
                    public void onDenied() {
                        showRequestPermissionPopup();
                    }
                }).request();
    }

    private void toggleThemeSetting() {
        if (AppConfig.isNightMode()) {
            setTheme(R.style.DayTheme);
        } else {
            setTheme(R.style.NightTheme);
        }
        AppConfig.toggleThemeMode();
        recreate();
    }

    @Subscribe
    public void onGetMainActivityEvent(GetMainActivityEvent event) {
        if (event.getCallback() != null) {
            event.getCallback().onCallback(this);
        }
    }

    @Subscribe
    public void startFragment(SupportFragment fragment) {
        start(fragment);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartFragmentEvent(StartFragmentEvent event) {
        start(event.getFragment());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowLoadingEvent(ShowLoadingEvent event) {
        if (loadingDialogFragment != null) {
            if (event.isUpdate()) {
                loadingDialogFragment.setTitle(event.getText());
                return;
            }
            loadingDialogFragment.dismiss();
        }
        loadingDialogFragment = null;
        loadingDialogFragment = new LoadingDialogFragment().setTitle(event.getText());
                loadingDialogFragment.show(MainActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideLoadingEvent(HideLoadingEvent event) {
        if (loadingDialogFragment != null) {
            loadingDialogFragment.setOnDismissListener(event.getOnDismissListener());
            loadingDialogFragment.dismiss();
            loadingDialogFragment = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStatusBarEvent(StatusBarEvent event) {
        if (event.isLightMode()) {
            StatusBarUtils.setLightMode(getWindow());
        } else {
            StatusBarUtils.setDarkMode(getWindow());
        }
    }

    @Subscribe
    public void onCropEvent(CropEvent event) {
        if (event.isAvatar()) {
            ShowLoadingEvent.post("上传头像...");
            try {
                HttpApi.uploadAvatarApi(event.getUri())
                        .onSuccess(doc -> {
                            Log.d("uploadAvatarApi", "data=" + doc);
                            String info = doc.selectFirst("info").text();
                            if ("success".equals(doc.selectFirst("result").text())) {
//                                AToast.success(info);

                                UserManager.getInstance().getMemberInfo().setMemberAvatar(info);
                                UserManager.getInstance().saveUserInfo();
                                PictureUtil.saveAvatar(event.getUri(), new IHttp.OnSuccessListener<File>() {
                                    @Override
                                    public void onSuccess(File data) throws Exception {
                                        IconUploadSuccessEvent.post(event);
                                    }
                                });
                            } else {
                                AToast.error(info);
                            }
                            HideLoadingEvent.postDelayed(500);
                        })
                        .onError(throwable -> {
                            AToast.error("上传头像失败！" + throwable.getMessage());
                            HideLoadingEvent.postDelayed(500);
                        })
                        .subscribe();
            } catch (Exception e) {
                e.printStackTrace();
                AToast.error("上传头像失败！" + e.getMessage());
                HideLoadingEvent.postDelayed(500);
            }
        } else {
            ShowLoadingEvent.post("上传背景...");
            try {
                HttpApi.uploadBackgroundApi(event.getUri())
                        .onSuccess(doc -> {
                            Log.d("uploadBackgroundApi", "data=" + doc);
                            String info = doc.selectFirst("info").text();
                            if ("success".equals(doc.selectFirst("result").text())) {
                                UserManager.getInstance().getMemberInfo().setMemberBackGround(info);
                                UserManager.getInstance().saveUserInfo();
                                PictureUtil.saveBackground(event.getUri(), data -> IconUploadSuccessEvent.post(event));
                            } else {
                                AToast.error(info);
                            }
                            HideLoadingEvent.postDelayed(500);
                        })
                        .onError(throwable -> {
                            Log.d("uploadBackgroundApi", "throwable.msg=" + throwable.getMessage());
                            throwable.printStackTrace();
                            AToast.error("上传背景失败！" + throwable.getMessage());
                            HideLoadingEvent.postDelayed(500);
                        })
                        .subscribe();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("uploadBackgroundApi", "e.msg=" + e.getMessage());
                AToast.error("上传背景失败！" + e.getMessage());
                HideLoadingEvent.postDelayed(500);
            }
        }
    }

    private float getSystemBrightness(){
        int screenBrightness=255;
        try{
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e){
            e.printStackTrace();
        }
        return (float) screenBrightness / 255 * 100;
    }


}
