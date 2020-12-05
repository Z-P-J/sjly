package com.zpj.shouji.market.event;

import com.zpj.fragmentation.dialog.IDialog;
import com.zpj.rxbus.RxSubscriber;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class HideLoadingEvent {

    private IDialog.OnDismissListener onDismissListener;

    public HideLoadingEvent() {

    }

    public IDialog.OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public static void post() {
        RxSubscriber.post(new HideLoadingEvent());
    }

    public static void post(IDialog.OnDismissListener onDismissListener) {
        HideLoadingEvent event = new HideLoadingEvent();
        event.onDismissListener = onDismissListener;
        RxSubscriber.post(event);
    }

    public static void post(long delay, IDialog.OnDismissListener onDismissListener) {
        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .doOnComplete(() -> post(onDismissListener))
                .subscribe();
    }

    public static void postDelayed(long delay) {
        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .doOnComplete(HideLoadingEvent::post)
                .subscribe();
    }

}
