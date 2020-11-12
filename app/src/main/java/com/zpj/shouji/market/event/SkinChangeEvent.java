package com.zpj.shouji.market.event;

import org.greenrobot.eventbus.EventBus;

public class SkinChangeEvent {

    private boolean isNight;

    private SkinChangeEvent(boolean isNight) {
        this.isNight = isNight;
    }

    public boolean isNight() {
        return isNight;
    }

    public static void post(boolean isNight) {
        EventBus.getDefault().post(new SkinChangeEvent(isNight));
    }

}
