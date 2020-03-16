package com.zpj.shouji.market.event;

import org.greenrobot.eventbus.EventBus;

public class BaseEvent {

    public void post() {
        EventBus.getDefault().post(this);
    }

}
