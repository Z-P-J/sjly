package com.zpj.shouji.market.manager;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.zpj.fragmentation.dialog.IDialog;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.http.core.HttpHeader;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.DocumentParser;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.model.MemberInfo;
import com.zpj.shouji.market.model.MessageInfo;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.toast.ZToast;
import com.zpj.utils.DeviceUtils;
import com.zpj.utils.PrefsHelper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class UserManager {

    private static UserManager USER_MANAGER;
    private MemberInfo memberInfo;
    private String cookie;
    private boolean isLogin;

    private long lastTime = 0;
    private MessageInfo messageInfo;

    public static UserManager getInstance() {
        if (USER_MANAGER == null) {
            synchronized (UserManager.class) {
                USER_MANAGER = new UserManager();
            }
        }
        return USER_MANAGER;
    }

    private UserManager() {

    }

    public void onDestroy() {
        USER_MANAGER = null;
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
//                .onError(throwable -> ZToast.error(throwable.getMessage()))
//                .subscribe();
        String info = getUserInfo();
        if (!TextUtils.isEmpty(info)) {
            Document doc = DocumentParser.parse(info);
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
                .setPositiveButton(new IDialog.OnButtonClickListener<AlertDialogFragment>() {
                    @Override
                    public void onClick(AlertDialogFragment fragment, int which) {
                        memberInfo = null;
                        setUserInfo("");
                        setCookie("");
                        isLogin = false;
                        PictureUtil.saveDefaultIcon(EventBus::sendSignOutEvent);
                    }
                })
                .show(context);
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
        PrefsHelper.with().putString("cookie", cookie);
    }

    public MessageInfo getMessageInfo() {
        if (messageInfo == null) {
            messageInfo = new MessageInfo();
        }
        Log.d("UserManager", "messageInfo=" + messageInfo);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Build.getSerial();
            } else {
                return DeviceUtils.getSerial();
            }
        } else {
            return sn;
        }
    }

    public boolean isLogin() {
//        return false;
        return memberInfo != null;
    }

    public boolean isAutoUser() {
//        return false;
        return memberInfo != null && memberInfo.isAutoUser();
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
            long SYNC_MSG_DURATION = 60000;
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

    public void signIn(String jsessionId) {
        HttpApi.get("/appv3/xml_login_v4.jsp")
                .data("jsessionid", jsessionId)
                .data("s", "12345678910")
                .data("stime", "" + System.currentTimeMillis())
                .data("setupid", "sjly3.1")
                .execute()
                .onSuccess(response -> {
                    String cookie = response.header(HttpHeader.SET_COOKIE);
                    if (!TextUtils.isEmpty(cookie)) {
                        setCookie(cookie);
                    }
                    onSignIn(DocumentParser.parse(response.body()));
                })
                .onError(throwable -> onSignInFailed(throwable.getMessage()))
                .subscribe();
    }

    private void signIn() {
        String sessionId = getSessionId();
        Log.d(getClass().getName(), "jsessionid=" + sessionId);
        signIn(sessionId);
//        HttpApi.get("http://tt.shouji.com.cn/appv3/xml_login_v4.jsp")
//                .data("jsessionid", sessionId)
//                .data("s", "12345678910")
//                .data("stime", "" + System.currentTimeMillis())
//                .data("setupid", "sjly3.1")
////                .cookie("")
//                .execute()
//                .onSuccess(response -> {
////                    for (Map.Entry<String, String> entry : response.cookies().entrySet()) {
////                        Log.d("signIn cookies", entry.getKey() + " = " + entry.getValue());
////                    }
////                    for (Map.Entry<String, String> entry : response.headers().entrySet()) {
////                        Log.d("signIn headers", entry.getKey() + " = " + entry.getValue());
////                    }
////                    String cookie = response.cookieStr();
//                    String cookie = response.header(HttpHeader.SET_COOKIE);
//                    if (!TextUtils.isEmpty(cookie)) {
//                        setCookie(cookie);
//                    }
//
//                    onSignIn(DocumentParser.parse(response.body()));
//                })
//                .onError(throwable -> ZToast.error(throwable.getMessage()))
//                .subscribe();
    }

    public void signIn(String userName, String password) {
        ZToast.normal("isLogin=" + isLogin());
        EventBus.showLoading("登录中...");
        HttpApi.post("/appv3/xml_login_v4.jsp")
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
                .onSuccess(response -> {
                    String cookie = response.header(HttpHeader.SET_COOKIE);
                    if (!TextUtils.isEmpty(cookie)) {
                        setCookie(cookie);
                    }

                    onSignIn(DocumentParser.parse(response.body()));
                })
                .onError(throwable -> onSignInFailed(throwable.getMessage()))
                .subscribe();
    }

    public void signInByQQ(String openId, String name, String logo1, String logo2) {
        ZToast.normal("isLogin=" + isLogin());
        HttpApi.post("/appv3/xml_login_v4.jsp")
                .data("openid", openId)
                .data("s", "12345678910")
                .data("stime", "" + System.currentTimeMillis())
                .data("versioncode", "210")
                .data("version", "3.1")
                .data("setupid", "sjly3.1")
                .data("opentype", "qq")
                .data("n", name)
                .data("logo", logo1)
                .data("logo2", logo2)
                .data("jsessionid", "")
                .execute()
                .onSuccess(response -> {
                    String cookie = response.header(HttpHeader.SET_COOKIE);
                    if (!TextUtils.isEmpty(cookie)) {
                        setCookie(cookie);
                    }
                    onSignIn(DocumentParser.parse(response.body()));
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
        ZToast.normal(info);
        if (memberInfo == null) {
            onSignInFailed(info);
        } else {
            onSignInSuccess();
        }
    }

    public void signUp(String account, String password, String email) {
        HttpApi.post("/app/xml_register_v4.jsp?")
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
//                    for (Map.Entry<String, String> entry : response.cookies().entrySet()) {
//                        Log.d("SignInLayout", entry.getKey() + " = " + entry.getValue());
//                    }

                    String cookie = response.header(HttpHeader.SET_COOKIE);

//                    String cookie = response.cookieStr();

//                    Document doc = response.parse();
                    Document doc = DocumentParser.parse(response.body());
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
        PictureUtil.saveIcon(EventBus::signInSuccess);
    }

    private void onSignInFailed(String info) {
        PictureUtil.saveDefaultIcon(() -> EventBus.signInFailed(info));
    }

    private void onSignUpSuccess() {
        isLogin = true;
//        SignUpEvent.postSuccess();
        EventBus.signUpSuccess();
    }

    private void onSignUpFailed(String info) {
        isLogin = false;
//        SignUpEvent.postFailed(info);
        EventBus.signUpFailed(info);
    }

}
