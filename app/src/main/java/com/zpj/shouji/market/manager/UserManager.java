package com.zpj.shouji.market.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.event.EventBus;
import com.zpj.shouji.market.event.SignInEvent;
import com.zpj.shouji.market.event.SignUpEvent;
import com.zpj.shouji.market.model.MemberInfo;
import com.zpj.shouji.market.model.MessageInfo;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.utils.DeviceUtils;
import com.zpj.utils.PrefsHelper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

public final class UserManager {

    private static final UserManager USER_MANAGER = new UserManager();
    private MemberInfo memberInfo;
    private String cookie;
    private boolean isLogin;
//    private final List<WeakReference<OnSignInListener>> onSignInListeners = new ArrayList<>();
//    private final List<WeakReference<OnSignUpListener>> onSignUpListeners = new ArrayList<>();

    private static final long SYNC_MSG_DURATION = 60000;

    private long lastTime = 0;
    private MessageInfo messageInfo;

    public static UserManager getInstance() {
        return USER_MANAGER;
    }

    private UserManager() {

    }

    public void init() {
//        new ObservableTask<MemberInfo>(
//                emitter -> {
//                    String info = getUserInfo();
//                    if (!TextUtils.isEmpty(info)) {
//                        Document doc = ZHttp.parse(info);
//                        if ("登录成功".equals(doc.selectFirst("info").text())
//                                && !TextUtils.isEmpty(doc.selectFirst("jsession").text())) {
////                        memberInfo = MemberInfo.from(doc);
//                            emitter.onNext(MemberInfo.from(doc));
////                                signIn();
//                        }
//                    }
//                    emitter.onComplete();
//                })
//                .onNext(new ObservableTask.OnNextListener<MemberInfo, Document>() {
//
//                    @Override
//                    public ObservableTask<Document> onNext(MemberInfo data) throws Exception {
//                        memberInfo = data;
//                        Log.d("UserManager", "memberInfo=" + memberInfo);
//                        String sessionId = getSessionId();
//                        Log.d(getClass().getName(), "jsessionid=" + sessionId);
//                        return HttpApi.openConnection("http://tt.shouji.com.cn/app/xml_login_v4.jsp", Connection.Method.POST)
//                                .data("jsessionid", sessionId)
//                                .data("s", "12345678910")
//                                .data("stime", "" + System.currentTimeMillis())
//                                .data("setupid", "sjly2.9.9.9.3")
//                                .toHtml();
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .onSuccess(this::onSignIn)
//                .onError(throwable -> AToast.error(throwable.getMessage()))
//                .subscribe();
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

    public void signOut(Context context) {
        new AlertDialogFragment()
                .setTitle("确认注销？")
                .setContent("您将注销当前登录的账户，确认继续？")
                .setPositiveButton(popup -> {
                    memberInfo = null;
                    setUserInfo("");
                    setCookie("");
                    isLogin = false;
                    PictureUtil.saveDefaultIcon(() -> EventBus.sendSignOutEvent());
                })
                .show(context);
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
        PrefsHelper.with().putString("cookie", cookie);
    }

    public MessageInfo getMessageInfo() {
        if (messageInfo == null) {
            return new MessageInfo();
        }
        return messageInfo;
    }

    public void setUserInfo(String info) {
        PrefsHelper.with("user_info").putString("user_info", info);
    }

    public void saveUserInfo() {
        if (memberInfo != null) {
            setUserInfo(memberInfo.toStr());
//            UserInfoChangeEvent.post();
            EventBus.sendUserInfoChangeEvent();
        }
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

    public String getUserNickName() {
        if (isLogin()) {
            return memberInfo.getMemberNickName();
        }
        return "";
    }

    public String getUserName() {
        if (isLogin()) {
            return memberInfo.getMemberName();
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
        String sn = null;
        if (isLogin()) {
            sn = memberInfo.getSn();
        }
        if (TextUtils.isEmpty(sn)) {
//            return "0123456789";
            return DeviceUtils.getSerial();
        } else {
            return sn;
        }
    }

    public boolean isLogin() {
//        return false;
        return memberInfo != null;
    }

//    public void rsyncMessage(IHttp.OnSuccessListener<MessageInfo> listener) {
//        long currentTime = System.currentTimeMillis();
//        if (lastTime == 0 || lastTime - currentTime > DEFAULT_RESUME_DELTA_TIME) {
//            lastTime = currentTime;
//            HttpApi.rsyncMessageApi()
//                    .onSuccess(data -> {
//                        messageInfo = MessageInfo.from(data);
//                        Log.d("rsyncMessage", "messageInfo=" + messageInfo);
//                        if (listener != null) {
//                            listener.onSuccess(messageInfo);
//                        }
//                    })
//                    .subscribe();
//        }
//    }

    public void rsyncMessage(boolean force) {
        if (isLogin()) {
            if (force) {
                messageInfo = null;
            }
            long currentTime = System.currentTimeMillis();
            if (messageInfo == null || lastTime == 0 || currentTime - lastTime > SYNC_MSG_DURATION) {
                lastTime = currentTime;
                HttpApi.rsyncMessageApi()
                        .onSuccess(data -> {
                            Log.d("rsyncMessage", "data=" + data);
                            messageInfo = MessageInfo.from(data);
                            Log.d("rsyncMessage", "messageInfo=" + messageInfo);
                            EventBus.post(messageInfo);
                        })
                        .subscribe();
            } else {
                EventBus.post(messageInfo);
            }
        }
    }

    private void signIn() {
        String sessionId = getSessionId();
        Log.d(getClass().getName(), "jsessionid=" + sessionId);
        HttpApi.openConnection("http://tt.shouji.com.cn/appv3/xml_login_v4.jsp", Connection.Method.GET)
                .data("jsessionid", sessionId)
                .data("s", "12345678910")
                .data("stime", "" + System.currentTimeMillis())
                .data("setupid", "sjly3.1")
                .cookie("")
                .execute()
                .onSuccess(response -> {
                    for (Map.Entry<String, String> entry : response.cookies().entrySet()) {
                        Log.d("signIn cookies", entry.getKey() + " = " + entry.getValue());
                    }
                    for (Map.Entry<String, String> entry : response.headers().entrySet()) {
                        Log.d("signIn headers", entry.getKey() + " = " + entry.getValue());
                    }
                    String cookie = response.cookieStr();
                    if (!TextUtils.isEmpty(cookie)) {
                        setCookie(cookie);
                    }
                    onSignIn(response.parse());
                })
                .onError(throwable -> AToast.error(throwable.getMessage()))
                .subscribe();
    }

    public void signIn(String userName, String password) {
        AToast.normal("isLogin=" + isLogin());
        HttpApi.openConnection("http://tt.shouji.com.cn/appv3/xml_login_v4.jsp", Connection.Method.POST)
                .data("openid", "")
                .data("s", "12345678910")
                .data("stime", "" + System.currentTimeMillis())
                .data("setupid", "sjly3.1")
                .data("m", userName)
                .data("p", encodePassword(password))
                .data("opentype", "")
                .data("logo", "")
                .data("logo2", "")
                .data("n", "")
                .data("jsessionid", "")
                .execute()
                .onSuccess(new IHttp.OnSuccessListener<Connection.Response>() {
                    @Override
                    public void onSuccess(Connection.Response response) throws Exception {
                        for (Map.Entry<String, String> entry : response.cookies().entrySet()) {
                            Log.d("signIn cookies", entry.getKey() + " = " + entry.getValue());
                        }
                        for (Map.Entry<String, String> entry : response.headers().entrySet()) {
                            Log.d("signIn headers", entry.getKey() + " = " + entry.getValue());
                        }
                        String cookie = response.cookieStr();
                        if (!TextUtils.isEmpty(cookie)) {
                            setCookie(cookie);
                        }
                        onSignIn(response.parse());
                    }
                })
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
                rsyncMessage(true);
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
                        if (!TextUtils.isEmpty(cookie)) {
                            setCookie(cookie);
                        }
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
        PictureUtil.saveIcon(SignInEvent::postSuccess);
    }

    private void onSignInFailed(String info) {
        PictureUtil.saveDefaultIcon(() -> SignInEvent.postFailed(info));
    }

    private void onSignUpSuccess() {
        isLogin = true;
        SignUpEvent.postSuccess();
    }

    private void onSignUpFailed(String info) {
        isLogin = false;
        SignUpEvent.postFailed(info);
    }

}
