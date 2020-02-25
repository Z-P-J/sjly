package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.shouji.market.R;
import com.zpj.utils.ScreenUtils;

public class SignInLayout extends LinearLayout {

    private EditText etAccount;
    private EditText etPassword;

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
        cbAgreement = findViewById(R.id.cb_agreement);
        tvSignIn = findViewById(R.id.tv_sign_in);
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

    public void setOnSignInClickListener(OnClickListener listener) {
        tvSignIn.setOnClickListener(listener);
    }

    public void setOnClickListener(OnClickListener listener) {
        tvSignIn.setOnClickListener(listener);
    }

}
