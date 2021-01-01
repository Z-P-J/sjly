package com.zpj.recyclerview.state;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

public abstract class BaseViewHolder implements IViewHolder {

    private final int layoutId;
    protected View view;
    protected Context context;

    private Runnable onRetry;
    private Runnable onLogin;

    private String msg;

    public BaseViewHolder(int layoutId) {
        this.layoutId = layoutId;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public String getMsg(String defaultMsg) {
        if (TextUtils.isEmpty(msg)) {
            return defaultMsg;
        }
        return msg;
    }

    public void setOnRetry(Runnable onRetry) {
        this.onRetry = onRetry;
    }

    public void setOnLogin(Runnable onLogin) {
        this.onLogin = onLogin;
    }

    protected void onRetry() {
        if (onRetry != null) {
            onRetry.run();
        }
    }

    protected void onLogin() {
        if (onLogin != null) {
            onLogin.run();
        }
    }

    @Override
    public int getLayoutId() {
        return layoutId;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public View onCreateView(Context context) {
        this.context = context;
        if (getLayoutId() > 0) {
            this.view = LayoutInflater.from(context).inflate(getLayoutId(), null, false);
        }
//        onViewCreated(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        this.view = null;
    }

//    protected abstract void onViewCreated(View view);

}
