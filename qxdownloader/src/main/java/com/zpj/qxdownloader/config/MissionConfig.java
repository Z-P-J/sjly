package com.zpj.qxdownloader.config;

import com.zpj.qxdownloader.core.DownloadManagerImpl;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

/**
 * @author Z-P-J
 * */
public class MissionConfig extends BaseConfig {

    private MissionConfig() {

    }

    public static MissionConfig with() {
        QianXunConfig config = DownloadManagerImpl.getInstance().getQianXunConfig();
        if (config == null) {
            throw new RuntimeException("QianXunConfig is null in DownloadManagerImp. You must init first!");
        }
        return new MissionConfig()
                .setDownloadPath(config.downloadPath)
                .setBufferSize(config.bufferSize)
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

    @Override
    public MissionConfig setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
        return this;
    }

    @Override
    public MissionConfig setThreadPoolConfig(ThreadPoolConfig threadPoolConfig) {
        this.threadPoolConfig = threadPoolConfig;
        return this;
    }

    @Deprecated
    @Override
    public MissionConfig setThreadCount(int threadCount) {
        threadPoolConfig.setCorePoolSize(threadCount);
        return this;
    }

    @Override
    public MissionConfig setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    @Override
    public MissionConfig setBlockSize(int blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    @Override
    public MissionConfig setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    @Override
    public MissionConfig setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    @Override
    public MissionConfig setCookie(String cookie) {
        this.cookie = cookie;
        return this;
    }

    @Override
    public MissionConfig setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
        return this;
    }

    @Override
    public MissionConfig setConnectOutTime(int connectOutTime) {
        this.connectOutTime = connectOutTime;
        return this;
    }

    @Override
    public MissionConfig setReadOutTime(int readOutTime) {
        this.readOutTime = readOutTime;
        return this;
    }

    @Override
    public MissionConfig setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public MissionConfig setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public MissionConfig setProxy(String host, int port) {
        this.proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port));
        return this;
    }

    @Override
    public MissionConfig setEnableNotification(boolean enableNotification) {
        this.enableNotification = enableNotification;
        return this;
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
