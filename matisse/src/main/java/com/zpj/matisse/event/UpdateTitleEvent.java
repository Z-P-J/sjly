package com.zpj.matisse.event;

public class UpdateTitleEvent {

    private final String title;

    public UpdateTitleEvent(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
