package com.zpj.shouji.market.event;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ShowLoadingEvent extends BaseEvent {

    private final String text;
    private boolean isUpdate = false;

    public ShowLoadingEvent(String text) {
        this.text = text;
    }

    public ShowLoadingEvent(String text, boolean isUpdate) {
        this.text = text;
        this.isUpdate = isUpdate;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public String getText() {
        return text;
    }

    public static void post(String text) {
        Observable.empty()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> new ShowLoadingEvent(text).post())
                .subscribe();
    }

    public static void post(String text, boolean isUpdate) {
        Observable.empty()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> new ShowLoadingEvent(text, isUpdate).post())
                .subscribe();
    }

}
