package com.zpj.shouji.market.event;

public class ShowLoadingEvent extends BaseEvent {

    private final String text;
    private boolean isUpdate = false;

    public ShowLoadingEvent(String text) {
        this.text = text;
    }

    public ShowLoadingEvent(String text, boolean isUpdate) {
        this.text = text;
        this.isUpdate = isUpdate;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public String getText() {
        return text;
    }

    public static void post(String text) {
        new ShowLoadingEvent(text).post();
    }

    public static void post(String text, boolean isUpdate) {
        new ShowLoadingEvent(text, isUpdate).post();
    }

}
