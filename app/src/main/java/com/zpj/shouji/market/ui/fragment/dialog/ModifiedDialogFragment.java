package com.zpj.shouji.market.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.fragmentation.dialog.base.BottomDialogFragment;
import com.zpj.utils.KeyboardObserver;

public abstract class ModifiedDialogFragment extends BottomDialogFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KeyboardObserver.registerSoftInputChangedListener(_mActivity, getContentView(), height -> {
            getContentView().setTranslationY(-height);
        });
    }

    @Override
    public void dismiss() {
        hideSoftInput();
        super.dismiss();
    }

    protected void showKeyboard(View view) {
        postDelayed(() -> showSoftInput(view), 150);
    }

}
