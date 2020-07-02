package com.zpj.shouji.market.event;

import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;

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
