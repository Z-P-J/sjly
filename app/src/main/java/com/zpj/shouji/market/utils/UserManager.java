package com.zpj.shouji.market.utils;

import android.text.TextUtils;
import android.util.Log;

import com.felix.atoast.library.AToast;
import com.zpj.http.ZHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.model.MemberInfo;
import com.zpj.utils.PrefsHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class UserManager {

    private static final UserManager USER_MANAGER = new UserManager();
    private MemberInfo memberInfo;
    private String cookie;
    private boolean isLogin;
    private final List<WeakReference<OnLoginListener>> onLoginListeners = new ArrayList<>();

    public static UserManager getInstance() {
        return USER_MANAGER;
    }

    private UserManager() {

    }

    public void init() {
        String info = getUserInfo();
        if (!TextUtils.isEmpty(info)) {
            Document doc = ZHttp.parse(info);
            if ("登录成功".equals(doc.selectFirst("info").text())
                    && !TextUtils.isEmpty(doc.selectFirst("jsession").text())) {
                memberInfo = MemberInfo.from(doc);
                Log.d("UserManager", "memberInfo=" + memberInfo);
                login();
            }
        }
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
        PrefsHelper.with().putString("cookie", cookie);
    }

    public void setUserInfo(String info) {
        PrefsHelper.with("user_info").putString("user_info", info);
    }

    public String getUserInfo() {
        return PrefsHelper.with("user_info").getString("user_info", "");
    }

    public MemberInfo getMemberInfo() {
        return memberInfo;
    }

    public String getCookie() {
        if (TextUtils.isEmpty(cookie)) {
            cookie = PrefsHelper.with().getString("cookie", "");
        }
        return cookie;
    }

    public String getSessionId() {
        if (isLogin()) {
            return memberInfo.getSessionId();
        }
        return "";
    }

    public boolean isLogin() {
//        return false;
        return memberInfo != null;
    }

    private void login() {
        Log.d(getClass().getName(), "jsessionid=" + getSessionId());
        HttpApi.openConnection("http://tt.shouji.com.cn/app/xml_login_v4.jsp?versioncode=198&version=2.9.9.9.3")
                .data("jsessionid", getSessionId())
                .data("s", "12345678910")
                .data("stime", "" + System.currentTimeMillis())
                .data("setupid", "sjly2.9.9.9.3")
                .toHtml()
                .onSuccess(data -> {
                    Log.d("UserManager", "data=" + data.toString());
                    String info = data.selectFirst("info").text();
                    if ("登录成功".equals(info)) {

                        memberInfo = MemberInfo.from(data);
                        setUserInfo(data.toString());
                        onLoginSuccess();
                        AToast.normal("登录成功");
                    } else {
                        AToast.normal(info);
                        onLoginFailed(info);
                    }
                })
                .onError(throwable -> AToast.error(throwable.getMessage()))
                .subscribe();
    }

    public void login(String userName, String password) {
        AToast.normal("isLogin=" + isLogin());
        HttpApi.openConnection("http://tt.shouji.com.cn/app/xml_login_v4.jsp?versioncode=198&version=2.9.9.9.3")
                .data("openid", "")
                .data("s", "12345678910")
                .data("stime", "" + System.currentTimeMillis())
                .data("setupid", "sjly2.9.9.9.3")
                .data("m", userName)
                .data("p", StringUtils.jiami(password))
                .data("opentype", "")
                .data("logo", "")
                .data("logo2", "")
                .data("n", "")
                .data("jsessionid", "")
                .toHtml()
                .onSuccess(data -> {
                    Log.d("UserManager", "data=" + data.toString());
                    data.remove("sjly");
                    String info = data.selectFirst("info").text();
                    if ("登录成功".equals(info)) {
                        memberInfo = MemberInfo.from(data);
                        setUserInfo(data.toString());
                        onLoginSuccess();
                        AToast.normal("登录成功");
                    } else {
                        AToast.normal(info);
                        onLoginFailed(info);
                    }
                })
                .onError(throwable -> AToast.error(throwable.getMessage()))
                .subscribe();
    }

    private void onLoginSuccess() {
        synchronized (onLoginListeners) {
            isLogin = true;
            for (WeakReference<OnLoginListener> listener : onLoginListeners) {
                if (listener != null && listener.get() != null) {
                    listener.get().onLoginSuccess();
                }
            }
        }
    }

    private void onLoginFailed(String info) {
        synchronized (onLoginListeners) {
            isLogin = false;
            for (WeakReference<OnLoginListener> listener : onLoginListeners) {
                if (listener != null && listener.get() != null) {
                    listener.get().onLoginFailed(info);
                }
            }
        }
    }

    public void addOnLoginListener(OnLoginListener listener) {
        synchronized (onLoginListeners) {
            if (isLogin) {
                listener.onLoginSuccess();
            }
            onLoginListeners.add(new WeakReference<>(listener));
        }
    }

    public void removeOnLoginListener(OnLoginListener onLoginListener) {
        synchronized (onLoginListeners) {
            for (WeakReference<OnLoginListener> listener : onLoginListeners) {
                if (listener != null && listener.get() != null && listener.get() == onLoginListener) {
                    onLoginListeners.remove(listener);
                    return;
                }
            }
        }
    }

    public interface OnLoginListener {
        void onLoginSuccess();
        void onLoginFailed(String errInfo);
    }

}
