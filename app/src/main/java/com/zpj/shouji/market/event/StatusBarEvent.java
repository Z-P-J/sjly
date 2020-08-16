package com.zpj.shouji.market.event;

public class StatusBarEvent extends BaseEvent {

    private final boolean isLightMode;

    private StatusBarEvent(boolean isLightMode) {
        this.isLightMode = isLightMode;
    }

    public boolean isLightMode() {
        return isLightMode;
    }

    public static void post(boolean isLight) {
        new StatusBarEvent(isLight).post();
    }
}
