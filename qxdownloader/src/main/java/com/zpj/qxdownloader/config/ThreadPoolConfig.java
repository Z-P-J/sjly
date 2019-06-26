package com.zpj.qxdownloader.config;

import com.zpj.qxdownloader.constant.ThreadPoolConstant;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;

/**
 *
 * @author Z-P-J
 * */
public class ThreadPoolConfig {

    /**
     * corePoolSize 核心线程数。
     */
    private int corePoolSize = ThreadPoolConstant.CORE_POOL_SIZE;

    /**
     * maximumPoolSize - 最大线程数(LinkedBlockingQueue时没有作用)。
     */
    private int maximumPoolSize = ThreadPoolConstant.MAXIMUM_POOL_SIZE;

    /**
     * keepAliveTime
     */
    private int keepAliveTime = ThreadPoolConstant.KEEP_ALIVE_TIME;

    private transient BlockingQueue<Runnable> workQueue = ThreadPoolConstant.WORK_QUEUE;

    private transient ThreadFactory threadFactory = ThreadPoolConstant.THREAD_FACTORY;

    private transient RejectedExecutionHandler handler = ThreadPoolConstant.HANDLER;

    private ThreadPoolConfig() {

    }

    public static ThreadPoolConfig build() {
        return new ThreadPoolConfig();
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public ThreadPoolConfig setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public ThreadPoolConfig setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public ThreadPoolConfig setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public ThreadPoolConfig setWorkQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    public BlockingQueue<Runnable> getWorkQueue() {
        return workQueue;
    }

    public ThreadPoolConfig setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public ThreadPoolConfig setHandler(RejectedExecutionHandler handler) {
        this.handler = handler;
        return this;
    }

    public RejectedExecutionHandler getHandler() {
        return handler;
    }
}
