package com.zpj.shouji.market.ui.widget.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.popup.core.BottomPopup;
import com.zpj.shouji.market.R;
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
        submitView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickName = inputView.getText();
                AToast.normal("TODO modify:" + nickName);
            }
        });
    }
}
