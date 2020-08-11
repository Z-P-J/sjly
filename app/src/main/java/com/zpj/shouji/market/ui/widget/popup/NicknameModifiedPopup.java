package com.zpj.shouji.market.ui.widget.popup;

import android.content.Context;
import android.support.annotation.NonNull;

import com.felix.atoast.library.AToast;
import com.zpj.popup.core.BottomPopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.widget.input.AccountInputView2;
import com.zpj.shouji.market.ui.widget.input.SubmitView;
import com.zpj.widget.editor.validator.LengthValidator;

public class NicknameModifiedPopup extends BottomPopup<NicknameModifiedPopup> {

    public static NicknameModifiedPopup with(Context context) {
        return new NicknameModifiedPopup(context);
    }

    public NicknameModifiedPopup(@NonNull Context context) {
        super(context);
        popupInfo.autoOpenSoftInput = true;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_popup_nickname_modified;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        AccountInputView2 inputView = findViewById(R.id.et_account);
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
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }
}
