package com.zpj.shouji.market.event;

public class HideLoadingEvent extends BaseEvent {

    public HideLoadingEvent() {

    }

    public static void postEvent() {
        new HideLoadingEvent().post();
    }

}
