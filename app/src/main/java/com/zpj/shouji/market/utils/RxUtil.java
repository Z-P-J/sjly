package com.zpj.shouji.market.utils;

import com.zpj.http.core.HttpObservable;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxUtil {

    public static HttpObservable<Runnable> with(Runnable runnable) {
        return new HttpObservable<>(Observable.create((ObservableOnSubscribe<Runnable>) emitter -> {
            runnable.run();
            emitter.onNext(runnable);
            emitter.onComplete();
        })).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }



}
