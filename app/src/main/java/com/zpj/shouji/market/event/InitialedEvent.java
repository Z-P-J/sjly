package com.zpj.shouji.market.event;

import org.greenrobot.eventbus.EventBus;

public class InitialedEvent {

    private InitialedEvent() {

    }

    public static void post() {
        EventBus.getDefault().post(new InitialedEvent());
    }

}
