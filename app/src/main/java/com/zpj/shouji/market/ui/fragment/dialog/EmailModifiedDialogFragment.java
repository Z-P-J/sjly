package com.zpj.shouji.market.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.toast.ZToast;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.widget.editor.EmailInputView;
import com.zpj.widget.editor.PasswordInputView;
import com.zpj.widget.editor.SubmitView;
import com.zpj.widget.editor.validator.EmailValidator;
import com.zpj.widget.editor.validator.LengthValidator;

public class EmailModifiedDialogFragment extends ModifiedDialogFragment {

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_popup_modified_email;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());
        EmailInputView emailView = findViewById(R.id.et_email);
        emailView.getEditText().setHint("请输入新邮箱");
        PasswordInputView passwordView = findViewById(R.id.et_password);
        passwordView.addValidator(new LengthValidator("密码长度不能小于6", 6, Integer.MAX_VALUE));
        emailView.addValidator(new EmailValidator("邮箱格式有误"));
        SubmitView submitView = findViewById(R.id.sv_submit);
        submitView.setOnClickListener(v -> {
            String email = emailView.getText();
            HttpApi.emailApi(email, passwordView.getText())
                    .onSuccess(data -> {
                        String result = data.selectFirst("result").text();
                        if ("email_is_used".equals(result)) {
                            emailView.setError("邮箱已被占用");
                        } else if ("password_is_wrong".equals(result)) {
                            passwordView.setError("密码错误");
                        } else if ("success".equals(result)) {
                            ZToast.success("修改成功");
                            UserManager.getInstance().getMemberInfo().setMemberEmail(email);
                            UserManager.getInstance().saveUserInfo();
                            dismiss();
                        } else {
                            ZToast.error("出错了：" + result);
                        }
                    })
                    .onError(throwable -> ZToast.error("出错了：" + throwable.getMessage()))
                    .subscribe();
        });

//        KeyboardUtils.registerSoftInputChangedListener(_mActivity, getContentView(), height -> {
//            getContentView().setTranslationY(-height);
//        });
//
//        postDelayed(() -> showSoftInput(emailView.getEditText()), 50);

        showKeyboard(emailView.getEditText());

    }
}
