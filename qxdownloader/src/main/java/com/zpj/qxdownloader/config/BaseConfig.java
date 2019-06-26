package com.zpj.qxdownloader.config;

import android.content.Context;

import com.zpj.qxdownloader.constant.DefaultConstant;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Z-P-J
 * */
abstract class BaseConfig {

    /**
     * context
     * */
    private transient Context context;

    /**
     * 线程池配置
     * */
    ThreadPoolConfig threadPoolConfig = ThreadPoolConfig.build();

    /**
     * 下载路径
     * */
    String downloadPath = DefaultConstant.DOWNLOAD_PATH;

    /**
     * 下载缓冲大小
     * */
    int bufferSize = DefaultConstant.BUFFER_SIZE;

    /**
     * 下载块大小
     * */
    int blockSize = DefaultConstant.BLOCK_SIZE;

    /**
     * 默认UserAgent
     * */
    String userAgent = DefaultConstant.USER_AGENT;

    /**
     * 下载出错重试次数
     * */
    int retryCount = DefaultConstant.RETRY_COUNT;

    /**
     * 下载出错重试延迟时间（单位ms）
     * */
    int retryDelay = DefaultConstant.RETRY_DELAY;

    /**
     * 下载连接超时
     * */
    int connectOutTime = DefaultConstant.CONNECT_OUT_TIME;

    /**
     * 下载链接读取超时
     * */
    int readOutTime = DefaultConstant.READ_OUT_TIME;

    /**
     * 是否允许在通知栏显示任务下载进度
     * */
    boolean enableNotification = true;

    /**
     * 下载时传入的cookie额值
     * */
    String cookie = "";

    Map<String, String> headers = new HashMap<>();

    Proxy proxy;

    void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public BaseConfig setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
        return this;
    }

    public abstract BaseConfig setThreadPoolConfig(ThreadPoolConfig threadPoolConfig);

    public ThreadPoolConfig getThreadPoolConfig() {
        return threadPoolConfig;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    @Deprecated
    public abstract BaseConfig setThreadCount(int threadCount);

    public abstract BaseConfig setBufferSize(int bufferSize);

    public int getBufferSize() {
        return bufferSize;
    }

    public abstract BaseConfig setBlockSize(int blockSize);

    public int getBlockSize() {
        return blockSize;
    }

    public abstract BaseConfig setUserAgent(String userAgent);

    public String getUserAgent() {
        return userAgent;
    }

    public abstract BaseConfig setRetryCount(int retryCount);

    public int getRetryCount() {
        return retryCount;
    }

    public abstract BaseConfig setCookie(String cookie);

    public String getCookie() {
        return cookie;
    }

    public abstract BaseConfig setRetryDelay(int retryDelay);

    public int getRetryDelay() {
        return retryDelay;
    }

    public abstract BaseConfig setConnectOutTime(int connectOutTime);

    public int getConnectOutTime() {
        return connectOutTime;
    }

    public abstract BaseConfig setReadOutTime(int readOutTime);

    public int getReadOutTime() {
        return readOutTime;
    }

    public abstract BaseConfig setHeaders(Map<String, String> headers);

    public Map<String, String> getHeaders() {
        return headers;
    }

    public abstract BaseConfig setProxy(Proxy proxy);

    public abstract BaseConfig setProxy(String host, int port);

    public Proxy getProxy() {
        return proxy;
    }

    public abstract BaseConfig setEnableNotification(boolean enableNotification);

    public boolean getEnableNotificatio() {
        return enableNotification;
    }
}
