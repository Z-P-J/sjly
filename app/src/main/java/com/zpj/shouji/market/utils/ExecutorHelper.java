package com.zpj.shouji.market.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Z-P-J
 * @date 2019/6/1 15:16
 */
public final class ExecutorHelper {

    private static ExecutorService executorService;
    
    private ExecutorHelper() { }

    public static void init() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(
                    6, 15,
                    60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>()
            );
        }
    }

//    public static void submit(final CallBack callBack) {
//        submit(new Runnable() {
//            @Override
//            public void run() {
//                if (callBack != null) {
//                    callBack.onRun();
//                }
//            }
//        });
//    }
    
    public static void submit(final Runnable runnable) {
        if (executorService == null) {
            init();
        }
        executorService.submit(runnable);
    }

//    public interface CallBack {
//        void onRun();
//    }

    public static void destroy() {
        executorService.shutdown();
        executorService = null;
    }
    
}
