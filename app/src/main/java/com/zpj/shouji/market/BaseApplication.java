package com.zpj.shouji.market;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.lqr.emoji.IImageLoader;
import com.lqr.emoji.LQREmotionKit;
import com.maning.librarycrashmonitor.MCrashMonitor;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.zpj.blur.ZBlurry;
import com.zpj.downloader.ZDownloader;
import com.zpj.http.ZHttp;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.shouji.market.download.DownloadNotificationInterceptor;
import com.zpj.utils.AppUtils;
import com.zpj.widget.setting.SimpleSettingItem;
import com.zpj.widget.setting.SwitchSettingItem;
import com.zxy.skin.sdk.SkinEngine;
import com.zxy.skin.sdk.applicator.SkinViewApplicator;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

public class BaseApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinEngine.registerSkinApplicator(SimpleSettingItem.class, new SimpleSettingItemApplicator());
        SkinEngine.registerSkinApplicator(SwitchSettingItem.class, new SettingItemApplicator());
//        SkinEngine.registerSkinApplicator(BaseToolBar.class, new ToolbarApplicator());
        int themeId = AppConfig.isNightMode() ? R.style.NightTheme : R.style.DayTheme;
        setTheme(themeId);
        SkinEngine.changeSkin(themeId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

//        DoraemonKit.install(this);

        long start = System.currentTimeMillis();

        //        MFileUtils.setCrashLogPath(Environment.getExternalStorageDirectory().getPath() + "/sjly/Crash/Log");
//        MFileUtils.setCrashPicPath(Environment.getExternalStorageDirectory().getPath() + "/sjly/Crash/ScreenShoot");
        MCrashMonitor.init(this, true, file -> {
//                MCrashMonitor.startCrashShowPage(getContext());
        });

//        String fileProvider = FileUtils.getFileProviderName(this);
//        // 微信设置
//        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
//        PlatformConfig.setWXFileProvider(fileProvider);
//        // QQ设置
//        PlatformConfig.setQQZone("101830139", "5d63ae8858f1caab67715ccd6c18d7a5");
//        PlatformConfig.setQQFileProvider(fileProvider);


        FlowManager.init(this);

        ZHttp.config()
                .allowAllSSL(true)
                .userAgent(HttpApi.USER_AGENT)
                .init();

        ZDownloader.config(this, AppDownloadMission.class)
                .setUserAgent("Sjly(3.0)")
                .setDownloadPath(AppConfig.getDownloadPath())
                .setNotificationInterceptor(new DownloadNotificationInterceptor())
                .setConcurrentMissionCount(AppConfig.getMaxDownloadConcurrentCount())
                .setEnableNotification(AppConfig.isShowDownloadNotification())
                .setProducerThreadCount(AppConfig.getMaxDownloadThreadCount())
                .init();

//        UMConfigure.init(this,"5f53cf523739314483bc4020"
//                ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"");

        LQREmotionKit.init(this, new IImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).centerCrop().into(imageView);
            }
        });
        ZBlurry.init(this);

        ViewTarget.setTagId(R.id.glide_tag_id);
//        Glide.with(getApplicationContext())
//                .setDefaultRequestOptions(GlideUtils.REQUEST_OPTIONS).applyDefaultRequestOptions()

        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });

        Log.d("AppAppApp", "signature=" + AppUtils.getAppSignatureMD5(this, getPackageName()));
        Log.d("AppAppApp", "duration=" + (System.currentTimeMillis() - start));
    }

    public static class SettingItemApplicator extends SkinViewApplicator {
        public SettingItemApplicator() {
            super();
            addAttributeApplicator("z_setting_titleTextColor", new IAttributeApplicator<SwitchSettingItem>() {
                @Override
                public void onApply(SwitchSettingItem view, TypedArray typedArray, int typedArrayIndex) {
                    view.setTitleTextColor(typedArray.getColor(typedArrayIndex, AppConfig.isNightMode() ? Color.BLACK : Color.WHITE));
                }
            });
        }
    }

    public static class SimpleSettingItemApplicator extends SkinViewApplicator {
        public SimpleSettingItemApplicator() {
            super();
            addAttributeApplicator("z_setting_titleTextColor", new IAttributeApplicator<SimpleSettingItem>() {
                @Override
                public void onApply(SimpleSettingItem view, TypedArray typedArray, int typedArrayIndex) {
                    view.setTextColor(typedArray.getColorStateList(typedArrayIndex));
                }
            });
        }
    }

//    public static class ToolbarApplicator extends SkinViewApplicator {
//        public ToolbarApplicator() {
//            super();
//            addAttributeApplicator("z_toolbar_titleBarColor", new IAttributeApplicator<BaseToolBar>() {
//                @Override
//                public void onApply(BaseToolBar view, TypedArray typedArray, int typedArrayIndex) {
//                    view.setBackgroundColor(typedArray.getColor(typedArrayIndex, ThemeUtils.getDefaultBackgroundColor(view.getContext())), false);
//                }
//            });
//            addAttributeApplicator("z_toolbar_statusBarColor", new IAttributeApplicator<BaseToolBar>() {
//                @Override
//                public void onApply(BaseToolBar view, TypedArray typedArray, int typedArrayIndex) {
//                    view.setStatusBarColor(typedArray.getColor(typedArrayIndex, ThemeUtils.getDefaultBackgroundColor(view.getContext())));
//                }
//            });
//        }
//    }

}
