package com.zpj.shouji.market.event;

public class SignInEvent extends BaseEvent {

    private final boolean isSuccess;
    private final String errorMsg;

    public SignInEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
        this.errorMsg = "";
    }

    public SignInEvent(boolean isSuccess, String errorMsg) {
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
        new SignInEvent(true).post();
    }

    public static void postFailed(String errorMsg) {
        new SignInEvent(false, errorMsg).post();
    }

}
