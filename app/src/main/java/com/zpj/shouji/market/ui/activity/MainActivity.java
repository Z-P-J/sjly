package com.zpj.shouji.market.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.lqr.emoji.IImageLoader;
import com.lqr.emoji.LQREmotionKit;
import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;
import com.maning.librarycrashmonitor.MCrashMonitor;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.yalantis.ucrop.CropEvent;
import com.zpj.downloader.ZDownloader;
import com.zpj.downloader.util.permission.PermissionUtil;
import com.zpj.fragmentation.SupportActivity;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.fragmentation.anim.DefaultHorizontalAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;
import com.zpj.http.core.IHttp;
import com.zpj.popup.ZPopup;
import com.zpj.popup.impl.LoadingPopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.constant.Keys;
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
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.utils.PrefsHelper;
import com.zpj.utils.StatusBarUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import per.goweii.burred.Blurred;
import site.gemus.openingstartanimation.OpeningStartAnimation;

public class MainActivity extends SupportActivity {

    private long firstTime = 0;

    private OpeningStartAnimation openingStartAnimation3;
    private LoadingPopup loadingPopup;

    private RelativeLayout rlSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_main);

        rlSplash = findViewById(R.id.rl_splash);
        rlSplash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rlSplash.setOnTouchListener((v, event) -> true);

//        loadRootFragment(R.id.main_content, new TestFragment());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

//        StatusBarUtils.setDarkMode(getWindow());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        float brightness;
        if (PrefsHelper.with().getBoolean(Keys.SYSTEM_BRIGHTNESS, false)) {
            brightness = getSystemBrightness();
        } else {
            brightness  = PrefsHelper.with().getFloat(Keys.APP_BRIGHTNESS, getSystemBrightness());
        }
        lp.screenBrightness = brightness / 100;
        getWindow().setAttributes(lp);



//        postDelayed(() -> {
//            mainFragment = findFragment(MainFragment3.class);
//            if (mainFragment == null) {
//                mainFragment = new MainFragment3();
//                loadRootFragment(R.id.fl_container, mainFragment);
//            }
//        }, 200);

//        Observable.timer(250, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnComplete(() -> {
//                    mainFragment = findFragment(MainFragment3.class);
//                    if (mainFragment == null) {
//                        mainFragment = new MainFragment3();
//                        loadRootFragment(R.id.fl_container, mainFragment);
//                    }
//                })
//                .subscribe();

//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showRequestPermissionPopup();
//                rlSplash.setVisibility(View.GONE);
//            }
//        }, 10000);

//        openingStartAnimation3 = new OpeningStartAnimation.Builder(MainActivity.this)
//                .setDrawStategy(new NormalDrawStrategy())
//                .setAppName("手机乐园")
//                .setAppStatement("分享优质应用")
//                .setAnimationInterval(2500)
////                .setAnimationFinishTime(350)
//                .setAppIcon(getResources().getDrawable(R.mipmap.ic_launcher))
//                .setAnimationListener((openingStartAnimation, activity) -> {
////                    postDelayed(() -> {
////                        mainFragment = findFragment(MainFragment.class);
////                        if (mainFragment == null) {
////                            mainFragment = new MainFragment();
////                            loadRootFragment(R.id.main_content, mainFragment);
////                        }
//////                        loadRootFragment(R.id.main_content, new TestFragment());
////                    }, 50);
//
//                    UserManager.getInstance().init();
//
//                    HttpPreLoader.getInstance().loadHomepage();
//
//                    AppUpdateManager.getInstance().checkUpdate(MainActivity.this);
//
//                    AppInstalledManager.getInstance().loadApps(this);
//
//                    mainFragment = findFragment(MainFragment3.class);
//                    if (mainFragment == null) {
//                        mainFragment = new MainFragment3();
//                        loadRootFragment(R.id.fl_container, mainFragment);
//                    }
//                    postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            showRequestPermissionPopup();
//
//                        }
//                    }, 1000);
//                })
//                .create();
//        openingStartAnimation3.show(MainActivity.this);



    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();

        //        MFileUtils.setCrashLogPath(Environment.getExternalStorageDirectory().getPath() + "/sjly/Crash/Log");
//        MFileUtils.setCrashPicPath(Environment.getExternalStorageDirectory().getPath() + "/sjly/Crash/ScreenShoot");
        MCrashMonitor.init(this, true, file -> {
//                MCrashMonitor.startCrashShowPage(getContext());
        });
        FlowManager.init(this);

        AToast.onInit(this);
        ZDownloader.init(this);
        LQREmotionKit.init(this, new IImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).centerCrop().into(imageView);
            }
        });
        Blurred.init(this);


        UserManager.getInstance().init();

        HttpPreLoader.getInstance().loadHomepage();

        AppUpdateManager.getInstance().checkUpdate(MainActivity.this);

        AppInstalledManager.getInstance().loadApps(this);

        Observable.timer(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    showRequestPermissionPopup();
                    rlSplash.setVisibility(View.GONE);
                })
                .subscribe();

        MainFragment mainFragment = findFragment(MainFragment.class);
        if (mainFragment == null) {
            mainFragment = new MainFragment();
            loadRootFragment(R.id.fl_container, mainFragment);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        HttpPreLoader.getInstance().onDestroy();
        super.onDestroy();
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

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }

    private void showRequestPermissionPopup() {
        if (PermissionUtil.checkStoragePermissions(getApplicationContext())) {
            requestPermission();
        } else {
            ZPopup.alert(MainActivity.this)
                    .setTitle("权限申请")
                    .setContent("本软件需要读写手机存储的权限用于文件的下载与查看，是否申请该权限？")
                    .setConfirmButton("去申请", popup -> requestPermission())
                    .setCancelButton("拒绝", () -> ActivityCompat.finishAfterTransition(MainActivity.this))
                    .show();
        }
    }

    private void requestPermission() {
        XPermission.create(getApplicationContext(), PermissionConstants.STORAGE)
                .callback(new XPermission.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        if (openingStartAnimation3 != null) {
                            openingStartAnimation3.dismiss(MainActivity.this);
                        }
                    }

                    @Override
                    public void onDenied() {
                        showRequestPermissionPopup();
                    }
                }).request();
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
        if (loadingPopup != null) {
            if (event.isUpdate()) {
                loadingPopup.setTitle(event.getText());
                return;
            }
            loadingPopup.dismiss();
        }
        loadingPopup = null;
        loadingPopup = ZPopup.loading(MainActivity.this)
                .setTitle(event.getText())
                .show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideLoadingEvent(HideLoadingEvent event) {
        if (loadingPopup != null) {
            loadingPopup.setOnDismissListener(event.getOnDismissListener());
            loadingPopup.dismiss();
            loadingPopup = null;
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
//        AToast.success("path=" + event.getUri().getPath());
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
