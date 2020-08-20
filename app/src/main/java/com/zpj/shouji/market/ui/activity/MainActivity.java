package com.zpj.shouji.market.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.felix.atoast.library.AToast;
import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;
import com.yalantis.ucrop.CropEvent;
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
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.utils.StatusBarUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import site.gemus.openingstartanimation.NormalDrawStrategy;
import site.gemus.openingstartanimation.OpeningStartAnimation;

public class MainActivity extends SupportActivity {

    private long firstTime = 0;

    private OpeningStartAnimation openingStartAnimation3;
    private LoadingPopup loadingPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        StatusBarUtils.setDarkMode(getWindow());
        openingStartAnimation3 = new OpeningStartAnimation.Builder(MainActivity.this)
                .setDrawStategy(new NormalDrawStrategy())
                .setAppName("手机乐园")
                .setAppStatement("分享优质应用")
                .setAnimationInterval(1500)
                .setAppIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                .setAnimationListener((openingStartAnimation, activity) -> {
                    AppUpdateManager.getInstance().checkUpdate(MainActivity.this);
                    showRequestPermissionPopup();
                    postDelayed(() -> {
                        MainFragment mainFragment = findFragment(MainFragment.class);
                        if (mainFragment == null) {
                            mainFragment = new MainFragment();
                            loadRootFragment(R.id.main_content, mainFragment);
                        }
                    }, 50);
                })
                .create();
        openingStartAnimation3.show(MainActivity.this);

        UserManager.getInstance().init();

        HttpPreLoader.getInstance().loadHomepage();

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        AppInstalledManager.getInstance().loadApps(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
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
                    .setCancelButton("拒绝", this::finish)
                    .show();
        }
    }

    private void requestPermission() {
        XPermission.create(getApplicationContext(), PermissionConstants.STORAGE)
                .callback(new XPermission.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        openingStartAnimation3.dismiss(MainActivity.this);
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
        if (loadingPopup != null && event.isUpdate()) {
            loadingPopup.setTitle(event.getText());
            return;
        }
        loadingPopup = null;
        loadingPopup = ZPopup.loading(MainActivity.this)
                .setTitle(event.getText())
                .show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideLoadingEvent(HideLoadingEvent event) {
        if (loadingPopup != null) {
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


}
