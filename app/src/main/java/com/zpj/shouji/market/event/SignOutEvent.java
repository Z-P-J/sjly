package com.zpj.shouji.market.event;

public class SignOutEvent extends BaseEvent {

    private SignOutEvent() {

    }

    public static void postEvent() {
        new SignOutEvent().post();
    }

}
