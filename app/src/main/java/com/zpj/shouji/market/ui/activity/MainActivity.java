package com.zpj.shouji.market.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;
import com.zpj.downloader.ZDownloader;
import com.zpj.fragmentation.dialog.IDialog;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.api.UploadImageApi;
import com.zpj.shouji.market.constant.Actions;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.manager.AppBackupManager;
import com.zpj.shouji.market.manager.AppInstalledManager;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.receiver.AppReceiver;
import com.zpj.shouji.market.ui.fragment.MainFragment;
import com.zpj.shouji.market.ui.fragment.manager.DownloadManagerFragment;
import com.zpj.shouji.market.ui.fragment.manager.ManagerFragment;
import com.zpj.shouji.market.ui.fragment.manager.UpdateManagerFragment;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.skin.SkinEngine;
import com.zpj.toast.ZToast;
import com.zpj.utils.BrightnessUtils;
import com.zpj.utils.StatusBarUtils;

import java.io.File;

import static android.Manifest.permission.READ_PHONE_STATE;

public class MainActivity extends BaseActivity { // implements IUiListener

    private MainFragment mainFragment;

    private FrameLayout flContainer;

//    private Tencent tencent;
//
//    private Tencent getTencent() {
//        if (tencent == null) {
//            synchronized (MainActivity.class) {
//                if (tencent == null) {
//                    tencent = Tencent.createInstance("101925427", this);
//                }
//            }
//        }
//        return tencent;
//    }


    @Override
    protected void onDestroy() {
        AppReceiver.unregister(this);
        AppBackupManager.getInstance().onDestroy();
        AppInstalledManager.getInstance().onDestroy();
        AppUpdateManager.getInstance().onDestroy();
        UserManager.getInstance().onDestroy();
        HttpPreLoader.getInstance().onDestroy();
        ZDownloader.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        EventBus.onCropEvent(this, new RxBus.PairConsumer<File, Boolean>() {
            @Override
            public void onAccept(File file, Boolean isAvatar) throws Exception {
                UploadImageApi.uploadCropImage(file, isAvatar);
            }
        });
        EventBus.onGetActivityEvent(this);
        AppReceiver.register(this);

//        EventBus.onQQLoginEvent(this, s -> {
//            if (AppUtils.isInstalled(MainActivity.this, "com.tencent.mobileqq")) {
//                if (getTencent().isSessionValid()) {
//                    getTencent().logout(MainActivity.this);
//                }
//                getTencent().login(MainActivity.this, "all", MainActivity.this);
//            } else {
//                ZToast.warning("未安装QQ应用！");
//            }
//        });


        flContainer = findViewById(R.id.fl_container);
        flContainer.setOnTouchListener((v, event) -> true);

        BrightnessUtils.setBrightness(this);

        StatusBarUtils.transparentStatusBar(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setNavigationBarColor(AppConfig.isNightMode() ? Color.BLACK : Color.WHITE);

        UserManager.getInstance().init();

//        HttpPreLoader.getInstance().loadHomepage();

        AppUpdateManager.getInstance().checkUpdate(MainActivity.this);

//        AppInstalledManager.getInstance().loadApps(this);



        XPermission.create(this, PermissionConstants.PHONE)
                .callback(new XPermission.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        mainFragment = findFragment(MainFragment.class);
                        if (mainFragment == null) {
                            mainFragment = new MainFragment();
                            loadRootFragment(R.id.fl_container, mainFragment);
                        }



                        showRequestPermissionPopup();
                    }

                    @Override
                    public void onDenied() {
                        ZToast.warning("请授予读取手机信息权限！");
                        finish();
                    }
                }).request();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        // 腾讯QQ第三方登录回调
//        Tencent.onActivityResultData(requestCode, resultCode, data, this);
//        if (requestCode == Constants.REQUEST_API) {
//            if (resultCode == Constants.REQUEST_LOGIN) {
//                Tencent.handleResultData(data, this);
//            }
//        }
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
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
    }

    private void showRequestPermissionPopup() {
        if (hasStoragePermissions(getApplicationContext())) {
            requestPermission();
        } else {
            new AlertDialogFragment()
                    .setTitle("权限申请")
                    .setContent("本软件需要读写手机存储的权限用于文件的下载与查看，是否申请该权限？")
                    .setPositiveButton("去申请", (fragment, which) -> requestPermission())
                    .setNegativeButton("拒绝", (fragment, which) -> ActivityCompat.finishAfterTransition(MainActivity.this))
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

                        postDelayed(() -> handleIntent(getIntent()), 250);



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


//    @Override
//    public void onComplete(Object o) {
//        EventBus.showLoading("QQ第三方登录中...");
//        JSONObject jsonObject = (JSONObject) o;
//        Log.e("onComplete", "jsonObject: " + jsonObject.toString());
//        try {
//            //得到token、expires、openId等参数
//            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
//            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
//            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
//
//            getTencent().setAccessToken(token, expires);
//            getTencent().setOpenId(openId);
//            Log.e("onComplete", "token: " + token);
//            Log.e("onComplete", "expires: " + expires);
//            Log.e("onComplete", "openId: " + openId);
//
//            //获取个人信息
//            getQQInfo();
//        } catch (Exception e) {
//        }
//    }
//
//    @Override
//    public void onError(UiError uiError) {
//        ZToast.error("QQ登录失败！" + uiError.errorMessage);
//    }
//
//    @Override
//    public void onCancel() {
//        ZToast.warning("QQ登录取消");
//    }
//
//    @Override
//    public void onWarning(int i) {
//
//    }
//
//
//    private void getQQInfo() {
//        //获取基本信息
//        QQToken qqToken = getTencent().getQQToken();
//        UserInfo info = new UserInfo(this, qqToken);
//        info.getUserInfo(new IUiListener() {
//            @Override
//            public void onComplete(Object object) {
//                Log.e("onComplete", "个人信息：" + object.toString());
//
//                try {
//                    JSONObject userInfo = (JSONObject) object;
//                    String nickName = userInfo.getString("nickname");
//                    String logo1 = userInfo.getString("figureurl_qq_1");
//                    String logo2 = userInfo.getString("figureurl_qq_2");
//                    Log.d("onComplete", "openId=" + getTencent().getOpenId().trim());
//                    Log.d("onComplete", "nickname=" + nickName);
//                    Log.d("onComplete", "logo1=" + logo1);
//                    Log.d("onComplete", "logo1=" + logo2);
//                    UserManager.getInstance().signInByQQ(getTencent().getOpenId(), nickName, logo1, logo2);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    EventBus.hideLoading();
//                    ZToast.error("QQ登录失败！" + e.getMessage());
//                }
//
//            }
//
//            @Override
//            public void onError(UiError uiError) {
//                ZToast.error("QQ登录失败！" + uiError.errorMessage);
//            }
//
//            @Override
//            public void onCancel() {
//            }
//
//            @Override
//            public void onWarning(int i) {
//
//            }
//        });
//    }

}
