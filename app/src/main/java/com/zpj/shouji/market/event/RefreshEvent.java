package com.zpj.shouji.market.event;

public class RefreshEvent extends BaseEvent {

    public static void postEvent() {
        new RefreshEvent().post();
    }

}
