package com.zpj.shouji.market.manager;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.felix.atoast.library.AToast;
import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.model.MemberInfo;
import com.zpj.shouji.market.api.HttpApi;
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
    private final List<WeakReference<OnSignInListener>> onSignInListeners = new ArrayList<>();
    private final List<WeakReference<OnSignUpListener>> onSignUpListeners = new ArrayList<>();

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
                signIn();
            }
        }
    }

    public void signOut() {
        memberInfo = null;
        setUserInfo("");
        setCookie("");
        isLogin = false;
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

    public String getUserId() {
        if (isLogin()) {
            return memberInfo.getMemberId();
        }
        return "";
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

    public String getSn() {
        if (isLogin()) {
            return memberInfo.getSn();
        }
        return "0123456789";
    }

    public boolean isLogin() {
//        return false;
        return memberInfo != null;
    }

    private void signIn() {
        String sessionId = getSessionId();
        Log.d(getClass().getName(), "jsessionid=" + sessionId);
        HttpApi.openConnection("http://tt.shouji.com.cn/app/xml_login_v4.jsp?version=2.9.9.9.3", Connection.Method.POST)
                .data("jsessionid", sessionId)
                .data("s", "12345678910")
                .data("stime", "" + System.currentTimeMillis())
                .data("setupid", "sjly2.9.9.9.3")
                .toHtml()
                .onSuccess(this::onSignIn)
                .onError(throwable -> AToast.error(throwable.getMessage()))
                .subscribe();
    }

    public void signIn(String userName, String password) {
        AToast.normal("isLogin=" + isLogin());
        HttpApi.openConnection("http://tt.shouji.com.cn/app/xml_login_v4.jsp?version=2.9.9.9.3", Connection.Method.POST)
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
                .onSuccess(this::onSignIn)
                .onError(throwable -> onSignInFailed(throwable.getMessage()))
                .subscribe();
    }

    private void onSignIn(Document data) {
        Log.d("UserManager", "data=" + data.toString());
        String info;
        Log.d("UserManager", "hasMember=" + data.has("member"));
        Log.d("UserManager", "size=" + data.select("member").size());
        if (data.has("member")) {
            data.selectFirst("sjly").remove();
            info = data.selectFirst("member").selectFirst("info").text().trim();
            if ("登录成功".equals(info)) {
                memberInfo = MemberInfo.from(data);
                Log.d("UserManager", "memberInfo=" + memberInfo);
                setUserInfo(data.toString());
                onSignInSuccess();
                AToast.normal("登录成功");
                return;
            }
        }
        info = data.selectFirst("info").text().trim();
        if (TextUtils.isEmpty(info)) {
            info = "登录失败";
        }
        AToast.normal(info);
        if (memberInfo == null) {
            onSignInFailed(info);
        } else {
            onSignInSuccess();
        }
    }

    public void signUp(String account, String password, String email) {
        HttpApi.openConnection("http://tt.shouji.com.cn/app/xml_register_v4.jsp?", Connection.Method.POST)
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
                        onSignUpFailed(info);
                    } else {
                        memberInfo = MemberInfo.from(doc);
                        setCookie(cookie);
                        setUserInfo(doc.toString());
                        onSignUpSuccess();
                    }
                })
                .onError(throwable -> onSignUpFailed(throwable.getMessage()))
                .subscribe();
    }

    private void sign() {

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

    public void removeOnSignInListener(OnSignInListener onSignInListener) {
        synchronized (onSignInListeners) {
            for (WeakReference<OnSignInListener> listener : onSignInListeners) {
                if (listener != null && listener.get() != null && listener.get() == onSignInListener) {
                    onSignInListeners.remove(listener);
                    return;
                }
            }
        }
    }

    private void onSignUpSuccess() {
        synchronized (onSignUpListeners) {
            isLogin = true;
            for (WeakReference<OnSignUpListener> listener : onSignUpListeners) {
                if (listener != null && listener.get() != null) {
                    listener.get().onSignUpSuccess();
                }
            }
        }
    }

    private void onSignUpFailed(String info) {
        synchronized (onSignUpListeners) {
            isLogin = false;
            for (WeakReference<OnSignUpListener> listener : onSignUpListeners) {
                if (listener != null && listener.get() != null) {
                    listener.get().onSignUpFailed(info);
                }
            }
        }
    }

    public void addOnSignUpListener(OnSignUpListener listener) {
        synchronized (onSignUpListeners) {
            if (isLogin) {
                listener.onSignUpSuccess();
            }
            onSignUpListeners.add(new WeakReference<>(listener));
        }
    }

    public void removeOnSignUpListener(OnSignUpListener onSignUpListener) {
        synchronized (onSignUpListeners) {
            for (WeakReference<OnSignUpListener> listener : onSignUpListeners) {
                if (listener != null && listener.get() != null && listener.get() == onSignUpListener) {
                    onSignUpListeners.remove(listener);
                    return;
                }
            }
        }
    }

    public interface OnSignInListener {
        void onSignInSuccess();
        void onSignInFailed(String errInfo);
    }

    public interface OnSignUpListener {
        void onSignUpSuccess();
        void onSignUpFailed(String errInfo);
    }

}
