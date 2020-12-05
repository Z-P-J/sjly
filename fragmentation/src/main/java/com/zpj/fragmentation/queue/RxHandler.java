package com.zpj.fragmentation.queue;

import com.dhh.rxlife2.RxLife;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

public final class RxHandler {

    private RxHandler() {
//        throw new IllegalAccessException();
    }

    public static void post(Action action) {
        Observable.empty()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(action)
                .subscribe();
    }

    public static void post(Action action, long delayMillis) {
        Observable.timer(delayMillis, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(action)
                .subscribe();
    }

}
