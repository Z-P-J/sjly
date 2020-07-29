package com.zpj.shouji.market.event;

public class FabEvent extends BaseEvent {

    private final boolean show;

    private FabEvent(boolean show) {
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }

    public static void post(boolean show) {
        new FabEvent(show).post();
    }

}
