package com.zpj.downloader.config;

import android.content.Context;

import com.zpj.downloader.constant.DefaultConstant;
import com.zpj.downloader.core.INotificationInterceptor;


/**
* @author Z-P-J
* */
public class DownloaderConfig extends BaseConfig<DownloaderConfig> {

    private int concurrentMissionCount = DefaultConstant.CONCURRENT_MISSION_COUNT;
//    private INotificationInterceptor interceptor;

    private DownloaderConfig() {

    }

    public static DownloaderConfig with(Context context) {
        DownloaderConfig options = new DownloaderConfig();
        options.setContext(context);
        return options;
    }

    public int getConcurrentMissionCount() {
        return concurrentMissionCount;
    }

//    public INotificationInterceptor getNotificationInterceptor() {
//        return interceptor;
//    }

    public DownloaderConfig setConcurrentMissionCount(int concurrentMissionCount) {
        this.concurrentMissionCount = concurrentMissionCount;
        return this;
    }

//    public DownloaderConfig setNotificationInterceptor(INotificationInterceptor interceptor) {
//        this.interceptor = interceptor;
//        return this;
//    }

}
