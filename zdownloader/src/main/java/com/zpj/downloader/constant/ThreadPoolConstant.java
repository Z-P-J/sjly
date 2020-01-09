package com.zpj.downloader.constant;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Z-P-J
 */
public class ThreadPoolConstant {

    private ThreadPoolConstant() {

    }

    /**
     * corePoolSiz
     */
    public static final int CORE_POOL_SIZE = 3;
    /**
     * maximumPoolSize
     */
    public static final int MAXIMUM_POOL_SIZE = 128;
    /**
     * keepAliveTime
     * MILLISECONDS
     */
    public static final int KEEP_ALIVE_TIME = 60;

    public static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>();

    public static final ThreadFactory THREAD_FACTORY = Executors.defaultThreadFactory();

    public static final RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.AbortPolicy();

    public static final String SINGLE_THREAD_POOL_TYPE_DEFAULE = "DEFAULT_CLASSIC_DOWNLOAD_SINGLE_THREAD_POOL_TYPE";

}
