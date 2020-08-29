package com.zpj.shouji.market.constant;

public enum UpdateFlagAction {

    GOOD("good"),
    AT("theme"),
    COMMENT("review"),
    DISCOVER("discuss"),
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
