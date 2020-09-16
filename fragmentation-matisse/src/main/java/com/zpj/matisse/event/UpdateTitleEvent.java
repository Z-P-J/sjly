package com.zpj.matisse.event;

import org.greenrobot.eventbus.EventBus;

public class UpdateTitleEvent {

    private final String title;

    public UpdateTitleEvent(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static void post(String title) {
        EventBus.getDefault().post(new UpdateTitleEvent(title));
    }

}
