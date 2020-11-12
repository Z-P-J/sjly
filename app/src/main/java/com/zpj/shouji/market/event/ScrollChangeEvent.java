package com.zpj.shouji.market.event;

import org.greenrobot.eventbus.EventBus;

public class ScrollChangeEvent {

    private float percent;

    private ScrollChangeEvent(float percent) {
        this.percent = percent;
    }

    public float getPercent() {
        return percent;
    }

    public static void post(float percent) {
        EventBus.getDefault().post(new ScrollChangeEvent(percent));
    }

}
