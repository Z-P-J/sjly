package com.zpj.shouji.market.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.widget.input.AccountInputView;
import com.zpj.shouji.market.ui.widget.input.SubmitView;
import com.zpj.widget.editor.validator.LengthValidator;

public class NicknameModifiedDialogFragment extends ModifiedDialogFragment {

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_popup_modified_nickname;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());
        AccountInputView inputView = findViewById(R.id.et_account);
        inputView.addValidator(new LengthValidator("昵称长度必须在2-15之间", 2, 15));
        SubmitView submitView = findViewById(R.id.sv_submit);
        submitView.setOnClickListener(v -> {
            String nickName = inputView.getText();
            HttpApi.nicknameApi(nickName)
                    .onSuccess(data -> {
                        String result = data.selectFirst("result").text();
                        if ("nickname_is_used".equals(result)) {
                            inputView.setError("昵称已被占用");
                        } else if ("success".equals(result)) {
                            AToast.success("修改成功");
                            UserManager.getInstance().init();
                            dismiss();
                        } else {
                            AToast.error("出错了：" + result);
                        }
                    })
                    .onError(throwable -> AToast.error("出错了：" + throwable.getMessage()))
                    .subscribe();
        });

//        KeyboardUtils.registerSoftInputChangedListener(_mActivity, getContentView(), height -> {
//            getContentView().setTranslationY(-height);
//        });
//
//        postDelayed(() -> showSoftInput(inputView.getEditText()), 50);

        showKeyboard(inputView.getEditText());

    }
}
