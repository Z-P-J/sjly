package com.zpj.shouji.market.ui.fragment.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.SignUpEvent;
import com.zpj.shouji.market.event.ToggleLoginModeEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.widget.input.InputView2;
import com.zpj.shouji.market.ui.widget.input.PasswordInputView;
import com.zpj.shouji.market.ui.widget.input.SubmitView;
import com.zpj.shouji.market.ui.widget.input.PasswordInputView2;
import com.zpj.widget.checkbox.SmoothCheckBox;
import com.zpj.widget.editor.validator.LengthValidator;
import com.zpj.widget.editor.validator.SameValueValidator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SignUpFragment extends BaseFragment
        implements View.OnClickListener { // UserManager.OnSignUpListener


    LinearLayout ll_go_login;
    InputView2 piv_account;
    InputView2 aiv_email;
    PasswordInputView2 piv_password;
    PasswordInputView2 piv_password_again;
    SubmitView sv_register;
    private SmoothCheckBox cbAgreement;
    private TextView tvAgreement;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sign_up;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
//        UserManager.getInstance().addOnSignUpListener(this);
        ll_go_login = view.findViewById(R.id.ll_go_login);
        piv_account = view.findViewById(R.id.piv_register_account);
        piv_account.addValidator(new LengthValidator("账号长度必须在3-20之间", 3, 20));
        piv_password = view.findViewById(R.id.piv_register_password);
        piv_password.addValidator(new LengthValidator("密码长度不能小于6", 6, Integer.MAX_VALUE));
        piv_password_again = view.findViewById(R.id.piv_register_password_again);
        piv_password_again.addValidator(new LengthValidator("密码长度不能小于6", 6, Integer.MAX_VALUE));
        piv_password_again.addValidator(new SameValueValidator(piv_password.getEditText(), "两次输入的密码不相同"));
        aiv_email = view.findViewById(R.id.aiv_register_email);
        sv_register = view.findViewById(R.id.sv_register);

        ll_go_login.setOnClickListener(this);
        sv_register.setOnClickListener(this);

//        if (getParentFragment() instanceof LoginFragment) {
//            ((LoginFragment) getParentFragment()).getSoftInputHelper().moveWith(sv_register,
//                    piv_account.getEditText(), piv_password.getEditText(),
//                    piv_password_again.getEditText(), aiv_email.getEditText());
//        }

        cbAgreement = view.findViewById(R.id.cb_agreement);
        tvAgreement = view.findViewById(R.id.tv_agreement);


        String text = "同意《用户协议》和《隐私协议》";
        SpannableString sp = new SpannableString(text);


        int index1 = text.indexOf("用户协议");
        int index2 = text.indexOf("隐私协议");
        sp.setSpan(new URLSpan("https://wap.shouji.com.cn/sjlyyhxy.html") {
                       @Override
                       public void onClick(View widget) {
                           widget.setTag(true);
                           Log.d("widget", "widget=" + widget);
                           WebFragment.start(getURL());
                       }
                   }, index1, index1 + 4,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new URLSpan("https://wap.shouji.com.cn/ysxy.html") {
                       @Override
                       public void onClick(View widget) {
                           widget.setTag(true);
                           Log.d("widget", "widget=" + widget);
                           WebFragment.start(getURL());
                       }
                   }, index2, index2 + 4,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvAgreement.setText(sp);
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        tvAgreement.setTag(false);
        tvAgreement.setOnClickListener(v -> {
            if ((boolean)tvAgreement.getTag()) {
                tvAgreement.setTag(false);
                return;
            }
            cbAgreement.setChecked(!cbAgreement.isChecked(), true);
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
//        UserManager.getInstance().removeOnSignUpListener(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.ll_go_login:
                new ToggleLoginModeEvent().post();
                break;
            case R.id.sv_register:
                String userName = piv_account.getText();
                String password = piv_password.getText();
                String repassword = piv_password_again.getText();
                String email = aiv_email.getText();
                AToast.normal("userName=" + userName + " password=" + password + " repassword=" + repassword + " email=" + email);
                if (cbAgreement.isChecked()) {
                    UserManager.getInstance().signUp(userName, password, email);
                } else {
                    AToast.warning("请同意《手机乐园协议》");
                }
                break;
        }
    }

//    @Override
//    public void onSignUpSuccess() {
//        AToast.success("注册成功！");
//    }
//
//    @Override
//    public void onSignUpFailed(String errInfo) {
//        if ("用户名已被注册".equals(errInfo)) {
//            AToast.error(errInfo);
//        } else {
//            AToast.error(errInfo);
//        }
//    }

    @Subscribe
    public void onSignUpEvent(SignUpEvent event) {
        if (event.isSuccess()) {
            AToast.success("注册成功！");
        } else {
            String errInfo = event.getErrorMsg();
            if ("用户名已被注册".equals(errInfo)) {
                AToast.error(errInfo);
            } else {
                AToast.error(errInfo);
            }
        }
    }

}
