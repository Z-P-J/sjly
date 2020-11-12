package com.zpj.downloader.config;

import android.content.Context;

import com.zpj.downloader.constant.DefaultConstant;
import com.zpj.downloader.core.INotificationListener;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;


/**
* @author Z-P-J
* */
public class DownloaderConfig extends BaseConfig<DownloaderConfig> {

    private int concurrentMissionCount = DefaultConstant.CONCURRENT_MISSION_COUNT;
    private INotificationListener listener;

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

    public INotificationListener getNotificationListener() {
        return listener;
    }

    public DownloaderConfig setConcurrentMissionCount(int concurrentMissionCount) {
        this.concurrentMissionCount = concurrentMissionCount;
        return this;
    }

    public DownloaderConfig setNotificationListener(INotificationListener listener) {
        this.listener = listener;
        return this;
    }

}
