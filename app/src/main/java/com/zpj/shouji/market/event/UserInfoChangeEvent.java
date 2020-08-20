package com.zpj.shouji.market.event;

import org.greenrobot.eventbus.EventBus;

public class UserInfoChangeEvent {

    private UserInfoChangeEvent() {

    }

    public static void post() {
        EventBus.getDefault().post(new UserInfoChangeEvent());
    }

}
