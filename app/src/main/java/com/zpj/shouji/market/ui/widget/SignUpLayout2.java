//package com.zpj.shouji.market.ui.widget;
//
//import android.content.Context;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.zpj.toast.ZToast;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.event.SignInEvent;
//import com.zpj.shouji.market.manager.UserManager;
//import com.zpj.widget.editor.AccountInputView;
//import com.zpj.widget.editor.PasswordInputView;
//import com.zpj.widget.editor.SubmitView;
//import com.zpj.utils.ScreenUtils;
//import com.zpj.widget.editor.validator.LengthValidator;
//
//import org.greenrobot.eventbus.Subscribe;
//
//public class SignUpLayout2 extends LinearLayout { // implements UserManager.OnSignInListener
//
//    private AccountInputView etAccount;
//    private PasswordInputView etPassword;
//
//    private TextView tvFogotPassword;
//    private TextView tvLoginFailed;
//    private SubmitView tvSignUp;
//
//    public SignUpLayout2(Context context) {
//        this(context, null);
//    }
//
//    public SignUpLayout2(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public SignUpLayout2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context);
//    }
//
//    private void init(Context context) {
//        setOrientation(VERTICAL);
//        LayoutInflater.from(context).inflate(R.layout.layout_sign_up2, this, true);
//
//        int padding = ScreenUtils.dp2pxInt(context, 16);
//        setPadding(padding, padding, padding, padding);
//
//        etAccount = findViewById(R.id.et_account);
//        etAccount.addValidator(new LengthValidator("账号长度必须在3-20之间", 3, 20));
//
//        etPassword = findViewById(R.id.et_password);
//        etPassword.addValidator(new LengthValidator("密码长度不能小于6", 6, Integer.MAX_VALUE));
//
//        tvFogotPassword = findViewById(R.id.tv_forgot_password);
//        tvLoginFailed = findViewById(R.id.tv_login_failed);
//        tvSignUp = findViewById(R.id.tv_sign_up);
//        tvSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (etAccount.isValid() && etPassword.isValid()) {
//                    String account = etAccount.getText();
//                    String password = etPassword.getText();
//                    UserManager.getInstance().signIn(account, password);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void clearFocus() {
//        super.clearFocus();
//        if (etAccount != null) {
//            etAccount.clearFocus();
//        }
//        if (etPassword != null) {
//            etPassword.clearFocus();
//        }
//    }
//
//    //    public String getAccountText() {
////        return etAccount.getText().toString();
////    }
////
////    public String getPasswordText() {
////        return etPassword.getText().toString();
////    }
//
//    public void setOnSignUpClickListener(OnClickListener listener) {
//        tvSignUp.setOnClickListener(listener);
//    }
//
//    public void setOnClickListener(OnClickListener listener) {
//        tvFogotPassword.setOnClickListener(listener);
//        tvLoginFailed.setOnClickListener(listener);
//        tvSignUp.setOnClickListener(listener);
//    }
//
////    @Override
////    public void onSignInSuccess() {
////
////    }
////
////    @Override
////    public void onSignInFailed(String errInfo) {
////        ZToast.error("onLoginFailed " + errInfo);
////        etAccount.setError(errInfo);
//////        if ("".equals(errInfo)) {
//////
//////        }
////    }
//
//    @Subscribe
//    public void onSignInEvent(SignInEvent event) {
//        if (!event.isSuccess()) {
//            ZToast.error("onLoginFailed " + event.getErrorMsg());
//            etAccount.setError(event.getErrorMsg());
//        }
//    }
//
//}
