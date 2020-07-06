package com.zpj.shouji.market.constant;

public enum UpdateFlagAction {

    GOOD("good"),
    AT("theme"),
    COMMENT("review"),
    DISCOVER("review"),
    PRIVATE("review"),
    FAN("review");;
    private String action;
    UpdateFlagAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

}
