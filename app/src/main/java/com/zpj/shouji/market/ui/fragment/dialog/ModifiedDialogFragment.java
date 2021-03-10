package com.zpj.shouji.market.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.fragmentation.dialog.base.BottomDragDialogFragment;
import com.zpj.utils.KeyboardObserver;

public abstract class ModifiedDialogFragment extends BottomDragDialogFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KeyboardObserver.registerSoftInputChangedListener(_mActivity, contentView, height -> {
            contentView.setTranslationY(-height);
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
