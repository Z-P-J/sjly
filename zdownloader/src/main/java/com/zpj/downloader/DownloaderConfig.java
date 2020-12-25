package com.zpj.downloader;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.zpj.downloader.constant.DefaultConstant;


/**
* @author Z-P-J
* */
public class DownloaderConfig extends BaseConfig<DownloaderConfig> {

    private transient Class<? extends BaseMission<?>> clazz = DownloadMission.class;

    private int concurrentMissionCount = DefaultConstant.CONCURRENT_MISSION_COUNT;

    private DownloaderConfig() {

    }

//    public static DownloaderConfig with(Context context) {
//        DownloaderConfig options = new DownloaderConfig();
//        options.setContext(context);
//        return options;
//    }

    static DownloaderConfig with(Context context, Class<? extends BaseMission<?>> clazz) {
        DownloaderConfig options = new DownloaderConfig();
        options.setContext(context);
        if (clazz == null) {
            clazz = DownloadMission.class;
        }
        options.clazz = clazz;
        return options;
    }

    public int getConcurrentMissionCount() {
        return concurrentMissionCount;
    }

    public DownloaderConfig setConcurrentMissionCount(int concurrentMissionCount) {
        this.concurrentMissionCount = concurrentMissionCount;
        return this;
    }

    public void init() {
        Application app = null;
        if (getContext() instanceof Application) {
            app = (Application) getContext();
        } else if (getContext() instanceof Activity) {
            app = ((Activity) getContext()).getApplication();
        }
        if (app != null) {
            app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    DownloadManagerImpl.register(DownloaderConfig.this, clazz);
                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            });
        }
        DownloadManagerImpl.register(this, clazz);
    }

}
