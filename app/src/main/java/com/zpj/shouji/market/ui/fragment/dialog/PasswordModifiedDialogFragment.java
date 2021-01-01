package com.zpj.shouji.market.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.toast.ZToast;
import com.zpj.widget.editor.PasswordInputView;
import com.zpj.widget.editor.SubmitView;
import com.zpj.widget.editor.validator.DifferentValueValidator;
import com.zpj.widget.editor.validator.LengthValidator;

public class PasswordModifiedDialogFragment extends ModifiedDialogFragment {

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_popup_modified_password;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());

        PasswordInputView oldPasswordView = findViewById(R.id.et_password_old);
        PasswordInputView newPasswordView = findViewById(R.id.et_password_new);

        oldPasswordView.getEditText().setHint("请输入旧密码");
        newPasswordView.getEditText().setHint("请输入新密码");

        LengthValidator lengthValidator = new LengthValidator("密码长度不能小于6", 6, Integer.MAX_VALUE);
        oldPasswordView.addValidator(lengthValidator);
        newPasswordView.addValidator(lengthValidator);

        newPasswordView.addValidator(new DifferentValueValidator(oldPasswordView.getEditText(), "新密码与旧密码相同"));

        SubmitView submitView = findViewById(R.id.sv_submit);
        submitView.setOnClickListener(v -> {
            HttpApi.passwordApi(oldPasswordView.getText(), newPasswordView.getText())
                    .onSuccess(data -> {
                        String result = data.selectFirst("result").text();
                        if ("password_is_wrong".equals(result)) {
                            oldPasswordView.setError("密码错误");
                        } else if ("success".equals(result)) {
                            ZToast.success("修改成功");
                            UserManager.getInstance().signIn(UserManager.getInstance().getMemberInfo().getMemberName(), newPasswordView.getText());
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
//        postDelayed(() -> showSoftInput(oldPasswordView.getEditText()), 50);

        showKeyboard(oldPasswordView.getEditText());

    }
}
