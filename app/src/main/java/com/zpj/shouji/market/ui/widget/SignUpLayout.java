package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.shouji.market.R;
import com.zpj.utils.ScreenUtils;

public class SignUpLayout extends LinearLayout {

    private EditText etAccount;
    private EditText etPassword;

    private TextView tvFogotPassword;
    private TextView tvLoginFailed;
    private TextView tvSignUp;

    public SignUpLayout(Context context) {
        this(context, null);
    }

    public SignUpLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignUpLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.layout_sign_up, this, true);

        int padding = ScreenUtils.dp2pxInt(context, 16);
        setPadding(padding, padding, padding, padding);

        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        tvFogotPassword = findViewById(R.id.tv_forgot_password);
        tvLoginFailed = findViewById(R.id.tv_login_failed);
        tvSignUp = findViewById(R.id.tv_sign_up);
    }

    public String getAccountText() {
        return etAccount.getText().toString();
    }

    public String getPasswordText() {
        return etPassword.getText().toString();
    }

    public void setOnSignUpClickListener(OnClickListener listener) {
        tvSignUp.setOnClickListener(listener);
    }

    public void setOnClickListener(OnClickListener listener) {
        tvFogotPassword.setOnClickListener(listener);
        tvLoginFailed.setOnClickListener(listener);
        tvSignUp.setOnClickListener(listener);
    }

}
