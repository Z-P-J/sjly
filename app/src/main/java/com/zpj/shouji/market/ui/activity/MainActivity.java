package com.zpj.shouji.market.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.zpj.toast.ZToast;
import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;
import com.yalantis.ucrop.CropEvent;
import com.zpj.downloader.ZDownloader;
import com.zpj.downloader.config.DownloaderConfig;
import com.zpj.downloader.config.ThreadPoolConfig;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.api.UploadImageApi;
import com.zpj.shouji.market.constant.Actions;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.shouji.market.download.DownloadNotificationInterceptor;
import com.zpj.shouji.market.event.GetMainActivityEvent;
import com.zpj.shouji.market.manager.AppInstalledManager;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.fragment.MainFragment;
import com.zpj.shouji.market.ui.fragment.manager.DownloadManagerFragment;
import com.zpj.shouji.market.ui.fragment.manager.ManagerFragment;
import com.zpj.shouji.market.ui.fragment.manager.UpdateManagerFragment;
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.shouji.market.utils.BrightnessUtils;
import com.zpj.utils.StatusBarUtils;

public class MainActivity extends BaseActivity {

    private MainFragment mainFragment;

    private FrameLayout flContainer;



    @Override
    protected void onDestroy() {
        ZDownloader.onDestroy();
        HttpPreLoader.getInstance().onDestroy();
        super.onDestroy();
//        System.exit(0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZToast.normal(intent.getStringExtra(Actions.ACTION));
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerObserver(GetMainActivityEvent.class, event -> {
            if (event.getCallback() != null) {
                event.getCallback().onCallback(MainActivity.this);
            }
        });

        registerObserver(CropEvent.class, UploadImageApi::uploadCropImage);


        flContainer = findViewById(R.id.fl_container);
        flContainer.setOnTouchListener((v, event) -> true);

        BrightnessUtils.setBrightness(this);

        StatusBarUtils.transparentStatusBar(getWindow());

        ZDownloader.init(
                DownloaderConfig.with(MainActivity.this)
                        .setUserAgent("Sjly(3.0)")
                        .setNotificationInterceptor(new DownloadNotificationInterceptor())
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

        UserManager.getInstance().init();

        HttpPreLoader.getInstance().loadHomepage();

        AppUpdateManager.getInstance().checkUpdate(MainActivity.this);

        AppInstalledManager.getInstance().loadApps(this);

        showRequestPermissionPopup();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppUtil.UNINSTALL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                ZToast.success("应用卸载成功！");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                ZToast.normal("应用卸载取消！");
            }
        }
    }

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

//    @Subscribe
//    public void onCropEvent(CropEvent event) {
//        if (event.isAvatar()) {
//            ShowLoadingEvent.post("上传头像...");
//            try {
//                HttpApi.uploadAvatarApi(event.getUri())
//                        .onSuccess(doc -> {
//                            Log.d("uploadAvatarApi", "data=" + doc);
//                            String info = doc.selectFirst("info").text();
//                            if ("success".equals(doc.selectFirst("result").text())) {
////                                ZToast.success(info);
//
//                                UserManager.getInstance().getMemberInfo().setMemberAvatar(info);
//                                UserManager.getInstance().saveUserInfo();
//                                PictureUtil.saveAvatar(event.getUri(), new IHttp.OnSuccessListener<File>() {
//                                    @Override
//                                    public void onSuccess(File data) throws Exception {
//                                        EventSender.sendImageUploadEvent(event);
//                                    }
//                                });
//                            } else {
//                                ZToast.error(info);
//                            }
//                            HideLoadingEvent.postDelayed(500);
//                        })
//                        .onError(throwable -> {
//                            ZToast.error("上传头像失败！" + throwable.getMessage());
//                            HideLoadingEvent.postDelayed(500);
//                        })
//                        .subscribe();
//            } catch (Exception e) {
//                e.printStackTrace();
//                ZToast.error("上传头像失败！" + e.getMessage());
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
//                                PictureUtil.saveBackground(event.getUri(), data -> EventSender.sendImageUploadEvent(event));
//                            } else {
//                                ZToast.error(info);
//                            }
//                            HideLoadingEvent.postDelayed(500);
//                        })
//                        .onError(throwable -> {
//                            Log.d("uploadBackgroundApi", "throwable.msg=" + throwable.getMessage());
//                            throwable.printStackTrace();
//                            ZToast.error("上传背景失败！" + throwable.getMessage());
//                            HideLoadingEvent.postDelayed(500);
//                        })
//                        .subscribe();
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.d("uploadBackgroundApi", "e.msg=" + e.getMessage());
//                ZToast.error("上传背景失败！" + e.getMessage());
//                HideLoadingEvent.postDelayed(500);
//            }
//        }
//    }

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
