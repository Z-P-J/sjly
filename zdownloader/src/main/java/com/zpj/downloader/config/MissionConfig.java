package com.zpj.downloader.config;

import com.zpj.downloader.core.DownloadManagerImpl;

/**
 * @author Z-P-J
 * */
public class MissionConfig extends BaseConfig<MissionConfig> {

    private MissionConfig() {

    }

    public static MissionConfig with() {
        DownloaderConfig config = DownloadManagerImpl.getInstance().getDownloaderConfig();
        if (config == null) {
            throw new RuntimeException("DownloaderConfig is null in DownloadManagerImp. You must init first!");
        }
        return new MissionConfig()
                .setDownloadPath(config.downloadPath)
                .setBufferSize(config.bufferSize)
                .setProgressInterval(config.progressInterval)
                .setThreadPoolConfig(config.threadPoolConfig)
                .setBlockSize(config.blockSize)
                .setRetryCount(config.retryCount)
                .setRetryDelay(config.retryDelay)
                .setConnectOutTime(config.connectOutTime)
                .setReadOutTime(config.readOutTime)
                .setUserAgent(config.userAgent)
                .setCookie(config.cookie)
                .setEnableNotification(config.enableNotification);
    }

    public int getThreadCount() {
        return threadPoolConfig.getCorePoolSize();
    }

    public int getKeepAliveTime() {
        return threadPoolConfig.getKeepAliveTime();
    }

    public int getMaximumPoolSize() {
        return threadPoolConfig.getMaximumPoolSize();
    }
}
