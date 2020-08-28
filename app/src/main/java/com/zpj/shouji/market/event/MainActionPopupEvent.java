package com.zpj.shouji.market.event;

import com.zpj.shouji.market.ui.widget.popup.MainActionPopup;

import org.greenrobot.eventbus.EventBus;

public class MainActionPopupEvent {

    private boolean isShow;

    public MainActionPopupEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }

    public static void post(boolean isShow) {
        EventBus.getDefault().post(new MainActionPopupEvent(isShow));
    }

}
