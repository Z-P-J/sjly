package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ctetin.expandabletextviewlibrary.ExpandableTextView;
import com.ctetin.expandabletextviewlibrary.app.LinkType;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.toast.ZToast;
import com.zpj.widget.checkbox.SmoothCheckBox;
import com.zpj.widget.editor.AccountInputView;
import com.zpj.widget.editor.EmailInputView;
import com.zpj.widget.editor.PasswordInputView;
import com.zpj.widget.editor.SubmitView;
import com.zpj.widget.editor.validator.EmailValidator;
import com.zpj.widget.editor.validator.LengthValidator;
import com.zpj.widget.editor.validator.SameValueValidator;

public class SignUpLayout3 extends LinearLayout {

    private AccountInputView etAccount;
    private PasswordInputView etPassword;
    private PasswordInputView etPasswordAgain;
    private EmailInputView etEmail;

    private SmoothCheckBox cbAgreement;
    private ExpandableTextView tvAgreement;
    private SubmitView tvSignIn;

    public SignUpLayout3(Context context) {
        this(context, null);
    }

    public SignUpLayout3(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignUpLayout3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AccountInputView getEtAccount() {
        return etAccount;
    }

    public EmailInputView getEtEmail() {
        return etEmail;
    }

    public PasswordInputView getEtPassword() {
        return etPassword;
    }

    public PasswordInputView getEtPasswordAgain() {
        return etPasswordAgain;
    }

    public SubmitView getSubmitView() {
        return tvSignIn;
    }

    private void init(Context context) {
        setOrientation(VERTICAL);

        LayoutInflater.from(context).inflate(R.layout.layout_sign_up3, this, true);


        etAccount = findViewById(R.id.et_account);
        etAccount.addValidator(new LengthValidator("账号长度必须在3-20之间", 3, 20));
        etPassword = findViewById(R.id.et_password);
        etPassword.addValidator(new LengthValidator("密码长度不能小于6", 6, Integer.MAX_VALUE));
        etPasswordAgain = findViewById(R.id.et_password_again);
        etPasswordAgain.addValidator(new LengthValidator("密码长度不能小于6", 6, Integer.MAX_VALUE));
        etPasswordAgain.addValidator(new SameValueValidator(etPassword.getEditText(), "两次输入的密码不相同"));
        etEmail = findViewById(R.id.et_email);
        etEmail.addValidator(new EmailValidator("邮箱格式有误"));
        cbAgreement = findViewById(R.id.cb_agreement);
        cbAgreement.setClickable(false);
        tvAgreement = findViewById(R.id.tv_agreement);


//        String content = "同意[%s](%s)和[%s](%s)";
//        String content = String.format("同意[%s](%s)和[%s](%s)", "《用户协议》", "https://m.shouji.com.cn/sjlyyhxy.html", "《隐私协议》", "https://m.shouji.com.cn/ysxy.html");

        tvAgreement.setContent(getResources().getString(R.string.text_agreement));
        tvAgreement.setLinkClickListener((type, title, url) -> {
            if (type == LinkType.SELF) {
                WebFragment.start(url);
            }
        });
        findViewById(R.id.ll_agreement).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cbAgreement.setChecked(!cbAgreement.isChecked(), true);
            }
        });

//        String text = "同意《用户协议》和《隐私协议》";
//        SpannableString sp = new SpannableString(text);
//
//
//        int index1 = text.indexOf("用户协议");
//        int index2 = text.indexOf("隐私协议");
//        sp.setSpan(new URLSpan("https://m.shouji.com.cn/sjlyyhxy.html") {
//                       @Override
//                       public void onClick(View widget) {
//                           widget.setTag(true);
//                           Log.d("widget", "widget=" + widget);
//                           WebFragment.start(getURL());
//                       }
//                   }, index1, index1 + 4,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        sp.setSpan(new URLSpan("https://m.shouji.com.cn/ysxy.html") {
//                       @Override
//                       public void onClick(View widget) {
//                           widget.setTag(true);
//                           Log.d("widget", "widget=" + widget);
//                           WebFragment.start(getURL());
//                       }
//                   }, index2, index2 + 4,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tvAgreement.setText(sp);
//        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
//        tvAgreement.setTag(false);
//        tvAgreement.setOnClickListener(v -> {
//            if ((boolean)tvAgreement.getTag()) {
//                tvAgreement.setTag(false);
//                return;
//            }
//            cbAgreement.setChecked(!cbAgreement.isChecked(), true);
//            Log.d("widget", "onClick v=" + v);
//        });

        tvSignIn = findViewById(R.id.tv_sign_in);
        tvSignIn.setOnClickListener(v -> {
            if (cbAgreement.isChecked()) {
                if (etAccount.isValid() && etPassword.isValid() && etEmail.isValid()) {
                    String accountName = etAccount.getText();
                    String password = etPassword.getText();
                    String email = etPassword.getText();
                    ZToast.normal("onClick");
                    UserManager.getInstance().signUp(accountName, password, email);
                } else {
                    ZToast.warning("输入内容有误");
                }
            } else {
                ZToast.warning("请同意《手机乐园协议》");
            }
        });

//        EventSender.registerObserver(this, SignUpEvent.class, event -> {
//            if (!event.isSuccess()) {
//                String errInfo = event.getErrorMsg();
//                if ("用户名已被注册".equals(errInfo)) {
//                    etAccount.requestFocus();
//                    etAccount.setError(errInfo);
//                } else {
//                    ZToast.error(errInfo);
//                }
//            }
//        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        EventBus.unSubscribe(this);
    }

    @Override
    public void clearFocus() {
        super.clearFocus();
        if (etAccount != null) {
            etAccount.clearFocus();
        }
        if (etPassword != null) {
            etPassword.clearFocus();
        }
        if (etPasswordAgain != null) {
            etPasswordAgain.clearFocus();
        }
        if (etEmail != null) {
            etEmail.clearFocus();
        }
    }

    public String getAccountText() {
        return etAccount.getText().toString();
    }

    public String getPasswordText() {
        return etPassword.getText().toString();
    }

    public boolean isAgree() {
        return cbAgreement.isChecked();
    }

    public void onSignUp(boolean isSuccess, String errorMsg) {
        if (!isSuccess) {
            if ("用户名已被注册".equals(errorMsg)) {
                etAccount.requestFocus();
                etAccount.setError(errorMsg);
            } else {
                ZToast.error(errorMsg);
            }
        }
    }

//    @Override
//    public void onSignUpSuccess() {
//
//    }
//
//    @Override
//    public void onSignUpFailed(String errInfo) {
//        if ("用户名已被注册".equals(errInfo)) {
//            etAccount.requestFocus();
//            etAccount.setError(errInfo);
//        } else {
//            ZToast.error(errInfo);
//        }
//    }

//    @Subscribe
//    public void onSignUpEvent(SignUpEvent event) {
//        if (!event.isSuccess()) {
//            String errInfo = event.getErrorMsg();
//            if ("用户名已被注册".equals(errInfo)) {
//                etAccount.requestFocus();
//                etAccount.setError(errInfo);
//            } else {
//                ZToast.error(errInfo);
//            }
//        }
//    }

    public interface OnLoginListener {
        void onSignInSuccess();
    }

}
