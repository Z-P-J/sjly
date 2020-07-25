package com.zpj.shouji.market.event;

public class ColorChangeEvent extends BaseEvent {

    private final boolean isDark;

    private ColorChangeEvent(boolean isDark) {
        this.isDark = isDark;
    }

    public boolean isDark() {
        return isDark;
    }

    public static void post(boolean isDark) {
        new ColorChangeEvent(isDark).post();
    }

}
