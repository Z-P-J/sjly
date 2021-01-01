package com.zpj.shouji.market;

import android.app.Application;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.StrictMode;
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
import com.zpj.shouji.market.utils.ThemeUtils;
import com.zpj.widget.setting.SimpleSettingItem;
import com.zpj.widget.setting.SwitchSettingItem;
import com.zxy.skin.sdk.SkinEngine;
import com.zxy.skin.sdk.applicator.SkinViewApplicator;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinEngine.registerSkinApplicator(SimpleSettingItem.class, new SimpleSettingItemApplicator());
        SkinEngine.registerSkinApplicator(SwitchSettingItem.class, new SettingItemApplicator());
//        SkinEngine.registerSkinApplicator(BaseToolBar.class, new ToolbarApplicator());
        SkinEngine.changeSkin(AppConfig.isNightMode() ? R.style.NightTheme : R.style.DayTheme);

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
        FlowManager.init(this);

        ZHttp.config()
                .allowAllSSL(true)
                .userAgent(HttpApi.USER_AGENT)
                .init();

        ZDownloader.config(this, AppDownloadMission.class)
                .setUserAgent("Sjly(3.0)")
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

        Log.d("AppAppApp", "duration=" + (System.currentTimeMillis() - start));
    }

    public static class SettingItemApplicator extends SkinViewApplicator {
        public SettingItemApplicator() {
            super();
            addAttributeApplicator("z_setting_titleTextColor", new IAttributeApplicator<SwitchSettingItem>() {
                @Override
                public void onApply(SwitchSettingItem view, TypedArray typedArray, int typedArrayIndex) {
                    view.setTitleTextColor(typedArray.getColor(typedArrayIndex, ThemeUtils.getDefaultBackgroundColor(view.getContext())));
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
