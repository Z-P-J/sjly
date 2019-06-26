package com.zpj.qxdownloader.util;

import com.zpj.qxdownloader.config.ThreadPoolConfig;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Z-P-J
 */
public class ThreadPoolFactory {

    public static ThreadPoolExecutor newFixedThreadPool(ThreadPoolConfig config) {
        return new ThreadPoolExecutor(2 * config.getCorePoolSize(), config.getMaximumPoolSize(),
                config.getKeepAliveTime(), TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), config.getThreadFactory(), config.getHandler());
    }

//    public static ThreadPoolExecutor newSingleThreadExecutor() {
//        ThreadPoolConfig config = ThreadPoolConfig.build().setCorePoolSize(2).setMaximumPoolSize(2);
//        return newFixedThreadPool(config);
//    }

}
