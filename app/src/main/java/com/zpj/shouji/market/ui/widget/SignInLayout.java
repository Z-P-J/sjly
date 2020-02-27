package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.HttpApi;
import com.zpj.shouji.market.utils.UserManager;
import com.zpj.utils.ScreenUtils;

import java.util.Map;

public class SignInLayout extends LinearLayout {

    private EditText etAccount;
    private EditText etPassword;
    private EditText etPasswordAgain;
    private EditText etEmail;

    private CheckBox cbAgreement;
    private TextView tvSignIn;

    public SignInLayout(Context context) {
        this(context, null);
    }

    public SignInLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignInLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);

        LayoutInflater.from(context).inflate(R.layout.layout_sign_in, this, true);

        int padding = ScreenUtils.dp2pxInt(context, 16);
        setPadding(padding, padding, padding, padding);


        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        etPasswordAgain = findViewById(R.id.et_password_again);
        etEmail = findViewById(R.id.et_email);
        cbAgreement = findViewById(R.id.cb_agreement);
        tvSignIn = findViewById(R.id.tv_sign_in);
        tvSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountName = etAccount.getText().toString();
                String password = etPassword.getText().toString();
                String email = etPassword.getText().toString();
                AToast.normal("onClick");
                HttpApi.openConnection("http://tt.shouji.com.cn/app/xml_register_v4.jsp?")
                        .data("m", accountName)
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
                            String cookie = response.header("Set-Cookie");
                            Log.d("SignInLayout", "cookie=" + cookie);
                            if (cookie != null) {
                                String jsessionId = cookie.substring(cookie.indexOf("="), cookie.indexOf(";"));
                                Log.d("SignInLayout", "jsessionId=" + jsessionId);
                            }

                            Document doc = response.parse();
                            if ("failed".equals(doc.selectFirst("result").text())) {
                                AToast.error(doc.selectFirst("info").text());
                            } else {
                                AToast.success("登录成功");

                                UserManager.getInstance().setCookie(cookie);
                                UserManager.getInstance().setUserInfo(doc.toString());
                            }
                        })
                        .onError(new IHttp.OnErrorListener() {
                            @Override
                            public void onError(Throwable throwable) {
                                AToast.error(throwable.getMessage());
                            }
                        })
                        .subscribe();
            }
        });
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

    public interface OnLoginListener {
        void onSignInSuccess();
    }

}
