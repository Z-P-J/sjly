package com.zpj.shouji.market.event;

public class ToolbarColorChangeEvent extends BaseEvent {

    private final int color;
    private final boolean isLightStyle;

    private ToolbarColorChangeEvent(int color, boolean isLightStyle) {
        this.color = color;
        this.isLightStyle = isLightStyle;
    }

    public int getColor() {
        return color;
    }

    public boolean isLightStyle() {
        return isLightStyle;
    }

    public static void post(int color, boolean isLightStyle) {
        new ToolbarColorChangeEvent(color, isLightStyle).post();
    }

}
