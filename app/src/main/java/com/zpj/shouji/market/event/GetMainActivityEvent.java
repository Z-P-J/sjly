package com.zpj.shouji.market.event;

import com.zpj.rxbus.RxSubscriber;
import com.zpj.shouji.market.ui.activity.MainActivity;
import com.zpj.shouji.market.ui.fragment.MainFragment;
import com.zpj.shouji.market.utils.Callback;

public class GetMainActivityEvent {

    private final Callback<MainActivity> callback;

    GetMainActivityEvent(Callback<MainActivity> callback) {
        this.callback = callback;
    }

    public Callback<MainActivity> getCallback() {
        return callback;
    }

    public static void post(Callback<MainActivity> callback) {
//        new GetMainActivityEvent(callback).post();
        RxSubscriber.post(new GetMainActivityEvent(callback));
    }

}
