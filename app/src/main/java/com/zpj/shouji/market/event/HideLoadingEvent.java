package com.zpj.shouji.market.event;

import com.zpj.popup.interfaces.OnDismissListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Action;

public class HideLoadingEvent extends BaseEvent {

//    private Runnable runnable;
    private OnDismissListener onDismissListener;

    public HideLoadingEvent() {

    }

//    public Runnable getRunnable() {
//        return runnable;
//    }

    public OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public static void postEvent() {
        new HideLoadingEvent().post();
    }

    public static void post(OnDismissListener onDismissListener) {
        HideLoadingEvent event = new HideLoadingEvent();
        event.onDismissListener = onDismissListener;
        event.post();
    }

    public static void post(long delay, OnDismissListener onDismissListener) {
        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .doOnComplete(() -> post(onDismissListener))
                .subscribe();
    }

//    public static void post(Runnable onDismissRunnable) {
//        HideLoadingEvent event = new HideLoadingEvent();
//        event.runnable = onDismissRunnable;
//        event.post();
//    }

    public static void postDelayed(long delay) {
        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .doOnComplete(HideLoadingEvent::postEvent)
                .subscribe();
    }

}
