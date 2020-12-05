package com.zpj.shouji.market.event;

import com.zpj.rxbus.RxSubscriber;

public class SignInEvent {

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

    public void post() {
//        EventBus.getDefault().post(this);
        RxSubscriber.post(this);
    }

    public static void post(boolean isSuccess) {
        new SignInEvent(isSuccess).post();
    }

    public static void postSuccess() {
        new SignInEvent(true).post();
    }

    public static void postFailed(String errorMsg) {
        new SignInEvent(false, errorMsg).post();
    }

}
