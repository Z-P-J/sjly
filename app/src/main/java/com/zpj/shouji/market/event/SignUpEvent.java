package com.zpj.shouji.market.event;

public class SignUpEvent extends BaseEvent {

    private final boolean isSuccess;
    private final String errorMsg;

    public SignUpEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
        this.errorMsg = "";
    }

    public SignUpEvent(boolean isSuccess, String errorMsg) {
        this.isSuccess = isSuccess;
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public static void postSuccess() {
        new SignUpEvent(true).post();
    }

    public static void postFailed(String errorMsg) {
        new SignUpEvent(false, errorMsg).post();
    }

}
