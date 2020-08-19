package com.zpj.shouji.market.event;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class HideLoadingEvent extends BaseEvent {

    public HideLoadingEvent() {

    }

    public static void postEvent() {
        new HideLoadingEvent().post();
    }

    public static void postDelayed(long delay) {
        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .doOnComplete(HideLoadingEvent::postEvent)
                .subscribe();
    }

}
