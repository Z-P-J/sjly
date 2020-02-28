package com.zpj.shouji.market.utils;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.felix.atoast.library.AToast;
import com.zpj.http.ZHttp;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.model.MemberInfo;
import com.zpj.utils.PrefsHelper;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class UserManager {

    private static final UserManager USER_MANAGER = new UserManager();
    private MemberInfo memberInfo;
    private String cookie;
    private boolean isLogin;
    private final List<WeakReference<OnLoginListener>> onLoginListeners = new ArrayList<>();
    private final List<WeakReference<OnSignInListener>> onSignInListeners = new ArrayList<>();

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
                .data("p", encodePassword(password))
                .data("opentype", "")
                .data("logo", "")
                .data("logo2", "")
                .data("n", "")
                .data("jsessionid", "")
                .toHtml()
                .onSuccess(data -> {
                    Log.d("UserManager", "data=" + data.toString());
//                    if (data.select("sjly").size() > 1) {
//                        data.remove("sjly");
//                    }
                    String errInfo = "";
                    for (Element element : data.select("sjly")) {
                        String info = element.selectFirst("info").text();
                        if ("登录成功".equals(info)) {
                            memberInfo = MemberInfo.from(data);
                            setUserInfo(data.toString());
                            onLoginSuccess();
                            AToast.normal("登录成功");
                            return;
                        } else {
                            if (!TextUtils.isEmpty(info)) {
                                errInfo = info;
                            }
                        }
                    }
                    if (TextUtils.isEmpty(errInfo)) {
                        errInfo = "登录失败";
                    }
                    AToast.normal(errInfo);
                    onLoginFailed(errInfo);
//                    String info = data.selectFirst("info").text();
//                    if ("登录成功".equals(info)) {
//                        memberInfo = MemberInfo.from(data);
//                        setUserInfo(data.toString());
//                        onLoginSuccess();
//                        AToast.normal("登录成功");
//                    } else {
//                        AToast.normal(info);
//                        onLoginFailed(info);
//                    }
                })
                .onError(throwable -> onLoginFailed(throwable.getMessage()))
                .subscribe();
    }

    public void signIn(String account, String password, String email) {
        HttpApi.openConnection("http://tt.shouji.com.cn/app/xml_register_v4.jsp?")
                .data("m", account)
                .data("p", password)
                .data("MemberEmail", email)
                .data("n", "")
                .data("logo", "")
                .data("logo2", "")
                .data("openid", "")
                .data("s", "12345678910")
                .execute()
                .onSuccess(response -> {
                    for (Map.Entry<String, String> entry : response.cookies().entrySet()) {
                        Log.d("SignInLayout", entry.getKey() + " = " + entry.getValue());
                    }
                    String cookie = response.cookieStr();

                    Document doc = response.parse();
                    if ("failed".equals(doc.selectFirst("result").text())) {
                        String info = doc.selectFirst("info").text();
                        onSignInFailed(info);
                    } else {
                        memberInfo = MemberInfo.from(doc);
                        setCookie(cookie);
                        setUserInfo(doc.toString());
                        onSignInSuccess();
                    }
                })
                .onError(throwable -> onSignInFailed(throwable.getMessage()))
                .subscribe();
    }

    private String encodePassword(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(string.getBytes(StandardCharsets.UTF_8));
            return android.util.Base64.encodeToString(md5.digest(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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

    private void onSignInSuccess() {
        synchronized (onSignInListeners) {
            isLogin = true;
            for (WeakReference<OnSignInListener> listener : onSignInListeners) {
                if (listener != null && listener.get() != null) {
                    listener.get().onSignInSuccess();
                }
            }
        }
    }

    private void onSignInFailed(String info) {
        synchronized (onSignInListeners) {
            isLogin = false;
            for (WeakReference<OnSignInListener> listener : onSignInListeners) {
                if (listener != null && listener.get() != null) {
                    listener.get().onSignInFailed(info);
                }
            }
        }
    }

    public void addOnSignInListener(OnSignInListener listener) {
        synchronized (onSignInListeners) {
            if (isLogin) {
                listener.onSignInSuccess();
            }
            onSignInListeners.add(new WeakReference<>(listener));
        }
    }

    public void removeOnSignInListener(OnSignInListener onLoginListener) {
        synchronized (onSignInListeners) {
            for (WeakReference<OnSignInListener> listener : onSignInListeners) {
                if (listener != null && listener.get() != null && listener.get() == onLoginListener) {
                    onSignInListeners.remove(listener);
                    return;
                }
            }
        }
    }

    public interface OnLoginListener {
        void onLoginSuccess();
        void onLoginFailed(String errInfo);
    }

    public interface OnSignInListener {
        void onSignInSuccess();
        void onSignInFailed(String errInfo);
    }

}
